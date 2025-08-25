package com.example.android_applicatoins.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopwatchScreen(
    onBackPressed: () -> Unit
) {
    var time by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var laps by remember { mutableStateOf(listOf<Long>()) }
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (isRunning) {
                delay(10) // 10ms마다 업데이트
                time += 10
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("스톱워치") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 시간 디스플레이
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = formatTime(time),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "밀리초: ${(time % 1000) / 10}",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 컨트롤 버튼들
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 시작/정지 버튼
                Button(
                    onClick = {
                        isRunning = !isRunning
                    },
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) 
                            MaterialTheme.colorScheme.error 
                        else 
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "정지" else "시작",
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // 랩 버튼
                Button(
                    onClick = {
                        if (isRunning) {
                            laps = laps + time
                        }
                    },
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    enabled = isRunning,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "랩",
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // 리셋 버튼
                Button(
                    onClick = {
                        time = 0L
                        laps = emptyList()
                        isRunning = false
                    },
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "리셋",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 랩 기록
            if (laps.isNotEmpty()) {
                Text(
                    text = "랩 기록",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(laps.withIndex().toList()) { (index, lapTime) ->
                        LapItem(
                            lapNumber = index + 1,
                            lapTime = lapTime,
                            totalTime = lapTime
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LapItem(
    lapNumber: Int,
    lapTime: Long,
    totalTime: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "랩 $lapNumber",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatTime(lapTime),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "총: ${formatTime(totalTime)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun formatTime(timeInMillis: Long): String {
    val hours = timeInMillis / 3600000
    val minutes = (timeInMillis % 3600000) / 60000
    val seconds = (timeInMillis % 60000) / 1000
    
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
