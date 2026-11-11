#!/usr/bin/env bash
set -e

PROJECT_DIR="/data/user/0/com.termux/files/home/.gemini/antigravity-cli/scratch/yuva-shakti-app"
BUILD_DIR="/data/user/0/com.termux/files/home/.gemini/antigravity-cli/scratch/yuva-shakti-apk-build"

echo "==> 🔄 Updating assets & sources..."
mkdir -p "${BUILD_DIR}/assets"
cp "${PROJECT_DIR}/index.html" "${BUILD_DIR}/assets/index.html"
cp "${PROJECT_DIR}/payment-methods.html" "${BUILD_DIR}/assets/payment-methods.html" 2>/dev/null || true
cp "${PROJECT_DIR}/cash-counter.html" "${BUILD_DIR}/assets/cash-counter.html" 2>/dev/null || true
cp "${PROJECT_DIR}/notes_data.js" "${BUILD_DIR}/assets/notes_data.js" 2>/dev/null || true
mkdir -p "${BUILD_DIR}/assets/notes"
cp -r "${PROJECT_DIR}/assets/notes/"* "${BUILD_DIR}/assets/notes/" 2>/dev/null || true
cp "${PROJECT_DIR}/ic_launcher.png" "${BUILD_DIR}/assets/ic_launcher.png" 2>/dev/null || true
cp "${PROJECT_DIR}/android/AndroidManifest.xml" "${BUILD_DIR}/AndroidManifest.xml"
cp -r "${PROJECT_DIR}/android/src/"* "${BUILD_DIR}/src/"
cp -r "${PROJECT_DIR}/android/res/"* "${BUILD_DIR}/res/"

echo "==> 🧹 Cleaning build directory..."
cd "${BUILD_DIR}"
rm -rf bin/classes bin/dex gen bin/base.apk res.zip
mkdir -p bin/classes bin/dex gen

echo "==> 📦 Compiling resources with AAPT2..."
aapt2 compile --dir res -o res.zip
aapt2 link -I android.jar --manifest AndroidManifest.xml -o bin/base.apk -A assets --java gen res.zip

echo "==> ☕ Compiling Java sources..."
javac --release 8 -cp android.jar:gen -d bin/classes src/com/yuvashaktigroup/app/*.java gen/com/yuvashaktigroup/app/R.java

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
