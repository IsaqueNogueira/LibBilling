package com.isaquesoft.libbilling.presentation.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.isaquesoft.libbilling.R
import com.isaquesoft.libbilling.presentation.ui.theme.GrayDark
import com.isaquesoft.libbilling.presentation.ui.theme.GrayLight
import com.isaquesoft.libbilling.presentation.ui.theme.TextBlack
import com.isaquesoft.libbilling.presentation.ui.theme.TextGray

/**
 * Created by Isaque Nogueira on 11/01/2025
 */
@Composable
fun SubscriptionPurchaseScreen(
    pendingPurchase: Boolean,
    onFinish: () -> Unit = {},
) {
    val statusBarLight = GrayLight.toArgb()
    val view = LocalView.current
    val isDarkMod = isSystemInDarkTheme()

    DisposableEffect(isDarkMod) {
        val activity = view.context as Activity
        activity.window.statusBarColor = statusBarLight

        WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
            isAppearanceLightStatusBars = true
        }

        onDispose { }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(GrayLight),
    ) {
        val compostion by rememberLottieComposition(
            spec = LottieCompositionSpec.Asset("donecheck.json"),
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Icon(
                painter = painterResource(id = R.drawable.x_lib_billing),
                contentDescription = "Voltar",
                tint = GrayDark,
                modifier =
                    Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false),
                            onClick = {
                                onFinish.invoke()
                            },
                        ).padding(16.dp),
            )
        }

        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LottieAnimation(
                composition = compostion,
                iterations = LottieConstants.IterateForever,
                modifier =
                    Modifier
                        .width(80.dp)
                        .height(80.dp)
                        .padding(top = 16.dp),
            )

            Text(
                "Obrigado!",
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 16.dp),
                color = TextBlack,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            val congratulationsMessage =
                if (pendingPurchase) {
                    "Agradecemos por adquirir nossa assinatura premium! Seu pedido está sendo processado. Quando o pagamento for aprovado, você terá acesso total aos recursos premium do nosso aplicativo. Aproveite ao máximo sua experiência!"
                } else {
                    "Agradecemos imensamente por adquirir a assinatura do nosso aplicativo. Esperamos que você aproveite ao máximo!"
                }
            Text(
                congratulationsMessage,
                fontSize = 14.sp,
                modifier = Modifier.padding(16.dp),
                color = TextGray,
                textAlign = TextAlign.Center,
            )
        }
    }
}
