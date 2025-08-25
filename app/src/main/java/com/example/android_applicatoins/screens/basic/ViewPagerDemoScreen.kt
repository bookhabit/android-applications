package com.example.android_applicatoins.screens.basic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPagerDemoScreen(
    onBackPressed: () -> Unit
) {
    var currentPage by remember { mutableStateOf(0) }
    var pageOffset by remember { mutableStateOf(0f) }
    
    // 페이지 데이터
    data class PageInfo(
        val id: Int,
        val title: String,
        val description: String,
        val icon: String,
        val content: String,
        val color: Color
    )
    
    val pages = listOf(
        PageInfo(0, "첫 번째 페이지", "ViewPager의 첫 번째 화면", "1️⃣", 
                "ViewPager는 좌우 스와이프로 페이지를 전환할 수 있는 컨테이너입니다.\n\n" +
                "주요 특징:\n• 좌우 스와이프 제스처\n• 페이지 전환 애니메이션\n• Fragment와 연동 가능\n• TabLayout과 함께 사용",
                Color(0xFFE3F2FD)),
        
        PageInfo(1, "두 번째 페이지", "ViewPager의 두 번째 화면", "2️⃣", 
                "이 페이지에서는 ViewPager의 다양한 기능을 살펴볼 수 있습니다.\n\n" +
                "사용 사례:\n• 온보딩 화면\n• 이미지 갤러리\n• 설정 화면\n• 튜토리얼",
                Color(0xFFF3E5F5)),
        
        PageInfo(2, "세 번째 페이지", "ViewPager의 세 번째 화면", "3️⃣", 
                "ViewPager2는 RecyclerView 기반으로 만들어져 더 나은 성능을 제공합니다.\n\n" +
                "개선 사항:\n• RTL 지원\n• 수직 스와이프\n• 더 나은 접근성\n• 향상된 성능",
                Color(0xFFE8F5E8)),
        
        PageInfo(3, "네 번째 페이지", "ViewPager의 네 번째 화면", "4️⃣", 
                "Jetpack Compose에서는 LazyRow와 함께 사용하여 ViewPager를 구현할 수 있습니다.\n\n" +
                "Compose 방식:\n• LazyRow + rememberLazyListState\n• 스와이프 제스처 처리\n• 페이지 인디케이터\n• 자동 페이지 전환",
                Color(0xFFFFF3E0)),
        
        PageInfo(4, "다섯 번째 페이지", "ViewPager의 마지막 화면", "5️⃣", 
                "ViewPager 데모를 마칩니다!\n\n" +
                "학습한 내용:\n• ViewPager 기본 개념\n• 페이지 전환 방식\n• 다양한 사용 사례\n• Compose에서의 구현",
                Color(0xFFFCE4EC))
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ViewPager 데모") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. 페이지 인디케이터 (TabLayout과 유사)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "페이지 인디케이터",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        pages.forEachIndexed { index, page ->
                            Card(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { currentPage = index },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (currentPage == index) 
                                        MaterialTheme.colorScheme.primary 
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = page.icon,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (currentPage == index) 
                                            MaterialTheme.colorScheme.onPrimary 
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "현재 페이지: ${currentPage + 1} / ${pages.size}",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // 2. ViewPager 컨테이너 (실제 ViewPager와 유사)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures { _, dragAmount ->
                                pageOffset += dragAmount.x
                                
                                // 스와이프 제스처 처리
                                if (abs(pageOffset) > 100) {
                                    if (pageOffset > 0 && currentPage > 0) {
                                        currentPage--
                                    } else if (pageOffset < 0 && currentPage < pages.size - 1) {
                                        currentPage++
                                    }
                                    pageOffset = 0f
                                }
                            }
                        }
                ) {
                    // 현재 페이지 표시
                    val currentPageInfo = pages[currentPage]
                    if (currentPageInfo != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(currentPageInfo.color)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = currentPageInfo.icon,
                                style = MaterialTheme.typography.displayLarge
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                text = currentPageInfo.title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = currentPageInfo.description,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(8.dp)
                            ) {
                                Text(
                                    text = currentPageInfo.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 24.sp,
                                    modifier = Modifier.padding(20.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // 3. 네비게이션 컨트롤 (ViewPager 컨트롤과 유사)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // 이전 페이지 버튼
                    Button(
                        onClick = { 
                            if (currentPage > 0) currentPage-- 
                        },
                        enabled = currentPage > 0
                    ) {
                        Icon(Icons.Default.NavigateBefore, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("이전")
                    }
                    
                    // 페이지 점프 버튼들
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pages.forEachIndexed { index, _ ->
                            Button(
                                onClick = { currentPage = index },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentPage == index) 
                                        MaterialTheme.colorScheme.primary 
                                    else MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Text("${index + 1}")
                            }
                        }
                    }
                    
                    // 다음 페이지 버튼
                    Button(
                        onClick = { 
                            if (currentPage < pages.size - 1) currentPage++ 
                        },
                        enabled = currentPage < pages.size - 1
                    ) {
                        Text("다음")
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.NavigateNext, contentDescription = null)
                    }
                }
            }
            
            // 4. 페이지 정보 표시
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "ViewPager 정보",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    val currentPageInfo = pages[currentPage]
                    if (currentPageInfo != null) {
                        ListItem(
                            headlineContent = { Text("현재 페이지: ${currentPageInfo.title}") },
                            supportingContent = { Text("ID: ${currentPageInfo.id}") },
                            leadingContent = { 
                                Text(
                                    text = currentPageInfo.icon,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        )
                        
                        ListItem(
                            headlineContent = { Text("페이지 설명") },
                            supportingContent = { Text(currentPageInfo.description) },
                            leadingContent = { 
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                        
                        ListItem(
                            headlineContent = { Text("전체 페이지 수") },
                            supportingContent = { Text("${pages.size}개") },
                            leadingContent = { 
                                Icon(
                                    Icons.Default.ViewCarousel,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                }
            }
        }
        
        // 페이지 전환 애니메이션
        LaunchedEffect(currentPage) {
            pageOffset = 0f
        }
    }
}
