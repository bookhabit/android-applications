package com.example.android_applicatoins.screens.native

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(0) }
    
    // SharedPreferences 상태
    var sharedPrefsData by remember { mutableStateOf("") }
    var sharedPrefsResult by remember { mutableStateOf("") }
    
    // 내부 저장소 상태
    var internalStorageData by remember { mutableStateOf("") }
    var internalStorageResult by remember { mutableStateOf("") }
    
    // 외부 저장소 상태
    var externalStorageData by remember { mutableStateOf("") }
    var externalStorageResult by remember { mutableStateOf("") }
    
    // SQLite 상태
    var sqliteName by remember { mutableStateOf("") }
    var sqliteResult by remember { mutableStateOf("") }
    
    // DB Helper
    val dbHelper = remember { MyDBHelper(context) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("데이터 저장 테스트", fontWeight = FontWeight.Bold) },
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 탭 선택
            TabRow(
                selectedTabIndex = currentTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    text = { Text("SharedPreferences") },
                    icon = { Icon(Icons.Default.Settings, "설정") }
                )
                Tab(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    text = { Text("내부 저장소") },
                    icon = { Icon(Icons.Default.Storage, "내부") }
                )
                Tab(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    text = { Text("외부 저장소") },
                    icon = { Icon(Icons.Default.Folder, "외부") }
                )
                Tab(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3 },
                    text = { Text("SQLite") },
                    icon = { Icon(Icons.Default.DataUsage, "DB") }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            when (currentTab) {
                0 -> SharedPreferencesTab(
                    data = sharedPrefsData,
                    onDataChange = { sharedPrefsData = it },
                    result = sharedPrefsResult,
                    onSave = {
                        val prefs = context.getSharedPreferences("TestPrefs", Context.MODE_PRIVATE)
                        val editor = prefs.edit()
                        editor.putString("test_data", sharedPrefsData)
                        editor.apply()
                        sharedPrefsResult = "SharedPreferences에 저장 완료!"
                    },
                    onLoad = {
                        val prefs = context.getSharedPreferences("TestPrefs", Context.MODE_PRIVATE)
                        val savedData = prefs.getString("test_data", "저장된 데이터 없음")
                        sharedPrefsResult = "불러온 데이터: $savedData"
                    }
                )
                1 -> InternalStorageTab(
                    data = internalStorageData,
                    onDataChange = { internalStorageData = it },
                    result = internalStorageResult,
                    onSave = {
                        try {
                            context.openFileOutput("test_internal.txt", Context.MODE_PRIVATE).use { stream ->
                                stream.write(internalStorageData.toByteArray())
                            }
                            internalStorageResult = "내부 저장소에 저장 완료!"
                        } catch (e: Exception) {
                            internalStorageResult = "저장 실패: ${e.message}"
                        }
                    },
                    onLoad = {
                        try {
                            val savedData = context.openFileInput("test_internal.txt").bufferedReader().use { it.readText() }
                            internalStorageResult = "불러온 데이터: $savedData"
                        } catch (e: Exception) {
                            internalStorageResult = "불러오기 실패: ${e.message}"
                        }
                    }
                )
                2 -> ExternalStorageTab(
                    data = externalStorageData,
                    onDataChange = { externalStorageData = it },
                    result = externalStorageResult,
                    onSave = {
                        try {
                            val file = File(context.getExternalFilesDir(null), "test_external.txt")
                            file.writeText(externalStorageData)
                            externalStorageResult = "외부 저장소에 저장 완료!\n경로: ${file.absolutePath}"
                        } catch (e: Exception) {
                            externalStorageResult = "저장 실패: ${e.message}"
                        }
                    },
                    onLoad = {
                        try {
                            val file = File(context.getExternalFilesDir(null), "test_external.txt")
                            val savedData = file.readText()
                            externalStorageResult = "불러온 데이터: $savedData"
                        } catch (e: Exception) {
                            externalStorageResult = "불러오기 실패: ${e.message}"
                        }
                    }
                )
                3 -> SQLiteTab(
                    name = sqliteName,
                    onNameChange = { sqliteName = it },
                    result = sqliteResult,
                    onSave = {
                        try {
                            val db = dbHelper.writableDatabase
                            val values = ContentValues()
                            values.put("name", sqliteName)
                            db.insert("users", null, values)
                            sqliteResult = "SQLite에 저장 완료!"
                        } catch (e: Exception) {
                            sqliteResult = "저장 실패: ${e.message}"
                        }
                    },
                    onLoad = {
                        try {
                            val db = dbHelper.readableDatabase
                            val cursor = db.rawQuery("SELECT * FROM users", null)
                            val names = mutableListOf<String>()
                            
                            if (cursor.moveToFirst()) {
                                do {
                                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                                    names.add(name)
                                } while (cursor.moveToNext())
                            }
                            cursor.close()
                            
                            sqliteResult = if (names.isNotEmpty()) {
                                "불러온 데이터:\n${names.joinToString("\n")}"
                            } else {
                                "저장된 데이터 없음"
                            }
                        } catch (e: Exception) {
                            sqliteResult = "불러오기 실패: ${e.message}"
                        }
                    },
                    onClear = {
                        try {
                            val db = dbHelper.writableDatabase
                            db.delete("users", null, null)
                            sqliteResult = "SQLite 데이터 삭제 완료!"
                        } catch (e: Exception) {
                            sqliteResult = "삭제 실패: ${e.message}"
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SharedPreferencesTab(
    data: String,
    onDataChange: (String) -> Unit,
    result: String,
    onSave: () -> Unit,
    onLoad: () -> Unit
) {
    var showDetails by remember { mutableStateOf(false) }
    
    StorageTabContent(
        title = "SharedPreferences 테스트",
        description = "앱 설정, 로그인 정보 등 소량 데이터 저장",
        data = data,
        onDataChange = onDataChange,
        result = result,
        onSave = onSave,
        onLoad = onLoad,
        color = Color(0xFFE3F2FD),
        textColor = Color(0xFF1976D2),
        showDetails = showDetails,
        onToggleDetails = { showDetails = !showDetails },
        detailsContent = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 실무 예시
                DetailCard(
                    title = "📱 실무에서 저장하는 데이터",
                    content = """
                        • 사용자 설정 (다크모드, 언어, 폰트 크기)
                        • 로그인 정보 (토큰, 사용자 ID)
                        • 앱 상태 (마지막 접속 시간, 튜토리얼 완료 여부)
                        • 알림 설정 (푸시 알림 ON/OFF, 소리 설정)
                        • 테마 설정 (색상, 배경)
                        • 검색 기록, 즐겨찾기
                    """.trimIndent()
                )
                
                // 사용 메소드
                DetailCard(
                    title = "🔧 사용하는 메소드",
                    content = """
                        • getSharedPreferences(name, mode)
                        • edit() - SharedPreferences.Editor 반환
                        • putString(key, value), putInt(key, value)
                        • putBoolean(key, value), putLong(key, value)
                        • getString(key, defaultValue)
                        • apply() - 비동기 저장
                        • commit() - 동기 저장
                    """.trimIndent()
                )
                
                // 동작원리
                DetailCard(
                    title = "⚙️ 동작원리",
                    content = """
                        • XML 파일 형태로 앱 내부 저장소에 저장
                        • key-value 쌍으로 데이터 관리
                        • 앱 삭제 시 함께 삭제됨
                        • 메인 스레드에서 안전하게 사용 가능
                        • apply()는 백그라운드에서 저장하여 UI 블로킹 방지
                        • commit()은 즉시 저장하지만 UI 블로킹 가능성 있음
                    """.trimIndent()
                )
            }
        }
    )
}

@Composable
fun InternalStorageTab(
    data: String,
    onDataChange: (String) -> Unit,
    result: String,
    onSave: () -> Unit,
    onLoad: () -> Unit
) {
    var showDetails by remember { mutableStateOf(false) }
    
    StorageTabContent(
        title = "내부 저장소 테스트",
        description = "앱 전용 공간, 다른 앱에서 접근 불가",
        data = data,
        onDataChange = onDataChange,
        result = result,
        onSave = onSave,
        onLoad = onLoad,
        color = Color(0xFFE8F5E8),
        textColor = Color(0xFF4CAF50),
        showDetails = showDetails,
        onToggleDetails = { showDetails = !showDetails },
        detailsContent = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 실무 예시
                DetailCard(
                    title = "📱 실무에서 저장하는 데이터",
                    content = """
                        • 앱 내부 캐시 파일 (이미지, 동영상 임시 저장)
                        • 사용자 생성 콘텐츠 (노트, 메모, 문서)
                        • 앱 데이터베이스 파일
                        • 설정 파일, 로그 파일
                        • 임시 파일, 다운로드 파일
                        • 암호화된 민감한 데이터
                    """.trimIndent()
                )
                
                // 사용 메소드
                DetailCard(
                    title = "🔧 사용하는 메소드",
                    content = """
                        • openFileOutput(filename, mode)
                        • openFileInput(filename)
                        • getFilesDir() - 내부 저장소 디렉토리
                        • getCacheDir() - 캐시 디렉토리
                        • deleteFile(filename)
                        • fileList() - 파일 목록 반환
                        • MODE_PRIVATE, MODE_APPEND, MODE_WORLD_READABLE
                    """.trimIndent()
                )
                
                // 동작원리
                DetailCard(
                    title = "⚙️ 동작원리",
                    content = """
                        • /data/data/패키지명/files/ 경로에 저장
                        • 앱 전용 공간으로 다른 앱에서 접근 불가
                        • 앱 삭제 시 모든 데이터 자동 삭제
                        • 파일 시스템 기반으로 직접 파일 I/O 수행
                        • 스트림을 통한 바이트 단위 읽기/쓰기
                        • 메모리 효율적이며 빠른 접근 속도
                    """.trimIndent()
                )
            }
        }
    )
}

