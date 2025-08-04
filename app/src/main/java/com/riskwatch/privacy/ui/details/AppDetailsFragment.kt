package com.riskwatch.privacy.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.riskwatch.privacy.databinding.FragmentAppDetailsBinding
import com.riskwatch.privacy.ui.dashboard.DashboardViewModel

class AppDetailsFragment : Fragment() {
    
    private var _binding: FragmentAppDetailsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DashboardViewModel by activityViewModels()
    private val args: AppDetailsFragmentArgs by navArgs()
    
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
        
        setupUI()
        observeData()
    }
    
    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        
        // Set up app info
        args.appInfo?.let { appInfo ->
            binding.tvAppName.text = appInfo.name
            binding.tvPackageName.text = appInfo.packageName
            binding.tvVersion.text = appInfo.version
            binding.tvInstallDate.text = appInfo.installDate
            
            // Set privacy score
            binding.progressPrivacyScore.progress = appInfo.privacyScore
            binding.tvPrivacyScore.text = "${appInfo.privacyScore}%"
            
            // Set up permissions list
            // This would be implemented with a RecyclerView adapter
        }
    }
    
    private fun observeData() {
        // Observe any live data if needed
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}