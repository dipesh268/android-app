package com.riskwatch.privacy.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.riskwatch.privacy.data.models.PermissionInfo
import com.riskwatch.privacy.databinding.ItemPermissionBinding

class PermissionsAdapter : ListAdapter<PermissionInfo, PermissionsAdapter.PermissionViewHolder>(PermissionDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionViewHolder {
        val binding = ItemPermissionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PermissionViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: PermissionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class PermissionViewHolder(
        private val binding: ItemPermissionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(permission: PermissionInfo) {
            binding.apply {
                permissionIcon.setImageResource(permission.iconRes)
                permissionName.text = permission.name
                permissionDescription.text = permission.description
                permissionTechnical.text = permission.technicalName
                
                // Add subtle animation when binding
                root.alpha = 0f
                root.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()
            }
        }
    }
    
    private class PermissionDiffCallback : DiffUtil.ItemCallback<PermissionInfo>() {
        override fun areItemsTheSame(oldItem: PermissionInfo, newItem: PermissionInfo): Boolean {
            return oldItem.technicalName == newItem.technicalName
        }
        
        override fun areContentsTheSame(oldItem: PermissionInfo, newItem: PermissionInfo): Boolean {
            return oldItem == newItem
        }
    }
}