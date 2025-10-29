# Ticly Lavamusic App

![Build JAR](https://github.com/lori28167/lavmusic-app/workflows/Build%20JAR/badge.svg)

A modern Java music player application featuring a Material Expressive UI design. This application is built to integrate with Lavalink for music streaming and playback.

## Features

- 🎵 **Music Playback**: Play, pause, stop, and skip tracks
- 🔍 **Search Functionality**: Search for music tracks
- 📋 **Queue Management**: Add tracks to queue and manage playback order
- 🎨 **Material Expressive UI**: Clean, modern interface following Material Design principles
- 🔊 **Volume Control**: Adjustable volume slider
- ⏱️ **Track Progress**: Visual progress indicator for currently playing track

## Technology Stack

- **Java 17**: Core programming language
- **JavaFX 21**: Modern UI framework
- **Gradle 8.5**: Build tool and dependency management
- **Lavalink**: Music streaming backend (configured for integration)
- **Logback**: Logging framework

## Prerequisites

- Java 17 or higher
- Gradle 8.5 or higher (included via Gradle Wrapper)

## Project Structure

```
lavmusic-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/lavmusic/app/
│   │   │       ├── LavMusicApp.java          # Main application class
│   │   │       ├── config/
│   │   │       │   └── ConfigManager.java     # Configuration handler
│   │   │       ├── model/
│   │   │       │   └── Track.java             # Track data model
│   │   │       ├── player/
│   │   │       │   └── MusicPlayerManager.java # Music player logic
│   │   │       └── ui/
│   │   │           └── MainUI.java            # User interface
│   │   └── resources/
│   │       ├── config.json                    # Application configuration
│   │       └── logback.xml                    # Logging configuration
│   └── test/
│       └── java/                              # Test files
├── build.gradle                                # Build configuration
├── settings.gradle                             # Gradle settings
└── README.md                                   # This file
```

## Building the Application

### Using Gradle Wrapper (Recommended)

```bash
# Build the project
./gradlew build

# Run the application
./gradlew run

# Create distribution archives
./gradlew distZip distTar
```

### Building JAR

```bash
# Create a fat JAR with all dependencies
./gradlew jar

# The JAR will be located at: build/libs/lavmusic-app-1.0.0.jar
```

## Continuous Integration

The project includes GitHub Actions workflows for automated builds and releases.

### Build Workflow

The "Build JAR" workflow automatically builds the JAR file on every push to `main` or `develop` branches, and on pull requests.

The workflow:
- Sets up Java 17 (Temurin distribution)
- Builds the project using Gradle
- Runs all tests
- Uploads the JAR artifact (available for 30 days)
- Uploads distribution archives (ZIP/TAR)

You can download the built artifacts from the Actions tab in the GitHub repository after each successful build.

To manually trigger a build, go to the Actions tab and run the "Build JAR" workflow using the "Run workflow" button.

### Release Workflow

The "Release JAR" workflow automatically creates a GitHub release when a version tag is pushed.

To create a new release:
```bash
# Create and push a version tag
git tag v1.0.0
git push origin v1.0.0
```

The workflow will:
- Build the JAR file
- Create a GitHub release with the tag name
- Upload the JAR file to the release
- Upload distribution archives (ZIP/TAR)
- Generate release notes automatically

You can also manually trigger the release workflow from the Actions tab.

## Running the Application

### Method 1: Using Gradle

```bash
./gradlew run
```

### Method 2: Using the JAR

```bash
java -jar build/libs/lavmusic-app-1.0.0.jar
```

## Configuration

The application configuration is located at `src/main/resources/config.json`:

```json
{
  "lavalink": {
    "host": "localhost",
    "port": 2333,
    "password": "youshallnotpass"
  },
  "player": {
    "defaultVolume": 50,
    "bufferDuration": 400
  }
}
```

### Configuration Options

- **lavalink.host**: Lavalink server hostname
- **lavalink.port**: Lavalink server port
- **lavalink.password**: Lavalink server password (**WARNING**: Change this default password before production use!)
- **player.defaultVolume**: Default volume level (0-100)
- **player.bufferDuration**: Audio buffer duration in milliseconds

> ⚠️ **Security Note**: The default password in the configuration is for development only. Always change it to a secure password before deploying to production, or use environment variables for sensitive credentials.

## Setting up Lavalink Server

To use this application with actual music streaming, you need to run a Lavalink server:

1. Download Lavalink from [GitHub](https://github.com/freyacodes/Lavalink/releases)
2. Create `application.yml` configuration
3. Run Lavalink: `java -jar Lavalink.jar`
4. Update `config.json` with your Lavalink server details

## UI Features

### Main Window Components

1. **App Bar**: Purple header with application title
2. **Search Panel** (Left):
   - Search input field
   - Search button
   - Results list with "+" button to add to queue
3. **Queue Panel** (Right):
   - Current queue display
   - Clear queue button (🗑)
4. **Player Controls** (Bottom):
   - Current track information
   - Progress slider
   - Play/Pause/Next buttons
   - Volume control

### Material Expressive Design

The UI follows Material Design principles with:
- Elevated surfaces with subtle shadows
- Rounded corners on components
- Primary color: Purple (#6200EE)
- Secondary color: Teal (#03DAC6)
- Clean typography and spacing
- Smooth hover interactions

## Development

### Running Tests

```bash
./gradlew test
```

### Cleaning Build Artifacts

```bash
./gradlew clean
```

### IDE Setup

The project can be imported into any Java IDE that supports Gradle:
- IntelliJ IDEA: Open the project folder
- Eclipse: Import as Gradle project
- VS Code: Open with Gradle plugin

## Troubleshooting

### Application won't start
- Ensure Java 17 or higher is installed: `java -version`
- Check if JavaFX is properly configured
- Review logs in `lavmusic.log`

### UI rendering issues
- Update your graphics drivers
- Try running with different JavaFX rendering pipelines:
  ```bash
  java -Dprism.order=sw -jar build/libs/lavmusic-app-1.0.0.jar
  ```

### Lavalink connection issues
- Verify Lavalink server is running
- Check host, port, and password in `config.json`
- Review application logs for connection errors

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues.

## License

This project is open source and available for educational and commercial use.

## Future Enhancements

- [ ] Full Lavalink integration for real music streaming
- [ ] Playlist management
- [ ] Search from multiple sources (YouTube, SoundCloud, etc.)
- [ ] Audio visualization
- [ ] Equalizer controls
- [ ] Keyboard shortcuts
- [ ] Themes and customization options
- [ ] Recently played tracks
- [ ] Favorites/liked songs

## Acknowledgments

- Lavalink for audio streaming capabilities
- JavaFX community for the UI framework
- Material Design for design guidelines
