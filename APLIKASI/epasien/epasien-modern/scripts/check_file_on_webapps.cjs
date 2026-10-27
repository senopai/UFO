async function main() {
    const url = 'http://192.168.2.69/webapps/persetujuanumum/pages/upload/PSU20260618001.jpeg';
    console.log('Fetching local image:', url);
    try {
        const res = await fetch(url, { method: 'HEAD' });
        console.log('Status code:', res.status);
        console.log('Content-Type:', res.headers.get('content-type'));
        console.log('Content-Length:', res.headers.get('content-length'));
    } catch (err) {
        console.error('Fetch failed:', err.message);
    }
}
main();
