async function main() {
    const url = 'http://localhost:5000/api.php';
    console.log('Testing POST to local server:', url);
    
    // Simulate frontend API call payload
    const payload = {
        action: 'save_consent',
        params: {
            id: 'PSU20260618001',
            type: 'Umum',
            photo: 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP//////////////////////////////////////////////////////////////////////////////////////wgALCAABAAEBAREA/8QAFBABAAAAAAAAAAAAAAAAAAAAAP/aAAgBAQABPxA=',
            choices: {
                pengobatan_kepada: 'Suami',
                nilai_kepercayaan: 'setuju'
            }
        }
    };

    try {
        const res = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });
        console.log('Status code:', res.status);
        const json = await res.json();
        console.log('Response body:', json);
    } catch (err) {
        console.error('POST failed:', err.message);
    }
}
main();
