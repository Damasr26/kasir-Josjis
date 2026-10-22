package com.kasir.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * =========================================================================
 * KONSEP OOP: ENKAPSULASI & POLIMORFISME (METHOD OVERLOADING)
 * =========================================================================
 * Transaksi mengelola keranjang belanja dan proses pembayaran kasir (POS).
 * Menunjukkan Method Overloading pada metode 'tambahItem' dan 'bayar'.
 */
public class Transaksi {

    private String idTransaksi;
    private LocalDateTime tanggal;
    private String namaKasir;
    private List<ItemTransaksi> daftarItem;
    private double diskonTambahan; // Diskon voucher/member
    private double uangBayar;
    private double kembalian;
    private boolean sudahSelesai;

    public Transaksi(String idTransaksi, String namaKasir) {
        this.idTransaksi = idTransaksi;
        this.namaKasir = (namaKasir == null || namaKasir.trim().isEmpty()) ? "Kasir Utama" : namaKasir;
        this.tanggal = LocalDateTime.now();
        this.daftarItem = new ArrayList<>();
        this.diskonTambahan = 0.0;
        this.uangBayar = 0.0;
        this.kembalian = 0.0;
        this.sudahSelesai = false;
    }

    // === METHOD OVERLOADING #1 (POLIMORFISME STATIC / COMPILE-TIME) ===

    /**
     * Menambahkan item belanja dengan objek ItemTransaksi.
     */
    public void tambahItem(ItemTransaksi item) {
        if (sudahSelesai) {
            throw new IllegalStateException("Transaksi sudah selesai dan tidak dapat diubah!");
        }
        
        // Jika produk sudah ada di keranjang, akumulasikan jumlahnya
        for (ItemTransaksi existing : daftarItem) {
            if (existing.getProduk().getKode().equalsIgnoreCase(item.getProduk().getKode())) {
                existing.setJumlah(existing.getJumlah() + item.getJumlah());
                return;
            }
        }
        daftarItem.add(item);
    }

    /**
     * Overload: Menambahkan item belanja langsung menggunakan Produk dan Jumlah.
     */
    public void tambahItem(Produk produk, int jumlah) {
        tambahItem(new ItemTransaksi(produk, jumlah));
    }

    // === METODE PERHITUNGAN TOTAL TRANSAKSI ===

    public double hitungTotalKotor() {
        double total = 0.0;
        for (ItemTransaksi item : daftarItem) {
            total += item.getSubtotalKotor();
        }
        return total;
    }

    public double hitungTotalDiskonProduk() {
        double diskon = 0.0;
        for (ItemTransaksi item : daftarItem) {
            diskon += item.getDiskon();
        }
        return diskon;
    }

    public double hitungTotalAkhir() {
        double total = hitungTotalKotor() - hitungTotalDiskonProduk() - diskonTambahan;
        return Math.max(0.0, total);
    }

    // === METHOD OVERLOADING #2 (POLIMORFISME STATIC) PADA PEMBAYARAN ===

    /**
     * Memproses pembayaran standar tanpa diskon voucher.
     */
    public void bayar(double nominal) {
        bayar(nominal, 0.0);
    }

    /**
     * Overload: Memproses pembayaran dengan tambahan diskon voucher / promo.
     */
    public void bayar(double nominal, double diskonVoucher) {
        if (daftarItem.isEmpty()) {
            throw new IllegalStateException("Keranjang belanja masih kosong!");
        }
        if (diskonVoucher < 0) {
            throw new IllegalArgumentException("Diskon voucher tidak valid!");
        }
        this.diskonTambahan = diskonVoucher;
        double totalHarusDibayar = hitungTotalAkhir();

        if (nominal < totalHarusDibayar) {
            throw new IllegalArgumentException(String.format(
                    "Uang pembayaran kurang! Total: Rp %,.2f, Uang diterima: Rp %,.2f",
                    totalHarusDibayar, nominal));
        }

        this.uangBayar = nominal;
        this.kembalian = nominal - totalHarusDibayar;
        this.sudahSelesai = true;
    }

    // === GETTER (ENKAPSULASI) ===

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public LocalDateTime getTanggal() {
        return tanggal;
    }

    public String getNamaKasir() {
        return namaKasir;
    }

    public List<ItemTransaksi> getDaftarItem() {
        return Collections.unmodifiableList(daftarItem);
    }

    public double getDiskonTambahan() {
        return diskonTambahan;
    }

    public double getUangBayar() {
        return uangBayar;
    }

    public double getKembalian() {
        return kembalian;
    }

    public boolean isSudahSelesai() {
        return sudahSelesai;
    }

    // === CETAK STRUK STRUKTUR POS ===

    public void cetakStruk() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        System.out.println("\n=======================================================");
        System.out.println("                 STRUK PEMBELIAN KASIR                ");
        System.out.println("=======================================================");
        System.out.printf(" No. Transaksi : %s\n", idTransaksi);
        System.out.printf(" Waktu         : %s\n", tanggal.format(dtf));
        System.out.printf(" Kasir         : %s\n", namaKasir);
        System.out.println("-------------------------------------------------------");
        System.out.printf("%-18s %5s %12s %15s\n", "Item", "Qty", "Harga", "Subtotal");
        System.out.println("-------------------------------------------------------");

        for (ItemTransaksi item : daftarItem) {
            Produk p = item.getProduk();
            System.out.printf("%-18s %5d %,12.2f %,15.2f\n",
                    p.getNama().length() > 18 ? p.getNama().substring(0, 18) : p.getNama(),
                    item.getJumlah(),
                    p.getHarga(),
                    item.getSubtotalKotor());

            if (item.getDiskon() > 0) {
                System.out.printf("  -> Diskon Promo (%s) : -Rp %,.2f\n", 
                        p.getKategori(), item.getDiskon());
            }
        }

        System.out.println("-------------------------------------------------------");
        System.out.printf(" Total Kotor         : Rp %,15.2f\n", hitungTotalKotor());
        if (hitungTotalDiskonProduk() > 0) {
            System.out.printf(" Total Diskon Produk : -Rp %,14.2f\n", hitungTotalDiskonProduk());
        }
        if (diskonTambahan > 0) {
            System.out.printf(" Diskon Tambahan/Vcr : -Rp %,14.2f\n", diskonTambahan);
        }
        System.out.println("-------------------------------------------------------");
        System.out.printf(" TOTAL AKHIR         : Rp %,15.2f\n", hitungTotalAkhir());
        System.out.printf(" TUNAI               : Rp %,15.2f\n", uangBayar);
        System.out.printf(" KEMBALI             : Rp %,15.2f\n", kembalian);
        System.out.println("=======================================================");
        System.out.println("            Terima Kasih Atas Kunjungan Anda!          ");
        System.out.println("=======================================================\n");
    }
}
