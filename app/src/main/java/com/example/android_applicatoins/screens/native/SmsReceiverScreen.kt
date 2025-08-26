package com.example.android_applicatoins.screens.native

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.provider.Telephony
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

data class SmsMessage(
    val sender: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsReceiverScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    var hasSmsPermission by remember { mutableStateOf(false) }
    var smsMessages by remember { mutableStateOf(listOf<SmsMessage>()) }
    var isReceiverRegistered by remember { mutableStateOf(false) }

    val smsReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Telephony.Sms.Intents.SMS_RECEIVED_ACTION -> {
                        // SMS 수신 처리
                        val messages = intent.getParcelableArrayExtra("pdus")
                        messages?.forEach { pdu ->
                            val smsMessage = android.telephony.SmsMessage.createFromPdu(pdu as ByteArray)
                            val sender = smsMessage.originatingAddress ?: "알 수 없음"
                            val messageBody = smsMessage.messageBody
                            val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                            
                            val newMessage = SmsMessage(sender, messageBody, timestamp)
                            smsMessages = smsMessages.take(10).toMutableList().apply { add(0, newMessage) }
                        }
                    }
                }
            }
        }
    }

    fun registerSmsReceiver() {
        if (!isReceiverRegistered) {
            val filter = IntentFilter().apply {
                addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
            }
            context.registerReceiver(smsReceiver, filter)
            isReceiverRegistered = true
        }
    }

    fun unregisterSmsReceiver() {
        if (isReceiverRegistered) {
            context.unregisterReceiver(smsReceiver)
            isReceiverRegistered = false
        }
    }

    fun checkSmsPermission() {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECEIVE_SMS
            ) == PackageManager.PERMISSION_GRANTED -> {
                hasSmsPermission = true
                registerSmsReceiver()
            }
            else -> {
                // 권한이 없으면 권한 요청
                hasSmsPermission = false
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasSmsPermission = isGranted
        if (isGranted) {
            registerSmsReceiver()
        }
    }

    fun requestSmsPermission() {
        permissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
    }

    fun markAsRead(index: Int) {
        smsMessages = smsMessages.toMutableList().apply {
            this[index] = this[index].copy(isRead = true)
        }
    }

    fun deleteMessage(index: Int) {
        smsMessages = smsMessages.toMutableList().apply {
            removeAt(index)
        }
    }

    fun simulateSms() {
        val senders = listOf("010-1234-5678", "010-9876-5432", "010-5555-1234")
        val messages = listOf(
            "안녕하세요! 오늘 회의 시간 확인 부탁드립니다.",
            "주문하신 상품이 배송되었습니다.",
            "예약 확인되었습니다. 감사합니다.",
            "비밀번호 변경 요청이 접수되었습니다.",
            "새로운 메시지가 도착했습니다."
        )
        
        val randomSender = senders.random()
        val randomMessage = messages.random()
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        
        val newMessage = SmsMessage(randomSender, randomMessage, timestamp)
        smsMessages = smsMessages.take(10).toMutableList().apply { add(0, newMessage) }
    }

    DisposableEffect(context) {
        checkSmsPermission()
        
        onDispose {
            unregisterSmsReceiver()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SMS 수신 알림", fontWeight = FontWeight.Bold) },
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
        ) {
            // 권한 상태
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (hasSmsPermission) Color(0xFFE8F5E8) else Color(0xFFFFEBEE)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (hasSmsPermission) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = "권한 상태",
                            tint = if (hasSmsPermission) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (hasSmsPermission) "SMS 권한 허용됨" else "SMS 권한 필요",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (hasSmsPermission) Color(0xFF4CAF50) else Color(0xFFF44336)
                            )
                            if (!hasSmsPermission) {
                                Text(
                                    text = "SMS 수신을 위해 권한을 허용해주세요",
                                    fontSize = 14.sp,
                                    color = Color(0xFFF44336)
                                )
                            }
                        }
                    }
                    
                    if (!hasSmsPermission) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { requestSmsPermission() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
                            )
                        ) {
                            Text("권한 요청")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // SMS 시뮬레이션 버튼
            Button(
                onClick = { simulateSms() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Message,
                    contentDescription = "SMS 시뮬레이션",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("SMS 시뮬레이션")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // SMS 메시지 목록
            if (smsMessages.isNotEmpty()) {
                Text(
                    text = "수신된 메시지 (${smsMessages.size}개)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(smsMessages.indices.toList()) { index ->
                        val message = smsMessages[index]
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (message.isRead) Color(0xFFF5F5F5) else Color(0xFFE3F2FD)
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
                                    Text(
                                        text = message.sender,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = message.timestamp,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = message.message,
                                    fontSize = 14.sp,
                                    color = if (message.isRead) Color.Gray else Color.Black
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    if (!message.isRead) {
                                        TextButton(
                                            onClick = { markAsRead(index) }
                                        ) {
                                            Text("읽음 처리")
                                        }
                                    }
                                    
                                    TextButton(
                                        onClick = { deleteMessage(index) }
                                    ) {
                                        Text("삭제")
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // 메시지가 없을 때
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
                            imageVector = Icons.Default.Message,
                            contentDescription = "메시지 없음",
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "수신된 SMS가 없습니다",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "SMS 시뮬레이션 버튼을 눌러보세요",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
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
                        text = "SMS_RECEIVED_ACTION 방송을 수신하여 새 SMS가 도착하면 자동으로 알림을 표시합니다. 권한이 필요하며, 실제 SMS 수신 시에도 동작합니다.",
                        fontSize = 14.sp,
                        color = Color(0xFF1976D2)
                    )
                }
            }
        }
    }
}
