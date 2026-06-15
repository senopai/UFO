import React, { useState, useEffect } from 'react';
import { 
  LayoutDashboard, 
  CalendarRange, 
  History as HistoryIcon, 
  FileSignature, 
  Settings, 
  LogOut,
  HeartPulse,
  CreditCard
} from 'lucide-react';

export default function Sidebar({ 
  activeTab, 
  setActiveTab, 
  user, 
  onLogout, 
  historyDefaultTab, 
  setHistoryDefaultTab 
}) {
  const [isHistoryOpen, setIsHistoryOpen] = useState(activeTab === 'history');
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  // Sync expanded state with active tab
  useEffect(() => {
    if (activeTab === 'history') {
      setIsHistoryOpen(true);
    } else {
      setIsMobileMenuOpen(false);
    }
  }, [activeTab]);

  const menuItems = [
    { id: 'dashboard', name: 'Dashboard', icon: LayoutDashboard },
    { id: 'booking', name: 'Booking Pasien', icon: CalendarRange },
    { id: 'history', name: 'Hasil Lab & Rad', icon: HistoryIcon },
    { id: 'consent', name: 'E-Persetujuan', icon: FileSignature },
    { id: 'card', name: 'Kartu Pasien', icon: CreditCard },
    { id: 'settings', name: 'Pengaturan', icon: Settings },
  ];

  const subItems = [
    { id: 'lab', name: 'Laboratorium' },
    { id: 'rad', name: 'Radiologi' },
    { id: 'kunjungan', name: 'Riwayat Kunjungan' },
  ];

  return (
    <aside className="fixed bottom-0 left-0 z-40 w-full md:sticky md:top-0 md:h-screen md:w-64 glass-panel border-t md:border-t-0 md:border-r border-slate-200/50 flex flex-col justify-between py-4 px-3 md:py-6 md:px-4">
      {/* Brand Logo - hidden on mobile, shown on desktop */}
      <div className="hidden md:flex items-center gap-3 px-2 mb-8">
        <div className="p-2 bg-emerald-500/10 text-emerald-600 rounded-xl border border-emerald-500/20">
          <HeartPulse size={28} className="animate-pulse" />
        </div>
        <div>
          <h1 className="text-xl font-bold font-heading text-slate-800 tracking-wide leading-none">EPasien</h1>
          <span className="text-[10px] text-emerald-600 font-semibold uppercase tracking-wider">SIMKES KHANZA</span>
        </div>
      </div>

      {/* Navigation Links */}
      <nav className="flex md:flex-col justify-around md:justify-start gap-1 w-full md:space-y-1 relative">
        {menuItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;

          if (item.id === 'history') {
            return (
              <div key={item.id} className="w-full flex flex-col relative">
                {/* Main Button */}
                <button
                  onClick={() => {
                    setIsHistoryOpen(!isHistoryOpen);
                    setIsMobileMenuOpen(!isMobileMenuOpen);
                    setActiveTab('history');
                  }}
                  className={`flex flex-col md:flex-row items-center gap-1.5 md:gap-3 py-2 px-3 md:py-3 md:px-4 rounded-xl text-[11px] md:text-sm font-medium transition-all duration-200 w-full max-w-[80px] md:max-w-none ${
                    isActive 
                      ? 'bg-emerald-50 text-emerald-700 border border-emerald-200/60 shadow-sm font-semibold' 
                      : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100/60 border border-transparent'
                  }`}
                >
                  <Icon size={isActive ? 20 : 18} className="transition-transform duration-200 group-hover:scale-110" />
                  <span className="md:inline">{item.name}</span>
                </button>

                {/* Submenu Desktop */}
                {isHistoryOpen && (
                  <div className="hidden md:flex flex-col pl-9 mt-1 mb-2 space-y-1 border-l border-slate-200 ml-5 text-left">
                    {subItems.map((sub) => {
                      const isSubActive = activeTab === 'history' && historyDefaultTab === sub.id;
                      return (
                        <button
                          key={sub.id}
                          onClick={() => {
                            setHistoryDefaultTab(sub.id);
                            setActiveTab('history');
                          }}
                          className={`text-left py-1.5 px-3 rounded-lg text-xs font-medium transition-all ${
                            isSubActive
                              ? 'text-emerald-700 font-semibold bg-emerald-50'
                              : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100/40'
                          }`}
                        >
                          {sub.name}
                        </button>
                      );
                    })}
                  </div>
                )}

                {/* Submenu Mobile Tooltip Popup */}
                {isMobileMenuOpen && (
                  <div className="md:hidden absolute bottom-16 left-1/2 -translate-x-1/2 w-44 glass-panel border border-slate-200/80 rounded-2xl shadow-2xl p-2 z-50 flex flex-col gap-1">
                    {subItems.map((sub) => {
                      const isSubActive = activeTab === 'history' && historyDefaultTab === sub.id;
                      return (
                        <button
                          key={sub.id}
                          onClick={() => {
                            setHistoryDefaultTab(sub.id);
                            setActiveTab('history');
                            setIsMobileMenuOpen(false);
                          }}
                          className={`text-center py-2 px-3 rounded-xl text-xs font-semibold transition-all ${
                            isSubActive
                              ? 'bg-emerald-600 text-white shadow-md'
                              : 'text-slate-700 hover:text-slate-900 hover:bg-slate-100'
                          }`}
                        >
                          {sub.name}
                        </button>
                      );
                    })}
                  </div>
                )}
              </div>
            );
          }

          return (
            <button
              key={item.id}
              onClick={() => {
                setActiveTab(item.id);
                setIsMobileMenuOpen(false);
              }}
              className={`flex flex-col md:flex-row items-center gap-1.5 md:gap-3 py-2 px-3 md:py-3 md:px-4 rounded-xl text-[11px] md:text-sm font-medium transition-all duration-200 w-full max-w-[80px] md:max-w-none ${
                isActive 
                  ? 'bg-emerald-50 text-emerald-700 border border-emerald-200/60 shadow-sm font-semibold' 
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100/60 border border-transparent'
              }`}
            >
              <Icon size={isActive ? 20 : 18} className="transition-transform duration-200 group-hover:scale-110" />
              <span className="md:inline">{item.name}</span>
            </button>
          );
        })}
      </nav>

      {/* User Info & Logout Section - desktop only */}
      <div className="hidden md:flex flex-col gap-4 border-t border-slate-200/60 pt-4 px-2">
        <div className="flex items-center gap-3">
          <img 
            src={user.photo || "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=100&h=100&q=80"} 
            alt="Avatar" 
            className="w-10 h-10 rounded-full border border-emerald-500/20 object-cover"
          />
          <div className="overflow-hidden">
            <h4 className="text-sm font-semibold text-slate-800 truncate leading-tight">{user.name}</h4>
            <span className="text-xs text-slate-500 truncate block">No.RM: {user.norm}</span>
          </div>
        </div>
        
        <button 
          onClick={onLogout}
          className="flex items-center gap-3 py-2 px-4 rounded-xl text-sm font-medium text-rose-600 hover:text-rose-700 hover:bg-rose-50 border border-transparent hover:border-rose-200/50 transition-all duration-200"
        >
          <LogOut size={18} />
          <span>Keluar</span>
        </button>
      </div>
    </aside>
  );
}
