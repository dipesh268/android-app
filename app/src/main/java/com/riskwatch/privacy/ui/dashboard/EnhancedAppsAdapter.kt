package com.riskwatch.privacy.ui.dashboard

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.riskwatch.privacy.R
import com.riskwatch.privacy.data.models.AppInfo
import com.riskwatch.privacy.data.models.RiskLevel
import com.riskwatch.privacy.databinding.ItemAppCardEnhancedBinding

class EnhancedAppsAdapter(
    private val onItemClick: (AppInfo) -> Unit,
    private val onSettingsClick: (AppInfo) -> Unit = {}
) : ListAdapter<AppInfo, EnhancedAppsAdapter.EnhancedAppViewHolder>(AppDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnhancedAppViewHolder {
        val binding = ItemAppCardEnhancedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EnhancedAppViewHolder(binding, onItemClick, onSettingsClick)
    }
    
    override fun onBindViewHolder(holder: EnhancedAppViewHolder, position: Int) {
        holder.bind(getItem(position))
        
        // Animate item entrance
        animateItemEntrance(holder.itemView, position)
    }
    
    private fun animateItemEntrance(view: View, position: Int) {
        view.alpha = 0f
        view.translationY = 100f
        
        val delay = (position % 5) * 50L // Stagger animation for first 5 items
        
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .setStartDelay(delay)
            .setInterpolator(OvershootInterpolator(0.8f))
            .start()
    }
    
    class EnhancedAppViewHolder(
        private val binding: ItemAppCardEnhancedBinding,
        private val onItemClick: (AppInfo) -> Unit,
        private val onSettingsClick: (AppInfo) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        private var currentApp: AppInfo? = null
        
        init {
            setupClickListeners()
        }
        
        fun bind(appInfo: AppInfo) {
            currentApp = appInfo
            
            binding.apply {
                // Set app basic information
                appName.text = appInfo.appName
                riskScore.text = appInfo.privacyScore.toString()
                riskLevelText.text = appInfo.riskLevel.displayName
                permissionsCount.text = appInfo.dangerousPermissions.size.toString()
                trackersCount.text = appInfo.trackers.size.toString()
                
                // Set risk progress with animation
                animateRiskProgress(appInfo.privacyScore)
                
                // Apply dynamic theming based on risk level
                applyRiskTheming(appInfo.riskLevel)
                
                // Load app icon with fallback
                loadAppIcon(appInfo)
                
                // Animate elements
                animateRiskBadge()
            }
        }
        
        private fun setupClickListeners() {
            binding.apply {
                root.setOnClickListener {
                    currentApp?.let { app ->
                        animateCardPress {
                            onItemClick(app)
                        }
                    }
                }
                
                btnSettings.setOnClickListener {
                    currentApp?.let { onSettingsClick(it) }
                }
                
                root.setOnLongClickListener {
                    showQuickActions()
                    true
                }
            }
        }
        
        private fun animateCardPress(onComplete: () -> Unit) {
            val scaleDown = ObjectAnimator.ofFloat(binding.root, "scaleX", 1f, 0.95f).apply {
                duration = 100
            }
            val scaleUp = ObjectAnimator.ofFloat(binding.root, "scaleX", 0.95f, 1f).apply {
                duration = 100
                startDelay = 100
            }
            
            scaleDown.start()
            scaleUp.start()
            
            // Trigger callback after animation
            binding.root.postDelayed(onComplete, 150)
        }
        
        private fun animateRiskProgress(score: Int) {
            val animator = ValueAnimator.ofInt(0, score).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener { animation ->
                    val animatedValue = animation.animatedValue as Int
                    binding.riskProgress.setProgress(animatedValue, true)
                }
            }
            animator.start()
        }
        
        private fun animateRiskBadge() {
            binding.riskScoreBadge.apply {
                scaleX = 0f
                scaleY = 0f
                animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(600)
                    .setStartDelay(300)
                    .setInterpolator(OvershootInterpolator(1.2f))
                    .start()
            }
        }
        
        private fun applyRiskTheming(riskLevel: RiskLevel) {
            val context = binding.root.context
            
            val (indicatorDrawable, scoreColor, iconTint, progressColor) = when (riskLevel) {
                RiskLevel.HIGH -> Tuple4(
                    R.drawable.gradient_high_risk,
                    R.color.high_risk_color,
                    R.drawable.ic_warning,
                    R.color.high_risk_color
                )
                RiskLevel.MEDIUM -> Tuple4(
                    R.drawable.gradient_medium_risk,
                    R.color.medium_risk_color,
                    R.drawable.ic_warning,
                    R.color.medium_risk_color
                )
                RiskLevel.LOW -> Tuple4(
                    R.drawable.gradient_low_risk,
                    R.color.low_risk_color,
                    R.drawable.ic_shield_check,
                    R.color.low_risk_color
                )
            }
            
            binding.apply {
                // Apply gradient to risk indicator
                riskIndicator.background = ContextCompat.getDrawable(context, indicatorDrawable)
                
                // Apply colors to badge
                val badgeColor = ContextCompat.getColor(context, scoreColor)
                riskScoreBadge.setCardBackgroundColor(badgeColor)
                
                // Apply colors to risk level text and icon
                val riskColor = ContextCompat.getColor(context, scoreColor)
                riskLevelText.setTextColor(riskColor)
                riskIcon.setImageResource(iconTint)
                riskIcon.imageTintList = ColorStateList.valueOf(riskColor)
                
                // Apply colors to progress indicator
                val progressTintColor = ContextCompat.getColor(context, progressColor)
                riskProgress.setIndicatorColor(progressTintColor)
            }
        }
        
        private fun loadAppIcon(appInfo: AppInfo) {
            try {
                val context = binding.root.context
                val packageManager = context.packageManager
                val drawable = packageManager.getApplicationIcon(appInfo.packageName)
                
                binding.appIcon.setImageDrawable(drawable)
                
                // Animate icon appearance
                binding.iconContainer.apply {
                    alpha = 0f
                    scaleX = 0.8f
                    scaleY = 0.8f
                    animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(400)
                        .setStartDelay(100)
                        .setInterpolator(OvershootInterpolator(0.6f))
                        .start()
                }
            } catch (e: Exception) {
                // Use default icon with subtle animation
                binding.appIcon.setImageResource(R.drawable.ic_apps)
                binding.appIcon.alpha = 0.7f
            }
        }
        
        private fun showQuickActions() {
            binding.quickActions.apply {
                visibility = View.VISIBLE
                alpha = 0f
                animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()
            }
            
            // Hide after 3 seconds
            binding.root.postDelayed({
                hideQuickActions()
            }, 3000)
        }
        
        private fun hideQuickActions() {
            binding.quickActions.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction {
                    binding.quickActions.visibility = View.GONE
                }
                .start()
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
    
    // Helper data class for multiple return values
    private data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}