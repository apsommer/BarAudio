package com.sommerengineering.baraudio

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

enum class BillingState {
    Unsubscribed,
    NewSubscription,
    Subscribed
}

class BillingClientImpl(val context: Context)
    : BillingClientStateListener, PurchasesUpdatedListener {

    // todo remove Google Developer API? seems for backend only
    //  https://developer.android.com/google/play/billing/getting-ready#dev-api

    val isUserPaid = MutableStateFlow(BillingState.Unsubscribed)

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

    override fun onBillingServiceDisconnected() {

        isUserPaid.value = BillingState.Unsubscribed
        connect()
    }

    override fun onBillingSetupFinished(
        setupResult: BillingResult) {

        if (setupResult.responseCode != BillingResponseCode.OK) { return }

        // query previous purchases
        client
            .queryPurchasesAsync(
                QueryPurchasesParams
                    .newBuilder()
                    .setProductType(ProductType.SUBS)
                    .build(),
                PurchasesResponseListener { result, purchases ->
                    if (result.responseCode != BillingResponseCode.OK || purchases.isEmpty()) {
                        return@PurchasesResponseListener
                    }
                    processPurchase(purchases.first())
                })
    }

    private fun processPurchase(
        purchase: Purchase) {

        // subscription active: previously processed purchase
        if (purchase.isAcknowledged) {

            isUserPaid.value = BillingState.Subscribed
            return
        }

        // acknowledge new purchase
        CoroutineScope(Dispatchers.IO).launch {

            val acknowledgePurchaseResult = client
                .acknowledgePurchase(
                    AcknowledgePurchaseParams
                        .newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build())

            if (acknowledgePurchaseResult.responseCode != BillingResponseCode.OK) { return@launch }

            isUserPaid.value = BillingState.NewSubscription
        }
    }

    fun launchBillingFlowUi(
        context: Context) {

        CoroutineScope(Dispatchers.IO).launch {

            // query products configured on play store
            val result =
                client.queryProductDetails(
                    QueryProductDetailsParams
                        .newBuilder()
                        .setProductList(listOf(
                            Product
                                .newBuilder()
                                .setProductId(productId)
                                .setProductType(ProductType.SUBS)
                                .build()))
                        .build())

            if (result.billingResult.responseCode != BillingResponseCode.OK) { return@launch }

            // extract product params from response
            val productDetailsList = result.productDetailsList ?: return@launch
            val product = productDetailsList.first()
            val offers = product.subscriptionOfferDetails
            if (offers.isNullOrEmpty()) { return@launch }

            // offer free trial, if eligible
            val offer = offers
                .find { it.offerId == freeTrial } ?: // free trial
                    offers.first() // standard subscription

            // launch billing flow ui
            client.launchBillingFlow(
                context as MainActivity,
                BillingFlowParams
                    .newBuilder()
                    .setProductDetailsParamsList(listOf(
                        BillingFlowParams
                            .ProductDetailsParams
                            .newBuilder()
                            .setProductDetails(product)
                            .setOfferToken(offer.offerToken)
                            .build()))
                    .build())

            // billing flow ui result delivered to onPurchaseUpdated callback
        }
    }

    override fun onPurchasesUpdated(
        result: BillingResult,
        purchases: MutableList<Purchase>?) {

        // catch ui errors: user canceled flow, card declined, ...
        if (result.responseCode != BillingResponseCode.OK || purchases.isNullOrEmpty()) { return }

        processPurchase(purchases.first())
    }
}