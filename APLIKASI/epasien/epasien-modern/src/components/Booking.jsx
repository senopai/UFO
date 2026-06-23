import React, { useState, useEffect } from 'react';
import { 
  Calendar, 
  User as UserIcon, 
  Phone, 
  MapPin, 
  Mail, 
  ClipboardCheck, 
  ShieldCheck,
  ChevronRight,
  AlertCircle
} from 'lucide-react';

export default function Booking({ user, onBookingSuccess }) {
  const [clinics, setClinics] = useState([]);
  const [doctors, setDoctors] = useState({});

  useEffect(() => {
    fetch('api.php?action=get_booking_data')
      .then(res => res.json())
      .then(res => {
        if (res.success) {
          setClinics(res.clinics);
          setDoctors(res.doctors);
        }
      })
      .catch(err => console.error("Error loading booking options:", err));
  }, []);

  const [formData, setFormData] = useState({
    name: user.name || '',
    address: user.address || 'JL. RAYA DAGO NO. 120, BANDUNG',
    phone: user.phone || '081234567890',
    nik: user.nik || '',
    clinic: '',
    doctor: '',
    date: ''
  });

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
    if (errors[name]) {
      setErrors({ ...errors, [name]: '' });
    }
  };

  const validate = () => {
    const newErrors = {};
    if (!formData.name.trim()) newErrors.name = 'Nama lengkap wajib diisi';
    if (!formData.address.trim()) newErrors.address = 'Alamat wajib diisi';
    if (!formData.phone.trim()) newErrors.phone = 'Nomor HP wajib diisi';
    if (!formData.nik.trim()) newErrors.nik = 'NIK / Nomor KTP wajib diisi';
    if (!formData.clinic) newErrors.clinic = 'Silakan pilih poliklinik';
    if (!formData.doctor) newErrors.doctor = 'Silakan pilih dokter';
    if (!formData.date) {
      newErrors.date = 'Silakan pilih tanggal periksa';
    } else {
      const selected = new Date(formData.date);
      const today = new Date();
      today.setHours(0,0,0,0);
      if (selected <= today) {
        newErrors.date = 'Booking harus dilakukan minimal 1 hari sebelum pemeriksaan';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validate()) {
      const selectedDoctorObj = doctors[formData.clinic]?.find(d => d.id === formData.doctor);
      const selectedClinicObj = clinics.find(c => c.id === formData.clinic);

      const body = new FormData();
      body.append('clinic', formData.clinic);
      body.append('doctor', formData.doctor);
      body.append('date', formData.date);

      fetch('api.php?action=save_booking', {
        method: 'POST',
        body: body
      })
      .then(res => res.json())
      .then(res => {
        if (res.success) {
          onBookingSuccess({
            doctorName: res.booking.doctorName,
            clinicName: res.booking.clinicName,
            date: res.booking.date,
            fee: res.booking.fee,
            patientName: res.booking.patientName,
            queueNum: res.booking.noReg
          });
        } else {
          setErrors({ form: res.message || 'Gagal menyimpan booking.' });
        }
      })
      .catch(err => {
        setErrors({ form: 'Koneksi ke database gagal.' });
      });
    }
  };

  return (
    <div className="max-w-3xl mx-auto space-y-6 animate-in fade-in duration-300">
      
      {/* Header Info */}
      <div className="text-center md:text-left space-y-2">
        <h2 className="text-xl md:text-2xl font-bold font-heading text-slate-800 flex items-center gap-2 justify-center md:justify-start">
          <Calendar className="text-emerald-600" /> Booking Registrasi Pasien Umum
        </h2>
        <p className="text-xs md:text-sm text-slate-500">
          Formulir pendaftaran konsultasi rawat jalan mandiri dengan skema pembayaran non-asuransi (Umum).
        </p>
      </div>

      <div className="glass-panel p-6 md:p-8 rounded-3xl relative overflow-hidden bg-white/95 border-slate-200">
        <div className="absolute top-0 right-0 w-64 h-64 bg-indigo-500/5 rounded-full blur-3xl -z-10"></div>

        <form onSubmit={handleSubmit} className="space-y-6">
          
          {/* Section A: Identitas Pasien */}
          <div className="space-y-4">
            <h3 className="text-xs font-semibold text-emerald-600 uppercase tracking-wider flex items-center gap-2">
              <span className="w-1.5 h-1.5 bg-emerald-500 rounded-full"></span> Data Identitas Pasien
            </h3>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {/* Nama */}
              <div className="space-y-1.5">
                <label className="text-xs font-semibold text-slate-700 flex items-center gap-1.5">
                  <UserIcon size={14} className="text-slate-400" /> Nama Lengkap
                </label>
                <input 
                  type="text" 
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  className="glass-input w-full py-2.5 px-4 rounded-xl text-sm" 
                  placeholder="Nama sesuai KTP"
                />
                {errors.name && <span className="text-[10px] text-rose-600 flex items-center gap-1"><AlertCircle size={10} /> {errors.name}</span>}
              </div>

              {/* No HP */}
              <div className="space-y-1.5">
                <label className="text-xs font-semibold text-slate-700 flex items-center gap-1.5">
                  <Phone size={14} className="text-slate-400" /> Nomor HP / Telephone
                </label>
                <input 
                  type="tel" 
                  name="phone"
                  value={formData.phone}
                  onChange={handleChange}
                  className="glass-input w-full py-2.5 px-4 rounded-xl text-sm" 
                  placeholder="Contoh: 081234567890"
                />
                {errors.phone && <span className="text-[10px] text-rose-600 flex items-center gap-1"><AlertCircle size={10} /> {errors.phone}</span>}
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {/* NIK */}
              <div className="space-y-1.5">
                <label className="text-xs font-semibold text-slate-700 flex items-center gap-1.5">
                  <ShieldCheck size={14} className="text-slate-400" /> NIK / No. KTP
                </label>
                <input 
                  type="text" 
                  name="nik"
                  value={formData.nik}
                  onChange={handleChange}
                  className="glass-input w-full py-2.5 px-4 rounded-xl text-sm" 
                  placeholder="NIK Pasien (16 digit)"
                  maxLength={20}
                />
                {errors.nik && <span className="text-[10px] text-rose-600 flex items-center gap-1"><AlertCircle size={10} /> {errors.nik}</span>}
              </div>

              {/* Alamat */}
              <div className="space-y-1.5">
                <label className="text-xs font-semibold text-slate-700 flex items-center gap-1.5">
                  <MapPin size={14} className="text-slate-400" /> Alamat Rumah
                </label>
                <input 
                  type="text" 
                  name="address"
                  value={formData.address}
                  onChange={handleChange}
                  className="glass-input w-full py-2.5 px-4 rounded-xl text-sm" 
                  placeholder="JL. Raya No. XX, Kota"
                />
                {errors.address && <span className="text-[10px] text-rose-600 flex items-center gap-1"><AlertCircle size={10} /> {errors.address}</span>}
              </div>
            </div>
          </div>

          {/* Section B: Rencana Kunjungan */}
          <div className="space-y-4 pt-4 border-t border-slate-100">
            <h3 className="text-xs font-semibold text-emerald-600 uppercase tracking-wider flex items-center gap-2">
              <span className="w-1.5 h-1.5 bg-emerald-500 rounded-full"></span> Detail Kunjungan & Poli
            </h3>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              {/* Poliklinik */}
              <div className="space-y-1.5">
                <label className="text-xs font-semibold text-slate-700">Pilih Poliklinik</label>
                <select 
                  name="clinic"
                  value={formData.clinic}
                  onChange={(e) => {
                    handleChange(e);
                    setFormData(prev => ({ ...prev, clinic: e.target.value, doctor: '' }));
                  }}
                  className="glass-input w-full py-2.5 px-4 rounded-xl text-sm"
                >
                  <option value="">-- Pilih Poli --</option>
                  {clinics.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                </select>
                {errors.clinic && <span className="text-[10px] text-rose-600 flex items-center gap-1"><AlertCircle size={10} /> {errors.clinic}</span>}
              </div>

              {/* Dokter */}
              <div className="space-y-1.5">
                <label className="text-xs font-semibold text-slate-700">Pilih Dokter</label>
                <select 
                  name="doctor"
                  value={formData.doctor}
                  onChange={handleChange}
                  disabled={!formData.clinic}
                  className="glass-input w-full py-2.5 px-4 rounded-xl text-sm disabled:opacity-40"
                >
                  <option value="">-- Pilih Dokter --</option>
                  {formData.clinic && doctors[formData.clinic]?.map(d => (
                    <option key={d.id} value={d.id}>{d.name} ({d.fee})</option>
                  ))}
                </select>
                {errors.doctor && <span className="text-[10px] text-rose-600 flex items-center gap-1"><AlertCircle size={10} /> {errors.doctor}</span>}
              </div>

              {/* Tanggal */}
              <div className="space-y-1.5">
                <label className="text-xs font-semibold text-slate-700">Tanggal Periksa</label>
                <input 
                  type="date" 
                  name="date"
                  value={formData.date}
                  onChange={handleChange}
                  className="glass-input w-full py-2.2 px-4 rounded-xl text-sm text-slate-700"
                />
                {errors.date && <span className="text-[10px] text-rose-600 flex items-center gap-1"><AlertCircle size={10} /> {errors.date}</span>}
              </div>
            </div>
          </div>



          {/* Submit Button */}
          <div className="pt-4 flex flex-col md:flex-row justify-end gap-3 items-center">
            {errors.form && (
              <span className="text-xs text-rose-600 bg-rose-50 border border-rose-100 py-2 px-4 rounded-xl flex items-center gap-1.5 self-start md:self-auto mr-auto">
                <AlertCircle size={14} /> {errors.form}
              </span>
            )}
            <button 
              type="submit"
              className="py-3 px-6 bg-emerald-600 hover:bg-emerald-500 text-white font-semibold text-sm rounded-xl shadow-[0_4px_20px_rgba(16,185,129,0.15)] flex items-center gap-2 justify-center transition-all duration-200 cursor-pointer"
            >
              <ClipboardCheck size={18} /> Konfirmasi Booking
            </button>
          </div>

        </form>
      </div>

      {/* Safety Notice */}
      <div className="flex items-start gap-3 bg-slate-50 border border-slate-200/60 p-4 rounded-2xl">
        <ShieldCheck className="text-emerald-600 shrink-0 mt-0.5" size={18} />
        <p className="text-[11px] text-slate-600 leading-normal">
          <strong>Perhatian:</strong> Pendaftaran di atas khusus untuk skema <strong>Pasien Umum</strong>. Harap siapkan dana tunai atau non-tunai saat kunjungan untuk menyelesaikan pembayaran di bagian kasir sebelum pemeriksaan dimulai. Silakan tunjukkan bukti booking/registrasi kepada petugas administrasi kami saat berada di klinik.
        </p>
      </div>

    </div>
  );
}
