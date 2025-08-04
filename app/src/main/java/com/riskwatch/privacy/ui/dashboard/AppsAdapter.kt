package com.riskwatch.privacy.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.riskwatch.privacy.R
import com.riskwatch.privacy.data.models.AppInfo
import com.riskwatch.privacy.data.models.RiskLevel
import com.riskwatch.privacy.databinding.ItemAppCardBinding

class AppsAdapter(
    private val onItemClick: (AppInfo) -> Unit
) : ListAdapter<AppInfo, AppsAdapter.AppViewHolder>(AppDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppViewHolder(binding, onItemClick)
    }
    
    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class AppViewHolder(
        private val binding: ItemAppCardBinding,
        private val onItemClick: (AppInfo) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(appInfo: AppInfo) {
            binding.apply {
                // Set app basic info
                appName.text = appInfo.appName
                riskSummary.text = root.context.getString(
                    R.string.risk_summary_format,
                    appInfo.privacyScore,
                    appInfo.riskLevel.displayName
                )
                appDetails.text = root.context.getString(
                    R.string.permissions_trackers_format,
                    appInfo.dangerousPermissions.size,
                    appInfo.trackers.size
                )
                
                // Set risk level color bar and text color
                val riskColor = getRiskColor(appInfo.riskLevel)
                riskColorBar.setBackgroundColor(riskColor)
                riskSummary.setTextColor(riskColor)
                
                // Try to load app icon
                try {
                    val packageManager = root.context.packageManager
                    val drawable = packageManager.getApplicationIcon(appInfo.packageName)
                    appIcon.setImageDrawable(drawable)
                } catch (e: Exception) {
                    // Use default icon if app icon can't be loaded
                    appIcon.setImageResource(R.drawable.ic_apps)
                }
                
                // Set click listener
                root.setOnClickListener {
                    onItemClick(appInfo)
                }
            }
        }
        
        private fun getRiskColor(riskLevel: RiskLevel): Int {
            val colorRes = when (riskLevel) {
                RiskLevel.HIGH -> R.color.high_risk_color
                RiskLevel.MEDIUM -> R.color.medium_risk_color
                RiskLevel.LOW -> R.color.low_risk_color
            }
            return ContextCompat.getColor(binding.root.context, colorRes)
        }
    }
    
    private class AppDiffCallback : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.packageName == newItem.packageName
        }
        
        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem == newItem
        }
    }
}