package com.example.android_applicatoins.utils

import android.net.Uri

object SharedDataManager {
    var sharedText: String? = null
    var sharedImageUri: Uri? = null
    var sharedFileUri: Uri? = null
    var sharedDataType: String? = null
    
    // 저장 상태 관리
    var isSaving: Boolean = false
    var saveMessage: String = ""
    
    fun clearSharedData() {
        sharedText = null
        sharedImageUri = null
        sharedFileUri = null
        sharedDataType = null
        isSaving = false
        saveMessage = ""
    }
    
    fun hasSharedData(): Boolean {
        return !sharedText.isNullOrEmpty() || sharedImageUri != null || sharedFileUri != null
    }
}
