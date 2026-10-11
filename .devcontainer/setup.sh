#!/usr/bin/env bash
# Prepara l'Android SDK per Fastigum in un Codespace nuovo. Sicuro da rilanciare.
set -euo pipefail

SDK="$HOME/android-sdk"
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"
SDKMANAGER="$SDK/cmdline-tools/latest/bin/sdkmanager"

# Adegua questi pacchetti a compileSdk / build-tools del progetto
SDK_PACKAGES=("platform-tools" "platforms;android-34" "build-tools;34.0.0")

if [ ! -x "$SDKMANAGER" ]; then
  echo "Scarico Android command-line tools..."
  mkdir -p "$SDK/cmdline-tools"
  tmp="$(mktemp -d)"
  curl -fsSL -o "$tmp/cmdtools.zip" "$TOOLS_URL"
  unzip -q "$tmp/cmdtools.zip" -d "$tmp"
  rm -rf "$SDK/cmdline-tools/latest"
  mv "$tmp/cmdline-tools" "$SDK/cmdline-tools/latest"
  rm -rf "$tmp"
fi

echo "Accetto le licenze SDK..."
yes | "$SDKMANAGER" --licenses > /dev/null || true

echo "Installo componenti SDK: ${SDK_PACKAGES[*]}"
"$SDKMANAGER" "${SDK_PACKAGES[@]}" > /dev/null

if ! grep -q "ANDROID_HOME" "$HOME/.bashrc"; then
  {
    echo "export ANDROID_HOME=$SDK"
    echo 'export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools'
  } >> "$HOME/.bashrc"
fi

echo "sdk.dir=$SDK" > "$PROJECT_DIR/local.properties"

if [ -f "$PROJECT_DIR/gradlew" ]; then
  chmod +x "$PROJECT_DIR/gradlew"
else
  echo "ATTENZIONE: gradlew non trovato nel progetto, il Gradle Wrapper va rigenerato."
fi

echo "Setup Android SDK completato in $SDK"
