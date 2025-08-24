package com.example.android_applicatoins.screens.basic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphicsDrawingScreen(
    onBackPressed: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    // 그래픽 상태 변수들
    var selectedTool by remember { mutableStateOf("basic") }
    var paintColor by remember { mutableStateOf(Color.Red) }
    var paintStyle by remember { mutableStateOf("FILL") }
    var strokeWidth by remember { mutableStateOf(5f) }
    var showColorPicker by remember { mutableStateOf(false) }
    
    // 애니메이션 상태
    var animationAngle by remember { mutableStateOf(0f) }
    var animationScale by remember { mutableStateOf(1f) }
    
    // 터치 위치
    var touchPosition by remember { mutableStateOf(Offset.Zero) }
    var isDrawing by remember { mutableStateOf(false) }
    
    // 애니메이션 효과
    LaunchedEffect(Unit) {
        while (true) {
            animationAngle += 2f
            animationScale = 0.8f + 0.4f * sin(animationAngle * PI / 180).toFloat()
            kotlinx.coroutines.delay(50)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("그래픽 그리기") },
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
            // 도구 선택 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🎨 그리기 도구",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedTool == "basic",
                            onClick = { selectedTool = "basic" },
                            label = { Text("기본 도형") }
                        )
                        FilterChip(
                            selected = selectedTool == "path",
                            onClick = { selectedTool = "path" },
                            label = { Text("Path") }
                        )
                        FilterChip(
                            selected = selectedTool == "advanced",
                            onClick = { selectedTool = "advanced" },
                            label = { Text("고급") }
                        )
                    }
                }
            }
            
            // Paint 속성 설정 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🎭 Paint 속성",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("색상:")
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(paintColor, RoundedCornerShape(8.dp))
                                .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                                .pointerInput(Unit) {
                                    detectTapGestures {
                                        showColorPicker = true
                                    }
                                }
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("스타일:")
                        Row {
                            FilterChip(
                                selected = paintStyle == "FILL",
                                onClick = { paintStyle = "FILL" },
                                label = { Text("채우기") }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FilterChip(
                                selected = paintStyle == "STROKE",
                                onClick = { paintStyle = "STROKE" },
                                label = { Text("외곽선") }
                            )
                        }
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("선 두께:")
                        Slider(
                            value = strokeWidth,
                            onValueChange = { strokeWidth = it },
                            valueRange = 1f..20f,
                            steps = 19
                        )
                        Text("${strokeWidth.toInt()}")
                    }
                }
            }
            
            // 그래픽 그리기 영역
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🖼️ 그래픽 그리기",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    touchPosition = offset
                                    isDrawing = true
                                }
                            }
                            .pointerInput(Unit) {
                                detectDragGestures { change, _ ->
                                    touchPosition = change.position
                                    isDrawing = true
                                }
                            }
                    ) {
                        Canvas(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // 배경 그리기
                            drawRect(
                                color = Color(0xFFF5F5F5),
                                size = size
                            )
                            
                            when (selectedTool) {
                                "basic" -> drawBasicShapes(paintColor, paintStyle, strokeWidth)
                                "path" -> drawPathShapes(paintColor, paintStyle, strokeWidth)
                                "advanced" -> drawAdvancedShapes(paintColor, paintStyle, strokeWidth)
                            }
                            
                            // 터치 위치 표시
                            if (isDrawing) {
                                drawCircle(
                                    color = paintColor,
                                    radius = 10f,
                                    center = touchPosition
                                )
                            }
                        }
                    }
                }
            }
            
            // 애니메이션 그래픽 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "✨ 애니메이션 그래픽",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color(0xFFE8F5E8), RoundedCornerShape(8.dp))
                    ) {
                        Canvas(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // 회전하는 별 그리기
                            rotate(animationAngle) {
                                drawStar(
                                    center = Offset(size.width / 2, size.height / 2),
                                    radius = 50f * animationScale,
                                    color = paintColor,
                                    strokeWidth = strokeWidth
                                )
                            }
                            
                            // 움직이는 원들
                            repeat(5) { index ->
                                val x = (size.width / 6) * (index + 1)
                                val y = size.height / 2 + 30f * sin((animationAngle + index * 30) * PI / 180).toFloat()
                                
                                drawCircle(
                                    color = Color(
                                        red = (0.5f + 0.5f * sin((animationAngle + index * 60) * PI / 180).toFloat()),
                                        green = (0.5f + 0.5f * cos((animationAngle + index * 60) * PI / 180).toFloat()),
                                        blue = (0.5f + 0.5f * sin((animationAngle + index * 90) * PI / 180).toFloat())
                                    ),
                                    radius = 15f,
                                    center = Offset(x, y)
                                )
                            }
                        }
                    }
                }
            }
            
            // 정보 표시 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📊 그래픽 정보",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text("선택된 도구: $selectedTool")
                    Text("Paint 색상: ${paintColor.toString()}")
                    Text("Paint 스타일: $paintStyle")
                    Text("선 두께: ${strokeWidth.toInt()}px")
                    Text("애니메이션 각도: ${animationAngle.toInt()}°")
                    Text("애니메이션 스케일: ${String.format("%.2f", animationScale)}x")
                    if (isDrawing) {
                        Text("터치 위치: (${touchPosition.x.toInt()}, ${touchPosition.y.toInt()})")
                    }
                }
            }
        }
        
        // 색상 선택 다이얼로그
        if (showColorPicker) {
            AlertDialog(
                onDismissRequest = { showColorPicker = false },
                title = { Text("색상 선택") },
                text = {
                    Column {
                        val colors = listOf(
                            Color.Red, Color.Green, Color.Blue, Color.Yellow,
                            Color.Cyan, Color.Magenta, Color.Black, Color.White,
                            Color.Gray, Color.DarkGray, Color.LightGray
                        )
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(colors) { color ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(color, RoundedCornerShape(8.dp))
                                        .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                                        .pointerInput(Unit) {
                                            detectTapGestures {
                                                paintColor = color
                                                showColorPicker = false
                                            }
                                        }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showColorPicker = false }) {
                        Text("취소")
                    }
                }
            )
        }
    }
}



