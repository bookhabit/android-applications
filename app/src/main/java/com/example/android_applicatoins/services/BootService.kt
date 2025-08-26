package com.example.android_applicatoins.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.android_applicatoins.MainActivity
import com.example.android_applicatoins.R

class BootService : Service() {
    
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 포그라운드 서비스로 시작
        startForeground(NOTIFICATION_ID, createNotification())
        
        // 부팅 완료 후 실행되는 작업들
        performBootTasks()
        
        return START_STICKY // 서비스가 종료되어도 다시 시작
    }

    override fun onDestroy() {
        super.onDestroy()
        android.util.Log.d("BootService", "부팅 서비스 종료")
    }

    private fun performBootTasks() {
        android.util.Log.d("BootService", "부팅 완료 후 작업 시작")
        
        // 예시 작업들:
        // 1. 앱 설정 초기화
        // 2. 백그라운드 동기화 시작
        // 3. 알림 설정
        // 4. 데이터베이스 정리 등
        
        // 실제로는 여기에 필요한 작업들을 구현
        android.util.Log.d("BootService", "부팅 작업 완료")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Boot Service"
            val descriptionText = "부팅 후 자동 실행 서비스"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("부팅 서비스")
            .setContentText("부팅 완료 후 자동으로 실행되었습니다")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "boot_service"
        private const val NOTIFICATION_ID = 2
    }
}
