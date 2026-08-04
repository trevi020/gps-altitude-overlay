#!/bin/bash
# Setup Android SDK command-line in GitHub Codespaces
# Esegui questo script UNA VOLTA dentro il tuo Codespace

set -e

echo "📦 Installo Java 17..."
sudo apt-get update -qq
sudo apt-get install -y openjdk-17-jdk unzip

echo "📥 Scarico Android command-line tools..."
mkdir -p ~/android-sdk/cmdline-tools
cd ~/android-sdk/cmdline-tools
curl -o cmdtools.zip https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
unzip -q cmdtools.zip
mv cmdline-tools latest
rm cmdtools.zip

echo "🔧 Configuro variabili d'ambiente..."
export ANDROID_HOME=~/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# Rendi permanenti le variabili
echo "export ANDROID_HOME=~/android-sdk" >> ~/.bashrc
echo "export PATH=\$PATH:\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools" >> ~/.bashrc

echo "✅ Accetto licenze SDK..."
yes | sdkmanager --licenses > /dev/null 2>&1 || true

echo "⬇️ Installo componenti SDK (platform 34, build-tools, platform-tools)..."
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0" > /dev/null

echo "📥 Installo Gradle temporaneo per generare il wrapper..."
sudo apt-get install -y gradle > /dev/null 2>&1 || {
  curl -sL -o /tmp/gradle.zip https://services.gradle.org/distributions/gradle-8.5-bin.zip
  sudo unzip -q /tmp/gradle.zip -d /opt
  export PATH=$PATH:/opt/gradle-8.5/bin
}

echo "🔧 Genero il Gradle Wrapper nel progetto..."
gradle wrapper --gradle-version 8.5

echo "✅ Setup completato!"
echo "Riavvia il terminale o esegui: source ~/.bashrc"
echo "Ora puoi compilare con: ./gradlew assembleDebug"
