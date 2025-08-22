package com.example.android_applicatoins

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.android_applicatoins.navigation.AppNavigation
import com.example.android_applicatoins.ui.theme.AndroidapplicatoinsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidapplicatoinsTheme {
                AppNavigation()
            }
        }
    }
}