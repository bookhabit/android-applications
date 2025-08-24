package com.example.android_applicatoins.screens.basic

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogPopupNotificationScreen(
    onBackPressed: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    
    // 상태 변수들
    var showBasicDialog by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }
    var showPopupMenu by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var notificationCount by remember { mutableStateOf(0) }
    var hasNotificationPermission by remember { mutableStateOf(false) }
    
    // 알림 권한 요청
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            // 권한이 허용되면 알림 채널 생성
            createNotificationChannel(context)
        }
    }
    
    // 권한 확인 및 요청
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )) {
                PackageManager.PERMISSION_GRANTED -> {
                    hasNotificationPermission = true
                    createNotificationChannel(context)
                }
                PackageManager.PERMISSION_DENIED -> {
                    hasNotificationPermission = false
                    // 권한 요청
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Android 13 미만에서는 권한이 자동으로 허용됨
            hasNotificationPermission = true
            createNotificationChannel(context)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("대화상자 & 팝업 & 알림") },
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 대화상자 테스트 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "💬 대화상자 테스트",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                                                 Button(onClick = { showBasicDialog = true }) {
                             Icon(Icons.Default.Chat, "기본")
                             Spacer(modifier = Modifier.width(4.dp))
                             Text("기본 대화상자")
                         }
                        
                        Button(onClick = { showAlertDialog = true }) {
                            Icon(Icons.Default.Warning, "경고")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("경고 대화상자")
                        }
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(onClick = { showDatePickerDialog = true }) {
                            Icon(Icons.Default.DateRange, "날짜")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("날짜 선택")
                        }
                        
                        Button(onClick = { showTimePickerDialog = true }) {
                            Icon(Icons.Default.Schedule, "시간")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("시간 선택")
                        }
                    }
                    
                    Button(onClick = { showCustomDialog = true }) {
                        Icon(Icons.Default.Build, "커스텀")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("커스텀 대화상자")
                    }
                }
            }
            
            // 팝업 메뉴 테스트 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🍔 팝업 메뉴 테스트",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Button(onClick = { showPopupMenu = true }) {
                        Icon(Icons.Default.MoreVert, "메뉴")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("팝업 메뉴 표시")
                    }
                }
            }
            
                         // 알림 테스트 카드
             Card(
                 modifier = Modifier.fillMaxWidth(),
                 colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
             ) {
                 Column(
                     modifier = Modifier.padding(16.dp),
                     verticalArrangement = Arrangement.spacedBy(8.dp)
                 ) {
                     Text(
                         text = "🔔 알림 테스트",
                         fontSize = 18.sp,
                         fontWeight = FontWeight.Bold
                     )
                     
                     // 권한 상태 표시
                     Row(
                         verticalAlignment = Alignment.CenterVertically,
                         horizontalArrangement = Arrangement.spacedBy(8.dp)
                     ) {
                         Icon(
                             imageVector = if (hasNotificationPermission) Icons.Default.CheckCircle else Icons.Default.Warning,
                             contentDescription = "권한 상태",
                             tint = if (hasNotificationPermission) Color.Green else Color(0xFFFF9800)
                         )
                         Text(
                             text = if (hasNotificationPermission) "알림 권한 허용됨" else "알림 권한 필요",
                             color = if (hasNotificationPermission) Color.Green else Color(0xFFFF9800)
                         )
                     }
                     
                     if (!hasNotificationPermission) {
                         Button(
                             onClick = { 
                                 permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                             },
                             colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                         ) {
                             Icon(Icons.Default.Security, "권한")
                             Spacer(modifier = Modifier.width(4.dp))
                             Text("알림 권한 요청")
                         }
                     }
                     
                     Row(
                         horizontalArrangement = Arrangement.spacedBy(8.dp)
                     ) {
                         Button(
                             onClick = { 
                                 if (hasNotificationPermission) {
                                     showBasicNotification(context, ++notificationCount)
                                 }
                             },
                             enabled = hasNotificationPermission
                         ) {
                             Icon(Icons.Default.Notifications, "기본")
                             Spacer(modifier = Modifier.width(4.dp))
                             Text("기본 알림")
                         }
                         
                         Button(
                             onClick = { 
                                 if (hasNotificationPermission) {
                                     showActionNotification(context, ++notificationCount)
                                 }
                             },
                             enabled = hasNotificationPermission
                         ) {
                             Icon(Icons.Default.NotificationsActive, "액션")
                             Spacer(modifier = Modifier.width(4.dp))
                             Text("액션 알림")
                         }
                     }
                     
                     Button(
                         onClick = { 
                             if (hasNotificationPermission) {
                                 showProgressNotification(context, ++notificationCount)
                             }
                         },
                         enabled = hasNotificationPermission
                     ) {
                         Icon(Icons.Default.Sync, "진행률")
                         Spacer(modifier = Modifier.width(4.dp))
                         Text("진행률 알림")
                     }
                 }
             }
            
            // 선택된 정보 표시 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📊 선택된 정보",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (selectedDate.isNotEmpty()) {
                        Text("선택된 날짜: $selectedDate")
                    }
                    if (selectedTime.isNotEmpty()) {
                        Text("선택된 시간: $selectedTime")
                    }
                    Text("알림 개수: $notificationCount")
                }
            }
        }
        
        // 기본 대화상자
        if (showBasicDialog) {
            AlertDialog(
                onDismissRequest = { showBasicDialog = false },
                title = { Text("기본 대화상자") },
                text = { Text("이것은 기본 대화상자입니다.") },
                confirmButton = {
                    TextButton(onClick = { showBasicDialog = false }) {
                        Text("확인")
                    }
                }
            )
        }
        
        // 경고 대화상자
        if (showAlertDialog) {
            AlertDialog(
                onDismissRequest = { showAlertDialog = false },
                title = { Text("경고") },
                text = { Text("정말 삭제하시겠습니까?") },
                icon = { Icon(Icons.Default.Warning, "경고") },
                confirmButton = {
                    TextButton(
                        onClick = { 
                            showAlertDialog = false
                            // 여기에 삭제 로직 추가
                        }
                    ) {
                        Text("삭제")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAlertDialog = false }) {
                        Text("취소")
                    }
                }
            )
        }
        
        // 날짜 선택 대화상자
        if (showDatePickerDialog) {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                onDismissRequest = { showDatePickerDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedDate = "${calendar.get(Calendar.YEAR)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.DAY_OF_MONTH)}"
                            showDatePickerDialog = false
                        }
                    ) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePickerDialog = false }) {
                        Text("취소")
                    }
                }
            ) {
                DatePicker(
                    state = rememberDatePickerState(
                        initialSelectedDateMillis = calendar.timeInMillis
                    )
                )
            }
        }
        
        // 시간 선택 대화상자
        if (showTimePickerDialog) {
            val calendar = Calendar.getInstance()
            AlertDialog(
                onDismissRequest = { showTimePickerDialog = false },
                title = { Text("시간 선택") },
                text = {
                    TimePicker(
                        state = rememberTimePickerState(
                            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
                            initialMinute = calendar.get(Calendar.MINUTE)
                        )
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedTime = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
                            showTimePickerDialog = false
                        }
                    ) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePickerDialog = false }) {
                        Text("취소")
                    }
                }
            )
        }
        
        // 커스텀 대화상자
        if (showCustomDialog) {
            AlertDialog(
                onDismissRequest = { showCustomDialog = false },
                title = { Text("커스텀 대화상자") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("이것은 커스텀 대화상자입니다.")
                        Text("여러 줄의 텍스트와 복잡한 레이아웃을 포함할 수 있습니다.")
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "정보",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text("추가 정보 표시")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showCustomDialog = false }) {
                        Text("확인")
                    }
                }
            )
        }
        
        // 팝업 메뉴
        if (showPopupMenu) {
            var expanded by remember { mutableStateOf(true) }
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { 
                    expanded = false
                    showPopupMenu = false
                }
            ) {
                DropdownMenuItem(
                    text = { Text("옵션 1") },
                    onClick = {
                        expanded = false
                        showPopupMenu = false
                        // 옵션 1 처리
                    },
                    leadingIcon = { Icon(Icons.Default.Star, "별") }
                )
                DropdownMenuItem(
                    text = { Text("옵션 2") },
                    onClick = {
                        expanded = false
                        showPopupMenu = false
                        // 옵션 2 처리
                    },
                    leadingIcon = { Icon(Icons.Default.Favorite, "하트") }
                )
                DropdownMenuItem(
                    text = { Text("옵션 3") },
                    onClick = {
                        expanded = false
                        showPopupMenu = false
                        // 옵션 3 처리
                    },
                    leadingIcon = { Icon(Icons.Default.ThumbUp, "좋아요") }
                )
            }
        }
    }
}

