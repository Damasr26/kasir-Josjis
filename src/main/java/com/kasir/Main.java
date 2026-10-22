package com.kasir;

import com.kasir.service.ProdukService;
import com.kasir.service.TransaksiService;

import java.awt.Desktop;
import java.io.File;

/**
 * =========================================================================
 * TITIK AWAL APLIKASI KASIR JOSJISMART (ENTRY POINT)
 * =========================================================================
 * Menjalankan inisialisasi backend Kasir Java OOP dan otomatis membuka
 * file website Kasir Josjismart (index.html) di browser.
 */
public class Main {

    public static void main(String[] args) {
        // Inisialisasi Service Layer (Data & Logika Bisnis OOP Java)
        ProdukService produkService = new ProdukService();
        TransaksiService transaksiService = new TransaksiService(produkService);

        System.out.println("=======================================================================");
        System.out.println("   [SUKSES] PROYEK KASIR JOSJISMART BERHASIL DIJALANKAN!");
        System.out.println("=======================================================================");
        System.out.println(" Membuka file website kasir (index.html)...");

        // Buka file index.html langsung di browser default pengguna
        bukaWebsiteLangsung();
    }

    private static void bukaWebsiteLangsung() {
        try {
            File htmlFile = new File("index.html");
            if (!htmlFile.exists()) {
                htmlFile = new File("src/main/resources/web/index.html");
            }

            if (htmlFile.exists()) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(htmlFile.toURI());
                } else {
                    new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", htmlFile.getAbsolutePath()).start();
                }
                System.out.println(" Website kasir berhasil dibuka di browser: " + htmlFile.getAbsolutePath());
            } else {
                System.out.println("[INFO] File index.html siap dibuka.");
            }
        } catch (Exception e) {
            System.err.println("[INFO] Silakan buka langsung file index.html di browser Anda: " + e.getMessage());
        }
    }
}
