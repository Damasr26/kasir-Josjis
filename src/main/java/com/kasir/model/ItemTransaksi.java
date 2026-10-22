package com.kasir.model;

/**
 * =========================================================================
 * KONSEP OOP: ENKAPSULASI & POLIMORFISME PADA PENGGUNAAN OBJEK PRODUK
 * =========================================================================
 * ItemTransaksi menyimpan relasi antara Produk yang dibeli dan jumlahnya.
 * Memanfaatkan polimorfisme saat memanggil getProduk().hitungDiskon().
 */
public class ItemTransaksi {

    private Produk produk;
    private int jumlah;

    public ItemTransaksi(Produk produk, int jumlah) {
        if (produk == null) {
            throw new IllegalArgumentException("Produk tidak boleh bernilai null!");
        }
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah pembelian harus lebih dari 0!");
        }
        this.produk = produk;
        this.jumlah = jumlah;
    }

    public Produk getProduk() {
        return produk;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah pembelian harus lebih dari 0!");
        }
        this.jumlah = jumlah;
    }

    public double getSubtotalKotor() {
        return produk.getHarga() * jumlah;
    }

    /**
     * POLIMORFISME DI SINI:
     * Metode hitungDiskon() dipanggil secara dinamis (runtime polymorphism).
     * Jika produk adalah Makanan, logika Makanan yang jalan.
     * Jika produk adalah Elektronik, logika Elektronik yang jalan.
     */
    public double getDiskon() {
        return produk.hitungDiskon(jumlah);
    }

    public double getSubtotalBersih() {
        return getSubtotalKotor() - getDiskon();
    }
}
