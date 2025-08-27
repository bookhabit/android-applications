package com.example.android_applicatoins.services

import android.app.*
import android.content.Intent
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.android_applicatoins.MainActivity
import com.example.android_applicatoins.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ScreenRecordingService : Service() {
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "screen_recording_channel"
        private const val CHANNEL_NAME = "화면 녹화"
        
        // 액션 상수들
        const val ACTION_START_RECORDING = "START_RECORDING"
        const val ACTION_STOP_RECORDING = "STOP_RECORDING"
        const val EXTRA_RESULT_CODE = "RESULT_CODE"
        const val EXTRA_RESULT_DATA = "RESULT_DATA"
        const val EXTRA_VIDEO_QUALITY = "VIDEO_QUALITY"
        const val EXTRA_FRAME_RATE = "FRAME_RATE"
        const val EXTRA_BIT_RATE = "BIT_RATE"
    }
    
    private val binder = ScreenRecordingBinder()
    private var mediaProjection: MediaProjection? = null
    private var mediaRecorder: MediaRecorder? = null
    private var virtualDisplay: android.hardware.display.VirtualDisplay? = null
    private var outputFile: File? = null
    private var isRecording = false
    
    // 녹화 설정
    private var videoQuality = "HD (720p)"
    private var frameRate = 30
    private var bitRate = 5000000
    
    inner class ScreenRecordingBinder : Binder() {
        fun getService(): ScreenRecordingService = this@ScreenRecordingService
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_RECORDING -> {
                val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED)
                val resultData = intent.getParcelableExtra<Intent>(EXTRA_RESULT_DATA)
                videoQuality = intent.getStringExtra(EXTRA_VIDEO_QUALITY) ?: "HD (720p)"
                frameRate = intent.getIntExtra(EXTRA_FRAME_RATE, 30)
                bitRate = intent.getIntExtra(EXTRA_BIT_RATE, 5000000)
                
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    startForeground(NOTIFICATION_ID, createNotification())
                    startRecording(resultCode, resultData)
                }
            }
            ACTION_STOP_RECORDING -> {
                stopRecording()
                stopForeground(true)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }
    
    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "화면 녹화 중입니다"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val stopIntent = Intent(this, ScreenRecordingService::class.java).apply {
            action = ACTION_STOP_RECORDING
        }
        
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("📱 화면 녹화 중")
            .setContentText("녹화를 중지하려면 탭하세요")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "녹화 중지",
                stopPendingIntent
            )
            .setOngoing(true)
            .setSilent(true)
            .build()
    }
    
    private fun startRecording(resultCode: Int, resultData: Intent) {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            outputFile = File(getExternalFilesDir(null), "screen_record_$timestamp.mp4")
            
            // MediaRecorder 설정
            mediaRecorder = MediaRecorder().apply {
                setVideoSource(MediaRecorder.VideoSource.SURFACE)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                setVideoEncodingBitRate(bitRate)
                setVideoFrameRate(frameRate)
                
                // 화질 설정에 따른 해상도 설정
                when (videoQuality) {
                    "HD (720p)" -> setVideoSize(1280, 720)
                    "Full HD (1080p)" -> setVideoSize(1920, 1080)
                    "4K (2160p)" -> setVideoSize(3840, 2160)
                    else -> setVideoSize(1280, 720)
                }
                
                setOutputFile(outputFile?.absolutePath)
                prepare()
            }
            
            // MediaProjection 생성
            val mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, resultData)
            
            // VirtualDisplay 생성
            virtualDisplay = mediaProjection?.createVirtualDisplay(
                "ScreenRecording",
                when (videoQuality) {
                    "HD (720p)" -> 1280
                    "Full HD (1080p)" -> 1920
                    "4K (2160p)" -> 3840
                    else -> 1280
                },
                when (videoQuality) {
                    "HD (720p)" -> 720
                    "Full HD (1080p)" -> 1080
                    "4K (2160p)" -> 2160
                    else -> 720
                },
                1,
                android.hardware.display.DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder?.surface, null, null
            )
            
            // 녹화 시작
            mediaRecorder?.start()
            isRecording = true
            
            Log.d("ScreenRecordingService", "화면 녹화 시작됨")
            
        } catch (e: Exception) {
            Log.e("ScreenRecordingService", "녹화 시작 실패", e)
            stopSelf()
        }
    }
    
    private fun stopRecording() {
        try {
            isRecording = false
            
            // MediaRecorder 정지 및 해제
            mediaRecorder?.let { recorder ->
                try {
                    recorder.stop()
                    recorder.release()
                } catch (e: Exception) {
                    Log.e("ScreenRecordingService", "MediaRecorder 정지 실패", e)
                }
            }
            
            // VirtualDisplay 해제
            virtualDisplay?.let { display ->
                try {
                    display.release()
                } catch (e: Exception) {
                    Log.e("ScreenRecordingService", "VirtualDisplay 해제 실패", e)
                }
            }
            
            // MediaProjection 해제
            mediaProjection?.let { projection ->
                try {
                    projection.stop()
                } catch (e: Exception) {
                    Log.e("ScreenRecordingService", "MediaProjection 정지 실패", e)
                }
            }
            
            // 참조 초기화
            mediaRecorder = null
            virtualDisplay = null
            mediaProjection = null
            
            Log.d("ScreenRecordingService", "화면 녹화 중지됨")
            
        } catch (e: Exception) {
            Log.e("ScreenRecordingService", "녹화 중지 실패", e)
        }
    }
    
    fun isRecording(): Boolean = isRecording
    
    fun getOutputFile(): File? = outputFile
    
    override fun onDestroy() {
        stopRecording()
        super.onDestroy()
    }
}
