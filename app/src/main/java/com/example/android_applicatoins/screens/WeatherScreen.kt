package com.example.android_applicatoins.screens

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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import java.text.SimpleDateFormat
import java.util.*

data class WeatherInfo(
    val date: String,
    val temperature: Int,
    val condition: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    onBackPressed: () -> Unit
) {
    var currentTemp by remember { mutableStateOf(22) }
    var currentCondition by remember { mutableStateOf("맑음") }
    var currentIcon by remember { mutableStateOf(Icons.Default.WbSunny) }
    
    // 샘플 날씨 데이터
    val weatherForecast = remember {
        listOf<WeatherInfo>(
            WeatherInfo("오늘", 22, "맑음", Icons.Default.WbSunny),
            WeatherInfo("내일", 18, "흐림", Icons.Default.Cloud),
            WeatherInfo("모레", 25, "맑음", Icons.Default.WbSunny),
            WeatherInfo("3일 후", 20, "비", Icons.Default.Opacity),
            WeatherInfo("4일 후", 23, "맑음", Icons.Default.WbSunny),
            WeatherInfo("5일 후", 19, "흐림", Icons.Default.Cloud),
            WeatherInfo("6일 후", 26, "맑음", Icons.Default.WbSunny)
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("날씨") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "뒤로가기")
                    }
                },
                actions = {
                    IconButton(onClick = { /* 새로고침 */ }) {
                        Icon(Icons.Default.Refresh, "새로고침")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 현재 날씨 카드
            item {
                CurrentWeatherCard(
                    temperature = currentTemp,
                    condition = currentCondition,
                    icon = currentIcon
                )
            }
            
            // 시간별 날씨
            item {
                Text(
                    text = "시간별 날씨",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                HourlyWeatherRow()
            }
            
            // 일주일 예보
            item {
                Text(
                    text = "7일 예보",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            items(weatherForecast) { weather ->
                WeatherForecastItem(weather = weather)
            }
        }
    }
}

@Composable
fun CurrentWeatherCard(
    temperature: Int,
    condition: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = condition,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "${temperature}°",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = condition,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = SimpleDateFormat("yyyy년 MM월 dd일 EEEE", Locale.KOREAN)
                    .format(Date()),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun HourlyWeatherRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(6) { hour ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${hour * 4}시",
                    style = MaterialTheme.typography.bodyMedium
                )
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = "맑음",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${20 + (hour * 2)}°",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun WeatherForecastItem(weather: WeatherInfo) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = weather.date,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
            
            Icon(
                imageVector = weather.icon,
                contentDescription = weather.condition,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = weather.condition,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "${weather.temperature}°",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
