package com.example.android_applicatoins.screens.native

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.android_applicatoins.utils.SharedDataManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(0) }
    
    // 공유된 데이터 상태
    var sharedText by remember { mutableStateOf(SharedDataManager.sharedText) }
    var sharedImageUri by remember { mutableStateOf(SharedDataManager.sharedImageUri) }
    var sharedFileUri by remember { mutableStateOf(SharedDataManager.sharedFileUri) }
    var sharedDataType by remember { mutableStateOf(SharedDataManager.sharedDataType) }
    
    // 저장 상태
    var isSaving by remember { mutableStateOf(false) }
    var saveMessage by remember { mutableStateOf("") }
    
    // 이미지 비트맵
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    // 파일 정보
    var fileName by remember { mutableStateOf("") }
    var fileSize by remember { mutableStateOf("") }
    
    // 내부→외부 공유 상태
    var shareText by remember { mutableStateOf("") }
    var shareResult by remember { mutableStateOf("") }
    
    // 공유된 데이터가 있으면 로드
    LaunchedEffect(Unit) {
        Log.d("ShareScreen", "=== LaunchedEffect 시작 ===")
        Log.d("ShareScreen", "SharedDataManager.hasSharedData(): ${SharedDataManager.hasSharedData()}")
        Log.d("ShareScreen", "SharedDataManager.sharedDataType: ${SharedDataManager.sharedDataType}")
        Log.d("ShareScreen", "SharedDataManager.sharedText: ${SharedDataManager.sharedText}")
        Log.d("ShareScreen", "SharedDataManager.sharedImageUri: ${SharedDataManager.sharedImageUri}")
        Log.d("ShareScreen", "SharedDataManager.sharedFileUri: ${SharedDataManager.sharedFileUri}")
        
        if (SharedDataManager.hasSharedData()) {
            sharedText = SharedDataManager.sharedText
            sharedImageUri = SharedDataManager.sharedImageUri
            sharedFileUri = SharedDataManager.sharedFileUri
            sharedDataType = SharedDataManager.sharedDataType
            
            Log.d("ShareScreen", "로컬 상태 업데이트 완료:")
            Log.d("ShareScreen", "  - sharedText: $sharedText")
            Log.d("ShareScreen", "  - sharedImageUri: $sharedImageUri")
            Log.d("ShareScreen", "  - sharedFileUri: $sharedFileUri")
            Log.d("ShareScreen", "  - sharedDataType: $sharedDataType")
            
            // 이미지인 경우 비트맵 로드
            if (sharedImageUri != null) {
                Log.d("ShareScreen", "이미지 URI 감지됨: $sharedImageUri")
                try {
                    val inputStream = context.contentResolver.openInputStream(sharedImageUri!!)
                    if (inputStream != null) {
                        imageBitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream.close()
                        Log.d("ShareScreen", "✅ 이미지 로드 성공: ${imageBitmap?.width}x${imageBitmap?.height}")
                    } else {
                        Log.e("ShareScreen", "❌ 이미지 InputStream이 null")
                    }
                } catch (e: Exception) {
                    Log.e("ShareScreen", "❌ 이미지 로드 실패", e)
                    imageBitmap = null
                }
            } else {
                Log.d("ShareScreen", "이미지 URI가 null")
            }
            
            // 파일인 경우 정보 로드
            if (sharedFileUri != null) {
                Log.d("ShareScreen", "파일 URI 감지됨: $sharedFileUri")
                try {
                    val inputStream = context.contentResolver.openInputStream(sharedFileUri!!)
                    if (inputStream != null) {
                        val bytes = inputStream.readBytes()
                        inputStream.close()
                        
                        fileSize = "${bytes.size / 1024} KB"
                        
                        // 파일명 추출
                        val uriString = sharedFileUri.toString()
                        fileName = uriString.substringAfterLast("/").substringBefore("?")
                        if (fileName.isEmpty()) {
                            fileName = "shared_file_${System.currentTimeMillis()}"
                        }
                        Log.d("ShareScreen", "✅ 파일 정보 로드 성공: $fileName, $fileSize")
                    } else {
                        Log.e("ShareScreen", "❌ 파일 InputStream이 null")
                    }
                } catch (e: Exception) {
                    Log.e("ShareScreen", "❌ 파일 정보 로드 실패", e)
                }
            } else {
                Log.d("ShareScreen", "파일 URI가 null")
            }
        } else {
            Log.d("ShareScreen", "공유된 데이터가 없음")
        }
        Log.d("ShareScreen", "=== LaunchedEffect 완료 ===")
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("공유 및 저장소") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 탭 선택
            TabRow(
                selectedTabIndex = currentTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    text = { Text("외부→내부") },
                    icon = { Icon(Icons.Default.Download, "받기") }
                )
                Tab(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    text = { Text("내부→외부") },
                    icon = { Icon(Icons.Default.Share, "공유") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (currentTab) {
                0 -> ExternalToInternalTab(
                    sharedText = sharedText,
                    imageBitmap = imageBitmap,
                    sharedFileUri = sharedFileUri,
                    fileName = fileName,
                    fileSize = fileSize,
                    sharedDataType = sharedDataType,
                    isSaving = isSaving,
                    saveMessage = saveMessage,
                    onSave = { dataType ->
                        isSaving = true
                        saveMessage = ""
                        
                        // 저장 완료 후 UI 상태 업데이트를 위한 콜백
                        fun updateUIAfterSave() {
                            isSaving = false
                            saveMessage = SharedDataManager.saveMessage
                        }
                        
                        when (dataType) {
                            "text" -> saveTextToFile(context, sharedText!!, ::updateUIAfterSave)
                            "image" -> saveImageToGallery(context, sharedImageUri!!, ::updateUIAfterSave)
                            "file" -> saveFileToDownloads(context, sharedFileUri!!, fileName, ::updateUIAfterSave)
                        }
                    }
                )
                1 -> InternalToExternalTab(
                    shareText = shareText,
                    onShareTextChange = { shareText = it },
                    shareResult = shareResult,
                    onShareText = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "텍스트 공유"))
                        shareResult = "텍스트 공유 완료!"
                    },
                    onShareImage = {
                        try {
                            // 샘플 이미지 생성 (실제로는 앱 내부 이미지를 사용)
                            val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
                            val filename = "shared_image_${System.currentTimeMillis()}.jpg"
                            val file = File(context.cacheDir, filename)
                            
                            FileOutputStream(file).use { stream ->
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                            }
                            
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                file
                            )
                            
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "image/jpeg"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, "이미지 공유"))
                            shareResult = "이미지 공유 완료!"
                        } catch (e: Exception) {
                            shareResult = "이미지 공유 실패: ${e.message}"
                        }
                    }
                )
            }
        }
    }
}

