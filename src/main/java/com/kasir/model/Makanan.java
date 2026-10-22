package com.kasir.model;

/**
 * =========================================================================
 * KONSEP OOP: PEWARISAN (INHERITANCE) & POLIMORFISME (METHOD OVERRIDING)
 * =========================================================================
 * 1. Pewarisan: Kelas Makanan mewarisi seluruh atribut dan metode dari kelas Produk (extends Produk).
 *    Memiliki atribut tambahan berupa 'tanggalKadaluarsa'.
 * 2. Polimorfisme: Meng-override metode 'hitungDiskon', 'getKategori', dan 'tampilDetail'
 *    dengan implementasi spesifik untuk produk makanan.
 */
public class Makanan extends Produk {

    // === ATRIBUT SPESIFIK KELAS MAKANAN (ENKAPSULASI) ===
    private String tanggalKadaluarsa;

    // === KONSTRUKTOR MENGGUNAKAN 'super' (PEWARISAN) ===
    public Makanan(String kode, String nama, double harga, int stok, String tanggalKadaluarsa) {
        super(kode, nama, harga, stok);
        setTanggalKadaluarsa(tanggalKadaluarsa);
    }

    public String getTanggalKadaluarsa() {
        return tanggalKadaluarsa;
    }

    public void setTanggalKadaluarsa(String tanggalKadaluarsa) {
        if (tanggalKadaluarsa == null || tanggalKadaluarsa.trim().isEmpty()) {
            this.tanggalKadaluarsa = "Tidak Tercatat";
        } else {
            this.tanggalKadaluarsa = tanggalKadaluarsa.trim();
        }
    }

    // === POLIMORFISME: OVERRIDE METODE DARI INTERFACE / SUPERCLASS ===

    /**
     * Polimorfisme Aturan Diskon Makanan:
     * Pembelian makanan sebanyak 5 unit atau lebih mendapatkan diskon 10%
     */
    @Override
    public double hitungDiskon(int jumlah) {
        double subtotal = getHarga() * jumlah;
        if (jumlah >= 5) {
            return subtotal * 0.10; // Diskon 10%
        }
        return 0.0;
    }

    @Override
    public String getKategori() {
        return "Makanan & Minuman";
    }

    @Override
    public void tampilDetail() {
        System.out.printf("| %-8s | %-20s | %-15s | Rp %,10.2f | Stok: %-4d | Exp: %-10s |\n",
                getKode(), getNama(), getKategori(), getHarga(), getStok(), tanggalKadaluarsa);
    }
}
