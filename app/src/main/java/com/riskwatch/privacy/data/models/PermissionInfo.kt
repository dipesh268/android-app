package com.riskwatch.privacy.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PermissionInfo(
    val name: String,
    val technicalName: String,
    val description: String,
    val category: PermissionCategory,
    val isDangerous: Boolean,
    val iconRes: Int
) : Parcelable

enum class PermissionCategory(val displayName: String) {
    CAMERA("Camera"),
    MICROPHONE("Microphone"),
    LOCATION("Location"),
    STORAGE("Storage"),
    CONTACTS("Contacts"),
    PHONE("Phone"),
    SMS("SMS"),
    CALENDAR("Calendar"),
    SENSORS("Sensors"),
    NETWORK("Network"),
    SYSTEM("System"),
    OTHER("Other")
}

// Common dangerous permissions data
object DangerousPermissions {
    val CAMERA = PermissionInfo(
        name = "Camera",
        technicalName = "android.permission.CAMERA",
        description = "Allows this app to take photos and record videos at any time.",
        category = PermissionCategory.CAMERA,
        isDangerous = true,
        iconRes = android.R.drawable.ic_menu_camera
    )
    
    val RECORD_AUDIO = PermissionInfo(
        name = "Microphone",
        technicalName = "android.permission.RECORD_AUDIO",
        description = "Allows this app to record audio using the microphone at any time.",
        category = PermissionCategory.MICROPHONE,
        isDangerous = true,
        iconRes = android.R.drawable.ic_btn_speak_now
    )
    
    val ACCESS_FINE_LOCATION = PermissionInfo(
        name = "Precise Location",
        technicalName = "android.permission.ACCESS_FINE_LOCATION",
        description = "Allows this app to get your exact location using GPS or network location sources.",
        category = PermissionCategory.LOCATION,
        isDangerous = true,
        iconRes = android.R.drawable.ic_dialog_map
    )
    
    val ACCESS_COARSE_LOCATION = PermissionInfo(
        name = "Approximate Location",
        technicalName = "android.permission.ACCESS_COARSE_LOCATION",
        description = "Allows this app to get your approximate location using network-based location sources.",
        category = PermissionCategory.LOCATION,
        isDangerous = true,
        iconRes = android.R.drawable.ic_dialog_map
    )
    
    val READ_CONTACTS = PermissionInfo(
        name = "Read Contacts",
        technicalName = "android.permission.READ_CONTACTS",
        description = "Allows this app to read data about your contacts stored on your device.",
        category = PermissionCategory.CONTACTS,
        isDangerous = true,
        iconRes = android.R.drawable.ic_menu_my_calendar
    )
    
    val READ_PHONE_STATE = PermissionInfo(
        name = "Phone",
        technicalName = "android.permission.READ_PHONE_STATE",
        description = "Allows this app to access phone features including phone number and device IDs.",
        category = PermissionCategory.PHONE,
        isDangerous = true,
        iconRes = android.R.drawable.ic_menu_call
    )
    
    val READ_SMS = PermissionInfo(
        name = "Read SMS",
        technicalName = "android.permission.READ_SMS",
        description = "Allows this app to read SMS messages stored on your device.",
        category = PermissionCategory.SMS,
        isDangerous = true,
        iconRes = android.R.drawable.ic_dialog_email
    )
    
    val READ_EXTERNAL_STORAGE = PermissionInfo(
        name = "Read Storage",
        technicalName = "android.permission.READ_EXTERNAL_STORAGE",
        description = "Allows this app to read files from your device's storage.",
        category = PermissionCategory.STORAGE,
        isDangerous = true,
        iconRes = android.R.drawable.ic_menu_save
    )
    
    fun getAllDangerousPermissions(): List<PermissionInfo> {
        return listOf(
            CAMERA,
            RECORD_AUDIO,
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            READ_CONTACTS,
            READ_PHONE_STATE,
            READ_SMS,
            READ_EXTERNAL_STORAGE
        )
    }
    
    fun getByTechnicalName(technicalName: String): PermissionInfo? {
        return getAllDangerousPermissions().find { it.technicalName == technicalName }
    }
}