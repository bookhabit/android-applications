package com.example.android_applicatoins

import android.os.Bundle
import android.util.Log
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.android_applicatoins.navigation.AppNavigation
import com.example.android_applicatoins.ui.theme.AndroidapplicatoinsTheme

class MainActivity : ComponentActivity() {
    
    private val TAG = "MainActivity_Lifecycle"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() 호출됨")
        enableEdgeToEdge()
        setContent {
            AndroidapplicatoinsTheme {
                AppNavigation()
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