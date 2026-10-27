import React from 'react';
import { 
  CalendarRange, 
  History, 
  FileSignature, 
  Users, 
  Activity, 
  CheckCircle, 
  QrCode, 
  Clock, 
  ChevronRight,
  Megaphone,
  Bed,
  FileText,
  Lock
} from 'lucide-react';

export default function Dashboard({ 
  user, 
  activeQueue, 
  setActiveTab, 
  setHistoryDefaultTab,
  pendingConsentsCount,
  recentResultsCount,
  labCount = 0,
  radCount = 0,
  visitCount = 0,
  rajalCount = 0,
  ranapCount = 0,
  hospitalName = 'Rumah Sakit',
  schedules = []
}) {
  
  // Indonesian Date Formatter
  const getIndonesianDate = () => {
    const days = ['Minggu', 'Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu'];
    const months = [
      'Januari', 'Februari', 'Maret', 'April', 'Mei', 'Juni',
      'Juli', 'Agustus', 'September', 'Oktober', 'November', 'Desember'
    ];
    const d = new Date();
    const dayName = days[d.getDay()];
    const date = d.getDate();
    const monthName = months[d.getMonth()];
    const year = d.getFullYear();
    return `${dayName}, ${date} ${monthName} ${year}`;
  };

  // Mock Doctor Schedule Data aligned with hospital database
  const doctorSchedules = [
    { name: 'dr. Aisyah, Sp.OG', clinic: 'Poliklinik Kandungan', start: '08:00:00', end: '12:00:00', quota: 50, checkin: 12 },
    { name: 'dr. Qotrunnada, Sp.A', clinic: 'Poliklinik Anak', start: '09:00:00', end: '13:00:00', quota: 40, checkin: 15 },
    { name: 'dr. Hilyatul Nadia, Sp.PD', clinic: 'Poliklinik Penyakit Dalam', start: '08:00:00', end: '11:00:00', quota: 60, checkin: 28 },
    { name: 'dr. Hilyatul Nadia, Sp.B', clinic: 'Poliklinik Bedah', start: '13:00:00', end: '16:00:00', quota: 30, checkin: 5 },
    { name: 'dr. Dian Safitri, Sp.M', clinic: 'Poliklinik Mata', start: '10:00:00', end: '13:00:00', quota: 50, checkin: 8 },
    { name: 'dr. Sri Rahma, Sp.KK', clinic: 'Poliklinik Kulit & Kelamin', start: '11:00:00', end: '14:00:00', quota: 35, checkin: 4 },
    { name: 'dr. Sri Rahma, Sp.N', clinic: 'Poliklinik Syaraf / Neurologi', start: '09:00:00', end: '12:00:00', quota: 45, checkin: 10 }
  ];

  const displaySchedules = (schedules && schedules.length > 0) ? schedules : doctorSchedules;

  return (
    <div className="space-y-6 md:space-y-8 animate-in fade-in duration-300">
      
      {/* Active Queue / Check-in Tracker Section */}
      {activeQueue && (
        <section className="glass-panel border-emerald-250/20 p-5 md:p-6 rounded-3xl relative overflow-hidden bg-white/90">
          {/* Glow effect */}
          <div className="absolute top-0 right-0 w-64 h-64 bg-emerald-500/5 rounded-full blur-3xl -z-10"></div>
          
          <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-6">
            <div className="space-y-3">
              <span className="text-[10px] uppercase font-bold tracking-wider text-emerald-700 bg-emerald-50 px-3 py-1 rounded-full border border-emerald-100">
                Antrean Kunjungan Anda Hari Ini
              </span>
              <div>
                <h3 className="text-xl font-bold font-heading text-slate-800">
                  {activeQueue.clinic}
                </h3>
                <p className="text-xs md:text-sm text-slate-500 mt-1">
                  Dokter: <span className="text-slate-800 font-medium">{activeQueue.doctor}</span>
                </p>
                <div className="flex flex-wrap items-center gap-x-4 gap-y-2 mt-3 text-xs text-slate-500">
                  <span className="flex items-center gap-1.5"><Clock size={14} className="text-emerald-600" /> Sesi: 09:00 - 12:00</span>
                  <span className="flex items-center gap-1.5"><Activity size={14} className="text-emerald-600" /> Status: Dalam Antrean</span>
                </div>
              </div>
            </div>

            {/* Action Box */}
            <div className="flex items-center gap-4 bg-slate-50 p-4 rounded-2xl border border-slate-200/80 w-full lg:w-auto lg:min-w-[320px]">
              <div className="flex items-center justify-between w-full">
                <div>
                  <span className="text-[10px] text-slate-500 block uppercase font-bold">Nomor Antrean Anda</span>
                  <span className="text-3xl md:text-4xl font-extrabold font-heading text-emerald-600 tracking-tight">
                    {activeQueue.queueNum}
                  </span>
                  <span className="text-[10px] text-emerald-600 block mt-1 font-semibold flex items-center gap-1">
                    <CheckCircle size={10} /> Sudah Terdaftar
                  </span>
                </div>
                <div className="p-3 bg-white border border-slate-200 rounded-xl">
                  <QrCode size={48} className="text-slate-700" />
                </div>
              </div>
            </div>
          </div>
        </section>
      )}

      {/* 2. Key Metrics Grid (6 boxes layout similar to screenshot) */}
      <section className="space-y-4">
        <h4 className="text-xs font-semibold text-slate-500 font-heading uppercase tracking-wider">Dashboard Informasi Medis</h4>
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
          {[
            { 
              title: 'Hasil Laborat', 
              val: String(labCount), 
              icon: FileText, 
              color: 'text-purple-600',
              bg: 'bg-purple-50/70 border-purple-100/80 hover:border-purple-300 hover:bg-purple-50 cursor-pointer hover:scale-[1.02] active:scale-[0.98] shadow-sm',
              onClick: () => { setHistoryDefaultTab('lab'); setActiveTab('history'); }
            },
            { 
              title: 'Hasil Radiologi', 
              val: String(radCount), 
              icon: History, 
              color: 'text-blue-600',
              bg: 'bg-blue-50/70 border-blue-100/80 hover:border-blue-300 hover:bg-blue-50 cursor-pointer hover:scale-[1.02] active:scale-[0.98] shadow-sm',
              onClick: () => { setHistoryDefaultTab('rad'); setActiveTab('history'); }
            },
            { 
              title: 'Kunjungan', 
              val: String(visitCount), 
              icon: Lock, 
              color: 'text-rose-600',
              bg: 'bg-rose-50/70 border-rose-100/80 hover:border-rose-300 hover:bg-rose-50 cursor-pointer hover:scale-[1.02] active:scale-[0.98] shadow-sm',
              onClick: () => { setHistoryDefaultTab('kunjungan'); setActiveTab('history'); }
            },
            { 
              title: 'Rawat Jalan', 
              val: String(rajalCount), 
              icon: Activity, 
              color: 'text-cyan-600',
              bg: 'bg-cyan-50/70 border-cyan-100/80 hover:border-cyan-300 hover:bg-cyan-50 cursor-pointer hover:scale-[1.02] active:scale-[0.98] shadow-sm',
              onClick: () => { setHistoryDefaultTab('kunjungan'); setActiveTab('history'); }
            },
            { 
              title: 'Rawat Inap', 
              val: String(ranapCount), 
              icon: Bed, 
              color: 'text-emerald-600',
              bg: 'bg-emerald-50/70 border-emerald-100/80 hover:border-emerald-300 hover:bg-emerald-50 cursor-pointer hover:scale-[1.02] active:scale-[0.98] shadow-sm',
              onClick: () => { setHistoryDefaultTab('kunjungan'); setActiveTab('history'); }
            },
            { 
              title: 'Bulan Ini', 
              val: '0', 
              icon: CalendarRange, 
              color: 'text-amber-600',
              bg: 'bg-amber-50/70 border-amber-100/80 hover:border-amber-300 hover:bg-amber-50 cursor-pointer hover:scale-[1.02] active:scale-[0.98] shadow-sm',
              onClick: () => {}
            },
          ].map((m, idx) => (
            <div 
              key={idx} 
              onClick={m.onClick}
              className={`p-4 rounded-2xl border flex flex-col justify-between min-h-[110px] transition-all duration-300 ${m.bg}`}
            >
              <div className="flex justify-between items-start gap-2">
                <span className="text-[10px] md:text-xs text-slate-500 font-semibold leading-tight uppercase">{m.title}</span>
                <m.icon size={18} className={m.color} />
              </div>
              <span className="text-2xl md:text-3xl font-extrabold font-heading text-slate-800 block mt-3">{m.val}</span>
            </div>
          ))}
        </div>
      </section>

      {/* 3. Jadwal Praktek Dokter (Practice Schedule Table) */}
      <section className="glass-panel p-5 md:p-6 rounded-3xl space-y-4 bg-white/95">
        <div className="flex flex-col md:flex-row md:items-center justify-between border-b border-slate-100 pb-4 gap-2">
          <div>
            <h4 className="text-sm font-semibold text-emerald-600 font-heading uppercase tracking-wider">Jadwal Praktek Dokter</h4>
            <p className="text-xs text-slate-500 mt-0.5">Daftar dokter praktek aktif hari ini di {hospitalName}.</p>
          </div>
          <span className="text-xs font-semibold text-emerald-700 bg-emerald-50 border border-emerald-100 px-3 py-1.5 rounded-xl flex items-center gap-1.5 self-start">
            <Clock size={12} className="text-emerald-600" /> {getIndonesianDate()}
          </span>
        </div>

        {/* Schedule Table */}
        <div className="overflow-x-auto rounded-2xl border border-slate-200/50">
          <table className="w-full text-left border-collapse text-xs md:text-sm">
            <thead>
              <tr className="bg-slate-100/70 text-slate-700 border-b border-slate-200 font-heading text-[11px] uppercase tracking-wider">
                <th className="py-3 px-4">Nama Dokter</th>
                <th className="py-3 px-4">Poliklinik</th>
                <th className="py-3 px-4 text-center">Mulai</th>
                <th className="py-3 px-4 text-center">Selesai</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-slate-600">
              {displaySchedules.map((doc, idx) => (
                <tr key={idx} className="hover:bg-slate-50/50 transition-colors">
                  <td className="py-3.5 px-4 font-semibold text-slate-800">{doc.name}</td>
                  <td className="py-3.5 px-4 text-slate-500">{doc.clinic}</td>
                  <td className="py-3.5 px-4 text-center font-mono">{doc.start}</td>
                  <td className="py-3.5 px-4 text-center font-mono">{doc.end}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      {/* 4. Quick Access & Announcement Split */}
      <section className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Quick Actions */}
        <div className="lg:col-span-2 space-y-4">
          <h4 className="text-sm font-semibold text-slate-500 font-heading uppercase tracking-wider">Akses Cepat</h4>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {[
              { 
                title: 'Buat Booking Baru', 
                desc: 'Khusus pasien umum, buat antrean berobat secara mandiri.', 
                action: () => setActiveTab('booking'), 
                icon: CalendarRange, 
                color: 'text-emerald-600 bg-emerald-50 border-emerald-100'
              },
              { 
                title: 'Hasil Lab & Radiologi', 
                desc: 'Lihat diagnosis, riwayat klinis, dan file laboratorium Anda.', 
                action: () => setActiveTab('history'), 
                icon: History, 
                color: 'text-indigo-600 bg-indigo-50 border-indigo-100'
              }
            ].map((act, i) => (
              <button 
                key={i} 
                onClick={act.action}
                className="glass-card hover:bg-slate-50 border border-slate-200/60 hover:border-slate-300 p-5 rounded-2xl text-left flex gap-4 transition-all duration-200 bg-white"
              >
                <div className={`p-3 rounded-xl h-fit border ${act.color}`}>
                  <act.icon size={22} />
                </div>
                <div className="space-y-1">
                  <h5 className="text-sm font-bold text-slate-800 flex items-center gap-1 group">
                    {act.title} <ChevronRight size={14} className="opacity-0 group-hover:opacity-100 transition-opacity" />
                  </h5>
                  <p className="text-xs text-slate-500 leading-normal">{act.desc}</p>
                </div>
              </button>
            ))}
          </div>
        </div>

        {/* Announcements */}
        <div className="space-y-4">
          <h4 className="text-sm font-semibold text-slate-500 font-heading uppercase tracking-wider">Pengumuman Klinik</h4>
          <div className="glass-card border border-slate-200/60 p-5 rounded-2xl space-y-4 relative overflow-hidden h-fit bg-white">
            <div className="flex items-center gap-3 text-emerald-600">
              <Megaphone size={18} />
              <h5 className="text-xs font-bold uppercase tracking-wider">Informasi Pelayanan</h5>
            </div>
            <div className="space-y-2">
              <h6 className="text-xs font-semibold text-slate-800 leading-normal">
                Perubahan Jadwal Dokter Spesialis Penyakit Dalam
              </h6>
              <p className="text-[11px] text-slate-600 leading-relaxed">
                Sehubungan dengan hari libur nasional, jadwal praktik dr. H. Saiful Umam, Sp.PD mengalami penyesuaian khusus. Pasien yang memiliki booking disarankan check-in 1 jam lebih awal.
              </p>
            </div>
            <div className="text-[10px] text-slate-400 border-t border-slate-100 pt-3 flex justify-between">
              <span>Oleh: Humas RS</span>
              <span>13 Jun 2026</span>
            </div>
          </div>
        </div>

      </section>

    </div>
  );
}
