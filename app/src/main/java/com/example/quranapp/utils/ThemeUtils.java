package com.example.quranapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeUtils {

    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME = "app_theme";
    private static final int LIGHT_THEME = AppCompatDelegate.MODE_NIGHT_NO;
    private static final int DARK_THEME = AppCompatDelegate.MODE_NIGHT_YES;
    private static final int DEFAULT_THEME = LIGHT_THEME;

    public static void applyTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int selectedTheme = prefs.getInt(KEY_THEME, DEFAULT_THEME);
        AppCompatDelegate.setDefaultNightMode(selectedTheme);
    }

    public static boolean isDarkTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME, DEFAULT_THEME) == DARK_THEME;
    }

    public static void setLightTheme(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_THEME, LIGHT_THEME);
        editor.apply();
        AppCompatDelegate.setDefaultNightMode(LIGHT_THEME);
    }

    public static void setDarkTheme(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_THEME, DARK_THEME);
        editor.apply();
        AppCompatDelegate.setDefaultNightMode(DARK_THEME);
    }
}
