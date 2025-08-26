package com.example.android_applicatoins.data

import android.content.Context
import android.content.SharedPreferences

class MotivationDataStore(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "motivation_data",
        Context.MODE_PRIVATE
    )

               // 동기부여 콘텐츠
           var motivationImage: String
               get() = sharedPreferences.getString("motivation_image", "") ?: ""
               set(value) = sharedPreferences.edit().putString("motivation_image", value).apply()
               
           var motivationImageUri: String
               get() = sharedPreferences.getString("motivation_image_uri", "") ?: ""
               set(value) = sharedPreferences.edit().putString("motivation_image_uri", value).apply()

    var motivationVideo: String
        get() = sharedPreferences.getString("motivation_video", "") ?: ""
        set(value) = sharedPreferences.edit().putString("motivation_video", value).apply()

    var motivationText: String
        get() = sharedPreferences.getString("motivation_text", "") ?: ""
        set(value) = sharedPreferences.edit().putString("motivation_text", value).apply()

    // 할 일 관리
    var finalGoal: String
        get() = sharedPreferences.getString("final_goal", "") ?: ""
        set(value) = sharedPreferences.edit().putString("final_goal", value).apply()

    var middleGoal: String
        get() = sharedPreferences.getString("middle_goal", "") ?: ""
        set(value) = sharedPreferences.edit().putString("middle_goal", value).apply()

    var detailGoal: String
        get() = sharedPreferences.getString("detail_goal", "") ?: ""
        set(value) = sharedPreferences.edit().putString("detail_goal", value).apply()

    var monthlyGoal: String
        get() = sharedPreferences.getString("monthly_goal", "") ?: ""
        set(value) = sharedPreferences.edit().putString("monthly_goal", value).apply()

    var weeklyGoal: String
        get() = sharedPreferences.getString("weekly_goal", "") ?: ""
        set(value) = sharedPreferences.edit().putString("weekly_goal", value).apply()

    var todayGoal: String
        get() = sharedPreferences.getString("today_goal", "") ?: ""
        set(value) = sharedPreferences.edit().putString("today_goal", value).apply()

    // 설정
    var autoMotivationEnabled: Boolean
        get() = sharedPreferences.getBoolean("auto_motivation_enabled", true)
        set(value) = sharedPreferences.edit().putBoolean("auto_motivation_enabled", value).apply()

    var startTime: String
        get() = sharedPreferences.getString("start_time", "23:00") ?: "23:00"
        set(value) = sharedPreferences.edit().putString("start_time", value).apply()

    var endTime: String
        get() = sharedPreferences.getString("end_time", "07:00") ?: "07:00"
        set(value) = sharedPreferences.edit().putString("end_time", value).apply()

    // SNS 앱 차단 설정
    var snsBlockingEnabled: Boolean
        get() = sharedPreferences.getBoolean("sns_blocking_enabled", false)
        set(value) = sharedPreferences.edit().putBoolean("sns_blocking_enabled", value).apply()

    // 모든 데이터 초기화
    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }

               // 데이터 백업 (JSON 형태로 내보내기)
           fun exportData(): String {
               return """
               {
                   "motivation_image": "$motivationImage",
                   "motivation_image_uri": "$motivationImageUri",
                   "motivation_video": "$motivationVideo",
                   "motivation_text": "$motivationText",
                   "final_goal": "$finalGoal",
                   "middle_goal": "$middleGoal",
                   "detail_goal": "$detailGoal",
                   "monthly_goal": "$monthlyGoal",
                   "weekly_goal": "$weeklyGoal",
                   "today_goal": "$todayGoal",
                   "auto_motivation_enabled": $autoMotivationEnabled,
                   "start_time": "$startTime",
                   "end_time": "$endTime",
                   "sns_blocking_enabled": $snsBlockingEnabled
               }
               """.trimIndent()
           }
}
