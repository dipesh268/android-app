package com.riskwatch.privacy.ui.dashboard

import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.riskwatch.privacy.R
import com.riskwatch.privacy.data.models.AppInfo
import com.riskwatch.privacy.databinding.FragmentDashboardBinding

class EnhancedDashboardFragment : Fragment(), MenuProvider {
    
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var enhancedAppsAdapter: EnhancedAppsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupMenu()
        setupRecyclerView()
        setupFilterChips()
        setupSwipeRefresh()
        setupFAB()
        observeViewModel()
        
        // Start initial scan with animation
        startScanWithAnimation()
    }
    
    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
    }
    
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_search -> {
                // TODO: Implement advanced search with filters
                showSearchBottomSheet()
                true
            }
            R.id.action_alerts -> {
                findNavController().navigate(R.id.action_dashboard_to_alerts)
                true
            }
            R.id.action_compare -> {
                findNavController().navigate(R.id.action_dashboard_to_compare)
                true
            }
            R.id.action_settings -> {
                findNavController().navigate(R.id.action_dashboard_to_settings)
                true
            }
            else -> false
        }
    }
    
    private fun setupRecyclerView() {
        enhancedAppsAdapter = EnhancedAppsAdapter(
            onItemClick = { appInfo ->
                navigateToAppDetails(appInfo)
            },
            onSettingsClick = { appInfo ->
                openAppSettings(appInfo)
            }
        )
        
        binding.appsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = enhancedAppsAdapter
            
            // Add scroll listener for animations
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    // Hide/show FAB based on scroll direction
                    if (dy > 0 && binding.fabQuickScan.isShown) {
                        binding.fabQuickScan.hide()
                    } else if (dy < 0 && !binding.fabQuickScan.isShown) {
                        binding.fabQuickScan.show()
                    }
                }
            })
        }
    }
    
    private fun setupFilterChips() {
        binding.filterChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            
            val checkedChipId = checkedIds.first()
            val filter = when (checkedChipId) {
                R.id.chip_high_risk -> FilterType.HIGH_RISK
                R.id.chip_medium_risk -> FilterType.MEDIUM_RISK
                R.id.chip_low_risk -> FilterType.LOW_RISK
                R.id.chip_with_trackers -> FilterType.WITH_TRACKERS
                else -> FilterType.ALL_APPS
            }
            
            // Animate filter change
            animateFilterChange(filter)
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.apply {
            setColorSchemeResources(
                R.color.primary_color,
                R.color.secondary_color,
                R.color.accent_color
            )
            
            setOnRefreshListener {
                performQuickScan()
            }
        }
    }
    
    private fun setupFAB() {
        binding.fabQuickScan.setOnClickListener {
            performQuickScan()
        }
    }
    
    private fun observeViewModel() {
        viewModel.filteredApps.observe(viewLifecycleOwner) { apps ->
            updateAppsList(apps)
            updateStatsCard(apps)
            updateEmptyState(apps.isEmpty())
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            updateLoadingState(isLoading)
        }
        
        viewModel.scanProgress.observe(viewLifecycleOwner) { progress ->
            updateScanProgress(progress)
        }
    }
    
    private fun startScanWithAnimation() {
        showScanningUI()
        viewModel.loadApps()
    }
    
    private fun performQuickScan() {
        binding.swipeRefresh.isRefreshing = false
        
        // Show quick scan feedback
        Snackbar.make(binding.root, "Performing quick scan...", Snackbar.LENGTH_SHORT)
            .setAction("Cancel") {
                // TODO: Implement scan cancellation
            }
            .show()
        
        viewModel.loadApps()
    }
    
    private fun updateAppsList(apps: List<AppInfo>) {
        enhancedAppsAdapter.submitList(apps) {
            // Scroll to top when list updates
            if (apps.isNotEmpty()) {
                binding.appsRecyclerView.scrollToPosition(0)
            }
        }
    }
    
    private fun updateStatsCard(apps: List<AppInfo>) {
        val totalApps = apps.size
        val highRiskApps = apps.count { it.riskLevel.name == "HIGH" }
        val averageScore = if (apps.isNotEmpty()) {
            apps.map { it.privacyScore }.average().toInt()
        } else 0
        
        // Animate counter updates
        animateCounter(binding.totalAppsCount, totalApps)
        animateCounter(binding.highRiskCount, highRiskApps)
        animateCounter(binding.averageScore, averageScore)
    }
    
    private fun animateCounter(textView: android.widget.TextView, targetValue: Int) {
        val currentValue = textView.text.toString().toIntOrNull() ?: 0
        
        ValueAnimator.ofInt(currentValue, targetValue).apply {
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                textView.text = animation.animatedValue.toString()
            }
            start()
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.swipeRefresh.visibility = if (isEmpty) View.GONE else View.VISIBLE
        
        if (isEmpty) {
            // Animate empty state appearance
            binding.emptyState.apply {
                alpha = 0f
                translationY = 50f
                animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .start()
            }
        }
    }
    
    private fun updateLoadingState(isLoading: Boolean) {
        binding.loadingContainer.visibility = if (isLoading) View.VISIBLE else View.GONE
        
        if (isLoading) {
            showScanningUI()
        }
    }
    
    private fun updateScanProgress(progress: String) {
        binding.scanningStatus.text = progress
    }
    
    private fun showScanningUI() {
        binding.loadingContainer.apply {
            visibility = View.VISIBLE
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(200)
                .start()
        }
        
        // Animate scanning status updates
        val scanningMessages = listOf(
            "Analyzing app permissions...",
            "Detecting tracking libraries...",
            "Calculating privacy scores...",
            "Processing security data..."
        )
        
        var messageIndex = 0
        val messageAnimator = object : Runnable {
            override fun run() {
                if (messageIndex < scanningMessages.size && binding.loadingContainer.visibility == View.VISIBLE) {
                    binding.scanningStatus.text = scanningMessages[messageIndex]
                    messageIndex++
                    binding.root.postDelayed(this, 1000)
                }
            }
        }
        binding.root.postDelayed(messageAnimator, 500)
    }
    
    private fun animateFilterChange(filter: FilterType) {
        // Fade out current list
        binding.appsRecyclerView.animate()
            .alpha(0.3f)
            .setDuration(150)
            .withEndAction {
                // Apply filter
                viewModel.setFilter(filter)
                
                // Fade in new list
                binding.appsRecyclerView.animate()
                    .alpha(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }
    
    private fun navigateToAppDetails(appInfo: AppInfo) {
        val action = DashboardFragmentDirections.actionDashboardToAppDetails(appInfo)
        findNavController().navigate(action)
    }
    
    private fun openAppSettings(appInfo: AppInfo) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", appInfo.packageName, null)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Snackbar.make(binding.root, "Unable to open app settings", Snackbar.LENGTH_SHORT).show()
        }
    }
    
    private fun showSearchBottomSheet() {
        // TODO: Implement advanced search bottom sheet
        Snackbar.make(binding.root, "Advanced search coming soon!", Snackbar.LENGTH_SHORT).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}