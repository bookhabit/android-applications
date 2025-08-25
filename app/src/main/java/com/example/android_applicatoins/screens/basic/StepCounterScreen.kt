package com.example.android_applicatoins.screens.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepCounterScreen(
    onBackPressed: () -> Unit
) {
    var stepCount by remember { mutableStateOf(0) }
    var dailyGoal by remember { mutableStateOf(10000) }
    var isTracking by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }
    
    // 시뮬레이션된 걸음 수 증가
    LaunchedEffect(isTracking) {
        while (isTracking) {
            delay(2000) // 2초마다 걸음 수 증가
            stepCount += (1..5).random() // 1-5 걸음 랜덤 증가
        }
    }

    val progress = (stepCount.toFloat() / dailyGoal).coerceAtMost(1f)
    val calories = (stepCount * 0.04).roundToInt() // 대략적인 칼로리 계산
    val distance = (stepCount * 0.0008).roundToInt() // 대략적인 거리 계산 (km)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("스텝 카운터") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 메인 걸음 수 표시
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "오늘의 걸음",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = stepCount.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Text(
                        text = "걸음",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // 진행률 표시
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "일일 목표 진행률",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 원형 진행률 바
                    Box(
                        modifier = Modifier.size(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = progress,
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 12.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Text(
                            text = "${(progress * 100).roundToInt()}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "$stepCount / $dailyGoal 걸음",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // 통계 정보
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "오늘의 통계",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            icon = Icons.Default.LocalFireDepartment,
                            value = "$calories",
                            unit = "kcal",
                            label = "소모 칼로리"
                        )
                        
                        StatItem(
                            icon = Icons.Default.Place,
                            value = "$distance",
                            unit = "km",
                            label = "이동 거리"
                        )
                    }
                }
            }

            // 컨트롤 버튼들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { isTracking = !isTracking },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTracking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        if (isTracking) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isTracking) "정지" else "시작"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isTracking) "정지" else "시작")
                }
                
                Button(
                    onClick = { stepCount = 0 }
                ) {
                    Icon(Icons.Default.Refresh, "초기화")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("초기화")
                }
            }

            // 추가 옵션들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { showGoalDialog = true }
                ) {
                    Icon(Icons.Default.Edit, "목표 설정")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("목표 설정")
                }
                
                OutlinedButton(
                    onClick = { showStats = true }
                ) {
                    Icon(Icons.Default.Analytics, "상세 통계")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("상세 통계")
                }
            }
        }
    }

    // 목표 설정 다이얼로그
    if (showGoalDialog) {
        var newGoal by remember { mutableStateOf(dailyGoal.toString()) }
        
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text("일일 목표 설정") },
            text = {
                OutlinedTextField(
                    value = newGoal,
                    onValueChange = { newGoal = it },
                    label = { Text("목표 걸음 수") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        newGoal.toIntOrNull()?.let { goal ->
                            if (goal > 0) {
                                dailyGoal = goal
                            }
                        }
                        showGoalDialog = false
                    }
                ) {
                    Text("설정")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    // 상세 통계 다이얼로그
    if (showStats) {
        AlertDialog(
            onDismissRequest = { showStats = false },
            title = { Text("상세 통계") },
            text = {
                Column {
                    StatRow("총 걸음 수", "$stepCount 걸음")
                    StatRow("일일 목표", "$dailyGoal 걸음")
                    StatRow("달성률", "${(progress * 100).roundToInt()}%")
                    StatRow("소모 칼로리", "$calories kcal")
                    StatRow("이동 거리", "$distance km")
                    StatRow("예상 시간", "${(stepCount * 0.5).roundToInt()} 분")
                }
            },
            confirmButton = {
                TextButton(onClick = { showStats = false }) {
                    Text("닫기")
                }
            }
        )
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    unit: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = label,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = unit,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StatRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold
        )
    }
}
