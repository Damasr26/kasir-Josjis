package com.kasir.model;

/**
 * =========================================================================
 * KONSEP OOP: ABSTRAKSI (ABSTRACT CLASS) & ENKAPSULASI (ENCAPSULATION)
 * =========================================================================
 * 1. Abstraksi: Kelas Produk adalah kelas abstrak yang tidak bisa di-instansiasi langsung.
 *    Kelas ini menjadi blueprint/kerangka bagi kelas-kelas turunan (Makanan, Elektronik).
 * 2. Enkapsulasi: Semua atribut dibuat bertipe 'private' dan hanya dapat diakses
 *    atau diubah melalui metode getter dan setter dengan validasi tertentu.
 */
public abstract class Produk implements Diskonable {
    
    // === ATRIBUT DENGAN AKSES PRIVATE (ENKAPSULASI) ===
    private String kode;
    private String nama;
    private double harga;
    private int stok;

    // === KONSTRUKTOR ===
    public Produk(String kode, String nama, double harga, int stok) {
        setKode(kode);
        setNama(nama);
        setHarga(harga);
        setStok(stok);
    }

    // === METODE GETTER & SETTER (ENKAPSULASI DENGAN VALIDASI) ===

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        if (kode == null || kode.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode produk tidak boleh kosong!");
        }
        this.kode = kode.trim().toUpperCase();
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama produk tidak boleh kosong!");
        }
        this.nama = nama.trim();
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        if (harga <= 0) {
            throw new IllegalArgumentException("Harga produk harus lebih besar dari 0!");
        }
        this.harga = harga;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        if (stok < 0) {
            throw new IllegalArgumentException("Stok tidak boleh bernilai negatif!");
        }
        this.stok = stok;
    }

    // === METODE OPERASIONAL STOK ===

    public boolean kurangiStok(int jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah pengurangan harus lebih dari 0!");
        }
        if (this.stok >= jumlah) {
            this.stok -= jumlah;
            return true;
        }
        return false;
    }

    public void tambahStok(int jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah penambahan harus lebih dari 0!");
        }
        this.stok += jumlah;
    }

    // === METODE ABSTRAK (WAJIB DI-OVERRIDE OLEH KELAS TURUNAN) ===
    
    /**
     * Mengembalikan nama kategori dari produk.
     */
    public abstract String getKategori();

    /**
     * Menampilkan detail spesifik produk ke layar.
     */
    public abstract void tampilDetail();

    @Override
    public String toString() {
        return String.format("[%s] %s | Kategori: %s | Harga: Rp %,.2f | Stok: %d", 
                kode, nama, getKategori(), harga, stok);
    }
}
