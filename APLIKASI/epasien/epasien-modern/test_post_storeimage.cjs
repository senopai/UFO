async function main() {
    const url = 'http://192.168.2.69/webapps/persetujuanumum/pages/storeImage.php';
    console.log('Testing POST to local server:', url);
    
    const formData = new URLSearchParams();
    formData.append('nosurat', 'PSU20260618001');
    formData.append('pengobatan_kepada', 'Suami');
    formData.append('nilai_kepercayaan', 'setuju');
    formData.append('image', 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP//////////////////////////////////////////////////////////////////////////////////////wgALCAABAAEBAREA/8QAFBABAAAAAAAAAAAAAAAAAAAAAP/aAAgBAQABPxA=');

    try {
        const res = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: formData.toString()
        });
        console.log('Status code:', res.status);
        console.log('Content-Type:', res.headers.get('content-type'));
        const text = await res.text();
        console.log('Response body snippet:', text.substring(0, 300));
    } catch (err) {
        console.error('POST failed:', err.message);
    }
}
main();
