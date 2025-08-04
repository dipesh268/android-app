package com.riskwatch.privacy.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.riskwatch.privacy.R
import com.riskwatch.privacy.data.models.TrackerCategory
import com.riskwatch.privacy.data.models.TrackerInfo
import com.riskwatch.privacy.databinding.ItemTrackerBinding

class TrackersAdapter : ListAdapter<TrackerInfo, TrackersAdapter.TrackerViewHolder>(TrackerDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackerViewHolder {
        val binding = ItemTrackerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackerViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: TrackerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class TrackerViewHolder(
        private val binding: ItemTrackerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(tracker: TrackerInfo) {
            binding.apply {
                // Set tracker information
                trackerName.text = tracker.name
                trackerDescription.text = tracker.description
                trackerCompany.text = "by ${tracker.company}"
                trackerCategoryChip.text = tracker.category.displayName
                
                // Set category-specific styling
                applyCategoryTheming(tracker.category)
                
                // Set tracker icon based on category
                val iconRes = when (tracker.category) {
                    TrackerCategory.ADVERTISING -> R.drawable.ic_ads
                    TrackerCategory.ANALYTICS -> R.drawable.ic_analytics
                    TrackerCategory.SOCIAL -> R.drawable.ic_people
                    TrackerCategory.CRASH_REPORTING -> R.drawable.ic_bug_report
                    TrackerCategory.PROFILING -> R.drawable.ic_person_search
                    TrackerCategory.LOCATION -> R.drawable.ic_location
                    TrackerCategory.OTHER -> R.drawable.ic_tracker
                }
                trackerIcon.setImageResource(iconRes)
                
                // Add subtle animation when binding
                root.alpha = 0f
                root.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()
            }
        }
        
        private fun applyCategoryTheming(category: TrackerCategory) {
            val context = binding.root.context
            
            val (chipBackgroundColor, indicatorColor) = when (category) {
                TrackerCategory.ADVERTISING -> Pair(R.color.advertising_light, R.color.advertising_color)
                TrackerCategory.ANALYTICS -> Pair(R.color.analytics_light, R.color.analytics_color)
                TrackerCategory.SOCIAL -> Pair(R.color.social_light, R.color.social_color)
                TrackerCategory.CRASH_REPORTING -> Pair(R.color.crash_reporting_light, R.color.crash_reporting_color)
                TrackerCategory.PROFILING -> Pair(R.color.profiling_light, R.color.profiling_color)
                TrackerCategory.LOCATION -> Pair(R.color.location_light, R.color.location_color)
                TrackerCategory.OTHER -> Pair(R.color.other_light, R.color.other_color)
            }
            
            binding.apply {
                // Apply category colors
                trackerCategoryChip.chipBackgroundColor = ContextCompat.getColorStateList(context, chipBackgroundColor)
                privacyImpactIndicator.setBackgroundColor(ContextCompat.getColor(context, indicatorColor))
            }
        }
    }
    
    private class TrackerDiffCallback : DiffUtil.ItemCallback<TrackerInfo>() {
        override fun areItemsTheSame(oldItem: TrackerInfo, newItem: TrackerInfo): Boolean {
            return oldItem.name == newItem.name
        }
        
        override fun areContentsTheSame(oldItem: TrackerInfo, newItem: TrackerInfo): Boolean {
            return oldItem == newItem
        }
    }
}