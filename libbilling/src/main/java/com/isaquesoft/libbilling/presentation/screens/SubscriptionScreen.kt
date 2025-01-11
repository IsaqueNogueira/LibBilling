package com.isaquesoft.libbilling.presentation.screens

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.android.billingclient.api.ProductDetails
import com.isaquesoft.libbilling.R
import com.isaquesoft.libbilling.presentation.model.ProductsSubscription
import com.isaquesoft.libbilling.presentation.state.ProductsSubscriptionState
import com.isaquesoft.libbilling.presentation.ui.theme.BlueApp
import com.isaquesoft.libbilling.presentation.ui.theme.GrayDark
import com.isaquesoft.libbilling.presentation.ui.theme.GrayLight
import com.isaquesoft.libbilling.presentation.ui.theme.TextGray
import com.isaquesoft.libbilling.presentation.ui.theme.latoBlack
import com.isaquesoft.libbilling.presentation.ui.theme.latoFontFamily
import com.isaquesoft.libbilling.presentation.viewmodel.SubscriptionViewModel
import com.isaquesoft.libbilling.util.formatterDoubleInCoin
import com.isaquesoft.libbilling.util.getSubscriptionDuration
import com.isaquesoft.libbilling.util.parseCurrencyValue

/**
 * Created by Isaque Nogueira on 18/10/2024
 */

@Composable
fun SubscriptionScreen(
    viewModel: SubscriptionViewModel,
    itemClick: (productDetail: ProductDetails) -> Unit = {},
    onFinish: () -> Unit = {},
) {
    val statusBarLight = GrayLight.toArgb()
    val view = LocalView.current
    val isDarkMod = isSystemInDarkTheme()
    val state by viewModel.productsSubscriptionState.collectAsState()

    DisposableEffect(isDarkMod) {
        val activity = view.context as Activity
        activity.window.statusBarColor = statusBarLight

        WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
            isAppearanceLightStatusBars = true
        }

        onDispose { }
    }

    when (val result = state) {
        is ProductsSubscriptionState.ShowProducts -> {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(GrayLight),
            ) {
                val compostion by rememberLottieComposition(
                    spec = LottieCompositionSpec.Asset("premium.json"),
                )

                Box {
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
                                ).align(Alignment.TopEnd)
                                .padding(16.dp),
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
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
                    }
                }

                Text(
                    text = "Compre o Premium,",
                    color = BlueApp,
                    fontSize = 26.sp,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp),
                    fontFamily = latoBlack,
                )
                Text(
                    text = "desbloqueie todos os\nrecursos",
                    color = Color(0xFF343434),
                    fontSize = 26.sp,
                    modifier = Modifier.padding(start = 20.dp, end = 16.dp),
                    fontFamily = latoBlack,
                )

                Row(
                    modifier = Modifier.padding(top = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.seal_check_lib_billing),
                        contentDescription = "Check",
                        tint = BlueApp,
                        modifier =
                            Modifier
                                .padding(start = 20.dp)
                                .size(30.dp),
                    )

                    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                        Text(
                            text = "Sem anúncios",
                            color = Color(0xFF343434),
                            fontFamily = latoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                        )

                        Text(
                            text = "Elimine distrações, otimize sua experiência:\nAssine agora e liberte-se dos anúncios.",
                            textAlign = TextAlign.Start,
                            color = GrayDark,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        )
                    }
                }

                HorizontalItemList(result.products, itemClick = itemClick)
            }
        }

        else -> {}
    }
}

@Composable
fun HorizontalItemList(
    list: List<ProductsSubscription> = emptyList(),
    itemClick: (productDetail: ProductDetails) -> Unit = {},
) {
    val productsSubscription = list.sortedBy { parseCurrencyValue(it.value ?: "") }

    var productDetailsSelected: ProductDetails? by remember {
        mutableStateOf(
            productsSubscription
                .sortedBy { parseCurrencyValue(it.value ?: "") }
                .last()
                .productDetails,
        )
    }

    LazyRow(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        contentPadding = PaddingValues(horizontal = 20.dp),
    ) {
        items(productsSubscription) { item ->

            val isSelected = item.productDetails == productDetailsSelected

            Card(
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, if (isSelected) BlueApp else GrayDark),
                colors =
                    CardDefaults.cardColors(
                        Color.White,
                    ),
                modifier =
                    Modifier.clickable {
                        productDetailsSelected = item.productDetails
                        item.productDetails?.let {
                            itemClick.invoke(item.productDetails)
                        }
                    },
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = item.name.uppercase(),
                            color = TextGray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 10.dp, start = 14.dp, end = 14.dp),
                        )

                        Text(
                            text = item.value.toString(),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 6.dp, end = 14.dp, start = 14.dp),
                            fontFamily = latoBlack,
                        )

                        item.value?.let {
                            val valueDouble = parseCurrencyValue(item.value)
                            val priceForMonth = valueDouble / getSubscriptionDuration(item.name)
                            Text(
                                text = formatterDoubleInCoin(priceForMonth) + "/mês",
                                modifier =
                                    Modifier.padding(
                                        start = 14.dp,
                                        end = 14.dp,
                                        top = 6.dp,
                                        bottom = 10.dp,
                                    ),
                                color = TextGray,
                                fontFamily = latoFontFamily,
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }
        }
    }

    Button(
        onClick = { productDetailsSelected?.let { (itemClick.invoke(it)) } },
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 40.dp),
        colors = ButtonDefaults.buttonColors(BlueApp),
    ) {
        Text(text = "Assinar", color = Color.White)
    }

    Text(
        text = "*A assinatura é renovada automaticamente. Você pode cancelá-la nas configurações do Google Play.",
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 16.dp,
                    bottom = 20.dp,
                ),
        color = TextGray,
        textAlign = TextAlign.Center,
        fontFamily = latoFontFamily,
        fontSize = 14.sp,
    )
}
