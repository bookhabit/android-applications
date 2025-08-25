package com.example.android_applicatoins.screens.basic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FragmentDemoScreen(
    onBackPressed: () -> Unit
) {
    var currentFragment by remember { mutableStateOf("home") }
    var fragmentHistory by remember { mutableStateOf(listOf("home")) }
    
    // Fragment 데이터
    data class FragmentInfo(
        val id: String,
        val title: String,
        val description: String,
        val icon: String,
        val content: String
    )
    
    val fragments = mapOf(
        "home" to FragmentInfo("home", "홈", "메인 화면", "🏠", "안녕하세요! Fragment 데모에 오신 것을 환영합니다.\n\n이 화면은 Fragment의 개념을 보여줍니다:\n• 독립적인 UI 단위\n• 재사용 가능한 컴포넌트\n• 독립적인 생명주기\n• Activity와 분리된 로직"),
        
        "profile" to FragmentInfo("profile", "프로필", "사용자 정보", "👤", "프로필 Fragment입니다.\n\n사용자 정보:\n• 이름: 김철수\n• 나이: 25세\n• 직업: 개발자\n• 위치: 서울\n\n이 Fragment는 다른 화면에서도 재사용할 수 있습니다."),
        
        "settings" to FragmentInfo("settings", "설정", "앱 설정", "⚙️", "설정 Fragment입니다.\n\n앱 설정 옵션:\n• 알림 설정\n• 테마 변경\n• 언어 설정\n• 개인정보 관리\n• 계정 설정\n\n각 설정은 독립적으로 관리됩니다."),
        
        "notifications" to FragmentInfo("notifications", "알림", "알림 목록", "🔔", "알림 Fragment입니다.\n\n새로운 알림:\n• 메시지 3개\n• 업데이트 1개\n• 친구 요청 2개\n\n알림을 탭하여 상세 내용을 확인하세요."),
        
        "favorites" to FragmentInfo("favorites", "즐겨찾기", "즐겨찾기 목록", "⭐", "즐겨찾기 Fragment입니다.\n\n즐겨찾기된 항목들:\n• 중요한 메모\n• 자주 사용하는 기능\n• 즐겨찾기한 링크\n• 저장된 이미지\n\n즐겨찾기를 관리하세요."),
        
        "help" to FragmentInfo("help", "도움말", "사용법 안내", "❓", "도움말 Fragment입니다.\n\nFragment 사용법:\n1. Fragment는 독립적인 UI 단위입니다\n2. 여러 Activity에서 재사용할 수 있습니다\n3. 독립적인 생명주기를 가집니다\n4. FragmentManager로 관리됩니다\n\n더 자세한 내용은 공식 문서를 참조하세요.")
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fragment 데모") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "뒤로가기")
                    }
                },
                actions = {
                    // Fragment 히스토리 표시
                    if (fragmentHistory.size > 1) {
                        IconButton(
                            onClick = {
                                if (fragmentHistory.size > 1) {
                                    fragmentHistory = fragmentHistory.dropLast(1)
                                    currentFragment = fragmentHistory.last()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Undo, "뒤로가기")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Fragment 네비게이션 (FragmentManager와 유사)
            Card(
                modifier = Modifier
                    .width(200.dp)
                    .fillMaxHeight(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Fragment 목록",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    fragments.values.forEach { fragment ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    currentFragment = fragment.id
                                    fragmentHistory = fragmentHistory + fragment.id
                                },
                            elevation = CardDefaults.cardElevation(
                                if (currentFragment == fragment.id) 4.dp else 1.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (currentFragment == fragment.id) 
                                    MaterialTheme.colorScheme.primaryContainer 
                                else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = fragment.icon,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = fragment.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (currentFragment == fragment.id) 
                                        FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
            
            // 2. Fragment 컨테이너 (FragmentTransaction과 유사)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Fragment 헤더
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = fragments[currentFragment]?.icon ?: "",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = fragments[currentFragment]?.title ?: "",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        // Fragment 생명주기 상태 표시
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                text = "활성",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Fragment 내용 (onCreateView와 유사)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Fragment 내용",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            Text(
                                text = fragments[currentFragment]?.content ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 20.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Fragment 정보 (FragmentManager 정보와 유사)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Fragment 정보",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            val currentFragmentInfo = fragments[currentFragment]
                            if (currentFragmentInfo != null) {
                                Text("ID: ${currentFragmentInfo.id}")
                                Text("제목: ${currentFragmentInfo.title}")
                                Text("설명: ${currentFragmentInfo.description}")
                                Text("현재 시간: ${System.currentTimeMillis()}")
                            }
                        }
                    }
                }
            }
        }
        
        // Fragment 전환 애니메이션 시뮬레이션
        LaunchedEffect(currentFragment) {
            // Fragment 전환 시 잠시 로딩 효과
            kotlinx.coroutines.delay(100)
        }
    }
}
