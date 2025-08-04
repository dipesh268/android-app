package com.riskwatch.privacy.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackerInfo(
    val name: String,
    val category: TrackerCategory,
    val description: String,
    val company: String,
    val website: String
) : Parcelable

enum class TrackerCategory(val displayName: String, val colorRes: Int) {
    ADVERTISING("Advertising", android.R.color.holo_red_light),
    ANALYTICS("Analytics", android.R.color.holo_orange_light),
    SOCIAL("Social", android.R.color.holo_blue_light),
    CRASH_REPORTING("Crash Reporting", android.R.color.holo_green_light),
    PROFILING("Profiling", android.R.color.holo_purple),
    LOCATION("Location", android.R.color.darker_gray),
    OTHER("Other", android.R.color.darker_gray)
}

// Common trackers data based on Exodus Privacy database
object CommonTrackers {
    val GOOGLE_ADMOB = TrackerInfo(
        name = "Google AdMob",
        category = TrackerCategory.ADVERTISING,
        description = "Mobile advertising platform by Google",
        company = "Google",
        website = "https://admob.google.com/"
    )
    
    val FACEBOOK_ANALYTICS = TrackerInfo(
        name = "Facebook Analytics",
        category = TrackerCategory.ANALYTICS,
        description = "Analytics platform by Meta",
        company = "Meta",
        website = "https://analytics.facebook.com/"
    )
    
    val GOOGLE_ANALYTICS = TrackerInfo(
        name = "Google Analytics",
        category = TrackerCategory.ANALYTICS,
        description = "Web analytics service by Google",
        company = "Google",
        website = "https://analytics.google.com/"
    )
    
    val CRASHLYTICS = TrackerInfo(
        name = "Firebase Crashlytics",
        category = TrackerCategory.CRASH_REPORTING,
        description = "Crash reporting service by Google",
        company = "Google",
        website = "https://firebase.google.com/products/crashlytics"
    )
    
    val AMAZON_ADVERTISING = TrackerInfo(
        name = "Amazon Mobile Ads",
        category = TrackerCategory.ADVERTISING,
        description = "Mobile advertising platform by Amazon",
        company = "Amazon",
        website = "https://advertising.amazon.com/"
    )
    
    val UNITY_ADS = TrackerInfo(
        name = "Unity Ads",
        category = TrackerCategory.ADVERTISING,
        description = "Mobile advertising platform for games",
        company = "Unity Technologies",
        website = "https://unity.com/products/unity-ads"
    )
    
    val FLURRY = TrackerInfo(
        name = "Flurry",
        category = TrackerCategory.ANALYTICS,
        description = "Mobile analytics platform by Verizon Media",
        company = "Verizon Media",
        website = "https://www.flurry.com/"
    )
    
    fun getAllTrackers(): List<TrackerInfo> {
        return listOf(
            GOOGLE_ADMOB,
            FACEBOOK_ANALYTICS,
            GOOGLE_ANALYTICS,
            CRASHLYTICS,
            AMAZON_ADVERTISING,
            UNITY_ADS,
            FLURRY
        )
    }
    
    fun getByName(name: String): TrackerInfo? {
        return getAllTrackers().find { it.name.equals(name, ignoreCase = true) }
    }
}