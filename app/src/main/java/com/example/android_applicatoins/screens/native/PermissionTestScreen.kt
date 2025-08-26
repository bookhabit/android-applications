package com.example.android_applicatoins.screens.native

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionTestScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    
    // 권한 상태를 저장할 상태 변수들
    var cameraPermission by remember { mutableStateOf(false) }
    var locationPermission by remember { mutableStateOf(false) }
    var phonePermission by remember { mutableStateOf(false) }
    var contactsPermission by remember { mutableStateOf(false) }
    var calendarPermission by remember { mutableStateOf(false) }
    var smsPermission by remember { mutableStateOf(false) }
    var storagePermission by remember { mutableStateOf(false) }
    var audioPermission by remember { mutableStateOf(false) }
    var bluetoothPermission by remember { mutableStateOf(false) }
    
    // 권한 요청 결과를 저장할 상태
    var permissionResults by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    
    // 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // 권한 요청 결과 처리
        val results = mutableMapOf<String, Boolean>()
        permissions.forEach { (permission, isGranted) ->
            results[permission] = isGranted
            // 각 권한별로 상태 업데이트
            when (permission) {
                Manifest.permission.CAMERA -> cameraPermission = isGranted
                Manifest.permission.ACCESS_FINE_LOCATION -> locationPermission = isGranted
                Manifest.permission.CALL_PHONE -> phonePermission = isGranted
                Manifest.permission.READ_CONTACTS -> contactsPermission = isGranted
                Manifest.permission.READ_CALENDAR -> calendarPermission = isGranted
                Manifest.permission.SEND_SMS -> smsPermission = isGranted
                Manifest.permission.READ_EXTERNAL_STORAGE -> storagePermission = isGranted
                Manifest.permission.RECORD_AUDIO -> audioPermission = isGranted
                Manifest.permission.BLUETOOTH_CONNECT -> bluetoothPermission = isGranted
            }
        }
        permissionResults = results
    }
    
    // 설정 앱으로 이동하는 런처
    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { }
    
    // 권한 체크 함수
    fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
    
    // 권한 요청 함수
    fun requestPermissions(permissions: Array<String>) {
        permissionLauncher.launch(permissions)
    }
    
    // 설정 앱으로 이동
    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        settingsLauncher.launch(intent)
    }
    
    // 초기 권한 상태 체크
    LaunchedEffect(Unit) {
        cameraPermission = checkPermission(Manifest.permission.CAMERA)
        locationPermission = checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        phonePermission = checkPermission(Manifest.permission.CALL_PHONE)
        contactsPermission = checkPermission(Manifest.permission.READ_CONTACTS)
        calendarPermission = checkPermission(Manifest.permission.READ_CALENDAR)
        smsPermission = checkPermission(Manifest.permission.SEND_SMS)
        storagePermission = checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        audioPermission = checkPermission(Manifest.permission.RECORD_AUDIO)
        bluetoothPermission = checkPermission(Manifest.permission.BLUETOOTH_CONNECT)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("권한 테스트") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "안드로이드 권한 시스템 테스트",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Text(
                text = "다양한 권한을 체크하고 요청해보세요. 각 권한별로 상태와 기능을 확인할 수 있습니다.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 32.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            // 권한 그룹별 테스트
            PermissionGroupSection(
                title = "📷 카메라 권한",
                description = "사진 촬영 및 카메라 기능 사용",
                permission = Manifest.permission.CAMERA,
                isGranted = cameraPermission,
                onRequest = {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA))
                },
                onTest = {
                    // 카메라 기능 테스트 (권한이 있을 때만)
                    if (cameraPermission) {
                        // 카메라 앱 실행
                        val intent = Intent("android.media.action.IMAGE_CAPTURE")
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // 카메라 앱이 없는 경우
                        }
                    }
                }
            )
            
            PermissionGroupSection(
                title = "📍 위치 권한",
                description = "정확한 위치 정보 접근",
                permission = Manifest.permission.ACCESS_FINE_LOCATION,
                isGranted = locationPermission,
                onRequest = {
                    requestPermissions(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ))
                },
                onTest = {
                    if (locationPermission) {
                        // 위치 관련 기능 테스트
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // 설정 앱이 없는 경우
                        }
                    }
                }
            )
            
            PermissionGroupSection(
                title = "📞 전화 권한",
                description = "전화 걸기 및 통화 로그 접근",
                permission = Manifest.permission.CALL_PHONE,
                isGranted = phonePermission,
                onRequest = {
                    requestPermissions(arrayOf(
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_CALL_LOG
                    ))
                },
                onTest = {
                    if (phonePermission) {
                        // 전화 다이얼러 열기
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse("tel:01012345678")
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // 전화 앱이 없는 경우
                        }
                    }
                }
            )
            
            PermissionGroupSection(
                title = "👥 연락처 권한",
                description = "연락처 정보 읽기 및 쓰기",
                permission = Manifest.permission.READ_CONTACTS,
                isGranted = contactsPermission,
                onRequest = {
                    requestPermissions(arrayOf(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS
                    ))
                },
                onTest = {
                    if (contactsPermission) {
                        // 연락처 앱 열기
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("content://contacts/people")
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // 연락처 앱이 없는 경우
                        }
                    }
                }
            )
            
            PermissionGroupSection(
                title = "📅 캘린더 권한",
                description = "캘린더 정보 읽기 및 쓰기",
                permission = Manifest.permission.READ_CALENDAR,
                isGranted = calendarPermission,
                onRequest = {
                    requestPermissions(arrayOf(
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR
                    ))
                },
                onTest = {
                    if (calendarPermission) {
                        // 캘린더 앱 열기
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("content://com.android.calendar/time")
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // 캘린더 앱이 없는 경우
                        }
                    }
                }
            )
            
            PermissionGroupSection(
                title = "💬 SMS 권한",
                description = "문자 메시지 전송 및 읽기",
                permission = Manifest.permission.SEND_SMS,
                isGranted = smsPermission,
                onRequest = {
                    requestPermissions(arrayOf(
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_SMS
                    ))
                },
                onTest = {
                    if (smsPermission) {
                        // SMS 앱 열기
                        val intent = Intent(Intent.ACTION_SENDTO)
                        intent.data = Uri.parse("smsto:01012345678")
                        intent.putExtra("sms_body", "테스트 메시지")
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // SMS 앱이 없는 경우
                        }
                    }
                }
            )
            
            PermissionGroupSection(
                title = "💾 저장소 권한",
                description = "외부 저장소 파일 접근",
                permission = Manifest.permission.READ_EXTERNAL_STORAGE,
                isGranted = storagePermission,
                onRequest = {
                    requestPermissions(arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ))
                },
                onTest = {
                    if (storagePermission) {
                        // 파일 관리자 열기
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "*/*"
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // 파일 관리자가 없는 경우
                        }
                    }
                }
            )
            
            PermissionGroupSection(
                title = "🎤 마이크 권한",
                description = "음성 녹음 및 오디오 접근",
                permission = Manifest.permission.RECORD_AUDIO,
                isGranted = audioPermission,
                onRequest = {
                    requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO))
                },
                onTest = {
                    if (audioPermission) {
                        // 음성 녹음 앱 열기 (시스템에서 제공하는 경우)
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "audio/*"
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // 음성 녹음 앱이 없는 경우
                        }
                    }
                }
            )
            
            PermissionGroupSection(
                title = "🔵 블루투스 권한",
                description = "블루투스 연결 및 스캔",
                permission = Manifest.permission.BLUETOOTH_CONNECT,
                isGranted = bluetoothPermission,
                onRequest = {
                    requestPermissions(arrayOf(
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                    ))
                },
                onTest = {
                    if (bluetoothPermission) {
                        // 블루투스 설정 열기
                        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // 블루투스 설정이 없는 경우
                        }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 일괄 권한 요청
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🚀 일괄 권한 요청",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Text(
                        text = "모든 권한을 한 번에 요청합니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Button(
                        onClick = {
                            requestPermissions(arrayOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.CALL_PHONE,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.READ_CALENDAR,
                                Manifest.permission.SEND_SMS,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.BLUETOOTH_CONNECT
                            ))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "권한 요청",
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 8.dp)
                        )
                        Text("모든 권한 요청")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 설정 앱으로 이동
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "⚙️ 앱 설정",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Text(
                        text = "권한이 거부된 경우 앱 설정에서 수동으로 허용할 수 있습니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Button(
                        onClick = { openAppSettings() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "설정",
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 8.dp)
                        )
                        Text("앱 설정 열기")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 권한 요청 결과 표시
            if (permissionResults.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📋 권한 요청 결과",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        permissionResults.forEach { (permission, isGranted) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = if (isGranted) "허용됨" else "거부됨",
                                    tint = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(end = 8.dp)
                                )
                                
                                Text(
                                    text = permission.substringAfterLast("."),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                Text(
                                    text = if (isGranted) "허용" else "거부",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 권한 시스템 설명
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
                        text = "📚 권한 시스템 가이드",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Text(
                        text = "• 설치 시간 권한: 앱 설치 시 자동으로 부여 (INTERNET 등)",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Text(
                        text = "• 런타임 권한: 실행 중 사용자 동의 필요 (카메라, 위치 등)",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Text(
                        text = "• 권한 거부 시: 해당 기능 사용 불가, 설정에서 수동 허용 필요",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Text(
                        text = "• 권한 그룹: 관련 권한들은 함께 요청하는 것이 좋습니다",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionGroupSection(
    title: String,
    description: String,
    permission: String,
    isGranted: Boolean,
    onRequest: () -> Unit,
    onTest: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // 권한 상태 표시
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isGranted) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = if (isGranted) "허용" else "거부",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRequest,
                    enabled = !isGranted,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isGranted) 
                            MaterialTheme.colorScheme.secondary 
                        else 
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "권한 요청",
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp)
                    )
                    Text(if (isGranted) "이미 허용됨" else "권한 요청")
                }
                
                Button(
                    onClick = onTest,
                    enabled = isGranted,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "기능 테스트",
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp)
                    )
                    Text("기능 테스트")
                }
            }
        }
    }
}
