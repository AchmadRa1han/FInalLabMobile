# KomikFinale - Aplikasi Katalog Manga

Aplikasi Android untuk menjelajahi, menyimpan, dan membaca informasi manga, dibuat sebagai Proyek Tugas Akhir Lab Mobile 2025.

**Nama:** [Nama Lengkap Anda]
**NIM:** [NIM Anda]
**Grup Asistensi:** [Contoh: Mobile-1]

---

## Deskripsi Aplikasi

KomikFinale adalah aplikasi katalog manga yang memungkinkan pengguna untuk menemukan manga terbaru dan terpopuler dari API publik MangaDex. Pengguna dapat melihat detail informasi dari setiap manga, termasuk deskripsi dan daftar chapter yang tersedia dalam bahasa Inggris. Aplikasi ini juga dilengkapi dengan fitur "Favorit" yang memungkinkan pengguna menyimpan manga pilihannya ke dalam database lokal di perangkat, sehingga bisa diakses kembali bahkan saat tidak ada koneksi internet (offline).

[cite_start]Proyek ini dibuat untuk memenuhi semua spesifikasi teknis yang diberikan dalam Tugas Final Lab Mobile 2025.

## Screenshot Aplikasi

| Halaman Utama | Halaman Detail | Halaman Favorit (Library) |
| :---: | :---: | :---: |
| ![Home Screen](./path/to/your/screenshot_home.png) | ![Detail Screen](./path/to/your/screenshot_detail.png) | ![Library Screen](./path/to/your/screenshot_library.png) |

*(Catatan: Unggah screenshot Anda ke dalam repositori dan ganti `./path/to/...` dengan lokasi file gambar Anda)*

## Fitur Utama

- [cite_start]**Menjelajahi Manga:** Menampilkan daftar 40 manga populer dari API MangaDex.
- [cite_start]**Detail Manga:** Menampilkan halaman detail untuk setiap manga, berisi judul, gambar sampul, dan deskripsi.
- [cite_start]**Daftar Chapter:** Menampilkan daftar chapter yang tersedia dalam bahasa Inggris, terurut dari chapter terlama.
- **Favorit (Database Lokal):** Pengguna dapat menambah dan menghapus manga dari daftar favorit. [cite_start]Daftar ini tersimpan di database lokal (Room/SQLite) dan bisa diakses secara offline.
- [cite_start]**Navigasi:** Menggunakan Navigation Component dengan Bottom Navigation untuk berpindah antara halaman Utama dan Favorit.
- [cite_start]**Mode Terang & Gelap:** Aplikasi mendukung dua mode tema yang bisa diganti untuk kenyamanan pengguna.
- [cite_start]**Penanganan Error:** Menampilkan pesan dan tombol refresh jika gagal mengambil data dari internet.
- [cite_start]**Membuka Chapter:** Menggunakan Intent untuk membuka Activity kedua (`ReaderActivity`) saat sebuah chapter diklik.

## Penjelasan Implementasi Teknis

Aplikasi ini dibangun menggunakan bahasa Java dengan arsitektur MVVM (Model-View-ViewModel). Komponen utama yang digunakan antara lain:

- **Android Jetpack:**
    - **ViewModel:** Untuk mengelola data yang berhubungan dengan UI dan bertahan dari perubahan konfigurasi.
    - **LiveData:** Untuk membuat komponen data yang bisa diobservasi dan bersifat lifecycle-aware.
    - [cite_start]**Room Persistence Library:** Sebagai lapisan abstraksi di atas SQLite untuk membuat database lokal yang robust.
    - [cite_start]**Navigation Component:** Untuk mengelola semua navigasi antar fragment di dalam aplikasi.
- **Networking:**
    - [cite_start]**Retrofit 2:** Sebagai HTTP client untuk berkomunikasi dengan API MangaDex secara deklaratif.
    - **Gson:** Untuk mem-parsing data JSON dari API menjadi objek Java.
- **Asynchronous:**
    - [cite_start]**Executor:** Untuk menjalankan operasi database (insert/delete) di background thread, agar tidak mengganggu UI thread.
- **UI:**
    - [cite_start]**RecyclerView:** Digunakan untuk menampilkan semua daftar data (manga dan chapter) secara efisien.
    - **Material Components:** Untuk komponen UI modern seperti `MaterialCardView`, `BottomNavigationView`, dan `FloatingActionButton`.
    - **Glide:** Untuk memuat dan men-cache gambar dari internet secara efisien.