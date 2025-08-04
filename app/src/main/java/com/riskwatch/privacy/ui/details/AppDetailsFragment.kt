package com.riskwatch.privacy.ui.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.riskwatch.privacy.R
import com.riskwatch.privacy.data.models.AppInfo
import com.riskwatch.privacy.data.models.RiskLevel
import com.riskwatch.privacy.databinding.FragmentAppDetailsBinding

class AppDetailsFragment : Fragment(), MenuProvider {
    
    private var _binding: FragmentAppDetailsBinding? = null
    private val binding get() = _binding!!
    
    private val args: AppDetailsFragmentArgs by navArgs()
    private val viewModel: AppDetailsViewModel by viewModels()
    
    private lateinit var appInfo: AppInfo
    private lateinit var tabsAdapter: AppDetailsTabsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        appInfo = args.appInfo
        
        setupMenu()
        setupToolbar()
        setupAppSummary()
        setupTabs()
        setupClickListeners()
        
        // Load detailed app information
        viewModel.loadAppDetails(appInfo.packageName)
        observeViewModel()
    }
    
    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.app_details_menu, menu)
    }
    
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_export -> {
                showExportBottomSheet()
                true
            }
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> false
        }
    }
    
    private fun setupToolbar() {
        binding.toolbar.apply {
            title = appInfo.appName
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
    
    private fun setupAppSummary() {
        binding.apply {
            // Set app name
            appName.text = appInfo.appName
            
            // Load app icon
            try {
                val packageManager = requireContext().packageManager
                val appIcon = packageManager.getApplicationIcon(appInfo.packageName)
                binding.appIcon.setImageDrawable(appIcon)
            } catch (e: Exception) {
                binding.appIcon.setImageResource(R.drawable.ic_apps)
            }
            
            // Setup risk gauge
            setupRiskGauge()
            
            // Setup settings button
            settingsButton.setOnClickListener {
                openAppSettings()
            }
        }
    }
    
    private fun setupRiskGauge() {
        binding.apply {
            // Set risk score and level
            riskScoreText.text = appInfo.privacyScore.toString()
            riskLevelText.text = appInfo.riskLevel.displayName
            
            // Configure circular gauge
            riskGauge.apply {
                max = 100f
                progress = appInfo.privacyScore.toFloat()
                
                // Set color based on risk level
                val gaugeColor = when (appInfo.riskLevel) {
                    RiskLevel.HIGH -> R.color.high_risk_color
                    RiskLevel.MEDIUM -> R.color.medium_risk_color
                    RiskLevel.LOW -> R.color.low_risk_color
                }
                finishedStrokeColor = resources.getColor(gaugeColor, null)
            }
            
            // Set risk level text color
            val textColor = when (appInfo.riskLevel) {
                RiskLevel.HIGH -> R.color.high_risk_color
                RiskLevel.MEDIUM -> R.color.medium_risk_color
                RiskLevel.LOW -> R.color.low_risk_color
            }
            riskLevelText.setTextColor(resources.getColor(textColor, null))
            
            // Animate gauge
            animateGauge()
        }
    }
    
    private fun animateGauge() {
        binding.riskGauge.apply {
            // Start from 0 and animate to actual score
            progress = 0f
            post {
                animate()
                    .setDuration(1500)
                    .withStartAction {
                        // Animate progress smoothly
                        val animator = android.animation.ValueAnimator.ofFloat(0f, appInfo.privacyScore.toFloat())
                        animator.duration = 1500
                        animator.addUpdateListener { animation ->
                            progress = animation.animatedValue as Float
                        }
                        animator.start()
                    }
                    .start()
            }
        }
    }
    
    private fun setupTabs() {
        tabsAdapter = AppDetailsTabsAdapter(this, appInfo)
        binding.viewPager.adapter = tabsAdapter
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.permissions_tab)
                1 -> getString(R.string.trackers_tab)
                else -> ""
            }
        }.attach()
    }
    
    private fun setupClickListeners() {
        // Additional click listeners can be added here
    }
    
    private fun observeViewModel() {
        viewModel.appDetails.observe(viewLifecycleOwner) { details ->
            // Update UI with detailed app information
            details?.let {
                // Update tabs with fresh data
                tabsAdapter.updateAppInfo(it)
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show/hide loading state if needed
        }
    }
    
    private fun openAppSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", appInfo.packageName, null)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Snackbar.make(binding.root, "Unable to open app settings", Snackbar.LENGTH_SHORT).show()
        }
    }
    
    private fun showExportBottomSheet() {
        val bottomSheet = PrivacyReportBottomSheet.newInstance(appInfo)
        bottomSheet.show(parentFragmentManager, "PrivacyReportBottomSheet")
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}