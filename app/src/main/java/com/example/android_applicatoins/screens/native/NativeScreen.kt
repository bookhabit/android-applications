package com.example.android_applicatoins.screens.native

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.Send
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
        ),
        AppItem(
            id = "music-player",
            title = "음악 플레이어",
            description = "안드로이드 서비스를 사용한 음악 재생 앱",
            icon = Icons.Default.MusicNote,
            route = "music-player"
        ),
        AppItem(
            id = "battery-monitor",
            title = "배터리 모니터",
            description = "배터리 상태를 실시간으로 모니터링",
            icon = Icons.Default.BatteryFull,
            route = "battery-monitor"
        ),
        AppItem(
            id = "network-monitor",
            title = "네트워크 모니터",
            description = "네트워크 상태 변화를 실시간으로 감지",
            icon = Icons.Default.Wifi,
            route = "network-monitor"
        ),
        AppItem(
            id = "boot-receiver",
            title = "부팅 자동 실행",
            description = "부팅 완료 후 자동으로 서비스 실행",
            icon = Icons.Default.Power,
            route = "boot-receiver"
        ),
        AppItem(
            id = "sms-receiver",
            title = "SMS 수신 알림",
            description = "SMS 수신을 감지하고 알림 표시",
            icon = Icons.AutoMirrored.Filled.Message,
            route = "sms-receiver"
        ),
        AppItem(
            id = "screen-monitor",
            title = "화면 상태 모니터",
            description = "화면 ON/OFF 상태를 감지하고 사용 시간 추적",
            icon = Icons.Default.Visibility,
            route = "screen-monitor"
        ),
        AppItem(
            id = "motivation",
            title = "동기부여 앱",
            description = "화면 ON 시 동기부여 콘텐츠 표시 및 목표 관리",
            icon = Icons.Default.Favorite,
            route = "motivation"
        ),
        AppItem(
            id = "storage",
            title = "데이터 저장 테스트",
            description = "SharedPreferences, 내부/외부 저장소, SQLite 테스트",
            icon = Icons.Default.Storage,
            route = "storage"
        ),
        AppItem(
            id = "share",
            title = "공용 저장소 & 공유",
            description = "외부 앱과의 데이터 공유 및 갤러리 저장 테스트",
            icon = Icons.Default.Share,
            route = "share"
        ),
        AppItem(
            id = "contacts-app",
            title = "연락처 앱",
            description = "Content Provider를 이용한 연락처 읽기/쓰기",
            icon = Icons.Default.Person,
            route = "contacts-app"
        ),
        AppItem(
            id = "file-download-app",
            title = "파일 다운로드 앱",
            description = "Content Provider를 이용한 파일 및 이미지 다운로드",
            icon = Icons.Default.Download,
            route = "file-download-app"
        ),
        AppItem(
            id = "intent-test",
            title = "인텐트 테스트",
            description = "액티비티 간 인텐트 전달을 테스트해보세요",
            icon = Icons.AutoMirrored.Filled.Send,
            route = "intent-test"
        ),
        AppItem(
            id = "implicit-intent-test",
            title = "암시적 인텐트 테스트",
            description = "다양한 암시적 인텐트를 테스트해보세요",
            icon = Icons.Default.Language,
            route = "implicit-intent-test"
        ),
        AppItem(
            id = "lifecycle-test",
            title = "생애주기 테스트",
            description = "액티비티 생애주기를 테스트해보세요",
            icon = Icons.Default.Refresh,
            route = "lifecycle-test"
        ),
        AppItem(
            id = "permission-test",
            title = "권한 테스트",
            description = "안드로이드 권한 시스템을 테스트해보세요",
            icon = Icons.Default.Security,
            route = "permission-test"
        ),
        AppItem(
            id = "network",
            title = "네트워크 & 웹 테스트",
            description = "네트워킹 상태 조회, 웹앱 개념, XML 처리 테스트",
            icon = Icons.Default.Cloud,
            route = "network"
        ),
        AppItem(
            id = "webview",
            title = "WebView 테스트",
            description = "안드로이드 WebView 사용법과 테스트",
            icon = Icons.Default.Language,
            route = "webview"
        ),
        AppItem(
            id = "multimedia",
            title = "멀티미디어 테스트",
            description = "MediaPlayer, MediaRecorder, 카메라, 비디오 등 멀티미디어 기능 테스트",
            icon = Icons.Default.MusicNote,
            route = "multimedia"
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("네이티브") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로가기")
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
