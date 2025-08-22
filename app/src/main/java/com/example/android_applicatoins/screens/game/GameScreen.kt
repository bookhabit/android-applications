package com.example.android_applicatoins.screens.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.android_applicatoins.components.AppCard
import com.example.android_applicatoins.model.AppItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    onBackPressed: () -> Unit,
    onAppSelected: (String) -> Unit
) {
    val gameApps = listOf(
        AppItem(
            id = "tictactoe",
            title = "틱택토",
            description = "클래식한 틱택토 게임을 즐기세요",
            icon = Icons.Default.Games,
            route = "tictactoe"
        ),
        AppItem(
            id = "memory",
            title = "메모리 게임",
            description = "카드 짝 맞추기 게임을 즐기세요",
            icon = Icons.Default.Psychology,
            route = "memory"
        ),
        AppItem(
            id = "snake",
            title = "스네이크 게임",
            description = "전통적인 스네이크 게임을 즐기세요",
            icon = Icons.Default.SmartToy,
            route = "snake"
        ),
        AppItem(
            id = "quiz",
            title = "퀴즈",
            description = "다양한 주제의 퀴즈를 풀어보세요",
            icon = Icons.Default.Quiz,
            route = "quiz"
        ),
        AppItem(
            id = "2048",
            title = "2048",
            description = "숫자 슬라이딩 퍼즐 게임을 즐기세요",
            icon = Icons.Default.Casino,
            route = "2048"
        ),
        AppItem(
            id = "avoid-game",
            title = "회피 게임",
            description = "장애물을 피하는 게임을 즐기세요",
            icon = Icons.Default.SportsEsports,
            route = "avoid-game"
        ),
        AppItem(
            id = "bowling",
            title = "볼링",
            description = "볼링 게임을 즐기세요",
            icon = Icons.Default.SportsCricket,
            route = "bowling"
        ),
        AppItem(
            id = "box-opening",
            title = "상자 열기",
            description = "상자를 열어보는 게임을 즐기세요",
            icon = Icons.Default.Inventory,
            route = "box-opening"
        ),
        AppItem(
            id = "brick-breaker",
            title = "벽돌 깨기",
            description = "클래식한 벽돌 깨기 게임을 즐기세요",
            icon = Icons.Default.Build,
            route = "brick-breaker"
        ),
        AppItem(
            id = "cup-trick",
            title = "컵 트릭",
            description = "컵을 이용한 트릭 게임을 즐기세요",
            icon = Icons.Default.Casino,
            route = "cup-trick"
        ),
        AppItem(
            id = "infinite-runner",
            title = "무한 러너",
            description = "끝없이 달리는 게임을 즐기세요",
            icon = Icons.Default.DirectionsRun,
            route = "infinite-runner"
        ),
        AppItem(
            id = "jump-game",
            title = "점프 게임",
            description = "점프하는 게임을 즐기세요",
            icon = Icons.Default.KeyboardArrowUp,
            route = "jump-game"
        ),
        AppItem(
            id = "mini-golf",
            title = "미니 골프",
            description = "미니 골프 게임을 즐기세요",
            icon = Icons.Default.SportsGolf,
            route = "mini-golf"
        ),
        AppItem(
            id = "numberguess",
            title = "숫자 맞추기",
            description = "숫자를 맞추는 게임을 즐기세요",
            icon = Icons.Default.Psychology,
            route = "numberguess"
        ),
        AppItem(
            id = "online-tictactoe",
            title = "온라인 틱택토",
            description = "온라인으로 틱택토를 즐기세요",
            icon = Icons.Default.Cloud,
            route = "online-tictactoe"
        ),
        AppItem(
            id = "pong",
            title = "퐁",
            description = "클래식한 퐁 게임을 즐기세요",
            icon = Icons.Default.SportsTennis,
            route = "pong"
        ),
        AppItem(
            id = "puzzle",
            title = "퍼즐",
            description = "다양한 퍼즐을 풀어보세요",
            icon = Icons.Default.Extension,
            route = "puzzle"
        ),
        AppItem(
            id = "quiz-challenge",
            title = "퀴즈 챌린지",
            description = "퀴즈 챌린지에 도전하세요",
            icon = Icons.Default.EmojiEvents,
            route = "quiz-challenge"
        ),
        AppItem(
            id = "race-game",
            title = "레이스 게임",
            description = "레이싱 게임을 즐기세요",
            icon = Icons.Default.Speed,
            route = "race-game"
        ),
        AppItem(
            id = "rhythm",
            title = "리듬 게임",
            description = "리듬에 맞춰 게임을 즐기세요",
            icon = Icons.Default.MusicNote,
            route = "rhythm"
        ),
        AppItem(
            id = "rock-paper-scissors",
            title = "가위바위보",
            description = "가위바위보 게임을 즐기세요",
            icon = Icons.Default.Gesture,
            route = "rock-paper-scissors"
        ),
        AppItem(
            id = "rope-climbing",
            title = "줄 오르기",
            description = "줄 오르기 게임을 즐기세요",
            icon = Icons.Default.TrendingUp,
            route = "rope-climbing"
        ),
        AppItem(
            id = "roulette",
            title = "룰렛",
            description = "룰렛 게임을 즐기세요",
            icon = Icons.Default.Casino,
            route = "roulette"
        ),
        AppItem(
            id = "scratch-lottery",
            title = "스크래치 복권",
            description = "스크래치 복권 게임을 즐기세요",
            icon = Icons.Default.Casino,
            route = "scratch-lottery"
        ),
        AppItem(
            id = "snail-race",
            title = "달팽이 레이스",
            description = "달팽이 레이스를 즐기세요",
            icon = Icons.Default.Pets,
            route = "snail-race"
        ),
        AppItem(
            id = "sudoku",
            title = "스도쿠",
            description = "스도쿠 퍼즐을 풀어보세요",
            icon = Icons.Default.GridOn,
            route = "sudoku"
        ),
        AppItem(
            id = "wordgame",
            title = "단어 게임",
            description = "단어 게임을 즐기세요",
            icon = Icons.Default.TextFields,
            route = "wordgame"
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("게임") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(gameApps) { appItem ->
                AppCard(
                    appItem = appItem,
                    onClick = {
                        onAppSelected(appItem.id)
                    }
                )
            }
        }
    }
}
