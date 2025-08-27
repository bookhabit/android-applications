package com.example.android_applicatoins

import android.os.Bundle
import android.util.Log
import android.content.pm.PackageManager
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
                val navController = rememberNavController()
                
                // 공유 인텐트가 있으면 ShareScreen으로 이동
                LaunchedEffect(Unit) {
                    if (intent?.action == Intent.ACTION_SEND) {
                        navController.navigate("share")
                    }
                }
                
                AppNavigation(navController = navController)
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
            val mimeType = intent.type
            Log.d(TAG, "=== 공유 인텐트 상세 분석 ===")
            Log.d(TAG, "MIME 타입: $mimeType")
            Log.d(TAG, "액션: ${intent.action}")
            Log.d(TAG, "카테고리: ${intent.categories}")
            
            // MIME 타입별 상세 분석
            when {
                mimeType == "text/plain" -> {
                    val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                    if (!sharedText.isNullOrEmpty()) {
                        Log.d(TAG, "✅ 텍스트 분류됨")
                        Log.d(TAG, "공유된 텍스트: $sharedText")
                        SharedDataManager.sharedText = sharedText
                        SharedDataManager.sharedDataType = "text"
                        Log.d(TAG, "SharedDataManager 업데이트 완료: text")
                    } else {
                        Log.w(TAG, "⚠️ 텍스트가 비어있음")
                    }
                }
                mimeType?.startsWith("image/") == true -> {
                    Log.d(TAG, "✅ 이미지 분류됨 (MIME: $mimeType)")
                    val imageUri = intent.getParcelableExtra<android.net.Uri>(Intent.EXTRA_STREAM)
                    if (imageUri != null) {
                        Log.d(TAG, "공유된 이미지 URI: $imageUri")
                        Log.d(TAG, "URI 스키마: ${imageUri.scheme}")
                        Log.d(TAG, "URI 호스트: ${imageUri.host}")
                        Log.d(TAG, "URI 경로: ${imageUri.path}")
                        
                        SharedDataManager.sharedImageUri = imageUri
                        SharedDataManager.sharedDataType = "image"
                        Log.d(TAG, "SharedDataManager 업데이트 완료: image")
                    } else {
                        Log.e(TAG, "❌ 이미지 URI가 null")
                    }
                }
                mimeType?.startsWith("video/") == true -> {
                    Log.d(TAG, "✅ 비디오 분류됨 (MIME: $mimeType)")
                    val videoUri = intent.getParcelableExtra<android.net.Uri>(Intent.EXTRA_STREAM)
                    if (videoUri != null) {
                        Log.d(TAG, "공유된 비디오 URI: $videoUri")
                        SharedDataManager.sharedFileUri = videoUri
                        SharedDataManager.sharedDataType = "file"
                        Log.d(TAG, "SharedDataManager 업데이트 완료: video -> file")
                    }
                }
                mimeType?.startsWith("audio/") == true -> {
                    Log.d(TAG, "✅ 오디오 분류됨 (MIME: $mimeType)")
                    val audioUri = intent.getParcelableExtra<android.net.Uri>(Intent.EXTRA_STREAM)
                    if (audioUri != null) {
                        Log.d(TAG, "공유된 오디오 URI: $audioUri")
                        SharedDataManager.sharedFileUri = audioUri
                        SharedDataManager.sharedDataType = "file"
                        Log.d(TAG, "SharedDataManager 업데이트 완료: audio -> file")
                    }
                }
                mimeType?.startsWith("application/") == true -> {
                    Log.d(TAG, "✅ 애플리케이션 파일 분류됨 (MIME: $mimeType)")
                    val fileUri = intent.getParcelableExtra<android.net.Uri>(Intent.EXTRA_STREAM)
                    if (fileUri != null) {
                        Log.d(TAG, "공유된 파일 URI: $fileUri")
                        SharedDataManager.sharedFileUri = fileUri
                        SharedDataManager.sharedDataType = "file"
                        Log.d(TAG, "SharedDataManager 업데이트 완료: application -> file")
                    }
                }
                else -> {
                    Log.d(TAG, "⚠️ 기타 타입 분류됨 (MIME: $mimeType)")
                    val streamUri = intent.getParcelableExtra<android.net.Uri>(Intent.EXTRA_STREAM)
                    if (streamUri != null) {
                        Log.d(TAG, "공유된 스트림 URI: $streamUri")
                        Log.d(TAG, "URI 스키마: ${streamUri.scheme}")
                        Log.d(TAG, "URI 호스트: ${streamUri.host}")
                        Log.d(TAG, "URI 경로: ${streamUri.path}")
                        
                        // MIME 타입이 null이거나 알 수 없는 경우 파일로 분류
                        SharedDataManager.sharedFileUri = streamUri
                        SharedDataManager.sharedDataType = "file"
                        Log.d(TAG, "SharedDataManager 업데이트 완료: unknown -> file")
                    } else {
                        Log.e(TAG, "❌ 스트림 URI가 null")
                    }
                }
            }
            
            // 최종 상태 로깅
            Log.d(TAG, "=== 최종 SharedDataManager 상태 ===")
            Log.d(TAG, "sharedDataType: ${SharedDataManager.sharedDataType}")
            Log.d(TAG, "sharedText: ${SharedDataManager.sharedText}")
            Log.d(TAG, "sharedImageUri: ${SharedDataManager.sharedImageUri}")
            Log.d(TAG, "sharedFileUri: ${SharedDataManager.sharedFileUri}")
            Log.d(TAG, "hasSharedData: ${SharedDataManager.hasSharedData()}")
            Log.d(TAG, "=== 공유 인텐트 분석 완료 ===")
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