// 외부→내부 탭 (외부 앱에서 공유된 데이터 받기)
@Composable
fun ExternalToInternalTab(
    sharedText: String?,
    imageBitmap: Bitmap?,
    sharedFileUri: Uri?,
    fileName: String,
    fileSize: String,
    sharedDataType: String?,
    isSaving: Boolean,
    saveMessage: String,
    onSave: (String) -> Unit
) {
    val context = LocalContext.current
    var localImageBitmap by remember { mutableStateOf(imageBitmap) }
    
    // 이미지 비트맵이 변경되면 로컬 상태 업데이트
    LaunchedEffect(imageBitmap) {
        localImageBitmap = imageBitmap
    }
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (SharedDataManager.hasSharedData()) {
            // 공유된 데이터 표시
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "공유 받은 데이터",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // 데이터 타입 표시
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = when (sharedDataType) {
                                "text" -> Icons.Default.TextFields
                                "image" -> Icons.Default.Image
                                "file" -> Icons.Default.InsertDriveFile
                                else -> Icons.Default.Info
                            },
                            contentDescription = "데이터 타입",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = when (sharedDataType) {
                                "text" -> "텍스트"
                                "image" -> "이미지"
                                "file" -> "파일"
                                else -> "알 수 없음"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // 텍스트 데이터 표시
                    if (!sharedText.isNullOrEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "텍스트 내용",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = sharedText!!,
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp
                                )
                            }
                        }
                    }
                    
                    // 이미지 데이터 표시
                    if (imageBitmap != null) {
                        var showImageFullScreen by remember { mutableStateOf(false) }
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "이미지",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // 이미지 크기 정보 표시
                                Text(
                                    text = "크기: ${imageBitmap.width} x ${imageBitmap.height}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Image(
                                    bitmap = imageBitmap.asImageBitmap(),
                                    contentDescription = "공유된 이미지",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { showImageFullScreen = true },
                                    contentScale = ContentScale.Fit
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "이미지를 클릭하면 전체화면으로 볼 수 있습니다",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        
                        // 전체화면 이미지 뷰어
                        if (showImageFullScreen) {
                            ImageFullScreenViewer(
                                bitmap = imageBitmap!!,
                                onDismiss = { showImageFullScreen = false }
                            )
                        }
                    } else if (sharedDataType == "image" && SharedDataManager.sharedImageUri != null) {
                        // 이미지 로드 실패 시 안내 메시지 (실제로 이미지 URI가 있을 때만)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFEBEE)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "이미지 로드 실패",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFC62828)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "이미지를 미리보기할 수 없습니다. 저장 후 갤러리에서 확인해주세요.",
                                    fontSize = 12.sp,
                                    color = Color(0xFFC62828)
                                )
                            }
                        }
                    }
                    
                    // 파일 데이터 표시
                    if (sharedFileUri != null && fileName.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "파일 정보",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.InsertDriveFile,
                                        contentDescription = "파일",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Column {
                                        Text(
                                            text = fileName,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (fileSize.isNotEmpty()) {
                                            Text(
                                                text = fileSize,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // 저장 버튼
                    Button(
                        onClick = { onSave(sharedDataType ?: "") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = when (sharedDataType) {
                                "text" -> "텍스트를 파일로 저장"
                                "image" -> "이미지를 갤러리에 저장"
                                "file" -> "파일을 다운로드 폴더에 저장"
                                else -> "저장"
                            }
                        )
                    }
                    
                    // 저장 메시지 표시
                    if (saveMessage.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (saveMessage.contains("성공")) 
                                    Color(0xFFE8F5E8) else Color(0xFFFFEBEE)
                            )
                        ) {
                            Text(
                                text = saveMessage,
                                modifier = Modifier.padding(16.dp),
                                color = if (saveMessage.contains("성공")) 
                                    Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        }
                    }
                }
            }
        } else {
            // 공유된 데이터가 없는 경우
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "공유",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "공유된 데이터가 없습니다",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "다른 앱에서 '공유하기'를 통해\n텍스트, 이미지, 파일을 보내보세요",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// 내부→외부 탭 (앱 내부 데이터를 외부로 공유)
@Composable
fun InternalToExternalTab(
    shareText: String,
    onShareTextChange: (String) -> Unit,
    shareResult: String,
    onShareText: () -> Unit,
    onShareImage: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "내부 → 외부 데이터 공유",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "앱 내부 데이터를 외부 앱으로 공유",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        // 텍스트 공유
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "텍스트 공유",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = shareText,
                    onValueChange = onShareTextChange,
                    label = { Text("공유할 텍스트를 입력하세요") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = onShareText,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, "텍스트 공유", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("텍스트 공유하기")
                }
            }
        }
        
        // 이미지 공유
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "이미지 공유",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = onShareImage,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, "이미지 공유", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("샘플 이미지 공유하기")
                }
            }
        }
        
        // 공유 결과 표시
        if (shareResult.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = shareResult,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// 텍스트를 파일로 저장
private fun saveTextToFile(context: Context, text: String, onComplete: () -> Unit) {
    try {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "shared_text_$timestamp.txt"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 이상에서는 MediaStore API 사용
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "text/plain")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { stream ->
                    stream.write(text.toByteArray())
                }
                SharedDataManager.saveMessage = "텍스트가 다운로드 폴더에 저장되었습니다: $fileName"
                Log.d("ShareScreen", "텍스트 저장 성공: $fileName")
            } ?: run {
                SharedDataManager.saveMessage = "텍스트 저장에 실패했습니다: MediaStore URI 생성 실패"
                Log.e("ShareScreen", "텍스트 저장 실패: MediaStore URI 생성 실패")
            }
        } else {
            // Android 9 이하에서는 기존 방식 사용
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            
            FileOutputStream(file).use { fos ->
                fos.write(text.toByteArray())
            }
            SharedDataManager.saveMessage = "텍스트가 다운로드 폴더에 저장되었습니다: $fileName"
            Log.d("ShareScreen", "텍스트 저장 성공: $fileName")
        }
            } catch (e: Exception) {
            Log.e("ShareScreen", "텍스트 저장 실패", e)
            SharedDataManager.saveMessage = "텍스트 저장에 실패했습니다: ${e.message}"
        } finally {
            SharedDataManager.isSaving = false
            onComplete() // UI 상태 업데이트 콜백 호출
        }
    }

