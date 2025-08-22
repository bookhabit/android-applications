package com.example.android_applicatoins.model

import androidx.compose.ui.graphics.vector.ImageVector

data class AppItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String
)
