const fs = require('fs');
const path = require('path');

const fileToFind = 'ATHAFARIZ_RADEYA_FADIL3_TH_BRONKITHIS.jpg';
const searchRoot = 'e:\\APLIKASI\\APLIKASI';

function findFile(dir, fileName) {
    let results = [];
    try {
        const list = fs.readdirSync(dir);
        list.forEach(file => {
            const filePath = path.join(dir, file);
            const stat = fs.statSync(filePath);
            if (stat && stat.isDirectory()) {
                results = results.concat(findFile(filePath, fileName));
            } else if (file.toLowerCase() === fileName.toLowerCase()) {
                results.push(filePath);
            }
        });
    } catch (err) {
        // ignore errors
    }
    return results;
}

console.log(`Searching for "${fileToFind}" under "${searchRoot}"...`);
const paths = findFile(searchRoot, fileToFind);
console.log('Found paths:', paths);
