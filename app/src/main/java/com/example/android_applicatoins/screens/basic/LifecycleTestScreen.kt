package com.example.android_applicatoins.screens.basic

import android.content.Intent
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
import com.example.android_applicatoins.SecondActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifecycleTestScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("생애주기 테스트") },
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
                text = "액티비티 생애주기 테스트",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Text(
                text = "다양한 생애주기 시나리오를 테스트해보세요. Logcat에서 생애주기 콜백을 확인할 수 있습니다.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 32.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            // 생애주기 설명
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
                        text = "📱 액티비티 생애주기 단계",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    LifecycleStep(
                        step = "1. onCreate()",
                        description = "액티비티가 생성될 때 호출됩니다."
                    )
                    
                    LifecycleStep(
                        step = "2. onStart()",
                        description = "액티비티가 사용자에게 보이기 시작할 때 호출됩니다."
                    )
                    
                    LifecycleStep(
                        step = "3. onResume()",
                        description = "액티비티가 사용자와 상호작용할 수 있을 때 호출됩니다."
                    )
                    
                    LifecycleStep(
                        step = "4. onPause()",
                        description = "다른 액티비티가 포커스를 얻을 때 호출됩니다."
                    )
                    
                    LifecycleStep(
                        step = "5. onStop()",
                        description = "액티비티가 더 이상 보이지 않을 때 호출됩니다."
                    )
                    
                    LifecycleStep(
                        step = "6. onDestroy()",
                        description = "액티비티가 완전히 소멸될 때 호출됩니다."
                    )
                    
                    LifecycleStep(
                        step = "7. onRestart()",
                        description = "중지된 액티비티가 다시 시작될 때 호출됩니다."
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 테스트 버튼들
            Text(
                text = "🧪 생애주기 테스트",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // SecondActivity 열기
            LifecycleTestButton(
                text = "SecondActivity 열기",
                icon = Icons.Default.OpenInNew,
                description = "새로운 액티비티를 열어 생애주기 변화를 관찰합니다.",
                onClick = {
                    val intent = Intent(context, SecondActivity::class.java)
                    context.startActivity(intent)
                }
            )
            
            // 홈 버튼 시뮬레이션
            LifecycleTestButton(
                text = "홈 버튼 효과",
                icon = Icons.Default.Home,
                description = "홈 버튼을 누른 것과 같은 효과를 시뮬레이션합니다.",
                onClick = {
                    val homeIntent = Intent(Intent.ACTION_MAIN)
                    homeIntent.addCategory(Intent.CATEGORY_HOME)
                    homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(homeIntent)
                }
            )
            
            // 설정 앱 열기
            LifecycleTestButton(
                text = "설정 앱 열기",
                icon = Icons.Default.Settings,
                description = "설정 앱을 열어 현재 앱이 백그라운드로 이동하는 것을 관찰합니다.",
                onClick = {
                    val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
                    context.startActivity(intent)
                }
            )
            
            // 알림 패널 열기
            LifecycleTestButton(
                text = "알림 패널 열기",
                icon = Icons.Default.Notifications,
                description = "알림 패널을 열어 앱이 일시적으로 일시정지되는 것을 관찰합니다.",
                onClick = {
                    // 알림 패널을 열기 위해 시스템 UI를 조작
                    // 실제로는 사용자가 직접 알림 패널을 열어야 합니다
                    // 여기서는 설명만 제공
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 로그 확인 방법
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
                        text = "📋 로그 확인 방법",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "1. Android Studio의 Logcat 창을 엽니다",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Text(
                        text = "2. 검색 필터에 'Lifecycle'을 입력합니다",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Text(
                        text = "3. 위의 테스트 버튼들을 클릭하여 생애주기 변화를 관찰합니다",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Text(
                        text = "4. 각 단계에서 호출되는 콜백 메서드를 확인합니다",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 주의사항
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "⚠️ 주의사항",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "• 실제 기기에서 테스트하는 것이 더 정확합니다",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Text(
                        text = "• 에뮬레이터에서는 일부 생애주기가 다르게 동작할 수 있습니다",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Text(
                        text = "• 시스템 설정에 따라 생애주기 동작이 달라질 수 있습니다",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
fun LifecycleStep(
    step: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = step,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(120.dp)
        )
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun LifecycleTestButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit
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
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
