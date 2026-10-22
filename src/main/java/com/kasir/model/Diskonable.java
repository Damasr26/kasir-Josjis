package com.kasir.model;

/**
 * =========================================================================
 * KONSEP OOP: ABSTRAKSI (INTERFACE)
 * =========================================================================
 * Interface Diskonable mendefinisikan kontrak (blueprint) perilaku perhitungan diskon.
 * Setiap kelas yang mengimplementasikan interface ini wajib menyediakan logika
 * perhitungan diskon sesuai karakteristik masing-masing produk.
 */
public interface Diskonable {
    
    /**
     * Menghitung total potongan harga (diskon) berdasarkan jumlah yang dibeli.
     * @param jumlah kuantitas produk yang dibeli
     * @return nilai diskon dalam rupiah
     */
    double hitungDiskon(int jumlah);
}
