package com.example.android_applicatoins.screens.basic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeLazyDemoScreen(
    onBackPressed: () -> Unit
) {
    var selectedItem by remember { mutableStateOf<String?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }
    
    // 샘플 데이터
    data class Item(
        val id: Int,
        val title: String,
        val description: String,
        val category: String,
        val icon: String,
        val color: Color
    )
    
    val items = listOf(
        Item(1, "사과", "빨간 사과입니다", "과일", "🍎", Color(0xFFFFCDD2)),
        Item(2, "바나나", "노란 바나나입니다", "과일", "🍌", Color(0xFFFFF9C4)),
        Item(3, "포도", "보라 포도입니다", "과일", "🍇", Color(0xFFE1BEE7)),
        Item(4, "수박", "초록 수박입니다", "과일", "🍉", Color(0xFFC8E6C9)),
        Item(5, "오렌지", "주황 오렌지입니다", "과일", "🍊", Color(0xFFFFE0B2)),
        Item(6, "키위", "갈색 키위입니다", "과일", "🥝", Color(0xFFDCEDC8)),
        Item(7, "망고", "노란 망고입니다", "과일", "🥭", Color(0xFFFFF8E1)),
        Item(8, "파인애플", "노란 파인애플입니다", "과일", "🍍", Color(0xFFFFF3E0))
    )
    
    val categories = listOf("과일", "채소", "육류", "해산물", "음료", "디저트")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compose Lazy 데모") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. LazyColumn - 세로 스크롤 리스트
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "1. LazyColumn - 세로 스크롤 리스트",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Text(
                            text = "RecyclerView의 세로 리스트를 대체하는 Compose 컴포넌트",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        LazyColumn(
                            modifier = Modifier.height(200.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(items.take(6)) { item ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedItem = item.title
                                            showSnackbar = true
                                        },
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    ListItem(
                                        headlineContent = { Text(item.title) },
                                        supportingContent = { Text(item.description) },
                                        leadingContent = { 
                                            Text(
                                                text = item.icon,
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // 2. LazyRow - 가로 스크롤 리스트
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "2. LazyRow - 가로 스크롤 리스트",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Text(
                            text = "LinearLayoutManager.HORIZONTAL과 유사한 가로 스크롤",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(items.take(5)) { item ->
                                Card(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .clickable {
                                            selectedItem = item.title
                                            showSnackbar = true
                                        },
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = item.icon,
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = item.category,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // 3. LazyVerticalGrid - 그리드 리스트
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "3. LazyVerticalGrid - 그리드 리스트",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Text(
                            text = "GridLayoutManager와 유사한 2열 그리드 레이아웃",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(240.dp)
                        ) {
                            items(items) { item ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedItem = item.title
                                            showSnackbar = true
                                        },
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = item.icon,
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = item.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // 4. LazyHorizontalGrid - 가로 그리드
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "4. LazyHorizontalGrid - 가로 그리드",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Text(
                            text = "가로 스크롤이 가능한 그리드 레이아웃",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        LazyHorizontalGrid(
                            rows = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(160.dp)
                        ) {
                            items(items.take(6)) { item ->
                                Card(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .clickable {
                                            selectedItem = item.title
                                            showSnackbar = true
                                        },
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = item.icon,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // 5. itemsIndexed - 인덱스와 함께 사용
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "5. itemsIndexed - 인덱스와 함께 사용",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Text(
                            text = "getItem(position)과 유사한 인덱스 기반 접근",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        LazyColumn(
                            modifier = Modifier.height(200.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(categories) { index, category ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedItem = "$index: $category"
                                            showSnackbar = true
                                        },
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    ListItem(
                                        headlineContent = { Text("$index: $category") },
                                        supportingContent = { 
                                            Text(
                                                text = "Index $index Category",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        leadingContent = { 
                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                                ),
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Box(
                                                    contentAlignment = Alignment.Center,
                                                    modifier = Modifier.fillMaxSize()
                                                ) {
                                                    Text(
                                                        text = "${index + 1}",
                                                        style = MaterialTheme.typography.titleMedium,
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // 6. 섹션 구분 (Sticky Header)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "6. 섹션 구분 (Sticky Header)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Text(
                            text = "카테고리별로 그룹화된 리스트 (Sticky Header와 유사)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        LazyColumn(
                            modifier = Modifier.height(300.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 과일 섹션
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Text(
                                        text = "🍎 과일 섹션",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                            
                            items(items.take(4)) { item ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedItem = item.title
                                            showSnackbar = true
                                        },
                                    elevation = CardDefaults.cardElevation(1.dp)
                                ) {
                                    ListItem(
                                        headlineContent = { Text(item.title) },
                                        supportingContent = { Text(item.description) },
                                        leadingContent = { 
                                            Text(
                                                text = item.icon,
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                        }
                                    )
                                }
                            }
                            
                            // 채소 섹션
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Text(
                                        text = "🥬 채소 섹션",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                            
                            items(listOf("상추", "양파", "당근", "토마토").take(3)) { vegetable ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedItem = vegetable
                                            showSnackbar = true
                                        },
                                    elevation = CardDefaults.cardElevation(1.dp)
                                ) {
                                    ListItem(
                                        headlineContent = { Text(vegetable) },
                                        supportingContent = { Text("신선한 $vegetable") },
                                        leadingContent = { 
                                            Text(
                                                text = "🥬",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // 7. 선택된 항목 표시
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "7. 선택된 항목",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        if (selectedItem != null) {
                            ListItem(
                                headlineContent = { Text("선택됨: $selectedItem") },
                                leadingContent = { 
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )
                        } else {
                            Text(
                                text = "항목을 클릭하여 선택하세요",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        
        // 스낵바 메시지
        if (showSnackbar) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                showSnackbar = false
            }
            
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { showSnackbar = false }) {
                        Text("확인")
                    }
                }
            ) {
                Text("선택됨: $selectedItem")
            }
        }
    }
}
