package com.example.android_applicatoins

import android.os.Bundle
import android.util.Log
import android.content.pm.PackageManager
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.example.android_applicatoins.navigation.AppNavigation
import com.example.android_applicatoins.ui.theme.AndroidapplicatoinsTheme
import com.example.android_applicatoins.utils.SharedDataManager

class MainActivity : ComponentActivity() {
    
    private val TAG = "MainActivity_Lifecycle"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() 호출됨")
        enableEdgeToEdge()
        
        // 공유 인텐트 처리
        handleShareIntent(intent)
        
        setContent {
            AndroidapplicatoinsTheme {
                AppNavigation()
            }
        }
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent() 호출됨")
        // 공유 인텐트 처리
        handleShareIntent(intent)
    }
    
    private fun handleShareIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND) {
            Log.d(TAG, "공유 인텐트 감지됨: ${intent.type}")
            
            // 공유된 데이터를 전역 변수나 SharedPreferences에 저장
            when (intent.type) {
                "text/plain" -> {
                    val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                    if (!sharedText.isNullOrEmpty()) {
                        Log.d(TAG, "공유된 텍스트: $sharedText")
                        // 텍스트를 전역 변수에 저장
                        SharedDataManager.sharedText = sharedText
                        SharedDataManager.sharedDataType = "text"
                    }
                }
                "image/*" -> {
                    val imageUri = intent.getParcelableExtra<android.net.Uri>(Intent.EXTRA_STREAM)
                    if (imageUri != null) {
                        Log.d(TAG, "공유된 이미지 URI: $imageUri")
                        // 이미지 URI를 전역 변수에 저장
                        SharedDataManager.sharedImageUri = imageUri
                        SharedDataManager.sharedDataType = "image"
                    }
                }
                else -> {
                    val streamUri = intent.getParcelableExtra<android.net.Uri>(Intent.EXTRA_STREAM)
                    if (streamUri != null) {
                        Log.d(TAG, "공유된 파일 URI: $streamUri")
                        // 파일 URI를 전역 변수에 저장
                        SharedDataManager.sharedFileUri = streamUri
                        SharedDataManager.sharedDataType = "file"
                    }
                }
            }
        }
    }
    
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() 호출됨")
    }
    
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() 호출됨")
    }
    
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() 호출됨")
    }
    
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() 호출됨")
    }
    
    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart() 호출됨")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() 호출됨")
    }
    
    // 권한 요청 결과 처리 (레거시 방식 - Compose에서는 권장하지 않음)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        Log.d(TAG, "onRequestPermissionsResult() 호출됨 - requestCode: $requestCode")
        
        for (i in permissions.indices) {
            val permission = permissions[i]
            val result = grantResults[i]
            val isGranted = result == PackageManager.PERMISSION_GRANTED
            
            Log.d(TAG, "권한: $permission, 결과: ${if (isGranted) "허용" else "거부"}")
        }
    }
}