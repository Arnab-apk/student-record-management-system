@echo off
setlocal
cd /d "%~dp0"
call build.bat
if errorlevel 1 exit /b 1
javac --release 17 -encoding UTF-8 -cp build -sourcepath src\main\java -d build src\test\java\com\academy\studentmanager\PreviewRenderer.java
if errorlevel 1 exit /b 1
java -Djava.awt.headless=true -cp build com.academy.studentmanager.PreviewRenderer
