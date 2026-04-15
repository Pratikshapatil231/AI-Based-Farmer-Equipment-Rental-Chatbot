@echo off
setlocal

echo ---------------------------------------------
echo AI Based Farmer Equipment Rental System
echo ---------------------------------------------

where java >nul 2>nul
if errorlevel 1 (
  echo [ERROR] Java not found. Please install JDK 17 and set PATH.
  pause
  exit /b 1
)

if exist out rmdir /s /q out
mkdir out

echo Compiling Java files...
javac -encoding UTF-8 -d out src\main\java\com\farmbot\model\*.java src\main\java\com\farmbot\service\*.java src\main\java\com\farmbot\ui\*.java src\main\java\com\farmbot\ChatbotApplication.java
if errorlevel 1 (
  echo [ERROR] Compilation failed.
  pause
  exit /b 1
)

echo Starting chatbot...
java -cp out com.farmbot.ChatbotApplication

endlocal
