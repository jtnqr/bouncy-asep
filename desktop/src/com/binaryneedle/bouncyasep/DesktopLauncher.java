package com.binaryneedle.bouncyasep;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1024, 768);
        config.setForegroundFPS(60);
        config.useVsync(false);
        config.setResizable(false);
        config.setTitle("Bouncy Asep: The Game");
//        config.setWindowIcon("icons/logo.jpg");
        new Lwjgl3Application(new BouncyAsep(), config);
    }
}
