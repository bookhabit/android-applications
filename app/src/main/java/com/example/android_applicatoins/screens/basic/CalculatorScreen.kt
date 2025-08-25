package com.example.android_applicatoins.screens.basic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    onBackPressed: () -> Unit
) {
    var display by remember { mutableStateOf("0") }
    var operation by remember { mutableStateOf<String?>(null) }
    var firstNumber by remember { mutableStateOf<Double?>(null) }
    var newNumber by remember { mutableStateOf(true) }
    
    fun appendNumber(number: String) {
        if (newNumber) {
            display = number
            newNumber = false
        } else {
            if (display == "0") {
                display = number
            } else {
                display += number
            }
        }
    }
    
    fun appendDecimal() {
        if (newNumber) {
            display = "0."
            newNumber = false
        } else if (!display.contains(".")) {
            display += "."
        }
    }
    
    fun setOperation(op: String) {
        firstNumber = display.toDoubleOrNull()
        operation = op
        newNumber = true
    }
    
    fun calculate() {
        val secondNumber = display.toDoubleOrNull()
        if (firstNumber != null && operation != null && secondNumber != null) {
            val result = when (operation) {
                "+" -> firstNumber!! + secondNumber
                "-" -> firstNumber!! - secondNumber
                "×" -> firstNumber!! * secondNumber
                "÷" -> if (secondNumber != 0.0) firstNumber!! / secondNumber else Double.NaN
                else -> secondNumber
            }
            
            display = if (result.isNaN()) "Error" else result.toString()
            operation = null
            firstNumber = null
            newNumber = true
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("계산기") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 디스플레이
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = display,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 계산기 버튼들
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 첫 번째 줄: AC, +/-, %, ÷
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalculatorButton(
                        text = "AC",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            display = "0"
                            operation = null
                            firstNumber = null
                            newNumber = true
                        }
                    )
                    CalculatorButton(
                        text = "+/-",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (display != "0") {
                                display = if (display.startsWith("-")) display.substring(1) else "-$display"
                            }
                        }
                    )
                    CalculatorButton(
                        text = "%",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val number = display.toDoubleOrNull() ?: return@CalculatorButton
                            display = (number / 100).toString()
                            newNumber = true
                        }
                    )
                    CalculatorButton(
                        text = "÷",
                        modifier = Modifier.weight(1f),
                        isOperation = true,
                        onClick = { setOperation("÷") }
                    )
                }
                
                // 두 번째 줄: 7, 8, 9, ×
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalculatorButton(
                        text = "7",
                        modifier = Modifier.weight(1f),
                        onClick = { appendNumber("7") }
                    )
                    CalculatorButton(
                        text = "8",
                        modifier = Modifier.weight(1f),
                        onClick = { appendNumber("8") }
                    )
                    CalculatorButton(
                        text = "9",
                        modifier = Modifier.weight(1f),
                        onClick = { appendNumber("9") }
                    )
                    CalculatorButton(
                        text = "×",
                        modifier = Modifier.weight(1f),
                        isOperation = true,
                        onClick = { setOperation("×") }
                    )
                }
                
                // 세 번째 줄: 4, 5, 6, -
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalculatorButton(
                        text = "4",
                        modifier = Modifier.weight(1f),
                        onClick = { appendNumber("4") }
                    )
                    CalculatorButton(
                        text = "5",
                        modifier = Modifier.weight(1f),
                        onClick = { appendNumber("5") }
                    )
                    CalculatorButton(
                        text = "6",
                        modifier = Modifier.weight(1f),
                        onClick = { appendNumber("6") }
                    )
                    CalculatorButton(
                        text = "-",
                        modifier = Modifier.weight(1f),
                        isOperation = true,
                        onClick = { setOperation("-") }
                    )
                }
                
                // 네 번째 줄: 1, 2, 3, +
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalculatorButton(
                        text = "1",
                        modifier = Modifier.weight(1f),
                        onClick = { appendNumber("1") }
                    )
                    CalculatorButton(
                        text = "2",
                        modifier = Modifier.weight(1f),
                        onClick = { appendNumber("2") }
                    )
                    CalculatorButton(
                        text = "3",
                        modifier = Modifier.weight(1f),
                        onClick = { appendNumber("3") }
                    )
                    CalculatorButton(
                        text = "+",
                        modifier = Modifier.weight(1f),
                        isOperation = true,
                        onClick = { setOperation("+") }
                    )
                }
                
                // 다섯 번째 줄: 0, ., =
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalculatorButton(
                        text = "0",
                        modifier = Modifier.weight(2f),
                        onClick = { appendNumber("0") }
                    )
                    CalculatorButton(
                        text = ".",
                        modifier = Modifier.weight(1f),
                        onClick = { appendDecimal() }
                    )
                    CalculatorButton(
                        text = "=",
                        modifier = Modifier.weight(1f),
                        isOperation = true,
                        onClick = { calculate() }
                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    isOperation: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(64.dp)
            .clip(CircleShape),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isOperation) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = if (isOperation) 
                MaterialTheme.colorScheme.onPrimary 
            else 
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
