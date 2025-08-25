package com.example.android_applicatoins.screens.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieInfoScreen(
    onBackPressed: () -> Unit
) {
    var selectedMovie by remember { mutableStateOf<Movie?>(null) }
    var showMovieDetail by remember { mutableStateOf(false) }

    val movies = remember {
        listOf(
            Movie(
                id = 1,
                title = "인셉션",
                director = "크리스토퍼 놀란",
                year = 2010,
                rating = 8.8f,
                genre = "SF/액션",
                description = "꿈과 현실의 경계를 넘나드는 미스터리한 SF 영화",
                posterUrl = "https://example.com/inception.jpg"
            ),
            Movie(
                id = 2,
                title = "기생충",
                director = "봉준호",
                year = 2019,
                rating = 8.6f,
                genre = "드라마/스릴러",
                description = "계급 갈등을 다룬 한국 영화의 걸작",
                posterUrl = "https://example.com/parasite.jpg"
            ),
            Movie(
                id = 3,
                title = "어벤져스: 엔드게임",
                director = "루소 형제",
                year = 2019,
                rating = 8.4f,
                genre = "액션/어드벤처",
                description = "마블 시리즈의 대단원을 장식하는 블록버스터",
                posterUrl = "https://example.com/endgame.jpg"
            ),
            Movie(
                id = 4,
                title = "라라랜드",
                director = "데미언 셔젤",
                year = 2016,
                rating = 8.0f,
                genre = "뮤지컬/로맨스",
                description = "재즈와 뮤지컬을 결합한 현대적인 로맨스",
                posterUrl = "https://example.com/lalaland.jpg"
            ),
            Movie(
                id = 5,
                title = "토이스토리 4",
                director = "조시 쿨리",
                year = 2019,
                rating = 7.7f,
                genre = "애니메이션/어드벤처",
                description = "장난감들의 새로운 모험을 그린 애니메이션",
                posterUrl = "https://example.com/toystory4.jpg"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("영화 정보") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "뒤로가기")
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
            item {
                Text(
                    text = "인기 영화",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(movies) { movie ->
                MovieCard(
                    movie = movie,
                    onClick = {
                        selectedMovie = movie
                        showMovieDetail = true
                    }
                )
            }
        }
    }

    // 영화 상세 정보 모달
    if (showMovieDetail && selectedMovie != null) {
        AlertDialog(
            onDismissRequest = { showMovieDetail = false },
            title = { Text(selectedMovie!!.title) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("감독: ${selectedMovie!!.director}")
                    Text("개봉년도: ${selectedMovie!!.year}")
                    Text("평점: ${selectedMovie!!.rating}/10")
                    Text("장르: ${selectedMovie!!.genre}")
                    Text("줄거리: ${selectedMovie!!.description}")
                }
            },
            confirmButton = {
                TextButton(onClick = { showMovieDetail = false }) {
                    Text("닫기")
                }
            }
        )
    }
}

@Composable
fun MovieCard(
    movie: Movie,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 영화 포스터 (플레이스홀더)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Movie,
                    contentDescription = "영화 포스터",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 영화 정보
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.director,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "평점",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${movie.rating}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = movie.year.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.genre,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "자세히 보기",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class Movie(
    val id: Int,
    val title: String,
    val director: String,
    val year: Int,
    val rating: Float,
    val genre: String,
    val description: String,
    val posterUrl: String
)
