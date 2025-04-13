package com.isaquesoft.libbilling.init

import android.content.Context
import android.content.Intent
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryPurchasesParams
import com.isaquesoft.libbilling.domain.usecase.SharedPreferencesUseCase
import com.isaquesoft.libbilling.presentation.activity.SubscriptionActivity
import com.isaquesoft.libbilling.presentation.model.Benefit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.lang.ref.WeakReference

/**
 * Created by Isaque Nogueira on 11/01/2025
 */
object LibBilling {
    private var contextRef: WeakReference<Context>? = null
    private val sharedPreferences: SharedPreferencesUseCase by inject(SharedPreferencesUseCase::class.java)
    private lateinit var billingClient: BillingClient

    const val PRODUCT_IDS = "PRODUCT_IDS"
    const val COLOR = "COLOR"
    const val LIST_BENEFIT = "LIST_BENEFIT"

    fun isUserPremium() = sharedPreferences.isUserPremium()

    fun setUserPremium(value: Boolean) = sharedPreferences.setUserPremium(value)

    fun startConnectionBillingClient(context: Context) {
        this.contextRef = WeakReference(context.applicationContext)
        connectToGooglePlayBilling()
    }

    /**
     * Abre a tela de assinaturas no aplicativo.
     *
     * @param activity O contexto da Activity que será usado para abrir a tela.
     * @param productIds Uma lista de IDs de produtos disponíveis para assinatura.
     * @param color (Opcional) Um valor de cor hexadecimal para personalizar a interface.
     * @param listBenefit (Opcional) Uma lista de benefícios associados à assinatura.
     *
     * Exemplo de uso:
     * ```
     * openSubscriptionScreen(
     *     activity = context,
     *     productIds = listOf("product_1", "product_2"),
     *     color = 0xFF1976D2.toInt(),
     *     listBenefit = listOf(
     *         Benefit(
     *             title = "Sem anúncios",
     *             description = "Elimine distrações, otimize sua experiência."
     *         )
     *     )
     * )
     * ```
     */
    fun openSubscriptionScreen(
        activity: Context,
        productIds: List<String>,
        color: Int? = null,
        listBenefit: List<Benefit> = emptyList(),
    ) {
        if (productIds.isEmpty() || productIds.size != 3) return

        val intent =
            Intent(activity, SubscriptionActivity::class.java).apply {
                putStringArrayListExtra(PRODUCT_IDS, ArrayList(productIds))
                putParcelableArrayListExtra(LIST_BENEFIT, ArrayList(listBenefit))
                putExtra(COLOR, color)
            }
        if (activity is android.app.Activity) {
            activity.startActivity(intent)
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
        }
    }

    fun endConnectionBillingClient() {
        billingClient.endConnection()
    }

    private fun getContext(): Context =
        contextRef?.get()
            ?: throw IllegalStateException("Context is not initialized. Call LibBilling.init() first.")

    private fun connectToGooglePlayBilling() {
        val scope = CoroutineScope(Dispatchers.IO)
        billingClient =
            BillingClient
                .newBuilder(getContext())
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build()

        billingClient.startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        scope.launch {
                            checkSubscriptions()
                        }
                    }
                }

                override fun onBillingServiceDisconnected() {
                    connectToGooglePlayBilling()
                }
            },
        )
    }

    private fun checkSubscriptions() {
        val params =
            QueryPurchasesParams
                .newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)

        billingClient.queryPurchasesAsync(params.build()) { billingResult, purchases ->

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (purchases.isNotEmpty()) {
                    purchases.forEach {
                        acknowledgePurchases(it)
                    }

                    var hasActiveSubscription = false
                    for (purchase in purchases) {
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && purchase.isAcknowledged) {
                            hasActiveSubscription = true
                            break
                        }
                    }
                    if (hasActiveSubscription) {
                        sharedPreferences.setUserPremium(true)
                    } else {
                        sharedPreferences.setUserPremium(false)
                    }
                } else {
                    sharedPreferences.setUserPremium(false)
                }
            } else {
                sharedPreferences.setUserPremium(false)
            }
        }
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { _, purchases ->
            purchases?.forEach {
                acknowledgePurchases(it)
            }
        }

    private fun acknowledgePurchases(purchase: Purchase?) {
        purchase?.let {
            if (!it.isAcknowledged) {
                val params =
                    AcknowledgePurchaseParams
                        .newBuilder()
                        .setPurchaseToken(it.purchaseToken)
                        .build()

                billingClient.acknowledgePurchase(
                    params,
                ) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK &&
                        it.purchaseState == Purchase.PurchaseState.PURCHASED
                    ) {
                        sharedPreferences.setUserPremium(true)
                    } else if (billingResult.responseCode == BillingClient.BillingResponseCode.OK &&
                        it.purchaseState == Purchase.PurchaseState.PENDING
                    ) {
                        sharedPreferences.setUserPremium(false)
                    } else {
                        sharedPreferences.setUserPremium(false)
                    }
                }
            }
        }
    }
}
