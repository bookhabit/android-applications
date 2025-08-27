package com.example.android_applicatoins.screens.native

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.core.content.ContextCompat
import com.example.android_applicatoins.services.ScreenRecordingService
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenRecordingScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current

    // 화면 녹화 관련 상태 변수들
    var isScreenRecording by remember { mutableStateOf(false) }
    var screenRecordingFile by remember { mutableStateOf<File?>(null) }
    var showRecordingInfo by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableStateOf(0L) }
    var recordingStartTime by remember { mutableStateOf(0L) }
    
    // 서비스 연결 관련
    var screenRecordingService by remember { mutableStateOf<ScreenRecordingService?>(null) }
    var isServiceBound by remember { mutableStateOf(false) }
    
    // 서비스 연결
    val serviceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as ScreenRecordingService.ScreenRecordingBinder
                screenRecordingService = binder.getService()
                isServiceBound = true
            }
            
            override fun onServiceDisconnected(name: ComponentName?) {
                screenRecordingService = null
                isServiceBound = false
            }
        }
    }

    // 녹화 설정
    var videoQuality by remember { mutableStateOf("HD (720p)") }
    var frameRate by remember { mutableStateOf(30) }
    var bitRate by remember { mutableStateOf(5000000) } // 5Mbps

    // 권한 관련
    val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            // 권한이 모두 허용됨
        }
    }

    // 녹화 시간 포맷팅
    fun formatDuration(duration: Long): String {
        val seconds = (duration / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    // 비디오를 갤러리에 저장
    fun saveVideoToGallery(videoFile: File) {
        try {
            val contentValues = android.content.ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, videoFile.name)
                put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let { videoUri ->
                resolver.openOutputStream(videoUri)?.use { outputStream ->
                    videoFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 화면 녹화 정지 (서비스 사용)
    fun stopScreenRecording() {
        try {
            // 서비스 중지
            val serviceIntent = Intent(context, ScreenRecordingService::class.java).apply {
                action = ScreenRecordingService.ACTION_STOP_RECORDING
            }
            context.stopService(serviceIntent)
            
            // 상태 초기화
            isScreenRecording = false
            recordingDuration = 0L
            recordingStartTime = 0L

            // 갤러리에 저장 (서비스에서 생성된 파일 사용)
            screenRecordingFile?.let { file ->
                if (file.exists()) {
                    saveVideoToGallery(file)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 화면 녹화 실제 시작 (서비스 사용)
    fun startScreenRecording(data: Intent?) {
        if (data == null) return

        try {
            // 서비스 시작
            val serviceIntent = Intent(context, ScreenRecordingService::class.java).apply {
                action = ScreenRecordingService.ACTION_START_RECORDING
                putExtra(ScreenRecordingService.EXTRA_RESULT_CODE, Activity.RESULT_OK)
                putExtra(ScreenRecordingService.EXTRA_RESULT_DATA, data)
                putExtra(ScreenRecordingService.EXTRA_VIDEO_QUALITY, videoQuality)
                putExtra(ScreenRecordingService.EXTRA_FRAME_RATE, frameRate)
                putExtra(ScreenRecordingService.EXTRA_BIT_RATE, bitRate)
            }
            
            context.startForegroundService(serviceIntent)
            
            // 녹화 상태 업데이트
            isScreenRecording = true
            recordingStartTime = System.currentTimeMillis()
            
            // 파일명 미리 설정 (타임스탬프 기반)
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val outputFile = File(context.getExternalFilesDir(null), "screen_record_$timestamp.mp4")
            screenRecordingFile = outputFile

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 권한 체크 및 요청
    fun checkAndRequestPermissions() {
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest)
        }
    }

    // 화면 녹화 권한 요청
    val screenCapturePermission = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            startScreenRecording(result.data)
        }
    }

    // 화면 녹화 시작
    fun startScreenRecording() {
        checkAndRequestPermissions()
        val mediaProjectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        screenCapturePermission.launch(mediaProjectionManager.createScreenCaptureIntent())
    }

    // 녹화 시간 업데이트
    LaunchedEffect(isScreenRecording) {
        while (isScreenRecording) {
            recordingDuration = System.currentTimeMillis() - recordingStartTime
            kotlinx.coroutines.delay(1000) // 1초마다 업데이트
        }
    }
    
    // 컴포넌트 정리
    DisposableEffect(Unit) {
        onDispose {
            // 녹화 중이면 서비스 중지
            if (isScreenRecording) {
                val serviceIntent = Intent(context, ScreenRecordingService::class.java).apply {
                    action = ScreenRecordingService.ACTION_STOP_RECORDING
                }
                context.stopService(serviceIntent)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📱 화면 녹화") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 화면 녹화 정보 섹션
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📚 화면 녹화 정보",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showRecordingInfo = !showRecordingInfo }) {
                                Icon(
                                    if (showRecordingInfo) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "펼치기/접기"
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = showRecordingInfo,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "• Android 5.0(API 21) 이상에서 MediaProjection API를 사용한 화면 녹화\n" +
                                            "• 권한: RECORD_AUDIO, WRITE_EXTERNAL_STORAGE 필요\n" +
                                            "• 시스템 UI와 앱 화면을 모두 녹화 가능\n" +
                                            "• 녹화된 파일은 갤러리(Movies 폴더)에 자동 저장\n" +
                                            "• 녹화 중에는 상태바에 알림이 표시됩니다",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Divider(modifier = Modifier.padding(vertical = 8.dp))

                                Text(
                                    text = "⚠️ 주의사항:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF9800)
                                )

                                Text(
                                    text = "• 화면 녹화는 시스템 권한이 필요합니다\n" +
                                            "• 녹화 중에는 배터리 소모가 증가할 수 있습니다\n" +
                                            "• 녹화 파일 크기는 화질과 시간에 따라 달라집니다",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFFF9800)
                                )
                            }
                        }
                    }
                }
            }

            // 녹화 설정 섹션
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚙️ 녹화 설정",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showSettings = !showSettings }) {
                                Icon(
                                    if (showSettings) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "펼치기/접기"
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = showSettings,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // 화질 설정
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = "🎥 화질 설정",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        listOf("HD (720p)", "Full HD (1080p)", "4K (2160p)").forEach { quality ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = videoQuality == quality,
                                                    onClick = { videoQuality = quality }
                                                )
                                                Text(
                                                    text = quality,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    }
                                }

                                // 프레임 레이트 설정
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = "🎬 프레임 레이트 설정",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        listOf(24, 30, 60).forEach { fps ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = frameRate == fps,
                                                    onClick = { frameRate = fps }
                                                )
                                                Text(
                                                    text = "$fps FPS",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    }
                                }

                                // 비트레이트 설정
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = "📊 비트레이트 설정",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        listOf(
                                            Triple(2000000, "2 Mbps", "낮은 품질, 작은 파일"),
                                            Triple(5000000, "5 Mbps", "보통 품질, 적당한 파일"),
                                            Triple(10000000, "10 Mbps", "높은 품질, 큰 파일")
                                        ).forEach { (rate, label, description) ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = bitRate == rate,
                                                    onClick = { bitRate = rate }
                                                )
                                                Column {
                                                    Text(
                                                        text = label,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                    Text(
                                                        text = description,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 녹화 컨트롤 섹션
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "🎬 화면 녹화 컨트롤",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 녹화 버튼들
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { startScreenRecording() },
                                enabled = !isScreenRecording,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(Icons.Default.ScreenShare, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("녹화 시작")
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Button(
                                onClick = { stopScreenRecording() },
                                enabled = isScreenRecording,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red
                                )
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("녹화 정지")
                            }
                        }

                        // 녹화 상태 표시
                        if (isScreenRecording) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(Color.Red)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "화면 녹화 중...",
                                            color = Color.Red,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "녹화 시간: ${formatDuration(recordingDuration)}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    Text(
                                        text = "화질: $videoQuality, ${frameRate}FPS, ${bitRate / 1000000}Mbps",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // 녹화 파일 정보
                        screenRecordingFile?.let { file ->
                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.Green.copy(alpha = 0.1f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color.Green
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "녹화 완료",
                                            color = Color.Green,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "파일명: ${file.name}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    Text(
                                        text = "파일 크기: ${(file.length() / 1024 / 1024)} MB",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        text = "갤러리(Movies 폴더)에 저장됨",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Green
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
