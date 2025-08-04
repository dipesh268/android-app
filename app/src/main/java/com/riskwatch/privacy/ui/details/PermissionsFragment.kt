package com.riskwatch.privacy.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.riskwatch.privacy.data.models.AppInfo
import com.riskwatch.privacy.databinding.FragmentPermissionsBinding

class PermissionsFragment : Fragment() {
    
    private var _binding: FragmentPermissionsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var appInfo: AppInfo
    private lateinit var permissionsAdapter: PermissionsAdapter
    
    companion object {
        private const val ARG_APP_INFO = "app_info"
        
        fun newInstance(appInfo: AppInfo): PermissionsFragment {
            return PermissionsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_APP_INFO, appInfo)
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appInfo = arguments?.getParcelable(ARG_APP_INFO) ?: throw IllegalArgumentException("AppInfo is required")
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        displayPermissions()
    }
    
    private fun setupRecyclerView() {
        permissionsAdapter = PermissionsAdapter()
        
        binding.permissionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = permissionsAdapter
        }
    }
    
    private fun displayPermissions() {
        val dangerousPermissions = appInfo.dangerousPermissions
        
        if (dangerousPermissions.isEmpty()) {
            binding.emptyPermissionsState.visibility = View.VISIBLE
            binding.permissionsRecyclerView.visibility = View.GONE
        } else {
            binding.emptyPermissionsState.visibility = View.GONE
            binding.permissionsRecyclerView.visibility = View.VISIBLE
            
            // Animate permissions appearance
            permissionsAdapter.submitList(dangerousPermissions)
            
            // Stagger animation for each permission item
            binding.permissionsRecyclerView.post {
                for (i in 0 until minOf(dangerousPermissions.size, 5)) {
                    val viewHolder = binding.permissionsRecyclerView.findViewHolderForAdapterPosition(i)
                    viewHolder?.itemView?.apply {
                        alpha = 0f
                        translationY = 50f
                        animate()
                            .alpha(1f)
                            .translationY(0f)
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