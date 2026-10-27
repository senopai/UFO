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

async function main() {
    const config = getDbConfig();
    console.log('Connecting to:', config.host, 'db:', config.database);
    
    try {
        const connection = await mysql.createConnection({
            host: config.host,
            user: config.user,
            password: config.password,
            database: config.database
        });
        
        console.log('Resetting test consent records...');
        
        // SPU Reset
        await connection.query("DELETE FROM surat_persetujuan_umum_pembuat_pernyataan WHERE no_surat = 'PSU20260618001'");
        await connection.query("UPDATE surat_persetujuan_umum SET pengobatan_kepada = '-', nilai_kepercayaan = '' WHERE no_surat = 'PSU20260618001'");
        
        // PPT Reset
        await connection.query("DELETE FROM bukti_persetujuan_penolakan_tindakan_penerimainformasi WHERE no_pernyataan = 'PM20260618001'");
        await connection.query(`
            UPDATE persetujuan_penolakan_tindakan 
            SET pernyataan = 'Belum Dikonfirmasi',
                diagnosa_konfirmasi = 'false', 
                tindakan_konfirmasi = 'false', 
                indikasi_tindakan_konfirmasi = 'false', 
                tata_cara_konfirmasi = 'false', 
                tujuan_konfirmasi = 'false', 
                risiko_konfirmasi = 'false', 
                komplikasi_konfirmasi = 'false', 
                prognosis_konfirmasi = 'false', 
                alternatif_konfirmasi = 'false', 
                biaya_konfirmasi = 'false', 
                lain_lain_konfirmasi = 'false'
            WHERE no_pernyataan = 'PM20260618001'
        `);
        
        console.log('Test records successfully reset!');
        await connection.end();
    } catch (err) {
        console.error('Reset failed:', err);
    }
}

main();
