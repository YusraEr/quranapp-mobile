# 📖 QuranApp Mobile

Aplikasi mobile Al-Quran yang elegan dan mudah digunakan, dikembangkan dengan Android Java. Aplikasi ini menyediakan akses lengkap ke teks Al-Quran beserta terjemahan Indonesia, tafsir, dan fitur audio untuk setiap ayat dan surah.

## ✨ Fitur Utama

### 📱 Core Features
- **Daftar Surah Lengkap**: Akses ke seluruh 114 surah Al-Quran
- **Teks Al-Quran**: Teks Arab asli dengan transliterasi latin
- **Terjemahan Indonesia**: Terjemahan dalam bahasa Indonesia untuk setiap ayat
- **Audio Al-Quran**: Putar audio untuk ayat individual atau surah lengkap
- **Tafsir**: Akses tafsir untuk pemahaman yang lebih mendalam
- **Pencarian Ayat**: Fitur pencarian untuk menemukan ayat tertentu

### 🎨 User Experience
- **Tema Dark/Light**: Dukungan tema gelap dan terang
- **Material Design 3**: Antarmuka modern dan intuitif
- **Offline Support**: Akses konten meski tanpa koneksi internet
- **Pilihan Qari**: Berbagai pilihan suara qari untuk audio

### 🔧 Technical Features
- **Local Database**: Penyimpanan offline dengan SQLite
- **Auto Sync**: Sinkronisasi otomatis dengan API online
- **Responsive Design**: Optimized untuk berbagai ukuran layar

## 🚀 Screenshot

*Screenshot akan ditambahkan setelah aplikasi di-build dan dijalankan*

## 🛠️ Teknologi yang Digunakan

### Programming Language
- **Java**: Bahasa pemrograman utama

### Android Framework
- **Android SDK**: Min SDK 26, Target SDK 34
- **Material Design 3**: Komponen UI modern
- **View Binding**: Binding view yang aman
- **Fragments**: Untuk navigasi antar halaman

### Libraries & Dependencies
- **Retrofit 2**: HTTP client untuk API calls
- **Gson**: JSON parsing
- **OkHttp**: Logging interceptor
- **AndroidX Lifecycle**: ViewModel dan LiveData
- **SwipeRefreshLayout**: Pull-to-refresh functionality

### Architecture Pattern
- **MVVM (Model-View-ViewModel)**: Arsitektur yang clean dan maintainable
- **Repository Pattern**: Centralized data management
- **LiveData**: Reactive data observation

### Database
- **SQLite**: Local database untuk offline storage
- **Custom DB Helper**: Management untuk Surah, Ayat, dan Tafsir

## 📋 Persyaratan Sistem

- **Android Version**: Android 8.0 (API level 26) atau lebih tinggi
- **RAM**: Minimal 2GB (Disarankan 4GB)
- **Storage**: 100MB ruang kosong
- **Internet**: Diperlukan untuk download konten pertama kali

## 🔧 Instalasi & Setup

### Prerequisites
- Android Studio (Latest version)
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

### Configuration

