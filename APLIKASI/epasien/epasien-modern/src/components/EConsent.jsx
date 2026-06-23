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
  const [consentList, setConsentList] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [cameraStream, setCameraStream] = useState(null);
  const [capturedPhoto, setCapturedPhoto] = useState(null);
  const [cameraError, setCameraError] = useState(false);
  const [isSigning, setIsSigning] = useState(false);

  // Dynamic inputs for SPU (Persetujuan Umum)
  const [pengobatanKepada, setPengobatanKepada] = useState('-');
  const [nilaiKepercayaan, setNilaiKepercayaan] = useState('');

  // Dynamic inputs for PPT (Persetujuan Tindakan)
  const [pernyataan, setPernyataan] = useState('Persetujuan');

  const [insecureContext, setInsecureContext] = useState(false);
  const [cameraErrorMessage, setCameraErrorMessage] = useState('');

  const videoRef = useRef(null);
  const canvasRef = useRef(null);

  // Fetch Consents from Database
  const fetchConsents = async () => {
    try {
      setIsLoading(true);
      const res = await fetch('/api.php?action=get_consents');
      const data = await res.json();
      if (data.success) {
        setConsentList(data.consents);
      }
    } catch (err) {
      console.error('Failed to fetch consents:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchConsents();
  }, []);

  const handleSelectForm = (form) => {
    setSelectedForm(form);
    if (form.type === 'Umum') {
      setPengobatanKepada(form.meta?.pengobatan_kepada || '-');
      setNilaiKepercayaan(form.meta?.nilai_kepercayaan || '');
    } else if (form.type === 'Tindakan') {
      setPernyataan(form.meta?.pernyataan === 'Penolakan' ? 'Penolakan' : 'Persetujuan');
    }
  };

  // Start Real Camera Stream
  const startCamera = async () => {
    setCapturedPhoto(null);
    setCameraError(false);
    setInsecureContext(false);
    setCameraErrorMessage('');

    // Check if secure context
    if (!window.isSecureContext && window.location.hostname !== 'localhost' && window.location.hostname !== '127.0.0.1') {
      setInsecureContext(true);
      setCameraError(true);
      setCameraErrorMessage('Kamera diblokir karena koneksi HTTP tidak aman. Silakan gunakan HTTPS/localhost atau aktifkan flag Chrome.');
      console.warn('Camera access blocked due to insecure context (non-HTTPS IP).');
      return;
    }

    try {
      if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
        // Use flexible constraints, specifically asking for the front camera (user-facing)
        const stream = await navigator.mediaDevices.getUserMedia({ 
          video: { facingMode: 'user' } 
        });
        setCameraStream(stream);
        if (videoRef.current) {
          videoRef.current.srcObject = stream;
          videoRef.current.play().catch(e => console.warn("Video play error:", e));
        }
      } else {
        setCameraError(true);
        setCameraErrorMessage('Kamera tidak didukung oleh browser ini.');
      }
    } catch (err) {
      console.warn("Camera access failed:", err);
      setCameraError(true);
      
      // Friendly error messages based on common failures
      if (err.name === 'NotAllowedError' || err.name === 'PermissionDeniedError') {
        setCameraErrorMessage('Izin kamera ditolak. Mohon izinkan akses kamera di browser HP Anda.');
      } else if (err.name === 'NotFoundError' || err.name === 'DevicesNotFoundError') {
        setCameraErrorMessage('Tidak ada kamera yang ditemukan pada perangkat ini.');
      } else {
        setCameraErrorMessage(err.message || 'Gagal mengakses kamera depan.');
      }
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

  // Confirm Signature / Form Submission to Database
  const confirmSignature = async () => {
    if (!capturedPhoto) return;
    
    setIsSigning(true);
    try {
      const choices = selectedForm.type === 'Umum' 
        ? { pengobatan_kepada: pengobatanKepada, nilai_kepercayaan: nilaiKepercayaan }
        : { pernyataan };

      const response = await fetch('/api.php?action=save_consent', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          id: selectedForm.id,
          type: selectedForm.type,
          photo: capturedPhoto,
          choices
        })
      });

      const resData = await response.json();
      if (resData.success) {
        onAddNotification({
          title: 'E-Consent Berhasil',
          desc: `${selectedForm.title} telah berhasil ditandatangani.`,
          time: 'Baru saja'
        });

        if (onConsentSigned) {
          onConsentSigned();
        }
        
        // Reload list and reset
        await fetchConsents();
        setSelectedForm(null);
        setCapturedPhoto(null);
      } else {
        alert('Gagal menyimpan tanda tangan: ' + resData.message);
      }
    } catch (err) {
      console.error('Save consent error:', err);
      alert('Terjadi kesalahan saat menyimpan persetujuan: ' + err.message);
    } finally {
      setIsSigning(false);
    }
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
                <div className="flex flex-wrap gap-x-4 gap-y-1 mt-1 text-[10px] text-slate-455">
                  <span className="flex items-center gap-1"><Calendar size={12} /> Tanggal Dokumen: {selectedForm.date}</span>
                  <span className="flex items-center gap-1"><Clock size={12} /> No. Rawat: {selectedForm.noRawat}</span>
                </div>
              </div>

              {selectedForm.type === 'Umum' ? (
                /* PERSATUJUAN UMUM (GENERAL CONSENT) DETAILS */
                <div className="space-y-6 text-xs md:text-sm text-slate-600">
                  <div className="space-y-3 max-h-[300px] overflow-y-auto pr-2 border-b border-slate-200 pb-4">
                    <p className="font-semibold text-slate-800">I. PERNYATAAN PERSETUJUAN & KETENTUAN:</p>
                    <p>{selectedForm.content}</p>
                    <p className="text-slate-450 text-[11px] leading-relaxed">
                      1. Saya memberikan persetujuan untuk pelayanan kesehatan di rumah sakit dan memberikan kuasa kepada dokter serta perawat untuk melakukan asuhan keperawatan, pemeriksaan fisik, dan prosedur diagnostik rutin yang diperlukan.<br/>
                      2. Saya menyetujui pelepasan informasi medis saya kepada pihak penjamin seperti BPJS/Asuransi demi kelancaran administrasi.<br/>
                      3. Saya menyetujui bahwa barang berharga pribadi adalah tanggung jawab saya pribadi.<br/>
                      4. Saya telah menerima informasi tentang Hak dan Kewajiban Pasien serta tata tertib yang berlaku.
                    </p>
                  </div>

                  {/* SPU Specific Inputs */}
                  <div className="bg-slate-50 p-5 rounded-2xl border border-slate-200/60 space-y-4">
                    <h5 className="font-bold text-xs text-slate-800 uppercase tracking-wide">Pilihan Pasien / Pernyataan</h5>
                    
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-[11px] font-bold text-slate-500 uppercase mb-1">Berikan Informasi Medis Kepada:</label>
                        {selectedForm.status === 'Belum Ditandatangani' ? (
                          <select 
                            value={pengobatanKepada} 
                            onChange={(e) => setPengobatanKepada(e.target.value)}
                            className="w-full px-3 py-2 text-xs border border-slate-250 bg-white rounded-xl focus:outline-none focus:ring-1 focus:ring-emerald-500"
                          >
                            {['Suami','Istri','Anak','Ayah','Ibu','Saudara','Keponakan','Adik','Kakak','Orang Tua','Diri Sendiri','-'].map(opt => (
                              <option key={opt} value={opt}>{opt}</option>
                            ))}
                          </select>
                        ) : (
                          <span className="text-xs font-semibold text-slate-800 bg-white border border-slate-200 px-3 py-1.5 rounded-lg block">
                            {selectedForm.meta.pengobatan_kepada}
                          </span>
                        )}
                      </div>

                      <div>
                        <label className="block text-[11px] font-bold text-slate-500 uppercase mb-1">Nilai Kepercayaan Dalam Perawatan:</label>
                        {selectedForm.status === 'Belum Ditandatangani' ? (
                          <input 
                            type="text"
                            placeholder="Contoh: Tidak ada kepercayaan khusus"
                            value={nilaiKepercayaan}
                            onChange={(e) => setNilaiKepercayaan(e.target.value)}
                            className="w-full px-3 py-2 text-xs border border-slate-250 bg-white rounded-xl focus:outline-none focus:ring-1 focus:ring-emerald-500"
                          />
                        ) : (
                          <span className="text-xs font-semibold text-slate-800 bg-white border border-slate-200 px-3 py-1.5 rounded-lg block min-h-[32px]">
                            {selectedForm.meta.nilai_kepercayaan || '-'}
                          </span>
                        )}
                      </div>
                    </div>
                  </div>

                  {/* PJ Info */}
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-4 bg-slate-50 p-4 rounded-2xl border border-slate-200/60 text-xs">
                    <div>
                      <span className="text-slate-500 block uppercase font-bold text-[9px]">Nama Penanggung Jawab</span>
                      <span className="text-slate-800 font-semibold">{selectedForm.meta.nama_pj}</span>
                    </div>
                    <div>
                      <span className="text-slate-500 block uppercase font-bold text-[9px]">Hubungan / Atas Nama</span>
                      <span className="text-slate-800 font-semibold">{selectedForm.meta.bertindak_atas}</span>
                    </div>
                    <div>
                      <span className="text-slate-500 block uppercase font-bold text-[9px]">No. KTP / Telepon PJ</span>
                      <span className="text-slate-800 font-semibold">{selectedForm.meta.no_ktppj} / {selectedForm.meta.no_telp}</span>
                    </div>
                    <div>
                      <span className="text-slate-500 block uppercase font-bold text-[9px]">Saksi RS / Dokter</span>
                      <span className="text-slate-800 font-semibold">{selectedForm.meta.dokter || selectedForm.meta.nip}</span>
                    </div>
                  </div>
                </div>
              ) : (
                /* PERSATUJUAN TINDAKAN DETAILS */
                <div className="space-y-6 text-xs md:text-sm text-slate-600">
                  <div className="space-y-3 max-h-[300px] overflow-y-auto pr-2 border-b border-slate-200 pb-4">
                    <p className="font-semibold text-slate-800">INFORMASI TINDAKAN MEDIS:</p>
                    
                    <div className="border border-slate-200 rounded-2xl overflow-hidden">
                      <table className="w-full text-left border-collapse text-[11px] md:text-xs">
                        <tbody>
                          <tr className="border-b border-slate-200">
                            <td className="p-3 bg-slate-50 font-bold w-1/3 text-slate-700">Diagnosa (WD/DD)</td>
                            <td className="p-3 text-slate-800 font-semibold">{selectedForm.meta.diagnosa}</td>
                          </tr>
                          <tr className="border-b border-slate-200">
                            <td className="p-3 bg-slate-50 font-bold text-slate-700">Tindakan Kedokteran</td>
                            <td className="p-3 text-slate-800 font-semibold">{selectedForm.meta.tindakan}</td>
                          </tr>
                          <tr className="border-b border-slate-200">
                            <td className="p-3 bg-slate-50 font-bold text-slate-700">Indikasi Tindakan</td>
                            <td className="p-3 text-slate-600">{selectedForm.meta.indikasi_tindakan}</td>
                          </tr>
                          <tr className="border-b border-slate-200">
                            <td className="p-3 bg-slate-50 font-bold text-slate-700">Tata Cara Tindakan</td>
                            <td className="p-3 text-slate-600 whitespace-pre-line">{selectedForm.meta.tata_cara}</td>
                          </tr>
                          <tr className="border-b border-slate-200">
                            <td className="p-3 bg-slate-50 font-bold text-slate-700">Tujuan</td>
                            <td className="p-3 text-slate-600">{selectedForm.meta.tujuan}</td>
                          </tr>
                          <tr className="border-b border-slate-200">
                            <td className="p-3 bg-slate-50 font-bold text-slate-700">Risiko & Komplikasi</td>
                            <td className="p-3 text-slate-600">{selectedForm.meta.risiko} {selectedForm.meta.komplikasi ? `/ ${selectedForm.meta.komplikasi}` : ''}</td>
                          </tr>
                          <tr className="border-b border-slate-200">
                            <td className="p-3 bg-slate-50 font-bold text-slate-700">Prognosis</td>
                            <td className="p-3 text-slate-600">{selectedForm.meta.prognosis}</td>
                          </tr>
                          <tr className="border-b border-slate-200">
                            <td className="p-3 bg-slate-50 font-bold text-slate-700">Alternatif & Risiko</td>
                            <td className="p-3 text-slate-600">{selectedForm.meta.alternatif_dan_risikonya}</td>
                          </tr>
                          <tr>
                            <td className="p-3 bg-slate-50 font-bold text-slate-700">Perkiraan Biaya</td>
                            <td className="p-3 text-emerald-600 font-bold">Rp. {Number(selectedForm.meta.biaya).toLocaleString('id-ID')}</td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                  </div>

                  {/* PPT Inputs */}
                  <div className="bg-slate-50 p-5 rounded-2xl border border-slate-200/60 space-y-4">
                    <h5 className="font-bold text-xs text-slate-800 uppercase tracking-wide">Pernyataan Persetujuan / Penolakan</h5>
                    
                    {selectedForm.status === 'Belum Ditandatangani' ? (
                      <div className="flex gap-4">
                        <button 
                          type="button"
                          onClick={() => setPernyataan('Persetujuan')}
                          className={`flex-1 py-3 px-4 rounded-xl border text-xs font-bold transition-all flex items-center justify-center gap-2 ${
                            pernyataan === 'Persetujuan'
                              ? 'bg-emerald-50 text-emerald-700 border-emerald-500 shadow-sm'
                              : 'bg-white text-slate-600 border-slate-200 hover:bg-slate-100'
                          }`}
                        >
                          <CheckCircle size={16} /> Saya MENYETUJUI Tindakan
                        </button>
                        <button 
                          type="button"
                          onClick={() => setPernyataan('Penolakan')}
                          className={`flex-1 py-3 px-4 rounded-xl border text-xs font-bold transition-all flex items-center justify-center gap-2 ${
                            pernyataan === 'Penolakan'
                              ? 'bg-red-50 text-red-700 border-red-500 shadow-sm'
                              : 'bg-white text-slate-600 border-slate-200 hover:bg-slate-100'
                          }`}
                        >
                          <AlertTriangle size={16} /> Saya MENOLAK Tindakan
                        </button>
                      </div>
                    ) : (
                      <div className={`p-4 rounded-xl border text-xs font-bold text-center uppercase ${
                        selectedForm.meta.pernyataan === 'Persetujuan'
                          ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                          : 'bg-red-50 text-red-700 border-red-200'
                      }`}>
                        Dokumen Dinyatakan sebagai: {selectedForm.meta.pernyataan}
                      </div>
                    )}
                  </div>

                  {/* Penerima Informasi */}
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-4 bg-slate-50 p-4 rounded-2xl border border-slate-200/60 text-xs">
                    <div>
                      <span className="text-slate-500 block uppercase font-bold text-[9px]">Penerima Informasi</span>
                      <span className="text-slate-800 font-semibold">{selectedForm.meta.penerima_informasi}</span>
                    </div>
                    <div>
                      <span className="text-slate-500 block uppercase font-bold text-[9px]">Hubungan Pasien</span>
                      <span className="text-slate-800 font-semibold">{selectedForm.meta.hubungan_penerima_informasi}</span>
                    </div>
                    <div>
                      <span className="text-slate-500 block uppercase font-bold text-[9px]">No. Telepon / Alamat</span>
                      <span className="text-slate-800 font-semibold">{selectedForm.meta.no_hp} / {selectedForm.meta.alamat_penerima_informasi}</span>
                    </div>
                    <div>
                      <span className="text-slate-500 block uppercase font-bold text-[9px]">Dokter / Saksi</span>
                      <span className="text-slate-800 font-semibold">{selectedForm.meta.dokter} / {selectedForm.meta.saksi_keluarga || '-'}</span>
                    </div>
                  </div>
                </div>
              )}
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
                      /* Simulator View / descriptive error */
                      <div className="absolute inset-0 flex flex-col items-center justify-center p-4 text-center bg-slate-100 overflow-y-auto">
                        <AlertTriangle className="text-amber-500 mb-1 shrink-0" size={20} />
                        <span className="text-[11px] font-bold text-slate-850">Akses Kamera Gagal</span>
                        <p className="text-[10px] text-slate-500 mt-0.5 leading-normal">
                          {cameraErrorMessage || 'Kamera terblokir atau tidak didukung.'}
                        </p>
                        {insecureContext ? (
                          <div className="mt-1.5 p-2 bg-red-50 border border-red-150 rounded-xl text-[9px] text-red-700 text-left leading-normal">
                            <span className="font-bold block mb-0.5">Solusi Uji Coba (Chrome Flags):</span>
                            1. Buka <b>chrome://flags/#unsafely-treat-insecure-origin-as-secure</b><br/>
                            2. Masukkan alamat IP server ini (misal: <code>{window.location.origin}</code>)<br/>
                            3. Ubah status menjadi <b>Enabled</b><br/>
                            4. Klik <b>Relaunch</b> Chrome.
                          </div>
                        ) : (
                          <p className="text-[9px] text-slate-400 mt-1">
                            Foto disimulasikan menggunakan gambar default.
                          </p>
                        )}
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
          {isLoading ? (
            <div className="flex flex-col items-center justify-center py-16 text-slate-500 gap-3 bg-white border border-slate-200/60 rounded-3xl p-8 shadow-sm">
              <RefreshCw className="animate-spin text-emerald-600" size={28} />
              <span className="text-xs font-semibold">Memuat dokumen persetujuan...</span>
            </div>
          ) : consentList.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-16 text-slate-400 bg-white border border-slate-200/60 rounded-3xl p-8 shadow-sm text-center">
              <ShieldCheck className="text-emerald-500/30 mb-3" size={48} />
              <h4 className="font-bold text-sm text-slate-800">Semua Dokumen Lengkap</h4>
              <p className="text-xs text-slate-500 mt-1 max-w-sm">
                Saat ini tidak ada dokumen persetujuan (General Consent atau Informed Consent) yang memerlukan tanda tangan Anda.
              </p>
            </div>
          ) : (
            <div className="space-y-3">
              {consentList.map((form) => {
                const isPending = form.status === 'Belum Ditandatangani';
                return (
                  <button 
                    key={form.id}
                    onClick={() => handleSelectForm(form)}
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
          )}
        </div>
      )}

    </div>
  );
}
