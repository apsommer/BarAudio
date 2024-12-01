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
import com.sommerengineering.baraudio.logMessage
import com.sommerengineering.baraudio.productId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class BillingClientImpl(
    private val context: Context
) : BillingClientStateListener, PurchasesUpdatedListener, KoinComponent {

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

    // listen for new purchases
    override fun onPurchasesUpdated(
        result: BillingResult,
        purchases: MutableList<Purchase>?) {

        // user declined, or other error
        if (result.responseCode != BillingResponseCode.OK || purchases == null) {
            logMessage("Billing ui result: ${result.debugMessage}")
            return
        }

        // process new purchase
        val purchase = purchases.first()
        handlePurchase(purchase)
    }

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
        queryPurchases()
    }

    override fun onBillingServiceDisconnected() {

        // todo
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
    }

    fun queryPurchases() {

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
                }

                val purchase = purchases.first()

                logMessage("Got previous purchase: ${purchase.signature}")
                logMessage("Got previous purchase: ${purchase.orderId}")
                logMessage("Got previous purchase: ${purchase.products}")
                logMessage("Got previous purchase: ${purchase.isAutoRenewing}")

                handlePurchase(purchase)
            }

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
                return@launch
            }

            logMessage("Success, purchase acknowledged")
            isSubscriptionPurchased = true
        }
    }

    suspend fun launchBillingFlowUi(
        context: Context) {

        // define subscription product
        val productList =
            listOf(
                Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(ProductType.SUBS)
                    .build())

        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

        // query play store for subscription product
        val productDetailsResult =
            billingClient.queryProductDetails(queryProductDetailsParams)

        if (productDetailsResult.billingResult.responseCode != BillingResponseCode.OK) {
            logMessage(productDetailsResult.billingResult.debugMessage)
            return
        }

        // extract products from response
        val productDetailsList = productDetailsResult.productDetailsList
        if (productDetailsList == null) {
            logMessage("productDetailsList is null")
            return
        }

        // build list of product details params
        val subscription = productDetailsList.first()
        val basePlanToken = subscription.subscriptionOfferDetails?.first()?.offerToken ?: return
        val freeTrialToken = subscription.subscriptionOfferDetails?.last()?.offerToken ?: return

        val productDetailsParamList =
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(subscription)
                    .setOfferToken(basePlanToken)
                    .setOfferToken(freeTrialToken) // todo not sure this pattern is right? how to show both offers on same product_id?
                    .build(),
            )

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
        }

        // all good, result delivered to callback onPurchaseUpdated
    }
}