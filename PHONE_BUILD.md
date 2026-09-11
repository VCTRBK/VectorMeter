# Cara membuat APK VectorMeter dari HP Android

Cara ini memakai GitHub Actions. Tidak perlu laptop/PC.

## 1. Buat repository GitHub
1. Buka https://github.com di Chrome.
2. Login.
3. Buat repository baru, misalnya `VectorMeter`.
4. Boleh Public agar paling sederhana.

## 2. Upload project
Upload isi folder project ini ke repository, sehingga file berikut berada di root repository:
- `build.gradle`
- `settings.gradle`
- `gradle.properties`
- folder `app/`
- folder `.github/workflows/`

Jangan upload folder pembungkus `VectorMeter_Final` sebagai satu folder jika ingin workflow langsung membaca project.

## 3. Jalankan build
1. Buka tab **Actions** pada repository.
2. Pilih workflow **Build VectorMeter APK**.
3. Tekan **Run workflow**.
4. Tunggu sampai status hijau.

## 4. Download APK
1. Buka hasil workflow yang selesai.
2. Cari bagian **Artifacts**.
3. Download `VectorMeter-debug-apk`.
4. Ekstrak ZIP artifact.
5. Instal `app-debug.apk` di HP.

Build debug APK memang ditandatangani dengan debug key oleh sistem Android, sehingga cocok untuk pengujian langsung. Untuk distribusi resmi, perlu membuat release APK dengan signing key sendiri.