// 이미지를 갤러리에 저장
private fun saveImageToGallery(context: Context, imageUri: Uri, onComplete: () -> Unit) {
    try {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        
        if (bitmap != null) {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "shared_image_$timestamp.jpg"
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10 이상에서는 MediaStore API 사용
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                
                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                    }
                    SharedDataManager.saveMessage = "이미지가 갤러리에 저장되었습니다: $fileName"
                    Log.d("ShareScreen", "이미지 저장 성공: $fileName")
                } ?: run {
                    SharedDataManager.saveMessage = "이미지 저장에 실패했습니다: MediaStore URI 생성 실패"
                    Log.e("ShareScreen", "이미지 저장 실패: MediaStore URI 생성 실패")
                }
            } else {
                // Android 9 이하에서는 기존 방식 사용
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val file = File(imagesDir, fileName)
                
                FileOutputStream(file).use { fos ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                }
                SharedDataManager.saveMessage = "이미지가 갤러리에 저장되었습니다: $fileName"
                Log.d("ShareScreen", "이미지 저장 성공: $fileName")
            }
        } else {
            SharedDataManager.saveMessage = "이미지 로드에 실패했습니다"
            Log.e("ShareScreen", "이미지 저장 실패: 비트맵이 null")
        }
            } catch (e: Exception) {
            Log.e("ShareScreen", "이미지 저장 실패", e)
            SharedDataManager.saveMessage = "이미지 저장에 실패했습니다: ${e.message}"
        } finally {
            SharedDataManager.isSaving = false
            onComplete() // UI 상태 업데이트 콜백 호출
        }
    }

