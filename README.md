# Aurora Launcher

A modern Minecraft launcher built with JavaFX.

## Features

- **Modular Architecture** - 13 business modules for clean separation
- **Modern UI** - PCL-style tab navigation with card layout
- **Multi-Platform Support** - Windows, macOS, Linux
- **Mirror Support** - BMCLAPI mirror for Chinese users
- **Resource Search** - Mod/Modpack/Resource Pack/Shader search via Modrinth API
- **WebP Support** - Full WebP image format support
- **Caching System** - Memory + Disk caching for images
- **Chinese Localization** - Full Chinese language support

## Tech Stack

- Java 17
- JavaFX 17
- Gradle 8.5
- OkHttp
- Gson
- TwelveMonkeys ImageIO (WebP support)
- SLF4J + Logback

## Project Structure

```
AuroraLauncher/
├── api/          # API clients (Modrinth, Mojang)
├── core/         # Core utilities, networking, events
├── ui/           # JavaFX UI components and controllers
├── download/     # Download engine
├── modpack/      # Modpack management
├── mod/          # Mod management
├── resource/     # Resource pack management
├── account/      # Account management
├── launcher/     # Game launcher
├── config/       # Configuration
├── diagnostic/   # Diagnostic tools
├── ai/           # AI features
└── dev/          # Development tools
```

## Build Requirements

- JDK 17 or later
- Gradle 8.5+

## Setup

1. Clone the repository:
```bash
git clone https://github.com/paperfrog5607/AuroraLauncher.git
cd AuroraLauncher
```

2. Download JavaFX jmods (for packaging):
```bash
# Windows
curl -L -o javafx-jmods.zip https://download2.gluonhq.com/openjfx/17.0.8/openjfx-17.0.8_windows-x64_bin-jmods.zip
unzip javafx-jmods.zip
mv javafx-jmods-17.0.8 javafx-jmods/javafx-jmods-17.0.8
```

3. Build:
```bash
./gradlew build
```

4. Run:
```bash
./gradlew :ui:run
```

5. Package (portable):
```bash
./gradlew :ui:packagePortable
```

## License

MIT License

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.