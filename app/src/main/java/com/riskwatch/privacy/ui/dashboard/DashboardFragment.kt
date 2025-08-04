package com.riskwatch.privacy.ui.dashboard

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.riskwatch.privacy.R
import com.riskwatch.privacy.data.models.AppInfo
import com.riskwatch.privacy.data.models.RiskLevel
import com.riskwatch.privacy.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment(), MenuProvider {
    
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var appsAdapter: AppsAdapter
    
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
        observeViewModel()
        
        // Start scanning apps
        viewModel.loadApps()
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
                // TODO: Implement search functionality
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
        appsAdapter = AppsAdapter { appInfo ->
            // Navigate to app details
            val action = DashboardFragmentDirections.actionDashboardToAppDetails(appInfo)
            findNavController().navigate(action)
        }
        
        binding.appsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appsAdapter
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
            
            viewModel.setFilter(filter)
        }
    }
    
    private fun observeViewModel() {
        viewModel.filteredApps.observe(viewLifecycleOwner) { apps ->
            appsAdapter.submitList(apps)
            updateEmptyState(apps.isEmpty())
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.appsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}