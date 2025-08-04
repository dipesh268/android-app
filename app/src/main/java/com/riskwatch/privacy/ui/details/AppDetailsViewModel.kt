package com.riskwatch.privacy.ui.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.riskwatch.privacy.data.AppScanner
import com.riskwatch.privacy.data.models.AppInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AppDetailsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val appScanner = AppScanner(application)
    
    private val _appDetails = MutableLiveData<AppInfo?>()
    val appDetails: LiveData<AppInfo?> = _appDetails
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun loadAppDetails(packageName: String) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                // Simulate loading delay for smooth UX
                delay(500)
                
                // Get fresh app details from scanner
                val apps = appScanner.scanAllApps()
                val appDetails = apps.find { it.packageName == packageName }
                
                _appDetails.value = appDetails
                
                if (appDetails == null) {
                    _error.value = "App not found or could not be analyzed"
                }
                
            } catch (e: Exception) {
                _error.value = "Failed to load app details: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun refreshAppDetails(packageName: String) {
        loadAppDetails(packageName)
    }
}