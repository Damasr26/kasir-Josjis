package com.kasir.model;

/**
 * =========================================================================
 * KONSEP OOP: PEWARISAN (INHERITANCE) & POLIMORFISME (METHOD OVERRIDING)
 * =========================================================================
 * 1. Pewarisan: Kelas Elektronik mewarisi sifat dari kelas induk Produk.
 *    Memiliki atribut tambahan berupa 'garansiBulan'.
 * 2. Polimorfisme: Memiliki implementasi aturan diskon tersendiri yang berbeda
 *    dari kelas Makanan.
 */
public class Elektronik extends Produk {

    // === ATRIBUT SPESIFIK KELAS ELEKTRONIK (ENKAPSULASI) ===
    private int garansiBulan;

    // === KONSTRUKTOR DENGAN 'super' ===
    public Elektronik(String kode, String nama, double harga, int stok, int garansiBulan) {
        super(kode, nama, harga, stok);
        setGaransiBulan(garansiBulan);
    }

    public int getGaransiBulan() {
        return garansiBulan;
    }

    public void setGaransiBulan(int garansiBulan) {
        if (garansiBulan < 0) {
            throw new IllegalArgumentException("Masa garansi tidak boleh negatif!");
        }
        this.garansiBulan = garansiBulan;
    }

    // === POLIMORFISME: OVERRIDE METODE ===

    /**
     * Polimorfisme Aturan Diskon Elektronik:
     * Jika total belanja barang elektronik ini mencapai Rp 500.000 atau lebih,
     * pembeli mendapatkan diskon promo elektronik sebesar 5%.
     */
    @Override
    public double hitungDiskon(int jumlah) {
        double subtotal = getHarga() * jumlah;
        if (subtotal >= 500_000) {
            return subtotal * 0.05; // Diskon 5% untuk pembelian bernilai tinggi
        }
        return 0.0;
    }

    @Override
    public String getKategori() {
        return "Elektronik";
    }

    @Override
    public void tampilDetail() {
        System.out.printf("| %-8s | %-20s | %-15s | Rp %,10.2f | Stok: %-4d | Garansi: %-2d bln |\n",
                getKode(), getNama(), getKategori(), getHarga(), getStok(), garansiBulan);
    }
}
