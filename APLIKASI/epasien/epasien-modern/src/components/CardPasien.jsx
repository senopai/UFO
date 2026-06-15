import React, { useState } from 'react';
import { CreditCard, QrCode, Printer, Download, HeartPulse, RefreshCw } from 'lucide-react';

export default function CardPasien({ user }) {
  const [isFlipped, setIsFlipped] = useState(false);

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6 md:space-y-8 animate-in fade-in duration-300">
      
      {/* Header Info */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl md:text-2xl font-bold font-heading text-slate-800 flex items-center gap-2">
            <CreditCard className="text-emerald-600" /> Kartu Digital Pasien
          </h2>
          <p className="text-xs md:text-sm text-slate-500">
            Tunjukkan kartu digital ini kepada petugas pendaftaran saat kunjungan rumah sakit.
          </p>
        </div>
        
        <div className="flex gap-2">
          <button 
            onClick={() => setIsFlipped(!isFlipped)}
            className="flex items-center gap-1.5 py-2 px-3 text-xs font-semibold text-slate-600 hover:text-slate-800 bg-white hover:bg-slate-50 border border-slate-200 rounded-xl shadow-sm transition-all"
          >
            <RefreshCw size={14} className="animate-spin-slow" /> Balik Kartu
          </button>
          <button 
            onClick={handlePrint}
            className="flex items-center gap-1.5 py-2 px-3 text-xs font-semibold text-emerald-600 hover:text-emerald-700 bg-emerald-50 hover:bg-emerald-100 border border-emerald-200 rounded-xl shadow-sm transition-all"
          >
            <Printer size={14} /> Cetak
          </button>
        </div>
      </div>

      {/* 3D Card Container */}
      <div className="relative w-full max-w-lg mx-auto aspect-[1.586/1] perspective-1000 group cursor-pointer" onClick={() => setIsFlipped(!isFlipped)}>
        <div className={`relative w-full h-full duration-500 transform-style-3d ${isFlipped ? 'rotate-y-180' : ''}`}>
          
          {/* FRONT SIDE */}
          <div className="absolute inset-0 w-full h-full backface-hidden rounded-3xl overflow-hidden border border-teal-400/20 p-6 flex flex-col justify-between shadow-xl bg-gradient-to-br from-teal-600 via-teal-500 to-emerald-600">
            {/* Background design elements */}
            <div className="absolute top-0 right-0 w-48 h-48 bg-white/10 rounded-full blur-2xl -z-10"></div>
            <div className="absolute -bottom-10 -left-10 w-48 h-48 bg-cyan-500/20 rounded-full blur-2xl -z-10"></div>
            
            {/* Card Header */}
            <div className="flex justify-between items-start">
              <div className="flex items-center gap-2">
                <div className="p-1.5 bg-white/20 text-white rounded-lg border border-white/30">
                  <HeartPulse size={18} />
                </div>
                <div>
                  <h4 className="text-xs font-bold text-white leading-none tracking-wide">RSM MARDHATILLAH</h4>
                  <span className="text-[7px] text-teal-100 font-bold uppercase tracking-wider block mt-0.5">RS MUHAMMADIYAH MARDHATILLAH</span>
                </div>
              </div>
              <span className="text-[8px] font-bold uppercase tracking-widest text-white bg-white/20 px-2 py-0.5 rounded-full border border-white/30">
                KARTU PASIEN
              </span>
            </div>

            {/* Chip & No. RM */}
            <div className="space-y-1.5">
              <span className="text-[9px] uppercase font-bold text-teal-100/80 tracking-wider">Nomor Rekam Medis (No.RM)</span>
              <div className="text-2xl md:text-3xl font-extrabold font-heading text-white tracking-widest leading-none">
                {user.norm.match(/.{1,2}/g).join(' - ')}
              </div>
            </div>

            {/* Card Footer */}
            <div className="flex justify-between items-end border-t border-white/20 pt-3">
              <div className="space-y-0.5">
                <span className="text-[8px] text-teal-100/70 uppercase font-bold tracking-wider block">Nama Pasien</span>
                <h5 className="text-sm font-bold text-white tracking-wide uppercase">{user.name}</h5>
              </div>
              <div className="text-right space-y-0.5">
                <span className="text-[8px] text-teal-100/70 uppercase font-bold tracking-wider block">Tanggal Lahir</span>
                <span className="text-xs font-semibold text-white">{user.dob}</span>
              </div>
            </div>

          </div>

          {/* BACK SIDE */}
          <div className="absolute inset-0 w-full h-full backface-hidden rounded-3xl overflow-hidden border border-slate-200 p-6 flex flex-col justify-between shadow-xl bg-white rotate-y-180">
            {/* Magnetic Stripe representation */}
            <div className="absolute top-6 left-0 w-full h-8 bg-slate-800"></div>
            
            {/* Barcode/QR Code content */}
            <div className="flex flex-col items-center justify-center space-y-2 mt-8">
              <div className="bg-slate-50 p-3 rounded-xl border border-slate-200/80 flex items-center justify-center shadow-inner">
                <QrCode size={90} className="text-slate-800" />
              </div>
              <span className="text-[10px] font-mono text-slate-600 font-bold tracking-widest">{user.norm}</span>
            </div>

            {/* Back Footer Details */}
            <div className="flex justify-between items-end text-[8px] text-slate-400 border-t border-slate-100 pt-3">
              <span>JL. RAYA DAGO NO. 120, BANDUNG</span>
              <span className="text-emerald-600 font-bold uppercase tracking-wider">SIMKES KHANZA</span>
            </div>

          </div>

        </div>
      </div>

      {/* Quick Instruction Notice */}
      <div className="glass-panel p-4 rounded-2xl border border-slate-200/60 text-xs text-slate-500 leading-relaxed max-w-lg mx-auto">
        <h5 className="font-semibold text-slate-800 mb-1">Panduan Penggunaan Kartu:</h5>
        <ul className="list-disc pl-4 space-y-1">
          <li>Kartu ini berlaku untuk seluruh unit poliklinik dan laboratorium di lingkungan RS Muhammadiyah Mardhatillah.</li>
          <li>Arahkan kursor atau klik pada kartu di atas untuk membalik kartu dan memperlihatkan kode QR/Barcode.</li>
          <li>Simpan kartu digital ini dengan mencetaknya secara mandiri menggunakan tombol cetak di kanan atas.</li>
        </ul>
      </div>

    </div>
  );
}
