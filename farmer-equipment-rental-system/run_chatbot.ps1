$ErrorActionPreference = "Stop"

Write-Host "---------------------------------------------"
Write-Host "AI Based Farmer Equipment Rental System"
Write-Host "---------------------------------------------"

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "[ERROR] Java not found. Install JDK 17 and set PATH."
    exit 1
}

if (Test-Path "out") {
    Remove-Item -Recurse -Force "out"
}
New-Item -ItemType Directory -Path "out" | Out-Null

Write-Host "Compiling Java files..."
javac -encoding UTF-8 -d out src/main/java/com/farmbot/model/*.java src/main/java/com/farmbot/service/*.java src/main/java/com/farmbot/ui/*.java src/main/java/com/farmbot/ChatbotApplication.java

Write-Host "Starting chatbot..."
java -cp out com.farmbot.ChatbotApplication
