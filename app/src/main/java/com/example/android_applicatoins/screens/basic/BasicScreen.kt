package com.example.android_applicatoins.screens.basic

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
fun BasicScreen(
    onBackPressed: () -> Unit,
    onAppSelected: (String) -> Unit
) {
    val basicApps = listOf(
        AppItem(
            id = "todo",
            title = "할일 관리",
            description = "일정과 할일을 체계적으로 관리하세요",
            icon = Icons.Default.CheckCircle,
            route = "todo"
        ),
        AppItem(
            id = "notes",
            title = "메모장",
            description = "간단한 메모를 작성하고 저장하세요",
            icon = Icons.Default.Note,
            route = "notes"
        ),
        AppItem(
            id = "weather",
            title = "날씨 앱",
            description = "현재 날씨와 예보를 확인하세요",
            icon = Icons.Default.WbSunny,
            route = "weather"
        ),
        AppItem(
            id = "calculator",
            title = "계산기",
            description = "기본적인 수학 계산을 수행하세요",
            icon = Icons.Default.Calculate,
            route = "calculator"
        ),
        AppItem(
            id = "calendar",
            title = "달력",
            description = "일정을 관리하고 날짜를 확인하세요",
            icon = Icons.Default.CalendarToday,
            route = "calendar"
        ),
        AppItem(
            id = "gallery",
            title = "갤러리",
            description = "사진과 이미지를 관리하세요",
            icon = Icons.Default.PhotoLibrary,
            route = "gallery"
        ),
        AppItem(
            id = "form",
            title = "폼 작성",
            description = "다양한 폼을 작성하고 관리하세요",
            icon = Icons.Default.Description,
            route = "form"
        ),
        AppItem(
            id = "modal",
            title = "모달",
            description = "모달 창을 사용한 인터페이스를 확인하세요",
            icon = Icons.Default.OpenInNew,
            route = "modal"
        ),
        AppItem(
            id = "action-sheet",
            title = "액션 시트",
            description = "액션 시트를 사용한 메뉴를 확인하세요",
            icon = Icons.Default.MoreVert,
            route = "action-sheet"
        ),
        AppItem(
            id = "step-counter",
            title = "걸음 수",
            description = "일일 걸음 수를 추적하고 기록하세요",
            icon = Icons.Default.DirectionsWalk,
            route = "step-counter"
        ),
        AppItem(
            id = "shop",
            title = "쇼핑",
            description = "온라인 쇼핑을 즐기세요",
            icon = Icons.Default.ShoppingCart,
            route = "shop"
        ),
        AppItem(
            id = "event-test",
            title = "이벤트 테스트",
            description = "다양한 이벤트 처리를 테스트해보세요",
            icon = Icons.Default.TouchApp,
            route = "event-test"
        ),
        AppItem(
            id = "graphics-drawing",
            title = "그래픽 그리기",
            description = "Canvas와 Paint로 그래픽을 그려보세요",
            icon = Icons.Default.Brush,
            route = "graphics-drawing"
        ),
        AppItem(
            id = "image-display",
            title = "이미지 표시",
            description = "다양한 이미지 표시 방법을 테스트해보세요",
            icon = Icons.Default.Image,
            route = "image-display"
        ),
        AppItem(
            id = "dialog-popup-notification",
            title = "대화상자 & 팝업 & 알림",
            description = "다양한 대화상자, 팝업, 알림을 테스트해보세요",
            icon = Icons.Default.Notifications,
            route = "dialog-popup-notification"
        ),
        AppItem(
            id = "list-view",
            title = "리스트 뷰",
            description = "ListView, RecyclerView, Spinner 등 다양한 리스트 기술을 학습하세요",
            icon = Icons.Default.List,
            route = "list-view"
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("기본 앱") },
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
            items(basicApps) { appItem ->
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