@Composable
fun ExternalStorageTab(
    data: String,
    onDataChange: (String) -> Unit,
    result: String,
    onSave: () -> Unit,
    onLoad: () -> Unit
) {
    var showDetails by remember { mutableStateOf(false) }
    
    StorageTabContent(
        title = "외부 저장소 테스트",
        description = "사용자가 접근 가능한 공간",
        data = data,
        onDataChange = onDataChange,
        result = result,
        onSave = onSave,
        onLoad = onLoad,
        color = Color(0xFFFFEBEE),
        textColor = Color(0xFFF44336),
        showDetails = showDetails,
        onToggleDetails = { showDetails = !showDetails },
        detailsContent = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 실무 예시
                DetailCard(
                    title = "📱 실무에서 저장하는 데이터",
                    content = """
                        • 사용자가 다운로드한 파일들
                        • 앱에서 생성한 문서, 이미지, 동영상
                        • 백업 파일, 로그 파일
                        • 공유 가능한 콘텐츠
                        • 대용량 미디어 파일
                        • 사용자가 직접 관리하는 파일들
                    """.trimIndent()
                )
                
                // 사용 메소드
                DetailCard(
                    title = "🔧 사용하는 메소드",
                    content = """
                        • getExternalFilesDir(type) - 앱 전용 외부 저장소
                        • getExternalStorageDirectory() - 공용 외부 저장소
                        • Environment.getExternalStorageState()
                        • Environment.DIRECTORY_PICTURES, DIRECTORY_DOWNLOADS
                        • File 객체의 readText(), writeText()
                        • FileInputStream, FileOutputStream
                        • READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE 권한
                    """.trimIndent()
                )
                
                // 동작원리
                DetailCard(
                    title = "⚙️ 동작원리",
                    content = """
                        • /storage/emulated/0/ 경로에 저장
                        • 사용자가 파일 탐색기로 직접 접근 가능
                        • 앱 삭제 시에도 데이터 유지 (getExternalFilesDir 제외)
                        • 권한이 필요하며 Android 10+ 에서는 Scoped Storage 적용
                        • MediaStore API를 통한 미디어 파일 접근 권장
                        • 대용량 파일 저장에 적합하며 공유 가능
                    """.trimIndent()
                )
            }
        }
    )
}

