package com.riskwatch.privacy.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppInfo(
    val name: String,
    val packageName: String,
    val version: String,
    val installDate: String,
    val privacyScore: Int,
    val permissions: List<String> = emptyList(),
    val trackers: List<String> = emptyList(),
    val icon: String? = null
) : Parcelable