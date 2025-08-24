package com.example.android_applicatoins.screens.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTestScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // 상태 변수들
    var checkbox1 by remember { mutableStateOf(false) }
    var checkbox2 by remember { mutableStateOf(false) }
    var checkbox3 by remember { mutableStateOf(false) }
    
    var selectedRadio by remember { mutableStateOf("option1") }
    
    var toggleState by remember { mutableStateOf(false) }
    
    var rating by remember { mutableStateOf(3f) }
    
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    var touchInfo by remember { mutableStateOf("터치 영역을 터치해보세요") }
    var touchCount by remember { mutableStateOf(0) }
    
    var imageScale by remember { mutableStateOf(1f) }
    
    var keyInput by remember { mutableStateOf("") }
    
    // 날짜 선택기 상태
    val datePickerState = rememberDatePickerState()
    
    // 시간 선택기 상태
    var selectedHour by remember { mutableStateOf(12) }
    var selectedMinute by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("이벤트 테스트") },
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. 체크박스 테스트
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📌 체크박스 테스트",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = checkbox1,
                            onCheckedChange = { checkbox1 = it }
                        )
                        Text("옵션 1")
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = checkbox2,
                            onCheckedChange = { checkbox2 = it }
                        )
                        Text("옵션 2")
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = checkbox3,
                            onCheckedChange = { checkbox3 = it }
                        )
                        Text("옵션 3")
                    }
                    
                    Text(
                        text = "선택된 옵션: ${listOfNotNull(
                            if (checkbox1) "옵션 1" else null,
                            if (checkbox2) "옵션 2" else null,
                            if (checkbox3) "옵션 3" else null
                        ).joinToString(", ").ifEmpty { "없음" }}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // 2. 라디오버튼 테스트
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📻 라디오버튼 테스트",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = selectedRadio == "option1",
                            onClick = { selectedRadio = "option1" }
                        )
                        Text("옵션 A")
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = selectedRadio == "option2",
                            onClick = { selectedRadio = "option2" }
                        )
                        Text("옵션 B")
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = selectedRadio == "option3",
                            onClick = { selectedRadio = "option3" }
                        )
                        Text("옵션 C")
                    }
                    
                    Text(
                        text = "선택된 옵션: $selectedRadio",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // 3. 토글버튼 테스트
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🔄 토글버튼 테스트",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Switch(
                            checked = toggleState,
                            onCheckedChange = { toggleState = it }
                        )
                        Text(
                            text = if (toggleState) "켜짐" else "꺼짐",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Text(
                        text = "현재 상태: ${if (toggleState) "활성화" else "비활성화"}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // 4. 레이팅바 테스트
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⭐ 레이팅바 테스트",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        repeat(5) { index ->
                            IconButton(
                                onClick = { rating = (index + 1).toFloat() }
                            ) {
                                Icon(
                                    imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "별 ${index + 1}",
                                    tint = if (index < rating) Color(0xFFFFD700) else Color.Gray,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    
                    Text(
                        text = "선택된 점수: $rating / 5",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // 5. 날짜/시간 선택기 테스트
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📅 날짜/시간 선택기 테스트",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, "날짜 선택")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("날짜 선택")
                        }
                        
                        Button(onClick = { showTimePicker = true }) {
                            Icon(Icons.Default.Schedule, "시간 선택")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("시간 선택")
                        }
                    }
                    
                    if (selectedDate.isNotEmpty()) {
                        Text("선택된 날짜: $selectedDate")
                    }
                    if (selectedTime.isNotEmpty()) {
                        Text("선택된 시간: $selectedTime")
                    }
                }
            }
            
            // 6. 터치 이벤트 테스트
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "👆 터치 이벤트 테스트",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Color(0xFF81C784), RoundedCornerShape(8.dp))
                            .border(2.dp, Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        touchInfo = "터치 시작: $offset"
                                        touchCount++
                                    },
                                    onDragEnd = {
                                        touchInfo = "터치 종료"
                                    },
                                    onDrag = { change, _ ->
                                        touchInfo = "터치 이동: ${change.position}"
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "터치 영역\n(드래그해보세요)",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Text(
                        text = touchInfo,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "터치 횟수: $touchCount",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // 7. 멀티터치 (핀치 줌) 테스트
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🤏 멀티터치 (핀치 줌) 테스트",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFFFFB74D), RoundedCornerShape(8.dp))
                            .border(2.dp, Color(0xFFFF9800), RoundedCornerShape(8.dp))
                            .scale(imageScale)
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    imageScale *= zoom
                                    imageScale = imageScale.coerceIn(0.5f, 3f)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "핀치 줌 영역\n(두 손가락으로 확대/축소)",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Text(
                        text = "현재 스케일: ${String.format("%.2f", imageScale)}x",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    Button(
                        onClick = { imageScale = 1f },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("스케일 리셋")
                    }
                }
            }
            
            // 8. 키보드 입력 테스트
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⌨️ 키보드 입력 테스트",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    OutlinedTextField(
                        value = keyInput,
                        onValueChange = { keyInput = it },
                        label = { Text("텍스트를 입력하세요") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = true
                    )
                    
                    Text(
                        text = "입력된 텍스트: $keyInput",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    Text(
                        text = "텍스트 길이: ${keyInput.length}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // 9. 이벤트 요약
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📊 이벤트 요약",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text("체크박스: ${if (checkbox1 || checkbox2 || checkbox3) "선택됨" else "선택 안됨"}")
                    Text("라디오: $selectedRadio")
                    Text("토글: ${if (toggleState) "켜짐" else "꺼짐"}")
                    Text("레이팅: $rating/5")
                    Text("날짜: ${if (selectedDate.isNotEmpty()) selectedDate else "선택 안됨"}")
                    Text("시간: ${if (selectedTime.isNotEmpty()) selectedTime else "선택 안됨"}")
                    Text("터치 횟수: $touchCount")
                    Text("이미지 스케일: ${String.format("%.2f", imageScale)}x")
                    Text("텍스트 길이: ${keyInput.length}")
                }
            }
        }
        
        // 날짜 선택기 다이얼로그
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Date(millis)
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            selectedDate = formatter.format(date)
                        }
                        showDatePicker = false
                    }) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("취소")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        
        // 시간 선택기 다이얼로그 (AlertDialog로 대체)
        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                title = { Text("시간 선택") },
                text = {
                    Column {
                        Text("시간: $selectedHour")
                        Slider(
                            value = selectedHour.toFloat(),
                            onValueChange = { selectedHour = it.toInt() },
                            valueRange = 0f..23f,
                            steps = 22
                        )
                        
                        Text("분: $selectedMinute")
                        Slider(
                            value = selectedMinute.toFloat(),
                            onValueChange = { selectedMinute = it.toInt() },
                            valueRange = 0f..59f,
                            steps = 58
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        selectedTime = "${selectedHour.toString().padStart(2, '0')}:${selectedMinute.toString().padStart(2, '0')}"
                        showTimePicker = false
                    }) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("취소")
                    }
                }
            )
        }
    }
}
