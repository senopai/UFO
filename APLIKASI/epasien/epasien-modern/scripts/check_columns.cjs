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

    try {
        const [lab] = await pool.query('describe periksa_lab');
        console.log('LAB:', lab.map(l => l.Field));
        
        const [rad] = await pool.query('describe periksa_radiologi');
        console.log('RAD:', rad.map(r => r.Field));
    } catch(e) {
        console.error(e);
    }

    process.exit(0);
}
main();
