package com.example.android_applicatoins.screens.native

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.core.content.FileProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

// 저장 유틸 함수들
fun saveTextToDownloads(context: Context, text: String, onResult: (Boolean, String) -> Unit) {
    try {
        val filename = "shared_text_${System.currentTimeMillis()}.txt"
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, filename)
            put(MediaStore.Downloads.MIME_TYPE, "text/plain")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { stream ->
                stream.write(text.toByteArray())
                onResult(true, "텍스트가 다운로드 폴더에 저장됨 ✅")
                return
            }
        }
        onResult(false, "텍스트 저장 실패")
    } catch (e: Exception) {
        onResult(false, "저장 실패: ${e.message}")
    }
}

fun saveImageToGallery(context: Context, bitmap: Bitmap, onResult: (Boolean, String) -> Unit) {
    try {
        val filename = "shared_image_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                onResult(true, "이미지가 갤러리에 저장됨 ✅")
                return
            }
        }
        onResult(false, "이미지 저장 실패")
    } catch (e: Exception) {
        onResult(false, "저장 실패: ${e.message}")
    }
}

// 권한 체크 함수
fun checkStoragePermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Android 13 이상: READ_MEDIA_IMAGES 권한 체크
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android 10 이상: Scoped Storage로 권한 불필요
        true
    } else {
        // Android 9 이하: WRITE_EXTERNAL_STORAGE 권한 체크
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

// 권한 요청 함수
fun requestStoragePermission(activity: android.app.Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Android 13 이상: READ_MEDIA_IMAGES 권한 요청
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
            1001
        )
    } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        // Android 9 이하: WRITE_EXTERNAL_STORAGE 권한 요청
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1002
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(0) }
    
    // 외부에서 받은 데이터 상태
    var receivedText by remember { mutableStateOf("") }
    var receivedImageUri by remember { mutableStateOf<Uri?>(null) }
    var receivedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    // 공유할 데이터 상태
    var shareText by remember { mutableStateOf("") }
    var shareResult by remember { mutableStateOf("") }
    
    // 외부 앱에서 데이터 받기
    val textReceiver = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val text = inputStream?.bufferedReader().use { reader -> reader?.readText() } ?: ""
                receivedText = text
            } catch (e: Exception) {
                receivedText = "텍스트 읽기 실패: ${e.message}"
            }
        }
    }
    
    val imageReceiver = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            receivedImageUri = it
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                receivedImageBitmap = bitmap
            } catch (e: Exception) {
                // 이미지 로딩 실패 처리
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("공용 저장소 & 공유 테스트", fontWeight = FontWeight.Bold) },
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
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
            
            Spacer(modifier = Modifier.height(24.dp))
            
            when (currentTab) {
                0 -> ExternalToInternalTab(
                    receivedText = receivedText,
                    receivedImageBitmap = receivedImageBitmap,
                    onReceiveText = { textReceiver.launch("text/*") },
                    onReceiveImage = { imageReceiver.launch("image/*") },
                    onSaveToGallery = {
                        receivedImageBitmap?.let { bitmap ->
                            try {
                                val filename = "shared_image_${System.currentTimeMillis()}.jpg"
                                val contentValues = ContentValues().apply {
                                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                                }
                                
                                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                                uri?.let {
                                    context.contentResolver.openOutputStream(it)?.use { stream ->
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                                    }
                                }
                            } catch (e: Exception) {
                                // 갤러리 저장 실패 처리
                            }
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

@Composable
fun ExternalToInternalTab(
    receivedText: String,
    receivedImageBitmap: Bitmap?,
    onReceiveText: () -> Unit,
    onReceiveImage: () -> Unit,
    onSaveToGallery: () -> Unit
) {
    val context = LocalContext.current
    var saveResult by remember { mutableStateOf("") }
    var showPermissionDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "외부 → 내부 데이터 받기",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2)
        )
        
        Text(
            text = "카톡, 외부 앱에서 공유받은 데이터를 앱에서 표시하고 공용 폴더에 저장",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 텍스트 받기 + 저장
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "텍스트 받기",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = onReceiveText,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Download, "텍스트 받기", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("텍스트 파일 선택")
                }
                
                if (receivedText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = receivedText,
                            modifier = Modifier.padding(16.dp),
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = {
                            if (checkStoragePermission(context)) {
                                saveTextToDownloads(context, receivedText) { success, msg ->
                                    saveResult = msg
                                }
                            } else {
                                showPermissionDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, "텍스트 저장", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("다운로드 폴더에 저장")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 이미지 받기 + 저장
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "이미지 받기",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = onReceiveImage,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Image, "이미지 받기", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("이미지 파일 선택")
                }
                
                if (receivedImageBitmap != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Image(
                            bitmap = receivedImageBitmap.asImageBitmap(),
                            contentDescription = "받은 이미지",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = {
                            if (checkStoragePermission(context)) {
                                saveImageToGallery(context, receivedImageBitmap) { success, msg ->
                                    saveResult = msg
                                }
                            } else {
                                showPermissionDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, "갤러리 저장", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("갤러리에 저장")
                    }
                }
            }
        }
        
        // 저장 결과 표시
        if (saveResult.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (saveResult.contains("✅")) Color(0xFFE8F5E8) else Color(0xFFFFEBEE)
                )
            ) {
                Text(
                    text = saveResult,
                    modifier = Modifier.padding(16.dp),
                    color = if (saveResult.contains("✅")) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // 권한 안내 다이얼로그
        if (showPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showPermissionDialog = false },
                title = {
                    Text(
                        "저장소 권한 필요",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "파일을 공용 폴더에 저장하려면 저장소 권한이 필요합니다.",
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "권한 설정 방법:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Text(
                                text = "설정 > 앱 > 동기부여 앱 > 권한 > 사진 및 동영상",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        } else {
                            Text(
                                text = "설정 > 앱 > 동기부여 앱 > 권한 > 저장소",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showPermissionDialog = false
                            try {
                                val activity = context as? android.app.Activity
                                activity?.let { requestStoragePermission(it) }
                            } catch (e: Exception) {
                                // 권한 요청 실패 처리
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text("권한 요청")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showPermissionDialog = false }
                    ) {
                        Text("취소")
                    }
                }
            )
        }
    }
}

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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "내부 → 외부 데이터 공유",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF44336)
        )
        
        Text(
            text = "앱 내부 데이터를 외부 앱으로 공유",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 텍스트 공유
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "텍스트 공유",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF44336)
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, "텍스트 공유", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("텍스트 공유하기")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 이미지 공유
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "이미지 공유",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF44336)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = onShareImage,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, "이미지 공유", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("샘플 이미지 공유하기")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (shareResult.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = shareResult,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}
