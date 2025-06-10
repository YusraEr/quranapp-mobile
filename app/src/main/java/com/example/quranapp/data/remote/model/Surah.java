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
    private String tempatTurun;

    @SerializedName("arti")
    private String arti;

    @SerializedName("deskripsi")
    private String deskripsi;

    @SerializedName("audioFull")
    private AudioUrl audioFull;

    private SuratNavInfo suratSelanjutnya;
    private SuratNavInfo suratSebelumnya;

    public Surah() {
    }

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

    public static class AudioUrl {
        @SerializedName("01")
        private String url01;

        @SerializedName("02")
        private String url02;

        @SerializedName("03")
        private String url03;

        @SerializedName("04")
        private String url04;

        @SerializedName("05")
        private String url05;

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


        public String getAudioUrlForReciter(String reciterKey) {
            Map<String, String> audioMap = new HashMap<>();
            if (url01 != null) audioMap.put("01", url01);
            if (url02 != null) audioMap.put("02", url02);
            if (url03 != null) audioMap.put("03", url03);
            if (url04 != null) audioMap.put("04", url04);
            if (url05 != null) audioMap.put("05", url05);

            if (audioMap.containsKey(reciterKey)) {
                return audioMap.get(reciterKey);
            }

            if (audioMap.containsKey(SettingsUtils.DEFAULT_RECITER_KEY)) {
                return audioMap.get(SettingsUtils.DEFAULT_RECITER_KEY);
            }

            if (!audioMap.isEmpty()) {
                return audioMap.values().iterator().next();
            }

            return null;
        }
    }

    @Override
    public String toString() {
        return "Surah{" +
                "nomor=" + nomor +
                ", namaLatin='" + namaLatin + '\'' +
                '}';
    }

    public SuratNavInfo getSuratSelanjutnya() {
        return suratSelanjutnya;
    }

    public void setSuratSelanjutnya(SuratNavInfo suratSelanjutnya) {
        this.suratSelanjutnya = suratSelanjutnya;
    }

    public SuratNavInfo getSuratSebelumnya() {
        return suratSebelumnya;
    }

    public void setSuratSebelumnya(SuratNavInfo suratSebelumnya) {
        this.suratSebelumnya = suratSebelumnya;
    }
}
