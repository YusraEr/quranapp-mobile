package com.example.quranapp.data.local; // Ganti dengan package name aplikasi Anda

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.quranapp.data.remote.model.AyatSearchResult;
import com.example.quranapp.data.remote.model.SuratNavInfo;
import com.example.quranapp.data.remote.model.Tafsir;
import com.google.gson.Gson;

import com.example.quranapp.data.remote.model.Ayat; // Model Ayat POJO
import com.example.quranapp.data.remote.model.Surah; // Model Surah POJO
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuranDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "QuranDbHelper";

    private static final String DATABASE_NAME = "quran_app.db";
    private static final int DATABASE_VERSION = 5;

    // Nama Tabel
    public static final String TABLE_SURAH = "surahs";
    public static final String TABLE_AYAT = "ayats";
    public static final String TABLE_TAFSIR = "tafsirs";

    // Kolom Tabel Surah
    public static final String COLUMN_SURAH_NOMOR = "nomor";
    public static final String COLUMN_SURAH_NAMA_ARAB = "nama_arab";
    public static final String COLUMN_SURAH_NAMA_LATIN = "nama_latin";
    public static final String COLUMN_SURAH_JUMLAH_AYAT = "jumlah_ayat";
    public static final String COLUMN_SURAH_TEMPAT_TURUN = "tempat_turun";
    public static final String COLUMN_SURAH_ARTI = "arti";
    public static final String COLUMN_SURAH_DESKRIPSI = "deskripsi";

    public static final String COLUMN_SURAH_NEXT_NUMBER = "next_surah_number";
    public static final String COLUMN_SURAH_NEXT_NAME = "next_surah_name";
    public static final String COLUMN_SURAH_PREV_NUMBER = "prev_surah_number";
    public static final String COLUMN_SURAH_PREV_NAME = "prev_surah_name";

    // Kolom Tabel Ayat
    public static final String COLUMN_AYAT_ID = "_id";
    public static final String COLUMN_AYAT_SURAH_NOMOR = "surah_nomor";
    public static final String COLUMN_AYAT_NOMOR_AYAT = "nomor_ayat";
    public static final String COLUMN_AYAT_TEKS_ARAB = "teks_arab";
    public static final String COLUMN_AYAT_TEKS_LATIN = "teks_latin";
    public static final String COLUMN_AYAT_TEKS_INDONESIA = "teks_indonesia";
    public static final String COLUMN_AYAT_AUDIO_JSON = "audio_json";
    public static final String COLUMN_TAFSIR_ID = "_id";
    public static final String COLUMN_TAFSIR_SURAH_NOMOR = "surah_nomor";
    public static final String COLUMN_TAFSIR_AYAT_NOMOR = "ayat_nomor";
    public static final String COLUMN_TAFSIR_TEKS = "teks";
    private final Gson gson = new Gson();

    private static final String SQL_CREATE_TABLE_SURAH =
            "CREATE TABLE " + TABLE_SURAH + " (" +
                    COLUMN_SURAH_NOMOR + " INTEGER PRIMARY KEY," +
                    COLUMN_SURAH_NAMA_ARAB + " TEXT," +
                    COLUMN_SURAH_NAMA_LATIN + " TEXT," +
                    COLUMN_SURAH_JUMLAH_AYAT + " INTEGER," +
                    COLUMN_SURAH_TEMPAT_TURUN + " TEXT," +
                    COLUMN_SURAH_ARTI + " TEXT," +
                    COLUMN_SURAH_DESKRIPSI + " TEXT," +
                    COLUMN_SURAH_NEXT_NUMBER + " INTEGER," +
                    COLUMN_SURAH_NEXT_NAME + " TEXT," +
                    COLUMN_SURAH_PREV_NUMBER + " INTEGER," +
                    COLUMN_SURAH_PREV_NAME + " TEXT)";

    // Perintah SQL untuk Membuat Tabel Ayat
    private static final String SQL_CREATE_TABLE_AYAT =
            "CREATE TABLE " + TABLE_AYAT + " (" +
                    COLUMN_AYAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_AYAT_SURAH_NOMOR + " INTEGER," +
                    COLUMN_AYAT_NOMOR_AYAT + " INTEGER," +
                    COLUMN_AYAT_TEKS_ARAB + " TEXT," +
                    COLUMN_AYAT_TEKS_LATIN + " TEXT," +
                    COLUMN_AYAT_TEKS_INDONESIA + " TEXT," +
                    COLUMN_AYAT_AUDIO_JSON + " TEXT," +
                    "FOREIGN KEY(" + COLUMN_AYAT_SURAH_NOMOR + ") REFERENCES " +
                    TABLE_SURAH + "(" + COLUMN_SURAH_NOMOR + ") ON DELETE CASCADE)";

    private static final String SQL_CREATE_TABLE_TAFSIR =
            "CREATE TABLE " + TABLE_TAFSIR + " (" +
                    COLUMN_TAFSIR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_TAFSIR_SURAH_NOMOR + " INTEGER," +
                    COLUMN_TAFSIR_AYAT_NOMOR + " INTEGER," +
                    COLUMN_TAFSIR_TEKS + " TEXT," +
                    "FOREIGN KEY(" + COLUMN_TAFSIR_SURAH_NOMOR + ") REFERENCES " +
                    TABLE_SURAH + "(" + COLUMN_SURAH_NOMOR + ") ON DELETE CASCADE)";

    public QuranDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "Creating database tables...");
            db.execSQL(SQL_CREATE_TABLE_SURAH);
            db.execSQL(SQL_CREATE_TABLE_AYAT);
            db.execSQL(SQL_CREATE_TABLE_TAFSIR);
            Log.d(TAG, "Database tables created.");
        } catch (SQLException e) {
            Log.e(TAG, "Error creating tables: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion);
        if (oldVersion < 2 && newVersion >= 2) {
            try {
                Log.d(TAG, "Creating table Ayat as part of upgrade from v" + oldVersion + " to v" + newVersion);
                db.execSQL(SQL_CREATE_TABLE_AYAT);
                db.execSQL(SQL_CREATE_TABLE_TAFSIR);
            } catch (SQLException e) {
                Log.e(TAG, "Error creating table Ayat during upgrade: " + e.getMessage());
            }
        }
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Downgrading database from version " + oldVersion + " to " + newVersion);
    }

    public void addOrReplaceSurahs(List<Surah> surahList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Surah surah : surahList) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_SURAH_NOMOR, surah.getNomor());
                values.put(COLUMN_SURAH_NAMA_ARAB, surah.getNama());
                values.put(COLUMN_SURAH_NAMA_LATIN, surah.getNamaLatin());
                values.put(COLUMN_SURAH_JUMLAH_AYAT, surah.getJumlahAyat());
                values.put(COLUMN_SURAH_TEMPAT_TURUN, surah.getTempatTurun());
                values.put(COLUMN_SURAH_ARTI, surah.getArti());
                values.put(COLUMN_SURAH_DESKRIPSI, surah.getDeskripsi());

                if (surah.getSuratSelanjutnya() != null) {
                    values.put(COLUMN_SURAH_NEXT_NUMBER, surah.getSuratSelanjutnya().getNomor());
                    values.put(COLUMN_SURAH_NEXT_NAME, surah.getSuratSelanjutnya().getNamaLatin());
                }

                if (surah.getSuratSebelumnya() != null) {
                    values.put(COLUMN_SURAH_PREV_NUMBER, surah.getSuratSebelumnya().getNomor());
                    values.put(COLUMN_SURAH_PREV_NAME, surah.getSuratSebelumnya().getNamaLatin());
                }

                db.insertWithOnConflict(TABLE_SURAH, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
            Log.d(TAG, "Successfully added or replaced " + surahList.size() + " surahs");
        } catch (SQLException e) {
            Log.e(TAG, "Error adding or replacing surahs: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public Surah getSurahByNomor(int nomorSurah) {
        SQLiteDatabase db = this.getReadableDatabase();
        Surah surah = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SURAH, null, COLUMN_SURAH_NOMOR + "=?",
                    new String[]{String.valueOf(nomorSurah)}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                surah = cursorToSurah(cursor);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting surah by nomor " + nomorSurah + ": " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }
        return surah;
    }

    public List<Surah> getAllSurahs() {
        List<Surah> surahList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String orderBy = COLUMN_SURAH_NOMOR + " ASC";
        try {
            cursor = db.query(TABLE_SURAH, null, null, null, null, null, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    surahList.add(cursorToSurah(cursor));
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting all surahs: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }
        return surahList;
    }

    public void addOrReplaceTafsirForSurah(int surahNumber, List<Tafsir> tafsirList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_TAFSIR, COLUMN_TAFSIR_SURAH_NOMOR + " = ?", new String[]{String.valueOf(surahNumber)});

            for (Tafsir tafsir : tafsirList) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_TAFSIR_SURAH_NOMOR, surahNumber);
                values.put(COLUMN_TAFSIR_AYAT_NOMOR, tafsir.getAyat());
                values.put(COLUMN_TAFSIR_TEKS, tafsir.getTeks());
                db.insert(TABLE_TAFSIR, null, values);
            }
            db.setTransactionSuccessful();
            Log.d(TAG, "Successfully added or replaced " + tafsirList.size() + " tafsirs for surah " + surahNumber);
        } catch (SQLException e) {
            Log.e(TAG, "Error adding or replacing tafsirs for surah " + surahNumber, e);
        } finally {
            db.endTransaction();
        }
    }
    public List<Tafsir> getTafsirBySurahNumber(int surahNumber) {
        List<Tafsir> tafsirList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String orderBy = COLUMN_TAFSIR_AYAT_NOMOR + " ASC";

        try {
            cursor = db.query(TABLE_TAFSIR, null,
                    COLUMN_TAFSIR_SURAH_NOMOR + " = ?",
                    new String[]{String.valueOf(surahNumber)},
                    null, null, orderBy);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Tafsir tafsir = new Tafsir();
                    tafsir.setAyat(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TAFSIR_AYAT_NOMOR)));
                    tafsir.setTeks(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAFSIR_TEKS)));
                    tafsirList.add(tafsir);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting tafsir for surah " + surahNumber, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tafsirList;
    }

    private Surah cursorToSurah(Cursor cursor) {
        int nomorIdx = cursor.getColumnIndex(COLUMN_SURAH_NOMOR);
        int namaArabIdx = cursor.getColumnIndex(COLUMN_SURAH_NAMA_ARAB);
        int namaLatinIdx = cursor.getColumnIndex(COLUMN_SURAH_NAMA_LATIN);
        int jumlahAyatIdx = cursor.getColumnIndex(COLUMN_SURAH_JUMLAH_AYAT);
        int tempatTurunIdx = cursor.getColumnIndex(COLUMN_SURAH_TEMPAT_TURUN);
        int artiIdx = cursor.getColumnIndex(COLUMN_SURAH_ARTI);
        int deskripsiIdx = cursor.getColumnIndex(COLUMN_SURAH_DESKRIPSI);

        int nextSurahNumberIdx = cursor.getColumnIndex(COLUMN_SURAH_NEXT_NUMBER);
        int nextSurahNameIdx = cursor.getColumnIndex(COLUMN_SURAH_NEXT_NAME);
        int prevSurahNumberIdx = cursor.getColumnIndex(COLUMN_SURAH_PREV_NUMBER);
        int prevSurahNameIdx = cursor.getColumnIndex(COLUMN_SURAH_PREV_NAME);

        Surah surah = new Surah();
        if (nomorIdx != -1) surah.setNomor(cursor.getInt(nomorIdx));
        if (namaArabIdx != -1) surah.setNama(cursor.getString(namaArabIdx));
        if (namaLatinIdx != -1) surah.setNamaLatin(cursor.getString(namaLatinIdx));
        if (jumlahAyatIdx != -1) surah.setJumlahAyat(cursor.getInt(jumlahAyatIdx));
        if (tempatTurunIdx != -1) surah.setTempatTurun(cursor.getString(tempatTurunIdx));
        if (artiIdx != -1) surah.setArti(cursor.getString(artiIdx));
        if (deskripsiIdx != -1) surah.setDeskripsi(cursor.getString(deskripsiIdx));

        if (nextSurahNumberIdx != -1 && nextSurahNameIdx != -1 &&
                !cursor.isNull(nextSurahNumberIdx) && !cursor.isNull(nextSurahNameIdx)) {

            int nextSurahNumber = cursor.getInt(nextSurahNumberIdx);
            String nextSurahName = cursor.getString(nextSurahNameIdx);

            if (nextSurahNumber > 0 && nextSurahName != null && !nextSurahName.isEmpty()) {
                SuratNavInfo nextSurah = new SuratNavInfo();
                nextSurah.setNomor(nextSurahNumber);
                nextSurah.setNamaLatin(nextSurahName);
                surah.setSuratSelanjutnya(nextSurah);
            }
        }

        if (prevSurahNumberIdx != -1 && prevSurahNameIdx != -1 &&
                !cursor.isNull(prevSurahNumberIdx) && !cursor.isNull(prevSurahNameIdx)) {

            int prevSurahNumber = cursor.getInt(prevSurahNumberIdx);
            String prevSurahName = cursor.getString(prevSurahNameIdx);

            if (prevSurahNumber > 0 && prevSurahName != null && !prevSurahName.isEmpty()) {
                SuratNavInfo prevSurah = new SuratNavInfo();
                prevSurah.setNomor(prevSurahNumber);
                prevSurah.setNamaLatin(prevSurahName);
                surah.setSuratSebelumnya(prevSurah);
            }
        }

        return surah;
    }

    public void addOrReplaceAyatsForSurah(int nomorSurah, List<Ayat> ayatList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Hapus ayat lama untuk surah ini
            db.delete(TABLE_AYAT, COLUMN_AYAT_SURAH_NOMOR + "=?", new String[]{String.valueOf(nomorSurah)});
            Log.d(TAG, "Old ayats deleted for surah: " + nomorSurah);

            // Tambahkan ayat baru
            for (Ayat ayat : ayatList) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_AYAT_SURAH_NOMOR, nomorSurah);
                values.put(COLUMN_AYAT_NOMOR_AYAT, ayat.getNomorAyat());
                values.put(COLUMN_AYAT_TEKS_ARAB, ayat.getTeksArab());
                values.put(COLUMN_AYAT_TEKS_LATIN, ayat.getTeksLatin());
                values.put(COLUMN_AYAT_TEKS_INDONESIA, ayat.getTeksIndonesia());
                values.put(COLUMN_AYAT_AUDIO_JSON, ayat.getAudio() != null ? convertMapToJsonString(ayat.getAudio()) : null);

                db.insert(TABLE_AYAT, null, values);
            }
            db.setTransactionSuccessful();
            Log.d(TAG, ayatList.size() + " ayats inserted for surah: " + nomorSurah);
        } catch (SQLException e) {
            Log.e(TAG, "Error inserting batch ayats for surah " + nomorSurah + ": " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public List<Ayat> getAyatsBySurahNumber(int nomorSurah) {
        List<Ayat> ayatList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String orderBy = COLUMN_AYAT_NOMOR_AYAT + " ASC";

        try {
            cursor = db.query(TABLE_AYAT, null,
                    COLUMN_AYAT_SURAH_NOMOR + "=?",
                    new String[]{String.valueOf(nomorSurah)},
                    null, null, orderBy);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ayatList.add(cursorToAyat(cursor));
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting ayats for surah " + nomorSurah + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return ayatList;
    }

    private Ayat cursorToAyat(Cursor cursor) {
        int surahNomorIdx = cursor.getColumnIndex(COLUMN_AYAT_SURAH_NOMOR);
        int nomorAyatIdx = cursor.getColumnIndex(COLUMN_AYAT_NOMOR_AYAT);
        int teksArabIdx = cursor.getColumnIndex(COLUMN_AYAT_TEKS_ARAB);
        int teksLatinIdx = cursor.getColumnIndex(COLUMN_AYAT_TEKS_LATIN);
        int teksIndonesiaIdx = cursor.getColumnIndex(COLUMN_AYAT_TEKS_INDONESIA);
        int audioJsonIdx = cursor.getColumnIndex(COLUMN_AYAT_AUDIO_JSON);

        Ayat ayat = new Ayat();
        if (nomorAyatIdx != -1) ayat.setNomorAyat(cursor.getInt(nomorAyatIdx));
        if (teksArabIdx != -1) ayat.setTeksArab(cursor.getString(teksArabIdx));
        if (teksLatinIdx != -1) ayat.setTeksLatin(cursor.getString(teksLatinIdx));
        if (teksIndonesiaIdx != -1) ayat.setTeksIndonesia(cursor.getString(teksIndonesiaIdx));

        if (audioJsonIdx != -1 && !cursor.isNull(audioJsonIdx)) {
            String audioJson = cursor.getString(audioJsonIdx);
            if (audioJson != null && !audioJson.isEmpty()) {
                Type type = new TypeToken<Map<String, String>>(){}.getType();
                Map<String, String> audioMap = gson.fromJson(audioJson, type);
                ayat.setAudio(audioMap);
            }
        }
        return ayat;
    }
    private String convertMapToJsonString(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        return gson.toJson(map);
    }
    public List<AyatSearchResult> searchAyatsByKeyword(String keyword) {
        List<AyatSearchResult> resultList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Query dengan JOIN antara tabel ayat dan surah
        String query = "SELECT " +
                "s." + COLUMN_SURAH_NAMA_LATIN + ", " +
                "a." + COLUMN_AYAT_SURAH_NOMOR + ", " +
                "a." + COLUMN_AYAT_NOMOR_AYAT + ", " +
                "a." + COLUMN_AYAT_TEKS_ARAB + ", " +
                "a." + COLUMN_AYAT_TEKS_INDONESIA + " " +
                "FROM " + TABLE_AYAT + " a " +
                "JOIN " + TABLE_SURAH + " s ON a." + COLUMN_AYAT_SURAH_NOMOR + " = s." + COLUMN_SURAH_NOMOR + " " +
                "WHERE a." + COLUMN_AYAT_TEKS_INDONESIA + " LIKE ?";

        try {
            cursor = db.rawQuery(query, new String[]{"%" + keyword + "%"});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String surahName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURAH_NAMA_LATIN));
                    int surahNum = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AYAT_SURAH_NOMOR));
                    int ayatNum = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AYAT_NOMOR_AYAT));
                    String textArab = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AYAT_TEKS_ARAB));
                    String textIndonesia = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AYAT_TEKS_INDONESIA));

                    resultList.add(new AyatSearchResult(surahNum, surahName, ayatNum, textArab, textIndonesia));
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error searching ayats with keyword '" + keyword + "': " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return resultList;
    }
}
