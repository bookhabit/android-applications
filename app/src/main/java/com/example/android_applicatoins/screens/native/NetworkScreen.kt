package com.example.android_applicatoins.screens.native

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

data class NetworkInfo(
    val isConnected: Boolean,
    val networkType: String,
    val networkState: String,
    val timestamp: String
)

data class DownloadResult(
    val success: Boolean,
    val message: String,
    val data: String = ""
)

data class XmlParseResult(
    val success: Boolean,
    val message: String,
    val parsedData: String = ""
)

// XML 문서 파싱 함수
fun parseXmlDocument(document: Document): String {
    val root = document.documentElement
    val rootName = root.tagName
    var result = "루트 요소: $rootName\n\n"
    
    val children = root.childNodes
    for (i in 0 until children.length) {
        val child = children.item(i)
        if (child.nodeType == org.w3c.dom.Node.ELEMENT_NODE) {
            val element = child as Element
            val tagName = element.tagName
            val textContent = element.textContent
            result += "$tagName: $textContent\n"
        }
    }
    
    return result
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var networkInfo by remember { mutableStateOf(NetworkInfo(false, "연결 없음", "DISCONNECTED", "")) }
    var networkHistory by remember { mutableStateOf(listOf<NetworkInfo>()) }
    var downloadResult by remember { mutableStateOf<DownloadResult?>(null) }
    var xmlParseResult by remember { mutableStateOf<XmlParseResult?>(null) }
    var testUrl by remember { mutableStateOf("https://httpbin.org/xml") }
    var xmlTestData by remember { mutableStateOf("") }
    
    // 토글 상태들
    var showNetworkInfo by remember { mutableStateOf(false) }
    var showWebAppInfo by remember { mutableStateOf(false) }
    var showXmlInfo by remember { mutableStateOf(false) }
    var showDownloadTest by remember { mutableStateOf(false) }
    var showXmlTest by remember { mutableStateOf(false) }
    
    val connectivityManager = remember {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    
    // 네트워크 상태 업데이트 함수
    fun updateNetworkStatus() {
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        
        val isConnected = networkCapabilities != null
        val networkType = when {
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "Wi-Fi"
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "모바일 데이터"
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "이더넷"
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) == true -> "블루투스"
            else -> "연결 없음"
        }
        
        val networkState = if (isConnected) "CONNECTED" else "DISCONNECTED"
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        
        val newNetworkInfo = NetworkInfo(isConnected, networkType, networkState, timestamp)
        networkInfo = newNetworkInfo
        
        // 히스토리에 추가 (최대 10개)
        networkHistory = (listOf(newNetworkInfo) + networkHistory).take(10)
    }
    
    // 네트워크 콜백
    val networkCallback = remember {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                updateNetworkStatus()
            }
            
            override fun onLost(network: Network) {
                updateNetworkStatus()
            }
        }
    }
    
    // 네트워크 모니터링 시작
    LaunchedEffect(Unit) {
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        updateNetworkStatus()
    }
    
    // 파일 다운로드 테스트
    fun testFileDownload() {
        scope.launch {
            downloadResult = try {
                val result = withContext(Dispatchers.IO) {
                    val url = URL(testUrl)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000
                    
                    val responseCode = connection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val inputStream = connection.inputStream
                        val content = inputStream.bufferedReader().use { it.readText() }
                        inputStream.close()
                        connection.disconnect()
                        
                        DownloadResult(true, "다운로드 성공! 응답 코드: $responseCode", content)
                    } else {
                        connection.disconnect()
                        DownloadResult(false, "다운로드 실패. 응답 코드: $responseCode")
                    }
                }
                result
            } catch (e: Exception) {
                DownloadResult(false, "다운로드 중 오류 발생: ${e.message}")
            }
        }
    }
    
    // XML 파싱 테스트
    fun testXmlParsing() {
        if (xmlTestData.isBlank()) {
            xmlParseResult = XmlParseResult(false, "XML 데이터를 먼저 입력해주세요.")
            return
        }
        
        scope.launch {
            xmlParseResult = try {
                val result = withContext(Dispatchers.IO) {
                    val factory = DocumentBuilderFactory.newInstance()
                    val builder = factory.newDocumentBuilder()
                    val inputSource = org.xml.sax.InputSource(StringReader(xmlTestData))
                    val document = builder.parse(inputSource)
                    
                    val parsedData = parseXmlDocument(document)
                    XmlParseResult(true, "XML 파싱 성공!", parsedData)
                }
                result
            } catch (e: Exception) {
                XmlParseResult(false, "XML 파싱 중 오류 발생: ${e.message}")
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("네트워크 & 웹 테스트") },
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
            // 네트워킹 상태 조회 섹션
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
                                text = "🌐 네트워킹 상태 조회",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showNetworkInfo = !showNetworkInfo }) {
                                Icon(
                                    if (showNetworkInfo) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "펼치기/접기"
                                )
                            }
                        }
                        
                        AnimatedVisibility(
                            visible = showNetworkInfo,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "• getActiveNetworkInfo(): 현재 활성화된 네트워크 정보 반환\n" +
                                            "• NetworkInfo 객체: 네트워크 상태를 표현하는 객체\n" +
                                            "• 주요 메서드: isConnected(), getType(), getState()",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                
                                // 현재 네트워크 상태
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("연결 상태:")
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (networkInfo.isConnected) Color.Green else Color.Red
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (networkInfo.isConnected) "연결됨" else "연결 안됨",
                                            color = if (networkInfo.isConnected) Color.Green else Color.Red,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("네트워크 타입:")
                                    Text(networkInfo.networkType, fontWeight = FontWeight.Medium)
                                }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("상태:")
                                    Text(networkInfo.networkState, fontWeight = FontWeight.Medium)
                                }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("마지막 업데이트:")
                                    Text(networkInfo.timestamp, fontWeight = FontWeight.Medium)
                                }
                                
                                // 네트워크 히스토리
                                if (networkHistory.isNotEmpty()) {
                                    Text(
                                        text = "네트워크 변경 히스토리:",
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.padding(top = 16.dp)
                                    )
                                    
                                    networkHistory.take(5).forEach { info ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = info.timestamp,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Text(
                                                text = "${info.networkType} - ${info.networkState}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (info.isConnected) Color.Green else Color.Red
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // 웹앱 개념 설명 섹션
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
                                text = "📱 웹앱 개념",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showWebAppInfo = !showWebAppInfo }) {
                                Icon(
                                    if (showWebAppInfo) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "펼치기/접기"
                                )
                            }
                        }
                        
                        AnimatedVisibility(
                            visible = showWebAppInfo,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // 네이티브 앱
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Text(
                                            text = "🏗️ 네이티브 앱 (Native App)",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "• 플랫폼 언어로 직접 개발 (Java/Kotlin, Swift/Objective-C)\n" +
                                                    "• 장점: 성능 최고, 디바이스 API 접근 가능\n" +
                                                    "• 단점: 플랫폼마다 따로 개발 필요",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                                
                                // 웹앱
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Text(
                                            text = "🌐 웹앱 (Web App)",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "• 웹 브라우저에서 실행 (HTML, CSS, JS 기반)\n" +
                                                    "• 장점: 배포 쉬움, 크로스플랫폼 지원\n" +
                                                    "• 단점: 디바이스 기능 접근 제한, 성능 제약",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                                
                                // 하이브리드 앱
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Text(
                                            text = "🔗 하이브리드 앱 (Hybrid App)",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "• 웹 기술 + 네이티브 컨테이너 (WebView)\n" +
                                                    "• 장점: 크로스플랫폼 + 일부 네이티브 API 접근\n" +
                                                    "• 단점: 네이티브 앱보다 성능 떨어짐",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                                
                                // WebView 위젯
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Text(
                                            text = "📱 WebView 위젯",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "• 앱 내에서 웹 페이지를 보여주는 뷰\n" +
                                                    "• 사용 분야: 뉴스/게시판, 하이브리드 앱\n" +
                                                    "• 한계: 네이티브 기능 접근 제약, 성능 문제",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // XML 처리 섹션
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
                                text = "📄 XML 처리",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showXmlInfo = !showXmlInfo }) {
                                Icon(
                                    if (showXmlInfo) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "펼치기/접기"
                                )
                            }
                        }
                        
                        AnimatedVisibility(
                            visible = showXmlInfo,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "• XML: 데이터 교환용 마크업 언어\n" +
                                            "• DOM(Document Object Model): XML을 트리 구조로 메모리에 로드\n" +
                                            "• 각 태그를 노드(Node)로 접근 가능",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                
                                Text(
                                    text = "예시 XML 데이터:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Text(
                                        text = "<users>\n" +
                                                "  <user id=\"1\">\n" +
                                                "    <name>홍길동</name>\n" +
                                                "    <email>hong@test.com</email>\n" +
                                                "  </user>\n" +
                                                "</users>",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // 파일 다운로드 테스트 섹션
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
                                text = "⬇️ 파일 다운로드 테스트",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showDownloadTest = !showDownloadTest }) {
                                Icon(
                                    if (showDownloadTest) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "펼치기/접기"
                                )
                            }
                        }
                        
                        AnimatedVisibility(
                            visible = showDownloadTest,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "• HttpURLConnection: 기본 HTTP 통신 클래스\n" +
                                            "• NetworkOnMainThreadException: 메인 스레드에서 네트워크 작업 시 발생\n" +
                                            "• 해결: AsyncTask, Thread, Coroutine 등 별도 스레드에서 실행",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                OutlinedTextField(
                                    value = testUrl,
                                    onValueChange = { testUrl = it },
                                    label = { Text("테스트 URL") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                Button(
                                    onClick = { testFileDownload() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("다운로드 테스트")
                                }
                                
                                downloadResult?.let { result ->
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (result.success) 
                                                MaterialTheme.colorScheme.primaryContainer 
                                            else 
                                                MaterialTheme.colorScheme.errorContainer
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp)
                                        ) {
                                            Text(
                                                text = if (result.success) "✅ 성공" else "❌ 실패",
                                                fontWeight = FontWeight.Bold,
                                                color = if (result.success) 
                                                    MaterialTheme.colorScheme.onPrimaryContainer 
                                                else 
                                                    MaterialTheme.colorScheme.onErrorContainer
                                            )
                                            Text(
                                                text = result.message,
                                                modifier = Modifier.padding(top = 4.dp),
                                                color = if (result.success) 
                                                    MaterialTheme.colorScheme.onPrimaryContainer 
                                                else 
                                                    MaterialTheme.colorScheme.onErrorContainer
                                            )
                                            if (result.data.isNotEmpty()) {
                                                Text(
                                                    text = "데이터 길이: ${result.data.length} 문자",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(top = 4.dp),
                                                    color = if (result.success) 
                                                        MaterialTheme.colorScheme.onPrimaryContainer 
                                                    else 
                                                        MaterialTheme.colorScheme.onErrorContainer
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
            
            // XML 파싱 테스트 섹션
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
                                text = "🔍 XML 파싱 테스트",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showXmlTest = !showXmlTest }) {
                                Icon(
                                    if (showXmlTest) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "펼치기/접기"
                                )
                            }
                        }
                        
                        AnimatedVisibility(
                            visible = showXmlTest,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "• DocumentBuilderFactory: XML 파서 생성\n" +
                                            "• DocumentBuilder: XML 문서 파싱\n" +
                                            "• DOM 트리 구조로 XML 데이터 접근",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                OutlinedTextField(
                                    value = xmlTestData,
                                    onValueChange = { xmlTestData = it },
                                    label = { Text("XML 데이터 입력") },
                                    placeholder = { Text("<users><user><name>홍길동</name></user></users>") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3
                                )
                                
                                Button(
                                    onClick = { testXmlParsing() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("XML 파싱 테스트")
                                }
                                
                                xmlParseResult?.let { result ->
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (result.success) 
                                                MaterialTheme.colorScheme.primaryContainer 
                                            else 
                                                MaterialTheme.colorScheme.errorContainer
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp)
                                        ) {
                                            Text(
                                                text = if (result.success) "✅ 파싱 성공" else "❌ 파싱 실패",
                                                fontWeight = FontWeight.Bold,
                                                color = if (result.success) 
                                                    MaterialTheme.colorScheme.onPrimaryContainer 
                                                else 
                                                    MaterialTheme.colorScheme.onErrorContainer
                                            )
                                            Text(
                                                text = result.message,
                                                modifier = Modifier.padding(top = 4.dp),
                                                color = if (result.success) 
                                                    MaterialTheme.colorScheme.onPrimaryContainer 
                                                else 
                                                    MaterialTheme.colorScheme.onErrorContainer
                                            )
                                            if (result.parsedData.isNotEmpty()) {
                                                Text(
                                                    text = "파싱 결과:",
                                                    fontWeight = FontWeight.Medium,
                                                    modifier = Modifier.padding(top = 8.dp),
                                                    color = if (result.success) 
                                                        MaterialTheme.colorScheme.onPrimaryContainer 
                                                    else 
                                                        MaterialTheme.colorScheme.onErrorContainer
                                                )
                                                Text(
                                                    text = result.parsedData,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(top = 4.dp),
                                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                                    color = if (result.success) 
                                                        MaterialTheme.colorScheme.onPrimaryContainer 
                                                    else 
                                                        MaterialTheme.colorScheme.onErrorContainer
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
    }
}
