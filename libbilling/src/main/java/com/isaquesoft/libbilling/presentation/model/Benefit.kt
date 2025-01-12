package com.isaquesoft.libbilling.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Isaque Nogueira on 12/01/2025
 */
@Parcelize
data class Benefit(
    val title: String,
    val description: String,
) : Parcelable