// 파일을 다운로드 폴더에 저장
private fun saveFileToDownloads(context: Context, fileUri: Uri, fileName: String, onComplete: () -> Unit) {
    try {
        val inputStream = context.contentResolver.openInputStream(fileUri)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 이상에서는 MediaStore API 사용
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "*/*")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { stream ->
                    inputStream?.use { input ->
                        input.copyTo(stream)
                    }
                }
                SharedDataManager.saveMessage = "파일이 다운로드 폴더에 저장되었습니다: $fileName"
                Log.d("ShareScreen", "파일 저장 성공: $fileName")
            } ?: run {
                SharedDataManager.saveMessage = "파일 저장에 실패했습니다: MediaStore URI 생성 실패"
                Log.e("ShareScreen", "파일 저장 실패: MediaStore URI 생성 실패")
            }
        } else {
            // Android 9 이하에서는 기존 방식 사용
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            
            FileOutputStream(file).use { fos ->
                inputStream?.use { input ->
                    input.copyTo(fos)
                }
            }
            SharedDataManager.saveMessage = "파일이 다운로드 폴더에 저장되었습니다: $fileName"
            Log.d("ShareScreen", "파일 저장 성공: $fileName")
        }
            } catch (e: Exception) {
            Log.e("ShareScreen", "파일 저장 실패", e)
            SharedDataManager.saveMessage = "파일 저장에 실패했습니다: ${e.message}"
        } finally {
            SharedDataManager.isSaving = false
            onComplete() // UI 상태 업데이트 콜백 호출
        }
    }

// 전체화면 이미지 뷰어
@Composable
fun ImageFullScreenViewer(
    bitmap: Bitmap,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
    ) {
        // 이미지
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "전체화면 이미지",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        
        // 닫기 버튼
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "닫기",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        
        // 안내 텍스트
        Text(
            text = "화면을 탭하면 닫힙니다",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}
