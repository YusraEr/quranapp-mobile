package com.example.quranapp.data.remote.model; // Sesuaikan dengan package name Anda

import com.example.quranapp.utils.SettingsUtils;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class Surah {

    @SerializedName("nomor")
    private int nomor;

    @SerializedName("nama")
    private String nama;

    @SerializedName("namaLatin")
    private String namaLatin;

    @SerializedName("jumlahAyat")
    private int jumlahAyat;

    @SerializedName("tempatTurun")
    private String tempatTurun; // "Mekah" atau "Madinah"

    @SerializedName("arti")
    private String arti;

    @SerializedName("deskripsi")
    private String deskripsi;

    @SerializedName("audioFull") // Jika ada field ini di API untuk audio full surah
    private AudioUrl audioFull; // Menggunakan kelas AudioUrl yang akan kita definisikan

    // Konstruktor default (diperlukan oleh beberapa library seperti Gson/Retrofit)
    public Surah() {
    }

    // Konstruktor dengan semua field (opsional, tapi berguna)
    public Surah(int nomor, String nama, String namaLatin, int jumlahAyat, String tempatTurun, String arti, String deskripsi, AudioUrl audioFull) {
        this.nomor = nomor;
        this.nama = nama;
        this.namaLatin = namaLatin;
        this.jumlahAyat = jumlahAyat;
        this.tempatTurun = tempatTurun;
        this.arti = arti;
        this.deskripsi = deskripsi;
        this.audioFull = audioFull;
    }

    // Getter dan Setter untuk semua field

    public int getNomor() {
        return nomor;
    }

    public void setNomor(int nomor) {
        this.nomor = nomor;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNamaLatin() {
        return namaLatin;
    }

    public void setNamaLatin(String namaLatin) {
        this.namaLatin = namaLatin;
    }

    public int getJumlahAyat() {
        return jumlahAyat;
    }

    public void setJumlahAyat(int jumlahAyat) {
        this.jumlahAyat = jumlahAyat;
    }

    public String getTempatTurun() {
        return tempatTurun;
    }

    public void setTempatTurun(String tempatTurun) {
        this.tempatTurun = tempatTurun;
    }

    public String getArti() {
        return arti;
    }

    public void setArti(String arti) {
        this.arti = arti;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public AudioUrl getAudioFull() {
        return audioFull;
    }

    public void setAudioFull(AudioUrl audioFull) {
        this.audioFull = audioFull;
    }

    /**
     * Kelas inner untuk merepresentasikan URL audio jika API menyediakannya dalam format nested.
     * Contoh: "audioFull": { "01": "url1", "02": "url2", ... }
     * Sesuaikan field di sini dengan struktur JSON API Anda.
     * Jika audioFull hanya string URL biasa, maka field audioFull di Surah bisa langsung String.
     */
    public static class AudioUrl {
        @SerializedName("01") // Contoh jika ada audio versi 01 (misal, Misyari Rasyid)
        private String url01;

        @SerializedName("02") // Contoh jika ada audio versi 02 (misal, Al-Ghamidi)
        private String url02;

        @SerializedName("03")
        private String url03;

        @SerializedName("04")
        private String url04;

        @SerializedName("05")
        private String url05;


        // Getter dan Setter
        public String getUrl01() {
            return url01;
        }

        public void setUrl01(String url01) {
            this.url01 = url01;
        }

        public String getUrl02() {
            return url02;
        }

        public void setUrl02(String url02) {
            this.url02 = url02;
        }

        public String getUrl03() { return url03; }

        public void setUrl03(String url03) { this.url03 = url03; }

        public String getUrl04() { return url04; }

        public void setUrl04(String url04) { this.url04 = url04; }

        public String getUrl05() { return url05; }

        public void setUrl05(String url05) { this.url05 = url05; }

        // Anda bisa menambahkan metode untuk mendapatkan URL default atau yang pertama tersedia
        public String getPreferredAudioUrl() {
            if (url01 != null && !url01.isEmpty()) return url01;
            if (url02 != null && !url02.isEmpty()) return url02;
            if (url03 != null && !url03.isEmpty()) return url03;
            if (url04 != null && !url04.isEmpty()) return url04;
            if (url05 != null && !url05.isEmpty()) return url05;
            return null;
        }

        public String getAudioUrlForReciter(String reciterKey) {
            Map<String, String> audioMap = new HashMap<>();
            if (url01 != null) audioMap.put("01", url01);
            if (url02 != null) audioMap.put("02", url02);
            if (url03 != null) audioMap.put("03", url03);
            if (url04 != null) audioMap.put("04", url04);
            if (url05 != null) audioMap.put("05", url05);

            // 1. Coba dapatkan URL untuk qari pilihan
            if (audioMap.containsKey(reciterKey)) {
                return audioMap.get(reciterKey);
            }

            // 2. Fallback jika qari pilihan tidak tersedia: Coba qari default (Misyari)
            if (audioMap.containsKey(SettingsUtils.DEFAULT_RECITER_KEY)) {
                return audioMap.get(SettingsUtils.DEFAULT_RECITER_KEY);
            }

            // 3. Fallback terakhir: Kembalikan URL pertama yang tersedia
            if (!audioMap.isEmpty()) {
                return audioMap.values().iterator().next();
            }

            return null; // Tidak ada audio sama sekali
        }
    }

    @Override
    public String toString() {
        return "Surah{" +
                "nomor=" + nomor +
                ", namaLatin='" + namaLatin + '\'' +
                '}';
    }
}
