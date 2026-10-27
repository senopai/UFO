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
        const [rows1] = await connection.query("SHOW TABLES LIKE '%whatsapp%'");
        const [rows2] = await connection.query("SHOW TABLES LIKE '%template_wa%'");
        const [rows3] = await connection.query("SHOW TABLES LIKE '%fonnte%'");
        const [rows4] = await connection.query("SHOW TABLES LIKE '%api_wa%'");
        console.log('--- SHOW TABLES ---');
        console.log('whatsapp:', rows1.map(r => Object.values(r)[0]));
        console.log('template_wa:', rows2.map(r => Object.values(r)[0]));
        console.log('fonnte:', rows3.map(r => Object.values(r)[0]));
        console.log('api_wa:', rows4.map(r => Object.values(r)[0]));
    } catch (err) {
        console.error('Error showing tables:', err.message);
    } finally {
        await connection.end();
    }
}

run();
