package com.riskwatch.privacy.ui.details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.riskwatch.privacy.R
import com.riskwatch.privacy.data.models.AppInfo
import com.riskwatch.privacy.databinding.BottomSheetPrivacyReportBinding
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class PrivacyReportBottomSheet : BottomSheetDialogFragment() {
    
    private var _binding: BottomSheetPrivacyReportBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var appInfo: AppInfo
    private var selectedFormat: ReportFormat = ReportFormat.TEXT
    
    enum class ReportFormat {
        TEXT, PDF
    }
    
    companion object {
        private const val ARG_APP_INFO = "app_info"
        
        fun newInstance(appInfo: AppInfo): PrivacyReportBottomSheet {
            return PrivacyReportBottomSheet().apply {
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
        _binding = BottomSheetPrivacyReportBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupClickListeners()
    }
    
    private fun setupUI() {
        binding.apply {
            // Set app information
            reportTitle.text = getString(R.string.export_report_title, appInfo.appName)
            
            // Load app icon
            try {
                val packageManager = requireContext().packageManager
                val appIcon = packageManager.getApplicationIcon(appInfo.packageName)
                appIconImage.setImageDrawable(appIcon)
            } catch (e: Exception) {
                appIconImage.setImageResource(R.drawable.ic_apps)
            }
            
            appNameText.text = appInfo.appName
            riskScoreText.text = getString(R.string.score_format, appInfo.privacyScore)
            
            // Set risk score chip color
            val chipColor = when (appInfo.riskLevel) {
                com.riskwatch.privacy.data.models.RiskLevel.HIGH -> R.color.high_risk_color
                com.riskwatch.privacy.data.models.RiskLevel.MEDIUM -> R.color.medium_risk_color
                com.riskwatch.privacy.data.models.RiskLevel.LOW -> R.color.low_risk_color
            }
            riskScoreChip.chipBackgroundColor = resources.getColorStateList(chipColor, null)
            
            // Set default selection
            textFormatCard.isSelected = true
            selectedFormat = ReportFormat.TEXT
        }
    }
    
    private fun setupClickListeners() {
        binding.apply {
            // Format selection
            textFormatCard.setOnClickListener {
                selectFormat(ReportFormat.TEXT)
            }
            
            pdfFormatCard.setOnClickListener {
                selectFormat(ReportFormat.PDF)
            }
            
            // Generate and share button
            generateShareButton.setOnClickListener {
                generateAndShareReport()
            }
            
            // Close button
            closeButton.setOnClickListener {
                dismiss()
            }
        }
    }
    
    private fun selectFormat(format: ReportFormat) {
        selectedFormat = format
        
        binding.apply {
            // Update visual selection
            textFormatCard.isSelected = format == ReportFormat.TEXT
            pdfFormatCard.isSelected = format == ReportFormat.PDF
            
            // Update generate button text
            generateShareButton.text = when (format) {
                ReportFormat.TEXT -> "Generate Text Report"
                ReportFormat.PDF -> "Generate PDF Report"
            }
        }
    }
    
    private fun generateAndShareReport() {
        binding.generateShareButton.isEnabled = false
        binding.progressIndicator.visibility = View.VISIBLE
        
        // Simulate report generation delay
        binding.root.postDelayed({
            when (selectedFormat) {
                ReportFormat.TEXT -> generateTextReport()
                ReportFormat.PDF -> generatePDFReport()
            }
            
            binding.generateShareButton.isEnabled = true
            binding.progressIndicator.visibility = View.GONE
        }, 1500)
    }
    
    private fun generateTextReport() {
        try {
            val reportContent = buildTextReport()
            val fileName = "privacy_report_${appInfo.appName}_${getCurrentTimestamp()}.txt"
            
            // Save to internal storage
            val file = File(requireContext().cacheDir, fileName)
            FileWriter(file).use { writer ->
                writer.write(reportContent)
            }
            
            shareFile(file, "text/plain")
            
        } catch (e: Exception) {
            showError("Failed to generate text report: ${e.message}")
        }
    }
    
    private fun generatePDFReport() {
        // For now, generate a detailed text report
        // In a real implementation, you would use a PDF library like iText
        try {
            val reportContent = buildDetailedTextReport()
            val fileName = "privacy_report_${appInfo.appName}_${getCurrentTimestamp()}.txt"
            
            val file = File(requireContext().cacheDir, fileName)
            FileWriter(file).use { writer ->
                writer.write(reportContent)
            }
            
            shareFile(file, "text/plain")
            
            Snackbar.make(binding.root, "PDF generation coming soon! Shared as detailed text.", Snackbar.LENGTH_LONG).show()
            
        } catch (e: Exception) {
            showError("Failed to generate PDF report: ${e.message}")
        }
    }
    
    private fun buildTextReport(): String {
        return buildString {
            appendLine("PRIVACY REPORT")
            appendLine("==============")
            appendLine()
            appendLine("App: ${appInfo.appName}")
            appendLine("Package: ${appInfo.packageName}")
            appendLine("Privacy Score: ${appInfo.privacyScore}/100 (${appInfo.riskLevel.displayName})")
            appendLine("Generated: ${getCurrentDate()}")
            appendLine()
            
            appendLine("DANGEROUS PERMISSIONS (${appInfo.dangerousPermissions.size}):")
            appendLine("----------------------------")
            if (appInfo.dangerousPermissions.isEmpty()) {
                appendLine("• No dangerous permissions found")
            } else {
                appInfo.dangerousPermissions.forEach { permission ->
                    appendLine("• ${permission.name}")
                    appendLine("  ${permission.description}")
                    appendLine()
                }
            }
            
            appendLine("TRACKING LIBRARIES (${appInfo.trackers.size}):")
            appendLine("----------------------")
            if (appInfo.trackers.isEmpty()) {
                appendLine("• No trackers detected")
            } else {
                appInfo.trackers.forEach { tracker ->
                    appendLine("• ${tracker.name} (${tracker.category.displayName})")
                    appendLine("  ${tracker.description}")
                    appendLine("  Company: ${tracker.company}")
                    appendLine()
                }
            }
            
            appendLine("---")
            appendLine("Generated by RiskWatch Privacy Monitor")
        }
    }
    
    private fun buildDetailedTextReport(): String {
        return buildString {
            appendLine("DETAILED PRIVACY REPORT")
            appendLine("=======================")
            appendLine()
            appendLine("App Information:")
            appendLine("  Name: ${appInfo.appName}")
            appendLine("  Package: ${appInfo.packageName}")
            appendLine("  Version: ${appInfo.versionName} (${appInfo.versionCode})")
            appendLine("  Privacy Score: ${appInfo.privacyScore}/100")
            appendLine("  Risk Level: ${appInfo.riskLevel.displayName}")
            appendLine("  Install Date: ${Date(appInfo.installTime)}")
            appendLine("  Last Update: ${Date(appInfo.updateTime)}")
            appendLine("  System App: ${if (appInfo.isSystemApp) "Yes" else "No"}")
            appendLine()
            
            appendLine("Privacy Analysis:")
            appendLine("  Total Dangerous Permissions: ${appInfo.dangerousPermissions.size}")
            appendLine("  Total Trackers: ${appInfo.trackers.size}")
            appendLine("  Risk Assessment: ${appInfo.riskDescription}")
            appendLine()
            
            appendLine("DETAILED PERMISSIONS ANALYSIS:")
            appendLine("==============================")
            if (appInfo.dangerousPermissions.isEmpty()) {
                appendLine("✓ No dangerous permissions detected - This is excellent for privacy!")
            } else {
                appInfo.dangerousPermissions.forEachIndexed { index, permission ->
                    appendLine("${index + 1}. ${permission.name}")
                    appendLine("   Technical Name: ${permission.technicalName}")
                    appendLine("   Category: ${permission.category.displayName}")
                    appendLine("   Privacy Impact: ${permission.description}")
                    appendLine("   Risk Level: ${if (permission.isDangerous) "HIGH" else "LOW"}")
                    appendLine()
                }
            }
            
            appendLine("DETAILED TRACKERS ANALYSIS:")
            appendLine("===========================")
            if (appInfo.trackers.isEmpty()) {
                appendLine("✓ No tracking libraries detected - This app respects your privacy!")
            } else {
                appInfo.trackers.forEachIndexed { index, tracker ->
                    appendLine("${index + 1}. ${tracker.name}")
                    appendLine("   Category: ${tracker.category.displayName}")
                    appendLine("   Company: ${tracker.company}")
                    appendLine("   Purpose: ${tracker.description}")
                    appendLine("   Website: ${tracker.website}")
                    appendLine()
                }
            }
            
            appendLine("PRIVACY RECOMMENDATIONS:")
            appendLine("========================")
            when (appInfo.riskLevel) {
                com.riskwatch.privacy.data.models.RiskLevel.HIGH -> {
                    appendLine("⚠️  HIGH RISK - Consider the following actions:")
                    appendLine("• Review app permissions in device settings")
                    appendLine("• Consider alternative apps with better privacy")
                    appendLine("• Limit app usage if possible")
                    appendLine("• Monitor app behavior regularly")
                }
                com.riskwatch.privacy.data.models.RiskLevel.MEDIUM -> {
                    appendLine("⚡ MEDIUM RISK - Consider these improvements:")
                    appendLine("• Review and disable unnecessary permissions")
                    appendLine("• Monitor app updates for privacy changes")
                    appendLine("• Use app privacy controls when available")
                }
                com.riskwatch.privacy.data.models.RiskLevel.LOW -> {
                    appendLine("✅ LOW RISK - This app has good privacy practices:")
                    appendLine("• Continue using with confidence")
                    appendLine("• Monitor for future updates")
                    appendLine("• Consider as privacy-friendly alternative")
                }
            }
            
            appendLine()
            appendLine("---")
            appendLine("Report generated: ${getCurrentDate()}")
            appendLine("Generated by RiskWatch Privacy Monitor")
            appendLine("For more information, visit: https://riskwatch.app")
        }
    }
    
    private fun shareFile(file: File, mimeType: String) {
        try {
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Privacy Report - ${appInfo.appName}")
                putExtra(Intent.EXTRA_TEXT, "Privacy analysis report for ${appInfo.appName}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            startActivity(Intent.createChooser(shareIntent, "Share Privacy Report"))
            dismiss()
            
        } catch (e: Exception) {
            showError("Failed to share report: ${e.message}")
        }
    }
    
    private fun getCurrentTimestamp(): String {
        return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    }
    
    private fun getCurrentDate(): String {
        return SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm", Locale.getDefault()).format(Date())
    }
    
    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}