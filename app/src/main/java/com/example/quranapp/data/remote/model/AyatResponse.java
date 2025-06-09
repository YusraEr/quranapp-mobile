package com.example.quranapp.data.remote.model; // Sesuaikan dengan package name Anda

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AyatResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private AyatData data;

    // Getter dan Setter
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AyatData getData() {
        return data;
    }

    public void setData(AyatData data) {
        this.data = data;
    }

    // Kelas inner untuk bagian "data" dalam respons detail surah
    public static class AyatData {
        @SerializedName("nomor")
        private int nomor;

        @SerializedName("nama")
        private String nama; // Nama Arab

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
        private Surah.AudioUrl audioFull; // Menggunakan AudioUrl dari Surah.java

        @SerializedName("ayat")
        private List<Ayat> ayat; // List dari Ayat.java yang sudah kita buat

        @SerializedName("suratSelanjutnya")
        private SuratNavInfo suratSelanjutnya; // Bisa objek atau false (boolean)

        @SerializedName("suratSebelumnya")
        private SuratNavInfo suratSebelumnya; // Bisa objek atau false (boolean)

        // Getter dan Setter untuk AyatData
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

        public Surah.AudioUrl getAudioFull() {
            return audioFull;
        }

        public void setAudioFull(Surah.AudioUrl audioFull) {
            this.audioFull = audioFull;
        }

        public List<Ayat> getAyat() {
            return ayat;
        }

        public void setAyat(List<Ayat> ayat) {
            this.ayat = ayat;
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
}
