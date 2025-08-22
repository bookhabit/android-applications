package com.example.android_applicatoins.screens.native

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
fun NativeScreen(
    onBackPressed: () -> Unit,
    onAppSelected: (String) -> Unit
) {
    val nativeApps = listOf(
        AppItem(
            id = "media-notes",
            title = "미디어 노트",
            description = "음성과 이미지를 포함한 노트를 작성하세요",
            icon = Icons.Default.Mic,
            route = "media-notes"
        ),
        AppItem(
            id = "mini-file-explorer",
            title = "미니 파일 탐색기",
            description = "기기 내 파일을 탐색하고 관리하세요",
            icon = Icons.Default.Folder,
            route = "mini-file-explorer"
        ),
        AppItem(
            id = "mini-health-tracker",
            title = "미니 건강 추적기",
            description = "건강 데이터를 추적하고 기록하세요",
            icon = Icons.Default.Favorite,
            route = "mini-health-tracker"
        ),
        AppItem(
            id = "secure-notes",
            title = "보안 노트",
            description = "암호화된 보안 노트를 작성하세요",
            icon = Icons.Default.Lock,
            route = "secure-notes"
        ),
        AppItem(
            id = "sensor-playground",
            title = "센서 놀이터",
            description = "기기의 다양한 센서를 체험해보세요",
            icon = Icons.Default.Sensors,
            route = "sensor-playground"
        ),
        AppItem(
            id = "simple-shop",
            title = "간단한 쇼핑",
            description = "로컬 저장소를 사용한 쇼핑 앱을 즐기세요",
            icon = Icons.Default.ShoppingCart,
            route = "simple-shop"
        ),
        AppItem(
            id = "trip-logger",
            title = "여행 로거",
            description = "GPS를 사용한 여행 기록을 남기세요",
            icon = Icons.Default.Place,
            route = "trip-logger"
        ),
        AppItem(
            id = "utility-kit",
            title = "유틸리티 키트",
            description = "다양한 유틸리티 도구들을 사용하세요",
            icon = Icons.Default.Build,
            route = "utility-kit"
        ),
        AppItem(
            id = "stopwatch",
            title = "스톱워치",
            description = "시간을 측정하고 기록하세요",
            icon = Icons.Default.Timer,
            route = "stopwatch"
        ),
        AppItem(
            id = "flashlight",
            title = "손전등",
            description = "화면을 밝게 만들어 손전등으로 사용하세요",
            icon = Icons.Default.FlashlightOn,
            route = "flashlight"
        ),
        AppItem(
            id = "compass",
            title = "나침반",
            description = "방향을 확인하고 길을 찾으세요",
            icon = Icons.Default.Explore,
            route = "compass"
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("네이티브") },
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
            items(nativeApps) { appItem ->
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
