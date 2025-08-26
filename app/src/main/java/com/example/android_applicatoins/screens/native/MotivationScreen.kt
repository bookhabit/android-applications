package com.example.android_applicatoins.screens.native

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.android_applicatoins.data.MotivationDataStore
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotivationScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val dataStore = remember { MotivationDataStore(context) }
    
    var isScreenOn by remember { mutableStateOf(true) }
    var showMotivation by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(0) }
    
         // 동기부여 콘텐츠 상태
     var motivationImage by remember { mutableStateOf(dataStore.motivationImage) }
     var motivationImageUri by remember { mutableStateOf(dataStore.motivationImageUri) }
     var motivationVideo by remember { mutableStateOf(dataStore.motivationVideo) }
     var motivationText by remember { mutableStateOf(dataStore.motivationText) }
    
    // 할 일 관리 상태
    var finalGoal by remember { mutableStateOf(dataStore.finalGoal) }
    var middleGoal by remember { mutableStateOf(dataStore.middleGoal) }
    var detailGoal by remember { mutableStateOf(dataStore.detailGoal) }
    var monthlyGoal by remember { mutableStateOf(dataStore.monthlyGoal) }
    var weeklyGoal by remember { mutableStateOf(dataStore.weeklyGoal) }
    var todayGoal by remember { mutableStateOf(dataStore.todayGoal) }
    
    // 설정 상태
    var autoMotivationEnabled by remember { mutableStateOf(dataStore.autoMotivationEnabled) }
    var startTime by remember { mutableStateOf(dataStore.startTime) }
    var endTime by remember { mutableStateOf(dataStore.endTime) }
    
         // 데이터 저장 함수들
     fun saveMotivationData() {
         dataStore.motivationImage = motivationImage
         dataStore.motivationImageUri = motivationImageUri
         dataStore.motivationVideo = motivationVideo
         dataStore.motivationText = motivationText
     }
    
    fun saveGoalData() {
        dataStore.finalGoal = finalGoal
        dataStore.middleGoal = middleGoal
        dataStore.detailGoal = detailGoal
        dataStore.monthlyGoal = monthlyGoal
        dataStore.weeklyGoal = weeklyGoal
        dataStore.todayGoal = todayGoal
    }
    
    fun saveSettingsData() {
        dataStore.autoMotivationEnabled = autoMotivationEnabled
        dataStore.startTime = startTime
        dataStore.endTime = endTime
    }
    
    // 화면 상태 감지
    val screenReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_ON -> {
                        isScreenOn = true
                        
                        // 시간대 체크
                        if (autoMotivationEnabled) {
                            val calendar = Calendar.getInstance()
                            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                            val currentMinute = calendar.get(Calendar.MINUTE)
                            val currentTime = currentHour * 60 + currentMinute
                            
                            val startParts = startTime.split(":")
                            val endParts = endTime.split(":")
                            val startTimeMinutes = startParts[0].toInt() * 60 + startParts[1].toInt()
                            val endTimeMinutes = endParts[0].toInt() * 60 + endParts[1].toInt()
                            
                            val isInTimeRange = if (startTimeMinutes <= endTimeMinutes) {
                                currentTime in startTimeMinutes..endTimeMinutes
                            } else {
                                currentTime >= startTimeMinutes || currentTime <= endTimeMinutes
                            }
                            
                            if (isInTimeRange) {
                                showMotivation = true
                            }
                        }
                    }
                    Intent.ACTION_SCREEN_OFF -> {
                        isScreenOn = false
                        showMotivation = false
                    }
                }
            }
        }
    }

    DisposableEffect(context) {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        context.registerReceiver(screenReceiver, filter)
        
        onDispose {
            context.unregisterReceiver(screenReceiver)
        }
    }



    // 동기부여 콘텐츠 표시 다이얼로그
    if (showMotivation) {
        AlertDialog(
            onDismissRequest = { showMotivation = false },
            title = {
                Text(
                    "오늘도 힘내세요! 💪",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                                         // 이미지 표시
                     if (motivationImage.isNotEmpty() || motivationImageUri.isNotEmpty()) {
                         Card(
                             modifier = Modifier.fillMaxWidth(),
                             colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                         ) {
                             Column(
                                 modifier = Modifier.padding(16.dp),
                                 horizontalAlignment = Alignment.CenterHorizontally
                             ) {
                                                                   if (motivationImageUri.isNotEmpty()) {
                                      // 선택된 이미지 표시
                                      Card(
                                          modifier = Modifier
                                              .fillMaxWidth()
                                              .height(150.dp),
                                          colors = CardDefaults.cardColors(containerColor = Color.White)
                                      ) {
                                          Box(
                                              modifier = Modifier
                                                  .fillMaxSize()
                                                  .clip(RoundedCornerShape(8.dp))
                                                  .background(Color(0xFFF5F5F5)),
                                              contentAlignment = Alignment.Center
                                          ) {
                                              Column(
                                                  horizontalAlignment = Alignment.CenterHorizontally
                                              ) {
                                                  Icon(
                                                      imageVector = Icons.Default.Image,
                                                      contentDescription = "선택된 이미지",
                                                      modifier = Modifier.size(48.dp),
                                                      tint = Color(0xFF1976D2)
                                                  )
                                                  Spacer(modifier = Modifier.height(8.dp))
                                                  Text(
                                                      text = "이미지가 선택되었습니다",
                                                      color = Color(0xFF1976D2),
                                                      fontSize = 14.sp
                                                  )
                                              }
                                          }
                                      }
                                      Spacer(modifier = Modifier.height(8.dp))
                                  }
                                 
                                 Text(
                                     text = "동기부여 이미지",
                                     fontWeight = FontWeight.Bold,
                                     color = Color(0xFF1976D2)
                                 )
                                 
                                 if (motivationImage.isNotEmpty()) {
                                     Text(
                                         text = motivationImage,
                                         textAlign = TextAlign.Center,
                                         color = Color.Gray
                                     )
                                 }
                             }
                         }
                         Spacer(modifier = Modifier.height(16.dp))
                     }
                    
                    // 영상 링크 표시
                    if (motivationVideo.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "동기부여 영상",
                                    modifier = Modifier.size(48.dp),
                                    tint = Color(0xFFF44336)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "동기부여 영상",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF44336)
                                )
                                Button(
                                    onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(motivationVideo))
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            // 에러 처리
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                                ) {
                                    Text("영상 보기")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // 텍스트 표시
                    if (motivationText.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatQuote,
                                    contentDescription = "동기부여 텍스트",
                                    modifier = Modifier.size(48.dp),
                                    tint = Color(0xFF4CAF50)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "동기부여 텍스트",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                                Text(
                                    text = motivationText,
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showMotivation = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("확인")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("동기부여 앱", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 탭 선택
            TabRow(
                selectedTabIndex = currentTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    text = { Text("동기부여 콘텐츠") },
                    icon = { Icon(Icons.Default.Favorite, "동기부여") }
                )
                Tab(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    text = { Text("할 일 관리") },
                    icon = { Icon(Icons.Default.CheckCircle, "할 일") }
                )
                Tab(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    text = { Text("설정") },
                    icon = { Icon(Icons.Default.Settings, "설정") }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            when (currentTab) {
                                 0 -> MotivationContentTab(
                     motivationImage = motivationImage,
                     onMotivationImageChange = { 
                         motivationImage = it
                         saveMotivationData()
                     },
                     motivationImageUri = motivationImageUri,
                     onMotivationImageUriChange = { 
                         motivationImageUri = it
                         saveMotivationData()
                     },
                     motivationVideo = motivationVideo,
                     onMotivationVideoChange = { 
                         motivationVideo = it
                         saveMotivationData()
                     },
                     motivationText = motivationText,
                     onMotivationTextChange = { 
                         motivationText = it
                         saveMotivationData()
                     }
                 )
                1 -> GoalManagementTab(
                    finalGoal = finalGoal,
                    onFinalGoalChange = { 
                        finalGoal = it
                        saveGoalData()
                    },
                    middleGoal = middleGoal,
                    onMiddleGoalChange = { 
                        middleGoal = it
                        saveGoalData()
                    },
                    detailGoal = detailGoal,
                    onDetailGoalChange = { 
                        detailGoal = it
                        saveGoalData()
                    },
                    monthlyGoal = monthlyGoal,
                    onMonthlyGoalChange = { 
                        monthlyGoal = it
                        saveGoalData()
                    },
                    weeklyGoal = weeklyGoal,
                    onWeeklyGoalChange = { 
                        weeklyGoal = it
                        saveGoalData()
                    },
                    todayGoal = todayGoal,
                    onTodayGoalChange = { 
                        todayGoal = it
                        saveGoalData()
                    }
                )
                2 -> SettingsTab(
                    autoMotivationEnabled = autoMotivationEnabled,
                    onAutoMotivationChange = { 
                        autoMotivationEnabled = it
                        saveSettingsData()
                    },
                    startTime = startTime,
                    onStartTimeChange = { 
                        startTime = it
                        saveSettingsData()
                    },
                    endTime = endTime,
                    onEndTimeChange = { 
                        endTime = it
                        saveSettingsData()
                    }
                )
            }
        }
    }
}

@Composable
fun MotivationContentTab(
    motivationImage: String,
    onMotivationImageChange: (String) -> Unit,
    motivationImageUri: String,
    onMotivationImageUriChange: (String) -> Unit,
    motivationVideo: String,
    onMotivationVideoChange: (String) -> Unit,
    motivationText: String,
    onMotivationTextChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "동기부여 콘텐츠 설정",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
                 // 이미지 설정
         Card(
             modifier = Modifier.fillMaxWidth(),
             colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
         ) {
             Column(
                 modifier = Modifier.padding(16.dp)
             ) {
                 Text(
                     text = "동기부여 이미지",
                     fontWeight = FontWeight.Bold,
                     color = Color(0xFF1976D2)
                 )
                 Spacer(modifier = Modifier.height(8.dp))
                 
                 // 이미지 설명 입력
                 OutlinedTextField(
                     value = motivationImage,
                     onValueChange = onMotivationImageChange,
                     label = { Text("가족, 롤모델 등 동기부여 이미지 설명") },
                     modifier = Modifier.fillMaxWidth(),
                     maxLines = 3
                 )
                 
                 Spacer(modifier = Modifier.height(16.dp))
                 
                 // 이미지 선택 버튼
                 val context = LocalContext.current
                 val imagePickerLauncher = rememberLauncherForActivityResult(
                     contract = ActivityResultContracts.GetContent()
                 ) { uri: Uri? ->
                     uri?.let {
                         onMotivationImageUriChange(it.toString())
                     }
                 }
                 
                 Button(
                     onClick = { imagePickerLauncher.launch("image/*") },
                     colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                     modifier = Modifier.fillMaxWidth()
                 ) {
                     Icon(
                         imageVector = Icons.Default.Image,
                         contentDescription = "이미지 선택",
                         modifier = Modifier.size(20.dp)
                     )
                     Spacer(modifier = Modifier.width(8.dp))
                     Text("갤러리에서 이미지 선택")
                 }
                 
                                   // 선택된 이미지 표시
                  if (motivationImageUri.isNotEmpty()) {
                      Spacer(modifier = Modifier.height(16.dp))
                      Card(
                          modifier = Modifier
                              .fillMaxWidth()
                              .height(200.dp),
                          colors = CardDefaults.cardColors(containerColor = Color.White)
                      ) {
                          Box(
                              modifier = Modifier
                                  .fillMaxSize()
                                  .clip(RoundedCornerShape(8.dp))
                                  .background(Color(0xFFF5F5F5)),
                              contentAlignment = Alignment.Center
                          ) {
                              Column(
                                  horizontalAlignment = Alignment.CenterHorizontally
                              ) {
                                  Icon(
                                      imageVector = Icons.Default.Image,
                                      contentDescription = "선택된 이미지",
                                      modifier = Modifier.size(64.dp),
                                      tint = Color(0xFF1976D2)
                                  )
                                  Spacer(modifier = Modifier.height(16.dp))
                                  Text(
                                      text = "이미지가 선택되었습니다",
                                      color = Color(0xFF1976D2),
                                      fontSize = 16.sp,
                                      fontWeight = FontWeight.Medium
                                  )
                                  Spacer(modifier = Modifier.height(8.dp))
                                  Text(
                                      text = "URI: ${motivationImageUri.take(50)}...",
                                      color = Color.Gray,
                                      fontSize = 12.sp,
                                      textAlign = TextAlign.Center
                                  )
                              }
                          }
                      }
                  }
             }
         }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 영상 링크 설정
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "동기부여 영상 링크",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF44336)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = motivationVideo,
                    onValueChange = onMotivationVideoChange,
                    label = { Text("YouTube 등 동기부여 영상 링크") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 텍스트 설정
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "동기부여 텍스트",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = motivationText,
                    onValueChange = onMotivationTextChange,
                    label = { Text("꿈, 목표, 다짐 등 동기부여 텍스트") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )
            }
        }
    }
}

@Composable
fun GoalManagementTab(
    finalGoal: String,
    onFinalGoalChange: (String) -> Unit,
    middleGoal: String,
    onMiddleGoalChange: (String) -> Unit,
    detailGoal: String,
    onDetailGoalChange: (String) -> Unit,
    monthlyGoal: String,
    onMonthlyGoalChange: (String) -> Unit,
    weeklyGoal: String,
    onWeeklyGoalChange: (String) -> Unit,
    todayGoal: String,
    onTodayGoalChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "목표 관리",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9C27B0)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 최종 목표
        GoalCard(
            title = "최종 목표",
            goal = finalGoal,
            onGoalChange = onFinalGoalChange,
            color = Color(0xFFE1BEE7),
            textColor = Color(0xFF7B1FA2)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 중간 목표
        GoalCard(
            title = "중간 목표",
            goal = middleGoal,
            onGoalChange = onMiddleGoalChange,
            color = Color(0xFFC8E6C9),
            textColor = Color(0xFF388E3C)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 상세 목표
        GoalCard(
            title = "상세 목표",
            goal = detailGoal,
            onGoalChange = onDetailGoalChange,
            color = Color(0xFFFFE0B2),
            textColor = Color(0xFFF57C00)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 이번달 목표
        GoalCard(
            title = "이번달 목표",
            goal = monthlyGoal,
            onGoalChange = onMonthlyGoalChange,
            color = Color(0xFFBBDEFB),
            textColor = Color(0xFF1976D2)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 이번주 목표
        GoalCard(
            title = "이번주 목표",
            goal = weeklyGoal,
            onGoalChange = onWeeklyGoalChange,
            color = Color(0xFFFFCDD2),
            textColor = Color(0xFFD32F2F)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 오늘 목표
        GoalCard(
            title = "오늘 목표",
            goal = todayGoal,
            onGoalChange = onTodayGoalChange,
            color = Color(0xFFF0F4C3),
            textColor = Color(0xFFAFB42B)
        )
    }
}

@Composable
fun GoalCard(
    title: String,
    goal: String,
    onGoalChange: (String) -> Unit,
    color: Color,
    textColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = goal,
                onValueChange = onGoalChange,
                label = { Text("목표를 입력하세요") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = textColor,
                    unfocusedBorderColor = textColor.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun SettingsTab(
    autoMotivationEnabled: Boolean,
    onAutoMotivationChange: (Boolean) -> Unit,
    startTime: String,
    onStartTimeChange: (String) -> Unit,
    endTime: String,
    onEndTimeChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "설정",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF607D8B)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 자동 동기부여 토글
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "자동 동기부여",
                    tint = Color(0xFFFF9800)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "자동 동기부여 실행",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800)
                    )
                    Text(
                        text = "화면을 켤 때 자동으로 동기부여 콘텐츠 표시",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Switch(
                    checked = autoMotivationEnabled,
                    onCheckedChange = onAutoMotivationChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFFFF9800),
                        checkedTrackColor = Color(0xFFFF9800).copy(alpha = 0.5f)
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 시간대 설정
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "동기부여 실행 시간대",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF607D8B)
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "시작 시간",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = onStartTimeChange,
                            label = { Text("HH:MM") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "종료 시간",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = onEndTimeChange,
                            label = { Text("HH:MM") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "설정된 시간대에만 동기부여가 실행됩니다",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
