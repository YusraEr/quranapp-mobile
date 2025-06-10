# QuranApp

Aplikasi Al-Quran untuk platform Android yang dikembangkan menggunakan Java. Aplikasi ini menyediakan akses ke teks Al-Quran, terjemahan bahasa Indonesia, tafsir, dan fitur audio.

## Fitur

### Konten
- Daftar lengkap 114 surah Al-Quran
- Teks Arab asli dengan transliterasi latin
- Terjemahan bahasa Indonesia
- Audio per ayat dan surah
- Tafsir untuk pemahaman lebih mendalam
- Pencarian ayat

### Antarmuka Pengguna
- Dukungan tema gelap dan terang
- Implementasi Material Design 3
- Akses konten dalam mode offline
- Pilihan qari untuk audio

### Teknis
- Penyimpanan lokal menggunakan SQLite
- Sinkronisasi dengan API
- Desain responsif untuk berbagai ukuran layar

## Screenshot

<img src="documentation/lightmode/image%20(1).jpg" alt="image 1 lightmode" width="300"/>
<img src="documentation/lightmode/image%20(2).jpg" alt="image 2 lightmode" width="300"/>
<img src="documentation/lightmode/image%20(3).jpg" alt="image 3 lightmode" width="300"/>
<img src="documentation/lightmode/image%20(4).jpg" alt="image 4 lightmode" width="300"/>
<img src="documentation/lightmode/image%20(5).jpg" alt="image 5 lightmode" width="300"/>
<img src="documentation/lightmode/image%20(6).jpg" alt="image 6 lightmode" width="300"/>
<img src="documentation/lightmode/image%20(7).jpg" alt="image 7 lightmode" width="300"/>_

<img src="documentation/darkmode/image%20(1).jpg" alt="image 1 darkmode" width="300"/>
<img src="documentation/darkmode/image%20(2).jpg" alt="image 2 darkmode" width="300"/>
<img src="documentation/darkmode/image%20(3).jpg" alt="image 3 darkmode" width="300"/>
<img src="documentation/darkmode/image%20(4).jpg" alt="image 4 darkmode" width="300"/>
<img src="documentation/darkmode/image%20(5).jpg" alt="image 5 darkmode" width="300"/>
<img src="documentation/darkmode/image%20(6).jpg" alt="image 6 darkmode" width="300"/>
## Teknologi

### Bahasa Pemrograman
- **Java**

### Framework Android
- **Android SDK**: Min SDK 26, Target SDK 34
- **Material Design 3**
- **View Binding**
- **Fragments**

### Library & Dependensi
- **Retrofit 2**: HTTP client untuk API
- **Gson**: JSON parsing
- **OkHttp**: Logging interceptor
- **AndroidX Lifecycle**: ViewModel dan LiveData

### Pola Arsitektur
- **MVVM (Model-View-ViewModel)**
- **Repository Pattern**
- **LiveData**

### Database
- **SQLite**
- **Custom DB Helper**

## Persyaratan Sistem

- **Android Version**: Android 8.0 (API level 26) atau lebih tinggi
- **RAM**: Minimal 2GB (Disarankan 4GB)
- **Storage**: 100MB ruang kosong
- **Internet**: Diperlukan untuk download konten pertama kali

## Instalasi & Setup

### Prasyarat
- Android Studio (Versi terbaru)
- JDK 11 atau lebih tinggi
- Android SDK dengan API level 26-34

### Langkah Instalasi

1. **Clone repository**
   ```bash
   git clone https://github.com/YusraEr/quranapp-mobile.git
   cd quranapp-mobile
   ```

2. **Buka di Android Studio**
   - Buka Android Studio
   - File → Open → Pilih folder project
   - Tunggu hingga Gradle sync selesai

3. **Build & Run**
   - Pilih device atau emulator
   - Klik tombol Run (▶️) atau tekan Shift+F10

### Konfigurasi

Aplikasi menggunakan API dari [equran.id](https://equran.id/api/). Tidak diperlukan konfigurasi API key khusus.

## Cara Penggunaan

### Halaman Utama
- Lihat daftar lengkap 114 surah Al-Quran
- Gunakan tombol audio untuk mendengar surah lengkap
- Klik surah untuk melihat detail ayat
- Gunakan menu untuk mengubah tema atau memilih qari

### Halaman Detail Surah
- Baca ayat dalam teks Arab, transliterasi, dan terjemahan
- Klik tombol audio untuk mendengar ayat individual
- Akses tafsir untuk pemahaman lebih dalam
- Navigasi antar surah dengan tombol next/previous

### Pencarian
- Gunakan fitur search untuk mencari ayat tertentu

## Kontribusi

Kontribusi untuk pengembangan QuranApp sangat dihargai. Berikut adalah langkah-langkah untuk berkontribusi:

1. Fork repositori ini
2. Buat branch fitur baru (`git checkout -b feature/fitur-baru`)
3. Commit perubahan Anda (`git commit -m 'Menambahkan fitur baru'`)
4. Push ke branch (`git push origin feature/fitur-baru`)
5. Buat Pull Request

### Pedoman Kontribusi

- Pastikan kode yang disubmit telah diuji
- Ikuti konvensi kode yang ada
- Tambahkan komentar yang jelas pada kode
- Update dokumentasi jika diperlukan

## Pemecahan Masalah

### Masalah Umum

1. **Audio tidak dapat diputar**
   - Pastikan koneksi internet stabil
   - Verifikasi bahwa file audio belum diblokir oleh firewall

2. **Aplikasi lambat merespon**
   - Pastikan perangkat memenuhi persyaratan sistem minimum
   - Bersihkan cache aplikasi melalui pengaturan Android

3. **Data tidak muncul dalam mode offline**
   - Pastikan konten telah diunduh sebelumnya saat terhubung ke internet
   - Periksa kapasitas penyimpanan pada perangkat

## Lisensi

Proyek ini dilisensikan di bawah [MIT License](LICENSE) - lihat file LICENSE untuk detail lebih lanjut.

## Kredit

- Data Al-Quran disediakan oleh [equran.id](https://equran.id/api/)
- Audio recitation oleh qari terpilih
- Icons dari [Material Design Icons](https://material.io/resources/icons/)

## Kontak

Untuk pertanyaan, saran, atau laporan bug, silakan buka issue di repositori GitHub atau hubungi tim pengembang di [email@example.com](mailto:email@example.com).

