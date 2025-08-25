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
fun ActionSheetScreen(
    onBackPressed: () -> Unit
) {
    var showBasicActionSheet by remember { mutableStateOf(false) }
    var showImageActionSheet by remember { mutableStateOf(false) }
    var showCustomActionSheet by remember { mutableStateOf(false) }
    var selectedAction by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("액션 시트") },
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
                text = "액션 시트 예제",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // 기본 액션 시트
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "기본 액션 시트",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "간단한 선택 옵션을 제공하는 액션 시트입니다.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Button(
                        onClick = { showBasicActionSheet = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.MoreVert, "액션 시트 열기")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("기본 액션 시트 열기")
                    }
                }
            }

            // 이미지 관련 액션 시트
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "이미지 액션 시트",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "사진 촬영, 갤러리 선택 등의 이미지 관련 옵션을 제공합니다.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Button(
                        onClick = { showImageActionSheet = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoCamera, "이미지 액션 시트 열기")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("이미지 액션 시트 열기")
                    }
                }
            }

            // 커스텀 액션 시트
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "커스텀 액션 시트",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "사용자 정의 옵션과 스타일을 가진 액션 시트입니다.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Button(
                        onClick = { showCustomActionSheet = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Settings, "커스텀 액션 시트 열기")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("커스텀 액션 시트 열기")
                    }
                }
            }

            // 선택된 액션 표시
            if (selectedAction != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "선택된 액션",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = selectedAction!!,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }

    // 기본 액션 시트
    if (showBasicActionSheet) {
        AlertDialog(
            onDismissRequest = { showBasicActionSheet = false },
            title = { Text("옵션 선택") },
            text = { Text("원하는 옵션을 선택하세요") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedAction = "확인 선택됨"
                        showBasicActionSheet = false
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        selectedAction = "취소 선택됨"
                        showBasicActionSheet = false
                    }
                ) {
                    Text("취소")
                }
            }
        )
    }

    // 이미지 액션 시트
    if (showImageActionSheet) {
        AlertDialog(
            onDismissRequest = { showImageActionSheet = false },
            title = { Text("이미지 선택") },
            text = { Text("이미지를 가져올 방법을 선택하세요") },
            confirmButton = {
                Column {
                    TextButton(
                        onClick = {
                            selectedAction = "사진 촬영 선택됨"
                            showImageActionSheet = false
                        }
                    ) {
                        Icon(Icons.Default.PhotoCamera, "사진 촬영")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("사진 촬영")
                    }
                    TextButton(
                        onClick = {
                            selectedAction = "갤러리에서 선택 선택됨"
                            showImageActionSheet = false
                        }
                    ) {
                        Icon(Icons.Default.PhotoLibrary, "갤러리에서 선택")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("갤러리에서 선택")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        selectedAction = "취소됨"
                        showImageActionSheet = false
                    }
                ) {
                    Text("취소")
                }
            }
        )
    }

    // 커스텀 액션 시트
    if (showCustomActionSheet) {
        AlertDialog(
            onDismissRequest = { showCustomActionSheet = false },
            title = { Text("고급 옵션") },
            text = { Text("추가 설정 옵션을 선택하세요") },
            confirmButton = {
                Column {
                    TextButton(
                        onClick = {
                            selectedAction = "설정 선택됨"
                            showCustomActionSheet = false
                        }
                    ) {
                        Icon(Icons.Default.Settings, "설정")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("설정")
                    }
                    TextButton(
                        onClick = {
                            selectedAction = "도움말 선택됨"
                            showCustomActionSheet = false
                        }
                    ) {
                        Icon(Icons.Default.Help, "도움말")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("도움말")
                    }
                    TextButton(
                        onClick = {
                            selectedAction = "정보 선택됨"
                            showCustomActionSheet = false
                        }
                    ) {
                        Icon(Icons.Default.Info, "정보")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("정보")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        selectedAction = "취소됨"
                        showCustomActionSheet = false
                    }
                ) {
                    Text("취소")
                }
            }
        )
    }
}
