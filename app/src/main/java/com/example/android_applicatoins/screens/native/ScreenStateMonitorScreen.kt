package com.example.android_applicatoins.screens.native

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
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
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenStateMonitorScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    var isScreenOn by remember { mutableStateOf(true) }
    var screenOnTime by remember { mutableStateOf(0L) }
    var screenOffTime by remember { mutableStateOf(0L) }
    var totalUsageTime by remember { mutableStateOf(0L) }
    var todayUsageTime by remember { mutableStateOf(0L) }
    var screenStateHistory by remember { mutableStateOf(listOf<String>()) }
    var lastScreenChange by remember { mutableStateOf("") }
    var autoYouTubeEnabled by remember { mutableStateOf(true) }

    // 유튜브 링크 실행 함수
    fun openYouTubeLink() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/shorts/MurVXaWHh8U"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            
            val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val change = "자동 유튜브 실행 - $timestamp"
            screenStateHistory = screenStateHistory.take(5).toMutableList().apply { add(0, change) }
        } catch (e: Exception) {
            val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val change = "유튜브 실행 실패 - $timestamp"
            screenStateHistory = screenStateHistory.take(5).toMutableList().apply { add(0, change) }
        }
    }

    val screenReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_ON -> {
                        isScreenOn = true
                        screenOnTime = System.currentTimeMillis()
                        
                        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                        val change = "화면 켜짐 - $timestamp"
                        lastScreenChange = change
                        screenStateHistory = screenStateHistory.take(5).toMutableList().apply { add(0, change) }
                        
                        // 화면을 켤 때마다 유튜브 자동 실행 (개발모드)
                        if (autoYouTubeEnabled) {
                            openYouTubeLink()
                        }
                        
                        // 화면이 꺼져있던 시간 계산
                        if (screenOffTime > 0) {
                            val offDuration = screenOnTime - screenOffTime
                            totalUsageTime += offDuration
                            todayUsageTime += offDuration
                        }
                    }
                    Intent.ACTION_SCREEN_OFF -> {
                        isScreenOn = false
                        screenOffTime = System.currentTimeMillis()
                        
                        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                        val change = "화면 꺼짐 - $timestamp"
                        lastScreenChange = change
                        screenStateHistory = screenStateHistory.take(5).toMutableList().apply { add(0, change) }
                        
                        // 화면이 켜져있던 시간 계산
                        if (screenOnTime > 0) {
                            val onDuration = screenOffTime - screenOnTime
                            totalUsageTime += onDuration
                            todayUsageTime += onDuration
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(context) {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        context.registerReceiver(screenReceiver, filter)
        
        onDispose {
            context.unregisterReceiver(screenReceiver)
        }
    }

    // 실시간 사용 시간 업데이트
    LaunchedEffect(isScreenOn) {
        while (true) {
            delay(1000) // 1초마다 업데이트
            if (isScreenOn && screenOnTime > 0) {
                val currentTime = System.currentTimeMillis()
                val currentSessionTime = currentTime - screenOnTime
                totalUsageTime += 1000
                todayUsageTime += 1000
            }
        }
    }

    fun formatTime(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        
        return when {
            hours > 0 -> String.format("%d시간 %d분", hours, minutes % 60)
            minutes > 0 -> String.format("%d분 %d초", minutes, seconds % 60)
            else -> String.format("%d초", seconds)
        }
    }

    fun resetUsageTime() {
        totalUsageTime = 0L
        todayUsageTime = 0L
        screenOnTime = 0L
        screenOffTime = 0L
        
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val change = "사용 시간 초기화 - $timestamp"
        screenStateHistory = screenStateHistory.take(5).toMutableList().apply { add(0, change) }
    }

    fun simulateScreenToggle() {
        if (isScreenOn) {
            // 화면 끄기 시뮬레이션
            isScreenOn = false
            screenOffTime = System.currentTimeMillis()
            
            val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val change = "화면 꺼짐 (시뮬레이션) - $timestamp"
            lastScreenChange = change
            screenStateHistory = screenStateHistory.take(5).toMutableList().apply { add(0, change) }
            
            // 화면이 켜져있던 시간 계산
            if (screenOnTime > 0) {
                val onDuration = screenOffTime - screenOnTime
                totalUsageTime += onDuration
                todayUsageTime += onDuration
            }
            
            // 3초 후 자동으로 화면 켜기
            CoroutineScope(Dispatchers.Main).launch {
                delay(3000) // 3초 대기
                isScreenOn = true
                screenOnTime = System.currentTimeMillis()
                
                val timestamp2 = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                val change2 = "화면 켜짐 (시뮬레이션) - $timestamp2"
                lastScreenChange = change2
                screenStateHistory = screenStateHistory.take(5).toMutableList().apply { add(0, change2) }
                
                // 화면이 꺼져있던 시간 계산
                if (screenOffTime > 0) {
                    val offDuration = screenOnTime - screenOffTime
                    totalUsageTime += offDuration
                    todayUsageTime += offDuration
                }
                
                // 화면을 켤 때마다 유튜브 자동 실행 (개발모드)
                if (autoYouTubeEnabled) {
                    openYouTubeLink()
                }
            }
        } else {
            // 이미 꺼져있다면 즉시 켜기
            isScreenOn = true
            screenOnTime = System.currentTimeMillis()
            
            val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val change = "화면 켜짐 (시뮬레이션) - $timestamp"
            lastScreenChange = change
            screenStateHistory = screenStateHistory.take(5).toMutableList().apply { add(0, change) }
            
            // 화면이 꺼져있던 시간 계산
            if (screenOffTime > 0) {
                val offDuration = screenOnTime - screenOffTime
                totalUsageTime += offDuration
                todayUsageTime += offDuration
            }
            
            // 화면을 켤 때마다 유튜브 자동 실행 (개발모드)
            if (autoYouTubeEnabled) {
                openYouTubeLink()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("화면 상태 모니터", fontWeight = FontWeight.Bold) },
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
            // 화면 상태 아이콘
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(
                        if (isScreenOn) Color(0xFF4CAF50) else Color(0xFFF44336)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (isScreenOn) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "화면 상태",
                        modifier = Modifier.size(80.dp),
                        tint = Color.White
                    )
                    Text(
                        text = if (isScreenOn) "화면 ON" else "화면 OFF",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 현재 화면 상태
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isScreenOn) Color(0xFFE8F5E8) else Color(0xFFFFEBEE)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isScreenOn) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "화면 상태",
                        tint = if (isScreenOn) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isScreenOn) "화면이 켜져 있습니다" else "화면이 꺼져 있습니다",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isScreenOn) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 사용 시간 정보 카드들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 오늘 사용 시간
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
                            imageVector = Icons.Default.Today,
                            contentDescription = "오늘 사용 시간",
                            tint = Color.Blue
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "오늘",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = formatTime(todayUsageTime),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // 총 사용 시간
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
                            contentDescription = "총 사용 시간",
                            tint = Color.Green
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "총 사용",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = formatTime(totalUsageTime),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 제어 버튼들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { simulateScreenToggle() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "화면 상태 토글",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("화면 시뮬레이션")
                }
                
                Button(
                    onClick = { resetUsageTime() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9C27B0)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "사용 시간 초기화",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("초기화")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 자동 유튜브 실행 토글
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF8E1)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "자동 유튜브 실행",
                        tint = Color(0xFFFF9800)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "자동 유튜브 실행 (개발모드)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9800)
                        )
                        Text(
                            text = "화면을 켤 때마다 유튜브 링크 자동 실행",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = autoYouTubeEnabled,
                        onCheckedChange = { autoYouTubeEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFF9800),
                            checkedTrackColor = Color(0xFFFF9800).copy(alpha = 0.5f)
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 마지막 변경사항
            if (lastScreenChange.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "마지막 변경사항",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFFF9800)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = lastScreenChange,
                            fontSize = 14.sp,
                            color = Color(0xFFFF9800)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // 화면 상태 히스토리
            if (screenStateHistory.isNotEmpty()) {
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
                            text = "화면 상태 히스토리",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        screenStateHistory.forEach { history ->
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
                        text = "ACTION_SCREEN_ON과 ACTION_SCREEN_OFF 방송을 수신하여 화면 상태 변화를 실시간으로 감지합니다. 사용 시간을 추적하고 UX 실험에 활용할 수 있습니다.",
                        fontSize = 14.sp,
                        color = Color(0xFF1976D2)
                    )
                }
            }
        }
    }
}
