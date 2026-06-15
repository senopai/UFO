import React, { useState, useRef, useEffect } from 'react';
import { 
  FileSignature, 
  ChevronRight, 
  Calendar, 
  CheckCircle, 
  Clock, 
  ArrowLeft,
  Camera,
  RefreshCw,
  ShieldCheck,
  AlertTriangle
} from 'lucide-react';

export default function EConsent({ user, onAddNotification, onConsentSigned }) {
  const [selectedForm, setSelectedForm] = useState(null);
  const [cameraStream, setCameraStream] = useState(null);
  const [capturedPhoto, setCapturedPhoto] = useState(null);
  const [cameraError, setCameraError] = useState(false);
  const [isSigning, setIsSigning] = useState(false);

  const videoRef = useRef(null);
  const canvasRef = useRef(null);

  // Mock Consent Forms Data
  const [consentList, setConsentList] = useState([
    { 
      id: 'c-1', 
      title: 'Persetujuan Umum (General Consent)', 
      date: '13 Jun 2026', 
      status: 'Belum Ditandatangani',
      type: 'Umum',
      content: `Saya yang bertanda tangan di bawah ini memberikan persetujuan untuk mendapatkan pelayanan kesehatan di rumah sakit dan memberikan kuasa kepada dokter serta perawat untuk melakukan asuhan keperawatan, pemeriksaan fisik, dan prosedur diagnostik rutin yang diperlukan. Saya telah menerima informasi tentang peraturan, hak dan kewajiban pasien, tarif ruang perawatan, tata tertib pasien pulang, serta setuju untuk mematuhi semua ketentuan yang berlaku.`
    },
    { 
      id: 'c-2', 
      title: 'Persetujuan Tindakan Medis (Informed Consent)', 
      date: '12 Jun 2026', 
      status: 'Belum Ditandatangani',
      type: 'Tindakan',
      content: `Persetujuan pemberian tindakan kedokteran berupa pemasangan kateter urin dan infus intravena untuk kelancaran jalannya terapi obat. Saya telah dijelaskan mengenai tujuan tindakan, risiko infeksi ringan, efek samping, alternatif tindakan, serta konsekuensi jika tindakan ini ditolak.`
    },
    { 
      id: 'c-3', 
      title: 'Rencana Pemulangan Pasien (Discharge Planning)', 
      date: '11 Jun 2026', 
      status: 'Ditandatangani',
      photo: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=100&h=100&q=80',
      type: 'Administrasi',
      content: `Rencana pemulangan pasien atas persetujuan Dokter DPJP dengan syarat kontrol rutin 1 minggu setelah pulang, meminum obat jalan secara teratur sesuai resep, serta menjaga pola makan rendah garam.`
    }
  ]);

  // Start Real Camera Stream
  const startCamera = async () => {
    setCapturedPhoto(null);
    setCameraError(false);
    try {
      if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
        const stream = await navigator.mediaDevices.getUserMedia({ video: { width: 640, height: 480 } });
        setCameraStream(stream);
        if (videoRef.current) {
          videoRef.current.srcObject = stream;
        }
      } else {
        setCameraError(true);
      }
    } catch (err) {
      console.warn("Camera access failed, falling back to simulator:", err);
      setCameraError(true);
    }
  };

  // Stop Camera Stream
  const stopCamera = () => {
    if (cameraStream) {
      cameraStream.getTracks().forEach(track => track.stop());
      setCameraStream(null);
    }
  };

  useEffect(() => {
    if (selectedForm && selectedForm.status === 'Belum Ditandatangani') {
      startCamera();
    }
    return () => {
      stopCamera();
    };
  }, [selectedForm]);

  // Capture Image Frame from Video Element
  const capturePhoto = () => {
    if (videoRef.current && canvasRef.current) {
      const video = videoRef.current;
      const canvas = canvasRef.current;
      const ctx = canvas.getContext('2d');
      
      canvas.width = video.videoWidth || 640;
      canvas.height = video.videoHeight || 480;
      
      ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
      const dataUrl = canvas.toDataURL('image/jpeg');
      setCapturedPhoto(dataUrl);
      stopCamera();
    } else if (cameraError) {
      // Simulator fallback photo
      setCapturedPhoto("https://images.unsplash.com/photo-1576091160550-2173dba999ef?auto=format&fit=crop&w=300&h=200&q=80");
    }
  };

  // Confirm Signature / Form Submission
  const confirmSignature = () => {
    setIsSigning(true);
    setTimeout(() => {
      // Update form status in local state
      setConsentList(prev => prev.map(form => {
        if (form.id === selectedForm.id) {
          return {
            ...form,
            status: 'Ditandatangani',
            photo: capturedPhoto || "https://images.unsplash.com/photo-1576091160550-2173dba999ef?auto=format&fit=crop&w=300&h=200&q=80"
          };
        }
        return form;
      }));

      onAddNotification({
        title: 'E-Consent Berhasil',
        desc: `${selectedForm.title} telah berhasil ditandatangani.`,
        time: 'Just now'
      });

      onConsentSigned();
      setIsSigning(false);
      setSelectedForm(null);
      setCapturedPhoto(null);
    }, 1500);
  };

  return (
    <div className="space-y-6 md:space-y-8 animate-in fade-in duration-300">
      
      {/* Offscreen canvas for photo rendering */}
      <canvas ref={canvasRef} className="hidden"></canvas>

      {selectedForm ? (
        /* 1. Detail Document Viewer & Signing Page */
        <div className="space-y-6">
          <button 
            onClick={() => {
              setSelectedForm(null);
              stopCamera();
            }}
            className="flex items-center gap-2 text-xs font-semibold text-emerald-600 hover:text-emerald-700 transition-colors bg-white border border-slate-200 px-4 py-2 rounded-xl shadow-sm"
          >
            <ArrowLeft size={14} /> Kembali ke Daftar
          </button>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            
            {/* Document details */}
            <div className="lg:col-span-2 glass-panel p-6 md:p-8 rounded-3xl space-y-6 relative overflow-hidden bg-white/95 border-slate-200 shadow-lg">
              <div className="absolute top-0 right-0 w-64 h-64 bg-indigo-500/5 rounded-full blur-3xl -z-10"></div>
              
              <div className="border-b border-slate-100 pb-4">
                <span className="text-[10px] uppercase font-bold tracking-wider text-indigo-700 bg-indigo-50 px-2.5 py-0.5 rounded-full border border-indigo-100">
                  {selectedForm.type}
                </span>
                <h3 className="text-lg md:text-xl font-bold font-heading text-slate-800 mt-2">{selectedForm.title}</h3>
                <span className="text-[10px] text-slate-400 flex items-center gap-1 mt-1"><Calendar size={12} /> Tanggal Dokumen: {selectedForm.date}</span>
              </div>

              <div className="space-y-4 text-xs md:text-sm text-slate-600 leading-relaxed text-justify max-h-[300px] overflow-y-auto pr-2">
                <p className="font-semibold text-slate-800">PERNYATAAN PERSETUJUAN & KETENTUAN:</p>
                <p>{selectedForm.content}</p>
                <p className="text-slate-400 text-xs">
                  Dengan menandatangani dokumen ini secara elektronik (verifikasi wajah), saya menyatakan bahwa saya telah membaca, memahami, dan menyetujui seluruh ketentuan perawatan medis yang tercantum di atas tanpa adanya unsur paksaan dari pihak manapun.
                </p>
              </div>

              {/* Patient Profile Meta */}
              <div className="grid grid-cols-2 gap-4 bg-slate-50 p-4 rounded-2xl border border-slate-200/60 text-xs">
                <div>
                  <span className="text-slate-500 block uppercase font-bold text-[9px]">Nama Lengkap Pasien</span>
                  <span className="text-slate-800 font-semibold">{user.name}</span>
                </div>
                <div>
                  <span className="text-slate-500 block uppercase font-bold text-[9px]">No. Rekam Medis</span>
                  <span className="text-slate-800 font-semibold">{user.norm}</span>
                </div>
              </div>
            </div>

            {/* Signature & Camera Box */}
            <div className="glass-panel p-6 rounded-3xl border-slate-200 bg-white/95 shadow-md space-y-4 flex flex-col justify-between h-fit">
              <h4 className="text-xs font-semibold text-emerald-600 uppercase tracking-wider flex items-center gap-1.5"><Camera size={14} /> Otentikasi Wajah</h4>
              
              {selectedForm.status === 'Belum Ditandatangani' ? (
                /* Interactive Capture Area */
                <div className="space-y-4">
                  
                  {/* Viewfinder Panel */}
                  <div className="relative aspect-video w-full bg-slate-100 border border-slate-200 rounded-2xl overflow-hidden flex items-center justify-center">
                    {capturedPhoto ? (
                      /* Captured Preview */
                      <img src={capturedPhoto} alt="Captured preview" className="w-full h-full object-cover" />
                    ) : cameraError ? (
                      /* Simulator View */
                      <div className="absolute inset-0 flex flex-col items-center justify-center p-4 text-center bg-slate-100">
                        <AlertTriangle className="text-amber-500 mb-2" size={24} />
                        <span className="text-xs font-semibold text-slate-800">Simulator Kamera</span>
                        <p className="text-[10px] text-slate-400 mt-1">
                          Akses kamera terblokir atau tidak didukung. Kamera disimulasikan menggunakan data foto default.
                        </p>
                      </div>
                    ) : (
                      /* Live Camera Feed */
                      <video 
                        ref={videoRef} 
                        autoPlay 
                        playsInline 
                        className="w-full h-full object-cover scale-x-[-1]"
                      ></video>
                    )}

                    {/* Camera indicator */}
                    {!capturedPhoto && !cameraError && (
                      <div className="absolute top-3 left-3 bg-red-500 px-2 py-0.5 rounded-full flex items-center gap-1 animate-pulse">
                        <span className="w-1.5 h-1.5 bg-white rounded-full"></span>
                        <span className="text-[8px] text-white uppercase font-bold tracking-wider">Live</span>
                      </div>
                    )}
                  </div>

                  {/* Actions buttons */}
                  {!capturedPhoto ? (
                    <button 
                      onClick={capturePhoto}
                      className="w-full py-2.5 px-4 bg-emerald-600 hover:bg-emerald-500 text-white font-semibold text-xs rounded-xl flex items-center gap-2 justify-center shadow-lg transition-all duration-200"
                    >
                      <Camera size={16} /> Ambil Foto Wajah
                    </button>
                  ) : (
                    <div className="flex gap-2">
                      <button 
                        onClick={startCamera}
                        className="w-1/3 py-2.5 px-3 bg-slate-100 hover:bg-slate-200 border border-slate-200 text-slate-700 hover:text-slate-900 font-semibold text-xs rounded-xl flex items-center gap-1.5 justify-center transition-all"
                        disabled={isSigning}
                      >
                        <RefreshCw size={14} /> Ulangi
                      </button>
                      <button 
                        onClick={confirmSignature}
                        className="w-2/3 py-2.5 px-4 bg-emerald-600 hover:bg-emerald-500 text-white font-semibold text-xs rounded-xl flex items-center gap-2 justify-center shadow-[0_4px_15px_rgba(16,185,129,0.15)] transition-all"
                        disabled={isSigning}
                      >
                        {isSigning ? 'Menyimpan...' : 'Setujui & Tandatangani'}
                      </button>
                    </div>
                  )}

                  <span className="text-[10px] text-slate-500 leading-normal block text-center">
                    Foto wajah digunakan sebagai bukti tanda tangan elektronik untuk dokumen rekam medis ini.
                  </span>
                </div>
              ) : (
                /* Already Signed Display */
                <div className="space-y-4 text-center py-6">
                  <div className="mx-auto w-24 h-24 rounded-full border-2 border-emerald-500/20 overflow-hidden relative">
                    <img src={selectedForm.photo} alt="Verification Photo" className="w-full h-full object-cover" />
                    <div className="absolute inset-0 bg-emerald-500/5 flex items-center justify-center">
                      <CheckCircle className="text-emerald-600" size={32} />
                    </div>
                  </div>
                  <div className="space-y-1">
                    <h5 className="font-bold text-sm text-slate-800">Dokumen Ditandatangani</h5>
                    <p className="text-[10px] text-emerald-600 font-semibold">Telah Terverifikasi secara Digital</p>
                  </div>
                </div>
              )}

              {/* Safety Shield */}
              <div className="flex items-center gap-2 border-t border-slate-100 pt-3 text-[10px] text-emerald-600 font-medium">
                <ShieldCheck size={14} /> Enkripsi data persetujuan terlindungi.
              </div>

            </div>

          </div>
        </div>
      ) : (
        /* 2. Main Consent List */
        <div className="space-y-6">
          {/* Section Header */}
          <div>
            <h2 className="text-xl md:text-2xl font-bold font-heading text-slate-800">Persetujuan Medis Elektronik (E-Consent)</h2>
            <p className="text-xs md:text-sm text-slate-500">
              Tinjau dan tandatangani dokumen persetujuan tindakan medis dan administratif secara mandiri.
            </p>
          </div>

          {/* List of documents */}
          <div className="space-y-3">
            {consentList.map((form) => {
              const isPending = form.status === 'Belum Ditandatangani';
              return (
                <button 
                  key={form.id}
                  onClick={() => setSelectedForm(form)}
                  className="glass-card hover:bg-slate-50 border border-slate-200/60 hover:border-slate-300 p-5 rounded-3xl text-left flex flex-col md:flex-row md:items-center justify-between gap-4 w-full transition-all duration-200 bg-white shadow-sm"
                >
                  <div className="flex gap-4 items-start">
                    <div className={`p-3 rounded-xl border shrink-0 ${
                      isPending 
                        ? 'bg-amber-50 text-amber-600 border-amber-100' 
                        : 'bg-emerald-50 text-emerald-600 border-emerald-100'
                    }`}>
                      <FileSignature size={22} />
                    </div>
                    <div className="space-y-1">
                      <h4 className="font-bold font-heading text-slate-800 text-sm md:text-base leading-snug">{form.title}</h4>
                      <div className="flex flex-wrap items-center gap-x-3 gap-y-1 text-xs text-slate-400">
                        <span className="flex items-center gap-1"><Calendar size={12} className="text-slate-400" /> Dibuat: {form.date}</span>
                        <span className="flex items-center gap-1"><Clock size={12} className="text-slate-400" /> Tipe: {form.type}</span>
                      </div>
                    </div>
                  </div>

                  <div className="flex items-center justify-between border-t md:border-t-0 border-slate-100 pt-3 md:pt-0 mt-2 md:mt-0 gap-4 shrink-0">
                    <span className={`text-[10px] uppercase font-bold tracking-wider px-2.5 py-1 rounded-full border ${
                      isPending 
                        ? 'bg-amber-50 text-amber-700 border-amber-250/20 animate-pulse' 
                        : 'bg-emerald-50 text-emerald-750 border-emerald-250/20'
                    }`}>
                      {form.status}
                    </span>
                    <ChevronRight size={16} className="text-slate-400 hidden md:block" />
                  </div>
                </button>
              );
            })}
          </div>
        </div>
      )}

    </div>
  );
}
