package com.isaquesoft.libbilling.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.isaquesoft.libbilling.R

// Set of Material typography styles to start with

val poppinsFontFamily =
    FontFamily(
        Font(resId = R.font.poppins, weight = FontWeight.Normal),
        Font(resId = R.font.poppins_bold, weight = FontWeight.Bold),
    )
val latoFontFamily =
    FontFamily(
        Font(resId = R.font.lato, weight = FontWeight.Normal),
        Font(resId = R.font.lato_bold, weight = FontWeight.Bold),
    )
val latoBlack = FontFamily(Font(resId = R.font.lato_black))

val Typography =
    Typography(
        bodyLarge =
            TextStyle(
                fontFamily = latoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp,
            ),
        /* Other default text styles to override
        titleLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        labelSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
         */
    )
