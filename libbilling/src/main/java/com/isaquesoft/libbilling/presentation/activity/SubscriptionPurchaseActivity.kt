package com.isaquesoft.libbilling.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.isaquesoft.libbilling.presentation.screens.SubscriptionPurchaseScreen
import com.isaquesoft.libbilling.presentation.ui.theme.LibBillingTheme

/**
 * Created by Isaque Nogueira on 02/11/2024
 */
class SubscriptionPurchaseActivity : ComponentActivity() {
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                restartActivity()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        val pendingPurchase = intent.getBooleanExtra("pendingPurchase", false)
        setContent {
            LibBillingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                ) {
                    SubscriptionPurchaseScreen(pendingPurchase = pendingPurchase) {
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        }
    }

    private fun restartActivity() {
        val intent =
            packageManager.getLaunchIntentForPackage(packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }

        if (intent != null) {
            startActivity(intent)
            finish()
            Runtime.getRuntime().exit(0)
        }
    }
}
