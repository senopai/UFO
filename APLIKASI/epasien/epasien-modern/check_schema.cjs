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
    try {
        const connection = await mysql.createConnection(config);
        const [rows] = await connection.query('DESCRIBE surat_persetujuan_umum_pembuat_pernyataan');
        console.log('Columns in surat_persetujuan_umum_pembuat_pernyataan:');
        console.log(rows);

        const [rows2] = await connection.query('DESCRIBE bukti_persetujuan_penolakan_tindakan_penerimainformasi');
        console.log('Columns in bukti_persetujuan_penolakan_tindakan_penerimainformasi:');
        console.log(rows2);

        await connection.end();
    } catch (err) {
        console.error(err);
    }
}
main();
