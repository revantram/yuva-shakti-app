#!/usr/bin/env bash
set -e

APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="${TMPDIR:-/data/user/0/ai.antigem/files/usr/tmp}/yuva_shakti_build"
ANDROID_JAR="${APP_DIR}/android.jar"
if [ ! -f "${ANDROID_JAR}" ]; then
  ANDROID_JAR="/data/user/0/ai.antigem/files/home/meri-taiyari-mcq-prep/android.jar"
fi
R8_JAR="${APP_DIR}/r8.jar"
if [ ! -f "${R8_JAR}" ]; then
  R8_JAR="/data/user/0/ai.antigem/files/home/meri-taiyari-mcq-prep/r8.jar"
fi

KEYSTORE="${APP_DIR}/android/release.keystore"
if [ ! -f "${KEYSTORE}" ]; then
  KEYSTORE="${APP_DIR}/release.keystore"
fi
if [ ! -f "${KEYSTORE}" ]; then
  KEYSTORE="/data/user/0/ai.antigem/files/home/meri-taiyari-mcq-prep/release.keystore"
fi

KEY_PASS="${KEY_PASSWORD:-yuvashakti123}"
STORE_PASS="${KEYSTORE_PASSWORD:-yuvashakti123}"
ALIAS="${KEY_ALIAS:-yuvashakti}"

echo "==> 🧹 Setting up build workspace in ${BUILD_DIR}..."
rm -rf "${BUILD_DIR}"
mkdir -p "${BUILD_DIR}/gen" "${BUILD_DIR}/classes" "${BUILD_DIR}/assets"

echo "==> 🔄 Copying web assets..."
cp "${APP_DIR}/index.html" "${BUILD_DIR}/assets/index.html"
cp "${APP_DIR}/login.html" "${BUILD_DIR}/assets/login.html" 2>/dev/null || true
cp "${APP_DIR}/payment-methods.html" "${BUILD_DIR}/assets/payment-methods.html" 2>/dev/null || true
cp "${APP_DIR}/cash-counter.html" "${BUILD_DIR}/assets/cash-counter.html" 2>/dev/null || true
cp "${APP_DIR}/notes_data.js" "${BUILD_DIR}/assets/notes_data.js" 2>/dev/null || true
cp "${APP_DIR}/icons_data.js" "${BUILD_DIR}/assets/icons_data.js" 2>/dev/null || true
cp "${APP_DIR}/manifest.json" "${BUILD_DIR}/assets/manifest.json" 2>/dev/null || true
cp "${APP_DIR}/favicon.png" "${BUILD_DIR}/assets/favicon.png" 2>/dev/null || true
cp "${APP_DIR}/ic_launcher.png" "${BUILD_DIR}/assets/ic_launcher.png" 2>/dev/null || true
if [ -d "${APP_DIR}/assets" ]; then
  cp -r "${APP_DIR}/assets"/* "${BUILD_DIR}/assets/"
  mkdir -p "${BUILD_DIR}/assets/assets"
  cp -r "${APP_DIR}/assets"/* "${BUILD_DIR}/assets/assets/"
fi

VERSION_CODE=$(grep -oE 'versionCode [0-9]+' "${APP_DIR}/android/app/build.gradle" | awk '{print $2}')
VERSION_NAME=$(grep -oE 'versionName "[^"]+"' "${APP_DIR}/android/app/build.gradle" | cut -d'"' -f2)

if [ -n "${VERSION_CODE}" ] && [ -n "${VERSION_NAME}" ]; then
  echo "==> 🏷️ Syncing version ${VERSION_NAME} (build ${VERSION_CODE}) into AndroidManifest.xml..."
  sed -i 's/android:versionCode="[0-9]*"/android:versionCode="'"${VERSION_CODE}"'"/' "${APP_DIR}/android/AndroidManifest.xml"
  sed -i 's/android:versionName="[^"]*"/android:versionName="'"${VERSION_NAME}"'"/' "${APP_DIR}/android/AndroidManifest.xml"
fi

echo "==> ⚙️ Compiling resources with AAPT2..."
aapt2 compile --dir "${APP_DIR}/android/res" -o "${BUILD_DIR}/res.zip"
aapt2 link -I "${ANDROID_JAR}" --manifest "${APP_DIR}/android/AndroidManifest.xml" --version-code "${VERSION_CODE}" --version-name "${VERSION_NAME}" -o "${BUILD_DIR}/base.apk" -A "${BUILD_DIR}/assets" --java "${BUILD_DIR}/gen" "${BUILD_DIR}/res.zip"

echo "==> ☕ Compiling Java sources..."
javac --release 8 -cp "${ANDROID_JAR}:${BUILD_DIR}/gen" -d "${BUILD_DIR}/classes" "${APP_DIR}/android/src/com/yuvashaktigroup/app/"*.java "${BUILD_DIR}/gen/com/yuvashaktigroup/app/R.java"

echo "==> ⚡ Converting to Dalvik Executable (DEX)..."
if [ -f "${R8_JAR}" ]; then
  java -cp "${R8_JAR}" com.android.tools.r8.D8 --lib "${ANDROID_JAR}" --output "${BUILD_DIR}/classes" "${BUILD_DIR}/classes/com/yuvashaktigroup/app/"*.class
else
  find "${BUILD_DIR}/classes" -name "*.class" | xargs d8 --lib "${ANDROID_JAR}" --output "${BUILD_DIR}/classes/"
fi

echo "==> 📁 Packaging classes.dex..."
cd "${BUILD_DIR}/classes"
zip -u "${BUILD_DIR}/base.apk" classes.dex
cd "${APP_DIR}"

echo "==> ✍️ Signing APK with permanent release.keystore..."
apksigner sign --ks "${KEYSTORE}" --ks-pass "pass:${STORE_PASS}" --ks-key-alias "${ALIAS}" --key-pass "pass:${KEY_PASS}" --out "${APP_DIR}/YuvaShaktiGroup.apk" "${BUILD_DIR}/base.apk"

echo "==> 🚚 Copying signed APK..."
cp "${APP_DIR}/YuvaShaktiGroup.apk" "${APP_DIR}/yuva-shakti.apk"
mkdir -p /sdcard/Download 2>/dev/null || true
cp "${APP_DIR}/YuvaShaktiGroup.apk" /sdcard/Download/YuvaShaktiGroup.apk 2>/dev/null || true

echo "==> ✅ Android APK Successfully Built & Signed!"
ls -lh "${APP_DIR}/YuvaShaktiGroup.apk"
echo "==> 🔍 Verification & Signature Fingerprint:"
apksigner verify --print-certs "${APP_DIR}/YuvaShaktiGroup.apk"
