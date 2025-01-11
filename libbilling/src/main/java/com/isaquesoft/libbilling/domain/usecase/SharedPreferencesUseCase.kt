package com.isaquesoft.libbilling.domain.usecase

/**
 * Created by Isaque Nogueira on 11/01/2025
 */
interface SharedPreferencesUseCase {
    fun isUserPremium(): Boolean

    fun setUserPremium(value: Boolean)
}
