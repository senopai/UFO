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

async function run() {
    const config = getDbConfig();
    const connection = await mysql.createConnection({
        host: config.host,
        user: config.user,
        password: config.password,
        database: config.database
    });

    try {
        console.log('\n--- REGISTRATIONS FOR RM: 180059 (ARDIYANTO) ---');
        const [regs] = await connection.query(
            "select r.no_rawat, r.no_reg, r.tgl_registrasi, r.jam_reg, r.kd_dokter, r.kd_poli, r.stts, p.no_tlp from reg_periksa r inner join pasien p on r.no_rkm_medis=p.no_rkm_medis where r.no_rkm_medis = '180059' order by r.tgl_registrasi desc, r.jam_reg desc limit 5"
        );
        console.log(regs);
    } catch (err) {
        console.error('Error running inspect:', err.message);
    } finally {
        await connection.end();
    }
}

run();