// 알림 채널 생성 함수
private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "dialog_popup_notification_channel"
        val channelName = "대화상자 & 팝업 & 알림 채널"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "대화상자, 팝업, 알림 테스트를 위한 채널"
        }
        
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

// 기본 알림 표시 함수
private fun showBasicNotification(context: Context, notificationId: Int) {
    // 권한 체크
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
    }
    
    val channelId = "dialog_popup_notification_channel"
    
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("기본 알림")
        .setContentText("이것은 기본 알림입니다. ID: $notificationId")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
    
    try {
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    } catch (e: SecurityException) {
        // 권한이 없거나 알림이 차단된 경우
        android.util.Log.e("Notification", "알림 전송 실패: ${e.message}")
    }
}

// 액션 알림 표시 함수
private fun showActionNotification(context: Context, notificationId: Int) {
    // 권한 체크
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
    }
    
    val channelId = "dialog_popup_notification_channel"
    
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle("액션 알림")
        .setContentText("버튼이 포함된 알림입니다.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .addAction(
            android.R.drawable.ic_menu_send,
            "확인",
            null
        )
        .addAction(
            android.R.drawable.ic_menu_close_clear_cancel,
            "취소",
            null
        )
    
    try {
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    } catch (e: SecurityException) {
        // 권한이 없거나 알림이 차단된 경우
        android.util.Log.e("Notification", "액션 알림 전송 실패: ${e.message}")
    }
}

// 진행률 알림 표시 함수
private fun showProgressNotification(context: Context, notificationId: Int) {
    // 권한 체크
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
    }
    
    val channelId = "dialog_popup_notification_channel"
    
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_popup_sync)
        .setContentTitle("진행률 알림")
        .setContentText("작업이 진행 중입니다...")
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
        .setProgress(100, 50, false) // 진행률 50%
    
    try {
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
        
        // 3초 후 진행률 100%로 업데이트
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val updatedBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_popup_sync)
                .setContentTitle("진행률 알림")
                .setContentText("작업이 완료되었습니다!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setProgress(100, 100, false)
                .setAutoCancel(true)
            
            try {
                with(NotificationManagerCompat.from(context)) {
                    notify(notificationId, updatedBuilder.build())
                }
            } catch (e: SecurityException) {
                android.util.Log.e("Notification", "진행률 알림 업데이트 실패: ${e.message}")
            }
        }, 3000)
    } catch (e: SecurityException) {
        // 권한이 없거나 알림이 차단된 경우
        android.util.Log.e("Notification", "진행률 알림 전송 실패: ${e.message}")
    }
}
