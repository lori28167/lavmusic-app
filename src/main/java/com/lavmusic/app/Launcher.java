package com.lavmusic.app;

/**
 * Launcher class for the JavaFX application.
 * This class is needed to properly launch JavaFX applications from a fat JAR.
 * It does not extend Application, which avoids issues with the JavaFX runtime
 * when running with java -jar.
 */
public class Launcher {
    public static void main(String[] args) {
        LavMusicApp.main(args);
    }
}
