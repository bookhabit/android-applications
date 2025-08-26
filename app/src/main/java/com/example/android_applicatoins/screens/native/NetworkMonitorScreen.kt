package com.example.android_applicatoins.screens.native

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
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
fun NetworkMonitorScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    var networkType by remember { mutableStateOf("") }
    var isConnected by remember { mutableStateOf(false) }
    var networkStrength by remember { mutableStateOf(0) }
    var lastNetworkChange by remember { mutableStateOf("") }
    var networkHistory by remember { mutableStateOf(listOf<String>()) }

    val connectivityManager = remember {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun updateNetworkStatus(context: Context?) {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        cm?.let { manager ->
            val activeNetwork = manager.activeNetwork
            val networkCapabilities = manager.getNetworkCapabilities(activeNetwork)
            
            isConnected = networkCapabilities != null
            
            networkType = when {
                networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "Wi-Fi"
                networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "모바일 데이터"
                networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "이더넷"
                networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) == true -> "블루투스"
                else -> "연결 없음"
            }
            
            // 네트워크 강도 (Wi-Fi의 경우)
            if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                networkStrength = when {
                    networkCapabilities.linkDownstreamBandwidthKbps > 100000 -> 100
                    networkCapabilities.linkDownstreamBandwidthKbps > 50000 -> 75
                    networkCapabilities.linkDownstreamBandwidthKbps > 10000 -> 50
                    else -> 25
                }
            } else {
                networkStrength = if (isConnected) 100 else 0
            }
        }
    }

    val networkReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    ConnectivityManager.CONNECTIVITY_ACTION -> {
                        updateNetworkStatus(context)
                    }
                }
            }
        }
    }

    val networkCallback = remember {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                updateNetworkStatus(context)
                val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                val change = "연결됨 - $timestamp"
                lastNetworkChange = change
                networkHistory = networkHistory.take(5).toMutableList().apply { add(0, change) }
            }

            override fun onLost(network: Network) {
                updateNetworkStatus(context)
                val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                val change = "연결 끊김 - $timestamp"
                lastNetworkChange = change
                networkHistory = networkHistory.take(5).toMutableList().apply { add(0, change) }
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                updateNetworkStatus(context)
            }
        }
    }

    DisposableEffect(context) {
        // Android 7.0 이전 버전용 방송수신자
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            val filter = IntentFilter().apply {
                addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            }
            context.registerReceiver(networkReceiver, filter)
        }
        
        // Android 7.0 이후 버전용 네트워크 콜백
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        
        // 초기 상태 확인
        updateNetworkStatus(context)
        
        onDispose {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                context.unregisterReceiver(networkReceiver)
            }
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("네트워크 모니터", fontWeight = FontWeight.Bold) },
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
            // 네트워크 상태 아이콘
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            !isConnected -> Color(0xFFF44336) // 빨간색 (연결 없음)
                            networkType == "Wi-Fi" -> Color(0xFF2196F3) // 파란색 (Wi-Fi)
                            networkType == "모바일 데이터" -> Color(0xFF4CAF50) // 초록색 (모바일)
                            else -> Color(0xFF9C27B0) // 보라색 (기타)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = when {
                            !isConnected -> Icons.Default.WifiOff
                            networkType == "Wi-Fi" -> Icons.Default.Wifi
                            networkType == "모바일 데이터" -> Icons.Default.SignalCellular4Bar
                            else -> Icons.Default.NetworkCheck
                        },
                        contentDescription = "네트워크",
                        modifier = Modifier.size(80.dp),
                        tint = Color.White
                    )
                    Text(
                        text = networkType,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 연결 상태
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isConnected) Color(0xFFE8F5E8) else Color(0xFFFFEBEE)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isConnected) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = "연결 상태",
                        tint = if (isConnected) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isConnected) "연결됨" else "연결 없음",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isConnected) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 네트워크 정보 카드들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 네트워크 타입
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
                            imageVector = Icons.Default.NetworkCheck,
                            contentDescription = "네트워크 타입",
                            tint = Color.Blue
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "타입",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = networkType,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // 네트워크 강도
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
                            imageVector = Icons.Default.SignalCellular4Bar,
                            contentDescription = "네트워크 강도",
                            tint = Color.Green
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "강도",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "$networkStrength%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 마지막 변경사항
            if (lastNetworkChange.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "마지막 변경사항",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFFF9800)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = lastNetworkChange,
                            fontSize = 14.sp,
                            color = Color(0xFFFF9800)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // 네트워크 변경 히스토리
            if (networkHistory.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "변경 히스토리",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        networkHistory.forEach { history ->
                            Text(
                                text = "• $history",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
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
                        text = "CONNECTIVITY_ACTION 방송과 NetworkCallback을 사용하여 네트워크 상태 변화를 실시간으로 감지합니다. Wi-Fi와 모바일 데이터 전환을 자동으로 감지합니다.",
                        fontSize = 14.sp,
                        color = Color(0xFF1976D2)
                    )
                }
            }
        }
    }
}
