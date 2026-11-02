#!/usr/bin/env bash
set -e

PROJECT_DIR="/data/user/0/com.termux/files/home/.gemini/antigravity-cli/scratch/yuva-shakti-app"
BUILD_DIR="/data/user/0/com.termux/files/home/.gemini/antigravity-cli/scratch/yuva-shakti-apk-build"

echo "==> 🔄 Updating assets & sources..."
mkdir -p "${BUILD_DIR}/assets"
cp "${PROJECT_DIR}/index.html" "${BUILD_DIR}/assets/index.html"
cp "${PROJECT_DIR}/ic_launcher.png" "${BUILD_DIR}/assets/ic_launcher.png" 2>/dev/null || true
cp "${PROJECT_DIR}/android/AndroidManifest.xml" "${BUILD_DIR}/AndroidManifest.xml"
cp "${PROJECT_DIR}/android/src/com/yuvashaktigroup/app/MainActivity.java" "${BUILD_DIR}/src/com/yuvashaktigroup/app/MainActivity.java"
cp -r "${PROJECT_DIR}/android/res/"* "${BUILD_DIR}/res/"

echo "==> 📦 Compiling resources with AAPT2..."
cd "${BUILD_DIR}"
aapt2 compile --dir res -o res.zip
aapt2 link -I android.jar --manifest AndroidManifest.xml -o bin/base.apk -A assets --java gen res.zip

echo "==> ☕ Compiling Java sources..."
javac --release 8 -cp android.jar:gen -d bin/classes src/com/yuvashaktigroup/app/MainActivity.java gen/com/yuvashaktigroup/app/R.java

echo "==> ⚡ Converting to Dalvik Executable (DEX)..."
d8 --lib android.jar --output bin/dex/ bin/classes/com/yuvashaktigroup/app/*.class

echo "==> 📁 Packaging classes.dex..."
cd bin/dex
zip -u ../base.apk classes.dex
cd ../..

echo "==> ✍️ Signing APK..."
apksigner sign --ks release.keystore --ks-pass pass:yuvashakti123 --ks-key-alias yuvashakti --key-pass pass:yuvashakti123 --out YuvaShaktiGroup.apk bin/base.apk

echo "==> 🚚 Copying final APK..."
cp YuvaShaktiGroup.apk "${PROJECT_DIR}/YuvaShaktiGroup.apk"
cp YuvaShaktiGroup.apk "${PROJECT_DIR}/yuva-shakti.apk"
cp YuvaShaktiGroup.apk /sdcard/Download/YuvaShaktiGroup.apk 2>/dev/null || true

echo "==> ✅ APK Build Complete: ${PROJECT_DIR}/YuvaShaktiGroup.apk"
echo "==> 📱 Also copied to Downloads: /sdcard/Download/YuvaShaktiGroup.apk"
