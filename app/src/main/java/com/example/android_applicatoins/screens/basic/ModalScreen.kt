package com.example.android_applicatoins.screens.basic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalScreen(
    onBackPressed: () -> Unit
) {
    var showBasicModal by remember { mutableStateOf(false) }
    var showFormModal by remember { mutableStateOf(false) }
    var showCustomModal by remember { mutableStateOf(false) }
    var showFullScreenModal by remember { mutableStateOf(false) }
    var modalResult by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("모달") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "모달 예제",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // 기본 모달
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "기본 모달",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "간단한 정보를 표시하는 기본 모달입니다.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Button(
                        onClick = { showBasicModal = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Info, "기본 모달 열기")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("기본 모달 열기")
                    }
                }
            }

            // 폼 모달
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "폼 모달",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "사용자 입력을 받는 폼이 포함된 모달입니다.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Button(
                        onClick = { showFormModal = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, "폼 모달 열기")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("폼 모달 열기")
                    }
                }
            }

            // 커스텀 모달
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "커스텀 모달",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "사용자 정의 스타일과 레이아웃을 가진 모달입니다.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Button(
                        onClick = { showCustomModal = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Settings, "커스텀 모달 열기")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("커스텀 모달 열기")
                    }
                }
            }

            // 전체 화면 모달
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "전체 화면 모달",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "화면 전체를 덮는 전체 화면 모달입니다.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Button(
                        onClick = { showFullScreenModal = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Fullscreen, "전체 화면 모달 열기")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("전체 화면 모달 열기")
                    }
                }
            }

            // 모달 결과 표시
            if (modalResult != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "모달 결과",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = modalResult!!,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }

    // 기본 모달
    if (showBasicModal) {
        AlertDialog(
            onDismissRequest = { showBasicModal = false },
            title = { Text("정보") },
            text = { Text("이것은 기본 모달입니다. 간단한 정보를 표시합니다.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        modalResult = "기본 모달에서 확인됨"
                        showBasicModal = false
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        modalResult = "기본 모달에서 취소됨"
                        showBasicModal = false
                    }
                ) {
                    Text("취소")
                }
            }
        )
    }

    // 폼 모달
    if (showFormModal) {
        var inputText by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showFormModal = false },
            title = { Text("입력") },
            text = { 
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("텍스트를 입력하세요") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        modalResult = "폼 모달에서 입력됨: $inputText"
                        showFormModal = false
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        modalResult = "폼 모달에서 취소됨"
                        showFormModal = false
                    }
                ) {
                    Text("취소")
                }
            }
        )
    }

    // 커스텀 모달
    if (showCustomModal) {
        AlertDialog(
            onDismissRequest = { showCustomModal = false },
            title = { 
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, "별표")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("커스텀 모달")
                }
            },
            text = { 
                Column {
                    Text("이것은 커스텀 스타일을 가진 모달입니다.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, "체크")
                        Text("사용자 정의 아이콘")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        modalResult = "커스텀 모달에서 확인됨"
                        showCustomModal = false
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        modalResult = "커스텀 모달에서 취소됨"
                        showCustomModal = false
                    }
                ) {
                    Text("취소")
                }
            }
        )
    }

    // 전체 화면 모달
    if (showFullScreenModal) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showFullScreenModal = false },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Fullscreen,
                        contentDescription = "전체 화면",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "전체 화면 모달",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "이것은 전체 화면을 덮는 모달입니다.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            modalResult = "전체 화면 모달에서 닫힘"
                            showFullScreenModal = false
                        }
                    ) {
                        Text("닫기")
                    }
                }
            }
        }
    }
}
