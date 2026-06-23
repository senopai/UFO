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
    console.log('Searching database for IP 192.168.2.69...');
    
    try {
        const connection = await mysql.createConnection({
            host: config.host,
            user: config.user,
            password: config.password,
            database: config.database
        });
        
        // Get all tables
        const [tables] = await connection.query("SHOW TABLES");
        const tableNames = tables.map(r => Object.values(r)[0]);
        
        let found = false;
        for (const tableName of tableNames) {
            // Get columns of table
            const [columns] = await connection.query(`DESCRIBE \`${tableName}\``);
            const textColumns = columns
                .filter(col => col.Type.includes('char') || col.Type.includes('text'))
                .map(col => col.Field);
                
            if (textColumns.length === 0) continue;
            
            // Build search query
            const conditions = textColumns.map(col => `\`${col}\` LIKE '%192.168.2.69%'`).join(' OR ');
            const query = `SELECT * FROM \`${tableName}\` WHERE ${conditions} LIMIT 5`;
            
            try {
                const [rows] = await connection.query(query);
                if (rows.length > 0) {
                    console.log(`Found match in table [${tableName}]:`, rows);
                    found = true;
                }
            } catch (err) {
                // Ignore query errors for specific formats
            }
        }
        
        if (!found) {
            console.log('No matches found for IP 192.168.2.69 in database tables.');
        }
        
        await connection.end();
    } catch (err) {
        console.error('Database query failed:', err);
    }
}

main();
