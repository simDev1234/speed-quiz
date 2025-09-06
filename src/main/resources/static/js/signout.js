//const csrfHeader = $('meta[name="_csrf_header"]').attr('content');
//const csrfToken = $('meta[name="_csrf"]').attr('content');
//const NGROK_URL = 'https://1dd58f2c7c5c.ngrok-free.app';
//const NGROK_URL = 'http://localhost:10004';

async function logout() {

    fetch(`${NGROK_URL}/api/v1/users/logout`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            [csrfHeader]: csrfToken,
            "ngrok-skip-browser-warning": "true"
        },
        credentials: 'include'
    })
        .then(response => {
            if (response.ok) {
                window.location.href = '/login';
            } else {
                return response.json().then(err => {
                    showAlert(err?.exception?.message);
                });
            }
        })
        .catch(error => {
            console.log('Logout error:', error);
            alert('로그아웃 중 오류가 발생했습니다.');
        });
}