package com.example.android_applicatoins

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android_applicatoins.ui.theme.AndroidapplicatoinsTheme

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidapplicatoinsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SecondActivityContent(
                        onBackPressed = { finish() },
                        onResult = { selectedOption, inputText ->
                            // 결과 데이터를 인텐트에 담아서 전달
                            val resultIntent = Intent().apply {
                                putExtra("selected_option", selectedOption)
                                putExtra("input_text", inputText)
                                putExtra("timestamp", System.currentTimeMillis())
                            }
                            
                            // RESULT_OK와 함께 결과 전달
                            setResult(ComponentActivity.RESULT_OK, resultIntent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SecondActivityContent(
    onBackPressed: () -> Unit,
    onResult: (String, String) -> Unit
) {
    var selectedOption by remember { mutableStateOf("") }
    var inputText by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "두 번째 액티비티입니다!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Text(
            text = "데이터를 입력하고 결과를 전달하세요.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // 옵션 선택
        Text(
            text = "옵션 선택:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            listOf("옵션 A", "옵션 B", "옵션 C").forEach { option ->
                FilterChip(
                    selected = selectedOption == option,
                    onClick = { selectedOption = option },
                    label = { Text(option) }
                )
            }
        }
        
        // 텍스트 입력
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("텍스트 입력") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onBackPressed,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("취소")
            }
            
            Button(
                onClick = {
                    onResult(selectedOption, inputText)
                },
                enabled = selectedOption.isNotEmpty() && inputText.isNotEmpty()
            ) {
                Text("결과 전달")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SecondActivityContentPreview() {
    AndroidapplicatoinsTheme {
        SecondActivityContent(
            onBackPressed = {},
            onResult = { _, _ -> }
        )
    }
}
