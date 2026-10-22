/**
 * ==========================================================================
 * APLIKASI WEB KASIR JOSJISMART - STANDALONE FRONTEND
 * ==========================================================================
 * Website kasir ini berjalan mandiri (dapat dibuka langsung via index.html)
 * dengan penyimpanan persisten browser (localStorage).
 */

// Data Awal Produk (Sesuai Kelas Model Makanan & Elektronik di Java)
const DATA_AWAL_PRODUK = [
    { kode: "M01", nama: "Indomie Goreng", kategori: "Makanan & Minuman", harga: 3500, stok: 50, info: "Exp: 2026-12-31" },
    { kode: "M02", nama: "Susu Ultra Milk 250ml", kategori: "Makanan & Minuman", harga: 7000, stok: 30, info: "Exp: 2026-08-15" },
    { kode: "M03", nama: "Roti Tawar Sari Roti", kategori: "Makanan & Minuman", harga: 15000, stok: 20, info: "Exp: 2026-05-10" },
    { kode: "E01", nama: "Mouse Wireless Logitech", kategori: "Elektronik", harga: 175000, stok: 15, info: "Garansi: 12 bln" },
    { kode: "E02", nama: "Keyboard Mechanical RGB", kategori: "Elektronik", harga: 450000, stok: 10, info: "Garansi: 24 bln" },
    { kode: "E03", nama: "Headset Bluetooth Sony", kategori: "Elektronik", harga: 650000, stok: 8, info: "Garansi: 12 bln" }
];

let daftarKatalog = [];
let daftarRiwayat = [];
let keranjangBelanja = {}; // { kode: { produk, qty } }
let counterTrx = 1001;

// Inisialisasi Aplikasi Saat Halaman Dibuka
document.addEventListener('DOMContentLoaded', () => {
    muatDataLokal();
    renderTabelPOS(daftarKatalog);
    renderTabelCRUD(daftarKatalog);
    renderTabelRiwayat(daftarRiwayat);
});

// ==========================================================================
// 1. MANAJEMEN PENYIMPANAN LOCALSTORAGE
// ==========================================================================
function muatDataLokal() {
    const savedKatalog = localStorage.getItem('josji_katalog');
    if (savedKatalog) {
        try {
            daftarKatalog = JSON.parse(savedKatalog);
        } catch (e) {
            daftarKatalog = [...DATA_AWAL_PRODUK];
        }
    } else {
        daftarKatalog = [...DATA_AWAL_PRODUK];
        simpanKatalogLokal();
    }

    const savedRiwayat = localStorage.getItem('josji_riwayat');
    if (savedRiwayat) {
        try {
            daftarRiwayat = JSON.parse(savedRiwayat);
            if (daftarRiwayat.length > 0) {
                counterTrx = 1001 + daftarRiwayat.length;
            }
        } catch (e) {
            daftarRiwayat = [];
        }
    }
}

function simpanKatalogLokal() {
    localStorage.setItem('josji_katalog', JSON.stringify(daftarKatalog));
}

function simpanRiwayatLokal() {
    localStorage.setItem('josji_riwayat', JSON.stringify(daftarRiwayat));
}

// ==========================================================================
// 2. NAVIGASI TAB
// ==========================================================================
function gantiTab(tabId) {
    document.querySelectorAll('.nav-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-pane').forEach(pane => pane.classList.remove('active'));

    event.target.classList.add('active');
    document.getElementById('tab-' + tabId).classList.add('active');

    if (tabId === 'pos') {
        renderTabelPOS(daftarKatalog);
    } else if (tabId === 'crud') {
        renderTabelCRUD(daftarKatalog);
    } else if (tabId === 'riwayat') {
        renderTabelRiwayat(daftarRiwayat);
    }
}