@Composable
fun SQLiteTab(
    name: String,
    onNameChange: (String) -> Unit,
    result: String,
    onSave: () -> Unit,
    onLoad: () -> Unit,
    onClear: () -> Unit
) {
    var showDetails by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SQLite 테스트",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9C27B0)
        )
        
        Text(
            text = "구조화된 데이터 저장/조회",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "사용자 이름",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9C27B0)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("이름을 입력하세요") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onSave,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Save, "저장", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("저장")
                    }
                    
                    Button(
                        onClick = onLoad,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Download, "불러오기", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("불러오기")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = onClear,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, "삭제", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("모든 데이터 삭제")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 상세 정보 토글 버튼
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "상세 정보",
                        tint = Color(0xFF9C27B0)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "상세 정보 보기",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9C27B0)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = showDetails,
                        onCheckedChange = { showDetails = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF9C27B0),
                            checkedTrackColor = Color(0xFF9C27B0).copy(alpha = 0.5f)
                        )
                    )
                }
                
                if (showDetails) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 실무 예시
                        DetailCard(
                            title = "📱 실무에서 저장하는 데이터",
                            content = """
                                • 사용자 계정 정보 (이름, 이메일, 프로필)
                                • 게시글, 댓글, 메시지
                                • 상품 정보, 주문 내역
                                • 일정, 할 일, 메모
                                • 게임 점수, 업적, 설정
                                • 센서 데이터, 로그 기록
                            """.trimIndent()
                        )
                        
                        // 사용 메소드
                        DetailCard(
                            title = "🔧 사용하는 메소드",
                            content = """
                                • SQLiteOpenHelper 상속 클래스 생성
                                • onCreate(db) - 테이블 생성
                                • onUpgrade(db, oldVersion, newVersion)
                                • getWritableDatabase(), getReadableDatabase()
                                • insert(table, nullColumnHack, values)
                                • query(table, columns, selection, selectionArgs, ...)
                                • update(table, values, whereClause, whereArgs)
                                • delete(table, whereClause, whereArgs)
                            """.trimIndent()
                        )
                        
                        // 동작원리
                        DetailCard(
                            title = "⚙️ 동작원리",
                            content = """
                                • SQLite 데이터베이스 엔진 사용
                                • ACID 트랜잭션 지원 (원자성, 일관성, 격리성, 지속성)
                                • 테이블 구조로 데이터 정규화
                                • SQL 쿼리 언어로 데이터 조작
                                • 인덱스를 통한 빠른 검색
                                • 외래키를 통한 관계 설정
                                • 앱 내부에 .db 파일로 저장
                            """.trimIndent()
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (result.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = result,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun StorageTabContent(
    title: String,
    description: String,
    data: String,
    onDataChange: (String) -> Unit,
    result: String,
    onSave: () -> Unit,
    onLoad: () -> Unit,
    color: Color,
    textColor: Color,
    showDetails: Boolean = false,
    onToggleDetails: (() -> Unit)? = null,
    detailsContent: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        
        Text(
            text = description,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = color)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "테스트 데이터",
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = data,
                    onValueChange = onDataChange,
                    label = { Text("저장할 데이터를 입력하세요") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onSave,
                        colors = ButtonDefaults.buttonColors(containerColor = textColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Save, "저장", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("저장")
                    }
                    
                    Button(
                        onClick = onLoad,
                        colors = ButtonDefaults.buttonColors(containerColor = textColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Download, "불러오기", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("불러오기")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 상세 정보 토글 버튼 (있는 경우에만 표시)
        if (onToggleDetails != null && detailsContent != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "상세 정보",
                            tint = textColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "상세 정보 보기",
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = showDetails,
                            onCheckedChange = { onToggleDetails() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = textColor,
                                checkedTrackColor = textColor.copy(alpha = 0.5f)
                            )
                        )
                    }
                    
                    if (showDetails) {
                        Spacer(modifier = Modifier.height(16.dp))
                        detailsContent()
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        if (result.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = result,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// DetailCard 컴포넌트
@Composable
fun DetailCard(
    title: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                color = Color.Gray,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

// SQLite Helper 클래스
class MyDBHelper(context: Context) : SQLiteOpenHelper(context, "TestDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)")
    }
    
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }
}
