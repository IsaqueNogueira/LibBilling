package com.isaquesoft.libbilling.framework.di

import com.isaquesoft.libbilling.data.repository.SharedPreferencesRepository
import com.isaquesoft.libbilling.domain.usecase.SharedPreferencesUseCase
import com.isaquesoft.libbilling.presentation.viewmodel.SubscriptionViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.loadKoinModules
import org.koin.dsl.module

/**
 * Created by Isaque Nogueira on 11/01/2025
 */

val libBillingModule =
    module {
        factory<SharedPreferencesUseCase> { SharedPreferencesRepository(androidContext()) }
        viewModel { SubscriptionViewModel(get()) }
    }

fun loadBillingModule() {
    loadKoinModules(libBillingModule)
}
