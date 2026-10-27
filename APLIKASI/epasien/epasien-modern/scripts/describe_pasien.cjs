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
        console.log('\n--- DESCRIBE PASIEN ---');
        const [rows] = await connection.query("DESCRIBE pasien");
        console.log(rows.map(r => r.Field).join(', '));
        
        console.log('\n--- SAMPLE PASIEN DATA ---');
        const [sample] = await connection.query("select no_rkm_medis, nm_pasien, no_ktp, no_tlp from pasien limit 3");
        console.log(sample);
    } catch (err) {
        console.error('Error running describe:', err.message);
    } finally {
        await connection.end();
    }
}

run();
