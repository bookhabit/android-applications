package com.example.android_applicatoins.screens.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onBackPressed: () -> Unit
) {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showEventDialog by remember { mutableStateOf(false) }
    var events by remember { mutableStateOf(mutableMapOf<String, List<String>>()) }

    val yearMonth = YearMonth.from(currentDate)
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
    val daysInMonth = lastDayOfMonth.dayOfMonth

    val calendarDays = remember(yearMonth) {
        val days = mutableListOf<CalendarDay>()
        
        // 이전 달의 마지막 날들
        val prevMonth = yearMonth.minusMonths(1)
        val prevMonthLastDay = prevMonth.atEndOfMonth()
        for (i in (firstDayOfWeek - 1) downTo 1) {
            val day = prevMonthLastDay.minusDays(i.toLong())
            days.add(CalendarDay(day, false))
        }
        
        // 현재 달의 날들
        for (day in 1..daysInMonth) {
            val date = yearMonth.atDay(day)
            days.add(CalendarDay(date, true))
        }
        
        // 다음 달의 첫 날들
        val nextMonth = yearMonth.plusMonths(1)
        val remainingDays = 42 - days.size // 6주 * 7일 = 42
        for (day in 1..remainingDays) {
            val date = nextMonth.atDay(day)
            days.add(CalendarDay(date, false))
        }
        
        days
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("캘린더") },
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
            // 월 네비게이션
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        currentDate = currentDate.minusMonths(1)
                    }
                ) {
                    Icon(Icons.Default.ChevronLeft, "이전 달")
                }
                
                Text(
                    text = "${yearMonth.year}년 ${yearMonth.month.getDisplayName(TextStyle.FULL, Locale.KOREAN)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = {
                        currentDate = currentDate.plusMonths(1)
                    }
                ) {
                    Icon(Icons.Default.ChevronRight, "다음 달")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 요일 헤더
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                val weekdays = listOf("일", "월", "화", "수", "목", "금", "토")
                weekdays.forEach { day ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (day == "일") Color.Red else if (day == "토") Color.Blue else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 캘린더 그리드
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(300.dp)
            ) {
                items(calendarDays) { calendarDay ->
                    CalendarDayItem(
                        calendarDay = calendarDay,
                        isSelected = selectedDate == calendarDay.date,
                        hasEvents = events[calendarDay.date.toString()]?.isNotEmpty() == true,
                        onClick = {
                            if (calendarDay.isCurrentMonth) {
                                selectedDate = calendarDay.date
                                showEventDialog = true
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 선택된 날짜 정보
            selectedDate?.let { date ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "${date.year}년 ${date.month.getDisplayName(TextStyle.FULL, Locale.KOREAN)} ${date.dayOfMonth}일",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        val dateEvents = events[date.toString()] ?: emptyList()
                        if (dateEvents.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "일정:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            dateEvents.forEach { event ->
                                Text(
                                    text = "• $event",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "등록된 일정이 없습니다.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // 일정 추가 다이얼로그
    if (showEventDialog && selectedDate != null) {
        var eventText by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showEventDialog = false },
            title = { Text("일정 추가") },
            text = {
                Column {
                    Text("${selectedDate!!.month.getDisplayName(TextStyle.FULL, Locale.KOREAN)} ${selectedDate!!.dayOfMonth}일")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = eventText,
                        onValueChange = { eventText = it },
                        label = { Text("일정을 입력하세요") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (eventText.isNotBlank()) {
                            val dateKey = selectedDate!!.toString()
                            val currentEvents = events[dateKey] ?: emptyList()
                            events[dateKey] = currentEvents + eventText
                            events = events.toMutableMap()
                            showEventDialog = false
                        }
                    }
                ) {
                    Text("추가")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEventDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}

@Composable
fun CalendarDayItem(
    calendarDay: CalendarDay,
    isSelected: Boolean,
    hasEvents: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .background(
                color = when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    calendarDay.isCurrentMonth -> MaterialTheme.colorScheme.surface
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                shape = MaterialTheme.shapes.small
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = calendarDay.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    calendarDay.isCurrentMonth -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            
            if (hasEvents) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }
    }
}

data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean
)
