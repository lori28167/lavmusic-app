package com.lavmusic.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Launcher class
 */
class LauncherTest {
    
    @Test
    void testLauncherClassExists() {
        // Verify that the Launcher class exists and can be instantiated
        assertDoesNotThrow(() -> {
            Class<?> launcherClass = Class.forName("com.lavmusic.app.Launcher");
            assertNotNull(launcherClass);
        });
    }
    
    @Test
    void testLauncherHasMainMethod() {
        // Verify that the Launcher class has a main method with the correct signature
        assertDoesNotThrow(() -> {
            Class<?> launcherClass = Class.forName("com.lavmusic.app.Launcher");
            java.lang.reflect.Method mainMethod = launcherClass.getMethod("main", String[].class);
            assertNotNull(mainMethod);
            assertEquals(void.class, mainMethod.getReturnType());
            assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));
            assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()));
        });
    }
}