// ==========================================================================
// 3. RENDER TABEL POS & FILTER PENCARIAN
// ==========================================================================
function renderTabelPOS(list) {
    const tbody = document.getElementById('tabel-pos-body');
    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align:center; color:#94a3b8; padding:20px;">Tidak ada produk yang cocok</td></tr>';
        return;
    }

    tbody.innerHTML = list.map(p => {
        const isMakanan = p.kategori.includes('Makanan');
        const badgeClass = isMakanan ? 'badge-food' : 'badge-elec';
        const habis = p.stok <= 0;
        const disabled = habis ? 'disabled' : '';
        const btnText = habis ? 'Habis' : '+ Tambah';

        return `
            <tr>
                <td><strong>${p.kode}</strong></td>
                <td>${p.nama}</td>
                <td><span class="badge ${badgeClass}">${p.kategori}</span></td>
                <td>Rp ${p.harga.toLocaleString('id-ID')}</td>
                <td><strong>${p.stok}</strong></td>
                <td><small style="color:#64748b;">${p.info || '-'}</small></td>
                <td>
                    <button class="btn btn-sm btn-success" ${disabled} onclick="tambahKeKeranjang('${p.kode}')">
                        ${btnText}
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

function filterProdukPOS() {
    const keyword = document.getElementById('pos-search').value.toLowerCase().trim();
    const kategori = document.getElementById('pos-kategori').value;

    const filtered = daftarKatalog.filter(p => {
        const matchKeyword = p.nama.toLowerCase().includes(keyword) || p.kode.toLowerCase().includes(keyword);
        const matchKategori = (kategori === 'semua') || p.kategori.toLowerCase().includes(kategori.toLowerCase());
        return matchKeyword && matchKategori;
    });

    renderTabelPOS(filtered);
}

// ==========================================================================
// 4. KERANJANG BELANJA & PEMBAYARAN KASIR (POS)
// ==========================================================================
function tambahKeKeranjang(kode) {
    const produk = daftarKatalog.find(p => p.kode === kode);
    if (!produk) return;

    const currentQty = keranjangBelanja[kode] ? keranjangBelanja[kode].qty : 0;
    if (currentQty + 1 > produk.stok) {
        alert('Stok produk ' + produk.nama + ' tidak mencukupi (sisa: ' + produk.stok + ')!');
        return;
    }

    if (keranjangBelanja[kode]) {
        keranjangBelanja[kode].qty++;
    } else {
        keranjangBelanja[kode] = { produk, qty: 1 };
    }

    renderKeranjang();
}

function ubahQty(kode, delta) {
    if (!keranjangBelanja[kode]) return;

    const baru = keranjangBelanja[kode].qty + delta;
    if (baru <= 0) {
        hapusItem(kode);
        return;
    }

    const produkAsli = daftarKatalog.find(p => p.kode === kode);
    if (baru > (produkAsli ? produkAsli.stok : 0)) {
        alert('Jumlah pembelian melebihi stok tersedia!');
        return;
    }

    keranjangBelanja[kode].qty = baru;
    renderKeranjang();
}

function hapusItem(kode) {
    delete keranjangBelanja[kode];
    renderKeranjang();
}

function kosongkanKeranjang() {
    keranjangBelanja = {};
    renderKeranjang();
    document.getElementById('input-bayar').value = '';
    document.getElementById('input-voucher').value = '0';
    document.getElementById('lbl-kembalian').innerText = 'Rp 0';
}

function renderKeranjang() {
    const tbody = document.getElementById('keranjang-body');
    const keys = Object.keys(keranjangBelanja);

    if (keys.length === 0) {
        tbody.innerHTML = '<tr class="empty-row"><td colspan="4">Keranjang belanja masih kosong</td></tr>';
        document.getElementById('lbl-total-kotor').innerText = 'Rp 0';
        document.getElementById('lbl-total-diskon').innerText = '-Rp 0';
        document.getElementById('lbl-total-akhir').innerText = 'Rp 0';
        hitungKembalianRealtime();
        return;
    }

    let subtotalKotor = 0;
    let diskonProduk = 0;

    tbody.innerHTML = keys.map(k => {
        const item = keranjangBelanja[k];
        const p = item.produk;
        const totalBaris = p.harga * item.qty;

        // Aturan Polimorfisme Diskon
        let diskonItem = 0;
        if (p.kategori.includes('Makanan') && item.qty >= 5) {
            diskonItem = totalBaris * 0.10; // Diskon 10%
        } else if (p.kategori.includes('Elektronik') && totalBaris >= 500000) {
            diskonItem = totalBaris * 0.05; // Diskon 5%
        }

        subtotalKotor += totalBaris;
        diskonProduk += diskonItem;

        return `
            <tr>
                <td>
                    <strong>${p.nama}</strong><br>
                    <small style="color:#64748b;">Rp ${p.harga.toLocaleString('id-ID')}</small>
                </td>
                <td>
                    <div class="qty-control">
                        <button class="qty-btn" onclick="ubahQty('${k}', -1)">-</button>
                        <span>${item.qty}</span>
                        <button class="qty-btn" onclick="ubahQty('${k}', 1)">+</button>
                    </div>
                </td>
                <td>Rp ${totalBaris.toLocaleString('id-ID')}</td>
                <td>
                    <button class="cart-del-btn" onclick="hapusItem('${k}')">&times;</button>
                </td>
            </tr>
        `;
    }).join('');

    document.getElementById('lbl-total-kotor').innerText = 'Rp ' + subtotalKotor.toLocaleString('id-ID');
    document.getElementById('lbl-total-diskon').innerText = '-Rp ' + diskonProduk.toLocaleString('id-ID');

    hitungTotalPembayaran();
}

function hitungTotalPembayaran() {
    let subtotalKotor = 0;
    let diskonProduk = 0;

    Object.keys(keranjangBelanja).forEach(k => {
        const item = keranjangBelanja[k];
        const total = item.produk.harga * item.qty;
        let diskon = 0;
        if (item.produk.kategori.includes('Makanan') && item.qty >= 5) {
            diskon = total * 0.10;
        } else if (item.produk.kategori.includes('Elektronik') && total >= 500000) {
            diskon = total * 0.05;
        }
        subtotalKotor += total;
        diskonProduk += diskon;
    });

    const voucher = parseFloat(document.getElementById('input-voucher').value) || 0;
    const totalAkhir = Math.max(0, subtotalKotor - diskonProduk - voucher);

    document.getElementById('lbl-total-akhir').innerText = 'Rp ' + totalAkhir.toLocaleString('id-ID');
    hitungKembalianRealtime();
    return totalAkhir;
}

function hitungKembalianRealtime() {
    const totalAkhir = hitungTotalPembayaran();
    const uangBayar = parseFloat(document.getElementById('input-bayar').value) || 0;
    const lblKembalian = document.getElementById('lbl-kembalian');

    if (uangBayar >= totalAkhir && totalAkhir > 0) {
        const kembalian = uangBayar - totalAkhir;
        lblKembalian.innerText = 'Rp ' + kembalian.toLocaleString('id-ID');
        lblKembalian.className = 'font-bold text-success';
    } else {
        lblKembalian.innerText = 'Rp 0';
        lblKembalian.className = 'font-bold';
    }
}

function prosesCheckout() {
    const keys = Object.keys(keranjangBelanja);
    if (keys.length === 0) {
        alert('Keranjang belanja masih kosong! Silakan pilih produk terlebih dahulu.');
        return;
    }

    const totalAkhir = hitungTotalPembayaran();
    const uangBayar = parseFloat(document.getElementById('input-bayar').value) || 0;

    if (uangBayar < totalAkhir) {
        alert(`Uang pembayaran kurang!\nTotal Harus Dibayar: Rp ${totalAkhir.toLocaleString('id-ID')}\nUang Diterima: Rp ${uangBayar.toLocaleString('id-ID')}`);
        return;
    }

    const kasirNama = document.getElementById('input-kasir').value || 'Kasir 1';
    const voucher = parseFloat(document.getElementById('input-voucher').value) || 0;
    const kembalian = uangBayar - totalAkhir;
    const idTrx = "TRX-" + (counterTrx++);

    const now = new Date();
    const waktu = now.toLocaleDateString('id-ID') + ' ' + now.toLocaleTimeString('id-ID');

    // Potong stok produk di katalog
    let totalItems = 0;
    keys.forEach(k => {
        const item = keranjangBelanja[k];
        totalItems += item.qty;
        const p = daftarKatalog.find(prod => prod.kode === k);
        if (p) {
            p.stok = Math.max(0, p.stok - item.qty);
        }
    });
    simpanKatalogLokal();

    // Buat Struk
    const strukText = generateStrukText(idTrx, waktu, kasirNama, totalAkhir, uangBayar, kembalian, voucher);

    // Simpan ke Riwayat Transaksi
    daftarRiwayat.unshift({
        id: idTrx,
        waktu: waktu,
        kasir: kasirNama,
        itemCount: totalItems,
        total: totalAkhir,
        struk: strukText
    });
    simpanRiwayatLokal();

    // Tampilkan Modal Struk
    document.getElementById('struk-konten').innerText = strukText;
    bukaModal('modal-struk');

    // Reset keranjang & perbarui UI
    kosongkanKeranjang();
    renderTabelPOS(daftarKatalog);
    renderTabelCRUD(daftarKatalog);
    renderTabelRiwayat(daftarRiwayat);
}

function generateStrukText(idTrx, waktu, kasir, totalAkhir, bayar, kembali, voucher) {
    let struk = "=======================================================\n";
    struk += "                 STRUK PEMBELIAN KASIR                \n";
    struk += "                   KASIR JOSJISMART                   \n";
    struk += "=======================================================\n";
    struk += ` No. Transaksi : ${idTrx}\n`;
    struk += ` Waktu         : ${waktu}\n`;
    struk += ` Kasir         : ${kasir}\n`;
    struk += "-------------------------------------------------------\n";
    struk += "Item                 Qty        Harga        Subtotal\n";
    struk += "-------------------------------------------------------\n";

    let totalKotor = 0;
    let totalDiskonProduk = 0;

    Object.keys(keranjangBelanja).forEach(k => {
        const item = keranjangBelanja[k];
        const p = item.produk;
        const sub = p.harga * item.qty;
        totalKotor += sub;

        let diskon = 0;
        if (p.kategori.includes('Makanan') && item.qty >= 5) {
            diskon = sub * 0.10;
        } else if (p.kategori.includes('Elektronik') && sub >= 500000) {
            diskon = sub * 0.05;
        }
        totalDiskonProduk += diskon;

        const namaP = p.nama.length > 18 ? p.nama.substring(0, 18) : p.nama;
        struk += `${namaP.padEnd(18)} ${String(item.qty).padStart(4)} ${p.harga.toLocaleString('id-ID').padStart(12)} ${sub.toLocaleString('id-ID').padStart(15)}\n`;

        if (diskon > 0) {
            struk += `  -> Diskon Promo (${p.kategori}) : -Rp ${diskon.toLocaleString('id-ID')}\n`;
        }
    });

    struk += "-------------------------------------------------------\n";
    struk += ` Total Kotor         : Rp ${totalKotor.toLocaleString('id-ID').padStart(15)}\n`;
    if (totalDiskonProduk > 0) {
        struk += ` Diskon Promo Produk : -Rp ${totalDiskonProduk.toLocaleString('id-ID').padStart(14)}\n`;
    }
    if (voucher > 0) {
        struk += ` Diskon Voucher      : -Rp ${voucher.toLocaleString('id-ID').padStart(14)}\n`;
    }
    struk += "-------------------------------------------------------\n";
    struk += ` TOTAL AKHIR         : Rp ${totalAkhir.toLocaleString('id-ID').padStart(15)}\n`;
    struk += ` TUNAI               : Rp ${bayar.toLocaleString('id-ID').padStart(15)}\n`;
    struk += ` KEMBALI             : Rp ${kembali.toLocaleString('id-ID').padStart(15)}\n`;
    struk += "=======================================================\n";
    struk += "            Terima Kasih Atas Kunjungan Anda!          \n";
    struk += "=======================================================\n";

    return struk;
}

function cetakStrukKertas() {
    window.print();
}

// ==========================================================================
// 5. MASTER DATA PRODUK (CRUD)
// ==========================================================================
function renderTabelCRUD(list) {
    const tbody = document.getElementById('tabel-crud-body');
    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align:center; color:#94a3b8; padding:20px;">Belum ada produk terdaftar</td></tr>';
        return;
    }

    tbody.innerHTML = list.map(p => {
        const isMakanan = p.kategori.includes('Makanan');
        const badgeClass = isMakanan ? 'badge-food' : 'badge-elec';

        return `
            <tr>
                <td><strong>${p.kode}</strong></td>
                <td>${p.nama}</td>
                <td><span class="badge ${badgeClass}">${p.kategori}</span></td>
                <td>Rp ${p.harga.toLocaleString('id-ID')}</td>
                <td>${p.stok}</td>
                <td><small>${p.info || '-'}</small></td>
                <td>
                    <button class="btn btn-sm btn-secondary" onclick="bukaModalEdit('${p.kode}')">Ubah</button>
                    <button class="btn btn-sm btn-outline-danger" onclick="hapusProduk('${p.kode}')">Hapus</button>
                </td>
            </tr>
        `;
    }).join('');
}

function bukaModalTambah(tipe) {
    document.getElementById('form-mode').value = 'tambah';
    document.getElementById('form-tipe').value = tipe;
    document.getElementById('modal-produk-title').innerText = tipe === 'makanan' ? 'Tambah Produk Makanan' : 'Tambah Produk Elektronik';

    const lblKhusus = document.getElementById('lbl-khusus');
    const inputKhusus = document.getElementById('prod-khusus');

    if (tipe === 'makanan') {
        lblKhusus.innerText = 'Tanggal Kadaluarsa (YYYY-MM-DD)';
        inputKhusus.placeholder = 'Contoh: 2026-12-31';
    } else {
        lblKhusus.innerText = 'Masa Garansi (Bulan)';
        inputKhusus.placeholder = 'Contoh: 12';
    }

    document.getElementById('prod-kode').value = '';
    document.getElementById('prod-kode').readOnly = false;
    document.getElementById('prod-nama').value = '';
    document.getElementById('prod-harga').value = '';
    document.getElementById('prod-stok').value = '';
    inputKhusus.value = '';
    document.getElementById('group-khusus').style.display = 'block';

    bukaModal('modal-produk');
}

function bukaModalEdit(kode) {
    const p = daftarKatalog.find(item => item.kode === kode);
    if (!p) return;

    document.getElementById('form-mode').value = 'edit';
    document.getElementById('modal-produk-title').innerText = 'Ubah Data Produk ' + p.kode;

    document.getElementById('prod-kode').value = p.kode;
    document.getElementById('prod-kode').readOnly = true;
    document.getElementById('prod-nama').value = p.nama;
    document.getElementById('prod-harga').value = p.harga;
    document.getElementById('prod-stok').value = p.stok;

    document.getElementById('group-khusus').style.display = 'none';
    bukaModal('modal-produk');
}

function simpanDataProduk(e) {
    e.preventDefault();
    const mode = document.getElementById('form-mode').value;
    const kode = document.getElementById('prod-kode').value.trim().toUpperCase();
    const nama = document.getElementById('prod-nama').value.trim();
    const harga = parseFloat(document.getElementById('prod-harga').value);
    const stok = parseInt(document.getElementById('prod-stok').value);
    const khusus = document.getElementById('prod-khusus').value.trim();
    const tipe = document.getElementById('form-tipe').value;

    if (mode === 'tambah') {
        if (daftarKatalog.some(p => p.kode === kode)) {
            alert('Kode produk "' + kode + '" sudah terdaftar! Gunakan kode lain.');
            return;
        }

        const kategori = tipe === 'makanan' ? 'Makanan & Minuman' : 'Elektronik';
        const info = tipe === 'makanan' ? ('Exp: ' + (khusus || '-')) : ('Garansi: ' + (khusus || '0') + ' bln');

        daftarKatalog.push({ kode, nama, kategori, harga, stok, info });
    } else {
        const index = daftarKatalog.findIndex(p => p.kode === kode);
        if (index !== -1) {
            daftarKatalog[index].nama = nama;
            daftarKatalog[index].harga = harga;
            daftarKatalog[index].stok = stok;
        }
    }

    simpanKatalogLokal();
    tutupModal('modal-produk');
    renderTabelPOS(daftarKatalog);
    renderTabelCRUD(daftarKatalog);
    alert('Data produk berhasil disimpan!');
}

function hapusProduk(kode) {
    if (!confirm(`Apakah Anda yakin ingin menghapus produk '${kode}' dari sistem?`)) {
        return;
    }

    daftarKatalog = daftarKatalog.filter(p => p.kode !== kode);
    delete keranjangBelanja[kode];

    simpanKatalogLokal();
    renderTabelPOS(daftarKatalog);
    renderTabelCRUD(daftarKatalog);
    renderKeranjang();
}

// ==========================================================================
// 6. RIWAYAT TRANSAKSI
// ==========================================================================
function renderTabelRiwayat(list) {
    const tbody = document.getElementById('tabel-riwayat-body');
    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; color:#94a3b8; padding:20px;">Belum ada riwayat transaksi penjualan</td></tr>';
        document.getElementById('lbl-grand-omset').innerText = 'Rp 0';
        return;
    }

    let grandOmset = 0;
    tbody.innerHTML = list.map(t => {
        grandOmset += t.total;
        return `
            <tr>
                <td><strong>${t.id}</strong></td>
                <td>${t.waktu}</td>
                <td>${t.kasir}</td>
                <td>${t.itemCount} unit</td>
                <td><strong>Rp ${t.total.toLocaleString('id-ID')}</strong></td>
                <td>
                    <button class="btn btn-sm btn-info" onclick="lihatStrukLama('${t.id}')">Lihat Struk</button>
                </td>
            </tr>
        `;
    }).join('');

    document.getElementById('lbl-grand-omset').innerText = 'Rp ' + grandOmset.toLocaleString('id-ID');
}

function lihatStrukLama(idTrx) {
    const trx = daftarRiwayat.find(t => t.id === idTrx);
    if (trx && trx.struk) {
        document.getElementById('struk-konten').innerText = trx.struk;
        bukaModal('modal-struk');
    }
}

// ==========================================================================
// 7. HELPER MODAL
// ==========================================================================
function bukaModal(id) {
    document.getElementById(id).classList.add('active');
}

function tutupModal(id) {
    document.getElementById(id).classList.remove('active');
    document.getElementById('group-khusus').style.display = 'block';
}
