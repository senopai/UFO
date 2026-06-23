import React, { useState, useRef } from 'react';
import { CreditCard, Download, RefreshCw, AlertCircle } from 'lucide-react';
import { toPng } from 'html-to-image';

export default function CardPasien({ user }) {
  const [isFlipped, setIsFlipped] = useState(false);
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const cardRef = useRef(null);

  const handleDownloadClick = () => {
    setShowConfirmModal(true);
  };

  const executeDownload = async () => {
    setShowConfirmModal(false);
    if (!cardRef.current) return;
    
    // We capture the specific side directly to avoid 3D transform issues
    const elementToCapture = isFlipped 
      ? cardRef.current.querySelector('.back-side') 
      : cardRef.current.querySelector('.front-side');
      
    if(!elementToCapture) return;

    try {
      // html-to-image handles modern CSS (including oklch) beautifully
      const dataUrl = await toPng(elementToCapture, {
        cacheBust: true,
        pixelRatio: 3, // High resolution
      });
      
      const link = document.createElement('a');
      link.href = dataUrl;
      const sideName = isFlipped ? 'Belakang' : 'Depan';
      link.download = `Kartu_Pasien_${user.name.replace(/\s+/g, '_')}_${sideName}.png`;
      link.click();
    } catch (error) {
      console.error('Failed to generate image:', error);
      alert('Gagal mengunduh kartu. Error: ' + (error.message || error));
    }
  };

  const handleCancelAction = () => {
    setShowConfirmModal(false);
  };

  return (
    <div className="max-w-3xl mx-auto space-y-6 md:space-y-8 animate-in fade-in duration-300">
      
      {/* Header Info */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 print:hidden">
        <div>
          <h2 className="text-xl md:text-2xl font-bold font-heading text-slate-800 flex items-center gap-2">
            <CreditCard className="text-emerald-600" /> Kartu Berobat Pasien
          </h2>
          <p className="text-xs md:text-sm text-slate-500">
            Tunjukkan kartu ini kepada petugas pendaftaran saat kunjungan rumah sakit.
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
            onClick={handleDownloadClick}
            className="flex items-center gap-1.5 py-2 px-3 text-xs font-semibold text-emerald-600 hover:text-emerald-700 bg-emerald-50 hover:bg-emerald-100 border border-emerald-200 rounded-xl shadow-sm transition-all"
          >
            <Download size={14} /> Download
          </button>
        </div>
      </div>

      {/* 3D Card Container */}
      <div ref={cardRef} className="relative w-full max-w-[650px] mx-auto aspect-[1.586/1] perspective-1000 group cursor-pointer print:max-w-[8.5cm] print:aspect-[1.586/1]" onClick={() => setIsFlipped(!isFlipped)}>
        <div className={`relative w-full h-full duration-700 transform-style-3d ${isFlipped ? 'rotate-y-180' : ''} print:rotate-y-0`}>
          
          {/* FRONT SIDE */}
          <div className="front-side absolute inset-0 w-full h-full backface-hidden rounded-xl md:rounded-2xl overflow-hidden border border-slate-300 flex flex-col shadow-2xl bg-white print:shadow-none print:border-black">
            
            {/* Header */}
            <div className="bg-[#3f2f79] px-3 md:px-5 py-2 md:py-3 flex items-center h-[24%] shrink-0 print:h-[24%]">
              <div className="h-full aspect-square bg-transparent flex items-center justify-center shrink-0">
                <img src="images/logo_rsm.png" alt="Logo RSM" className="max-w-full max-h-full object-contain drop-shadow-md" />
              </div>
              <div className="flex-1 flex items-center justify-center pl-2">
                <h2 className="text-white font-extrabold text-[12px] sm:text-base md:text-xl lg:text-2xl leading-tight text-center drop-shadow-md tracking-wide">
                  RUMAH SAKIT MUHAMMADIYAH<br/>MARDHATILLAH
                </h2>
              </div>
            </div>

            {/* Body */}
            <div className="flex-1 relative flex flex-col overflow-hidden">
              {/* Background Gradient simulating the image backdrop */}
              <div className="absolute inset-0 bg-gradient-to-r from-[#ffe500] via-[#cce066] to-[#e6f0fa] z-0"></div>
              
              <div className="relative z-10 px-4 md:px-8 py-2 md:py-3 flex flex-col h-full justify-between">
                <h3 className="text-center font-black text-lg sm:text-xl md:text-2xl text-slate-900 tracking-wider mb-1 md:mb-2 drop-shadow-sm">
                  KARTU BEROBAT
                </h3>
                
                <div className="space-y-1.5 md:space-y-2.5 flex-1 flex flex-col justify-center mb-1">
                  {/* Row No RM */}
                  <div className="flex items-center">
                    <div className="w-24 sm:w-28 md:w-36 font-black text-slate-900 text-xs sm:text-sm md:text-lg tracking-wide">NOMOR RM</div>
                    <div className="font-black text-slate-900 text-xs sm:text-sm md:text-lg mr-2 md:mr-3">:</div>
                    <div className="flex gap-1 md:gap-1.5">
                      {user.norm.replace(/-/g, '').padEnd(6, ' ').split('').map((char, i) => (
                        <div key={i} className="w-6 h-8 sm:w-8 sm:h-9 md:w-9 md:h-11 bg-white border-2 border-slate-900 flex items-center justify-center font-bold text-base sm:text-lg md:text-2xl shadow-sm text-slate-900">
                          {char.trim()}
                        </div>
                      ))}
                    </div>
                  </div>
                  
                  {/* Row Nama */}
                  <div className="flex items-end">
                    <div className="w-24 sm:w-28 md:w-36 font-black text-slate-900 text-xs sm:text-sm md:text-lg tracking-wide shrink-0">NAMA PASIEN</div>
                    <div className="font-black text-slate-900 text-xs sm:text-sm md:text-lg mr-2 md:mr-3 shrink-0">:</div>
                    <div className="flex-1 border-b-[2px] border-dotted border-slate-700 pb-0 md:pb-0.5 text-slate-900 font-bold text-xs sm:text-sm md:text-lg uppercase truncate">
                      {user.name}
                    </div>
                  </div>

                  {/* Row Tgl Lahir */}
                  <div className="flex items-end">
                    <div className="w-24 sm:w-28 md:w-36 font-black text-slate-900 text-xs sm:text-sm md:text-lg tracking-wide shrink-0">TGL. LAHIR</div>
                    <div className="font-black text-slate-900 text-xs sm:text-sm md:text-lg mr-2 md:mr-3 shrink-0">:</div>
                    <div className="flex-1 border-b-[2px] border-dotted border-slate-700 pb-0 md:pb-0.5 text-slate-900 font-bold text-xs sm:text-sm md:text-lg">
                      {user.dob}
                    </div>
                  </div>

                  {/* Row Alamat */}
                  <div className="flex items-end">
                    <div className="w-24 sm:w-28 md:w-36 font-black text-slate-900 text-xs sm:text-sm md:text-lg tracking-wide shrink-0">ALAMAT</div>
                    <div className="font-black text-slate-900 text-xs sm:text-sm md:text-lg mr-2 md:mr-3 shrink-0">:</div>
                    <div className="flex-1 border-b-[2px] border-dotted border-slate-700 pb-0 md:pb-0.5 text-slate-900 font-bold text-xs sm:text-sm md:text-lg uppercase truncate">
                      {user.address || 'RANDUDONGKAL'}
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* Footer */}
            <div className="bg-[#3f2f79] py-1.5 md:py-3 shrink-0 flex items-center justify-center">
              <p className="text-white text-center font-bold text-[8px] sm:text-[10px] md:text-sm lg:text-lg tracking-widest uppercase">
                BAWALAH SELALU KARTU INI BILA ANDA BEROBAT
              </p>
            </div>
          </div>

          {/* BACK SIDE */}
          <div className="back-side absolute inset-0 w-full h-full backface-hidden rounded-xl md:rounded-2xl overflow-hidden border border-slate-300 flex flex-col shadow-2xl rotate-y-180 bg-[#fbf9eb] print:shadow-none print:border-black print:hidden">
            
            {/* Header */}
            <div className="bg-[#3f2f79] h-[12%] shrink-0"></div>

            {/* Body */}
            <div className="flex-1 relative px-5 md:px-10 py-3 flex flex-col items-center justify-center">
              {/* Watermark Logo */}
              <div className="absolute inset-0 flex items-center justify-center opacity-[0.08] pointer-events-none">
                <img src="images/logo_rsm.png" alt="Watermark" className="w-[60%] h-[60%] object-contain" />
              </div>

              <div className="relative z-10 w-full">
                <h3 className="text-center font-extrabold text-[13px] sm:text-lg md:text-2xl text-slate-800 mb-3 md:mb-5 tracking-wide">
                  PENTING UNTUK DIPERHATIKAN
                </h3>
                <ol className="list-decimal list-outside ml-4 md:ml-6 text-slate-800 text-[10px] sm:text-[13px] md:text-[17px] font-medium space-y-2 md:space-y-3 leading-snug pr-2">
                  <li>Kartu berobat dibawa setiap kali berobat ke RS Muhammadiyah Mardhatillah Randudongkal</li>
                  <li>Untuk mempercepat pelayanan, bawalah kartu ini setiap mau berobat ke RS Muhammadiyah Mardhatillah Randudongkal Pemalang</li>
                  <li>Apabila tidak membawa Kartu Berobat ini harap bersabar</li>
                  <li>Barangsiapa yang menemukan kartu ini, harap mengembalikan ke alamat dibawah ini :</li>
                </ol>
              </div>
            </div>

            {/* Footer */}
            <div className="bg-[#3f2f79] py-2 md:py-3 px-4 shrink-0 text-center flex flex-col justify-center">
              <h4 className="text-white font-extrabold text-[11px] sm:text-[14px] md:text-[18px] tracking-wide uppercase">
                RUMAH SAKIT MUHAMMADIYAH MARDHATILLAH
              </h4>
              <p className="text-white text-[7px] sm:text-[9px] md:text-[12px] font-semibold tracking-wider uppercase mt-0.5">
                JL. JEND. SUDIRMAN TIMUR RANDUDONGKAL PEMALANG 52353 TELP. (0284) 3287180
              </p>
            </div>

          </div>

        </div>
      </div>

      {/* Quick Instruction Notice */}
      <div className="glass-panel p-4 rounded-2xl border border-slate-200/60 text-xs md:text-sm text-slate-500 leading-relaxed max-w-2xl mx-auto print:hidden">
        <h5 className="font-semibold text-slate-800 mb-1">Panduan Penggunaan Kartu:</h5>
        <ul className="list-disc pl-4 space-y-1">
          <li>Arahkan kursor atau sentuh pada kartu di atas untuk membalik kartu dan membaca informasi penting.</li>
          <li>Simpan kartu digital ini dengan mengunduhnya secara mandiri menggunakan tombol Download di kanan atas. Kartu yang tersimpan di HP akan sangat memudahkan Anda saat mendaftar berobat kembali.</li>
        </ul>
      </div>

      {/* Confirmation Modal for Download */}
      {showConfirmModal && (
        <div className="fixed inset-0 z-[100] bg-slate-900/40 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="w-full max-w-sm glass-panel p-6 md:p-8 rounded-3xl space-y-6 relative border border-slate-200 bg-white shadow-2xl text-center animate-in fade-in zoom-in duration-200">
            
            <div className="inline-flex p-3 bg-amber-50 text-amber-500 rounded-full border border-amber-100">
              <AlertCircle size={48} className="stroke-[1.5]" />
            </div>

            <div className="space-y-3">
              <h3 className="text-base md:text-lg font-bold font-heading text-slate-800">Konfirmasi</h3>
              <p className="text-xs text-slate-600 leading-relaxed text-center px-1">
                Dengan mengunduh Kartu Pasien ini, Anda bertanggung jawab atas kerahasiaan dan keamanan data Nomor Rekam Medis (No RM) sesuai dengan regulasi perlindungan data medis yang berlaku.
              </p>
            </div>

            <div className="flex gap-3 justify-center pt-2">
              <button 
                onClick={executeDownload}
                className="flex-1 py-2 px-4 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-xs rounded-xl shadow-md transition-all cursor-pointer"
              >
                Setuju & Unduh
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
