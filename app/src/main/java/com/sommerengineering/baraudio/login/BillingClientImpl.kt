package com.sommerengineering.baraudio.login

import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.freeTrial
import com.sommerengineering.baraudio.logMessage
import com.sommerengineering.baraudio.productId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BillingClientImpl(
    val context: Context)
    : BillingClientStateListener,
    PurchasesUpdatedListener {

    // todo remove Google Developer API? seems for backend only
    //  https://developer.android.com/google/play/billing/getting-ready#dev-api

    val isSubscriptionPurchased = MutableStateFlow(false)

    // create billing client
    val client =
        BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts().build())
            .build()

    // connect to play store
    fun connect() =
        client.startConnection(this)

    override fun onBillingSetupFinished(result: BillingResult) {

        // unexpected error
        if (result.responseCode != BillingResponseCode.OK) { return }
        getPurchases()
    }

    override fun onBillingServiceDisconnected() { }

    private fun getPurchases() {

        // define purchase
        val queryPurchasesParams =
            QueryPurchasesParams.newBuilder()
                .setProductType(ProductType.SUBS)
                .build()

        // get previous purchases
        client
            .queryPurchasesAsync(
                queryPurchasesParams,
                PurchasesResponseListener { result, purchases ->

            if (result.responseCode != BillingResponseCode.OK) { return@PurchasesResponseListener }
            if (purchases.isEmpty()) { return@PurchasesResponseListener }

            handlePurchase(purchases.first())
        })
    }

    fun handlePurchase(
        purchase: Purchase) {

        // subscription active
        if (purchase.isAcknowledged) {
            isSubscriptionPurchased.value = true
            return
        }

        // acknowledge new purchase
        CoroutineScope(Dispatchers.IO).launch {

            val acknowledgePurchaseResult = client
                .acknowledgePurchase(
                    AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build())

            if (acknowledgePurchaseResult.responseCode != BillingResponseCode.OK) { return@launch }

            isSubscriptionPurchased.value = true
        }
    }

    fun launchBillingFlowUi(
        context: Context) {

        CoroutineScope(Dispatchers.IO).launch {

            // retrieve product from play store
            val result =
                client.queryProductDetails(
                    QueryProductDetailsParams.newBuilder()
                        .setProductList(listOf(
                            Product.newBuilder()
                                .setProductId(productId)
                                .setProductType(ProductType.SUBS)
                                .build())).build())

            if (result.billingResult.responseCode != BillingResponseCode.OK) { return@launch }

            // extract products from response
            val productDetailsList = result.productDetailsList ?: return@launch

            // build list of product details params
            val product = productDetailsList.first()

            val offers = product.subscriptionOfferDetails
            if (offers.isNullOrEmpty()) { return@launch }

            // offer free trial, if eligible
            val offer = offers
                .find { it.offerId == freeTrial } ?:
                offers.first()

            // launch the billing flow
            client.launchBillingFlow(
                context as MainActivity,
                BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(listOf(
                        BillingFlowParams
                            .ProductDetailsParams.newBuilder()
                            .setProductDetails(product)
                            .setOfferToken(offer.offerToken)
                            .build())).build())

            // billing flow result delivered to onPurchaseUpdated
        }
    }

    override fun onPurchasesUpdated(
        result: BillingResult,
        purchases: MutableList<Purchase>?) {

        // catch error: user canceled flow, card declined, ...
        if (result.responseCode != BillingResponseCode.OK || purchases.isNullOrEmpty()) { return }

        handlePurchase(purchases.first())
    }
}