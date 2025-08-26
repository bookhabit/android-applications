package com.example.android_applicatoins.screens.native

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_applicatoins.services.BootService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BootReceiverScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    var isServiceRunning by remember { mutableStateOf(false) }
    var lastBootTime by remember { mutableStateOf("") }
    var serviceStartCount by remember { mutableStateOf(0) }
    var bootHistory by remember { mutableStateOf(listOf<String>()) }

    val bootReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_BOOT_COMPLETED -> {
                        val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                        lastBootTime = timestamp
                        bootHistory = bootHistory.take(5).toMutableList().apply { add(0, "부팅 완료 - $timestamp") }
                        
                        // 부팅 완료 후 서비스 시작
                        val serviceIntent = Intent(context, BootService::class.java)
                        context?.startService(serviceIntent)
                    }
                }
            }
        }
    }

    DisposableEffect(context) {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BOOT_COMPLETED)
        }
        context.registerReceiver(bootReceiver, filter)
        
        onDispose {
            context.unregisterReceiver(bootReceiver)
        }
    }

    fun startBootService() {
        val intent = Intent(context, BootService::class.java)
        context.startService(intent)
        isServiceRunning = true
        serviceStartCount++
        
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        bootHistory = bootHistory.take(5).toMutableList().apply { add(0, "서비스 시작 - $timestamp") }
    }

    fun stopBootService() {
        val intent = Intent(context, BootService::class.java)
        context.stopService(intent)
        isServiceRunning = false
        
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        bootHistory = bootHistory.take(5).toMutableList().apply { add(0, "서비스 중지 - $timestamp") }
    }

    fun simulateBoot() {
        val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        lastBootTime = timestamp
        bootHistory = bootHistory.take(5).toMutableList().apply { add(0, "시뮬레이션 부팅 - $timestamp") }
        
        // 시뮬레이션 부팅 후 서비스 시작
        startBootService()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("부팅 자동 실행", fontWeight = FontWeight.Bold) },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 부팅 아이콘
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isServiceRunning -> Color(0xFF4CAF50) // 초록색 (실행 중)
                            else -> Color(0xFF2196F3) // 파란색 (대기 중)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (isServiceRunning) Icons.Default.PlayArrow else Icons.Default.Power,
                        contentDescription = "부팅",
                        modifier = Modifier.size(80.dp),
                        tint = Color.White
                    )
                    Text(
                        text = if (isServiceRunning) "실행 중" else "대기 중",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 서비스 상태
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isServiceRunning) Color(0xFFE8F5E8) else Color(0xFFF5F5F5)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isServiceRunning) Icons.Default.CheckCircle else Icons.Default.Schedule,
                        contentDescription = "서비스 상태",
                        tint = if (isServiceRunning) Color(0xFF4CAF50) else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isServiceRunning) "부팅 서비스 실행 중" else "부팅 서비스 대기 중",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isServiceRunning) Color(0xFF4CAF50) else Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 서비스 정보 카드들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 서비스 시작 횟수
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "시작 횟수",
                            tint = Color.Blue
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "시작 횟수",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "$serviceStartCount",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // 마지막 부팅 시간
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "마지막 부팅",
                            tint = Color.Green
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "마지막 부팅",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = if (lastBootTime.isNotEmpty()) "있음" else "없음",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 서비스 제어 버튼들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { startBootService() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "서비스 시작",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("서비스 시작")
                }
                
                Button(
                    onClick = { stopBootService() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "서비스 중지",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("서비스 중지")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 부팅 시뮬레이션 버튼
            Button(
                onClick = { simulateBoot() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "부팅 시뮬레이션",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("부팅 시뮬레이션")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 부팅 히스토리
            if (bootHistory.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "부팅 히스토리",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        bootHistory.forEach { history ->
                            Text(
                                text = "• $history",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // 방송수신자 정보
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "방송수신자 정보",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1976D2)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ACTION_BOOT_COMPLETED 방송을 수신하여 기기 부팅 완료 후 자동으로 서비스를 시작합니다. 앱이 실행되지 않아도 백그라운드에서 동작할 수 있습니다.",
                        fontSize = 14.sp,
                        color = Color(0xFF1976D2)
                    )
                }
            }
        }
    }
}
