package com.example.android_applicatoins.screens.animation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.android_applicatoins.components.AppCard
import com.example.android_applicatoins.model.AppItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimationScreen(
    onBackPressed: () -> Unit,
    onAppSelected: (String) -> Unit
) {
    val animationApps = listOf(
        AppItem(
            id = "cook-explorer",
            title = "요리 탐험가",
            description = "요리 애니메이션을 즐기세요",
            icon = Icons.Default.Restaurant,
            route = "cook-explorer"
        ),
        AppItem(
            id = "mind-flow",
            title = "마음의 흐름",
            description = "명상과 마음의 평화를 찾아보세요",
            icon = Icons.Default.Psychology,
            route = "mind-flow"
        ),
        AppItem(
            id = "money-journey",
            title = "돈의 여정",
            description = "재정 관리 애니메이션을 확인하세요",
            icon = Icons.Default.AccountBalance,
            route = "money-journey"
        ),
        AppItem(
            id = "quiz-hero",
            title = "퀴즈 히어로",
            description = "애니메이션 퀴즈를 풀어보세요",
            icon = Icons.Default.Quiz,
            route = "quiz-hero"
        ),
        AppItem(
            id = "travel-dreams",
            title = "여행의 꿈",
            description = "여행 애니메이션을 즐기세요",
            icon = Icons.Default.Flight,
            route = "travel-dreams"
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("애니메이션") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(animationApps) { appItem ->
                AppCard(
                    appItem = appItem,
                    onClick = {
                        onAppSelected(appItem.id)
                    }
                )
            }
        }
    }
}
