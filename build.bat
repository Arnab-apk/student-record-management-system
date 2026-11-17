@echo off
setlocal
cd /d "%~dp0"
if not exist build mkdir build
javac --release 17 -encoding UTF-8 -d build -sourcepath src\main\java src\main\java\com\academy\studentmanager\StudentManagementApp.java
if errorlevel 1 exit /b 1
echo Build complete. Classes are in the build folder.
