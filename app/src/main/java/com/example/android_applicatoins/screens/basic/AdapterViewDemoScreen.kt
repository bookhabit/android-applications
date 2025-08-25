package com.example.android_applicatoins.screens.basic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdapterViewDemoScreen(
    onBackPressed: () -> Unit
) {
    var selectedItem by remember { mutableStateOf<String?>(null) }
    var showToast by remember { mutableStateOf(false) }
    
    // 샘플 데이터 (ArrayAdapter에서 사용할 데이터)
    val fruits = listOf("사과", "바나나", "포도", "수박", "오렌지", "키위", "망고", "파인애플")
    val colors = listOf("빨강", "파랑", "초록", "노랑", "보라", "주황", "분홍", "검정")
    val numbers = (1..20).map { "항목 $it" }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AdapterView 데모") },
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
            // 1. 기본 리스트 (ArrayAdapter와 유사)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "1. 기본 리스트 (ArrayAdapter와 유사)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Text(
                            text = "ListView + ArrayAdapter의 개념을 Compose로 구현",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        fruits.forEach { fruit ->
                            ListItem(
                                headlineContent = { Text(fruit) },
                                leadingContent = { 
                                    Icon(
                                        Icons.Default.Circle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                modifier = Modifier.clickable {
                                    selectedItem = fruit
                                    showToast = true
                                }
                            )
                            Divider()
                        }
                    }
                }
            }
            
            // 2. 인덱스가 있는 리스트
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "2. 인덱스가 있는 리스트",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Text(
                            text = "getItem(position)과 유사한 기능",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        colors.take(6).forEachIndexed { index, color ->
                            ListItem(
                                headlineContent = { Text("$index: $color") },
                                leadingContent = { 
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer
                                        ),
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = "$index",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.clickable {
                                    selectedItem = "$index: $color"
                                    showToast = true
                                }
                            )
                            if (index < 5) Divider()
                        }
                    }
                }
            }
            
            // 3. 긴 리스트 (getCount()와 유사)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "3. 긴 리스트 (getCount()와 유사)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Text(
                            text = "총 ${numbers.size}개 항목 (getItemCount()와 유사)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        numbers.take(10).forEach { number ->
                            ListItem(
                                headlineContent = { Text(number) },
                                leadingContent = { 
                                    Icon(
                                        Icons.Default.NavigateNext,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                modifier = Modifier.clickable {
                                    selectedItem = number
                                    showToast = true
                                }
                            )
                            Divider()
                        }
                        
                        if (numbers.size > 10) {
                            Text(
                                text = "... 그리고 ${numbers.size - 10}개 더",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
            
            // 4. 선택된 항목 표시
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "4. 선택된 항목",
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
        
        // 토스트 메시지
        if (showToast) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                showToast = false
            }
            
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { showToast = false }) {
                        Text("확인")
                    }
                }
            ) {
                Text("선택됨: $selectedItem")
            }
        }
    }
}
