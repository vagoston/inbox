#!/bin/bash

# PhotoBox Project Validation Script
# This script validates the project structure and key files

echo "🎯 PhotoBox Android Project Validation"
echo "======================================="

# Check project structure
echo
echo "📁 Checking project structure..."

required_files=(
    "build.gradle.kts"
    "settings.gradle.kts"
    "app/build.gradle.kts"
    "app/src/main/AndroidManifest.xml"
    "app/src/main/java/com/inbox/photobox/MainActivity.kt"
    "app/src/main/java/com/inbox/photobox/PhotoBoxApp.kt"
    "app/src/main/java/com/inbox/photobox/PhotoBoxViewModel.kt"
    "app/src/main/res/values/strings.xml"
)

missing_files=0
for file in "${required_files[@]}"; do
    if [ -f "$file" ]; then
        echo "✅ $file"
    else
        echo "❌ $file (missing)"
        ((missing_files++))
    fi
done

echo
echo "📊 Project Structure Summary:"
echo "   - Total files checked: ${#required_files[@]}"
echo "   - Missing files: $missing_files"

# Check for key features in source code
echo
echo "🔍 Checking key features..."

features=(
    "GoogleSignIn:app/src/main/java/com/inbox/photobox/PhotoBoxViewModel.kt"
    "Camera permissions:app/src/main/AndroidManifest.xml"
    "Drive API:app/src/main/java/com/inbox/photobox/PhotoBoxViewModel.kt"
    "Compose UI:app/src/main/java/com/inbox/photobox/PhotoBoxApp.kt"
)

for feature_check in "${features[@]}"; do
    feature=$(echo "$feature_check" | cut -d: -f1)
    file=$(echo "$feature_check" | cut -d: -f2)
    
    case $feature in
        "GoogleSignIn")
            if grep -q "GoogleSignIn" "$file" 2>/dev/null; then
                echo "✅ Google Sign-In integration"
            else
                echo "❌ Google Sign-In integration"
            fi
            ;;
        "Camera permissions")
            if grep -q "android.permission.CAMERA" "$file" 2>/dev/null; then
                echo "✅ Camera permissions"
            else
                echo "❌ Camera permissions"
            fi
            ;;
        "Drive API")
            if grep -q "google.api.services.drive" "$file" 2>/dev/null; then
                echo "✅ Google Drive API integration"
            else
                echo "❌ Google Drive API integration"
            fi
            ;;
        "Compose UI")
            if grep -q "@Composable" "$file" 2>/dev/null; then
                echo "✅ Jetpack Compose UI"
            else
                echo "❌ Jetpack Compose UI"
            fi
            ;;
    esac
done

# Check gradle wrapper
echo
echo "🔧 Checking build system..."
if [ -f "gradlew" ] && [ -x "gradlew" ]; then
    echo "✅ Gradle wrapper (executable)"
else
    echo "❌ Gradle wrapper (not executable or missing)"
fi

if [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "✅ Gradle wrapper JAR"
else
    echo "❌ Gradle wrapper JAR"
fi

# Check documentation
echo
echo "📚 Checking documentation..."
if [ -f "README.md" ]; then
    if grep -q "PhotoBox" "README.md" 2>/dev/null; then
        echo "✅ README.md (with PhotoBox content)"
    else
        echo "⚠️  README.md (exists but may need PhotoBox content)"
    fi
else
    echo "❌ README.md"
fi

if [ -f "SETUP.md" ]; then
    echo "✅ SETUP.md (Google API setup guide)"
else
    echo "❌ SETUP.md (Google API setup guide)"
fi

echo
echo "🎉 Validation Complete!"
echo
echo "To build this project:"
echo "1. Install Android Studio"
echo "2. Follow SETUP.md for Google API configuration"  
echo "3. Open project in Android Studio"
echo "4. Sync Gradle files"
echo "5. Build and run on device/emulator"
echo
echo "For more information, see README.md"