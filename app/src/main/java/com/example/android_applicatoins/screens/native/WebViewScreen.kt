package com.example.android_applicatoins.screens.native

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebSettings
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    var webViewUrl by remember { mutableStateOf("https://www.google.com") }
    var isLoading by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showWebViewInfo by remember { mutableStateOf(false) }
    var showWebViewTest by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WebView 테스트") },
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
            // WebView 개념 설명 섹션
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
                                text = "📱 WebView 개념",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showWebViewInfo = !showWebViewInfo }) {
                                Icon(
                                    if (showWebViewInfo) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "펼치기/접기"
                                )
                            }
                        }
                        
                        AnimatedVisibility(
                            visible = showWebViewInfo,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // WebView란
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Text(
                                            text = "🌐 WebView란?",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "• 앱 내에서 웹 페이지를 보여주는 뷰(View)\n" +
                                                    "• 웹 기술(HTML, CSS, JS)로 만든 콘텐츠를 네이티브 앱에서 표시\n" +
                                                    "• 하이브리드 앱 개발의 핵심 요소",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                                
                                // 사용 분야
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Text(
                                            text = "🎯 사용 분야",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "• 뉴스/게시판 같은 콘텐츠 앱\n" +
                                                    "• 하이브리드 앱 (예: 카카오페이 일부 화면)\n" +
                                                    "• 웹 기반 기능을 앱에 통합\n" +
                                                    "• HTML5 게임이나 웹 애플리케이션",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                                
                                // 한계
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Text(
                                            text = "⚠️ 한계점",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "• 네이티브 기능 접근 제약\n" +
                                                    "• 성능 문제 (특히 애니메이션/게임)\n" +
                                                    "• 오프라인 지원 제한\n" +
                                                    "• 플랫폼별 동작 차이",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                                
                                // 사용 방법
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Text(
                                            text = "🔧 사용 방법",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "• WebView 위젯을 레이아웃에 추가\n" +
                                                    "• JavaScript 활성화 설정\n" +
                                                    "• WebViewClient로 페이지 로딩 제어\n" +
                                                    "• loadUrl() 메서드로 웹페이지 로드",
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
            
            // WebView 테스트 섹션
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
                                text = "🌐 WebView 테스트",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showWebViewTest = !showWebViewTest }) {
                                Icon(
                                    if (showWebViewTest) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "펼치기/접기"
                                )
                            }
                        }
                        
                        AnimatedVisibility(
                            visible = showWebViewTest,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // URL 입력
                                OutlinedTextField(
                                    value = webViewUrl,
                                    onValueChange = { webViewUrl = it },
                                    label = { Text("웹페이지 URL") },
                                    placeholder = { Text("https://www.google.com") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                // 로딩 상태 표시
                                if (isLoading) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("페이지 로딩 중...")
                                    }
                                }
                                
                                // 에러 상태 표시
                                if (hasError) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp)
                                        ) {
                                            Text(
                                                text = "❌ 로딩 실패",
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onErrorContainer
                                            )
                                            Text(
                                                text = errorMessage,
                                                modifier = Modifier.padding(top = 4.dp),
                                                color = MaterialTheme.colorScheme.onErrorContainer
                                            )
                                        }
                                    }
                                }
                                
                                // WebView
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(400.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    AndroidView(
                                        factory = { context ->
                                            WebView(context).apply {
                                                webViewClient = object : WebViewClient() {
                                                    override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                                        super.onPageStarted(view, url, favicon)
                                                        isLoading = true
                                                        hasError = false
                                                    }
                                                    
                                                    override fun onPageFinished(view: WebView?, url: String?) {
                                                        super.onPageFinished(view, url)
                                                        isLoading = false
                                                    }
                                                    
                                                    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                                                        super.onReceivedError(view, request, error)
                                                        isLoading = false
                                                        hasError = true
                                                        errorMessage = "페이지 로딩 중 오류가 발생했습니다: ${error?.description}"
                                                    }
                                                }
                                                
                                                settings.apply {
                                                    javaScriptEnabled = true
                                                    domStorageEnabled = true
                                                    loadWithOverviewMode = true
                                                    useWideViewPort = true
                                                    builtInZoomControls = true
                                                    displayZoomControls = false
                                                }
                                            }
                                        },
                                        update = { webView ->
                                            webView.loadUrl(webViewUrl)
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                
                                // 빠른 링크 버튼들
                                Text(
                                    text = "빠른 링크:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { webViewUrl = "https://www.google.com" },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Google", style = MaterialTheme.typography.bodySmall)
                                    }
                                    
                                    Button(
                                        onClick = { webViewUrl = "https://www.naver.com" },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Naver", style = MaterialTheme.typography.bodySmall)
                                    }
                                    
                                    Button(
                                        onClick = { webViewUrl = "https://www.github.com" },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("GitHub", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { webViewUrl = "https://httpbin.org/html" },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("HTML 테스트", style = MaterialTheme.typography.bodySmall)
                                    }
                                    
                                    Button(
                                        onClick = { webViewUrl = "https://httpbin.org/json" },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("JSON 테스트", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // WebView 예제 코드 섹션
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "💻 WebView 예제 코드",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // XML 레이아웃 예제
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "XML 레이아웃:",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "<WebView\n" +
                                            "    android:id=\"@+id/webView\"\n" +
                                            "    android:layout_width=\"match_parent\"\n" +
                                            "    android:layout_height=\"match_parent\" />",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Kotlin 코드 예제
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "Kotlin 코드:",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "val webView = findViewById<WebView>(R.id.webView)\n" +
                                            "webView.settings.javaScriptEnabled = true\n" +
                                            "webView.loadUrl(\"https://www.example.com\")",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Compose 예제
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "Compose 코드:",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "AndroidView(\n" +
                                            "    factory = { context ->\n" +
                                            "        WebView(context)\n" +
                                            "    }\n" +
                                            ")",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
