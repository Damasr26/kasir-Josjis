package com.kasir.service;

import com.kasir.model.ItemTransaksi;
import com.kasir.model.Produk;
import com.kasir.model.Transaksi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * =========================================================================
 * SERVICE: TRANSAKSI SERVICE (POINT OF SALES / POS ENGINE)
 * =========================================================================
 * Mengelola alur transaksi kasir, pengurangan stok produk secara otomatis,
 * riwayat penjualan, dan pencetakan struk.
 */
public class TransaksiService {

    private final ProdukService produkService;
    private final List<Transaksi> riwayatTransaksi;
    private int counterId = 1001;

    public TransaksiService(ProdukService produkService) {
        this.produkService = produkService;
        this.riwayatTransaksi = new ArrayList<>();
    }

    /**
     * Membuat sesi transaksi baru untuk kasir.
     */
    public Transaksi buatTransaksiBaru(String namaKasir) {
        String idTrx = "TRX-" + (counterId++);
        return new Transaksi(idTrx, namaKasir);
    }

    /**
     * Memvalidasi dan menambahkan produk ke dalam keranjang transaksi.
     */
    public boolean tambahKeKeranjang(Transaksi transaksi, String kodeProduk, int jumlah) {
        if (transaksi == null || transaksi.isSudahSelesai()) {
            System.out.println("[GAGAL] Transaksi tidak valid atau sudah selesai.");
            return false;
        }

        Produk produk = produkService.cariProduk(kodeProduk);
        if (produk == null) {
            System.out.println("[GAGAL] Produk dengan kode '" + kodeProduk + "' tidak ditemukan!");
            return false;
        }

        // Hitung total kuantitas jika barang yang sama sudah ada di keranjang
        int qtyDiKeranjang = 0;
        for (ItemTransaksi item : transaksi.getDaftarItem()) {
            if (item.getProduk().getKode().equalsIgnoreCase(produk.getKode())) {
                qtyDiKeranjang += item.getJumlah();
            }
        }

        if (produk.getStok() < (qtyDiKeranjang + jumlah)) {
            System.out.printf("[GAGAL] Stok tidak mencukupi! Sisa stok: %d, di keranjang: %d, diminta: %d\n",
                    produk.getStok(), qtyDiKeranjang, jumlah);
            return false;
        }

        // Memanggil overload tambahItem
        transaksi.tambahItem(produk, jumlah);
        System.out.printf("[SUKSES] Berhasil menambahkan %dx %s ke keranjang.\n", jumlah, produk.getNama());
        return true;
    }

    /**
     * Memproses checkout: memotong stok fisik dan menyimpan transaksi ke riwayat.
     */
    public boolean selesaikanPembayaran(Transaksi transaksi, double uangBayar, double diskonVoucher) {
        if (transaksi == null || transaksi.getDaftarItem().isEmpty()) {
            System.out.println("[GAGAL] Keranjang belanja kosong.");
            return false;
        }

        try {
            // Proses bayar (manfaatkan polimorfisme overloading)
            if (diskonVoucher > 0) {
                transaksi.bayar(uangBayar, diskonVoucher);
            } else {
                transaksi.bayar(uangBayar);
            }

            // Potong stok produk di inventaris secara resmi
            for (ItemTransaksi item : transaksi.getDaftarItem()) {
                item.getProduk().kurangiStok(item.getJumlah());
            }

            // Simpan ke riwayat
            riwayatTransaksi.add(transaksi);

            // Cetak struk pembelian
            transaksi.cetakStruk();
            return true;

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("[GAGAL PEMBAYARAN] " + e.getMessage());
            return false;
        }
    }

    public List<Transaksi> getRiwayatTransaksi() {
        return Collections.unmodifiableList(riwayatTransaksi);
    }

    /**
     * Menampilkan rekap ringkas riwayat transaksi kasir.
     */
    public void tampilkanRiwayat() {
        if (riwayatTransaksi.isEmpty()) {
            System.out.println("\n[!] Belum ada riwayat transaksi yang tercatat.");
            return;
        }

        System.out.println("\n" + "=".repeat(75));
        System.out.printf("| %-12s | %-15s | %-10s | %-25s |\n",
                "ID TRANSAKSI", "KASIR", "JML ITEM", "TOTAL AKHIR");
        System.out.println("=".repeat(75));

        double grandTotal = 0;
        for (Transaksi t : riwayatTransaksi) {
            int totalQty = t.getDaftarItem().stream().mapToInt(ItemTransaksi::getJumlah).sum();
            double totalAkhir = t.hitungTotalAkhir();
            grandTotal += totalAkhir;

            System.out.printf("| %-12s | %-15s | %-10d | Rp %,22.2f |\n",
                    t.getIdTransaksi(), t.getNamaKasir(), totalQty, totalAkhir);
        }
        System.out.println("=".repeat(75));
        System.out.printf(" TOTAL PENDAPATAN : Rp %,25.2f\n", grandTotal);
        System.out.println("=".repeat(75));
    }
}
