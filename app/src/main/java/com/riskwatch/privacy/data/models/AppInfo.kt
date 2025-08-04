package com.riskwatch.privacy.data.models

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val privacyScore: Int,
    val dangerousPermissions: List<PermissionInfo>,
    val trackers: List<TrackerInfo>,
    val installTime: Long,
    val updateTime: Long,
    val isSystemApp: Boolean = false
) : Parcelable {
    
    val riskLevel: RiskLevel
        get() = when (privacyScore) {
            in 0..39 -> RiskLevel.LOW
            in 40..74 -> RiskLevel.MEDIUM
            else -> RiskLevel.HIGH
        }
    
    val riskDescription: String
        get() = "${dangerousPermissions.size} Dangerous Permissions | ${trackers.size} Trackers Found"
}

enum class RiskLevel(val displayName: String, val colorRes: Int) {
    LOW("Low Risk", android.R.color.holo_green_dark),
    MEDIUM("Medium Risk", android.R.color.holo_orange_dark),
    HIGH("High Risk", android.R.color.holo_red_dark)
}