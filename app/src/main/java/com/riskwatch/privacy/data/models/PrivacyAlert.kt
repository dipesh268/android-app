package com.riskwatch.privacy.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PrivacyAlert(
    val id: String,
    val type: AlertType,
    val appInfo: AppInfo,
    val timestamp: Long,
    val title: String,
    val description: String,
    val isRead: Boolean = false
) : Parcelable

enum class AlertType(val displayName: String, val iconRes: Int) {
    HIGH_RISK_APP_DETECTED("High-Risk App Detected", android.R.drawable.ic_dialog_alert),
    NEW_APP_INSTALLED("New App Installed", android.R.drawable.ic_input_add),
    PERMISSION_ADDED("New Permission Granted", android.R.drawable.ic_partial_secure),
    TRACKER_DETECTED("Tracker Detected", android.R.drawable.ic_menu_search),
    APP_UPDATED("App Updated", android.R.drawable.ic_popup_sync)
}