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
import kotlinx.coroutines.launch

class BillingClientImpl(
    private val context: Context)
    : BillingClientStateListener,
    PurchasesUpdatedListener {

    // todo remove Google Developer API? seems for backend only
    //  https://developer.android.com/google/play/billing/getting-ready#dev-api

    var isSubscriptionPurchased = false

    // create billing client
    val billingClient =
        BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts().build())
            .build()

    // connect to play store
    fun connect() =
        billingClient.startConnection(this)

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

        // define purchase
        val queryPurchasesParams =
            QueryPurchasesParams.newBuilder()
                .setProductType(ProductType.SUBS)
                .build()

        // listen to response
        val purchaseResponseListener =
            PurchasesResponseListener { billingResult, purchases ->

                if (billingResult.responseCode != BillingResponseCode.OK) {

                    logMessage("Error retrieving previous purchases: ${billingResult.debugMessage}")
                    logMessage("Checking previous purchases again ...")
                    checkPreviousPurchases()
                    // todo this should never happen, do something?
                    return@PurchasesResponseListener
                }

                if (purchases.isEmpty()) {
                    logMessage("No previous purchases, launching billing flow ...")
                    launchBillingFlowUi(context)
                    return@PurchasesResponseListener
                }

                // todo can be more than one purchase here? .find() latest?
                val purchase = purchases.first()
                handlePurchase(purchase)
            }

        // query previous purchases
        billingClient
            .queryPurchasesAsync(
                queryPurchasesParams,
                purchaseResponseListener)
    }

    fun handlePurchase(
        purchase: Purchase) {

        // confirm state is purchased, not pending
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) {
            logMessage("Purchase incomplete, user must finished transaction")
            return
        }

        // check if purchase already acknowledged
        if (purchase.isAcknowledged) {
            logMessage("Purchase already acknowledged")
            logMessage("purchaseToken: ${purchase.purchaseToken}")
            isSubscriptionPurchased = true
            return
        }

        val acknowledgePurchaseParams =
            AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

        // acknowledge purchase
        CoroutineScope(Dispatchers.IO).launch {

            val acknowledgePurchaseResult =
                billingClient
                    .acknowledgePurchase(acknowledgePurchaseParams)

            if (acknowledgePurchaseResult.responseCode != BillingResponseCode.OK) {
                logMessage("Error, purchase not acknowledged")
                // todo this should never happen, do something?
                return@launch
            }

            logMessage("Success, purchase acknowledged")
            isSubscriptionPurchased = true
        }
    }

    fun launchBillingFlowUi(
        context: Context) {

        // check if subscription already purchased
        // if (isSubscriptionPurchased) { return }

        // define subscription product
        val productList = listOf(
            Product.newBuilder()
                .setProductId(productId)
                .setProductType(ProductType.SUBS)
                .build())

        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

        // query play store for subscription product
        CoroutineScope(Dispatchers.IO).launch {

            val productDetailsResult =
                billingClient.queryProductDetails(queryProductDetailsParams)

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

            val productDetailsParamList = listOf(
                BillingFlowParams
                    .ProductDetailsParams.newBuilder()
                        .setProductDetails(subscription)
                        .setOfferToken(offer.offerToken)
                        .build())

            val billingFlowParams =
                BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamList)
                    .build()

            // launch the billing flow
            val billingResult =
                billingClient
                    .launchBillingFlow(
                        context as MainActivity,
                        billingFlowParams)

            if (billingResult.responseCode != BillingResponseCode.OK) {
                logMessage("Billing flow error: ${billingResult.debugMessage}")
                logMessage("Relaunching billing flow ui ...")
                launchBillingFlowUi(context)
            }
        }

        // all good, result delivered to callback onPurchaseUpdated
    }

    // listen for new purchases
    override fun onPurchasesUpdated(
        result: BillingResult,
        purchases: MutableList<Purchase>?) {

        if (result.responseCode == BillingResponseCode.USER_CANCELED) {
            logMessage("Billing flow ui: user canceled")
            return
        }

        if (result.responseCode == BillingResponseCode.BILLING_UNAVAILABLE) {
            logMessage("Billing flow ui: user card declined")
            return
        }

        // user declined, or other error
        if (result.responseCode != BillingResponseCode.OK || purchases == null) {
            logMessage("Billing flow ui error: ${result.responseCode}")
            return
        }

        // process new purchase
        val purchase = purchases.first()
        handlePurchase(purchase)
    }
}