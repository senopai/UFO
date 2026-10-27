const https = require('https');

const agent = new https.Agent({  
  rejectUnauthorized: false
});

const urls = [
    'https://192.168.5.100/webapps/radiologi/pages/upload/sanksiRME.jpg',
    'https://192.168.5.100/webapps/radiologi/pages/upload/TAKHUDIN._49TH._TB.jpg',
    'https://192.168.5.100/webapps/radiologi/pages/upload/TAKHUDIN._49TH._TB.JPG'
];

async function checkUrl(url) {
    return new Promise((resolve) => {
        https.get(url, { agent }, (res) => {
            resolve({ url, statusCode: res.statusCode });
        }).on('error', (err) => {
            resolve({ url, error: err.message });
        });
    });
}

async function run() {
    console.log('Testing HTTPS URLs on remote server...');
    for (const url of urls) {
        const res = await checkUrl(url);
        console.log(JSON.stringify(res));
    }
}

run();
