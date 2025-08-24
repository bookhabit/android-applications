package com.example.android_applicatoins.screens.basic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDisplayScreen(
    onBackPressed: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    // 이미지 상태 변수들
    var selectedImageType by remember { mutableStateOf("resource") }
    var imageScale by remember { mutableStateOf(1f) }
    var imageRotation by remember { mutableStateOf(0f) }
    var imageAlpha by remember { mutableStateOf(1f) }
    var showImageFilters by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("none") }
    
    // 애니메이션 상태
    var animationScale by remember { mutableStateOf(1f) }
    var animationRotation by remember { mutableStateOf(0f) }
    var animationAlpha by remember { mutableStateOf(1f) }
    
    // 애니메이션 효과
    LaunchedEffect(Unit) {
        while (true) {
            animationScale = 0.8f + 0.4f * sin(System.currentTimeMillis() * 0.003).toFloat()
            animationRotation += 2f
            animationAlpha = 0.5f + 0.5f * sin(System.currentTimeMillis() * 0.002).toFloat()
            kotlinx.coroutines.delay(50)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("이미지 표시") },
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
            // 이미지 타입 선택 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🖼️ 이미지 타입",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedImageType == "resource",
                            onClick = { selectedImageType = "resource" },
                            label = { Text("리소스") }
                        )
                        FilterChip(
                            selected = selectedImageType == "canvas",
                            onClick = { selectedImageType = "canvas" },
                            label = { Text("Canvas") }
                        )
                        FilterChip(
                            selected = selectedImageType == "animated",
                            onClick = { selectedImageType = "animated" },
                            label = { Text("애니메이션") }
                        )
                    }
                }
            }
            
            // 이미지 속성 설정 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🎭 이미지 속성",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("크기:")
                        Slider(
                            value = imageScale,
                            onValueChange = { imageScale = it },
                            valueRange = 0.1f..3f,
                            steps = 29
                        )
                        Text("${String.format("%.1f", imageScale)}x")
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("회전:")
                        Slider(
                            value = imageRotation,
                            onValueChange = { imageRotation = it },
                            valueRange = 0f..360f,
                            steps = 72
                        )
                        Text("${imageRotation.toInt()}°")
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("투명도:")
                        Slider(
                            value = imageAlpha,
                            onValueChange = { imageAlpha = it },
                            valueRange = 0f..1f,
                            steps = 20
                        )
                        Text("${String.format("%.1f", imageAlpha)}")
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(onClick = { showImageFilters = true }) {
                            Icon(Icons.Default.FilterAlt, "필터")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("이미지 필터")
                        }
                        
                        Button(
                            onClick = {
                                imageScale = 1f
                                imageRotation = 0f
                                imageAlpha = 1f
                            }
                        ) {
                            Icon(Icons.Default.Refresh, "리셋")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("리셋")
                        }
                    }
                }
            }
            
            // 이미지 표시 영역
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🖼️ 이미지 표시",
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
                                detectTransformGestures { _, pan, zoom, _ ->
                                    imageScale *= zoom
                                    imageScale = imageScale.coerceIn(0.1f, 5f)
                                }
                            }
                    ) {
                        when (selectedImageType) {
                            "resource" -> drawResourceImage()
                            "canvas" -> drawCanvasImage()
                            "animated" -> drawAnimatedImage()
                        }
                    }
                }
            }
            
            // 이미지 효과 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "✨ 이미지 효과",
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
                            // 그라데이션 배경
                            val gradient = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF4CAF50),
                                    Color(0xFF81C784),
                                    Color(0xFFC8E6C9)
                                ),
                                center = Offset(size.width / 2, size.height / 2),
                                radius = size.width / 2
                            )
                            
                            drawRect(
                                brush = gradient,
                                size = size
                            )
                            
                            // 애니메이션 이미지들
                            repeat(3) { index ->
                                val x = (size.width / 4) * (index + 1)
                                val y = size.height / 2
                                
                                // 회전하는 사각형
                                rotate(
                                    degrees = animationRotation + index * 30f,
                                    pivot = Offset(x, y)
                                ) {
                                    drawRect(
                                        color = Color.White.copy(alpha = animationAlpha),
                                        topLeft = Offset(x - 20f, y - 20f),
                                        size = Size(40f, 40f),
                                        style = Fill
                                    )
                                }
                                
                                // 크기 변하는 원
                                scale(animationScale) {
                                    drawCircle(
                                        color = Color.Red.copy(alpha = 0.7f),
                                        radius = 25f,
                                        center = Offset(x, y + 60f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // 이미지 정보 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📊 이미지 정보",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text("선택된 이미지 타입: $selectedImageType")
                    Text("이미지 크기: ${String.format("%.1f", imageScale)}x")
                    Text("이미지 회전: ${imageRotation.toInt()}°")
                    Text("이미지 투명도: ${String.format("%.1f", imageAlpha)}")
                    Text("적용된 필터: $selectedFilter")
                    Text("애니메이션 크기: ${String.format("%.2f", animationScale)}x")
                    Text("애니메이션 회전: ${animationRotation.toInt()}°")
                    Text("애니메이션 투명도: ${String.format("%.2f", animationAlpha)}")
                }
            }
        }
        
        // 이미지 필터 선택 다이얼로그
        if (showImageFilters) {
            AlertDialog(
                onDismissRequest = { showImageFilters = false },
                title = { Text("이미지 필터 선택") },
                text = {
                    Column {
                        val filters = listOf(
                            "none" to "없음",
                            "grayscale" to "흑백",
                            "sepia" to "세피아",
                            "invert" to "반전",
                            "blur" to "블러",
                            "sharpen" to "선명화"
                        )
                        
                        filters.forEach { (filter, name) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedFilter == filter,
                                    onClick = { selectedFilter = filter }
                                )
                                Text(name)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showImageFilters = false }) {
                        Text("확인")
                    }
                }
            )
        }
    }
}

