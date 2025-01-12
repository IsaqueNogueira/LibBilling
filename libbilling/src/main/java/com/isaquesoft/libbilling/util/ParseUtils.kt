package com.isaquesoft.libbilling.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Created by Isaque Nogueira on 13/10/2024
 */
fun String.toDoubleCoin(): Double =
    try {
        val cleanValue =
            this
                .replace(Regex("[^\\d.,]"), "")
                .replace(Regex("[.,]"), "")

        cleanValue.toDoubleOrNull()?.div(100) ?: 0.0
    } catch (e: Exception) {
        0.0
    }

fun parseCurrencyValue(text: String): Double =
    try {
        val cleanValue =
            text
                .replace(Regex("[^\\d.,]"), "")
                .replace(Regex("[.,]"), "")

        cleanValue.toDoubleOrNull()?.div(100) ?: 0.0
    } catch (e: Exception) {
        0.0
    }

fun getSubscriptionDuration(namePlano: String): Int =
    when {
        namePlano.contains("mensal", ignoreCase = true) -> 1
        namePlano.contains("trimestral", ignoreCase = true) -> 3
        namePlano.contains("semestral", ignoreCase = true) -> 6
        namePlano.contains("anual", ignoreCase = true) -> 12
        else -> 0
    }

fun formatterDoubleInCoin(double: Double?): String {
    val localizacao: Locale = Locale.getDefault()
    val formatador: NumberFormat = NumberFormat.getCurrencyInstance(localizacao)
    return formatador.format(double)
}
