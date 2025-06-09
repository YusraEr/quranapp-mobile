package com.example.quranapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedHashMap;
import java.util.Map;

public class SettingsUtils {

    private static final String PREFS_NAME = "quran_app_settings";
    public static final String KEY_RECITER = "selected_reciter";
    public static final String DEFAULT_RECITER_KEY = "04";

    public static void saveReciterPreference(Context context, String reciterKey) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_RECITER, reciterKey);
        editor.apply();
    }

    public static String getReciterPreference(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_RECITER, DEFAULT_RECITER_KEY);
    }


    public static Map<String, String> getAvailableReciters() {
        Map<String, String> reciters = new LinkedHashMap<>();
        reciters.put("01", "Abdullah Al-Juhany");
        reciters.put("02", "Abdul Muhsin Al-Qasim");
        reciters.put("03", "Abdurrahman as-Sudais");
        reciters.put("04", "Ibrahim Al-Dossari");
        reciters.put("05", "Misyari Rasyid Al-Afasi");
        return reciters;
    }
}
