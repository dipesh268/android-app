package com.riskwatch.privacy.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.riskwatch.privacy.data.AppScanner
import com.riskwatch.privacy.data.models.AppInfo
import com.riskwatch.privacy.data.models.RiskLevel
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
    
    private var currentFilter = FilterType.ALL_APPS
    
    fun loadApps() {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val apps = appScanner.scanAllApps()
                _allApps.value = apps
                applyFilter(currentFilter)
            } catch (e: Exception) {
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
        
            val filtered = when (filter) {
                FilterType.ALL_APPS -> apps
                FilterType.HIGH_RISK -> apps.filter { it.riskLevel == RiskLevel.HIGH }
                FilterType.MEDIUM_RISK -> apps.filter { it.riskLevel == RiskLevel.MEDIUM }
                FilterType.LOW_RISK -> apps.filter { it.riskLevel == RiskLevel.LOW }
                FilterType.WITH_TRACKERS -> apps.filter { it.trackers.isNotEmpty() }
            }
            
            _filteredApps.value = filtered
    }
    
    fun getAppIcon(packageName: String) = appScanner.getAppIcon(packageName)
    }