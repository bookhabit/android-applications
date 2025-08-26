package com.example.android_applicatoins.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.android_applicatoins.R
import java.io.IOException

class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val binder = LocalBinder()
    private var isPlaying = false

    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializeMediaPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 포그라운드 서비스로 시작
        startForeground(NOTIFICATION_ID, createNotification())
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun initializeMediaPlayer() {
        try {
            mediaPlayer = MediaPlayer()
            
            // assets 폴더의 음악 파일을 재생하기 위한 AssetFileDescriptor 사용
            val assetFileDescriptor = assets.openFd("retro-lounge.mp3")
            mediaPlayer?.setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.declaredLength
            )
            
            // MediaPlayer 준비
            mediaPlayer?.prepareAsync()
            
            // 에러 리스너
            mediaPlayer?.setOnErrorListener { mp, what, extra ->
                android.util.Log.e("MusicService", "MediaPlayer error: what=$what, extra=$extra")
                true
            }
            
            // 준비 완료 리스너
            mediaPlayer?.setOnPreparedListener { mp ->
                android.util.Log.d("MusicService", "MediaPlayer 준비 완료")
            }
            
            // 재생 완료 리스너
            mediaPlayer?.setOnCompletionListener {
                android.util.Log.d("MusicService", "음악 재생 완료")
                isPlaying = false
                updateNotification()
            }
            
            assetFileDescriptor.close()
            
        } catch (e: Exception) {
            android.util.Log.e("MusicService", "MediaPlayer 초기화 실패", e)
        }
    }

    fun play() {
        try {
            if (mediaPlayer == null) {
                initializeMediaPlayer()
                return
            }
            
            if (!isPlaying && mediaPlayer?.isPlaying == false) {
                mediaPlayer?.start()
                isPlaying = true
                android.util.Log.d("MusicService", "음악 재생 시작")
                updateNotification()
            }
        } catch (e: Exception) {
            android.util.Log.e("MusicService", "음악 재생 실패", e)
        }
    }

    fun pause() {
        try {
            if (isPlaying && mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                isPlaying = false
                android.util.Log.d("MusicService", "음악 일시정지")
                updateNotification()
            }
        } catch (e: Exception) {
            android.util.Log.e("MusicService", "음악 일시정지 실패", e)
        }
    }

    fun stop() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
                mediaPlayer?.prepareAsync() // 재생을 위해 다시 준비
            }
            isPlaying = false
            android.util.Log.d("MusicService", "음악 정지")
            updateNotification()
        } catch (e: Exception) {
            android.util.Log.e("MusicService", "음악 정지 실패", e)
        }
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    fun seekTo(position: Int) {
        try {
            mediaPlayer?.seekTo(position)
        } catch (e: Exception) {
            android.util.Log.e("MusicService", "seekTo 실패", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Music Player"
            val descriptionText = "음악 재생 알림"
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
        val intent = Intent(this, com.example.android_applicatoins.MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("음악 플레이어")
            .setContentText(if (isPlaying) "재생 중..." else "일시정지됨")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    companion object {
        private const val CHANNEL_ID = "music_player"
        private const val NOTIFICATION_ID = 1
    }
}