Aplikasi menggunakan API dari [equran.id](https://equran.id/api/). Tidak diperlukan konfigurasi API key khusus.

## 📱 Cara Penggunaan

### 🏠 Halaman Utama
- Lihat daftar lengkap 114 surah Al-Quran
- Gunakan tombol audio untuk mendengar surah lengkap
- Klik surah untuk melihat detail ayat
- Gunakan menu untuk mengubah tema atau memilih qari

### 📖 Halaman Detail Surah
- Baca ayat dalam teks Arab, transliterasi, dan terjemahan
- Klik tombol audio untuk mendengar ayat individual
- Akses tafsir untuk pemahaman lebih dalam
- Navigasi antar surah dengan tombol next/previous

### 🔍 Pencarian
- Gunakan fitur search untuk mencari ayat tertentu
- Hasil pencarian menampilkan ayat beserta konteksnya

### ⚙️ Pengaturan
- **Tema**: Pilih antara tema terang atau gelap
- **Qari**: Pilih suara qari favorit untuk audio Al-Quran
- **Auto-sync**: Data akan otomatis tersinkron saat online

## 🏗️ Struktur Project

```
app/src/main/
├── java/com/example/quranapp/
│   ├── MainActivity.java                    # Activity utama
│   ├── DetailActivity.java                  # Detail surah
│   ├── SearchActivity.java                  # Pencarian ayat
│   ├── adapter/                            # RecyclerView adapters
│   │   ├── AyatAdapter.java
│   │   └── SurahAdapter.java
│   ├── data/                               # Data layer
│   │   ├── QuranRepository.java            # Repository pattern
│   │   ├── local/
│   │   │   └── QuranDbHelper.java          # SQLite database
│   │   └── remote/
│   │       ├── QuranApiService.java        # API interface
│   │       ├── RetrofitClient.java         # HTTP client
│   │       └── model/                      # Data models
│   ├── fragments/                          # UI fragments
│   │   ├── SurahListFragment.java
│   │   └── AyatListFragment.java
│   ├── utils/                              # Utility classes
│   │   ├── ThemeUtils.java                 # Theme management
│   │   ├── SettingsUtils.java              # Settings management
│   │   └── NetworkUtils.java               # Network utilities
│   └── viewmodel/                          # MVVM ViewModels
│       ├── SurahViewModel.java
│       └── AyatViewModel.java
└── res/                                    # Resources
    ├── layout/                             # XML layouts
    ├── values/                             # Colors, strings, themes
    └── drawable/                           # Graphics resources
```

## 🎨 Design System

### Color Palette
- **Primary**: Deep Navy Blue (#0D2C54)
- **Secondary**: Elegant Gold (#C9A96E)
- **Background Light**: #F8F9FA
- **Background Dark**: #121212

### Typography
- **Arabic Text**: Font khusus untuk teks Al-Quran
- **Latin Text**: Material Design 3 typography scale

## 🌐 API Integration

Aplikasi terintegrasi dengan [equran.id API v2](https://equran.id/api/v2/):

- **Base URL**: `https://equran.id/api/v2/`
- **Endpoints**:
  - `GET /surat` - Daftar semua surah
  - `GET /surat/{nomor}` - Detail surah dan ayat
  - `GET /tafsir/{nomor}` - Tafsir surah

## 🔄 Offline Support

- Data surah dan ayat disimpan secara lokal menggunakan SQLite
- Aplikasi dapat berfungsi offline setelah download pertama
- Auto-sync saat koneksi internet tersedia

## 🤝 Kontribusi

Kami menyambut kontribusi dari komunitas! Berikut cara berkontribusi:

1. **Fork repository**
2. **Buat branch feature** (`git checkout -b feature/AmazingFeature`)
3. **Commit perubahan** (`git commit -m 'Add some AmazingFeature'`)
4. **Push ke branch** (`git push origin feature/AmazingFeature`)
5. **Buat Pull Request**

### Guidelines
- Ikuti coding style yang sudah ada
- Tambahkan dokumentasi untuk fitur baru
- Test aplikasi sebelum submit PR
- Pastikan tidak ada breaking changes

## 📝 License

Project ini menggunakan license [MIT License](LICENSE).

## 👨‍💻 Developer

**YusraEr**
- GitHub: [@YusraEr](https://github.com/YusraEr)

## 🙏 Acknowledgments

- [equran.id](https://equran.id) untuk API Al-Quran
- Material Design team untuk design guidelines
- Android developer community untuk inspirasi dan bantuan

## 📞 Support

Jika Anda mengalami masalah atau memiliki pertanyaan:

1. **Issues**: Buat issue di GitHub repository
2. **Discussions**: Gunakan GitHub Discussions untuk pertanyaan umum
3. **Email**: Hubungi developer melalui GitHub profile

## 🔄 Changelog

### Version 1.0.0 (Current)
- ✅ Initial release
- ✅ Complete Quran text dengan terjemahan
- ✅ Audio playback untuk ayat dan surah
- ✅ Tafsir integration
- ✅ Dark/Light theme
- ✅ Offline support
- ✅ Search functionality

### Upcoming Features
- 🔄 Bookmark ayat favorit
- 🔄 Reading progress tracker
- 🔄 Multiple language support
- 🔄 Widget untuk home screen
- 🔄 Notification untuk waktu sholat

---

<div align="center">
  <p>Dibuat dengan ❤️ untuk umat Muslim di seluruh dunia</p>
  <p>بارك الله فيكم</p>
</div>
