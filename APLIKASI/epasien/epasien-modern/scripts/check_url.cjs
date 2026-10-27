async function main() {
    const url = 'http://192.168.2.69/webapps/persetujuantindakan/pages/storeImage.php';
    console.log('Fetching local persetujuantindakan storeImage:', url);
    try {
        const res = await fetch(url, { method: 'GET' });
        console.log('Status code:', res.status);
        console.log('Content-Type:', res.headers.get('content-type'));
        const text = await res.text();
        console.log('Response body snippet:', text.substring(0, 200));
    } catch (err) {
        console.error('Fetch failed:', err.message);
    }
}
main();
