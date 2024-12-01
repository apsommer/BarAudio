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
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
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

    override fun onBillingServiceDisconnected() {
        logMessage("onBillingServiceDisconnected")
    }

    override fun onBillingSetupFinished(result: BillingResult) {

        // unexpected error
        if (result.responseCode != BillingResponseCode.OK) {
            logMessage("Billing client failed to initialize")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {

            // todo, check if user has already purchased subscription
//            checkPreviousUserPurchases(context, billingClient)

            // todo, if user has not purchased subscription, launch billing flow ui
            launchBillingFlowUi(context)
        }
    }

    fun handlePurchase(
        purchase: Purchase) {

        // check if purchase already acknowledged
        if (purchase.isAcknowledged) {
            logMessage("Purchase already acknowledged")
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
        }
    }


    suspend fun launchBillingFlowUi(
        context: Context) {

        // define subscription product
        // configured in Play Console
        val productList =
            listOf(
                Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(ProductType.SUBS)
                    .build())

        val params =
            QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

        // query play store for subscription product
        val productDetailsResult =
            billingClient.queryProductDetails(params)

        if (productDetailsResult.billingResult.responseCode != BillingResponseCode.OK) {
            logMessage(productDetailsResult.billingResult.debugMessage)
            return
        }

        // extract products from result
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
    }



}