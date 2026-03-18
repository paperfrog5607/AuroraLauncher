@echo off
echo Starting jpackage... > package.log 2>&1
"C:\Program Files\Java\jdk-17\bin\jpackage.exe" ^
  --name AuroraLauncher ^
  --app-version 1.0.0 ^
  --input ui\build\package-input ^
  --main-jar aurora-launcher-1.0.0.jar ^
  --main-class org.aurora.launcher.ui.AuroraApplication ^
  --type app-image ^
  --dest ui\build\distribute ^
  --description "A modern Minecraft launcher" ^
  --vendor "Aurora Team" ^
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.media,javafx.web,java.logging,java.sql,java.naming,java.security.jgss,jdk.crypto.ec ^
  --java-options "-Djava.net.preferIPv4Stack=true" ^
  --java-options "--add-opens=java.base/java.time=ALL-UNNAMED" ^
  --java-options "--add-opens=java.base/java.lang=ALL-UNNAMED" ^
  --module-path javafx-jmods\javafx-jmods-17.0.8 ^
  --win-console >> package.log 2>&1
echo Done. Exit code: %ERRORLEVEL% >> package.log 2>&1