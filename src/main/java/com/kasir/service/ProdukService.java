package com.kasir.service;

import com.kasir.model.Elektronik;
import com.kasir.model.Makanan;
import com.kasir.model.Produk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * =========================================================================
 * SERVICE: PRODUK SERVICE (LOGIKA BISNIS & OPERASI CRUD)
 * =========================================================================
 * Mengelola penyimpanan dan manipulasi data produk (Create, Read, Update, Delete).
 * Memanfaatkan polimorfisme koleksi: List<Produk> dapat menampung objek Makanan
 * maupun Elektronik.
 */
public class ProdukService {

    // === KOLEKSI POLIMORFISME (LIST OF PRODUK MENAMPUNG SUBCLASS MAKANAN & ELEKTRONIK) ===
    private final List<Produk> listProduk;

    public ProdukService() {
        this.listProduk = new ArrayList<>();
        inisialisasiDataAwal(); // Memberikan data awal untuk kemudahan pengujian
    }

    /**
     * Mengisi beberapa data awal secara otomatis agar program langsung dapat diuji.
     */
    private void inisialisasiDataAwal() {
        // Objek Makanan (Pewarisan & Polimorfisme)
        listProduk.add(new Makanan("M01", "Indomie Goreng", 3500, 50, "2026-12-31"));
        listProduk.add(new Makanan("M02", "Susu Ultra Milk 250ml", 7000, 30, "2026-08-15"));
        listProduk.add(new Makanan("M03", "Roti Tawar Sari Roti", 15000, 20, "2026-05-10"));

        // Objek Elektronik (Pewarisan & Polimorfisme)
        listProduk.add(new Elektronik("E01", "Mouse Wireless Logitech", 175000, 15, 12));
        listProduk.add(new Elektronik("E02", "Keyboard Mechanical RGB", 450000, 10, 24));
        listProduk.add(new Elektronik("E03", "Headset Bluetooth Sony", 650000, 8, 12));
    }

    // === 1. CREATE (TAMBAH PRODUK) ===
    public boolean tambahProduk(Produk produkBaru) {
        if (produkBaru == null) {
            throw new IllegalArgumentException("Produk tidak boleh kosong!");
        }
        if (cariProduk(produkBaru.getKode()) != null) {
            System.out.println("[GAGAL] Produk dengan kode " + produkBaru.getKode() + " sudah terdaftar!");
            return false;
        }
        listProduk.add(produkBaru);
        return true;
    }

    // === 2. READ (BACA / CARI PRODUK) ===
    public List<Produk> getSemuaProduk() {
        return Collections.unmodifiableList(listProduk);
    }

    public Produk cariProduk(String kode) {
        if (kode == null) return null;
        for (Produk p : listProduk) {
            if (p.getKode().equalsIgnoreCase(kode.trim())) {
                return p;
            }
        }
        return null;
    }

    // === 3. UPDATE (UBAH DATA PRODUK) ===
    public boolean updateProduk(String kode, String namaBaru, double hargaBaru, int stokBaru) {
        Produk p = cariProduk(kode);
        if (p == null) {
            System.out.println("[GAGAL] Produk dengan kode " + kode + " tidak ditemukan!");
            return false;
        }

        p.setNama(namaBaru);
        p.setHarga(hargaBaru);
        p.setStok(stokBaru);
        return true;
    }

    // === 4. DELETE (HAPUS PRODUK) ===
    public boolean hapusProduk(String kode) {
        Produk p = cariProduk(kode);
        if (p == null) {
            System.out.println("[GAGAL] Produk dengan kode " + kode + " tidak ditemukan!");
            return false;
        }
        return listProduk.remove(p);
    }

    /**
     * Menampilkan seluruh daftar produk dalam format tabel rapi.
     */
    public void tampilkanSemuaProduk() {
        if (listProduk.isEmpty()) {
            System.out.println("\n[!] Belum ada produk yang terdaftar di sistem.");
            return;
        }

        System.out.println("\n" + "=".repeat(95));
        System.out.printf("| %-8s | %-25s | %-20s | %-14s | %-6s | %-15s |\n",
                "KODE", "NAMA PRODUK", "KATEGORI", "HARGA", "STOK", "INFO TAMBAHAN");
        System.out.println("=".repeat(95));

        for (Produk p : listProduk) {
            String info = "-";
            if (p instanceof Makanan) {
                info = "Exp: " + ((Makanan) p).getTanggalKadaluarsa();
            } else if (p instanceof Elektronik) {
                info = "Garansi: " + ((Elektronik) p).getGaransiBulan() + " bln";
            }

            System.out.printf("| %-8s | %-25s | %-20s | Rp %,11.2f | %-6d | %-15s |\n",
                    p.getKode(),
                    p.getNama().length() > 25 ? p.getNama().substring(0, 22) + "..." : p.getNama(),
                    p.getKategori(),
                    p.getHarga(),
                    p.getStok(),
                    info);
        }
        System.out.println("=".repeat(95));
    }
}
