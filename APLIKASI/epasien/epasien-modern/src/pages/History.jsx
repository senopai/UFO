import React, { useState, useEffect } from 'react';
import { 
  FileText, 
  Search, 
  Download, 
  Printer, 
  ChevronRight, 
  Calendar, 
  FileSpreadsheet, 
  ShieldAlert,
  ArrowLeft,
  CheckCircle,
  AlertCircle
} from 'lucide-react';

export default function History({ user, onAddNotification, defaultTab, labHistory = [], radHistory = [], visitHistory = [] }) {
  const [activeSubTab, setActiveSubTab] = useState(defaultTab || 'lab'); // lab, rad, or kunjungan
  const [selectedItem, setSelectedItem] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [pendingAction, setPendingAction] = useState(null);

  const handleConfirmAction = () => {
    setShowConfirmModal(false);
    if (pendingAction) {
      pendingAction();
      setPendingAction(null);
    }
  };

  const handleCancelAction = () => {
    setShowConfirmModal(false);
    setPendingAction(null);
  };

  useEffect(() => {
    if (defaultTab) {
      setActiveSubTab(defaultTab);
      setSelectedItem(null);
    }
  }, [defaultTab]);

  const filteredHistory = (
    activeSubTab === 'lab' ? labHistory : 
    activeSubTab === 'rad' ? radHistory : 
    visitHistory
  ).filter(item => {
    if (activeSubTab === 'kunjungan') {
      return (
        item.clinic.toLowerCase().includes(searchQuery.toLowerCase()) || 
        item.doctor.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.diagnosa.toLowerCase().includes(searchQuery.toLowerCase())
      );
    }
    return (
      item.title.toLowerCase().includes(searchQuery.toLowerCase()) || 
      item.doctor.toLowerCase().includes(searchQuery.toLowerCase())
    );
  });

  const handlePrintReport = (item, type) => {
    const printWindow = window.open('', '_blank');
    if (!printWindow) {
      alert('Gagal membuka jendela cetak. Pastikan pop-up diizinkan di browser Anda.');
      return;
    }
    
    // Build print HTML content
    const htmlContent = `
      <html>
        <head>
          <title>Laporan ${type === 'lab' ? 'Laboratorium' : type === 'rad' ? 'Radiologi' : 'Kunjungan'} - ${user.name}</title>
          <style>
            body {
              font-family: Arial, sans-serif;
              padding: 40px;
              color: #333;
              line-height: 1.6;
            }
            .watermark-overlay {
              position: fixed;
              top: 0;
              left: 0;
              width: 100%;
              height: 100%;
              z-index: -1000;
              pointer-events: none;
              background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='400' height='220' viewBox='0 0 400 220'%3E%3Ctext x='200' y='110' fill='%23888888' fill-opacity='0.15' font-size='13' font-weight='900' font-family='Arial, sans-serif' text-anchor='middle' transform='rotate(-25 200 110)'%3ESALINAN REKAM MEDIS RESMI RSM MARDHATILLAH%3C/text%3E%3C/svg%3E");
              background-repeat: repeat;
              -webkit-print-color-adjust: exact;
              print-color-adjust: exact;
            }
            .header-table, .meta-table, .data-table {
              width: 100%;
              border-collapse: collapse;
              margin-bottom: 20px;
            }
            .header-title {
              text-align: center;
              font-size: 20px;
              font-weight: bold;
              margin-bottom: 30px;
              text-transform: uppercase;
              border-bottom: 3px double #333;
              padding-bottom: 10px;
            }
            .meta-table td {
              padding: 6px;
              font-size: 13px;
              vertical-align: top;
            }
            .meta-label {
              font-weight: bold;
              width: 20%;
            }
            .meta-value {
              width: 30%;
            }
            .data-table th, .data-table td {
              border: 1px solid #ddd;
              padding: 10px;
              font-size: 13px;
            }
            .data-table th {
              background-color: #f5f5f5;
              font-weight: bold;
              text-align: left;
            }
            .findings-box {
              border: 1px solid #ccc;
              background-color: #fafafa;
              padding: 15px;
              font-size: 13px;
              white-space: pre-wrap;
              margin-top: 10px;
              margin-bottom: 20px;
              border-radius: 8px;
            }
            .section-title {
              font-size: 14px;
              font-weight: bold;
              color: #059669;
              text-transform: uppercase;
              margin-top: 20px;
              margin-bottom: 8px;
              border-bottom: 1px solid #ddd;
              padding-bottom: 4px;
            }
            .badge {
              display: inline-block;
              padding: 3px 8px;
              border-radius: 12px;
              font-size: 10px;
              font-weight: bold;
              text-transform: uppercase;
            }
            .badge-success { background-color: #d1fae5; color: #065f46; }
            .badge-danger { background-color: #fee2e2; color: #991b1b; }
            .badge-warning { background-color: #fef3c7; color: #92400e; }
            .image-gallery {
              display: flex;
              flex-wrap: wrap;
              gap: 15px;
              margin-top: 15px;
            }
            .image-item {
              border: 1px solid #ddd;
              border-radius: 8px;
              overflow: hidden;
              max-width: 300px;
              margin-bottom: 10px;
            }
            .image-item img {
              width: 100%;
              height: auto;
              display: block;
            }
            .footer-info {
              margin-top: 50px;
              font-size: 11px;
              color: #777;
              text-align: center;
            }
            @media print {
              body { padding: 20px; }
              .no-print { display: none; }
              .watermark-overlay {
                -webkit-print-color-adjust: exact;
                print-color-adjust: exact;
              }
            }
          </style>
        </head>
        <body>
          <div class="watermark-overlay"></div>
          <div class="header-title">
            Hasil Pemeriksaan ${type === 'lab' ? 'Laboratorium' : type === 'rad' ? 'Radiologi' : 'Kunjungan'}
          </div>
          
          <table class="meta-table">
            <tr>
              <td class="meta-label">Nama Pasien:</td>
              <td class="meta-value">${user.name}</td>
              <td class="meta-label">No. Rekam Medis:</td>
              <td class="meta-value">${user.norm}</td>
            </tr>
            <tr>
              <td class="meta-label">Tanggal Periksa:</td>
              <td class="meta-value">${item.date}</td>
              <td class="meta-label">Dokter Pengkaji:</td>
              <td class="meta-value">${item.doctor}</td>
            </tr>
            <tr>
              <td class="meta-label">Status Layanan:</td>
              <td class="meta-value">${item.status}</td>
              <td class="meta-label">ID Kunjungan:</td>
              <td class="meta-value">${item.id}</td>
            </tr>
          </table>
          
          ${type === 'lab' ? `
            <div class="section-title">Parameter Hasil Laboratorium</div>
            <table class="data-table">
              <thead>
                <tr>
                  <th>Parameter Pemeriksaan</th>
                  <th style="text-align: center;">Hasil</th>
                  <th style="text-align: center;">Satuan</th>
                  <th style="text-align: center;">Nilai Rujukan</th>
                  <th style="text-align: center;">Keterangan</th>
                </tr>
              </thead>
              <tbody>
                ${item.results.map(res => `
                  <tr>
                    <td><strong>${res.name}</strong></td>
                    <td style="text-align: center; font-family: monospace; font-weight: bold;">${res.value}</td>
                    <td style="text-align: center;">${res.unit}</td>
                    <td style="text-align: center;">${res.ref}</td>
                    <td style="text-align: center;">
                      <span class="badge ${
                        res.status === 'Tinggi' ? 'badge-danger' :
                        res.status === 'Rendah' ? 'badge-warning' : 'badge-success'
                      }">${res.status}</span>
                    </td>
                  </tr>
                `).join('')}
              </tbody>
            </table>
          ` : type === 'rad' ? `
            <div class="section-title">Jenis Pemeriksaan</div>
            <div style="font-size: 13px; font-weight: bold; margin-bottom: 15px;">${item.examType}</div>
            
            <div class="section-title">Temuan Klinis (Findings)</div>
            <div class="findings-box">${item.findings}</div>
            
            ${item.images && item.images.length > 0 ? `
              <div class="section-title">Gambar Hasil Ronsen</div>
              <div class="image-gallery">
                ${item.images.map(img => `
                  <div class="image-item">
                    <img src="${img}" alt="Ronsen" />
                  </div>
                `).join('')}
              </div>
            ` : ''}
          ` : `
            <div class="section-title">Tanda Vital</div>
            <table class="data-table">
              <tr>
                <th>Tekanan Darah</th>
                <th>Suhu Tubuh</th>
                <th>Berat Badan</th>
              </tr>
              <tr>
                <td>${item.tensi}</td>
                <td>${item.suhu}</td>
                <td>${item.berat}</td>
              </tr>
            </table>
            
            <div class="section-title">Diagnosa Utama</div>
            <div class="findings-box">${item.diagnosa}</div>
            
            <div class="section-title">Tindakan / Prosedur</div>
            <div class="findings-box">${item.tindakan}</div>
            
            <div class="section-title">Resep Terapi</div>
            <div class="findings-box" style="font-family: monospace;">${item.resep}</div>
          `}
          <div class="footer-info">
            Laporan ini dibuat secara otomatis oleh sistem EPasien Rumah Sakit Muhammadiyah Mardhatillah Dengan Persetujuan Pasien.<br/>
            Dicetak pada tanggal: ${new Date().toLocaleDateString('id-ID', { day: '2-digit', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit' })}
          </div>
          
          <script>
            window.onload = function() {
              window.print();
            };
          </script>
        </body>
      </html>
    `;
    
    printWindow.document.write(htmlContent);
    printWindow.document.close();
  };

  const handleDownload = (item) => {
    handlePrintReport(item, activeSubTab);
    
    const title = activeSubTab === 'kunjungan' ? `Riwayat Kunjungan ${item.clinic}` : item.title;
    onAddNotification({
      title: 'Dokumen Diunduh',
      desc: `Laporan ${title} berhasil disimpan ke perangkat Anda.`,
      time: 'Just now'
    });
  };

  const handlePrint = () => {
    window.print();
  };

  const triggerImageDownload = (imgUrl, fileName) => {
    const downloadUrl = `api.php?action=download_image&url=${encodeURIComponent(imgUrl)}`;
    const link = document.createElement('a');
    link.href = downloadUrl;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="space-y-6 md:space-y-8 animate-in fade-in duration-300">
      
      {/* 1. Detail Report Viewer Modal/View */}
      {selectedItem ? (
        <div className="space-y-6">
          {/* Back Button */}
          <button 
            onClick={() => setSelectedItem(null)}
            className="flex items-center gap-2 text-xs font-semibold text-emerald-600 hover:text-emerald-700 transition-colors bg-white border border-slate-200 px-4 py-2 rounded-xl shadow-sm"
          >
            <ArrowLeft size={14} /> Kembali ke Riwayat
          </button>

          {/* Clinical Report Sheet */}
          <div className="glass-panel p-6 md:p-8 rounded-3xl space-y-6 relative overflow-hidden bg-white/95 shadow-lg border-slate-200">
            <div className="absolute top-0 right-0 w-64 h-64 bg-indigo-500/5 rounded-full blur-3xl -z-10"></div>
            
            {/* Report Header */}
            <div className="flex flex-col md:flex-row justify-between gap-4 border-b border-slate-100 pb-5">
              <div>
                <h3 className="text-lg md:text-xl font-bold font-heading text-slate-800">
                  {activeSubTab === 'kunjungan' ? `Riwayat Kunjungan - ${selectedItem.clinic}` : selectedItem.title}
                </h3>
                <p className="text-xs text-slate-500 mt-1 flex items-center gap-1.5">
                  <Calendar size={12} /> {activeSubTab === 'kunjungan' ? 'Tanggal Kunjungan' : 'Tanggal Pemeriksaan'}: {selectedItem.date}
                </p>
              </div>
              <div className="flex items-center gap-2 self-start md:self-center">
                {activeSubTab !== 'kunjungan' && (
                  <button 
                    onClick={() => {
                      setPendingAction(() => () => handleDownload(selectedItem));
                      setShowConfirmModal(true);
                    }}
                    className="p-2 text-slate-600 hover:text-emerald-600 bg-slate-50 hover:bg-slate-100 border border-slate-200 rounded-xl transition-all shadow-sm"
                    title="Unduh PDF"
                  >
                    <Download size={16} />
                  </button>
                )}
                {/* <button 
                  onClick={handlePrint}
                  className="p-2 text-slate-600 hover:text-emerald-600 bg-slate-50 hover:bg-slate-100 border border-slate-200 rounded-xl transition-all shadow-sm"
                  title="Cetak Hasil"
                >
                  <Printer size={16} />
                </button> */}
              </div>
            </div>

            {/* Patient & Doctor Meta */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 bg-slate-50 p-4 rounded-2xl border border-slate-200/60 text-xs">
              <div>
                <span className="text-slate-500 block uppercase font-bold text-[9px]">Nama Pasien</span>
                <span className="text-slate-800 font-semibold">{user.name}</span>
              </div>
              <div>
                <span className="text-slate-500 block uppercase font-bold text-[9px]">No. Rekam Medis</span>
                <span className="text-slate-800 font-semibold">{user.norm}</span>
              </div>
              <div>
                <span className="text-slate-500 block uppercase font-bold text-[9px]">{activeSubTab === 'kunjungan' ? 'Dokter Pemeriksa' : 'Dokter Pengkaji'}</span>
                <span className="text-slate-800 font-semibold">{selectedItem.doctor}</span>
              </div>
              <div>
                <span className="text-slate-500 block uppercase font-bold text-[9px]">Status Layanan</span>
                <span className="text-emerald-600 font-semibold flex items-center gap-1"><CheckCircle size={10} /> {selectedItem.status}</span>
              </div>
            </div>

            {/* Report Content */}
            {activeSubTab === 'kunjungan' ? (
              /* Visit Report Details */
              <div className="space-y-6 text-slate-700">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-1">
                    <span className="text-slate-500 block uppercase font-bold text-[9px]">Nomor Rawat / Registrasi</span>
                    <span className="text-slate-800 font-medium bg-slate-100 py-2 px-4 rounded-xl border border-slate-200 block font-mono">
                      {selectedItem.noRawat} / {selectedItem.noReg}
                    </span>
                  </div>
                  <div className="space-y-1">
                    <span className="text-slate-500 block uppercase font-bold text-[9px]">Jenis Layanan</span>
                    <span className={`inline-block py-2 px-4 rounded-xl border font-semibold text-xs ${
                      selectedItem.type === 'Rawat Inap' 
                        ? 'bg-emerald-50 text-emerald-700 border-emerald-100' 
                        : 'bg-cyan-50 text-cyan-700 border-cyan-100'
                    }`}>
                      {selectedItem.type}
                    </span>
                  </div>
                </div>

                {/* Vital Signs */}
                <div className="space-y-3">
                  <h4 className="text-xs font-semibold text-emerald-600 uppercase tracking-wider">Pemeriksaan Fisik & Tanda Vital</h4>
                  <div className="grid grid-cols-3 gap-4">
                    <div className="bg-slate-50 p-3 rounded-2xl border border-slate-200/60 text-center">
                      <span className="text-slate-500 block text-[9px] uppercase font-bold">Tekanan Darah</span>
                      <span className="text-slate-800 font-semibold text-sm font-mono mt-1 block">{selectedItem.tensi}</span>
                    </div>
                    <div className="bg-slate-50 p-3 rounded-2xl border border-slate-200/60 text-center">
                      <span className="text-slate-500 block text-[9px] uppercase font-bold">Suhu Tubuh</span>
                      <span className="text-slate-800 font-semibold text-sm font-mono mt-1 block">{selectedItem.suhu}</span>
                    </div>
                    <div className="bg-slate-50 p-3 rounded-2xl border border-slate-200/60 text-center">
                      <span className="text-slate-500 block text-[9px] uppercase font-bold">Berat Badan</span>
                      <span className="text-slate-800 font-semibold text-sm font-mono mt-1 block">{selectedItem.berat}</span>
                    </div>
                  </div>
                </div>

                {/* Diagnosa & Tindakan */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="space-y-2">
                    <h4 className="text-xs font-semibold text-emerald-600 uppercase tracking-wider">Diagnosa (ICD-10)</h4>
                    <div className="bg-slate-50 p-4 rounded-2xl border border-slate-200/60 text-xs md:text-sm text-slate-800 font-medium leading-relaxed">
                      {selectedItem.diagnosa}
                    </div>
                  </div>
                  <div className="space-y-2">
                    <h4 className="text-xs font-semibold text-emerald-600 uppercase tracking-wider">Tindakan / Prosedur</h4>
                    <div className="bg-slate-50 p-4 rounded-2xl border border-slate-200/60 text-xs md:text-sm text-slate-700 leading-relaxed">
                      {selectedItem.tindakan}
                    </div>
                  </div>
                </div>

                {/* Resep Obat */}
                <div className="space-y-2">
                  <h4 className="text-xs font-semibold text-emerald-600 uppercase tracking-wider">Resep Obat & Terapi</h4>
                  <div className="bg-indigo-50/50 p-4 rounded-2xl border border-indigo-100/80 text-xs md:text-sm text-indigo-700 font-mono leading-relaxed">
                    {selectedItem.resep}
                  </div>
                </div>
              </div>
            ) : activeSubTab === 'lab' ? (
              /* Lab Results Table */
              <div className="space-y-4">
                <h4 className="text-xs font-semibold text-emerald-600 uppercase tracking-wider">Hasil Parameter Laboratorium</h4>
                <div className="overflow-x-auto rounded-2xl border border-slate-200/60">
                  <table className="w-full text-left border-collapse text-xs md:text-sm">
                    <thead>
                      <tr className="bg-slate-100/70 text-slate-700 border-b border-slate-200">
                        <th className="py-3 px-4">Parameter Pemeriksaan</th>
                        <th className="py-3 px-4 text-center">Hasil</th>
                        <th className="py-3 px-4 text-center">Satuan</th>
                        <th className="py-3 px-4 text-center">Nilai Rujukan</th>
                        <th className="py-3 px-4 text-center">Keterangan</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100 text-slate-700">
                      {selectedItem.results.map((res, i) => (
                        <tr key={i} className="hover:bg-slate-50/50 transition-colors">
                          <td className="py-3.5 px-4 font-semibold text-slate-800">{res.name}</td>
                          <td className="py-3.5 px-4 text-center font-mono font-semibold">{res.value}</td>
                          <td className="py-3.5 px-4 text-center text-slate-500">{res.unit}</td>
                          <td className="py-3.5 px-4 text-center text-slate-500">{res.ref}</td>
                          <td className="py-3.5 px-4 text-center">
                            <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                              res.status === 'Tinggi' ? 'bg-rose-50 text-rose-600 border border-rose-200/50' :
                              res.status === 'Rendah' ? 'bg-amber-50 text-amber-600 border border-amber-200/50' :
                              'bg-emerald-50 text-emerald-600 border border-emerald-200/50'
                            }`}>
                              {res.status}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            ) : (
              /* Radiology Report Findings */
              <div className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
                  <div>
                    <span className="text-slate-500 block uppercase font-bold text-[9px] mb-1">Jenis Pemeriksaan</span>
                    <span className="text-slate-800 font-medium bg-slate-50 py-2 px-4 rounded-xl border border-slate-200 block">{selectedItem.examType}</span>
                  </div>
                </div>
                
                <div className="space-y-2">
                  <h4 className="text-xs font-semibold text-emerald-600 uppercase tracking-wider">Temuan Eksaminasi (Findings)</h4>
                  <div className="bg-slate-50 p-4 rounded-2xl border border-slate-200/50 text-xs md:text-sm text-slate-700 leading-relaxed justify-justify">
                    {selectedItem.findings}
                  </div>
                </div>

                {selectedItem.images && selectedItem.images.length > 0 && (
                  <div className="space-y-3">
                    <h4 className="text-xs font-semibold text-emerald-600 uppercase tracking-wider">Berkas Gambar Hasil Ronsen</h4>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                      {selectedItem.images.map((imgUrl, i) => (
                        <div key={i} className="relative group overflow-hidden rounded-2xl border border-slate-200 bg-slate-900 aspect-[4/3] flex items-center justify-center">
                          <img 
                            src={imgUrl} 
                            alt={`Hasil Ronsen ${i+1}`}
                            className="max-w-full max-h-full object-contain transition-transform duration-300 group-hover:scale-105"
                          />
                          <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity flex items-end justify-center p-4 gap-2">
                            <button 
                              type="button"
                              onClick={() => {
                                setPendingAction(() => () => window.open(imgUrl, '_blank', 'noopener,noreferrer'));
                                setShowConfirmModal(true);
                              }}
                              className="text-xs font-semibold text-white bg-emerald-600 hover:bg-emerald-500 py-1.5 px-3 rounded-lg shadow-md transition-colors cursor-pointer"
                            >
                              Buka Gambar Penuh
                            </button>
                            <button 
                              type="button"
                              onClick={() => {
                                setPendingAction(() => () => {
                                  triggerImageDownload(imgUrl, `ronsen-${selectedItem.id || 'radiologi'}-${i+1}.jpg`);
                                  onAddNotification({
                                    title: 'Gambar Diunduh',
                                    desc: 'Berkas gambar hasil ronsen berhasil disimpan ke perangkat Anda.',
                                    time: 'Just now'
                                  });
                                });
                                setShowConfirmModal(true);
                              }}
                              className="text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-500 py-1.5 px-3 rounded-lg shadow-md transition-colors cursor-pointer"
                            >
                              Unduh Gambar
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            )}

            {/* Kesimpulan section removed */}

          </div>
        </div>
      ) : (
        /* 2. Main History List */
        <div className="space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
            {/* Section Header */}
            <div>
              <h2 className="text-xl md:text-2xl font-bold font-heading text-slate-800">
                {activeSubTab === 'kunjungan' ? 'Riwayat Kunjungan Medis' : activeSubTab === 'lab' ? 'Hasil Laboratorium' : 'Hasil Radiologi'}
              </h2>
              <p className="text-xs md:text-sm text-slate-500">
                {activeSubTab === 'kunjungan' 
                  ? 'Riwayat kunjungan rawat jalan dan rawat inap Anda.' 
                  : `Riwayat hasil pemeriksaan ${activeSubTab === 'lab' ? 'laboratorium' : 'radiologi'} Anda.`}
              </p>
            </div>
          </div>

          {/* Search bar */}
          <div className="relative max-w-md">
            <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" size={16} />
            <input 
              type="text" 
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="glass-input w-full py-2.5 pl-10 pr-4 rounded-xl text-sm" 
              placeholder={
                activeSubTab === 'kunjungan'
                  ? "Cari poliklinik, dokter, atau diagnosa..."
                  : "Cari berdasarkan nama pemeriksaan atau dokter..."
              }
            />
          </div>

          {/* List Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {filteredHistory.length > 0 ? (
              filteredHistory.map((item) => {
                const isKunjungan = activeSubTab === 'kunjungan';
                return (
                  <button 
                    key={item.id}
                    onClick={() => setSelectedItem(item)}
                    className="glass-card hover:bg-slate-50 border border-slate-200/60 hover:border-slate-300 p-5 rounded-3xl text-left flex flex-col justify-between transition-all duration-200 bg-white"
                  >
                    <div className="space-y-3 w-full">
                      <div className="flex justify-between items-start">
                        <span className={`text-[10px] uppercase font-bold tracking-wider px-2.5 py-0.5 rounded-full border ${
                          isKunjungan
                            ? item.type === 'Rawat Inap'
                              ? 'text-emerald-700 bg-emerald-50 border-emerald-200/50'
                              : 'text-cyan-700 bg-cyan-50 border-cyan-200/50'
                            : 'text-indigo-700 bg-indigo-50 border-indigo-200/50'
                        }`}>
                          {isKunjungan ? item.type : activeSubTab === 'lab' ? 'Laboratorium' : 'Radiologi'}
                        </span>
                        <span className="text-[10px] text-slate-400 flex items-center gap-1"><Calendar size={10} /> {item.date}</span>
                      </div>

                      <div>
                        <h4 className="font-bold font-heading text-slate-800 text-sm md:text-base truncate">
                          {isKunjungan ? item.clinic : item.title}
                        </h4>
                        <p className="text-xs text-slate-500 truncate mt-1">Dokter: {item.doctor}</p>
                      </div>

                      <p className="text-xs text-slate-600 leading-normal line-clamp-2 bg-slate-50 p-3 rounded-xl border border-slate-100">
                        {isKunjungan ? `Diagnosa: ${item.diagnosa}` : `Kesan: ${item.kesimpulan}`}
                      </p>
                    </div>

                    <div className="flex items-center justify-between border-t border-slate-100 pt-3 mt-4 w-full text-xs font-semibold text-emerald-600 group">
                      <span>{isKunjungan ? 'Lihat Detail Kunjungan' : 'Lihat Hasil Lengkap'}</span>
                      <ChevronRight size={16} className="transform group-hover:translate-x-1 transition-transform text-emerald-600" />
                    </div>
                  </button>
                );
              })
            ) : (
              <div className="col-span-1 md:col-span-2 text-center py-12 glass-panel rounded-3xl border border-slate-200 bg-white">
                <FileText className="mx-auto text-slate-400 mb-2" size={32} />
                <p className="text-sm text-slate-500">Tidak ada data riwayat yang cocok.</p>
              </div>
            )}
          </div>
        </div>
      )}

      {/* Confirmation Modal for Medical Records Confidentiality */}
      {showConfirmModal && (
        <div className="fixed inset-0 z-[100] bg-slate-900/40 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="w-full max-w-sm glass-panel p-6 md:p-8 rounded-3xl space-y-6 relative border border-slate-200 bg-white shadow-2xl text-center animate-in fade-in zoom-in duration-200">
            
            <div className="inline-flex p-3 bg-amber-50 text-amber-500 rounded-full border border-amber-100">
              <AlertCircle size={48} className="stroke-[1.5]" />
            </div>

            <div className="space-y-3">
              <h3 className="text-base md:text-lg font-bold font-heading text-slate-800">Konfirmasi</h3>
              <p className="text-xs text-slate-600 leading-relaxed text-center px-1">
                Dengan mengunduh hasil pemeriksaan ini, Anda menyatakan memahami kerahasiaan rekam medis sesuai Permenkes No. 24 Tahun 2022 dan bertanggung jawab atas perlindungan data pribadi sesuai UU No. 27 Tahun 2022.
              </p>
            </div>

            <div className="flex gap-3 justify-center pt-2">
              <button 
                onClick={handleConfirmAction}
                className="flex-1 py-2 px-4 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl shadow-md transition-all cursor-pointer"
              >
                Setuju
              </button>
              <button 
                onClick={handleCancelAction}
                className="flex-1 py-2 px-4 bg-slate-500 hover:bg-slate-600 text-white font-semibold text-xs rounded-xl shadow-md transition-all cursor-pointer"
              >
                Batal
              </button>
            </div>

          </div>
        </div>
      )}

    </div>
  );
}
