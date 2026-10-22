# 🛒 Kasir Josjismart - Sistem Kasir & Point of Sales (POS) Berbasis Website & Java OOP

Aplikasi Kasir modern berbasis **Website** dengan backend **Java murni (PBO/OOP)** yang dirancang untuk platform **Apache NetBeans**. Semua kode dan antarmuka desktop/terminal lama yang tidak berguna telah dibersihkan sehingga proyek ini murni berjalan sebagai **Aplikasi Website Modern** di browser.

---

## 📁 Struktur Folder & File (Sangat Rapi & Bersih)

```text
kasir/
├── pom.xml                                   # Konfigurasi proyek standar Apache NetBeans (Maven)
├── run.bat                                   # Script jalankan website cepat tanpa IDE
├── README.md                                 # Panduan lengkap proyek & konsep tugas
└── src/
    └── main/
        ├── java/com/kasir/                   # BACKEND JAVA (OOP CORE & LOGIC)
        │   ├── Main.java                     # Titik awal: menjalankan web server & buka browser
        │   │
        │   ├── model/                        # Entitas Data & Konsep OOP
        │   │   ├── Diskonable.java           # [Abstraksi - Interface] Kontrak perhitungan diskon
        │   │   ├── Produk.java               # [Abstraksi & Enkapsulasi] Superclass produk
        │   │   ├── Makanan.java              # [Pewarisan & Polimorfisme] Subclass produk makanan
        │   │   ├── Elektronik.java           # [Pewarisan & Polimorfisme] Subclass produk elektronik
        │   │   ├── ItemTransaksi.java        # [Enkapsulasi] Item belanja dalam keranjang
        │   │   └── Transaksi.java            # [Enkapsulasi & Overloading] Logika transaksi & struk
        │   │
        │   ├── service/                      # Lapisan Logika Bisnis
        │   │   ├── ProdukService.java        # Logika CRUD Master Data Produk
        │   │   └── TransaksiService.java     # Logika POS (Keranjang, Checkout, Struk)
        │   │
        │   └── web/                          # Server Web Lokal & REST API
        │       └── WebServer.java            # Menyajikan file statis & endpoint REST API
        │
        └── resources/web/                    # FRONTEND WEBSITE (MODERN UI)
            ├── index.html                    # Struktur halaman web kasir
            ├── style.css                     # Desain tampilan & tata letak responsif
            └── app.js                        # Logika interaktif keranjang, CRUD, & checkout
```

---

## 💻 Cara Menjalankan di Apache NetBeans

