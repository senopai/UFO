const crypto = require('crypto');
const fs = require('fs');
const path = require('path');

function decryptAES128(encrypted) {
    if (!encrypted) return '';
    const key = Buffer.from('Bar12345Bar12345', 'utf8');
    const iv = Buffer.from('sayangsamakhanza', 'utf8');
    try {
        const decipher = crypto.createDecipheriv('aes-128-cbc', key, iv);
        let decrypted = decipher.update(encrypted, 'base64', 'utf8');
        decrypted += decipher.final('utf8');
        return decrypted;
    } catch (err) {
        return `[Decryption Error: ${err.message}]`;
    }
}

// Read database.xml
const xmlPath = path.join(__dirname, '..', '..', 'setting', 'database.xml');
if (!fs.existsSync(xmlPath)) {
    console.error('database.xml not found at:', xmlPath);
    process.exit(1);
}

const content = fs.readFileSync(xmlPath, 'utf8');

const regex = /<entry key="([^"]+)">([^<]+)<\/entry>/g;
let match;
console.log('--- Decrypted database.xml entries ---');
while ((match = regex.exec(content)) !== null) {
    const key = match[1];
    const val = match[2];
    // Decrypt if it looks like base64 encrypted string
    if (val.length > 10 && val.endsWith('==')) {
        console.log(`${key}: ${decryptAES128(val)}`);
    } else {
        console.log(`${key}: ${val}`);
    }
}
