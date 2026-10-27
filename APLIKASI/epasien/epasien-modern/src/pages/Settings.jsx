import React, { useState } from 'react';
import { 
  Settings as SettingsIcon, 
  Lock, 
  User, 
  Key, 
  ShieldCheck, 
  AlertCircle,
  Eye,
  EyeOff
} from 'lucide-react';

export default function Settings({ user, onAddNotification, onPasswordUpdated }) {
  const [passwordForm, setPasswordForm] = useState({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  });

  const [showPass, setShowPass] = useState({
    old: false,
    new: false,
    confirm: false
  });

  const [errors, setErrors] = useState({});
  const [successMsg, setSuccessMsg] = useState('');

  const handleChange = (e) => {
    setPasswordForm({
      ...passwordForm,
      [e.target.name]: e.target.value
    });
    if (errors[e.target.name]) {
      setErrors({ ...errors, [e.target.name]: '' });
    }
  };

  const handlePasswordSubmit = (e) => {
    e.preventDefault();
    const newErrors = {};

    if (!passwordForm.oldPassword) newErrors.oldPassword = 'Password lama wajib diisi';
    if (!passwordForm.newPassword) {
      newErrors.newPassword = 'Password baru wajib diisi';
    } else if (passwordForm.newPassword.length < 6) {
      newErrors.newPassword = 'Password baru minimal 6 karakter';
    }
    
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      newErrors.confirmPassword = 'Konfirmasi password baru tidak cocok';
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    const body = new FormData();
    body.append('oldPassword', passwordForm.oldPassword);
    body.append('newPassword', passwordForm.newPassword);

    fetch('api.php?action=change_password', {
      method: 'POST',
      body: body
    })
    .then(res => res.json())
    .then(res => {
      if (res.success) {
        setSuccessMsg('Password Anda berhasil diperbarui.');
        onAddNotification({
          title: 'Ubah Password Berhasil',
          desc: 'Password akun EPasien Anda telah diperbarui demi keamanan.',
          time: 'Just now'
        });
        onPasswordUpdated();
        setPasswordForm({ oldPassword: '', newPassword: '', confirmPassword: '' });
        setTimeout(() => {
          setSuccessMsg('');
        }, 4000);
      } else {
        setErrors({ form: res.message || 'Gagal mengubah password.' });
      }
    })
    .catch(err => {
      setErrors({ form: 'Koneksi ke database gagal.' });
    });
  };

  return (
    <div className="max-w-3xl mx-auto space-y-6 md:space-y-8 animate-in fade-in duration-300">
      
      {/* Page Header */}
      <div>
        <h2 className="text-xl md:text-2xl font-bold font-heading text-slate-800 flex items-center gap-2">
          <SettingsIcon className="text-emerald-600" /> Pengaturan & Keamanan Akun
        </h2>
        <p className="text-xs md:text-sm text-slate-500">
          Ubah kata sandi dan kelola data keamanan profil pasien Anda.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        
        {/* Left Side: Profile Card */}
        <div className="glass-panel p-6 rounded-3xl space-y-5 h-fit text-center">
          <div className="relative w-24 h-24 mx-auto rounded-full overflow-hidden border-2 border-emerald-500/30">
            <img 
              src={user.photo || "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=100&h=100&q=80"} 
              alt="Avatar" 
              className="w-full h-full object-cover"
            />
          </div>
          <div>
            <h3 className="font-bold text-slate-800 text-base leading-tight">{user.name}</h3>
            <span className="text-xs text-slate-500 font-medium mt-1 block">No. Rekam Medis: {user.norm}</span>
          </div>
          
          <div className="border-t border-slate-200/80 pt-4 space-y-2 text-left text-xs">
            <div className="flex justify-between">
              <span className="text-slate-400">Tempat Lahir:</span>
              <span className="text-slate-700 font-medium">{user.pob || 'BANDUNG'}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-400">Tanggal Lahir:</span>
              <span className="text-slate-700 font-medium">{user.dob || '05/06/1992'}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-400">Jenis Kelamin:</span>
              <span className="text-slate-700 font-medium">{user.gender === 'L' ? 'Laki-laki' : 'Perempuan'}</span>
            </div>
          </div>
        </div>

        {/* Right Side: Change Password Form */}
        <div className="md:col-span-2 glass-panel p-6 md:p-8 rounded-3xl space-y-6 relative overflow-hidden">
          <div className="absolute top-0 right-0 w-64 h-64 bg-emerald-500/5 rounded-full blur-3xl -z-10"></div>
          
          <h3 className="text-xs font-semibold text-emerald-600 uppercase tracking-wider flex items-center gap-2">
            <Lock size={14} /> Ganti Kata Sandi (Password)
          </h3>

          {successMsg && (
            <div className="flex items-center gap-2 bg-emerald-50 border border-emerald-200/60 p-3.5 rounded-xl text-xs text-emerald-600">
              <ShieldCheck size={16} /> {successMsg}
            </div>
          )}

          {errors.form && (
            <div className="flex items-center gap-2 bg-rose-50 border border-rose-200/60 p-3.5 rounded-xl text-xs text-rose-600 animate-in fade-in duration-200">
              <AlertCircle size={16} /> {errors.form}
            </div>
          )}

          <form onSubmit={handlePasswordSubmit} className="space-y-4">
            
            {/* Old Password */}
            <div className="space-y-1.5 relative">
              <label className="text-xs font-semibold text-slate-600">Password Saat Ini</label>
              <div className="relative">
                <input 
                  type={showPass.old ? "text" : "password"} 
                  name="oldPassword"
                  value={passwordForm.oldPassword}
                  onChange={handleChange}
                  className="glass-input w-full py-2.5 pl-4 pr-10 rounded-xl text-sm" 
                  placeholder="Masukkan password saat ini"
                />
                <button 
                  type="button"
                  onClick={() => setShowPass({ ...showPass, old: !showPass.old })}
                  className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
                >
                  {showPass.old ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
              {errors.oldPassword && <span className="text-[10px] text-rose-600 flex items-center gap-1"><AlertCircle size={10} /> {errors.oldPassword}</span>}
            </div>

            {/* New Password */}
            <div className="space-y-1.5 relative">
              <label className="text-xs font-semibold text-slate-600">Password Baru</label>
              <div className="relative">
                <input 
                  type={showPass.new ? "text" : "password"} 
                  name="newPassword"
                  value={passwordForm.newPassword}
                  onChange={handleChange}
                  className="glass-input w-full py-2.5 pl-4 pr-10 rounded-xl text-sm" 
                  placeholder="Password baru (minimal 6 karakter)"
                />
                <button 
                  type="button"
                  onClick={() => setShowPass({ ...showPass, new: !showPass.new })}
                  className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
                >
                  {showPass.new ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
              {errors.newPassword && <span className="text-[10px] text-rose-600 flex items-center gap-1"><AlertCircle size={10} /> {errors.newPassword}</span>}
            </div>

            {/* Confirm New Password */}
            <div className="space-y-1.5 relative">
              <label className="text-xs font-semibold text-slate-600">Konfirmasi Password Baru</label>
              <div className="relative">
                <input 
                  type={showPass.confirm ? "text" : "password"} 
                  name="confirmPassword"
                  value={passwordForm.confirmPassword}
                  onChange={handleChange}
                  className="glass-input w-full py-2.5 pl-4 pr-10 rounded-xl text-sm" 
                  placeholder="Ulangi password baru"
                />
                <button 
                  type="button"
                  onClick={() => setShowPass({ ...showPass, confirm: !showPass.confirm })}
                  className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
                >
                  {showPass.confirm ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
              {errors.confirmPassword && <span className="text-[10px] text-rose-600 flex items-center gap-1"><AlertCircle size={10} /> {errors.confirmPassword}</span>}
            </div>

            {/* Submit Button */}
            <div className="pt-2 flex justify-end">
              <button 
                type="submit"
                className="w-full md:w-auto py-2.5 px-6 bg-emerald-500 hover:bg-emerald-400 text-white font-semibold text-sm rounded-xl shadow-[0_4px_15px_rgba(16,185,129,0.15)] flex items-center gap-2 justify-center transition-all duration-200"
              >
                <Key size={16} /> Perbarui Kata Sandi
              </button>
            </div>

          </form>
        </div>

      </div>

    </div>
  );
}
