package com.example.android_applicatoins.screens.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    onBackPressed: () -> Unit
) {
    var selectedImage by remember { mutableStateOf<GalleryImage?>(null) }
    var showImageDetail by remember { mutableStateOf(false) }
    var currentFilter by remember { mutableStateOf("all") }

    val images = remember {
        listOf(
            GalleryImage(1, "자연", "자연의 아름다움을 담은 이미지", "nature", Color.Green),
            GalleryImage(2, "도시", "현대적인 도시의 모습", "city", Color.Blue),
            GalleryImage(3, "동물", "귀여운 동물들의 모습", "animal", Color(0xFFFF9800)),
            GalleryImage(4, "음식", "맛있는 음식들의 모습", "food", Color.Red),
            GalleryImage(5, "여행", "아름다운 여행지의 풍경", "travel", Color(0xFF00BCD4)),
            GalleryImage(6, "예술", "창의적인 예술 작품들", "art", Color(0xFF9C27B0)),
            GalleryImage(7, "스포츠", "역동적인 스포츠의 순간들", "sports", Color.Yellow),
            GalleryImage(8, "기술", "최신 기술의 발전 모습", "tech", Color.Gray),
            GalleryImage(9, "음악", "음악과 관련된 이미지들", "music", Color(0xFFE91E63)),
            GalleryImage(10, "책", "지식의 보고인 책들의 모습", "books", Color(0xFF795548)),
            GalleryImage(11, "건강", "건강한 라이프스타일", "health", Color(0xFF4CAF50)),
            GalleryImage(12, "패션", "트렌디한 패션의 모습", "fashion", Color(0xFF3F51B5))
        )
    }

    val filteredImages = remember(currentFilter) {
        if (currentFilter == "all") images else images.filter { it.category == currentFilter }
    }

    val categories = remember {
        listOf("all", "nature", "city", "animal", "food", "travel", "art", "sports", "tech", "music", "books", "health", "fashion")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("갤러리") },
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
        ) {
            // 필터 버튼들
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = currentFilter == category,
                        onClick = { currentFilter = category },
                        label = {
                            Text(
                                when (category) {
                                    "all" -> "전체"
                                    "nature" -> "자연"
                                    "city" -> "도시"
                                    "animal" -> "동물"
                                    "food" -> "음식"
                                    "travel" -> "여행"
                                    "art" -> "예술"
                                    "sports" -> "스포츠"
                                    "tech" -> "기술"
                                    "music" -> "음악"
                                    "books" -> "책"
                                    "health" -> "건강"
                                    "fashion" -> "패션"
                                    else -> category
                                }
                            )
                        }
                    )
                }
            }

            // 이미지 그리드
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredImages) { image ->
                    GalleryImageCard(
                        image = image,
                        onClick = {
                            selectedImage = image
                            showImageDetail = true
                        }
                    )
                }
            }
        }
    }

    // 이미지 상세 모달
    if (showImageDetail && selectedImage != null) {
        AlertDialog(
            onDismissRequest = { showImageDetail = false },
            title = { Text(selectedImage!!.title) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 이미지 플레이스홀더
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(selectedImage!!.color, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "이미지",
                            modifier = Modifier.size(64.dp),
                            tint = Color.White
                        )
                    }
                    
                    Text("카테고리: ${selectedImage!!.category}")
                    Text("설명: ${selectedImage!!.description}")
                }
            },
            confirmButton = {
                TextButton(onClick = { showImageDetail = false }) {
                    Text("닫기")
                }
            }
        )
    }
}

@Composable
fun GalleryImageCard(
    image: GalleryImage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(image.color, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Image,
                    contentDescription = image.title,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = image.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

data class GalleryImage(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val color: Color
)
