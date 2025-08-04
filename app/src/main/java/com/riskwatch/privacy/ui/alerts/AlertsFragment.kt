package com.riskwatch.privacy.ui.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.riskwatch.privacy.data.models.PrivacyAlert
import com.riskwatch.privacy.databinding.FragmentAlertsBinding

class AlertsFragment : Fragment() {
    
    private var _binding: FragmentAlertsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AlertsViewModel by viewModels()
    private lateinit var alertsAdapter: AlertsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupRecyclerView()
        observeViewModel()
        
        // Load alerts
        viewModel.loadAlerts()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupRecyclerView() {
        alertsAdapter = AlertsAdapter { alert ->
            // Navigate to app details when alert is clicked
            val action = AlertsFragmentDirections.actionAlertsToAppDetails(alert.appInfo)
            findNavController().navigate(action)
        }
        
        binding.alertsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = alertsAdapter
        }
    }
    
    private fun observeViewModel() {
        viewModel.alerts.observe(viewLifecycleOwner) { alerts ->
            updateAlertsList(alerts)
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
    
    private fun updateAlertsList(alerts: List<PrivacyAlert>) {
        if (alerts.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.alertsRecyclerView.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.alertsRecyclerView.visibility = View.VISIBLE
            
            alertsAdapter.submitList(alerts)
            
            // Animate alerts appearance
            binding.alertsRecyclerView.post {
                for (i in 0 until minOf(alerts.size, 5)) {
                    val viewHolder = binding.alertsRecyclerView.findViewHolderForAdapterPosition(i)
                    viewHolder?.itemView?.apply {
                        alpha = 0f
                        translationX = 100f
                        animate()
                            .alpha(1f)
                            .translationX(0f)
                            .setDuration(300)
                            .setStartDelay(i * 100L)
                            .start()
                    }
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}