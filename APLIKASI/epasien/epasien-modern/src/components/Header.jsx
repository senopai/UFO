import React, { useState, useEffect } from 'react';
import { Bell, User, LogOut, HeartPulse } from 'lucide-react';

export default function Header({ user, onLogout }) {
  const [greeting, setGreeting] = useState('Selamat Pagi');
  const [showNotifications, setShowNotifications] = useState(false);
  const [showProfileMenu, setShowProfileMenu] = useState(false);

  useEffect(() => {
    const hrs = new Date().getHours();
    if (hrs < 12) setGreeting('Selamat Pagi');
    else if (hrs < 16) setGreeting('Selamat Siang');
    else if (hrs < 20) setGreeting('Selamat Sore');
    else setGreeting('Selamat Malam');
  }, []);

  const notifications = [
    { id: 1, title: 'Check-in Berhasil', desc: 'Check-in untuk Poli Dalam hari ini berhasil.', time: '5m ago' },
    { id: 2, title: 'Hasil Lab PK Tersedia', desc: 'Hasil laboratorium tanggal 12 Juni sudah keluar.', time: '1h ago' },
    { id: 3, title: 'Permintaan Persetujuan', desc: 'Silakan tandatangani dokumen Persetujuan Umum.', time: '1d ago' },
  ];

  return (
    <header className="sticky top-0 z-30 flex items-center justify-between py-4 px-4 md:px-8 border-b border-slate-200/50 glass-panel">
      {/* Mobile Branding - hidden on desktop */}
      <div className="flex md:hidden items-center gap-2 max-w-[70%]">
        <img src="images/logo_rsm.png" alt="Logo RSM" className="w-7 h-7 object-contain drop-shadow-sm" />
        <span className="font-heading font-bold text-slate-800 text-[15px] tracking-wide truncate">E-Pasien RSM Mardhatillah</span>
      </div>

      {/* Greeting - desktop only */}
      <div className="hidden md:block">
        <h2 className="text-lg font-semibold font-heading text-slate-800">{greeting}, {user.name}!</h2>
        <p className="text-xs text-slate-500">Kelola kunjungan medis dan rekam medis Anda secara praktis.</p>
      </div>

      {/* Right Tools: Notifications & Profile */}
      <div className="flex items-center gap-3">
        {/* Notifications */}
        <div className="relative">
          <button 
            onClick={() => {
              setShowNotifications(!showNotifications);
              setShowProfileMenu(false);
            }}
            className="p-2 text-slate-600 hover:text-emerald-600 bg-slate-100 hover:bg-slate-200/60 border border-slate-200 rounded-xl transition-all duration-200"
          >
            <div className="relative">
              <Bell size={20} />
              <span className="absolute top-0 right-0 w-2 h-2 bg-emerald-600 rounded-full"></span>
            </div>
          </button>

          {/* Notifications Dropdown */}
          {showNotifications && (
            <div className="absolute right-0 mt-3 w-80 glass-panel border border-slate-200/80 rounded-2xl shadow-2xl p-4 overflow-hidden z-50 animate-in fade-in slide-in-from-top-2 duration-200">
              <div className="flex justify-between items-center pb-3 border-b border-slate-100 mb-3">
                <h4 className="font-semibold text-sm text-slate-800">Notifikasi Terbaru</h4>
                <span className="text-[10px] text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-full font-semibold border border-emerald-100">3 Baru</span>
              </div>
              <div className="space-y-3 max-h-60 overflow-y-auto pr-1">
                {notifications.map((notif) => (
                  <div key={notif.id} className="p-2 hover:bg-slate-50 rounded-xl border border-transparent hover:border-slate-100 transition-all">
                    <div className="flex justify-between items-baseline mb-0.5">
                      <h5 className="text-xs font-semibold text-slate-800">{notif.title}</h5>
                      <span className="text-[9px] text-slate-400">{notif.time}</span>
                    </div>
                    <p className="text-[10px] text-slate-600 leading-normal">{notif.desc}</p>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* User Avatar & Dropdown for Mobile / Desktop Quick Settings */}
        <div className="relative">
          <button 
            onClick={() => {
              setShowProfileMenu(!showProfileMenu);
              setShowNotifications(false);
            }}
            className="flex items-center gap-2 p-1 md:pr-3 bg-slate-100 hover:bg-slate-200/60 border border-slate-200 rounded-xl transition-all duration-200"
          >
            <img 
              src={user.photo || "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=100&h=100&q=80"} 
              alt="Avatar" 
              className="w-8 h-8 rounded-full border border-emerald-500/20 object-cover"
            />
            <span className="hidden md:inline text-xs font-semibold text-slate-700 hover:text-slate-900 truncate max-w-[100px]">{user.name}</span>
          </button>

          {/* Profile Dropdown */}
          {showProfileMenu && (
            <div className="absolute right-0 mt-3 w-56 glass-panel border border-slate-200/80 rounded-2xl shadow-2xl p-2 z-50 animate-in fade-in slide-in-from-top-2 duration-200">
              <div className="p-3 border-b border-slate-100 mb-1 md:hidden">
                <h4 className="font-semibold text-xs text-slate-800 truncate">{user.name}</h4>
                <span className="text-[10px] text-slate-400">No.RM: {user.norm}</span>
              </div>
              <div className="space-y-1">
                <div className="px-3 py-1 text-[10px] uppercase font-semibold text-emerald-600 tracking-wider">Aktivitas Pasien</div>
                <button 
                  onClick={onLogout}
                  className="flex items-center gap-3 w-full py-2.5 px-3 text-left rounded-xl text-xs font-semibold text-rose-600 hover:bg-rose-50 transition-all duration-150"
                >
                  <LogOut size={16} />
                  <span>Log Out</span>
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
