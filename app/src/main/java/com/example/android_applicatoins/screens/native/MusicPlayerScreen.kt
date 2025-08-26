package com.example.android_applicatoins.screens.native

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
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
import com.example.android_applicatoins.services.MusicService
import kotlinx.coroutines.delay
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    var musicService: MusicService? by remember { mutableStateOf(null) }
    var bound by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }

    val connection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                val binder = service as MusicService.LocalBinder
                musicService = binder.getService()
                bound = true
                
                // 초기 상태 설정
                isPlaying = musicService?.isPlaying() == true
                duration = musicService?.getDuration() ?: 0
                currentPosition = musicService?.getCurrentPosition() ?: 0
            }

            override fun onServiceDisconnected(arg0: ComponentName) {
                bound = false
            }
        }
    }

    DisposableEffect(context) {
        val intent = Intent(context, MusicService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        
        onDispose {
            if (bound) {
                context.unbindService(connection)
                bound = false
            }
        }
    }

    LaunchedEffect(context) {
        createNotificationChannel(context)
    }

    // 재생 시간 업데이트를 위한 타이머
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(1000) // 1초마다 업데이트
            if (bound) {
                currentPosition = musicService?.getCurrentPosition() ?: 0
                duration = musicService?.getDuration() ?: 0
            }
        }
    }

    fun playPauseMusic() {
        if (bound) {
            if (musicService?.isPlaying() == true) {
                musicService?.pause()
                isPlaying = false
            } else {
                musicService?.play()
                isPlaying = true
            }
        }
    }

    fun stopMusic() {
        if (bound) {
            musicService?.stop()
            isPlaying = false
            currentPosition = 0
        }
    }

    fun seekTo(position: Int) {
        if (bound) {
            musicService?.seekTo(position)
            currentPosition = position
        }
    }

    fun formatTime(milliseconds: Int): String {
        val totalSeconds = floor(milliseconds / 1000.0).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("음악 플레이어", fontWeight = FontWeight.Bold) },
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
            // 앨범 아트 영역
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "앨범 아트",
                    modifier = Modifier.size(120.dp),
                    tint = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 곡 정보
            Text(
                text = "Retro Lounge",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Chill Vibes",
                fontSize = 16.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 진행바
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Slider(
                    value = currentPosition.toFloat(),
                    onValueChange = { seekTo(it.toInt()) },
                    valueRange = 0f..duration.toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(currentPosition),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = formatTime(duration),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 재생 컨트롤
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 이전 곡 버튼
                IconButton(
                    onClick = { /* 이전 곡 기능 */ },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5))
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "이전 곡",
                        modifier = Modifier.size(28.dp),
                        tint = Color.Black
                    )
                }
                
                // 재생/일시정지 버튼
                IconButton(
                    onClick = { playPauseMusic() },
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2196F3))
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "일시정지" else "재생",
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                }
                
                // 다음 곡 버튼
                IconButton(
                    onClick = { /* 다음 곡 기능 */ },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5))
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "다음 곡",
                        modifier = Modifier.size(28.dp),
                        tint = Color.Black
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 정지 버튼
            Button(
                onClick = { stopMusic() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF44336)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "정지",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("정지")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 서비스 정보
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
                        text = "서비스 정보",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "이 앱은 안드로이드 Service를 사용하여 백그라운드에서 음악을 재생합니다. 앱을 닫아도 음악이 계속 재생됩니다.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "현재 재생 중: ${if (isPlaying) "예" else "아니오"}",
                        fontSize = 14.sp,
                        color = if (isPlaying) Color(0xFF4CAF50) else Color.Gray
                    )
                }
            }
        }
    }
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Music Player"
        val descriptionText = "음악 재생 알림"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel("music_player", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
