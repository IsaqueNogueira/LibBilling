package com.isaquesoft.libbilling.data.repository

import android.content.Context
import com.isaquesoft.libbilling.domain.usecase.SharedPreferencesUseCase

/**
 * Created by Isaque Nogueira on 11/01/2025
 */
internal class SharedPreferencesRepository(context: Context) :
    SharedPreferencesUseCase {

    companion object {
        private const val USER_PREMIUM = "user_premium"
    }


    private val sharedPreferences =
        context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

    override fun isUserPremium(): Boolean {
        return sharedPreferences.getBoolean(USER_PREMIUM, false)
    }

    override fun setUserPremium(value: Boolean) {
        sharedPreferences.edit().putBoolean(USER_PREMIUM, value).apply()
    }
}