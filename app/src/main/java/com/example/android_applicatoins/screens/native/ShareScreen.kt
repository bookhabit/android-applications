package com.example.android_applicatoins.screens.native

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.android_applicatoins.utils.SharedDataManager
import java.io.File
import java.io.FileOutputStream
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
        if (SharedDataManager.hasSharedData()) {
            sharedText = SharedDataManager.sharedText
            sharedImageUri = SharedDataManager.sharedImageUri
            sharedFileUri = SharedDataManager.sharedFileUri
            sharedDataType = SharedDataManager.sharedDataType
            
            // 이미지인 경우 비트맵 로드
            if (sharedImageUri != null) {
                try {
                    val inputStream = context.contentResolver.openInputStream(sharedImageUri!!)
                    imageBitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                } catch (e: Exception) {
                    Log.e("ShareScreen", "이미지 로드 실패", e)
                }
            }
            
            // 파일인 경우 정보 로드
            if (sharedFileUri != null) {
                try {
                    val inputStream = context.contentResolver.openInputStream(sharedFileUri!!)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()
                    
                    if (bytes != null) {
                        fileSize = "${bytes.size / 1024} KB"
                        
                        // 파일명 추출
                        val uriString = sharedFileUri.toString()
                        fileName = uriString.substringAfterLast("/").substringBefore("?")
                        if (fileName.isEmpty()) {
                            fileName = "shared_file_${System.currentTimeMillis()}"
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ShareScreen", "파일 정보 로드 실패", e)
                }
            }
        }
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
                        when (dataType) {
                            "text" -> saveTextToFile(context, sharedText!!)
                            "image" -> saveImageToGallery(context, sharedImageUri!!)
                            "file" -> saveFileToDownloads(context, sharedFileUri!!, fileName)
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
                                Image(
                                    bitmap = imageBitmap.asImageBitmap(),
                                    contentDescription = "공유된 이미지",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(8.dp))
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
private fun saveTextToFile(context: Context, text: String) {
    try {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "shared_text_$timestamp.txt"
        
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        
        FileOutputStream(file).use { fos ->
            fos.write(text.toByteArray())
        }
        
        SharedDataManager.saveMessage = "텍스트가 다운로드 폴더에 저장되었습니다: $fileName"
        SharedDataManager.isSaving = false
    } catch (e: Exception) {
        Log.e("ShareScreen", "텍스트 저장 실패", e)
        SharedDataManager.saveMessage = "텍스트 저장에 실패했습니다: ${e.message}"
        SharedDataManager.isSaving = false
    }
}

// 이미지를 갤러리에 저장
private fun saveImageToGallery(context: Context, imageUri: Uri) {
    try {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        
        if (bitmap != null) {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "shared_image_$timestamp.jpg"
            
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val file = File(imagesDir, fileName)
            
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            }
            
            SharedDataManager.saveMessage = "이미지가 갤러리에 저장되었습니다: $fileName"
            SharedDataManager.isSaving = false
        } else {
            SharedDataManager.saveMessage = "이미지 로드에 실패했습니다"
            SharedDataManager.isSaving = false
        }
    } catch (e: Exception) {
        Log.e("ShareScreen", "이미지 저장 실패", e)
        SharedDataManager.saveMessage = "이미지 저장에 실패했습니다: ${e.message}"
        SharedDataManager.isSaving = false
    }
}

// 파일을 다운로드 폴더에 저장
private fun saveFileToDownloads(context: Context, fileUri: Uri, fileName: String) {
    try {
        val inputStream = context.contentResolver.openInputStream(fileUri)
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        
        FileOutputStream(file).use { fos ->
            inputStream?.use { input ->
                input.copyTo(fos)
            }
        }
        
        SharedDataManager.saveMessage = "파일이 다운로드 폴더에 저장되었습니다: $fileName"
        SharedDataManager.isSaving = false
    } catch (e: Exception) {
        Log.e("ShareScreen", "파일 저장 실패", e)
        SharedDataManager.saveMessage = "파일 저장에 실패했습니다: ${e.message}"
        SharedDataManager.isSaving = false
    }
}
