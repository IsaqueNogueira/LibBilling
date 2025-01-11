package com.isaquesoft.sample.framework.application

import android.app.Application
import com.isaquesoft.libbilling.framework.di.loadBillingModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class AppAplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            modules(listOf())
        }

        loadBillingModule()
    }
}
