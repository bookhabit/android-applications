package com.example.android_applicatoins.screens.native

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryMonitorScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    var batteryLevel by remember { mutableStateOf(0) }
    var isCharging by remember { mutableStateOf(false) }
    var batteryHealth by remember { mutableStateOf("") }
    var batteryTemperature by remember { mutableStateOf(0f) }
    var batteryVoltage by remember { mutableStateOf(0) }

    val batteryReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_BATTERY_CHANGED -> {
                        batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                        if (batteryLevel != -1 && scale != -1) {
                            batteryLevel = (batteryLevel * 100 / scale.toFloat()).toInt()
                        }
                        
                        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                   status == BatteryManager.BATTERY_STATUS_FULL
                        
                        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
                        batteryHealth = when (health) {
                            BatteryManager.BATTERY_HEALTH_GOOD -> "양호"
                            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "과열"
                            BatteryManager.BATTERY_HEALTH_DEAD -> "수명 종료"
                            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "과전압"
                            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "알 수 없는 오류"
                            else -> "알 수 없음"
                        }
                        
                        batteryTemperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f
                        batteryVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
                    }
                }
            }
        }
    }

    DisposableEffect(context) {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }
        context.registerReceiver(batteryReceiver, filter)
        
        onDispose {
            context.unregisterReceiver(batteryReceiver)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("배터리 모니터", fontWeight = FontWeight.Bold) },
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
            // 배터리 아이콘과 퍼센트
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            batteryLevel <= 15 -> Color(0xFFF44336) // 빨간색 (위험)
                            batteryLevel <= 30 -> Color(0xFFFF9800) // 주황색 (경고)
                            else -> Color(0xFF4CAF50) // 초록색 (정상)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                        contentDescription = "배터리",
                        modifier = Modifier.size(80.dp),
                        tint = Color.White
                    )
                    Text(
                        text = "$batteryLevel%",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 충전 상태
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCharging) Color(0xFFE8F5E8) else Color(0xFFF5F5F5)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isCharging) Icons.Default.Power else Icons.Default.BatteryStd,
                        contentDescription = "충전 상태",
                        tint = if (isCharging) Color(0xFF4CAF50) else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isCharging) "충전 중" else "방전 중",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCharging) Color(0xFF4CAF50) else Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 배터리 정보 카드들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 배터리 상태
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = "배터리 상태",
                            tint = Color.Blue
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "상태",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = batteryHealth,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // 온도
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Thermostat,
                            contentDescription = "온도",
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "온도",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "${batteryTemperature}°C",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 전압
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "전압",
                        tint = Color.Yellow
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "전압",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${batteryVoltage}mV",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
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
                        text = "ACTION_BATTERY_CHANGED 방송을 수신하여 배터리 상태를 실시간으로 모니터링합니다. 배터리가 15% 이하로 떨어지면 빨간색으로 표시됩니다.",
                        fontSize = 14.sp,
                        color = Color(0xFF1976D2)
                    )
                }
            }
        }
    }
}
