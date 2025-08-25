package com.example.android_applicatoins.screens.basic

import android.content.Intent
import android.net.Uri
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImplicitIntentTestScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("암시적 인텐트 테스트") },
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
                text = "암시적 인텐트 테스트",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Text(
                text = "다양한 암시적 인텐트를 테스트해보세요. 시스템이 적절한 앱을 자동으로 찾아 실행합니다.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 32.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            // 웹 브라우저 관련
            IntentTestSection(
                title = "🌐 웹 브라우저",
                description = "웹사이트를 브라우저로 열기"
            ) {
                IntentTestButton(
                    text = "Google 열기",
                    icon = Icons.Default.Language,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://www.google.com")
                        }
                        context.startActivity(intent)
                    }
                )
                
                IntentTestButton(
                    text = "네이버 열기",
                    icon = Icons.Default.Language,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://www.naver.com")
                        }
                        context.startActivity(intent)
                    }
                )
            }
            
            // 전화 관련
            IntentTestSection(
                title = "📞 전화",
                description = "전화 다이얼러 열기"
            ) {
                IntentTestButton(
                    text = "전화 다이얼러",
                    icon = Icons.Default.Phone,
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:01012345678")
                        }
                        context.startActivity(intent)
                    }
                )
            }
            
            // 문자 메시지
            IntentTestSection(
                title = "💬 문자 메시지",
                description = "SMS 앱으로 문자 보내기"
            ) {
                IntentTestButton(
                    text = "문자 보내기",
                    icon = Icons.Default.Sms,
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("smsto:01012345678")
                            putExtra("sms_body", "안녕하세요!")
                        }
                        context.startActivity(intent)
                    }
                )
            }
            
            // 이메일
            IntentTestSection(
                title = "📧 이메일",
                description = "이메일 앱으로 메일 보내기"
            ) {
                IntentTestButton(
                    text = "이메일 보내기",
                    icon = Icons.Default.Email,
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:test@example.com")
                            putExtra(Intent.EXTRA_SUBJECT, "테스트 메일")
                            putExtra(Intent.EXTRA_TEXT, "안녕하세요! 이것은 테스트 메일입니다.")
                        }
                        context.startActivity(intent)
                    }
                )
            }
            
            // 지도
            IntentTestSection(
                title = "🗺️ 지도",
                description = "지도 앱으로 위치 보기"
            ) {
                IntentTestButton(
                    text = "서울시청 위치",
                    icon = Icons.Default.LocationOn,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("geo:37.5665,126.9780?q=서울시청")
                        }
                        context.startActivity(intent)
                    }
                )
                
                IntentTestButton(
                    text = "경로 검색",
                    icon = Icons.Default.Directions,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("google.navigation:q=서울시청")
                        }
                        context.startActivity(intent)
                    }
                )
            }
            
            // 파일/갤러리
            IntentTestSection(
                title = "📁 파일 & 갤러리",
                description = "파일 선택 및 갤러리 열기"
            ) {
                IntentTestButton(
                    text = "이미지 선택",
                    icon = Icons.Default.Image,
                    onClick = {
                        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                            type = "image/*"
                        }
                        context.startActivity(intent)
                    }
                )
                
                IntentTestButton(
                    text = "파일 선택",
                    icon = Icons.Default.Folder,
                    onClick = {
                        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                            type = "*/*"
                        }
                        context.startActivity(intent)
                    }
                )
            }
            
            // 설정
            IntentTestSection(
                title = "⚙️ 설정",
                description = "시스템 설정 화면 열기"
            ) {
                IntentTestButton(
                    text = "설정",
                    icon = Icons.Default.Settings,
                    onClick = {
                        val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
                        context.startActivity(intent)
                    }
                )
                
                IntentTestButton(
                    text = "Wi-Fi 설정",
                    icon = Icons.Default.Wifi,
                    onClick = {
                        val intent = Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)
                        context.startActivity(intent)
                    }
                )
                
                IntentTestButton(
                    text = "앱 정보",
                    icon = Icons.Default.Info,
                    onClick = {
                        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                )
            }
            
            // 공유
            IntentTestSection(
                title = "📤 공유",
                description = "다른 앱으로 데이터 공유하기"
            ) {
                IntentTestButton(
                    text = "텍스트 공유",
                    icon = Icons.Default.Share,
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "안녕하세요! 이것은 공유할 텍스트입니다.")
                            putExtra(Intent.EXTRA_SUBJECT, "공유 제목")
                        }
                        context.startActivity(Intent.createChooser(intent, "공유하기"))
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 코드 예시
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
                        text = "암시적 인텐트 코드 예시:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "// 웹사이트 열기",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Text(
                        text = "Intent intent = new Intent(Intent.ACTION_VIEW);",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Text(
                        text = "intent.setData(Uri.parse(\"https://www.google.com\"));",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Text(
                        text = "startActivity(intent);",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun IntentTestSection(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            content()
        }
    }
}

@Composable
fun IntentTestButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier
                .size(20.dp)
                .padding(end = 8.dp)
        )
        Text(text)
    }
}
