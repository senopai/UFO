import React, { useState, useEffect } from 'react';
import { Bell, User, LogOut, HeartPulse } from 'lucide-react';

export default function Header({ user, onLogout }) {
  const [greeting, setGreeting] = useState('Selamat Pagi');
  const [showNotifications, setShowNotifications] = useState(false);
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const [notifications, setNotifications] = useState([]);

  useEffect(() => {
    const hrs = new Date().getHours();
    if (hrs < 12) setGreeting('Selamat Pagi');
    else if (hrs < 16) setGreeting('Selamat Siang');
    else if (hrs < 20) setGreeting('Selamat Sore');
    else setGreeting('Selamat Malam');

    const fetchNotifications = async () => {
      try {
        const baseUrl = import.meta.env.DEV ? 'http://localhost:5000' : '';
        const res = await fetch(`${baseUrl}/api.php?action=get_notifications`);
        const data = await res.json();
        if (data.success && data.notifications) {
          setNotifications(data.notifications);
        }
      } catch (e) {
        console.error('Failed to fetch notifications:', e);
      }
    };

    fetchNotifications();
    // Optional: Set interval here if you want it to auto-refresh every X minutes
    // const interval = setInterval(fetchNotifications, 60000 * 5); // 5 minutes
    // return () => clearInterval(interval);
  }, []);

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
