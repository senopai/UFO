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
  CheckCircle
} from 'lucide-react';

export default function History({ user, onAddNotification, defaultTab, labHistory = [], radHistory = [], visitHistory = [] }) {
  const [activeSubTab, setActiveSubTab] = useState(defaultTab || 'lab'); // lab, rad, or kunjungan
  const [selectedItem, setSelectedItem] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');

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

  const handleDownload = (title) => {
    onAddNotification({
      title: 'Dokumen Diunduh',
      desc: `Laporan ${title} berhasil disimpan ke perangkat Anda.`,
      time: 'Just now'
    });
  };

  const handlePrint = () => {
    window.print();
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
                <button 
                  onClick={() => handleDownload(activeSubTab === 'kunjungan' ? `Riwayat Kunjungan ${selectedItem.clinic}` : selectedItem.title)}
                  className="p-2 text-slate-600 hover:text-emerald-600 bg-slate-50 hover:bg-slate-100 border border-slate-200 rounded-xl transition-all shadow-sm"
                  title="Unduh PDF"
                >
                  <Download size={16} />
                </button>
                <button 
                  onClick={handlePrint}
                  className="p-2 text-slate-600 hover:text-emerald-600 bg-slate-50 hover:bg-slate-100 border border-slate-200 rounded-xl transition-all shadow-sm"
                  title="Cetak Hasil"
                >
                  <Printer size={16} />
                </button>
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
              </div>
            )}

            {/* Kesimpulan */}
            {activeSubTab !== 'kunjungan' && (
              <div className="space-y-2 border-t border-slate-100 pt-5">
                <h4 className="text-xs font-semibold text-emerald-600 uppercase tracking-wider flex items-center gap-1.5"><ShieldAlert size={14} /> Kesimpulan (Kesan Klinis)</h4>
                <p className="text-xs md:text-sm text-slate-700 leading-relaxed bg-emerald-50/50 border border-emerald-100 p-4 rounded-2xl">
                  {selectedItem.kesimpulan}
                </p>
              </div>
            )}

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

    </div>
  );
}
