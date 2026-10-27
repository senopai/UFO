const mysql = require('mysql2/promise');
const fs = require('fs');
const path = require('path');

function getDbConfig() {
    const configPath = path.join(__dirname, 'db_config.php');
    const content = fs.readFileSync(configPath, 'utf8');
    const hostMatch = content.match(/\$db_hostname\s*=\s*['"]([^'"]+)['"]/);
    const userMatch = content.match(/\$db_username\s*=\s*['"]([^'"]+)['"]/);
    const passMatch = content.match(/\$db_password\s*=\s*['"]([^'"]*)['"]/);
    const nameMatch = content.match(/\$db_name\s*=\s*['"]([^'"]+)['"]/);

    return {
        host: hostMatch ? hostMatch[1] : 'localhost',
        user: userMatch ? userMatch[1] : 'root',
        password: passMatch ? passMatch[1] : '',
        database: nameMatch ? nameMatch[1] : 'sik'
    };
}

const tableChecks = {
    personal_pasien: ['no_rkm_medis', 'password', 'gambar'],
    pasien: ['no_rkm_medis', 'nm_pasien', 'email', 'jk', 'no_tlp', 'tmp_lahir', 'tgl_lahir', 'alamat', 'umur', 'namakeluarga', 'alamatpj', 'keluarga', 'tgl_daftar'],
    reg_periksa: ['no_reg', 'no_rawat', 'tgl_registrasi', 'jam_reg', 'kd_dokter', 'no_rkm_medis', 'kd_poli', 'p_jawab', 'almt_pj', 'hubunganpj', 'biaya_reg', 'stts', 'stts_daftar', 'status_lanjut', 'kd_pj', 'umurdaftar', 'sttsumur', 'status_bayar', 'status_poli'],
    dokter: ['kd_dokter', 'nm_dokter'],
    poliklinik: ['kd_poli', 'nm_poli', 'registrasi', 'registrasilama', 'status'],
    jadwal: ['kd_dokter', 'kd_poli', 'jam_mulai', 'jam_selesai', 'kuota', 'hari_kerja'],
    setting: ['nama_instansi'],
    set_validasi_registrasi: ['wajib_closing_kasir'],
    skdp_bpjs: ['no_rkm_medis', 'tanggal_datang', 'status'],
    diagnosa_pasien: ['no_rawat', 'kd_penyakit'],
    penyakit: ['kd_penyakit', 'nm_penyakit'],
    pemeriksaan_ralan: ['no_rawat', 'suhu_tubuh', 'tensi', 'berat'],
    pemeriksaan_ranap: ['no_rawat', 'suhu_tubuh', 'tensi', 'berat'],
    detail_pemberian_obat: ['no_rawat', 'kode_brng', 'jml'],
    databarang: ['kode_brng', 'nama_brng'],
    periksa_lab: ['no_rawat', 'kd_dokter'],
    detail_periksa_lab: ['no_rawat', 'nilai', 'nilai_rujukan', 'keterangan', 'id_template'],
    template_laboratorium: ['id_template', 'Pemeriksaan', 'satuan'],
    periksa_radiologi: ['no_rawat', 'tgl_periksa', 'kd_dokter'],
    hasil_radiologi: ['no_rawat', 'hasil']
};

async function run() {
    console.log('--- STARTING COMPREHENSIVE SCHEMA VERIFICATION ---');
    const config = getDbConfig();
    const connection = await mysql.createConnection({
        host: config.host,
        user: config.user,
        password: config.password,
        database: config.database
    });

    let overallSuccess = true;

    for (const [table, columns] of Object.entries(tableChecks)) {
        try {
            // Check if table exists and describe columns
            const [descRows] = await connection.query(`DESCRIBE \`${table}\``);
            const actualColumns = descRows.map(r => r.Field.toLowerCase());
            
            console.log(`\nTable [${table}]: OK (Found)`);
            
            columns.forEach(col => {
                const colLower = col.toLowerCase();
                if (actualColumns.includes(colLower)) {
                    console.log(`  - Column [${col}]: OK`);
                } else {
                    console.error(`  - Column [${col}]: ERROR - NOT FOUND!`);
                    overallSuccess = false;
                }
            });
        } catch (err) {
            console.error(`\nTable [${table}]: ERROR - Table does not exist or description failed! Details: ${err.message}`);
            overallSuccess = false;
        }
    }

    console.log('\n--- VERIFICATION RESULT ---');
    if (overallSuccess) {
        console.log('SUCCESS: All tables and referenced columns exist in the database!');
    } else {
        console.error('FAILURE: Some tables or columns do not exist in the database!');
    }

    await connection.end();
}

run();
