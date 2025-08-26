package com.example.android_applicatoins.screens.native

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

data class DownloadItem(
    val id: String,
    val name: String,
    val url: String,
    val type: String, // "image" or "file"
    val status: String, // "pending", "downloading", "completed", "failed"
    val localPath: String = "",
    val size: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileDownloadAppScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var downloadItems by remember { mutableStateOf(listOf<DownloadItem>()) }
    var hasStoragePermission by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newDownloadUrl by remember { mutableStateOf("") }
    var newDownloadType by remember { mutableStateOf("image") }

    fun saveImageToMediaStore(context: android.content.Context, bitmap: Bitmap, fileName: String): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_${timeStamp}_${fileName}"
        
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }
        
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let { imageUri ->
            context.contentResolver.openOutputStream(imageUri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            return imageUri.toString()
        }
        
        return ""
    }

    suspend fun downloadFile(context: android.content.Context, item: DownloadItem): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(item.url)
                val connection = url.openConnection()
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                
                val inputStream = connection.getInputStream()
                
                if (item.type == "image") {
                    // 이미지 다운로드 - MediaStore 사용
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    if (bitmap != null) {
                        val imageUri = saveImageToMediaStore(context, bitmap, item.name)
                        inputStream.close()
                        Result.success(imageUri)
                    } else {
                        inputStream.close()
                        Result.failure(IOException("이미지를 디코딩할 수 없습니다"))
                    }
                } else {
                    // 파일 다운로드 - Downloads 폴더에 저장
                    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val file = File(downloadsDir, item.name)
                    
                    val outputStream = FileOutputStream(file)
                    inputStream.copyTo(outputStream)
                    outputStream.close()
                    inputStream.close()
                    
                    Result.success(file.absolutePath)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun startDownload(item: DownloadItem) {
        scope.launch {
            try {
                // 다운로드 상태 업데이트
                downloadItems = downloadItems.map { 
                    if (it.id == item.id) it.copy(status = "downloading") else it 
                }
                
                val result = withContext(Dispatchers.IO) {
                    downloadFile(context, item)
                }
                
                if (result.isSuccess) {
                    val localPath = result.getOrNull() ?: ""
                    downloadItems = downloadItems.map { 
                        if (it.id == item.id) it.copy(status = "completed", localPath = localPath) else it 
                    }
                } else {
                    downloadItems = downloadItems.map { 
                        if (it.id == item.id) it.copy(status = "failed") else it 
                    }
                }
            } catch (e: Exception) {
                downloadItems = downloadItems.map { 
                    if (it.id == item.id) it.copy(status = "failed") else it 
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val writePermission = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false
        val readPermission = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
        
        hasStoragePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true // Android 10 이상에서는 Scoped Storage 사용
        } else {
            writePermission && readPermission
        }
    }

    fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            hasStoragePermission = true
        } else {
            val writePermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            
            val readPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            
            hasStoragePermission = writePermission && readPermission
            
            if (!hasStoragePermission) {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }

    fun addDownloadItem() {
        if (newDownloadUrl.isNotEmpty()) {
            val id = UUID.randomUUID().toString()
            val name = newDownloadUrl.substringAfterLast("/").substringBefore("?")
            
            val newItem = DownloadItem(
                id = id,
                name = name,
                url = newDownloadUrl,
                type = newDownloadType,
                status = "pending"
            )
            
            downloadItems = downloadItems + newItem
            newDownloadUrl = ""
            showAddDialog = false
            
            // 다운로드 시작
            startDownload(newItem)
        }
    }

    fun removeDownloadItem(id: String) {
        downloadItems = downloadItems.filter { it.id != id }
    }

    DisposableEffect(context) {
        checkPermissions()
        onDispose { }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("파일 다운로드 앱", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "뒤로가기")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "다운로드 추가")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            // 권한 상태
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (hasStoragePermission) Color(0xFFE8F5E8) else Color(0xFFFFEBEE)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (hasStoragePermission) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = "권한 상태",
                        tint = if (hasStoragePermission) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (hasStoragePermission) "저장소 권한 허용됨" else "저장소 권한 필요",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasStoragePermission) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                        if (!hasStoragePermission) {
                            Text(
                                text = "파일을 다운로드하고 저장할 수 있도록 권한을 허용해주세요",
                                fontSize = 14.sp,
                                color = Color(0xFFF44336)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 다운로드 목록
            if (downloadItems.isNotEmpty()) {
                Text(
                    text = "다운로드 목록 (${downloadItems.size}개)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(downloadItems) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = when (item.status) {
                                    "completed" -> Color(0xFFE8F5E8)
                                    "failed" -> Color(0xFFFFEBEE)
                                    "downloading" -> Color(0xFFFFF3E0)
                                    else -> Color(0xFFF5F5F5)
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = when (item.type) {
                                                "image" -> Icons.Default.Image
                                                else -> Icons.Default.InsertDriveFile
                                            },
                                            contentDescription = "파일 타입",
                                            tint = when (item.type) {
                                                "image" -> Color(0xFF2196F3)
                                                else -> Color(0xFF4CAF50)
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = item.name,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = item.url,
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                    
                                    IconButton(onClick = { removeDownloadItem(item.id) }) {
                                        Icon(Icons.Default.Delete, "삭제")
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // 상태 표시
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (item.status) {
                                            "completed" -> Icons.Default.CheckCircle
                                            "failed" -> Icons.Default.Cancel
                                            "downloading" -> Icons.Default.Download
                                            else -> Icons.Default.Schedule
                                        },
                                        contentDescription = "상태",
                                        tint = when (item.status) {
                                            "completed" -> Color(0xFF4CAF50)
                                            "failed" -> Color(0xFFF44336)
                                            "downloading" -> Color(0xFFFF9800)
                                            else -> Color.Gray
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = when (item.status) {
                                            "completed" -> "완료됨"
                                            "failed" -> "실패함"
                                            "downloading" -> "다운로드 중..."
                                            else -> "대기 중"
                                        },
                                        fontSize = 14.sp,
                                        color = when (item.status) {
                                            "completed" -> Color(0xFF4CAF50)
                                            "failed" -> Color(0xFFF44336)
                                            "downloading" -> Color(0xFFFF9800)
                                            else -> Color.Gray
                                        }
                                    )
                                }
                                
                                if (item.status == "completed" && item.localPath.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "저장 위치: ${item.localPath}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // 다운로드 항목이 없을 때
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "다운로드 없음",
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "다운로드 항목이 없습니다",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "새로운 다운로드를 추가해보세요",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Content Provider 정보
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
                        text = "Content Provider 정보",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1976D2)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "MediaStore를 사용하여 이미지를 갤러리에 저장하고, Downloads 폴더에 파일을 저장합니다. Scoped Storage를 지원하여 Android 10 이상에서도 안전하게 동작합니다.",
                        fontSize = 14.sp,
                        color = Color(0xFF1976D2)
                    )
                }
            }
        }
    }

    // 다운로드 추가 다이얼로그
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("새 다운로드 추가") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newDownloadUrl,
                        onValueChange = { newDownloadUrl = it },
                        label = { Text("URL") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("https://example.com/image.jpg") }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 예시 URL들
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF3F4F6)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "💡 테스트용 URL 예시",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B7280)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // 이미지 예시
                            Text(
                                text = "이미지:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B7280)
                            )
                            Text(
                                text = "• https://picsum.photos/400/300",
                                fontSize = 10.sp,
                                color = Color(0xFF6B7280)
                            )
                            Text(
                                text = "• https://via.placeholder.com/400x300",
                                fontSize = 10.sp,
                                color = Color(0xFF6B7280)
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // 파일 예시
                            Text(
                                text = "파일:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B7280)
                            )
                            Text(
                                text = "• https://httpbin.org/robots.txt",
                                fontSize = 10.sp,
                                color = Color(0xFF6B7280)
                            )
                            Text(
                                text = "• https://httpbin.org/json",
                                fontSize = 10.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "파일 타입",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row {
                        RadioButton(
                            selected = newDownloadType == "image",
                            onClick = { newDownloadType = "image" }
                        )
                        Text("이미지 (갤러리에 저장)")
                    }
                    Row {
                        RadioButton(
                            selected = newDownloadType == "file",
                            onClick = { newDownloadType = "file" }
                        )
                        Text("파일 (Downloads 폴더에 저장)")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 사용법 안내
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F5E8)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "📋 사용법",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "1. 위의 예시 URL 중 하나를 복사하거나 직접 입력",
                                fontSize = 10.sp,
                                color = Color(0xFF2E7D32)
                            )
                            Text(
                                text = "2. 파일 타입 선택 (이미지/파일)",
                                fontSize = 10.sp,
                                color = Color(0xFF2E7D32)
                            )
                            Text(
                                text = "3. 추가 버튼 클릭하여 다운로드 시작",
                                fontSize = 10.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { addDownloadItem() },
                    enabled = newDownloadUrl.isNotEmpty()
                ) {
                    Text("추가")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}
