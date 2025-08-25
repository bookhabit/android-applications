package com.example.android_applicatoins.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicTacToeScreen(
    onBackPressed: () -> Unit
) {
    var board by remember { mutableStateOf(Array(9) { "" }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var gameOver by remember { mutableStateOf(false) }
    var winner by remember { mutableStateOf<String?>(null) }
    
    val gameLogic = remember {
        object {
            fun makeMove(index: Int) {
                board[index] = currentPlayer
                
                if (checkWinner()) {
                    winner = currentPlayer
                    gameOver = true
                } else if (board.all { it.isNotEmpty() }) {
                    gameOver = true
                } else {
                    currentPlayer = if (currentPlayer == "X") "O" else "X"
                }
            }
            
            fun checkWinner(): Boolean {
                val winPatterns = arrayOf(
                    // 가로
                    intArrayOf(0, 1, 2),
                    intArrayOf(3, 4, 5),
                    intArrayOf(6, 7, 8),
                    // 세로
                    intArrayOf(0, 3, 6),
                    intArrayOf(1, 4, 7),
                    intArrayOf(2, 5, 8),
                    // 대각선
                    intArrayOf(0, 4, 8),
                    intArrayOf(2, 4, 6)
                )
                
                for (pattern in winPatterns) {
                    if (board[pattern[0]].isNotEmpty() &&
                        board[pattern[0]] == board[pattern[1]] &&
                        board[pattern[0]] == board[pattern[2]]) {
                        return true
                    }
                }
                return false
            }
            
            fun resetGame() {
                board = Array(9) { "" }
                currentPlayer = "X"
                gameOver = false
                winner = null
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("틱택토") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "뒤로가기")
                    }
                },
                actions = {
                    IconButton(onClick = { gameLogic.resetGame() }) {
                        Icon(Icons.Default.Refresh, "새 게임")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 게임 상태 표시
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (winner != null) {
                        Text(
                            text = "승자: $winner",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else if (gameOver) {
                        Text(
                            text = "무승부!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        Text(
                            text = "현재 플레이어: $currentPlayer",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 게임 보드
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (row in 0..2) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (col in 0..2) {
                            val index = row * 3 + col
                            GameCell(
                                value = board[index],
                                onClick = {
                                    if (board[index].isEmpty() && !gameOver) {
                                        gameLogic.makeMove(index)
                                    }
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 새 게임 버튼
            Button(
                onClick = { gameLogic.resetGame() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("새 게임")
            }
        }
    }
}

@Composable
fun GameCell(
    value: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (value) {
                "X" -> {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "X",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                "O" -> {
                    Text(
                        text = "O",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                else -> {
                    // 빈 셀
                }
            }
        }
    }
}
