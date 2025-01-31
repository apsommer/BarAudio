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
    Loading,
    Unsubscribed,
    NewSubscription,
    Subscribed,
    Error
}

class BillingClientImpl(
    val context: Context)
: BillingClientStateListener, PurchasesUpdatedListener {

    // flow billing state
    val billingState = MutableStateFlow(BillingState.Unsubscribed)

    // create billing client
    private val client =
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

        handleError(43)
        billingState.value = BillingState.Unsubscribed
        connect()
    }

    override fun onBillingSetupFinished(
        setupResult: BillingResult) {

        if (setupResult.responseCode != BillingResponseCode.OK) {
            return handleError(setupResult.responseCode)
        }

        // query previous purchases
        client
            .queryPurchasesAsync(
                QueryPurchasesParams
                    .newBuilder()
                    .setProductType(ProductType.SUBS)
                    .build(),
                PurchasesResponseListener { result, purchases ->

                    if (result.responseCode != BillingResponseCode.OK) {
                        return@PurchasesResponseListener handleError(result.responseCode)
                    }

                    // user has no purchases, remains unsubscribed
                    if (purchases.isEmpty()) {
                        return@PurchasesResponseListener
                    }

                    processPurchase(purchases.first())
                })
    }

    private fun processPurchase(
        purchase: Purchase) {

        // subscription active: previously processed purchase
        if (purchase.isAcknowledged) {

            billingState.value = BillingState.Subscribed
            return
        }

        // acknowledge new purchase
        CoroutineScope(Dispatchers.IO).launch {

            val result = client
                .acknowledgePurchase(
                    AcknowledgePurchaseParams
                        .newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build())

            if (result.responseCode != BillingResponseCode.OK) {
                return@launch handleError(result.responseCode)
            }

            billingState.value = BillingState.NewSubscription
        }
    }

    fun launchBillingFlowUi(
        context: Context) {

        // debounce, only need to launch once
        if (billingState.value == BillingState.Loading) { return }

        // show progress spinner
        billingState.value = BillingState.Loading

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

            if (result.billingResult.responseCode != BillingResponseCode.OK) {
                return@launch handleError(result.billingResult.responseCode)
            }

            // extract product params from response
            val productDetailsList = result.productDetailsList ?: return@launch
            val product = productDetailsList.first()
            val offers = product.subscriptionOfferDetails
            if (offers.isNullOrEmpty()) {
                return@launch handleError(42)
            }

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
        if (result.responseCode != BillingResponseCode.OK || purchases.isNullOrEmpty()) {
            return handleError(result.responseCode)
        }

        processPurchase(purchases.first())
    }

    private fun handleError(responseCode: Int) {

        billingState.value = BillingState.Error

        val errorMap = hashMapOf(
            -3 to "Service timeout",
            -2 to "Feature not supported",
            -1 to "Service disconnected",
            0 to "Ok",
            1 to "User canceled",
            2 to "Service unavailable",
            3 to "Billing unavailable",
            4 to "Item unavailable",
            5 to "Developer error",
            6 to "Error",
            7 to "Item already owned",
            8 to "Item not owned",
            12 to "Network error",

            // customize
            42 to "No offers associated with retrieved produce",
            43 to "Client disconnected")

        logMessage("Billing error, code $responseCode: ${errorMap[responseCode]}")
    }
}