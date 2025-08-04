package com.riskwatch.privacy.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.riskwatch.privacy.data.AppScanner
import com.riskwatch.privacy.data.models.AppInfo
import com.riskwatch.privacy.data.models.RiskLevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class FilterType {
    ALL_APPS,
    HIGH_RISK,
    MEDIUM_RISK,
    LOW_RISK,
    WITH_TRACKERS
}

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    
    private val appScanner = AppScanner(application)
    
    private val _allApps = MutableLiveData<List<AppInfo>>()
    private val allApps: LiveData<List<AppInfo>> = _allApps
    
    private val _filteredApps = MutableLiveData<List<AppInfo>>()
    val filteredApps: LiveData<List<AppInfo>> = _filteredApps
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _scanProgress = MutableLiveData<String>()
    val scanProgress: LiveData<String> = _scanProgress
    
    private val _lastScanTime = MutableLiveData<Long>()
    val lastScanTime: LiveData<Long> = _lastScanTime
    
    private val _totalScanned = MutableLiveData<Int>()
    val totalScanned: LiveData<Int> = _totalScanned
    
    private var currentFilter = FilterType.ALL_APPS
    
    fun loadApps() {
        _isLoading.value = true
        _totalScanned.value = 0
        
        viewModelScope.launch {
            try {
                // Simulate progressive scanning with status updates
                updateScanProgress("Initializing app scanner...")
                delay(300)
                
                updateScanProgress("Discovering installed applications...")
                delay(500)
                
                val apps = appScanner.scanAllApps()
                
                // Simulate processing with progress updates
                updateScanProgress("Analyzing ${apps.size} applications...")
                delay(400)
                
                updateScanProgress("Calculating privacy scores...")
                delay(600)
                
                updateScanProgress("Detecting tracking libraries...")
                delay(500)
                
                updateScanProgress("Finalizing results...")
                delay(300)
                
                _allApps.value = apps
                _totalScanned.value = apps.size
                _lastScanTime.value = System.currentTimeMillis()
                
                applyFilter(currentFilter)
                
            } catch (e: Exception) {
                // Handle error gracefully
                _scanProgress.value = "Scan failed: ${e.message}"
                _allApps.value = emptyList()
                _filteredApps.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun setFilter(filter: FilterType) {
        currentFilter = filter
        applyFilter(filter)
    }
    
    private fun applyFilter(filter: FilterType) {
        val apps = allApps.value ?: return
        
        viewModelScope.launch {
            // Add slight delay for smooth animation
            delay(100)
            
            val filtered = when (filter) {
                FilterType.ALL_APPS -> apps
                FilterType.HIGH_RISK -> apps.filter { it.riskLevel == RiskLevel.HIGH }
                FilterType.MEDIUM_RISK -> apps.filter { it.riskLevel == RiskLevel.MEDIUM }
                FilterType.LOW_RISK -> apps.filter { it.riskLevel == RiskLevel.LOW }
                FilterType.WITH_TRACKERS -> apps.filter { it.trackers.isNotEmpty() }
            }
            
            _filteredApps.value = filtered
            
            // Update progress with filter results
            val filterDescription = when (filter) {
                FilterType.ALL_APPS -> "all apps"
                FilterType.HIGH_RISK -> "high risk apps"
                FilterType.MEDIUM_RISK -> "medium risk apps"
                FilterType.LOW_RISK -> "low risk apps"
                FilterType.WITH_TRACKERS -> "apps with trackers"
            }
            
            _scanProgress.value = "Showing ${filtered.size} $filterDescription"
        }
    }
    
    private fun updateScanProgress(message: String) {
        _scanProgress.value = message
    }
    
    fun getAppIcon(packageName: String) = appScanner.getAppIcon(packageName)
    
    // Real-time statistics
    fun getPrivacyStats(): PrivacyStats {
        val apps = allApps.value ?: emptyList()
        return PrivacyStats(
            totalApps = apps.size,
            highRiskApps = apps.count { it.riskLevel == RiskLevel.HIGH },
            mediumRiskApps = apps.count { it.riskLevel == RiskLevel.MEDIUM },
            lowRiskApps = apps.count { it.riskLevel == RiskLevel.LOW },
            averageScore = if (apps.isNotEmpty()) apps.map { it.privacyScore }.average() else 0.0,
            appsWithTrackers = apps.count { it.trackers.isNotEmpty() },
            totalPermissions = apps.sumOf { it.dangerousPermissions.size },
            totalTrackers = apps.sumOf { it.trackers.size }
        )
    }
    
    // Quick scan for specific app
    fun quickScanApp(packageName: String): AppInfo? {
        return allApps.value?.find { it.packageName == packageName }
    }
    
    // Search functionality
    fun searchApps(query: String): List<AppInfo> {
        val apps = allApps.value ?: return emptyList()
        return apps.filter { 
            it.appName.contains(query, ignoreCase = true) ||
            it.packageName.contains(query, ignoreCase = true)
        }
    }
    
    // Get trending risk apps (recently updated with high scores)
    fun getTrendingRiskApps(): List<AppInfo> {
        val apps = allApps.value ?: return emptyList()
        val recentThreshold = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000) // 7 days
        
        return apps
            .filter { it.updateTime > recentThreshold && it.privacyScore > 70 }
            .sortedByDescending { it.privacyScore }
            .take(5)
    }
}

data class PrivacyStats(
    val totalApps: Int,
    val highRiskApps: Int,
    val mediumRiskApps: Int,
    val lowRiskApps: Int,
    val averageScore: Double,
    val appsWithTrackers: Int,
    val totalPermissions: Int,
    val totalTrackers: Int
)