@Composable
private fun drawResourceImage() {
    // 기본 이미지 리소스 사용 (아이콘)
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 여러 아이콘을 조합하여 이미지 효과 생성
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "별",
                modifier = Modifier.size(64.dp),
                tint = Color.Yellow
            )
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "하트",
                modifier = Modifier.size(64.dp),
                tint = Color.Red
            )
            Icon(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = "좋아요",
                modifier = Modifier.size(64.dp),
                tint = Color.Blue
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 텍스트 이미지
        Text(
            text = "이미지 표시 예제",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 작은 아이콘들
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "체크",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Green.copy(alpha = 0.7f + 0.3f * (index / 4f))
                )
            }
        }
    }
}

@Composable
private fun drawCanvasImage() {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        // 배경 그리기
        drawRect(
            color = Color(0xFFF5F5F5),
            size = size
        )
        
        // 이미지 프레임 그리기
        val frameRect = Rect(
            left = size.width * 0.1f,
            top = size.height * 0.1f,
            right = size.width * 0.9f,
            bottom = size.height * 0.9f
        )
        
        // 프레임 배경
        drawRect(
            color = Color.White,
            topLeft = frameRect.topLeft,
            size = frameRect.size
        )
        
        // 프레임 테두리
        drawRect(
            color = Color.Gray,
            topLeft = frameRect.topLeft,
            size = frameRect.size,
            style = Stroke(width = 3f)
        )
        
        // 이미지 내용 (도형들로 표현)
        val centerX = size.width / 2
        val centerY = size.height / 2
        
        // 중앙 원
        drawCircle(
            color = Color.Blue,
            radius = 60f,
            center = Offset(centerX, centerY)
        )
        
        // 작은 원들
        repeat(8) { index ->
            val angle = index * 45f * PI / 180
            val x = centerX + 100f * cos(angle).toFloat()
            val y = centerY + 100f * sin(angle).toFloat()
            
            drawCircle(
                color = Color.Red.copy(alpha = 0.6f),
                radius = 15f,
                center = Offset(x, y)
            )
        }
        
        // 연결선
        repeat(8) { index ->
            val angle1 = index * 45f * PI / 180
            val angle2 = (index + 1) * 45f * PI / 180
            
            val x1 = centerX + 100f * cos(angle1).toFloat()
            val y1 = centerY + 100f * sin(angle1).toFloat()
            val x2 = centerX + 100f * cos(angle2).toFloat()
            val y2 = centerY + 100f * sin(angle2).toFloat()
            
            drawLine(
                color = Color.Green.copy(alpha = 0.4f),
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                strokeWidth = 2f
            )
        }
        
        // 텍스트는 Compose의 Text 컴포넌트로 대체
        // Canvas에서는 텍스트 그리기가 복잡하므로 생략
    }
}

@Composable
private fun drawAnimatedImage() {
    var animationState by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            animationState += 0.02f
            kotlinx.coroutines.delay(16) // 60 FPS
        }
    }
    
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        // 배경 그리기
        val gradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFE1F5FE),
                Color(0xFFB3E5FC),
                Color(0xFF81D4FA)
            )
        )
        
        drawRect(
            brush = gradient,
            size = size
        )
        
        val centerX = size.width / 2
        val centerY = size.height / 2
        
        // 회전하는 별
        rotate(animationState * 360f, Offset(centerX, centerY)) {
            drawStar(
                center = Offset(centerX, centerY),
                radius = 40f,
                color = Color.Yellow
            )
        }
        
        // 움직이는 원들
        repeat(6) { index ->
            val angle = animationState * 2 * PI + index * PI / 3
            val radius = 80f + 20f * sin(animationState * 3 + index).toFloat()
            val x = centerX + radius * cos(angle).toFloat()
            val y = centerY + radius * sin(angle).toFloat()
            
            drawCircle(
                color = Color(
                    red = (0.5f + 0.5f * sin(animationState * 2 + index)).toFloat(),
                    green = (0.5f + 0.5f * cos(animationState * 2 + index)).toFloat(),
                    blue = (0.5f + 0.5f * sin(animationState * 2 + index * 2)).toFloat()
                ),
                radius = 15f,
                center = Offset(x, y)
            )
        }
        
        // 파동 효과
        repeat(3) { wave ->
            val waveRadius = 50f + wave * 30f + 20f * sin(animationState * 2).toFloat()
            drawCircle(
                color = Color.White.copy(alpha = 0.3f - wave * 0.1f),
                radius = waveRadius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2f)
            )
        }
    }
}

private fun DrawScope.drawStar(
    center: Offset,
    radius: Float,
    color: Color
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
