# युवा शक्ति ग्रुप (Yuva Shakti Group)

**Version:** 4.5.7 (versionCode 43)  
**Package:** `com.yuvashaktigroup.app`  
**Family:** भाटी परिवार • कुचेरा

## Features

- सदस्य प्रबंधन (Members)
- खर्च / लेजर (Expenses & Ledger)
- हिंदू पंचांग + अलर्ट (Calendar)
- जमा राशि / अंशदान (Contributions)
- **वंशवली Flowchart** — हजारी राम जी से शुरू, drill-down tree + पत्नी + संतान जोड़ें
- सेटिंग्स, अपडेट चेक, नोटिफिकेशन, WhatsApp / UPI
- Firebase Auth + Firestore backend

## Project Structure

```
YuvaShaktiGroup-v4.5.7/
├── app/
│   ├── src/main/
│   │   ├── java/com/yuvashaktigroup/app/
│   │   │   ├── MainActivity.java      # WebView + JS Bridge
│   │   │   └── AppFileProvider.java
│   │   ├── assets/
│   │   │   ├── index.html             # Full frontend (HTML/CSS/JS)
│   │   │   └── ic_launcher.png
│   │   ├── res/mipmap-*/              # Launcher icons
│   │   └── AndroidManifest.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

## Build (Android Studio)

1. Android Studio में यह फोल्डर **Open** करें  
2. Gradle Sync होने दें (wrapper अपने आप बन जाएगा)  
3. **Build → Build Bundle(s) / APK(s) → Build APK(s)**  
4. APK मिलेगा: `app/build/outputs/apk/debug/app-debug.apk`

या terminal से:
```bash
./gradlew assembleDebug
# या
./gradlew assembleRelease
```

## GitHub पर Push

```bash
cd YuvaShaktiGroup-v4.5.7

git init
git add .
git commit -m "v4.5.7 - Yuva Shakti Group with Vamshavali flowchart"

git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/YuvaShaktiGroup.git
git push -u origin main

# Release tag (optional)
git tag v4.5.7
git push origin v4.5.7
```

> `YOUR_USERNAME` और repo नाम अपने GitHub अकाउंट के अनुसार बदलें।  
> नया repo बनाते समय **README / .gitignore add न करें** — यहाँ पहले से हैं।

## Version History

| Version | Code | Notes |
|---------|------|-------|
| 4.5.6   | 42   | Previous release |
| 4.5.7   | 43   | Source cleaned + वंशवली flowchart (हजारी राम जी) |

## Notes

- मुख्य UI `app/src/main/assets/index.html` में है  
- Native code सिर्फ WebView, share, notification, APK install, print देता है  
- Firebase project: `yuva-shakti-group-ace55`  
- वंशवली डेटा `localStorage` (`vamshaFlowchartData`) में सेव होता है  
