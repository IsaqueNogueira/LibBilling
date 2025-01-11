package com.isaquesoft.libbilling.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.queryProductDetails
import com.google.common.collect.ImmutableList
import com.isaquesoft.libbilling.presentation.model.ProductsSubscription
import com.isaquesoft.libbilling.presentation.screens.SubscriptionScreen
import com.isaquesoft.libbilling.presentation.ui.theme.LibBillingTheme
import com.isaquesoft.libbilling.presentation.viewmodel.SubscriptionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Isaque Nogueira on 18/10/2024
 */
class SubscriptionActivity : ComponentActivity() {
    private lateinit var billingClient: BillingClient

    private val viewModel: SubscriptionViewModel by viewModel()

    companion object {
        private var isCallSignaturePurchasedActivity = false
    }

    private val productIds by lazy { intent.getStringArrayListExtra("productIds") ?: emptyList() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LibBillingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                ) {
                    SubscriptionScreen(
                        viewModel = viewModel,
                        itemClick = { productDetails ->
                            launchPurchaseFlow(productDetails)
                        },
                        onFinish = {
                            finish()
                        },
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        connectToGooglePlayBilling()
    }

    private fun connectToGooglePlayBilling() {
        billingClient =
            BillingClient
                .newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build()

        billingClient.startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        lifecycleScope.launch {
                            showProducts()
                        }
                    } else {
                        billingClient.endConnection()
                        finish()
                    }
                }

                override fun onBillingServiceDisconnected() {
                    connectToGooglePlayBilling()
                }
            },
        )
    }

    private suspend fun showProducts() {
        val productList: List<QueryProductDetailsParams.Product> =
            productIds.map {
                QueryProductDetailsParams.Product
                    .newBuilder()
                    .setProductId(it)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            }

        val params =
            QueryProductDetailsParams
                .newBuilder()
                .setProductList(ImmutableList.copyOf(productList))

        val productDetailsResult =
            withContext(Dispatchers.IO) {
                billingClient.queryProductDetails(params.build())
            }

        if (productDetailsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK &&
            productDetailsResult.productDetailsList != null
        ) {
            val productDetailsList = productDetailsResult.productDetailsList!!
            if (productDetailsList.isNotEmpty()) {
                val productsSubscription = mutableListOf<ProductsSubscription>()
                for (productDetails in productDetailsList) {
                    if (productDetails.subscriptionOfferDetails != null) {
                        for (i in 0 until productDetails.subscriptionOfferDetails!!.size) {
                            productsSubscription.add(
                                ProductsSubscription(
                                    productDetails.name,
                                    productDetails.subscriptionOfferDetails
                                        ?.get(i)
                                        ?.pricingPhases
                                        ?.pricingPhaseList
                                        ?.get(
                                            i,
                                        )?.formattedPrice,
                                    productDetails,
                                ),
                            )
                        }
                    }
                }

                viewModel.showProducts(productsSubscription)
            } else {
                //  showLoading(false)
                finish()
            }
        }
    }

    private fun launchPurchaseFlow(productDetails: ProductDetails) {
        val productDetailsParamsList =
            ImmutableList.of(
                BillingFlowParams.ProductDetailsParams
                    .newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(productDetails.subscriptionOfferDetails!![0].offerToken)
                    .build(),
            )

        val billingFlowParams =
            BillingFlowParams
                .newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

        billingClient.launchBillingFlow(this, billingFlowParams)
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { _, purchases ->
            purchases?.forEach {
                handlePurchase(it)
            }
        }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams =
                    AcknowledgePurchaseParams
                        .newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()

                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // A compra foi confirmada com sucesso
                        viewModel.setUserPremium(true)
                        goToSubscriptionPurchaseScreen(false)
                    } else {
                        // Ocorreu um erro ao confirmar a compra
                    }
                }
            } else {
                // A compra já foi confirmada anteriormente
                viewModel.setUserPremium(true)
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            // A compra está pendente
            goToSubscriptionPurchaseScreen(true)
        } else if (purchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
            // O estado da compra é desconhecido
        }
    }

    private fun goToSubscriptionPurchaseScreen(pendingPurchase: Boolean) {
        if (!isCallSignaturePurchasedActivity) {
            Intent(this, SubscriptionPurchaseActivity::class.java).apply {
                putExtra("pendingPurchase", pendingPurchase)
                isCallSignaturePurchasedActivity = true
                startActivity(this)
                finish()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        billingClient.endConnection()
    }
}
