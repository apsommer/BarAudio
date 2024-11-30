package com.sommerengineering.baraudio

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
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
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sommerengineering.baraudio.login.LoginScreen
import com.sommerengineering.baraudio.messages.MessagesScreen
import com.sommerengineering.baraudio.messages.dbListener
import com.sommerengineering.baraudio.settings.SettingsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

// routes
const val LoginScreenRoute = "LoginScreen"
const val MessagesScreenRoute = "AlertScreen"
const val SettingsScreenRoute = "SettingsScreen"

@Composable
fun Navigation(
    controller: NavHostController) {

    // inject viewmodel
    val context = LocalContext.current
    val viewModel: MainViewModel = koinViewModel(viewModelStoreOwner = context as MainActivity)

    NavHost(
        navController = controller,
        startDestination = getStartDestination()) {
        composable(
            route = LoginScreenRoute) {
            LoginScreen(
                onAuthentication = {
                    onAuthentication(
                        controller = controller,
                        viewModel = viewModel,
                        context = context) })
        }
        composable(
            route = MessagesScreenRoute) {
            MessagesScreen(
                onSettingsClick = {
                    controller.navigate(SettingsScreenRoute)
                })
        }
        composable(
            route = SettingsScreenRoute) {
            SettingsScreen(
                onBackClicked = { controller.navigateUp() },
                onSignOut = {
                    onSignOut(
                        controller = controller,
                        viewModel = viewModel,
                        context = context) })
        }
    }
}

fun onAuthentication(
    controller: NavHostController,
    viewModel: MainViewModel,
    context: Context) {

    viewModel.setUiMode(context)

    (context as MainActivity).requestRealtimeNotificationPermission()

    validateToken()

    controller.navigate(MessagesScreenRoute) {
        popUpTo(LoginScreenRoute) { inclusive = true }
    }
}

fun onSignOut(
    controller: NavHostController,
    viewModel: MainViewModel,
    context: Context) {

    signOut()

    viewModel.setUiMode(context)

    // clear local cache by detaching database listener
    getDatabaseReference(messagesNode)
        .removeEventListener(dbListener)

    controller.navigate(LoginScreenRoute) {
        popUpTo(MessagesScreenRoute) { inclusive = true }
    }
}

// skip login screen if user already authenticated
fun getStartDestination() =

    if (Firebase.auth.currentUser != null) {
        logMessage("Authentication skipped, user signed-in: ${Firebase.auth.currentUser?.uid}")
        MessagesScreenRoute }
    else LoginScreenRoute

lateinit var billingClient: BillingClient

fun connectBillingClient(
    context: Context) {

    // listen for result of billing ui
    val purchasesUpdatedListener =
        PurchasesUpdatedListener { result, purchases ->

            val purchase = purchases?.first()

            // user canceled
            if (result.responseCode == BillingResponseCode.USER_CANCELED) {
                logMessage("User canceled billing flow: ${result.debugMessage}")
                return@PurchasesUpdatedListener
            }

            // unexpected error
            if (result.responseCode != BillingResponseCode.OK || purchase == null) {
                logMessage("Error in billing flow: ${result.debugMessage}")
                return@PurchasesUpdatedListener
            }

            // process new purchase
            handlePurchase(billingClient, purchase)
        }

    // create billing client
    billingClient =
        BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts().build())
            .build()

    // connect to play store
    billingClient
        .startConnection(object : BillingClientStateListener {

        override fun onBillingSetupFinished(result: BillingResult) {

            // unexpected error
            if (result.responseCode != BillingResponseCode.OK) {
                logMessage("Billing client failed to initialize")
                return
            }

            CoroutineScope(Dispatchers.IO).launch {

                // todo, check if user has already purchased subscription
                checkPreviousUserPurchases(context, billingClient)

                // todo, if user has not purchased subscription, launch billing flow ui
                launchBillingFlowUi(context, billingClient)
            }
        }

        override fun onBillingServiceDisconnected() {
            logMessage("onBillingServiceDisconnected")
        }
    })

}

suspend fun checkPreviousUserPurchases(
    context: Context,
    billingClient: BillingClient) {

        // todo get user's previous purchases
        val userPurchasesResult =
            billingClient.queryPurchasesAsync(
                QueryPurchasesParams())

        val purchase =
            userPurchasesResult.purchasesList.first()

        handlePurchase(billingClient, purchase)
    }

fun handlePurchase(
    billingClient: BillingClient,
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
    context: Context,
    billingClient: BillingClient) {

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