private fun DrawScope.drawBasicShapes(
    paintColor: Color,
    paintStyle: String,
    strokeWidth: Float
) {
    val currentPaintStyle = when (paintStyle) {
        "FILL" -> Fill
        "STROKE" -> Stroke(width = strokeWidth)
        else -> Fill
    }
    
    // 점
    drawCircle(
        color = paintColor,
        radius = 3f,
        center = Offset(50f, 50f)
    )
    
    // 선
    drawLine(
        color = paintColor,
        start = Offset(50f, 100f),
        end = Offset(300f, 100f),
        strokeWidth = strokeWidth
    )
    
    // 직사각형
    drawRect(
        color = paintColor,
        topLeft = Offset(50f, 150f),
        size = Size(200f, 100f),
        style = currentPaintStyle
    )
    
    // 둥근 직사각형
    drawRoundRect(
        color = paintColor,
        topLeft = Offset(50f, 280f),
        size = Size(200f, 100f),
        cornerRadius = CornerRadius(20f),
        style = currentPaintStyle
    )
    
    // 원
    drawCircle(
        color = paintColor,
        radius = 50f,
        center = Offset(200f, 450f),
        style = currentPaintStyle
    )
    
    // 타원
    drawOval(
        color = paintColor,
        topLeft = Offset(50f, 520f),
        size = Size(200f, 100f),
        style = currentPaintStyle
    )
    
    // 호
    drawArc(
        color = paintColor,
        topLeft = Offset(50f, 650f),
        size = Size(200f, 100f),
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = true,
        style = currentPaintStyle
    )
}

private fun DrawScope.drawPathShapes(
    paintColor: Color,
    paintStyle: String,
    strokeWidth: Float
) {
    val path = Path()
    
    // 시작점
    path.moveTo(50f, 50f)
    
    // 직선
    path.lineTo(300f, 50f)
    
    // 사각형 경로
    path.addRect(Rect(50f, 100f, 250f, 200f))
    
    // 원 경로
    path.addOval(Rect(50f, 250f, 250f, 350f))
    
    // 호 경로
    path.addArc(Rect(50f, 400f, 250f, 500f), 0f, 180f)
    
    // 베지어 곡선
    path.moveTo(50f, 550f)
    path.quadraticBezierTo(150f, 450f, 250f, 550f)
    
    // Path 그리기
    val currentPaintStyle = if (paintStyle == "FILL") Fill else Stroke(width = strokeWidth)
    drawPath(
        path = path,
        color = paintColor,
        style = currentPaintStyle
    )
}

private fun DrawScope.drawAdvancedShapes(
    paintColor: Color,
    paintStyle: String,
    strokeWidth: Float
) {
    // 그라데이션
    val gradient = Brush.verticalGradient(
        colors = listOf(paintColor, paintColor.copy(alpha = 0.3f))
    )
    
    // 그라데이션 원
    drawCircle(
        brush = gradient,
        radius = 60f,
        center = Offset(150f, 100f)
    )
    
    // 회전된 사각형
    rotate(45f, Offset(150f, 250f)) {
        drawRect(
            color = paintColor,
            topLeft = Offset(100f, 200f),
            size = Size(100f, 100f)
        )
    }
    
    // 별 모양
    drawStar(
        center = Offset(150f, 400f),
        radius = 40f,
        color = paintColor,
        strokeWidth = strokeWidth
    )
    
    // 복잡한 패턴
    repeat(8) { index ->
        val angle = index * 45f
        val x = 150f + 80f * cos(angle * PI / 180).toFloat()
        val y = 550f + 80f * sin(angle * PI / 180).toFloat()
        
        drawCircle(
            color = paintColor.copy(alpha = 0.7f),
            radius = 15f,
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawStar(
    center: Offset,
    radius: Float,
    color: Color,
    strokeWidth: Float
) {
    val path = Path()
    val outerRadius = radius
    val innerRadius = radius * 0.4f
    val points = 5
    
    for (i in 0 until points * 2) {
        val angle = i * PI / points
        val r = if (i % 2 == 0) outerRadius else innerRadius
        val x = center.x + r * cos(angle).toFloat()
        val y = center.y + r * sin(angle).toFloat()
        
        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()
    
    drawPath(
        path = path,
        color = color,
        style = Fill
    )
}
