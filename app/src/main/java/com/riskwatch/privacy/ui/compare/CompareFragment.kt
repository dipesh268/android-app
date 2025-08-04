package com.riskwatch.privacy.ui.compare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.riskwatch.privacy.databinding.FragmentCompareBinding
import com.riskwatch.privacy.ui.dashboard.DashboardViewModel

class CompareFragment : Fragment() {
    
    private var _binding: FragmentCompareBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DashboardViewModel by activityViewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompareBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeData()
    }
    
    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        
        // Set up app selection for comparison
        // This would be implemented with app selection UI
    }
    
    private fun observeData() {
        // Observe any live data if needed
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}