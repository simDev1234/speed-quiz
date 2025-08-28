// =====================
// 공통 CSRF & NGROK URL
// =====================
const csrfHeader = $('meta[name="_csrf_header"]').attr('content');
const csrfToken = $('meta[name="_csrf"]').attr('content');
const NGROK_URL = 'https://1dd58f2c7c5c.ngrok-free.app';

// =====================
// 1. 페이지 로드 시 공통 알럿 자동 생성
// =====================
(function createAlertBox() {
    if (document.getElementById('commonAlert')) return;

    const container = document.createElement('div');
    container.innerHTML = `
    <div id="commonAlert" class='hidden fixed top-5 left-1/2 transform -translate-x-1/2 flex items-center text-white max-w-sm w-full bg-red-400 shadow-md rounded-lg overflow-hidden z-50'>
        <div class='w-10 border-r px-2'>
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                 xmlns="http://www.w3.org/2000/svg">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636"></path>
            </svg>
        </div>
        <div class='flex items-center px-2 py-3'>
            <div class='mx-3'>
                <p id="commonAlertMessage">Your message</p>
            </div>
        </div>
    </div>
    `;
    document.body.prepend(container);
})();

// =====================
// 2. 공통 알럿 표시 함수
// =====================
function showAlert(message, duration = 3000) {
    const alertBox = document.getElementById('commonAlert');
    const alertMessage = document.getElementById('commonAlertMessage');

    alertMessage.textContent = message;
    alertBox.classList.remove('hidden');

    clearTimeout(alertBox.timeout);
    alertBox.timeout = setTimeout(() => {
        alertBox.classList.add('hidden');
    }, duration);
}

// =====================
// 3. 공통 fetch POST 함수
// =====================
async function sendPost(url, data = {}, options = {}) {
    const headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        ...(csrfHeader && csrfToken ? { [csrfHeader]: csrfToken } : {}),
        ...options.headers,
        "ngrok-skip-browser-warning": "true"
    };

    try {
        const response = await fetch(url, {
            method: options.method || 'POST',
            headers,
            body: JSON.stringify(data),
            credentials: options.credentials || 'same-origin',
        });

        const contentType = response.headers.get('content-type');
        if (!contentType?.includes('application/json')) {
            showAlert('서버에서 JSON 응답을 받지 못했습니다.');
            return null;
        }

        const responseData = await response.json();
        console.log(responseData);
        if (!response.ok) {
            showAlert(responseData?.exception?.message || '요청 실패');
            return null;
        }

        return responseData;

    } catch (error) {
        console.error('Request failed:', error);

        let message = '알 수 없는 오류가 발생했습니다.';
        if (error.name === 'TypeError' && error.message.includes('fetch')) {
            message = '네트워크 연결을 확인해주세요.';
        } else if (!navigator.onLine) {
            message = '인터넷 연결을 확인해주세요.';
        } else {
            message = error.message;
        }

        showAlert(message);
        return null;
    }
}
