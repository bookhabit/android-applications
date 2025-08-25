package com.example.android_applicatoins.screens.basic.listView

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpinnerDemoScreen(
    onBackPressed: () -> Unit
) {
    var selectedCity by remember { mutableStateOf<String?>(null) }
    var selectedColor by remember { mutableStateOf<String?>(null) }
    var selectedFruit by remember { mutableStateOf<String?>(null) }
    var selectedLanguage by remember { mutableStateOf<String?>(null) }
    var showCityDropdown by remember { mutableStateOf(false) }
    var showColorDropdown by remember { mutableStateOf(false) }
    var showFruitDropdown by remember { mutableStateOf(false) }
    var showLanguageDropdown by remember { mutableStateOf(false) }
    
    // 샘플 데이터
    val cities = listOf("서울", "부산", "대구", "인천", "광주", "대전", "울산", "세종")
    val colors = listOf("빨강", "파랑", "초록", "노랑", "보라", "주황", "분홍", "검정")
    val fruits = listOf("사과", "바나나", "포도", "수박", "오렌지", "키위", "망고", "파인애플")
    val languages = listOf("한국어", "영어", "일본어", "중국어", "프랑스어", "독일어", "스페인어", "러시아어")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spinner 데모") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. 기본 Spinner (도시 선택)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "1. 기본 Spinner (도시 선택)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Text(
                            text = "ArrayAdapter를 사용한 기본 드롭다운",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Spinner 버튼
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCityDropdown = !showCityDropdown },
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedCity ?: "도시를 선택하세요",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (selectedCity != null) MaterialTheme.colorScheme.onSurface 
                                           else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Icon(
                                    if (showCityDropdown) Icons.Default.KeyboardArrowUp 
                                    else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        // 드롭다운 메뉴
                        if (showCityDropdown) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column {
                                    cities.forEach { city ->
                                        ListItem(
                                            headlineContent = { Text(city) },
                                            modifier = Modifier.clickable {
                                                selectedCity = city
                                                showCityDropdown = false
                                            }
                                        )
                                        if (city != cities.last()) Divider()
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // 2. 색상 선택 Spinner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "2. 색상 선택 Spinner",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Text(
                            text = "색상 미리보기가 있는 드롭다운",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // 색상 Spinner 버튼
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showColorDropdown = !showColorDropdown },
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (selectedColor != null) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(
                                                    when (selectedColor) {
                                                        "빨강" -> Color.Red
                                                        "파랑" -> Color.Blue
                                                        "초록" -> Color.Green
                                                        "노랑" -> Color.Yellow
                                                        "보라" -> Color.Magenta
                                                        "주황" -> Color(0xFFFF9800)
                                                        "분홍" -> Color(0xFFE91E63)
                                                        "검정" -> Color.Black
                                                        else -> MaterialTheme.colorScheme.primary
                                                    },
                                                    shape = MaterialTheme.shapes.small
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                    }
                                    Text(
                                        text = selectedColor ?: "색상을 선택하세요",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (selectedColor != null) MaterialTheme.colorScheme.onSurface 
                                               else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    if (showColorDropdown) Icons.Default.KeyboardArrowUp 
                                    else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        // 색상 드롭다운 메뉴
                        if (showColorDropdown) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column {
                                    colors.forEach { color ->
                                        ListItem(
                                            headlineContent = { 
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .background(
                                                                when (color) {
                                                                    "빨강" -> Color.Red
                                                                    "파랑" -> Color.Blue
                                                                    "초록" -> Color.Green
                                                                    "노랑" -> Color.Yellow
                                                                    "보라" -> Color.Magenta
                                                                    "주황" -> Color(0xFFFF9800)
                                                                    "분홍" -> Color(0xFFE91E63)
                                                                    "검정" -> Color.Black
                                                                    else -> MaterialTheme.colorScheme.primary
                                                                },
                                                                shape = MaterialTheme.shapes.small
                                                            )
                                                    )
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Text(color)
                                                }
                                            },
                                            modifier = Modifier.clickable {
                                                selectedColor = color
                                                showColorDropdown = false
                                            }
                                        )
                                        if (color != colors.last()) Divider()
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // 3. 과일 선택 Spinner (아이콘 포함)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "3. 과일 선택 Spinner (아이콘 포함)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Text(
                            text = "이모지 아이콘이 있는 드롭다운",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // 과일 Spinner 버튼
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showFruitDropdown = !showFruitDropdown },
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (selectedFruit != null) {
                                        Text(
                                            text = when (selectedFruit) {
                                                "사과" -> "🍎"
                                                "바나나" -> "🍌"
                                                "포도" -> "🍇"
                                                "수박" -> "🍉"
                                                "오렌지" -> "🍊"
                                                "키위" -> "🥝"
                                                "망고" -> "🥭"
                                                "파인애플" -> "🍍"
                                                else -> "🍎"
                                            },
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                    }
                                    Text(
                                        text = selectedFruit ?: "과일을 선택하세요",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (selectedFruit != null) MaterialTheme.colorScheme.onSurface 
                                               else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    if (showFruitDropdown) Icons.Default.KeyboardArrowUp 
                                    else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        // 과일 드롭다운 메뉴
                        if (showFruitDropdown) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column {
                                    fruits.forEach { fruit ->
                                        ListItem(
                                            headlineContent = { 
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = when (fruit) {
                                                            "사과" -> "🍎"
                                                            "바나나" -> "🍌"
                                                            "포도" -> "🍇"
                                                            "수박" -> "🍉"
                                                            "오렌지" -> "🍊"
                                                            "키위" -> "🥝"
                                                            "망고" -> "🥭"
                                                            "파인애플" -> "🍍"
                                                            else -> "🍎"
                                                        },
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Text(fruit)
                                                }
                                            },
                                            modifier = Modifier.clickable {
                                                selectedFruit = fruit
                                                showFruitDropdown = false
                                            }
                                        )
                                        if (fruit != fruits.last()) Divider()
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // 4. 언어 선택 Spinner (국가별 아이콘)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "4. 언어 선택 Spinner (국가별 아이콘)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Text(
                            text = "국가 플래그가 있는 언어 선택 드롭다운",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // 언어 Spinner 버튼
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showLanguageDropdown = !showLanguageDropdown },
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (selectedLanguage != null) {
                                        Text(
                                            text = when (selectedLanguage) {
                                                "한국어" -> "🇰🇷"
                                                "영어" -> "🇺🇸"
                                                "일본어" -> "🇯🇵"
                                                "중국어" -> "🇨🇳"
                                                "프랑스어" -> "🇫🇷"
                                                "독일어" -> "🇩🇪"
                                                "스페인어" -> "🇪🇸"
                                                "러시아어" -> "🇷🇺"
                                                else -> "🌍"
                                            },
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                    }
                                    Text(
                                        text = selectedLanguage ?: "언어를 선택하세요",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (selectedLanguage != null) MaterialTheme.colorScheme.onSurface 
                                               else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    if (showLanguageDropdown) Icons.Default.KeyboardArrowUp 
                                    else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        // 언어 드롭다운 메뉴
                        if (showLanguageDropdown) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column {
                                    languages.forEach { language ->
                                        ListItem(
                                            headlineContent = { 
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = when (language) {
                                                            "한국어" -> "🇰🇷"
                                                            "영어" -> "🇺🇸"
                                                            "일본어" -> "🇯🇵"
                                                            "중국어" -> "🇨🇳"
                                                            "프랑스어" -> "🇫🇷"
                                                            "독일어" -> "🇩🇪"
                                                            "스페인어" -> "🇪🇸"
                                                            "러시아어" -> "🇷🇺"
                                                            else -> "🌍"
                                                        },
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Text(language)
                                                }
                                            },
                                            modifier = Modifier.clickable {
                                                selectedLanguage = language
                                                showLanguageDropdown = false
                                            }
                                        )
                                        if (language != languages.last()) Divider()
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // 5. 선택된 항목들 표시
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "5. 선택된 항목들",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        val selections = listOf(
                            "도시" to selectedCity,
                            "색상" to selectedColor,
                            "과일" to selectedFruit,
                            "언어" to selectedLanguage
                        )
                        
                        selections.forEach { (label, value) ->
                            ListItem(
                                headlineContent = { Text("$label: ${value ?: "선택되지 않음"}") },
                                leadingContent = { 
                                    Icon(
                                        if (value != null) Icons.Default.CheckCircle 
                                        else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (value != null) MaterialTheme.colorScheme.primary 
                                               else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                            if (label != selections.last().first) Divider()
                        }
                    }
                }
            }
        }
    }
}
