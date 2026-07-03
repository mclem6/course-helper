package com.coursehelper.frontend.theme;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Parent;


public class ThemeManager {

    private static final String LOCAL_THEME_FILE = System.getProperty("user.home") + "/.coursehelper/theme.txt";
    private static final StringProperty currentTheme = new SimpleStringProperty("LightMode");
    private static Long currentUserId;

    private static final String[] SHARED_STYLESHEETS = {
        "mainLayout.css",
        "navigation.css",
        "settingsPage.css",
        "sideCalendar.css"
    };

    public static void initializeGuest() {
        currentUserId = (long) -1;
        loadLocalThemePreference();
    }

    public static void initialize(Long userId) {
        currentUserId = userId;
    }

    public static void loadLocalThemePreference() {
        try {
            if (Files.exists(Paths.get(LOCAL_THEME_FILE))) {
                String theme = new String(Files.readAllBytes(Paths.get(LOCAL_THEME_FILE))).trim();
                currentTheme.set(theme);
            }
        } catch (IOException e) {
            // file may not exist on first run
        }
    }

    public static void saveLocalThemePreference(String theme) {
        try {
            Files.createDirectories(Paths.get(System.getProperty("user.home"), ".coursehelper"));
            Files.write(Paths.get(LOCAL_THEME_FILE), theme.getBytes());
            currentTheme.set(theme);
        } catch (Exception e) {
            // silently ignore — preference write failure is non-critical
        }
    }

    public static void saveThemePreference(String theme) {
        saveLocalThemePreference(theme);
    }

    public static void setTheme(Parent parent, String theme) {
        if (parent == null || theme == null) return;
        currentTheme.set(theme);
        parent.getStylesheets().clear();

        String themePath = getSharedStyleSheet(themeFileName(theme));
        if (themePath != null) parent.getStylesheets().add(themePath);

        for (String ss : SHARED_STYLESHEETS) {
            String path = getSharedStyleSheet(ss);
            if (path != null) parent.getStylesheets().add(path);
        }
    }

    public static void setPopupTheme(Parent parent) {
        parent.getStylesheets().clear();

        // Popup content has no parent in the main scene, so add the root style class
        // directly so JavaFX can resolve the color tokens defined on .root
        if (!parent.getStyleClass().contains("root")) {
            parent.getStyleClass().add("root");
        }

        String themePath = getSharedStyleSheet(themeFileName(currentTheme.get()));
        if (themePath != null) parent.getStylesheets().add(themePath);

        String formPath = getSharedStyleSheet("form.css");
        if (formPath != null) parent.getStylesheets().add(formPath);
    }

    public static void setAccessScreenTheme(Parent parent, String theme) {
        currentTheme.set(theme);
        parent.getStylesheets().clear();

        String themePath = getSharedStyleSheet(themeFileName(theme));
        if (themePath != null) parent.getStylesheets().add(themePath);

        String accessPath = getSharedStyleSheet("accessPage.css");
        if (accessPath != null) parent.getStylesheets().add(accessPath);

        parent.getStyleClass().removeIf(c -> c.equals("access-bg-light") || c.equals("access-bg-dark"));
        parent.getStyleClass().add(theme.equals("LightMode") ? "access-bg-light" : "access-bg-dark");
    }

    private static String getSharedStyleSheet(String name) {
        try {
            return ThemeManager.class.getResource("/stylesheets/" + name).toExternalForm();
        } catch (Exception e) {
            return null;
        }
    }

    private static String themeFileName(String theme) {
        return theme.equals("LightMode") ? "theme-light.css" : "theme-dark.css";
    }

    public static String toggleTheme(Parent parent) {
        String newTheme = currentTheme.get().equals("LightMode") ? "DarkMode" : "LightMode";
        setTheme(parent, newTheme);
        return newTheme;
    }

    public static String getCurrentTheme() {
        return currentTheme.get();
    }

    public static StringProperty currentThemeProperty() {
        return currentTheme;
    }

    public static void setCurrentTheme(String newTheme) {
        currentTheme.set(newTheme);
    }

    public static boolean isDarkMode() {
        return currentTheme.get().equals("DarkMode");
    }
}
