const express = require('express');
const mysql = require('mysql2/promise');
const cors = require('cors');
const session = require('express-session');
const crypto = require('crypto');
const fs = require('fs');
const path = require('path');
const multer = require('multer');

const app = express();
const PORT = process.env.PORT || 5000;

const upload = multer();

// Middleware
app.use(cors({
    origin: true,
    credentials: true
}));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(upload.none());

app.use(session({
    secret: 'epasien-modern-secret-key-12345678',
    resave: false,
    saveUninitialized: true,
    cookie: { secure: false }
}));

// Helper: Parse db_config.php
function getDbConfig() {
    const configPath = path.join(__dirname, 'db_config.php');
    if (!fs.existsSync(configPath)) {
        console.warn('db_config.php not found, using defaults.');
        return {
            host: 'localhost',
            user: 'root',
            password: '',
            database: 'sik',
            webapps_host: 'localhost',
            urutnoreg: 'dokter + poli'
        };
    }
    const content = fs.readFileSync(configPath, 'utf8');
    const hostMatch = content.match(/\$db_hostname\s*=\s*['"]([^'"]+)['"]/);
    const userMatch = content.match(/\$db_username\s*=\s*['"]([^'"]+)['"]/);
    const passMatch = content.match(/\$db_password\s*=\s*['"]([^'"]*)['"]/);
    const nameMatch = content.match(/\$db_name\s*=\s*['"]([^'"]+)['"]/);
    const webappsMatch = content.match(/\$webapps_hostname\s*=\s*['"]([^'"]+)['"]/);
    const urutMatch = content.match(/define\(\s*['"]URUTNOREG['"]\s*,\s*['"]([^'"]+)['"]\)/);
    const fonnteMatch = content.match(/define\(\s*['"]FONNTE_TOKEN['"]\s*,\s*['"]([^'"]*)['"]\)/);

    const dbHost = hostMatch ? hostMatch[1] : 'localhost';
    return {
        host: dbHost,
        user: userMatch ? userMatch[1] : 'root',
        password: passMatch ? passMatch[1] : '',
        database: nameMatch ? nameMatch[1] : 'sik',
        webapps_host: (webappsMatch && webappsMatch[1]) ? webappsMatch[1] : dbHost,
        urutnoreg: urutMatch ? urutMatch[1] : 'dokter + poli',
        fonnte_token: fonnteMatch ? fonnteMatch[1] : ''
    };
}

const config = getDbConfig();
console.log('Database Configuration Loaded:', {
    host: config.host,
    webapps_host: config.webapps_host,
    user: config.user,
    database: config.database,
    urutnoreg: config.urutnoreg
});

// Helper: Construct remote URL dynamically (uses HTTPS for domains, HTTP for IPs/localhost)
function getRemoteUrl(pathStr) {
    const host = config.webapps_host || config.host;
    if (host.startsWith('http://') || host.startsWith('https://')) {
        return `${host}${pathStr}`;
    }
    const protocol = (host.includes('.') && !/^[0-9.]+$/.test(host)) ? 'https://' : 'http://';
    return `${protocol}${host}${pathStr}`;
}

// Setup Connection Pool
const pool = mysql.createPool({
    host: config.host,
    user: config.user,
    password: config.password,
    database: config.database,
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

// Helper: encrypt_decrypt matching SIMKES Khanza
function encrypt_decrypt(string, action) {
    if (!string) return '';
    const secret_key = 'Bar12345Bar12345';
    const secret_iv = 'sayangsamakhanza';
    const encrypt_method = 'aes-256-cbc';

    const key = crypto.createHash('sha256').update(secret_key).digest('hex');
    const iv = crypto.createHash('sha256').update(secret_iv).digest('hex').substring(0, 16);

    try {
        if (action === 'e') {
            const cipher = crypto.createCipheriv(encrypt_method, Buffer.from(key, 'utf8').slice(0, 32), Buffer.from(iv, 'utf8'));
            let encrypted = cipher.update(string, 'utf8', 'base64');
            encrypted += cipher.final('base64');
            return encrypted;
        } else if (action === 'd') {
            const decipher = crypto.createDecipheriv(encrypt_method, Buffer.from(key, 'utf8').slice(0, 32), Buffer.from(iv, 'utf8'));
            let decrypted = decipher.update(string, 'base64', 'utf8');
            decrypted += decipher.final('utf8');
            return decrypted;
        }
    } catch (err) {
        console.error('Encryption Error:', err.message);
    }
    return null;
}

// Helper: Sanitize string input
function cleankar(dirty) {
    if (!dirty) return '';
    let clean = String(dirty).replace(/['\\;`]/g, '');
    clean = clean.replace(/--/g, '');
    clean = clean.replace(/\/\*/g, '');
    return clean.replace(/[^a-zA-Z0-9\s_,@. ]/g, '');
}

// Helper: Format date in Indonesian style
function formatIndoDate(dateStr) {
    if (!dateStr) return '';
    const parts = dateStr.split('-');
    if (parts.length !== 3) return dateStr;
    const year = parts[0];
    const monthIndex = parseInt(parts[1], 10) - 1;
    const day = parseInt(parts[2], 10);
    
    const monthsIndo = [
        'Januari', 'Februari', 'Maret', 'April', 'Mei', 'Juni',
        'Juli', 'Agustus', 'September', 'Oktober', 'November', 'Desember'
    ];
    
    const d = new Date(year, monthIndex, day);
    const daysIndo = ['Minggu', 'Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu'];
    const dayName = daysIndo[d.getDay()];
    
    return `${dayName}, ${day} ${monthsIndo[monthIndex]} ${year}`;
}

// Helper: Send WhatsApp Message using Fonnte API
async function sendFonnteMessage(token, target, message) {
    if (!token || !target) {
        console.log('Fonnte token or target phone number is empty. Skipping WA notification.');
        return;
    }
    
    let cleanTarget = target.trim().replace(/[^0-9]/g, '');
    if (cleanTarget.startsWith('0')) {
        cleanTarget = '62' + cleanTarget.substring(1);
    } else if (cleanTarget.startsWith('8')) {
        cleanTarget = '62' + cleanTarget;
    } else if (!cleanTarget.startsWith('62')) {
        cleanTarget = '62' + cleanTarget;
    }

    console.log(`Sending Fonnte WA message to ${cleanTarget}...`);

    try {
        const response = await fetch('https://api.fonnte.com/send', {
            method: 'POST',
            headers: {
                'Authorization': token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                target: cleanTarget,
                message: message,
                countryCode: '62'
            })
        });
        const body = await response.text();
        console.log(`Fonnte Response: StatusCode = ${response.status}, Body = ${body}`);
    } catch (err) {
        console.error('Error executing sendFonnteMessage:', err.message);
    }
}

// Helper: Get active patient norm from session
function getSessionNorm(req) {
    if (req.session && req.session.ses_pasien) {
        return cleankar(encrypt_decrypt(req.session.ses_pasien, 'd'));
    }
    return null;
}

// Router matching api.php actions
app.all('/api.php', async (req, res) => {
    const action = req.query.action || '';
    const params = req.method === 'POST' ? req.body : req.query;

    console.log(`API Call Received: Action = "${action}", Method = ${req.method}`);

    try {
        if (action === 'login') {
            const norm = cleankar(params.norm);
            const password = params.password || '';

            if (!norm || !password) {
                return res.json({ success: false, message: 'Nomer Rekam Medis dan Password wajib diisi.' });
            }

            // check count
            const [checkRows] = await pool.query(
                `select count(*) as count from personal_pasien where no_rkm_medis=? and password=aes_encrypt(?,'windi')`,
                [norm, password]
            );

            if (checkRows[0].count > 0) {
                req.session.ses_pasien = encrypt_decrypt(norm, 'e');

                // Get patient profile
                const [userRows] = await pool.query(
                    `select pasien.nm_pasien, pasien.no_ktp, pasien.email, pasien.jk, personal_pasien.gambar, pasien.no_tlp, pasien.tmp_lahir, date_format(pasien.tgl_lahir,'%d/%m/%Y') as tgl_lahir, pasien.alamat from pasien inner join personal_pasien on personal_pasien.no_rkm_medis=pasien.no_rkm_medis where pasien.no_rkm_medis=?`,
                    [norm]
                );

                if (userRows.length > 0) {
                    const user = userRows[0];
                    let photo = '';
                    if (!user.gambar || user.gambar === '' || user.gambar === '-') {
                        photo = (user.jk === 'L') ? 'images/userlaki.png' : 'images/userperempuan.png';
                    } else {
                        photo = `http://${req.headers.host}/webapps/photopasien/${user.gambar}`;
                    }

                    // Save session attributes
                    req.session.nm_pasien = user.nm_pasien;
                    req.session.email = user.email;
                    req.session.jk = user.jk;
                    req.session.no_tlp = user.no_tlp;
                    req.session.tmp_lahir = user.tmp_lahir;
                    req.session.tgl_lahir = user.tgl_lahir;
                    req.session.photo = photo;

                    return res.json({
                        success: true,
                        user: {
                            name: user.nm_pasien,
                            norm: norm,
                            nik: user.no_ktp || '',
                            phone: user.no_tlp || '',
                            email: user.email || '',
                            gender: user.jk,
                            pob: user.tmp_lahir || '',
                            dob: user.tgl_lahir || '',
                            address: user.alamat || '',
                            photo: photo
                        }
                    });
                }
            }
            return res.json({ success: false, message: 'Nomor rekam medis atau password Anda salah.' });
        }

        if (action === 'logout') {
            req.session.destroy(() => {
                res.json({ success: true });
            });
            return;
        }

        // Action gate: Session verification
        const norm = getSessionNorm(req);
        if (!norm) {
            return res.json({ success: false, message: 'Session expired. Silakan login kembali.' });
        }

        if (action === 'get_dashboard_data') {
            const today = new Date().toISOString().split('T')[0];
            const daysIndo = ['Minggu', 'Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu'];
            const todayIndo = daysIndo[new Date().getDay()];
            
            // Get active queue from reg_periksa
            const [queueRows] = await pool.query(
                `select reg_periksa.tgl_registrasi as tanggal_periksa, reg_periksa.kd_dokter, dokter.nm_dokter, reg_periksa.kd_poli, poliklinik.nm_poli, reg_periksa.no_reg, reg_periksa.stts as status from reg_periksa inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli where reg_periksa.no_rkm_medis=? and reg_periksa.tgl_registrasi>=? and reg_periksa.stts<>'Batal' and reg_periksa.stts<>'Sudah' order by reg_periksa.tgl_registrasi asc limit 1`,
                [norm, today]
            );

            let activeQueue = null;
            if (queueRows.length > 0) {
                const row = queueRows[0];
                let formattedDate = row.tanggal_periksa;
                if (formattedDate instanceof Date) {
                    const year = formattedDate.getFullYear();
                    const month = String(formattedDate.getMonth() + 1).padStart(2, '0');
                    const day = String(formattedDate.getDate()).padStart(2, '0');
                    formattedDate = `${year}-${month}-${day}`;
                }
                activeQueue = {
                    status: (row.status === 'Belum') ? 'Terdaftar' : row.status,
                    queueNum: row.no_reg,
                    doctor: row.nm_dokter,
                    clinic: row.nm_poli,
                    kd_dokter: row.kd_dokter,
                    kd_poli: row.kd_poli,
                    date: formattedDate
                };
            }

            // Get counts
            const [[labCount]] = await pool.query(`select count(distinct periksa_lab.no_rawat) as count from periksa_lab inner join reg_periksa on periksa_lab.no_rawat=reg_periksa.no_rawat where reg_periksa.no_rkm_medis=?`, [norm]);
            const [[radCount]] = await pool.query(`select count(distinct periksa_radiologi.no_rawat) as count from periksa_radiologi inner join reg_periksa on periksa_radiologi.no_rawat=reg_periksa.no_rawat where reg_periksa.no_rkm_medis=?`, [norm]);
            const [[visitCount]] = await pool.query(`select count(*) as count from reg_periksa where no_rkm_medis=?`, [norm]);
            const [[rajalCount]] = await pool.query(`select count(*) as count from reg_periksa where no_rkm_medis=? and status_lanjut='Ralan'`, [norm]);
            const [[ranapCount]] = await pool.query(`select count(*) as count from reg_periksa where no_rkm_medis=? and status_lanjut='Ranap'`, [norm]);

            // Get hospital settings
            let hospitalName = 'Rumah Sakit';
            const [settingRows] = await pool.query(`select nama_instansi from setting limit 1`);
            if (settingRows.length > 0) {
                hospitalName = settingRows[0].nama_instansi;
            }

            // Get today's doctor schedules from SIMRS database
            const [scheduleRows] = await pool.query(
                `select jadwal.kd_dokter, dokter.nm_dokter, jadwal.kd_poli, poliklinik.nm_poli, jadwal.jam_mulai, jadwal.jam_selesai, jadwal.kuota, (select count(*) from reg_periksa where reg_periksa.kd_dokter=jadwal.kd_dokter and reg_periksa.kd_poli=jadwal.kd_poli and reg_periksa.tgl_registrasi=CURDATE() and reg_periksa.stts<>'Batal') as checkin from jadwal inner join dokter on jadwal.kd_dokter=dokter.kd_dokter inner join poliklinik on jadwal.kd_poli=poliklinik.kd_poli where jadwal.hari_kerja=?`,
                [todayIndo]
            );

            const schedules = scheduleRows.map(r => ({
                name: r.nm_dokter,
                clinic: r.nm_poli,
                start: r.jam_mulai,
                end: r.jam_selesai,
                quota: r.kuota,
                checkin: r.checkin
            }));

            return res.json({
                success: true,
                activeQueue,
                hospitalName,
                schedules,
                counts: {
                    labCount: Number(labCount.count),
                    radCount: Number(radCount.count),
                    visitCount: Number(visitCount.count),
                    rajalCount: Number(rajalCount.count),
                    ranapCount: Number(ranapCount.count)
                }
            });
        }

        if (action === 'get_booking_data') {
            const [clinicRows] = await pool.query(`select kd_poli, nm_poli from poliklinik where status='1' order by nm_poli`);
            const clinics = clinicRows.map(r => ({ id: r.kd_poli, name: r.nm_poli }));

            const [scheduleRows] = await pool.query(
                `select jadwal.kd_dokter, dokter.nm_dokter, jadwal.kd_poli, poliklinik.registrasi from jadwal inner join dokter on jadwal.kd_dokter=dokter.kd_dokter inner join poliklinik on jadwal.kd_poli=poliklinik.kd_poli`
            );

            const doctors = {};
            scheduleRows.forEach(r => {
                const kd_poli = r.kd_poli;
                if (!doctors[kd_poli]) {
                    doctors[kd_poli] = [];
                }
                const exists = doctors[kd_poli].some(d => d.id === r.kd_dokter);
                if (!exists) {
                    doctors[kd_poli].push({
                        id: r.kd_dokter,
                        name: r.nm_dokter,
                        fee: 'Rp. ' + Number(r.registrasi).toLocaleString('id-ID')
                    });
                }
            });

            return res.json({
                success: true,
                clinics,
                doctors
            });
        }

        if (action === 'save_booking') {
            const kd_poli = cleankar(params.clinic);
            const kd_dokter = cleankar(params.doctor);
            const tanggalRaw = cleankar(params.date);
            const penjab = 'UM'; // Default UMUM

            if (!kd_poli || !kd_dokter || !tanggalRaw) {
                return res.json({ success: false, message: 'Poliklinik, Dokter, dan Tanggal Periksa wajib diisi.' });
            }

            // Clean date format (handle YYYYMMDD and format back to YYYY-MM-DD)
            let tanggal = tanggalRaw;
            if (tanggalRaw.length === 8 && /^\d+$/.test(tanggalRaw)) {
                tanggal = `${tanggalRaw.substring(0, 4)}-${tanggalRaw.substring(4, 6)}-${tanggalRaw.substring(6, 8)}`;
            }

            // Check if patient already registered in reg_periksa on the target date
            const [checkRows] = await pool.query(
                `select count(*) as count from reg_periksa where no_rkm_medis=? and tgl_registrasi=? and stts<>'Batal'`,
                [norm, tanggal]
            );

            if (checkRows[0].count > 0) {
                return res.json({ success: false, message: 'Anda sudah terdaftar untuk pemeriksaan pada tanggal tersebut.' });
            }

            // Check Cashier closing setting
            const [validRes] = await pool.query(`select wajib_closing_kasir from set_validasi_registrasi`);
            if (validRes.length > 0 && validRes[0].wajib_closing_kasir === 'Yes') {
                const [unpaidRes] = await pool.query(`select count(no_rkm_medis) as count from reg_periksa where no_rkm_medis=? and status_bayar='Belum Bayar' and stts<>'Batal'`, [norm]);
                if (unpaidRes[0].count > 0) {
                    return res.json({ success: false, message: 'Gagal registrasi. Anda memiliki tagihan kunjungan sebelumnya yang belum lunas.' });
                }
            }

            // Calculate age
            await pool.query(
                `update pasien set umur = CONCAT(CONCAT(CONCAT(TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()), ' Th '),CONCAT(TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12), ' Bl ')),CONCAT(TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()), ' Hr')) where no_rkm_medis=?`,
                [norm]
            );

            // Check if New or Old visitor
            const [visitCheck] = await pool.query(`select count(no_rkm_medis) as count from reg_periksa where no_rkm_medis=? and kd_poli=?`, [norm, kd_poli]);
            const statuspoli = visitCheck[0].count > 0 ? 'Lama' : 'Baru';

            // Generate rawat index number
            const [maxRawat] = await pool.query(`select ifnull(MAX(CONVERT(RIGHT(no_rawat,6),signed)),0)+1 as nextRawat from reg_periksa where tgl_registrasi=?`, [tanggal]);
            const rawatNum = String(maxRawat[0].nextRawat).padStart(6, '0');
            const no_rawat = `${tanggal.replace(/-/g, '/')}/${rawatNum}`;

            // Fetch patient variables
            const [patientRows] = await pool.query(
                `select no_rkm_medis, nm_pasien, no_tlp, namakeluarga, alamatpj, keluarga, TIMESTAMPDIFF(YEAR, pasien.tgl_lahir, CURDATE()) as tahun, (TIMESTAMPDIFF(MONTH, pasien.tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12)) as bulan, TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(pasien.tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, pasien.tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, pasien.tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()) as hari, tgl_daftar from pasien where no_rkm_medis=?`,
                [norm]
            );

            if (patientRows.length === 0) {
                return res.json({ success: false, message: 'Data pasien tidak ditemukan.' });
            }

            const p = patientRows[0];
            let umur = p.tahun;
            let sttsumur = 'Th';

            if (umur === 0) {
                if (p.bulan > 0) {
                    umur = p.bulan;
                    sttsumur = 'Bl';
                } else {
                    umur = p.hari;
                    sttsumur = 'Hr';
                }
            }

            // Check fee type
            const [[poliFees]] = await pool.query(`select registrasi, registrasilama from poliklinik where kd_poli=?`, [kd_poli]);
            let tglDaftarStr = p.tgl_daftar;
            if (tglDaftarStr instanceof Date) {
                const year = tglDaftarStr.getFullYear();
                const month = String(tglDaftarStr.getMonth() + 1).padStart(2, '0');
                const day = String(tglDaftarStr.getDate()).padStart(2, '0');
                tglDaftarStr = `${year}-${month}-${day}`;
            }
            const biayareg = (tglDaftarStr === tanggal) ? poliFees.registrasi : poliFees.registrasilama;

            // Penomoran Antrean in reg_periksa
            let maxCount = 0;
            const urutType = config.urutnoreg;

            if (urutType === 'poli') {
                const [maxRes] = await pool.query(`select ifnull(MAX(CONVERT(no_reg,signed)),0)+1 as maxReg from reg_periksa where kd_poli=? and tgl_registrasi=?`, [kd_poli, tanggal]);
                maxCount = maxRes[0].maxReg;
            } else if (urutType === 'dokter') {
                const [maxRes] = await pool.query(`select ifnull(MAX(CONVERT(no_reg,signed)),0)+1 as maxReg from reg_periksa where kd_dokter=? and tgl_registrasi=?`, [kd_dokter, tanggal]);
                maxCount = maxRes[0].maxReg;
            } else if (urutType === 'dokter + poli') {
                const [maxRes] = await pool.query(`select ifnull(MAX(CONVERT(no_reg,signed)),0)+1 as maxReg from reg_periksa where kd_poli=? and kd_dokter=? and tgl_registrasi=?`, [kd_poli, kd_dokter, tanggal]);
                maxCount = maxRes[0].maxReg;
            } else {
                const [maxRes] = await pool.query(`select ifnull(MAX(CONVERT(no_reg,signed)),0)+1 as maxReg from reg_periksa where kd_dokter=? and tgl_registrasi=?`, [kd_dokter, tanggal]);
                maxCount = maxRes[0].maxReg;
            }

            const noReg = String(maxCount).padStart(3, '0');

            // Insert into reg_periksa directly
            const [insertRes] = await pool.query(
                `insert into reg_periksa (no_reg, no_rawat, tgl_registrasi, jam_reg, kd_dokter, no_rkm_medis, kd_poli, p_jawab, almt_pj, hubunganpj, biaya_reg, stts, stts_daftar, status_lanjut, kd_pj, umurdaftar, sttsumur, status_bayar, status_poli) values (?, ?, ?, CURRENT_TIME(), ?, ?, ?, ?, ?, ?, ?, 'Belum', 'Lama', 'Ralan', ?, ?, ?, 'Belum Bayar', ?)`,
                [noReg, no_rawat, tanggal, kd_dokter, norm, kd_poli, p.namakeluarga || '', p.alamatpj || '', p.keluarga || '', biayareg, penjab, umur, sttsumur, statuspoli]
            );

            if (insertRes.affectedRows > 0) {
                // Update skdp_bpjs if applicable
                await pool.query(`update skdp_bpjs set status='Sudah Periksa' where no_rkm_medis=? and tanggal_datang=?`, [norm, tanggal]);

                const [[docRes]] = await pool.query(`select nm_dokter from dokter where kd_dokter=?`, [kd_dokter]);
                const [[poliRes]] = await pool.query(`select nm_poli, registrasi from poliklinik where kd_poli=?`, [kd_poli]);

                // --- WhatsApp Notification Fonnte ---
                const currentFonnteToken = getDbConfig().fonnte_token;
                if (currentFonnteToken && p.no_tlp) {
                    try {
                        const [settingRows] = await pool.query(`select nama_instansi from setting limit 1`);
                        const hospitalName = settingRows.length > 0 ? settingRows[0].nama_instansi : 'Rumah Sakit';
                        const formattedDate = formatIndoDate(tanggal);
                        const waMessage = 
`*BUKTI PENDAFTARAN ONLINE*

Halo *${p.nm_pasien}*, pendaftaran online Anda telah berhasil disimpan. Berikut rincian kunjungan Anda:

• *No. RM:* ${norm}
• *No. Rawat:* ${no_rawat}
• *Poliklinik:* ${poliRes.nm_poli}
• *Dokter:* ${docRes.nm_dokter}
• *Tanggal Periksa:* ${formattedDate}
• *Nomor Antrean:* ${noReg}

Silakan datang 15-30 menit sebelum jadwal untuk verifikasi berkas dan pembayaran. Tunjukkan pesan pendaftaran ini kepada petugas saat check-in.

Terima kasih atas kepercayaan Anda.
*${hospitalName}*`;

                        sendFonnteMessage(currentFonnteToken, p.no_tlp, waMessage);
                    } catch (waErr) {
                        console.error('Failed to trigger Fonnte WA message:', waErr);
                    }
                }
                // -------------------------------------

                return res.json({
                    success: true,
                    booking: {
                        patientName: req.session.nm_pasien || 'Pasien',
                        clinicName: poliRes.nm_poli,
                        doctorName: docRes.nm_dokter,
                        date: new Date(tanggal).toLocaleDateString('id-ID', { day: '2-digit', month: 'short', year: 'numeric' }),
                        fee: 'Rp. ' + Number(poliRes.registrasi).toLocaleString('id-ID'),
                        noReg: noReg
                    }
                });
            }

            return res.json({ success: false, message: 'Gagal melakukan pendaftaran langsung di database SIMRS.' });
        }

        if (action === 'check_in') {
            const kd_dokter = cleankar(params.kd_dokter);
            const kd_poli = cleankar(params.kd_poli);
            const tanggalRaw = cleankar(params.date);
            const no_reg = cleankar(params.queueNum);
            const kd_pj = 'UM';

            if (!kd_dokter || !kd_poli || !tanggalRaw) {
                return res.json({ success: false, message: 'Parameter check-in tidak lengkap.' });
            }

            // Clean date format (handle YYYYMMDD and format back to YYYY-MM-DD)
            let tanggal = tanggalRaw;
            if (tanggalRaw.length === 8 && /^\d+$/.test(tanggalRaw)) {
                tanggal = `${tanggalRaw.substring(0, 4)}-${tanggalRaw.substring(4, 6)}-${tanggalRaw.substring(6, 8)}`;
            }

            // Check Cashier closing setting
            const [validRes] = await pool.query(`select wajib_closing_kasir from set_validasi_registrasi`);
            if (validRes.length > 0 && validRes[0].wajib_closing_kasir === 'Yes') {
                const [unpaidRes] = await pool.query(`select count(no_rkm_medis) as count from reg_periksa where no_rkm_medis=? and status_bayar='Belum Bayar' and stts<>'Batal'`, [norm]);
                if (unpaidRes[0].count > 0) {
                    return res.json({ success: false, message: 'Gagal check-in. Anda memiliki tagihan kunjungan sebelumnya yang belum lunas.' });
                }
            }

            // Calculate age
            await pool.query(
                `update pasien set umur = CONCAT(CONCAT(CONCAT(TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()), ' Th '),CONCAT(TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12), ' Bl ')),CONCAT(TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()), ' Hr')) where no_rkm_medis=?`,
                [norm]
            );

            // Check if New or Old
            const [visitCheck] = await pool.query(`select count(no_rkm_medis) as count from reg_periksa where no_rkm_medis=? and kd_poli=?`, [norm, kd_poli]);
            const statuspoli = visitCheck[0].count > 0 ? 'Lama' : 'Baru';

            // Generate rawat index number
            const [maxRawat] = await pool.query(`select ifnull(MAX(CONVERT(RIGHT(no_rawat,6),signed)),0)+1 as nextRawat from reg_periksa where tgl_registrasi=?`, [tanggal]);
            const rawatNum = String(maxRawat[0].nextRawat).padStart(6, '0');
            const no_rawat = `${tanggal.replace(/-/g, '/')}/${rawatNum}`;

            // Fetch patient variables
            const [patientRows] = await pool.query(
                `select no_rkm_medis, nm_pasien, no_tlp, namakeluarga, alamatpj, keluarga, TIMESTAMPDIFF(YEAR, pasien.tgl_lahir, CURDATE()) as tahun, (TIMESTAMPDIFF(MONTH, pasien.tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12)) as bulan, TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(pasien.tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, pasien.tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, pasien.tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()) as hari, tgl_daftar from pasien where no_rkm_medis=?`,
                [norm]
            );

            if (patientRows.length > 0) {
                const p = patientRows[0];
                let umur = p.tahun;
                let sttsumur = 'Th';

                if (umur === 0) {
                    if (p.bulan > 0) {
                        umur = p.bulan;
                        sttsumur = 'Bl';
                    } else {
                        umur = p.hari;
                        sttsumur = 'Hr';
                    }
                }

                // Check fee type
                const [[poliFees]] = await pool.query(`select registrasi, registrasilama from poliklinik where kd_poli=?`, [kd_poli]);
                let tglDaftarStr = p.tgl_daftar;
                if (tglDaftarStr instanceof Date) {
                    const year = tglDaftarStr.getFullYear();
                    const month = String(tglDaftarStr.getMonth() + 1).padStart(2, '0');
                    const day = String(tglDaftarStr.getDate()).padStart(2, '0');
                    tglDaftarStr = `${year}-${month}-${day}`;
                }
                const biayareg = (tglDaftarStr === tanggal) ? poliFees.registrasi : poliFees.registrasilama;

                // Insert registration record
                const [insertReg] = await pool.query(
                    `insert into reg_periksa (no_reg, no_rawat, tgl_registrasi, jam_reg, kd_dokter, no_rkm_medis, kd_poli, p_jawab, almt_pj, hubunganpj, biaya_reg, stts, stts_daftar, status_lanjut, kd_pj, umurdaftar, sttsumur, status_bayar, status_poli) values (?, ?, ?, CURRENT_TIME(), ?, ?, ?, ?, ?, ?, ?, 'Belum', 'Lama', 'Ralan', ?, ?, ?, 'Belum Bayar', ?)`,
                    [no_reg, no_rawat, tanggal, kd_dokter, norm, kd_poli, p.namakeluarga || '', p.alamatpj || '', p.keluarga || '', biayareg, kd_pj, umur, sttsumur, statuspoli]
                );

                if (insertReg.affectedRows > 0) {
                    await pool.query(`update skdp_bpjs set status='Sudah Periksa' where no_rkm_medis=? and tanggal_datang=?`, [norm, tanggal]);
                    await pool.query(`update booking_registrasi set status='Terdaftar' where no_rkm_medis=? and tanggal_periksa=? and kd_dokter=? and kd_poli=? and kd_pj=?`, [norm, tanggal, kd_dokter, kd_poli, kd_pj]);

                    const [[docRes]] = await pool.query(`select nm_dokter from dokter where kd_dokter=?`, [kd_dokter]);
                    const [[poliRes]] = await pool.query(`select nm_poli from poliklinik where kd_poli=?`, [kd_poli]);

                    // --- WhatsApp Notification Fonnte ---
                    const currentFonnteToken = getDbConfig().fonnte_token;
                    if (currentFonnteToken && p.no_tlp) {
                        try {
                            const [settingRows] = await pool.query(`select nama_instansi from setting limit 1`);
                            const hospitalName = settingRows.length > 0 ? settingRows[0].nama_instansi : 'Rumah Sakit';
                            const formattedDate = formatIndoDate(tanggal);
                            const waMessage = 
`*BUKTI PENDAFTARAN ONLINE (CHECK-IN)*

Halo *${p.nm_pasien}*, check-in pendaftaran online Anda telah berhasil. Berikut rincian kunjungan Anda:

• *No. RM:* ${norm}
• *No. Rawat:* ${no_rawat}
• *Poliklinik:* ${poliRes.nm_poli}
• *Dokter:* ${docRes.nm_dokter}
• *Tanggal Periksa:* ${formattedDate}
• *Nomor Antrean:* ${no_reg}

Silakan menuju ke poliklinik tujuan Anda. Tunjukkan bukti check-in ini bila diperlukan.

Terima kasih atas kepercayaan Anda.
*${hospitalName}*`;

                            sendFonnteMessage(currentFonnteToken, p.no_tlp, waMessage);
                        } catch (waErr) {
                            console.error('Failed to trigger Fonnte WA message for check-in:', waErr);
                        }
                    }
                    // -------------------------------------

                    return res.json({ success: true });
                }
            }
            return res.json({ success: false, message: 'Gagal mendaftarkan antrean check-in.' });
        }

        if (action === 'get_medical_records') {
            // Visits
            const [visitRows] = await pool.query(
                `select reg_periksa.no_rawat, reg_periksa.no_reg, reg_periksa.tgl_registrasi, dokter.nm_dokter, poliklinik.nm_poli, reg_periksa.status_lanjut from reg_periksa inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli where reg_periksa.no_rkm_medis=? order by reg_periksa.tgl_registrasi desc`,
                [norm]
            );

            const visits = [];
            for (const r of visitRows) {
                // Fetch diagnosis
                const [diagRows] = await pool.query(
                    `select penyakit.nm_penyakit from diagnosa_pasien inner join penyakit on diagnosa_pasien.kd_penyakit=penyakit.kd_penyakit where diagnosa_pasien.no_rawat=? limit 1`,
                    [r.no_rawat]
                );
                const diagnosa = diagRows.length > 0 ? diagRows[0].nm_penyakit : 'Belum diinput';

                // Fetch vitals
                let vitals = { suhu: '-', tensi: '-', berat: '-' };
                const table = (r.status_lanjut === 'Ralan') ? 'pemeriksaan_ralan' : 'pemeriksaan_ranap';
                
                let [pemRows] = await pool.query(`select suhu_tubuh, tensi, berat from ${table} where no_rawat=? limit 1`, [r.no_rawat]);
                if (r.status_lanjut === 'Ranap' && pemRows.length === 0) {
                    [pemRows] = await pool.query(`select suhu_tubuh, tensi, berat from pemeriksaan_ralan where no_rawat=? limit 1`, [r.no_rawat]);
                }

                if (pemRows.length > 0) {
                    vitals = {
                        suhu: pemRows[0].suhu_tubuh ? pemRows[0].suhu_tubuh + ' °C' : '-',
                        tensi: pemRows[0].tensi ? pemRows[0].tensi + ' mmHg' : '-',
                        berat: pemRows[0].berat ? pemRows[0].berat + ' kg' : '-'
                    };
                }

                // Fetch medicine recipes
                const [recipeRows] = await pool.query(
                    `select databarang.nama_brng, detail_pemberian_obat.jml from detail_pemberian_obat inner join databarang on detail_pemberian_obat.kode_brng=databarang.kode_brng where detail_pemberian_obat.no_rawat=?`,
                    [r.no_rawat]
                );
                const recipes = recipeRows.map(rec => `${rec.nama_brng} (${rec.jml})`).join(', ') || 'Tidak ada resep obat.';

                visits.push({
                    id: r.no_rawat,
                    noRawat: r.no_rawat,
                    noReg: r.no_reg,
                    date: new Date(r.tgl_registrasi).toLocaleDateString('id-ID', { day: '2-digit', month: 'short', year: 'numeric' }),
                    type: (r.status_lanjut === 'Ralan') ? 'Rawat Jalan' : 'Rawat Inap',
                    clinic: r.nm_poli,
                    doctor: r.nm_dokter,
                    status: 'Selesai',
                    diagnosa,
                    tensi: vitals.tensi,
                    suhu: vitals.suhu,
                    berat: vitals.berat,
                    resep: recipes,
                    tindakan: 'Konsultasi & Pemeriksaan Fisik'
                });
            }

            // Labs
            const [labRows] = await pool.query(
                `select distinct reg_periksa.no_rawat, reg_periksa.tgl_registrasi, dokter.nm_dokter from periksa_lab inner join reg_periksa on periksa_lab.no_rawat=reg_periksa.no_rawat inner join dokter on periksa_lab.kd_dokter=dokter.kd_dokter where reg_periksa.no_rkm_medis=? order by reg_periksa.tgl_registrasi desc`,
                [norm]
            );

            const labs = [];
            for (const r of labRows) {
                const [detailRows] = await pool.query(
                    `select detail_periksa_lab.nilai, detail_periksa_lab.nilai_rujukan, detail_periksa_lab.keterangan, template_laboratorium.Pemeriksaan, template_laboratorium.satuan from detail_periksa_lab inner join template_laboratorium on detail_periksa_lab.id_template=template_laboratorium.id_template where detail_periksa_lab.no_rawat=?`,
                    [r.no_rawat]
                );

                const results = detailRows.map(d => ({
                    name: d.Pemeriksaan,
                    value: d.nilai,
                    unit: d.satuan,
                    ref: d.nilai_rujukan,
                    status: d.keterangan || 'Normal'
                }));

                labs.push({
                    id: r.no_rawat,
                    date: new Date(r.tgl_registrasi).toLocaleDateString('id-ID', { day: '2-digit', month: 'short', year: 'numeric' }),
                    title: 'Pemeriksaan Laboratorium',
                    doctor: r.nm_dokter,
                    status: 'Selesai',
                    results,
                    kesimpulan: 'Hasil periksa laboratorium PK selesai.'
                });
            }

            // Radiologi
            const [radRows] = await pool.query(
                `select periksa_radiologi.no_rawat, periksa_radiologi.tgl_periksa, dokter.nm_dokter, hasil_radiologi.hasil from periksa_radiologi inner join reg_periksa on periksa_radiologi.no_rawat=reg_periksa.no_rawat inner join dokter on periksa_radiologi.kd_dokter=dokter.kd_dokter left join hasil_radiologi on periksa_radiologi.no_rawat=hasil_radiologi.no_rawat where reg_periksa.no_rkm_medis=? order by periksa_radiologi.tgl_periksa desc`,
                [norm]
            );

            const rads = [];
            for (const r of radRows) {
                const [imgRows] = await pool.query(
                    `select lokasi_gambar from gambar_radiologi where no_rawat=?`,
                    [r.no_rawat]
                );
                
                // PERBAIKAN: Format path gambar agar support backslash Windows (\) dan karakter spasi
                const images = imgRows.map(img => {
                    let lokasi = img.lokasi_gambar;
                    // Ubah backslash Windows ke forward slash Web
                    lokasi = lokasi.replace(/\\/g, '/');
                    // Encode nama file (mengamankan karakter spasi menjadi %20)
                    lokasi = encodeURI(lokasi);
                    
                    if (lokasi.startsWith('radiologi/')) {
                        return `http://${req.headers.host}/webapps/${lokasi}`;
                    } else {
                        return `http://${req.headers.host}/webapps/radiologi/${lokasi}`;
                    }
                });

                let formattedDate = r.tgl_periksa;
                if (formattedDate instanceof Date) {
                    formattedDate = formattedDate.toLocaleDateString('id-ID', { day: '2-digit', month: 'short', year: 'numeric' });
                } else if (formattedDate) {
                    formattedDate = new Date(formattedDate).toLocaleDateString('id-ID', { day: '2-digit', month: 'short', year: 'numeric' });
                }

                rads.push({
                    id: r.no_rawat,
                    date: formattedDate,
                    title: 'Pemeriksaan Radiologi',
                    doctor: r.nm_dokter,
                    status: 'Selesai',
                    examType: 'Rontgen / USG',
                    findings: r.hasil || 'Tidak ada catatan temuan.',
                    kesimpulan: 'Hasil pemeriksaan radiologi selesai.',
                    images: images
                });
            }

            return res.json({
                success: true,
                visits,
                labs,
                rads
            });
        }

        if (action === 'change_password') {
            const oldPassword = params.oldPassword || '';
            const newPassword = params.newPassword || '';

            if (!oldPassword || !newPassword) {
                return res.json({ success: false, message: 'Password lama dan baru wajib diisi.' });
            }

            const [checkRows] = await pool.query(
                `select count(*) as count from personal_pasien where no_rkm_medis=? and password=aes_encrypt(?,'windi')`,
                [norm, oldPassword]
            );

            if (checkRows[0].count > 0) {
                const [updateRes] = await pool.query(
                    `update personal_pasien set password=aes_encrypt(?,'windi') where no_rkm_medis=?`,
                    [newPassword, norm]
                );
                if (updateRes.affectedRows > 0) {
                    return res.json({ success: true });
                }
                return res.json({ success: false, message: 'Gagal memperbarui password di database.' });
            }
            return res.json({ success: false, message: 'Password lama tidak sesuai.' });
        }

        if (action === 'download_image') {
            const norm = getSessionNorm(req);
            if (!norm) {
                return res.status(401).json({ success: false, message: 'Session expired. Silakan login kembali.' });
            }

            const imgUrl = params.url || '';
            if (!imgUrl) {
                return res.status(400).json({ success: false, message: 'Parameter URL wajib diisi.' });
            }

            try {
                const parsedUrl = new URL(imgUrl);
                const allowedHosts = ['localhost', '127.0.0.1', config.host, config.webapps_host];
                const isAllowedHost = allowedHosts.includes(parsedUrl.hostname) || parsedUrl.hostname.endsWith('.rsmardhatillah.com');
                const isWebapps = parsedUrl.pathname.startsWith('/webapps/');

                if (!isAllowedHost || !isWebapps) {
                    return res.status(403).json({ success: false, message: 'Domain atau path tidak diizinkan.' });
                }

                const filename = path.basename(parsedUrl.pathname) || 'ronsen.jpg';

                // Check local file
                const relativePath = parsedUrl.pathname.replace(/^\/webapps/, '');
                const localPath = path.join(__dirname, '..', '..', 'webapps', relativePath);

                if (fs.existsSync(localPath) && fs.statSync(localPath).isFile()) {
                    res.setHeader('Content-Disposition', `attachment; filename="${filename}"`);
                    const ext = path.extname(localPath).toLowerCase();
                    if (ext === '.png') res.setHeader('Content-Type', 'image/png');
                    else if (ext === '.gif') res.setHeader('Content-Type', 'image/gif');
                    else res.setHeader('Content-Type', 'image/jpeg');

                    return fs.createReadStream(localPath).pipe(res);
                } else {
                    // Fetch from production/webapps host
                    let remoteUrl = imgUrl;
                    if (parsedUrl.hostname === 'localhost' || parsedUrl.hostname === '127.0.0.1') {
                        remoteUrl = getRemoteUrl(parsedUrl.pathname);
                    }

                    console.log(`Node downloading remote image: ${remoteUrl}`);
                    const imgResponse = await fetch(remoteUrl);
                    if (!imgResponse.ok) {
                        return res.status(404).json({ success: false, message: 'Gambar tidak ditemukan.' });
                    }

                    res.setHeader('Content-Disposition', `attachment; filename="${filename}"`);
                    const contentType = imgResponse.headers.get('content-type');
                    if (contentType) {
                        res.setHeader('Content-Type', contentType);
                    } else {
                        res.setHeader('Content-Type', 'image/jpeg');
                    }

                    const arrayBuffer = await imgResponse.arrayBuffer();
                    return res.send(Buffer.from(arrayBuffer));
                }
            } catch (err) {
                console.error('Download error:', err);
                return res.status(500).json({ success: false, message: 'Internal Server Error: ' + err.message });
            }
        }

        if (action === 'get_consents') {
            try {
                // Fetch General Consents
                const [spuRows] = await pool.query(`
                    SELECT 
                        s.no_surat, s.no_rawat, s.tanggal, s.pengobatan_kepada, s.nilai_kepercayaan,
                        s.nama_pj, s.umur_pj, s.no_ktppj, s.jkpj, s.bertindak_atas, s.no_telp, s.nip,
                        pp.photo,
                        d.nm_dokter,
                        pol.nm_poli
                    FROM surat_persetujuan_umum s
                    INNER JOIN reg_periksa rp ON s.no_rawat = rp.no_rawat
                    INNER JOIN dokter d ON rp.kd_dokter = d.kd_dokter
                    INNER JOIN poliklinik pol ON rp.kd_poli = pol.kd_poli
                    LEFT JOIN surat_persetujuan_umum_pembuat_pernyataan pp ON s.no_surat = pp.no_surat
                    WHERE rp.no_rkm_medis = ?
                    ORDER BY s.tanggal DESC
                `, [norm]);

                const generalConsents = spuRows.map(r => {
                    let dateStr = '';
                    if (r.tanggal) {
                        const d = new Date(r.tanggal);
                        const year = d.getFullYear();
                        const month = String(d.getMonth() + 1).padStart(2, '0');
                        const day = String(d.getDate()).padStart(2, '0');
                        dateStr = `${year}-${month}-${day}`;
                    }
                    
                    const isSigned = !!r.photo;
                    const photoUrl = isSigned ? `http://${req.headers.host}/webapps/persetujuanumum/${r.photo}` : null;

                    return {
                        id: r.no_surat,
                        title: 'Persetujuan Umum (General Consent)',
                        date: dateStr ? formatIndoDate(dateStr) : '-',
                        status: isSigned ? 'Ditandatangani' : 'Belum Ditandatangani',
                        type: 'Umum',
                        noRawat: r.no_rawat,
                        photo: photoUrl,
                        content: `Saya yang bertanda tangan di bawah ini memberikan persetujuan untuk mendapatkan pelayanan kesehatan di rumah sakit dan memberikan kuasa kepada dokter serta perawat untuk melakukan asuhan keperawatan, pemeriksaan fisik, dan prosedur diagnostik rutin yang diperlukan. Saya telah menerima informasi tentang peraturan, hak dan kewajiban pasien, tarif ruang perawatan, tata tertib pasien pulang, serta setuju untuk mematuhi semua ketentuan yang berlaku.`,
                        meta: {
                            pengobatan_kepada: r.pengobatan_kepada,
                            nilai_kepercayaan: r.nilai_kepercayaan,
                            nama_pj: r.nama_pj,
                            umur_pj: r.umur_pj,
                            no_ktppj: r.no_ktppj,
                            jkpj: r.jkpj,
                            bertindak_atas: r.bertindak_atas,
                            no_telp: r.no_telp,
                            nip: r.nip,
                            dokter: r.nm_dokter,
                            poli: r.nm_poli
                        }
                    };
                });

                // Fetch Informed Consents
                const [pptRows] = await pool.query(`
                    SELECT 
                        p.*,
                        b.photo,
                        d.nm_dokter,
                        pol.nm_poli
                    FROM persetujuan_penolakan_tindakan p
                    INNER JOIN reg_periksa rp ON p.no_rawat = rp.no_rawat
                    INNER JOIN dokter d ON p.kd_dokter = d.kd_dokter
                    LEFT JOIN poliklinik pol ON rp.kd_poli = pol.kd_poli
                    LEFT JOIN bukti_persetujuan_penolakan_tindakan_penerimainformasi b ON p.no_pernyataan = b.no_pernyataan
                    WHERE rp.no_rkm_medis = ?
                    ORDER BY p.tanggal DESC
                `, [norm]);

                const informedConsents = pptRows.map(r => {
                    let dateStr = '';
                    if (r.tanggal) {
                        const d = new Date(r.tanggal);
                        const year = d.getFullYear();
                        const month = String(d.getMonth() + 1).padStart(2, '0');
                        const day = String(d.getDate()).padStart(2, '0');
                        dateStr = `${year}-${month}-${day}`;
                    }

                    const isSigned = !!r.photo && r.pernyataan !== 'Belum Dikonfirmasi';
                    const photoUrl = isSigned ? `http://${req.headers.host}/webapps/persetujuantindakan/${r.photo}` : null;

                    return {
                        id: r.no_pernyataan,
                        title: `Persetujuan/Penolakan Tindakan Medis (${r.tindakan})`,
                        date: dateStr ? formatIndoDate(dateStr) : '-',
                        status: isSigned ? 'Ditandatangani' : 'Belum Ditandatangani',
                        type: 'Tindakan',
                        noRawat: r.no_rawat,
                        photo: photoUrl,
                        content: `Persetujuan/penolakan pemberian tindakan kedokteran berupa ${r.tindakan} untuk diagnosa ${r.diagnosa}. Penerima informasi telah dijelaskan mengenai tujuan tindakan, risiko, komplikasi, prognosis, tata cara, alternatif tindakan, serta konsekuensi jika tindakan ini ditolak.`,
                        meta: {
                            diagnosa: r.diagnosa,
                            tindakan: r.tindakan,
                            indikasi_tindakan: r.indikasi_tindakan,
                            tata_cara: r.tata_cara,
                            tujuan: r.tujuan,
                            risiko: r.risiko,
                            komplikasi: r.komplikasi,
                            prognosis: r.prognosis,
                            alternatif_dan_risikonya: r.alternatif_dan_risikonya,
                            biaya: r.biaya,
                            penerima_informasi: r.penerima_informasi,
                            alasan_diwakilkan_penerima_informasi: r.alasan_diwakilkan_penerima_informasi,
                            jk_penerima_informasi: r.jk_penerima_informasi,
                            tanggal_lahir_penerima_informasi: r.tanggal_lahir_penerima_informasi,
                            umur_penerima_informasi: r.umur_penerima_informasi,
                            alamat_penerima_informasi: r.alamat_penerima_informasi,
                            no_hp: r.no_hp,
                            hubungan_penerima_informasi: r.hubungan_penerima_informasi,
                            pernyataan: r.pernyataan,
                            saksi_keluarga: r.saksi_keluarga,
                            dokter: r.nm_dokter,
                            poli: r.nm_poli
                        }
                    };
                });

                return res.json({
                    success: true,
                    consents: [...generalConsents, ...informedConsents]
                });
            } catch (err) {
                console.error('get_consents error:', err);
                return res.status(500).json({ success: false, message: 'Database Error: ' + err.message });
            }
        }

        if (action === 'save_consent') {
            const { id, type, photo, choices } = params;
            if (!id || !type || !photo) {
                return res.status(400).json({ success: false, message: 'Parameter id, type, dan photo wajib diisi.' });
            }

            try {
                // Decode base64 image
                const base64Data = photo.replace(/^data:image\/\w+;base64,/, "");
                const buffer = Buffer.from(base64Data, 'base64');

                if (type === 'Umum') {
                    // 1. Save locally (if directories exist/can be created)
                    try {
                        const uploadDir = path.join(__dirname, '..', '..', 'webapps', 'persetujuanumum', 'pages', 'upload');
                        if (!fs.existsSync(uploadDir)) {
                            fs.mkdirSync(uploadDir, { recursive: true });
                        }
                        const fileName = `${id}.jpeg`;
                        const filePath = path.join(uploadDir, fileName);
                        fs.writeFileSync(filePath, buffer);
                    } catch (localErr) {
                        console.warn('Warning: Could not save General Consent file locally:', localErr.message);
                    }

                    // 2. Forward to the main server PHP endpoint dynamically using config.webapps_host
                    const remoteUrl = getRemoteUrl('/webapps/persetujuanumum/pages/storeImage.php');
                    console.log(`Forwarding Persetujuan Umum to main server: ${remoteUrl}`);
                    try {
                        const formData = new URLSearchParams();
                        formData.append('nosurat', id);
                        formData.append('pengobatan_kepada', choices?.pengobatan_kepada || '-');
                        formData.append('nilai_kepercayaan', choices?.nilai_kepercayaan || '');
                        formData.append('image', photo);

                        const remoteResponse = await fetch(remoteUrl, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            body: formData.toString()
                        });
                        const remoteText = await remoteResponse.text();
                        console.log(`Main server storeImage.php response (General Consent):`, remoteText);
                    } catch (remoteErr) {
                        console.error('Error forwarding General Consent to main server:', remoteErr.message);
                    }

                    // 3. Update database directly from Node as redundancy
                    const dbPhotoPath = `pages/upload/${id}.jpeg`;
                    console.log('Inserting into DB. id:', id, 'dbPhotoPath:', dbPhotoPath);
                    await pool.query(`
                        INSERT INTO surat_persetujuan_umum_pembuat_pernyataan (no_surat, photo)
                        VALUES (?, ?)
                        ON DUPLICATE KEY UPDATE photo = ?
                    `, [id, dbPhotoPath, dbPhotoPath]);
                    console.log('DB Insert Success for Persetujuan Umum!');

                    const pengobatan_kepada = choices?.pengobatan_kepada || '-';
                    const nilai_kepercayaan = choices?.nilai_kepercayaan || '';
                    await pool.query(`
                        UPDATE surat_persetujuan_umum 
                        SET pengobatan_kepada = ?, nilai_kepercayaan = ?
                        WHERE no_surat = ?
                    `, [pengobatan_kepada, nilai_kepercayaan, id]);

                    return res.json({ success: true, message: 'Persetujuan Umum berhasil ditandatangani.' });

                } else if (type === 'Tindakan') {
                    // 1. Save locally (if directories exist/can be created)
                    try {
                        const uploadDir = path.join(__dirname, '..', '..', 'webapps', 'persetujuantindakan', 'pages', 'upload');
                        if (!fs.existsSync(uploadDir)) {
                            fs.mkdirSync(uploadDir, { recursive: true });
                        }
                        const fileName = `${id}PP.jpeg`;
                        const filePath = path.join(uploadDir, fileName);
                        fs.writeFileSync(filePath, buffer);
                    } catch (localErr) {
                        console.warn('Warning: Could not save Informed Consent file locally:', localErr.message);
                    }

                    // 2. Forward to the main server PHP endpoint dynamically using config.webapps_host
                    const remoteUrl = getRemoteUrl('/webapps/persetujuantindakan/pages/storeImage.php');
                    console.log(`Forwarding Persetujuan Tindakan to main server: ${remoteUrl}`);
                    try {
                        const formData = new URLSearchParams();
                        formData.append('nopernyataan', id);
                        formData.append('pilihansetuju', choices?.pernyataan || 'Persetujuan');
                        formData.append('image', photo);
                        formData.append('diagnosa_konfirmasi', 'true');
                        formData.append('tindakan_konfirmasi', 'true');
                        formData.append('indikasi_tindakan_konfirmasi', 'true');
                        formData.append('tata_cara_konfirmasi', 'true');
                        formData.append('tujuan_konfirmasi', 'true');
                        formData.append('risiko_konfirmasi', 'true');
                        formData.append('komplikasi_konfirmasi', 'true');
                        formData.append('prognosis_konfirmasi', 'true');
                        formData.append('alternatif_konfirmasi', 'true');
                        formData.append('biaya_konfirmasi', 'true');
                        formData.append('lain_lain_konfirmasi', 'true');

                        const remoteResponse = await fetch(remoteUrl, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            body: formData.toString()
                        });
                        const remoteText = await remoteResponse.text();
                        console.log(`Main server storeImage.php response (Informed Consent):`, remoteText);
                    } catch (remoteErr) {
                        console.error('Error forwarding Informed Consent to main server:', remoteErr.message);
                    }

                    // 3. Update database directly from Node as redundancy
                    const dbPhotoPath = `pages/upload/${id}PP.jpeg`;
                    console.log('Inserting into DB. id:', id, 'dbPhotoPath:', dbPhotoPath);
                    await pool.query(`
                        INSERT INTO bukti_persetujuan_penolakan_tindakan_penerimainformasi (no_pernyataan, photo)
                        VALUES (?, ?)
                        ON DUPLICATE KEY UPDATE photo = ?
                    `, [id, dbPhotoPath, dbPhotoPath]);
                    console.log('DB Insert Success for Persetujuan Tindakan!');

                    const pernyataan = choices?.pernyataan || 'Persetujuan';
                    await pool.query(`
                        UPDATE persetujuan_penolakan_tindakan 
                        SET pernyataan = ?, 
                            diagnosa_konfirmasi = 'true', 
                            tindakan_konfirmasi = 'true', 
                            indikasi_tindakan_konfirmasi = 'true', 
                            tata_cara_konfirmasi = 'true', 
                            tujuan_konfirmasi = 'true', 
                            risiko_konfirmasi = 'true', 
                            komplikasi_konfirmasi = 'true', 
                            prognosis_konfirmasi = 'true', 
                            alternatif_konfirmasi = 'true', 
                            biaya_konfirmasi = 'true', 
                            lain_lain_konfirmasi = 'true'
                        WHERE no_pernyataan = ?
                    `, [pernyataan, id]);

                    return res.json({ success: true, message: 'Persetujuan Tindakan Medis berhasil ditandatangani.' });
                } else {
                    return res.status(400).json({ success: false, message: 'Tipe dokumen tidak valid.' });
                }
            } catch (err) {
                console.error('save_consent error:', err);
                return res.status(500).json({ success: false, message: 'Gagal menyimpan persetujuan: ' + err.message });
            }
        }

        return res.json({ success: false, message: 'Aksi API tidak valid.' });

    } catch (err) {
        console.error(`Error executing API action "${action}":`, err);
        return res.status(500).json({ success: false, message: 'Internal Server Error: ' + err.message });
    }
});

app.use('/images', express.static(path.join(__dirname, '..', 'images')));

app.use('/webapps', async (req, res, next) => {
    const localPath = path.join(__dirname, '..', '..', 'webapps', req.path);
    if (fs.existsSync(localPath) && fs.statSync(localPath).isFile()) {
        return res.sendFile(localPath);
    }
    
    // Fallback: Fetch from the main server (config.webapps_host)
    const remoteUrl = getRemoteUrl(`/webapps${req.path}`);
    console.log(`Local file not found: ${req.path}. Fetching from main server: ${remoteUrl}`);
    try {
        const fileResponse = await fetch(remoteUrl);
        if (fileResponse.ok) {
            const contentType = fileResponse.headers.get('content-type');
            if (contentType) {
                res.setHeader('Content-Type', contentType);
            }
            const arrayBuffer = await fileResponse.arrayBuffer();
            return res.send(Buffer.from(arrayBuffer));
        } else {
            console.warn(`File not found on main server: ${remoteUrl}. Status: ${fileResponse.status}. Trying live server fallback.`);
        }
    } catch (err) {
        console.error(`Error fetching file from main server (${remoteUrl}):`, err.message);
    }
    
    // Secondary fallback: Redirect to live server
    const fallbackUrl = `https://rsmardhatillah.com/webapps${req.path}`;
    console.log(`Fallback redirect to: ${fallbackUrl}`);
    return res.redirect(fallbackUrl);
});

// Serve compiled React bundle in production (fallback)
app.use(express.static(path.join(__dirname, 'dist')));
app.get(/.*/, (req, res) => {
    res.sendFile(path.join(__dirname, 'dist', 'index.html'));
});

app.listen(PORT, () => {
    console.log(`Node.js API Dev Server listening on port ${PORT}`);
});