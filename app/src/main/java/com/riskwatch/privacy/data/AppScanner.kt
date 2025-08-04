package com.riskwatch.privacy.data

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.riskwatch.privacy.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

class AppScanner(private val context: Context) {
    
    private val packageManager = context.packageManager
    
    suspend fun scanAllApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val apps = mutableListOf<AppInfo>()
        
        try {
            val installedPackages = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
            
            for (packageInfo in installedPackages) {
                // Skip system apps that shouldn't be analyzed
                if (shouldSkipApp(packageInfo)) continue
                
                val appInfo = createAppInfo(packageInfo)
                apps.add(appInfo)
            }
        } catch (e: Exception) {
            // Handle scanning errors gracefully
        }
        
        apps.sortedByDescending { it.privacyScore }
    }
    
    private fun shouldSkipApp(packageInfo: PackageInfo): Boolean {
        val applicationInfo = packageInfo.applicationInfo
        
        // Skip our own app
        if (packageInfo.packageName == context.packageName) return true
        
        // Skip some core system apps that users shouldn't worry about
        val systemPackages = listOf(
            "android",
            "com.android.systemui",
            "com.android.settings",
            "com.android.phone"
        )
        
        return systemPackages.contains(packageInfo.packageName)
    }
    
    private fun createAppInfo(packageInfo: PackageInfo): AppInfo {
        val applicationInfo = packageInfo.applicationInfo
        val appName = packageManager.getApplicationLabel(applicationInfo).toString()
        
        // Analyze permissions
        val dangerousPermissions = analyzeDangerousPermissions(packageInfo.requestedPermissions)
        
        // Simulate tracker detection (in a real app, this would use Exodus Privacy API)
        val trackers = simulateTrackerDetection(packageInfo.packageName)
        
        // Calculate privacy score
        val privacyScore = calculatePrivacyScore(dangerousPermissions, trackers)
        
        return AppInfo(
            packageName = packageInfo.packageName,
            appName = appName,
            versionName = packageInfo.versionName ?: "Unknown",
            versionCode = packageInfo.longVersionCode,
            privacyScore = privacyScore,
            dangerousPermissions = dangerousPermissions,
            trackers = trackers,
            installTime = packageInfo.firstInstallTime,
            updateTime = packageInfo.lastUpdateTime,
            isSystemApp = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        )
    }
    
    private fun analyzeDangerousPermissions(requestedPermissions: Array<String>?): List<PermissionInfo> {
        if (requestedPermissions == null) return emptyList()
        
        val dangerousPermissions = mutableListOf<PermissionInfo>()
        val allDangerousPermissions = DangerousPermissions.getAllDangerousPermissions()
        
        for (permission in requestedPermissions) {
            val dangerousPermission = allDangerousPermissions.find { it.technicalName == permission }
            if (dangerousPermission != null) {
                dangerousPermissions.add(dangerousPermission)
            }
        }
        
        return dangerousPermissions
    }
    
    private fun simulateTrackerDetection(packageName: String): List<TrackerInfo> {
        // In a real implementation, this would query the Exodus Privacy API
        // For now, we'll simulate tracker detection based on app characteristics
        
        val allTrackers = CommonTrackers.getAllTrackers()
        val trackers = mutableListOf<TrackerInfo>()
        
        // Simulate some apps having trackers based on package name patterns
        when {
            packageName.contains("facebook") || packageName.contains("meta") -> {
                trackers.add(CommonTrackers.FACEBOOK_ANALYTICS)
            }
            packageName.contains("google") -> {
                trackers.add(CommonTrackers.GOOGLE_ANALYTICS)
                if (Random.nextBoolean()) trackers.add(CommonTrackers.GOOGLE_ADMOB)
            }
            packageName.contains("game") || packageName.contains("unity") -> {
                trackers.add(CommonTrackers.UNITY_ADS)
                trackers.add(CommonTrackers.CRASHLYTICS)
            }
            packageName.contains("amazon") -> {
                trackers.add(CommonTrackers.AMAZON_ADVERTISING)
            }
            else -> {
                // Randomly assign some trackers to other apps for demo purposes
                if (Random.nextFloat() < 0.3f) trackers.add(CommonTrackers.GOOGLE_ANALYTICS)
                if (Random.nextFloat() < 0.2f) trackers.add(CommonTrackers.CRASHLYTICS)
                if (Random.nextFloat() < 0.15f) trackers.add(CommonTrackers.FLURRY)
            }
        }
        
        return trackers
    }
    
    private fun calculatePrivacyScore(
        dangerousPermissions: List<PermissionInfo>,
        trackers: List<TrackerInfo>
    ): Int {
        var score = 0
        
        // Base score from dangerous permissions (up to 60 points)
        score += (dangerousPermissions.size * 10).coerceAtMost(60)
        
        // Additional score from trackers (up to 40 points)
        val trackerScore = when {
            trackers.isEmpty() -> 0
            trackers.size <= 2 -> trackers.size * 10
            else -> 20 + (trackers.size - 2) * 5
        }
        score += trackerScore.coerceAtMost(40)
        
        // Cap at 100
        return score.coerceAtMost(100)
    }
    
    fun getAppIcon(packageName: String): android.graphics.drawable.Drawable? {
        return try {
            packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
}