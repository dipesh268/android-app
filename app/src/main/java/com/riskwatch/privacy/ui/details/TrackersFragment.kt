package com.riskwatch.privacy.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.riskwatch.privacy.data.models.AppInfo
import com.riskwatch.privacy.databinding.FragmentTrackersBinding

class TrackersFragment : Fragment() {
    
    private var _binding: FragmentTrackersBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var appInfo: AppInfo
    private lateinit var trackersAdapter: TrackersAdapter
    
    companion object {
        private const val ARG_APP_INFO = "app_info"
        
        fun newInstance(appInfo: AppInfo): TrackersFragment {
            return TrackersFragment().apply {
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
        _binding = FragmentTrackersBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        displayTrackers()
    }
    
    private fun setupRecyclerView() {
        trackersAdapter = TrackersAdapter()
        
        binding.trackersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = trackersAdapter
        }
    }
    
    private fun displayTrackers() {
        val trackers = appInfo.trackers
        
        if (trackers.isEmpty()) {
            binding.emptyTrackersState.visibility = View.VISIBLE
            binding.trackersRecyclerView.visibility = View.GONE
        } else {
            binding.emptyTrackersState.visibility = View.GONE
            binding.trackersRecyclerView.visibility = View.VISIBLE
            
            // Animate trackers appearance
            trackersAdapter.submitList(trackers)
            
            // Stagger animation for each tracker item
            binding.trackersRecyclerView.post {
                for (i in 0 until minOf(trackers.size, 5)) {
                    val viewHolder = binding.trackersRecyclerView.findViewHolderForAdapterPosition(i)
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