package com.isaquesoft.libbilling.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaquesoft.libbilling.domain.usecase.SharedPreferencesUseCase
import com.isaquesoft.libbilling.presentation.model.ProductsSubscription
import com.isaquesoft.libbilling.presentation.state.ProductsSubscriptionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by Isaque Nogueira on 21/10/2024
 */
class SubscriptionViewModel(
    private val sharedPreferences: SharedPreferencesUseCase,
) : ViewModel() {
    private val _productsSubscriptionState: MutableStateFlow<ProductsSubscriptionState> =
        MutableStateFlow(ProductsSubscriptionState.Idle)

    val productsSubscriptionState: StateFlow<ProductsSubscriptionState>
        get() = _productsSubscriptionState.asStateFlow()

    fun isUserPremium(): Boolean = sharedPreferences.isUserPremium()

    fun setUserPremium(value: Boolean) {
        viewModelScope.launch {
            sharedPreferences.setUserPremium(value)
        }
    }

    fun showProducts(products: List<ProductsSubscription>) {
        _productsSubscriptionState.value = ProductsSubscriptionState.ShowProducts(products)
    }
}
