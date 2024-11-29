package com.sommerengineering.baraudio

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.android.billingclient.api.queryProductDetails
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sommerengineering.baraudio.login.LoginScreen
import com.sommerengineering.baraudio.messages.MessagesScreen
import com.sommerengineering.baraudio.settings.SettingsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import kotlin.math.log

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
    // val isSystemInDarkTheme = isSystemInDarkTheme()

    // todo temp
    launchSubscriptionRequest(context)

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

    controller.navigate(LoginScreenRoute) {
        popUpTo(MessagesScreenRoute) { inclusive = true }
    }
}

// skip login screen if user already authenticated
fun getStartDestination() =

    if (Firebase.auth.currentUser != null) {
        logMessage("Sign-in skipped, user: ${Firebase.auth.currentUser?.uid}")
        MessagesScreenRoute }
    else LoginScreenRoute

fun launchSubscriptionRequest(
    context: Context) {

    // listen to play store query
    val purchasesUpdatedListener =
        PurchasesUpdatedListener { result, purchases ->

            val responseCode = result.responseCode
            val purchase = purchases?.first()

            if (responseCode == BillingResponseCode.USER_CANCELED) {
                logMessage("User canceled billing flow: ${result.debugMessage}")
                return@PurchasesUpdatedListener
            }

            if (responseCode != BillingResponseCode.OK || purchase == null) {
                logMessage("Error in billing flow: ${result.debugMessage}")
                return@PurchasesUpdatedListener
            }

            // process purchase
            logMessage("Got purchase token: ${purchase.purchaseToken}")


        }

    val billingClient =
        BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)

            // todo not in docs?
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
            )

            .build()

    // initialize connection to play store
    billingClient
        .startConnection(object : BillingClientStateListener {

        override fun onBillingSetupFinished(result: BillingResult) {

            if (result.responseCode == BillingResponseCode.OK) {

                logMessage("onBillingSetupFinished -> BillingResponseCode.OK")

                CoroutineScope(Dispatchers.Default).launch {

                    // process response
                    processPurchases(
                        context,
                        billingClient)
                }
            }
        }

        override fun onBillingServiceDisconnected() {
            logMessage("onBillingServiceDisconnected")
            // Try to restart the connection on the next request to
            // Google Play by calling the startConnection() method.
        }
    })

}

suspend fun processPurchases(
    context: Context,
    billingClient: BillingClient
) {

    // define subscription product
    val productList =
        listOf(
            Product.newBuilder()
                .setProductId("premium")
                .setProductType(ProductType.SUBS)
                .build())

    val params =
        QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

    // query play store for product
    val productDetailsResult = withContext(Dispatchers.IO) {
        billingClient.queryProductDetails(params)
    }

    if (productDetailsResult.billingResult.responseCode != BillingResponseCode.OK) {
        logMessage(productDetailsResult.billingResult.debugMessage)
        return
    }

    // extract product from result
    val productDetails =
        productDetailsResult.productDetailsList?.first()

    if (productDetails == null) {
        logMessage("productDetailsList is null!")
        return
    }

    logMessage(productDetails.title)
    logMessage(productDetails.productId)
    logMessage(productDetails.productType)
    logMessage(productDetails.name)
    logMessage(productDetails.description)

    val offerToken =
        productDetails.subscriptionOfferDetails?.first()?.offerToken

    if (offerToken == null) {
        logMessage("Bad offerToken!")
        return
    }

    //
    val productDetailsParamList =
        listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
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
}
