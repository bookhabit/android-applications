package com.example.android_applicatoins.screens.native

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.provider.MediaStore
import android.widget.VideoView
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// 오디오 품질 설정
enum class AudioQuality(val sampleRate: Int, val bitRate: Int, val displayName: String) {
    LOW(22050, 64000, "낮음 (22kHz, 64kbps)"),
    MEDIUM(44100, 128000, "보통 (44kHz, 128kbps)"),
    HIGH(48000, 192000, "높음 (48kHz, 192kbps)")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultimediaScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    
    // 상태 변수들
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var recordedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedAudioQuality by remember { mutableStateOf(AudioQuality.MEDIUM) }
    var recordedFiles by remember { mutableStateOf<List<File>>(emptyList()) }
    
    // 토글 상태들
    var showMediaPlayerInfo by remember { mutableStateOf(false) }
    var showMediaRecorderInfo by remember { mutableStateOf(false) }
    var showCameraInfo by remember { mutableStateOf(false) }
    var showVideoInfo by remember { mutableStateOf(false) }
    
    // 권한 관련
    val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
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
    
    // 로컬 오디오 파일 선택
    val audioFilePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { 
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(context, it)
                    setOnPreparedListener { mp ->
                        mp.start()
                        isPlaying = true
                    }
                    setOnCompletionListener {
                        isPlaying = false
                    }
                    prepareAsync()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    // 카메라 이미지 캡처
    val imageCaptureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            capturedImage = imageBitmap
        }
    }
    
    // 비디오 캡처
    val videoCaptureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val videoUri = data?.data
            recordedVideoUri = videoUri
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
    

    
    // 샘플 오디오 재생
    fun createMediaPlayer() {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource("https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3")
                setOnPreparedListener { mp ->
                    mp.start()
                    isPlaying = true
                }
                setOnCompletionListener {
                    isPlaying = false
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun playPauseMedia() {
        mediaPlayer?.let { player ->
            if (isPlaying) {
                player.pause()
                isPlaying = false
            } else {
                player.start()
                isPlaying = true
            }
        }
    }
    
    fun stopMedia() {
        mediaPlayer?.let { player ->
            player.stop()
            player.release()
            mediaPlayer = null
            isPlaying = false
        }
    }
    
    // 오디오 녹음
    fun startAudioRecording() {
        try {
            val outputFile = File(context.getExternalFilesDir(null), "audio_record_${System.currentTimeMillis()}.mp3")
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(selectedAudioQuality.sampleRate)
                setAudioEncodingBitRate(selectedAudioQuality.bitRate)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
                isRecording = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun stopAudioRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // 이미지 캡처
    fun captureImage() {
        checkAndRequestPermissions()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imageCaptureLauncher.launch(intent)
    }
    
    // 비디오 캡처
    fun captureVideo() {
        checkAndRequestPermissions()
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        videoCaptureLauncher.launch(intent)
    }
    
    // 컴포넌트 정리
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaRecorder?.release()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("멀티미디어 테스트") },
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
            // MediaPlayer 정보 섹션
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
                                text = "🎵 MediaPlayer (오디오/비디오 재생)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showMediaPlayerInfo = !showMediaPlayerInfo }) {
                                Icon(
                                    if (showMediaPlayerInfo) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "펼치기/접기"
                                )
                            }
                        }
                        
                        AnimatedVisibility(
                            visible = showMediaPlayerInfo,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "• 오디오 파일이나 동영상을 앱에서 재생할 수 있도록 제공하는 클래스\n" +
                                            "• 지원 포맷: MP3, AAC, WAV, MP4 등\n" +
                                            "• 사용 흐름: MediaPlayer 생성 → 데이터 소스 설정 → 준비 → 재생",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                
                                // 로컬 파일 선택 버튼
                                Button(
                                    onClick = { audioFilePicker.launch("audio/*") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Icon(Icons.Default.Folder, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("로컬 오디오 파일 선택")
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { createMediaPlayer() },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("샘플 재생")
                                    }
                                    
                                    Button(
                                        onClick = { playPauseMedia() },
                                        modifier = Modifier.weight(1f),
                                        enabled = mediaPlayer != null
                                    ) {
                                        Icon(
                                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = null
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isPlaying) "일시정지" else "재생")
                                    }
                                    
                                    Button(
                                        onClick = { stopMedia() },
                                        modifier = Modifier.weight(1f),
                                        enabled = mediaPlayer != null
                                    ) {
                                        Icon(Icons.Default.Stop, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("정지")
                                    }
                                }
                                
                                if (mediaPlayer != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "상태: ${if (isPlaying) "재생 중" else "일시정지"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isPlaying) Color.Green else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // MediaRecorder 정보 섹션
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
                                text = "🎤 MediaRecorder (오디오/비디오 녹음)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showMediaRecorderInfo = !showMediaRecorderInfo }) {
                                Icon(
                                    if (showMediaRecorderInfo) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "펼치기/접기"
                                )
                            }
                        }
                        
                        AnimatedVisibility(
                            visible = showMediaRecorderInfo,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "• 디바이스 마이크나 카메라를 통해 오디오/비디오를 녹음하는 클래스\n" +
                                            "• 사용 흐름: MediaRecorder 생성 → 소스 지정 → 포맷/인코더 설정 → 출력 파일 지정 → 준비 → 녹음 시작",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                
                                // 오디오 품질 설정
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = "⚙️ 오디오 품질 설정",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        AudioQuality.values().forEach { quality ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = selectedAudioQuality == quality,
                                                    onClick = { selectedAudioQuality = quality }
                                                )
                                                Text(
                                                    text = quality.displayName,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                // 오디오 녹음 컨트롤
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = "🎤 오디오 녹음 테스트",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        
                                        Spacer(modifier = Modifier.height(12.dp))
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Button(
                                                onClick = { startAudioRecording() },
                                                enabled = !isRecording
                                            ) {
                                                Icon(Icons.Default.Mic, contentDescription = null)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("녹음 시작")
                                            }
                                            
                                            Button(
                                                onClick = { stopAudioRecording() },
                                                enabled = isRecording
                                            ) {
                                                Icon(Icons.Default.Stop, contentDescription = null)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("녹음 정지")
                                            }
                                        }
                                        
                                        if (isRecording) {
                                            Spacer(modifier = Modifier.height(8.dp))
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
                                                    text = "녹음 중... (${selectedAudioQuality.displayName})",
                                                    color = Color.Red,
                                                    fontWeight = FontWeight.Bold
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
            
            // 카메라 정보 섹션
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
                                text = "📷 카메라 & 이미지 캡처",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showCameraInfo = !showCameraInfo }) {
                                Icon(
                                    if (showCameraInfo) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "펼치기/접기"
                                )
                            }
                        }
                        
                        AnimatedVisibility(
                            visible = showCameraInfo,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "• Camera 앱 호출 → Intent 사용 (ACTION_IMAGE_CAPTURE)\n" +
                                            "• 결과는 onActivityResult 또는 ActivityResultLauncher로 받음\n" +
                                            "• 간단한 이미지 캡처는 카메라 앱을 호출하는 것이 가장 쉬움",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                
                                Button(
                                    onClick = { captureImage() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("사진 촬영")
                                }
                                
                                // 캡처된 이미지 표시
                                capturedImage?.let { bitmap ->
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "캡처된 이미지:",
                                        fontWeight = FontWeight.Medium
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                            .background(Color.LightGray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "이미지 크기: ${bitmap.width} x ${bitmap.height}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // 비디오 정보 섹션
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
                                text = "🎬 비디오 재생 & 녹화",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showVideoInfo = !showVideoInfo }) {
                                Icon(
                                    if (showVideoInfo) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "펼치기/접기"
                                )
                            }
                        }
                        
                        AnimatedVisibility(
                            visible = showVideoInfo,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "• VideoView + MediaController 조합으로 비디오 재생\n" +
                                            "• 비디오 녹화는 ACTION_VIDEO_CAPTURE 인텐트로 간단하게 가능\n" +
                                            "• 고급 기능은 Camera + MediaRecorder 조합 사용",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                
                                Button(
                                    onClick = { captureVideo() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Videocam, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("비디오 녹화")
                                }
                                
                                // 녹화된 비디오 표시
                                recordedVideoUri?.let { uri ->
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "녹화된 비디오:",
                                        fontWeight = FontWeight.Medium
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        AndroidView(
                                            factory = { context ->
                                                VideoView(context).apply {
                                                    setVideoURI(uri)
                                                    setMediaController(android.widget.MediaController(context))
                                                }
                                            },
                                            modifier = Modifier.fillMaxSize()
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
