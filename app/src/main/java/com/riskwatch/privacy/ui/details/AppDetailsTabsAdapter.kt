package com.riskwatch.privacy.ui.details

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.riskwatch.privacy.data.models.AppInfo

class AppDetailsTabsAdapter(
    fragment: Fragment,
    private var appInfo: AppInfo
) : FragmentStateAdapter(fragment) {
    
    override fun getItemCount(): Int = 2
    
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PermissionsFragment.newInstance(appInfo)
            1 -> TrackersFragment.newInstance(appInfo)
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
    
    fun updateAppInfo(newAppInfo: AppInfo) {
        appInfo = newAppInfo
        notifyDataSetChanged()
    }
}