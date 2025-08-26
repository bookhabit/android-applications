package com.example.android_applicatoins.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.android_applicatoins.data.MotivationDataStore

class AppBlockingService : AccessibilityService() {
    
    companion object {
        private const val TAG = "AppBlockingService"
        private var isServiceEnabled = false
        private var isBlockingEnabled = false
        
        fun setBlockingEnabled(enabled: Boolean) {
            isBlockingEnabled = enabled
        }
        
        fun isServiceRunning(): Boolean {
            return isServiceEnabled
        }
    }
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "AppBlockingService 연결됨")
        isServiceEnabled = true
        
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or 
                        AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            notificationTimeout = 100
        }
        serviceInfo = info
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!isBlockingEnabled) return
        
        event?.let {
            when (it.eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    val packageName = it.packageName?.toString() ?: return
                    if (isBlockedApp(packageName)) {
                        Log.d(TAG, "차단된 앱 실행 시도: $packageName")
                        blockApp(packageName)
                    }
                }
            }
        }
    }
    
    private fun isBlockedApp(packageName: String): Boolean {
        val blockedApps = listOf(
            "com.google.android.youtube",           // YouTube
            "com.android.chrome",                   // Chrome
            "com.sec.android.app.browser",          // Samsung Internet
            "com.facebook.katana",                  // Facebook
            "com.instagram.android",                 // Instagram
            "com.twitter.android",                  // Twitter
            "com.snapchat.android",                 // Snapchat
            "com.whatsapp",                         // WhatsApp
            "com.tencent.mm",                       // WeChat
            "com.kakao.talk",                       // KakaoTalk
            "com.naver.line.android",               // LINE
            "com.discord",                          // Discord
            "com.reddit.frontpage",                 // Reddit
            "com.pinterest",                        // Pinterest
            "com.linkedin.android",                 // LinkedIn
            "com.zhiliaoapp.musically",             // TikTok
            "com.spotify.music",                    // Spotify
            "com.netease.cloudmusic",               // NetEase Music
            "com.tencent.qqmusic",                  // QQ Music
            "com.tencent.qq",                       // QQ
            "com.tencent.tim",                      // TIM
            "com.tencent.mobileqq",                 // Mobile QQ
            "com.tencent.mm",                       // WeChat
            "com.tencent.qqgame",                   // QQ Game
            "com.tencent.qqgamecenter",             // QQ Game Center
            "com.tencent.qqgameassistant",          // QQ Game Assistant
            "com.tencent.qqgameassistant2",         // QQ Game Assistant 2
            "com.tencent.qqgameassistant3",         // QQ Game Assistant 3
            "com.tencent.qqgameassistant4",         // QQ Game Assistant 4
            "com.tencent.qqgameassistant5",         // QQ Game Assistant 5
            "com.tencent.qqgameassistant6",         // QQ Game Assistant 6
            "com.tencent.qqgameassistant7",         // QQ Game Assistant 7
            "com.tencent.qqgameassistant8",         // QQ Game Assistant 8
            "com.tencent.qqgameassistant9",         // QQ Game Assistant 9
            "com.tencent.qqgameassistant10",        // QQ Game Assistant 10
            "com.tencent.qqgameassistant11",        // QQ Game Assistant 11
            "com.tencent.qqgameassistant12",        // QQ Game Assistant 12
            "com.tencent.qqgameassistant13",        // QQ Game Assistant 13
            "com.tencent.qqgameassistant14",        // QQ Game Assistant 14
            "com.tencent.qqgameassistant15",        // QQ Game Assistant 15
            "com.tencent.qqgameassistant16",        // QQ Game Assistant 16
            "com.tencent.qqgameassistant17",        // QQ Game Assistant 17
            "com.tencent.qqgameassistant18",        // QQ Game Assistant 18
            "com.tencent.qqgameassistant19",        // QQ Game Assistant 19
            "com.tencent.qqgameassistant20",        // QQ Game Assistant 20
            "com.tencent.qqgameassistant21",        // QQ Game Assistant 21
            "com.tencent.qqgameassistant22",        // QQ Game Assistant 22
            "com.tencent.qqgameassistant23",        // QQ Game Assistant 23
            "com.tencent.qqgameassistant24",        // QQ Game Assistant 24
            "com.tencent.qqgameassistant25",        // QQ Game Assistant 25
            "com.tencent.qqgameassistant26",        // QQ Game Assistant 26
            "com.tencent.qqgameassistant27",        // QQ Game Assistant 27
            "com.tencent.qqgameassistant28",        // QQ Game Assistant 28
            "com.tencent.qqgameassistant29",        // QQ Game Assistant 29
            "com.tencent.qqgameassistant30",        // QQ Game Assistant 30
            "com.tencent.qqgameassistant31",        // QQ Game Assistant 31
            "com.tencent.qqgameassistant32",        // QQ Game Assistant 32
            "com.tencent.qqgameassistant33",        // QQ Game Assistant 33
            "com.tencent.qqgameassistant34",        // QQ Game Assistant 34
            "com.tencent.qqgameassistant35",        // QQ Game Assistant 35
            "com.tencent.qqgameassistant36",        // QQ Game Assistant 36
            "com.tencent.qqgameassistant37",        // QQ Game Assistant 37
            "com.tencent.qqgameassistant38",        // QQ Game Assistant 38
            "com.tencent.qqgameassistant39",        // QQ Game Assistant 39
            "com.tencent.qqgameassistant40",        // QQ Game Assistant 40
            "com.tencent.qqgameassistant41",        // QQ Game Assistant 41
            "com.tencent.qqgameassistant42",        // QQ Game Assistant 42
            "com.tencent.qqgameassistant43",        // QQ Game Assistant 43
            "com.tencent.qqgameassistant44",        // QQ Game Assistant 44
            "com.tencent.qqgameassistant45",        // QQ Game Assistant 45
            "com.tencent.qqgameassistant46",        // QQ Game Assistant 46
            "com.tencent.qqgameassistant47",        // QQ Game Assistant 47
            "com.tencent.qqgameassistant48",        // QQ Game Assistant 48
            "com.tencent.qqgameassistant49",        // QQ Game Assistant 49
            "com.tencent.qqgameassistant50",        // QQ Game Assistant 50
            "com.tencent.qqgameassistant51",        // QQ Game Assistant 51
            "com.tencent.qqgameassistant52",        // QQ Game Assistant 52
            "com.tencent.qqgameassistant53",        // QQ Game Assistant 53
            "com.tencent.qqgameassistant54",        // QQ Game Assistant 54
            "com.tencent.qqgameassistant55",        // QQ Game Assistant 55
            "com.tencent.qqgameassistant56",        // QQ Game Assistant 56
            "com.tencent.qqgameassistant57",        // QQ Game Assistant 57
            "com.tencent.qqgameassistant58",        // QQ Game Assistant 58
            "com.tencent.qqgameassistant59",        // QQ Game Assistant 59
            "com.tencent.qqgameassistant60",        // QQ Game Assistant 60
            "com.tencent.qqgameassistant61",        // QQ Game Assistant 61
            "com.tencent.qqgameassistant62",        // QQ Game Assistant 62
            "com.tencent.qqgameassistant63",        // QQ Game Assistant 63
            "com.tencent.qqgameassistant64",        // QQ Game Assistant 64
            "com.tencent.qqgameassistant65",        // QQ Game Assistant 65
            "com.tencent.qqgameassistant66",        // QQ Game Assistant 66
            "com.tencent.qqgameassistant67",        // QQ Game Assistant 67
            "com.tencent.qqgameassistant68",        // QQ Game Assistant 68
            "com.tencent.qqgameassistant69",        // QQ Game Assistant 69
            "com.tencent.qqgameassistant70",        // QQ Game Assistant 70
            "com.tencent.qqgameassistant71",        // QQ Game Assistant 71
            "com.tencent.qqgameassistant72",        // QQ Game Assistant 72
            "com.tencent.qqgameassistant73",        // QQ Game Assistant 73
            "com.tencent.qqgameassistant74",        // QQ Game Assistant 74
            "com.tencent.qqgameassistant75",        // QQ Game Assistant 75
            "com.tencent.qqgameassistant76",        // QQ Game Assistant 76
            "com.tencent.qqgameassistant77",        // QQ Game Assistant 77
            "com.tencent.qqgameassistant78",        // QQ Game Assistant 78
            "com.tencent.qqgameassistant79",        // QQ Game Assistant 79
            "com.tencent.qqgameassistant80",        // QQ Game Assistant 80
            "com.tencent.qqgameassistant81",        // QQ Game Assistant 81
            "com.tencent.qqgameassistant82",        // QQ Game Assistant 82
            "com.tencent.qqgameassistant83",        // QQ Game Assistant 83
            "com.tencent.qqgameassistant84",        // QQ Game Assistant 84
            "com.tencent.qqgameassistant85",        // QQ Game Assistant 85
            "com.tencent.qqgameassistant86",        // QQ Game Assistant 86
            "com.tencent.qqgameassistant87",        // QQ Game Assistant 87
            "com.tencent.qqgameassistant88",        // QQ Game Assistant 88
            "com.tencent.qqgameassistant89",        // QQ Game Assistant 89
            "com.tencent.qqgameassistant90",        // QQ Game Assistant 90
            "com.tencent.qqgameassistant91",        // QQ Game Assistant 91
            "com.tencent.qqgameassistant92",        // QQ Game Assistant 92
            "com.tencent.qqgameassistant93",        // QQ Game Assistant 93
            "com.tencent.qqgameassistant94",        // QQ Game Assistant 94
            "com.tencent.qqgameassistant95",        // QQ Game Assistant 95
            "com.tencent.qqgameassistant96",        // QQ Game Assistant 96
            "com.tencent.qqgameassistant97",        // QQ Game Assistant 97
            "com.tencent.qqgameassistant98",        // QQ Game Assistant 98
            "com.tencent.qqgameassistant99",        // QQ Game Assistant 99
            "com.tencent.qqgameassistant100"        // QQ Game Assistant 100
        )
        
        return blockedApps.contains(packageName)
    }
    
    private fun blockApp(packageName: String) {
        try {
            // 홈 화면으로 돌아가기
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(homeIntent)
            
            Log.d(TAG, "앱 차단 완료: $packageName")
        } catch (e: Exception) {
            Log.e(TAG, "앱 차단 실패: ${e.message}")
        }
    }
    
    override fun onInterrupt() {
        Log.d(TAG, "AppBlockingService 인터럽트됨")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "AppBlockingService 종료됨")
        isServiceEnabled = false
    }
}
