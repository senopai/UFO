const mysql = require('mysql2/promise');
const fs = require('fs');

async function main() {
    const content = fs.readFileSync('db_config.php', 'utf8');
    const hostMatch = content.match(/\$db_hostname\s*=\s*['"]([^'"]+)['"]/);
    const userMatch = content.match(/\$db_username\s*=\s*['"]([^'"]+)['"]/);
    const passMatch = content.match(/\$db_password\s*=\s*['"]([^'"]*)['"]/);
    const nameMatch = content.match(/\$db_name\s*=\s*['"]([^'"]+)['"]/);

    const pool = mysql.createPool({
        host: hostMatch[1],
        user: userMatch[1],
        password: passMatch ? passMatch[1] : '',
        database: nameMatch[1]
    });

    const norm = '180059'; // Test user

    try {
        // Test 1: Lab results
        const [lab] = await pool.query(`select tgl_periksa, jam_periksa, 'Laboratorium' as jenis from periksa_lab inner join reg_periksa on periksa_lab.no_rawat=reg_periksa.no_rawat where reg_periksa.no_rkm_medis=? order by tgl_periksa desc, jam_periksa desc limit 2`, [norm]);
        console.log('Lab:', lab);

        // Test 2: Booking (Upcoming)
        const [booking] = await pool.query(`select tgl_registrasi, jam_reg, dokter.nm_dokter, poliklinik.nm_poli from reg_periksa inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli where reg_periksa.no_rkm_medis=? and reg_periksa.tgl_registrasi >= CURDATE() and reg_periksa.stts='Belum' order by tgl_registrasi asc limit 2`, [norm]);
        console.log('Booking:', booking);

        // Test 3: Batal (Canceled)
        const [batal] = await pool.query(`select tgl_registrasi, jam_reg, dokter.nm_dokter, poliklinik.nm_poli from reg_periksa inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli where reg_periksa.no_rkm_medis=? and reg_periksa.stts='Batal' order by tgl_registrasi desc limit 2`, [norm]);
        console.log('Batal:', batal);

    } catch (e) {
        console.error(e);
    }
    process.exit(0);
}
main();
