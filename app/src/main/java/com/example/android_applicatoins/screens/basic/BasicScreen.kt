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
        ),
        AppItem(
            id = "form-example",
            title = "폼 예제",
            description = "다양한 폼 요소들을 사용한 사용자 입력 예제",
            icon = Icons.Default.Description,
            route = "form-example"
        ),
        AppItem(
            id = "action-sheet",
            title = "액션 시트",
            description = "사용자 선택을 위한 액션 시트 예제",
            icon = Icons.Default.MoreVert,
            route = "action-sheet"
        ),
        AppItem(
            id = "modal",
            title = "모달",
            description = "다양한 모달 창과 다이얼로그 예제",
            icon = Icons.Default.OpenInNew,
            route = "modal"
        ),
        AppItem(
            id = "movie-info",
            title = "영화 정보",
            description = "영화 정보를 표시하고 관리하는 앱",
            icon = Icons.Default.Movie,
            route = "movie-info"
        ),
        AppItem(
            id = "calendar",
            title = "캘린더",
            description = "일정 관리와 날짜 확인을 위한 캘린더 앱",
            icon = Icons.Default.CalendarToday,
            route = "calendar"
        ),
        AppItem(
            id = "step-counter",
            title = "스텝 카운터",
            description = "걸음 수를 측정하고 건강 정보를 관리하는 앱",
            icon = Icons.Default.DirectionsWalk,
            route = "step-counter"
        ),
        AppItem(
            id = "gallery",
            title = "갤러리",
            description = "이미지들을 그리드 형태로 표시하고 관리하는 앱",
            icon = Icons.Default.PhotoLibrary,
            route = "gallery"
        ),
        AppItem(
            id = "intent-test",
            title = "인텐트 테스트",
            description = "액티비티 간 인텐트 전달을 테스트해보세요",
            icon = Icons.Default.Send,
            route = "intent-test"
        ),
        AppItem(
            id = "implicit-intent-test",
            title = "암시적 인텐트 테스트",
            description = "다양한 암시적 인텐트를 테스트해보세요",
            icon = Icons.Default.Language,
            route = "implicit-intent-test"
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
