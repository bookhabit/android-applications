package com.example.android_applicatoins.screens.basic

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

data class TodoItem(
    val id: Int,
    val text: String,
    val isCompleted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    onBackPressed: () -> Unit
) {
    var todoText by remember { mutableStateOf("") }
    var todos by remember { mutableStateOf(listOf<TodoItem>()) }
    var nextId by remember { mutableStateOf(1) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("할일 관리") },
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
                .padding(16.dp)
        ) {
            // 입력 필드
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = todoText,
                    onValueChange = { todoText = it },
                    label = { Text("할일을 입력하세요") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = {
                        if (todoText.isNotBlank()) {
                            todos = todos + TodoItem(nextId, todoText)
                            nextId++
                            todoText = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, "추가")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 할일 목록
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(todos) { todo ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = todo.isCompleted,
                                onCheckedChange = { checked ->
                                    todos = todos.map { 
                                        if (it.id == todo.id) it.copy(isCompleted = checked) else it 
                                    }
                                }
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = todo.text,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (todo.isCompleted) FontWeight.Normal else FontWeight.Medium
                                )
                            )
                            
                            IconButton(
                                onClick = {
                                    todos = todos.filter { it.id != todo.id }
                                }
                            ) {
                                Icon(Icons.Default.Delete, "삭제")
                            }
                        }
                    }
                }
            }
        }
    }
}
