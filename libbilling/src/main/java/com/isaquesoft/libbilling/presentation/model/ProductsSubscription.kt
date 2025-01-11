package com.isaquesoft.libbilling.presentation.model

import com.android.billingclient.api.ProductDetails

/**
 * Created by Isaque Nogueira on 11/01/2025
 */
data class ProductsSubscription(
    val name: String,
    val value: String?,
    val productDetails: ProductDetails? = null,
)
