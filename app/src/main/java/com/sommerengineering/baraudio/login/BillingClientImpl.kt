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
    private val context: Context)
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
        if (result.responseCode != BillingResponseCode.OK) {

            logMessage("Billing client failed to initialize")

            // todo
            // Try to restart the connection on the next request to
            // Google Play by calling the startConnection() method.

            return
        }

        logMessage("Billing client initialized")
        checkPreviousPurchases()
    }

    override fun onBillingServiceDisconnected() {

        // todo
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
    }

    fun checkPreviousPurchases() {

        // define purchase params
        val queryPurchasesParams =
            QueryPurchasesParams.newBuilder()
                .setProductType(ProductType.SUBS)
                .build()

        // query previous purchases
        client
            .queryPurchasesAsync(
                queryPurchasesParams,
                PurchasesResponseListener { result, purchases ->

            if (result.responseCode != BillingResponseCode.OK) {
                logMessage("Error retrieving previous purchases: ${result.debugMessage}")
                // todo this should never happen, do something?
                return@PurchasesResponseListener
            }

            if (purchases.isEmpty()) {
                logMessage("No previous purchases, need to launch billing flow ...")
                return@PurchasesResponseListener
            }

            // todo can be more than one purchase here? .find() latest?
            val purchase = purchases.first()
            handlePurchase(purchase)
        })
    }

    fun handlePurchase(
        purchase: Purchase) {

        // check for active subscription
        if (purchase.isAcknowledged) {

            logMessage("Purchase already acknowledged")
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

            if (acknowledgePurchaseResult.responseCode != BillingResponseCode.OK) {
                logMessage("Error, purchase not acknowledged")
                isSubscriptionPurchased.value = false
                return@launch
            }

            logMessage("Success, purchase acknowledged")
            isSubscriptionPurchased.value = true
        }
    }

    fun launchBillingFlowUi(
        context: Context) {

        CoroutineScope(Dispatchers.IO).launch {

            // query play store for product
            val productDetailsResult =
                client.queryProductDetails(
                    QueryProductDetailsParams.newBuilder()
                        .setProductList(
                            listOf(
                                Product.newBuilder()
                                    .setProductId(productId)
                                    .setProductType(ProductType.SUBS)
                                    .build()))
                        .build())

            if (productDetailsResult.billingResult.responseCode != BillingResponseCode.OK) {

                logMessage("Billing flow ui error: ${productDetailsResult.billingResult.debugMessage}")
                // todo this should never happen, do something?
                return@launch
            }

            // extract products from response
            val productDetailsList = productDetailsResult.productDetailsList
            if (productDetailsList == null) {

                logMessage("productDetailsList is null")
                // todo this should never happen, do something?
                return@launch
            }

            // build list of product details params
            val subscription = productDetailsList.first()
            val subscriptionOffers = subscription.subscriptionOfferDetails

            if (subscriptionOffers == null || subscriptionOffers.isEmpty()) {

                logMessage("Retrieved malformed subscription")
                // todo this should never happen, do something?
                return@launch
            }

            val offer = subscriptionOffers
                .find { it.offerId == freeTrial } // free trial, only present if user eligible
                ?: subscriptionOffers.first() // base plan

            val billingFlowParams =
                BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                        listOf(
                            BillingFlowParams
                                .ProductDetailsParams.newBuilder()
                                .setProductDetails(subscription)
                                .setOfferToken(offer.offerToken)
                                .build()))
                    .build()

            // launch the billing flow
            val billingFlowResult = client
                .launchBillingFlow(
                    context as MainActivity,
                    billingFlowParams)

            if (billingFlowResult.responseCode != BillingResponseCode.OK) {
                logMessage("Billing flow error: ${billingFlowResult.debugMessage}")
            }
        }

        // all good, result delivered to callback onPurchaseUpdated
    }

    // listen for new purchases
    override fun onPurchasesUpdated(
        result: BillingResult,
        purchases: MutableList<Purchase>?) {

        // catch error: user canceled flow, card declined, ...
        if (result.responseCode != BillingResponseCode.OK || purchases.isNullOrEmpty()) {

            logMessage("Billing flow ui error: ${result.responseCode}")
            return
        }

        // process new purchase
        val purchase = purchases.first()
        handlePurchase(purchase)
    }
}