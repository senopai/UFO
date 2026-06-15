import React, { useState } from 'react';
import Sidebar from './components/Sidebar';
import Header from './components/Header';
import Dashboard from './components/Dashboard';
import Booking from './components/Booking';
import History from './components/History';
import EConsent from './components/EConsent';
import Settings from './components/Settings';
import CardPasien from './components/CardPasien';
import { 
  HeartPulse, 
  Lock, 
  User as UserIcon, 
  RefreshCw, 
  ShieldCheck, 
  X,
  AlertCircle,
  QrCode,
  CheckCircle,
  CalendarCheck
} from 'lucide-react';

// Mock Visit Data Generator
const generateMockVisits = () => {
  const clinics = [
    'Poliklinik Penyakit Dalam',
    'Poliklinik Bedah',
    'Poliklinik Syaraf / Neurologi',
    'Poliklinik Gigi & Mulut',
    'Poliklinik Mata'
  ];
  const doctors = [
    'dr. Hilyatul Nadia, Sp.PD',
    'dr. Hilyatul Nadia, Sp.B',
    'dr. Sri Rahma, Sp.N',
    'Drg. Qotrunnada',
    'dr. Dian Safitri, Sp.M'
  ];
  const diagnoses = [
    'K30 - Dyspepsia',
    'I10 - Essential Hypertension',
    'M54.5 - Low Back Pain',
    'K04 - Diseases of pulp and periapical tissues',
    'H10.9 - Conjunctivitis, unspecified'
  ];
  const therapies = [
    'Lansoprazole 30mg (2x1), Sucralfate suspensi (3x1 Cth)',
    'Amlodipine 5mg (1x1)',
    'Meloxicam 15mg (1x1), Eperisone 50mg (3x1)',
    'Asam Mefenamat 500mg (3x1 PRN), Amoxicillin 500mg (3x1)',
    'Cendo Xitrol tetes mata (4x1 tetes pada mata kanan/kiri)'
  ];

  const visits = [];
  const baseDate = new Date(2026, 5, 12); // June 12, 2026
  for (let i = 0; i < 19; i++) {
    const date = new Date(baseDate);
    date.setDate(baseDate.getDate() - (i * 3)); // every 3 days
    const formattedDate = date.toLocaleDateString('id-ID', {
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    });

    const clinicIdx = i % clinics.length;
    visits.push({
      id: `visit-${19 - i}`,
      noRawat: `2026/06/${String(100 + i).slice(1)}`,
      noReg: String(1000 + i).slice(1),
      date: formattedDate,
      type: 'Rawat Jalan',
      clinic: clinics[clinicIdx],
      doctor: doctors[clinicIdx],
      status: 'Selesai',
      diagnosa: diagnoses[clinicIdx],
      tensi: '120/80 mmHg',
      suhu: '36.5 °C',
      berat: '65 kg',
      resep: therapies[clinicIdx],
      tindakan: 'Konsultasi & Pemeriksaan Fisik'
    });
  }
  return visits;
};

