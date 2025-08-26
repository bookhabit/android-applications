package com.example.android_applicatoins.screens.native

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

data class Contact(
    val id: String,
    val name: String,
    val phone: String,
    val email: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsAppScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    var contacts by remember { mutableStateOf(listOf<Contact>()) }
    var hasPermission by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newContactName by remember { mutableStateOf("") }
    var newContactPhone by remember { mutableStateOf("") }
    var newContactEmail by remember { mutableStateOf("") }

    fun loadContacts(contentResolver: ContentResolver) {
        val contactList = mutableListOf<Contact>()
        
        try {
            // RawContacts를 통해 연락처 정보 가져오기
            val rawContactsProjection = arrayOf(
                ContactsContract.RawContacts._ID,
                ContactsContract.RawContacts.CONTACT_ID
            )
            
            contentResolver.query(
                ContactsContract.RawContacts.CONTENT_URI,
                rawContactsProjection,
                null,
                null,
                null
            )?.use { rawContactsCursor ->
                while (rawContactsCursor.moveToNext()) {
                    val rawContactId = rawContactsCursor.getString(
                        rawContactsCursor.getColumnIndexOrThrow(ContactsContract.RawContacts._ID)
                    )
                    val contactId = rawContactsCursor.getString(
                        rawContactsCursor.getColumnIndexOrThrow(ContactsContract.RawContacts.CONTACT_ID)
                    )
                    
                    var name = ""
                    var phone = ""
                    var email = ""
                    
                    // 이름 가져오기
                    val nameCursor = contentResolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME),
                        "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                        arrayOf(rawContactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE),
                        null
                    )
                    
                    nameCursor?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            name = cursor.getString(
                                cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME)
                            )
                        }
                    }
                    
                    // 전화번호 가져오기
                    val phoneCursor = contentResolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                        "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                        arrayOf(rawContactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE),
                        null
                    )
                    
                    phoneCursor?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            phone = cursor.getString(
                                cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )
                        }
                    }
                    
                    // 이메일 가져오기
                    val emailCursor = contentResolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS),
                        "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                        arrayOf(rawContactId, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE),
                        null
                    )
                    
                    emailCursor?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            email = cursor.getString(
                                cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS)
                            )
                        }
                    }
                    
                    // 이름이 있는 연락처만 추가
                    if (name.isNotEmpty()) {
                        contactList.add(Contact(contactId, name, phone, email))
                    }
                }
            }
            
            contacts = contactList.sortedBy { it.name }
        } catch (e: Exception) {
            android.widget.Toast.makeText(
                context,
                "연락처 로드 실패: ${e.message}",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    fun addNewContact() {
        if (newContactName.isNotEmpty() && newContactPhone.isNotEmpty()) {
            try {
                // 1. RawContacts에 기본 정보 추가
                val rawContactValues = ContentValues().apply {
                    put(ContactsContract.RawContacts.ACCOUNT_TYPE, "com.google")
                    put(ContactsContract.RawContacts.ACCOUNT_NAME, "Google")
                }
                
                val rawContactUri = context.contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, rawContactValues)
                val rawContactId = rawContactUri?.lastPathSegment
                
                if (rawContactId != null) {
                    // 2. 이름 추가
                    val nameValues = ContentValues().apply {
                        put(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, rawContactId)
                        put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, newContactName)
                        put(ContactsContract.CommonDataKinds.StructuredName.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    }
                    context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, nameValues)
                    
                    // 3. 전화번호 추가
                    val phoneValues = ContentValues().apply {
                        put(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID, rawContactId)
                        put(ContactsContract.CommonDataKinds.Phone.NUMBER, newContactPhone)
                        put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        put(ContactsContract.CommonDataKinds.Phone.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    }
                    context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues)
                    
                    // 4. 이메일 추가 (있는 경우)
                    if (newContactEmail.isNotEmpty()) {
                        val emailValues = ContentValues().apply {
                            put(ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID, rawContactId)
                            put(ContactsContract.CommonDataKinds.Email.ADDRESS, newContactEmail)
                            put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME)
                            put(ContactsContract.CommonDataKinds.Email.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        }
                        context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, emailValues)
                    }
                    
                    // 5. 연락처 목록 새로고침
                    loadContacts(context.contentResolver)
                    
                    // 6. 입력 필드 초기화
                    newContactName = ""
                    newContactPhone = ""
                    newContactEmail = ""
                    showAddDialog = false
                }
            } catch (e: Exception) {
                // 오류 발생 시 사용자에게 알림
                android.widget.Toast.makeText(
                    context,
                    "연락처 추가 실패: ${e.message}",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val readPermission = permissions[Manifest.permission.READ_CONTACTS] ?: false
        val writePermission = permissions[Manifest.permission.WRITE_CONTACTS] ?: false
        hasPermission = readPermission && writePermission
        
        if (hasPermission) {
            loadContacts(context.contentResolver)
        }
    }

    fun checkPermissions() {
        val readPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        
        val writePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        
        hasPermission = readPermission && writePermission
        
        if (hasPermission) {
            loadContacts(context.contentResolver)
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
                )
            )
        }
    }

    DisposableEffect(context) {
        checkPermissions()
        onDispose { }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("연락처 앱", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "뒤로가기")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "연락처 추가")
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
        ) {
            // 권한 상태
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (hasPermission) Color(0xFFE8F5E8) else Color(0xFFFFEBEE)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (hasPermission) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = "권한 상태",
                        tint = if (hasPermission) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (hasPermission) "연락처 권한 허용됨" else "연락처 권한 필요",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasPermission) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                        if (!hasPermission) {
                            Text(
                                text = "연락처를 읽고 쓸 수 있도록 권한을 허용해주세요",
                                fontSize = 14.sp,
                                color = Color(0xFFF44336)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 연락처 목록
            if (contacts.isNotEmpty()) {
                Text(
                    text = "연락처 목록 (${contacts.size}개)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(contacts) { contact ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF5F5F5)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 프로필 이미지
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2196F3)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = contact.name.firstOrNull()?.uppercase() ?: "?",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                // 연락처 정보
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = contact.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (contact.phone.isNotEmpty()) {
                                        Text(
                                            text = contact.phone,
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    if (contact.email.isNotEmpty()) {
                                        Text(
                                            text = contact.email,
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // 연락처가 없을 때
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "연락처 없음",
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "연락처가 없습니다",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "새로운 연락처를 추가해보세요",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Content Provider 정보
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Content Provider 정보",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1976D2)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ContactsContract를 사용하여 기기의 연락처를 읽고, 새로운 연락처를 추가할 수 있습니다. ContentResolver를 통해 데이터베이스에 직접 접근합니다.",
                        fontSize = 14.sp,
                        color = Color(0xFF1976D2)
                    )
                }
            }
        }
    }

    // 연락처 추가 다이얼로그
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("새 연락처 추가") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newContactName,
                        onValueChange = { newContactName = it },
                        label = { Text("이름") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newContactPhone,
                        onValueChange = { newContactPhone = it },
                        label = { Text("전화번호") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newContactEmail,
                        onValueChange = { newContactEmail = it },
                        label = { Text("이메일 (선택사항)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { addNewContact() }) {
                    Text("추가")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}
