# RiskWatch - Privacy Monitoring Android App

RiskWatch is a comprehensive Android application that analyzes the privacy risks of installed applications on your device. It provides users with detailed insights into app permissions, tracking libraries, and overall privacy scores through an intuitive heatmap interface.

## Features

### 📱 Main Dashboard (Privacy Heatmap)
- **Color-coded risk assessment**: Visual heatmap with red/yellow/green risk indicators
- **Filter chips**: Quick filtering by risk level (High/Medium/Low) and tracker presence
- **App list**: Scrollable list of all installed apps with privacy scores
- **Real-time scanning**: Automatic analysis of installed applications

### 🔍 App Details View
- **Circular progress gauge**: Visual representation of privacy risk score (0-100)
- **Dual-tab interface**: 
  - **Permissions tab**: List of dangerous permissions with plain-language explanations
  - **Trackers tab**: Identified tracking libraries and their purposes
- **Direct settings access**: One-tap navigation to app permission settings
- **Export functionality**: Generate and share privacy reports

### ⚖️ App Comparison View
- **Side-by-side comparison**: Compare privacy profiles of two apps
- **Permission checklist**: Visual comparison of critical permissions
- **Privacy score comparison**: Easy identification of safer alternatives

### 🚨 Privacy Alerts Timeline
- **Chronological alert feed**: Timeline of privacy-related events
- **Alert types**:
  - High-risk app detection
  - New app installations
  - Permission changes
  - Tracker detection
- **Direct navigation**: Tap alerts to view detailed app information

### ⚙️ Settings & Configuration
- **Scanning preferences**: Configure automatic new app scanning
- **Periodic scans**: Set up background privacy monitoring
- **Offline mode**: Disable tracker detection for offline usage
- **About section**: Information about scoring methodology and privacy policy

### 📊 Privacy Report Export
- **Multiple formats**: Text (.txt) and PDF export options
- **Detailed analysis**: Comprehensive privacy assessment reports
- **Easy sharing**: Direct sharing capabilities for privacy reports

## Technical Architecture

### 🏗️ Project Structure
```
app/src/main/
├── java/com/riskwatch/privacy/
│   ├── data/
│   │   ├── models/          # Data models (AppInfo, PermissionInfo, TrackerInfo)
│   │   └── AppScanner.kt    # Core app analysis engine
│   ├── ui/
│   │   ├── dashboard/       # Main heatmap interface
│   │   ├── details/         # App details with tabs
│   │   ├── compare/         # App comparison view
│   │   ├── alerts/          # Privacy alerts timeline
│   │   └── settings/        # Configuration screen
│   └── MainActivity.kt      # Main navigation host
└── res/
    ├── layout/              # UI layouts for all screens
    ├── values/              # Colors, strings, themes
    ├── drawable/            # Icons and graphics
    ├── menu/                # Toolbar menus
    └── navigation/          # Navigation graph
```

### 🛠️ Technology Stack
- **Language**: Kotlin
- **UI Framework**: Material Design 3
- **Architecture**: MVVM with LiveData
- **Navigation**: Navigation Component with Safe Args
- **Async Operations**: Coroutines
- **Permissions Analysis**: Android PackageManager APIs
- **Tracker Detection**: Simulated (extensible for Exodus Privacy API)
- **UI Binding**: View Binding
- **Progress Visualization**: Custom circular progress indicators

### 📊 Privacy Scoring Algorithm
The app calculates privacy scores based on:
- **Dangerous Permissions** (60% weight): Each dangerous permission adds 10 points
- **Tracking Libraries** (40% weight): Trackers contribute additional risk points
- **Risk Levels**:
  - 🟢 **Low Risk**: 0-39 points
  - 🟡 **Medium Risk**: 40-74 points
  - 🔴 **High Risk**: 75-100 points

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox (2020.3.1) or newer
- Android SDK API level 24 (Android 7.0) or higher
- Kotlin 1.9.20 or newer

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd riskwatch-android
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Sync Dependencies**
   ```bash
   ./gradlew build
   ```

4. **Configure Permissions**
   The app requires the `QUERY_ALL_PACKAGES` permission for comprehensive app scanning. This permission needs to be declared in the manifest and may require special handling on Android 11+.

5. **Run the Application**
   - Connect an Android device or start an emulator
   - Click "Run" in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

### Building for Release

1. **Generate Signed APK**
   ```bash
   ./gradlew assembleRelease
   ```

2. **Create App Bundle** (recommended for Play Store)
   ```bash
   ./gradlew bundleRelease
   ```

## Key Dependencies

```gradle
dependencies {
    // Core Android
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    
    // Navigation
    implementation 'androidx.navigation:navigation-fragment-ktx:2.7.6'
    implementation 'androidx.navigation:navigation-ui-ktx:2.7.6'
    
    // Architecture Components
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'
    
    // Async Operations
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    
    // UI Components
    implementation 'com.github.lzyzsd:circleprogress:1.2.1'
    
    // API Integration (for future Exodus Privacy integration)
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
}
```

## Permissions Required

```xml
<!-- Essential for app scanning -->
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />

<!-- Network access for tracker detection -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Future Enhancements

### 🔮 Planned Features
- **Real Exodus Privacy API Integration**: Replace simulated tracker detection
- **Machine Learning Risk Assessment**: Enhanced scoring with ML models
- **Privacy Trends**: Historical privacy score tracking
- **Community Reports**: Crowdsourced privacy insights
- **Advanced Filtering**: Custom filter creation and saved searches
- **Notification System**: Real-time privacy alerts
- **Backup/Restore**: Settings and preferences sync

### 🛡️ Security Considerations
- All app analysis performed locally on device
- No user data transmitted without explicit consent
- Optional offline mode for enhanced privacy
- Transparent data collection policies

## Contributing

### Development Guidelines
1. Follow Material Design 3 principles
2. Maintain MVVM architecture patterns
3. Write comprehensive unit tests
4. Use meaningful commit messages
5. Document new features thoroughly

### Code Style
- Kotlin coding conventions
- 4-space indentation
- Descriptive variable and function names
- Comprehensive inline documentation

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues, feature requests, or questions:
- Create an issue in the GitHub repository
- Follow the issue template for bug reports
- Include device information and Android version for technical issues

## Privacy Notice

RiskWatch analyzes app permissions and metadata stored locally on your device. The app does not collect or transmit personal information without explicit user consent. All privacy analysis is performed locally to protect user data.

---

**RiskWatch** - Empowering users with transparent privacy insights 🛡️