1. Buka aplikasi **Apache NetBeans**.
2. Pilih menu **File** $\rightarrow$ **Open Project...**
3. Pilih folder: `C:\Users\LENOVO\OneDrive\Documents\kasir`.
4. Tekan tombol **F6** (atau klik kanan pada [`Main.java`](file:///C:/Users/LENOVO/OneDrive/Documents/kasir/src/main/java/com/kasir/Main.java) $\rightarrow$ **Run File** / `Shift + F6`).
5. **HASIL:** Browser Anda (Chrome / Edge / Firefox) akan langsung terbuka otomatis menampilkan website kasir:
   **`index.html`**

---

## ⚡ Cara Menjalankan Cepat (Tanpa Buka NetBeans)
- Cukup **double-click** file [`index.html`](file:///C:/Users/LENOVO/OneDrive/Documents/kasir/index.html) atau [`run.bat`](file:///C:/Users/LENOVO/OneDrive/Documents/kasir/run.bat) di folder proyek.
- Halaman website kasir akan langsung terbuka di browser Anda secara instan tanpa memerlukan server.

---

## 🎓 Pemetaan Konsep Pemrograman Berorientasi Objek (OOP)

| No | Konsep OOP | Implementasi pada Kode | Lokasi File |
|---|---|---|---|
| **1** | **Atribut & Metode** | Seluruh kelas memiliki atribut data privat (`kode`, `nama`, `harga`, `stok`) dan fungsi aksi (`kurangiStok()`, `hitungTotalAkhir()`). | [`Produk.java`](file:///C:/Users/LENOVO/OneDrive/Documents/kasir/src/main/java/com/kasir/model/Produk.java) |
| **2** | **Enkapsulasi** | Atribut diset `private` dan hanya dapat diakses melalui `Getter` & `Setter` dengan validasi data ketat (harga > 0, stok $\ge$ 0). | [`Produk.java`](file:///C:/Users/LENOVO/OneDrive/Documents/kasir/src/main/java/com/kasir/model/Produk.java), [`Transaksi.java`](file:///C:/Users/LENOVO/OneDrive/Documents/kasir/src/main/java/com/kasir/model/Transaksi.java) |
| **3** | **Abstraksi** | 1. **Interface**: [`Diskonable`](file:///C:/Users/LENOVO/OneDrive/Documents/kasir/src/main/java/com/kasir/model/Diskonable.java) mendefinisikan metode kontrak kalkulasi diskon.<br>2. **Abstract Class**: [`Produk`](file:///C:/Users/LENOVO/OneDrive/Documents/kasir/src/main/java/com/kasir/model/Produk.java) sebagai kerangka umum yang memiliki metode abstrak `getKategori()` dan `tampilDetail()`. | [`Diskonable.java`](file:///C:/Users/LENOVO/OneDrive/Documents/kasir/src/main/java/com/kasir/model/Diskonable.java), [`Produk.java`](file:///C:/Users/LENOVO/OneDrive/Documents/kasir/src/main/java/com/kasir/model/Produk.java) |
| **4** | **Pewarisan (Inheritance)** | Subclass [`Makanan`](file:///C:/Users/LENOVO/OneDrive/Documents/kasir/src/main/java/com/kasir/model/Makanan.java) dan [`Elektronik`](file:///C:/Users/LENOVO/OneDrive/Documents/kasir/src/main/java/com/kasir/model/Elektronik.java) mewarisi `Produk` dengan kata kunci `extends` dan konstruktor `super(...)`. | [`Makanan.java`](file:///C:/Users/LENOVO/OneDrive/Documents/kasir/src/main/java/com/kasir/model/Makanan.java), [`Elektronik.java`](file:///C:/Users/LENOVO/OneDrive/Documents/kasir/src/main/java/com/kasir/model/Elektronik.java) |
| **5** | **Polimorfisme (Overriding)** | Subclass meng-override metode `hitungDiskon()`:<br>- `Makanan`: Diskon 10% jika beli $\ge 5$ unit.<br>- `Elektronik`: Diskon promo 5% jika total belanja $\ge \text{Rp } 500.000$.<br>Objek dipanggil dinamis melalui polimorfisme koleksi `List<Produk>`. | [`Makanan.java`](file:///C:/Users/LENOVO/OneDrive/Documents/kasir/src/main/java/com/kasir/model/Makanan.java), [`Elektronik.java`](file:///C:/Users/LENOVO/OneDrive/Documents/kasir/src/main/java/com/kasir/model/Elektronik.java) |
| **6** | **Polimorfisme (Overloading)** | Metode dengan nama yang sama namun parameter berbeda:<br>- `tambahItem(Produk, int)` dan `tambahItem(ItemTransaksi)`<br>- `bayar(nominal)` dan `bayar(nominal, diskonVoucher)`. | [`Transaksi.java`](file:///C:/Users/LENOVO/OneDrive/Documents/kasir/src/main/java/com/kasir/model/Transaksi.java) |

---

## 🌐 Fitur Utama Website Kasir Josjismart

1. **Point of Sales (POS):**
   - Katalog produk interaktif lengkap dengan pencarian realtime & filter kategori.
   - Keranjang belanja dinamis dengan tombol kontrol kuantitas (+ / -).
   - Perhitungan diskon otomatis berdasarkan polimorfisme produk.
   - Input voucher diskon tambahan.
   - Input nominal tunai & kalkulasi uang kembalian secara realtime.
   - Modal cetak struk belanja yang rapi dan siap dicetak ke printer fisik (`window.print()`).
2. **Kelola Master Data Produk (CRUD):**
   - **Create**: Modal tambah makanan (dengan tanggal kadaluarsa) atau elektronik (dengan masa garansi).
   - **Read**: Tabel data inventaris lengkap dengan badge kategori.
   - **Update**: Modal ubah nama, harga satuan, dan kuantitas stok barang.
   - **Delete**: Hapus produk dengan konfirmasi keamanan.
3. **Riwayat Penjualan:**
   - Rekap seluruh transaksi penjualan dan total pendapatan kasir.
   - Tombol untuk melihat kembali dan mencetak ulang struk lama.
