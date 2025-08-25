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
import java.text.SimpleDateFormat
import java.util.*

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onBackPressed: () -> Unit
) {
    var notes by remember { mutableStateOf(listOf<Note>()) }
    var nextId by remember { mutableStateOf(1) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("메모장") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "뒤로가기")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "새 메모")
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes) { note ->
                NoteCard(
                    note = note,
                    onClick = { selectedNote = note },
                    onDelete = {
                        notes = notes.filter { it.id != note.id }
                    }
                )
            }
        }
        
        // 새 메모 추가 다이얼로그
        if (showAddDialog) {
            AddNoteDialog(
                onDismiss = { showAddDialog = false },
                onSave = { title, content ->
                    notes = notes + Note(nextId, title, content)
                    nextId++
                    showAddDialog = false
                }
            )
        }
        
        // 메모 상세 보기 다이얼로그
        selectedNote?.let { note ->
            NoteDetailDialog(
                note = note,
                onDismiss = { selectedNote = null },
                onDelete = {
                    notes = notes.filter { it.id != note.id }
                    selectedNote = null
                }
            )
        }
    }
}

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "삭제")
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    .format(Date(note.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("새 메모") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("제목") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("내용") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onSave(title, content)
                    }
                }
            ) {
                Text("저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailDialog(
    note: Note,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(note.title) },
        text = {
            Column {
                Text(note.content)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        .format(Date(note.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("닫기")
            }
        },
        dismissButton = {
            TextButton(onClick = onDelete) {
                Text("삭제")
            }
        }
    )
}
