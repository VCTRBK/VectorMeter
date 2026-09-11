# VectorMeter 1.3.0 — VectorPlan + GreatVector + Support

## Perubahan
- VectorPlan memakai kalender HTML asli (`type=date`).
- Satu tanggal dapat memiliki banyak plan perjalanan.
- Plan dapat dipilih kembali, diedit, dan dihapus.
- Setiap plan menyimpan titik, nama lokasi otomatis, KM awal manual, KM akhir otomatis, dan jumlah KM rute.
- Menu baru GREATVECTOR merangkum semua plan tersimpan berdasarkan tanggal.
- GreatVector memiliki 6 kolom: NOMOR, TANGGAL, TUJUAN, KILOMETER AWAL, KILOMETER AKHIR, JUMLAH KM.
- Baris terakhir TOTAL KILOMETER menjumlahkan kolom 6.
- Menu SUPPORT hanya dapat diubah setelah login administrator; pengguna biasa hanya dapat melihat.
- Settings (⚙) di kanan atas berisi login admin.
- HELP dapat diedit admin.
- Admin dapat menambah tulisan serta gambar/foto/video ke SUPPORT dan menghapus konten SUPPORT.

## Login admin
Untuk build ini password administrator default adalah:
`VECTORMETER-ADMIN`

Password ini tertanam di aplikasi, jadi ini cocok untuk akses admin lokal sederhana, bukan sistem keamanan tingkat perusahaan/server.

## Catatan data
Data plan, SUPPORT, dan HELP disimpan di localStorage WebView pada perangkat. Menghapus data aplikasi dapat menghapus data tersebut.
