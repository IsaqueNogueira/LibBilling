package com.isaquesoft.libbilling.presentation.state

import com.isaquesoft.libbilling.presentation.model.ProductsSubscription

/**
 * Created by Isaque Nogueira on 21/10/2024
 */
sealed class ProductsSubscriptionState {
    object Idle : ProductsSubscriptionState()

    object Loading : ProductsSubscriptionState()

    object Failure : ProductsSubscriptionState()

    data class ShowProducts(
        val products: List<ProductsSubscription>,
    ) : ProductsSubscriptionState()
}