export default function App() {
  // Authentication State
  const [isLoggedIn, setIsLoggedIn] = useState(!!window.__PRELOADED_USER__);
  const [loginForm, setLoginForm] = useState({ norm: '', password: '' });
  const [loginErrors, setLoginErrors] = useState({});

  // App General State
  const [activeTab, setActiveTab] = useState('dashboard');
  const [historyDefaultTab, setHistoryDefaultTab] = useState('lab');
  const [user, setUser] = useState(window.__PRELOADED_USER__ || null);

  // Active Appointment / Queue State
  const [activeQueue, setActiveQueue] = useState(null);

  // Statistics & Counts
  const [counts, setCounts] = useState({
    labCount: 0,
    radCount: 0,
    visitCount: 0,
    rajalCount: 0,
    ranapCount: 0
  });

  // Notifications State
  const [notifications, setNotifications] = useState([
    { id: 1, title: 'Hasil Lab PK Tersedia', desc: 'Hasil laboratorium terbaru sudah keluar.', time: '1h ago' },
    { id: 2, title: 'Permintaan Persetujuan', desc: 'Silakan tandatangani dokumen Persetujuan Umum.', time: '1d ago' },
  ]);

  const [toast, setToast] = useState(null);
  
  // Booking State Success Modal
  const [bookingSuccessData, setBookingSuccessData] = useState(null);

  // Medical Histories from Database
  const [labHistory, setLabHistory] = useState([]);
  const [radHistory, setRadHistory] = useState([]);
  const [visitHistory, setVisitHistory] = useState([]);
  const [isLoadingHistory, setIsLoadingHistory] = useState(false);

  // Dynamic Metrics counts
  const [pendingConsentsCount, setPendingConsentsCount] = useState(1);
  const recentResultsCount = counts.labCount + counts.radCount;
  
  const [hospitalName, setHospitalName] = useState('Rumah Sakit');
  const [schedules, setSchedules] = useState([]);

  // Fetch Dashboard Stats and Active Queue
  const fetchDashboardData = async () => {
    try {
      const res = await fetch('api.php?action=get_dashboard_data');
      const data = await res.json();
      if (data.success) {
        setActiveQueue(data.activeQueue);
        setCounts(data.counts);
        setHospitalName(data.hospitalName || 'Rumah Sakit');
        setSchedules(data.schedules || []);
      }
    } catch (err) {
      console.error('Gagal mengambil data dashboard:', err);
    }
  };

  // Fetch Medical History Records
  const fetchMedicalRecords = async () => {
    setIsLoadingHistory(true);
    try {
      const res = await fetch('api.php?action=get_medical_records');
      const data = await res.json();
      if (data.success) {
        setLabHistory(data.labs || []);
        setRadHistory(data.rads || []);
        setVisitHistory(data.visits || []);
      }
    } catch (err) {
      console.error('Gagal mengambil riwayat medis:', err);
    } finally {
      setIsLoadingHistory(false);
    }
  };

  // Fetch data on mount if already logged in
  React.useEffect(() => {
    if (isLoggedIn) {
      fetchDashboardData();
      fetchMedicalRecords();
    }
  }, [isLoggedIn]);

  // Show a temporary toast notification
  const triggerToast = (title, message) => {
    setToast({ title, message });
    setTimeout(() => {
      setToast(null);
    }, 4000);
  };

  const handleAddNotification = (newNotif) => {
    setNotifications(prev => [
      { id: Date.now(), ...newNotif },
      ...prev
    ]);
    triggerToast(newNotif.title, newNotif.desc);
  };

  const handleCheckIn = async () => {
    if (!activeQueue) return;
    try {
      const formData = new FormData();
      formData.append('kd_dokter', activeQueue.kd_dokter);
      formData.append('kd_poli', activeQueue.kd_poli);
      formData.append('date', activeQueue.date);
      formData.append('queueNum', activeQueue.queueNum);

      const res = await fetch('api.php?action=check_in', {
        method: 'POST',
        body: formData
      });
      const data = await res.json();
      if (data.success) {
        setActiveQueue(prev => ({
          ...prev,
          status: 'Terdaftar'
        }));
        handleAddNotification({
          title: 'Check-in Berhasil',
          desc: `Antrean Anda telah diaktifkan. Silakan menuju ruang tunggu ${activeQueue.clinic}.`,
          time: 'Just now'
        });
        fetchDashboardData();
      } else {
        triggerToast('Gagal Check-in', data.message || 'Terjadi kesalahan saat check-in.');
      }
    } catch (err) {
      triggerToast('Gagal Check-in', 'Gagal menghubungi server.');
    }
  };

  const handleBookingSuccess = (data) => {
    setBookingSuccessData(data);
  };

  const handleConfirmBookingInList = () => {
    // Refresh dashboard data to reflect the new booking
    fetchDashboardData();

    handleAddNotification({
      title: 'Booking Berhasil',
      desc: `Booking ke ${bookingSuccessData.clinicName} berhasil dibuat. Silakan check-in pada hari H.`,
      time: 'Just now'
    });

    setBookingSuccessData(null);
    setActiveTab('dashboard');
  };

  // Login handler
  const handleLoginSubmit = async (e) => {
    e.preventDefault();
    const errors = {};
    if (!loginForm.norm) errors.norm = 'Nomor Rekam Medis wajib diisi';
    if (!loginForm.password) errors.password = 'Password wajib diisi';

    if (Object.keys(errors).length > 0) {
      setLoginErrors(errors);
      return;
    }

    try {
      const formData = new FormData();
      formData.append('norm', loginForm.norm);
      formData.append('password', loginForm.password);

      const res = await fetch('api.php?action=login', {
        method: 'POST',
        body: formData
      });
      const data = await res.json();
      if (data.success) {
        setUser(data.user);
        setIsLoggedIn(true);
        triggerToast('Login Berhasil', 'Selamat datang kembali di portal EPasien.');
      } else {
        setLoginErrors({ form: data.message || 'Nomor RM atau password salah.' });
      }
    } catch (err) {
      setLoginErrors({ form: 'Koneksi ke server gagal. Coba lagi nanti.' });
    }
  };

  const handleLogout = async () => {
    try {
      await fetch('api.php?action=logout');
    } catch (err) {
      console.error('Logout error', err);
    }
    setIsLoggedIn(false);
    setUser(null);
    setActiveTab('dashboard');
    setLoginForm({ norm: '', password: '' });
  };

  // Render sub components
  const renderTabContent = () => {
    switch (activeTab) {
      case 'dashboard':
        return (
          <Dashboard 
            user={user}
            activeQueue={activeQueue}
            handleCheckIn={handleCheckIn}
            setActiveTab={setActiveTab}
            setHistoryDefaultTab={setHistoryDefaultTab}
            pendingConsentsCount={pendingConsentsCount}
            recentResultsCount={recentResultsCount}
            labCount={counts.labCount}
            radCount={counts.radCount}
            visitCount={counts.visitCount}
            rajalCount={counts.rajalCount}
            ranapCount={counts.ranapCount}
            hospitalName={hospitalName}
            schedules={schedules}
          />
        );
      case 'booking':
        return (
          <Booking 
            user={user}
            onBookingSuccess={handleBookingSuccess}
          />
        );
      case 'history':
        return (
          <History 
            user={user}
            onAddNotification={handleAddNotification}
            defaultTab={historyDefaultTab}
            labHistory={labHistory}
            radHistory={radHistory}
            visitHistory={visitHistory}
          />
        );
      case 'consent':
        return (
          <EConsent 
            user={user}
            onAddNotification={handleAddNotification}
            onConsentSigned={() => setPendingConsentsCount(prev => Math.max(0, prev - 1))}
          />
        );
      case 'card':
        return (
          <CardPasien 
            user={user}
          />
        );
      case 'settings':
        return (
          <Settings 
            user={user}
            onAddNotification={handleAddNotification}
            onPasswordUpdated={() => {}}
          />
        );
      default:
        return <Dashboard user={user} />;
    }
  };

  if (!isLoggedIn) {
    /* Login Page render */
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-200/60 flex items-center justify-center p-4 relative overflow-hidden font-sans text-slate-700">
        {/* Glows */}
        <div className="absolute top-0 left-0 w-96 h-96 bg-emerald-400/20 rounded-full blur-3xl"></div>
        <div className="absolute bottom-0 right-0 w-96 h-96 bg-indigo-400/20 rounded-full blur-3xl"></div>

        <div className="w-full max-w-md glass-panel p-6 md:p-8 rounded-3xl space-y-6 relative border-slate-200/80 shadow-2xl">
          {/* Logo Branding */}
          <div className="text-center space-y-2">
            <div className="inline-flex p-3 bg-emerald-500/10 text-emerald-600 rounded-2xl border border-emerald-500/20">
              <HeartPulse size={36} className="animate-pulse" />
            </div>
            <div>
              <h1 className="text-2xl font-extrabold font-heading text-slate-800 tracking-wide leading-none">EPasien</h1>
              <span className="text-[10px] text-emerald-600 font-bold uppercase tracking-widest mt-1 block">SIMKES KHANZA</span>
            </div>
            <p className="text-xs text-slate-500 px-4">
              Nomor rekam medis & password dapat ditanyakan ke petugas saat registrasi offline di klinik.
            </p>
          </div>

          <form onSubmit={handleLoginSubmit} className="space-y-4">
            {/* Username/No.RM */}
            <div className="space-y-1.5">
              <label className="text-xs font-semibold text-slate-600 flex items-center gap-1.5">
                <UserIcon size={14} /> Nomor Rekam Medis (No.RM)
              </label>
              <input 
                type="text" 
                value={loginForm.norm}
                onChange={(e) => {
                  setLoginForm({ ...loginForm, norm: e.target.value });
                  if (loginErrors.norm) setLoginErrors(prev => ({ ...prev, norm: '' }));
                }}
                className="glass-input w-full py-2.5 px-4 rounded-xl text-sm" 
                placeholder="Masukkan Nomor RM (misal: 001294)"
                maxLength={20}
              />
              {loginErrors.norm && <span className="text-[10px] text-rose-600 flex items-center gap-1"><AlertCircle size={10} /> {loginErrors.norm}</span>}
            </div>

            {/* Password */}
            <div className="space-y-1.5">
              <label className="text-xs font-semibold text-slate-600 flex items-center gap-1.5">
                <Lock size={14} /> Password
              </label>
              <input 
                type="password" 
                value={loginForm.password}
                onChange={(e) => {
                  setLoginForm({ ...loginForm, password: e.target.value });
                  if (loginErrors.password) setLoginErrors(prev => ({ ...prev, password: '' }));
                }}
                className="glass-input w-full py-2.5 px-4 rounded-xl text-sm" 
                placeholder="Masukkan kata sandi akun"
              />
              {loginErrors.password && <span className="text-[10px] text-rose-600 flex items-center gap-1"><AlertCircle size={10} /> {loginErrors.password}</span>}
            </div>



            {loginErrors.form && (
              <div className="p-3 bg-rose-50 border border-rose-100 text-rose-600 rounded-xl text-xs flex items-center gap-2">
                <AlertCircle size={14} className="shrink-0" />
                <span>{loginErrors.form}</span>
              </div>
            )}

            {/* Login button */}
            <button 
              type="submit"
              className="w-full py-3 px-4 bg-emerald-500 hover:bg-emerald-400 text-white font-semibold text-sm rounded-xl shadow-[0_4px_20px_rgba(16,185,129,0.15)] transition-all duration-200 mt-2 cursor-pointer"
            >
              Masuk ke Aplikasi
            </button>
          </form>

          {/* Copyright */}
          <div className="text-[10px] text-center text-slate-400">
            &copy; 2026 SIMKES Khanza. All rights reserved.
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-200/60 flex flex-col md:flex-row relative font-sans text-slate-700 pb-20 md:pb-0">
      
      {/* Toast Notification */}
      {toast && (
        <div className="fixed top-5 right-5 z-50 glass-panel border-emerald-500/30 bg-emerald-50/95 p-4 rounded-2xl shadow-2xl flex items-start gap-3 max-w-sm animate-in fade-in slide-in-from-top-4 duration-200">
          <div className="p-2 bg-emerald-100 text-emerald-600 rounded-xl">
            <CheckCircle size={18} />
          </div>
          <div className="space-y-0.5">
            <h4 className="text-xs font-bold text-slate-800">{toast.title}</h4>
            <p className="text-[11px] text-slate-600 leading-normal">{toast.message}</p>
          </div>
          <button 
            onClick={() => setToast(null)}
            className="text-slate-400 hover:text-slate-600"
          >
            <X size={14} />
          </button>
        </div>
      )}

      {/* Booking Success Modal */}
      {bookingSuccessData && (
        <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="w-full max-w-md glass-panel p-6 md:p-8 rounded-3xl space-y-6 relative border-slate-200 shadow-2xl text-center">
            
            <div className="inline-flex p-3 bg-emerald-50 text-emerald-600 rounded-full border border-emerald-100 animate-bounce">
              <CalendarCheck size={36} />
            </div>

            <div className="space-y-2">
              <h3 className="text-lg md:text-xl font-bold font-heading text-slate-800">Booking Berhasil Dibuat!</h3>
              <p className="text-xs text-slate-500 leading-relaxed">
                Pendaftaran antrean konsultasi rawat jalan skema Pasien Umum berhasil dijadwalkan.
              </p>
            </div>

            {/* Ticket Summary */}
            <div className="bg-slate-50/80 border border-slate-200/60 rounded-2xl p-4 text-xs text-left space-y-3 relative">
              <div className="flex justify-between">
                <span className="text-slate-500">Pasien:</span>
                <span className="text-slate-700 font-semibold">{bookingSuccessData.patientName}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500">Klinik Tujuan:</span>
                <span className="text-slate-700 font-semibold">{bookingSuccessData.clinicName}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500">Dokter:</span>
                <span className="text-slate-700 font-semibold">{bookingSuccessData.doctorName}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500">Tanggal Periksa:</span>
                <span className="text-slate-700 font-semibold">{bookingSuccessData.date}</span>
              </div>
              <div className="flex justify-between border-t border-slate-200/80 pt-3">
                <span className="text-slate-500">Biaya Administrasi:</span>
                <span className="text-emerald-600 font-bold">{bookingSuccessData.fee}</span>
              </div>
            </div>

            <div className="flex flex-col gap-2">
              <button 
                onClick={handleConfirmBookingInList}
                className="w-full py-2.5 px-4 bg-emerald-500 hover:bg-emerald-400 text-white font-semibold text-xs rounded-xl shadow-lg transition-all cursor-pointer"
              >
                Konfirmasi & Simpan
              </button>
              <button 
                onClick={() => setBookingSuccessData(null)}
                className="w-full py-2.5 px-4 bg-slate-100 hover:bg-slate-200/80 border border-slate-200/80 text-slate-700 font-semibold text-xs rounded-xl transition-all cursor-pointer"
              >
                Batal
              </button>
            </div>

          </div>
        </div>
      )}

      {/* Navigation Left Sidebar */}
      <Sidebar 
        activeTab={activeTab} 
        setActiveTab={setActiveTab} 
        user={user} 
        onLogout={handleLogout}
        historyDefaultTab={historyDefaultTab}
        setHistoryDefaultTab={setHistoryDefaultTab}
      />

      {/* Main Workspace Frame */}
      <div className="flex-1 flex flex-col min-w-0">
        <Header user={user} onLogout={handleLogout} />
        
        {/* Main Content Area */}
        <main className="flex-1 py-6 px-4 md:py-8 md:px-8 max-w-7xl w-full mx-auto overflow-y-auto">
          {renderTabContent()}
        </main>
      </div>

    </div>
  );
}
