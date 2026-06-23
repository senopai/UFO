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
        
        console.log('\n--- General Consents (surat_persetujuan_umum) ---');
        const [spu] = await connection.query(`
            SELECT s.no_surat, s.no_rawat, s.tanggal, s.pengobatan_kepada, s.nilai_kepercayaan, pp.photo 
            FROM surat_persetujuan_umum s
            LEFT JOIN surat_persetujuan_umum_pembuat_pernyataan pp ON s.no_surat = pp.no_surat
            ORDER BY s.tanggal DESC LIMIT 5
        `);
        console.log(spu);

        console.log('\n--- Informed Consents (persetujuan_penolakan_tindakan) ---');
        const [ppt] = await connection.query(`
            SELECT p.no_pernyataan, p.no_rawat, p.tanggal, p.tindakan, p.pernyataan, b.photo 
            FROM persetujuan_penolakan_tindakan p
            LEFT JOIN bukti_persetujuan_penolakan_tindakan_penerimainformasi b ON p.no_pernyataan = b.no_pernyataan
            ORDER BY p.tanggal DESC LIMIT 5
        `);
        console.log(ppt);

        await connection.end();
    } catch (err) {
        console.error('Database query failed:', err);
    }
}

main();
