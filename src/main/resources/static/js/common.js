// =====================
// 공통 CSRF & NGROK URL
// =====================
const csrfHeader = $('meta[name="_csrf_header"]').attr('content');
const csrfToken = $('meta[name="_csrf"]').attr('content');
const NGROK_URL = 'https://00141d1c8415.ngrok-free.app';

// =====================
// 1. 페이지 로드 시 공통 알럿 자동 생성
// =====================
(function createAlertBox() {
    if (document.getElementById('commonAlert')) return;

    const container = document.createElement('div');
    container.innerHTML = `
    <div id="commonAlert" style='position: fixed; top: 20px; left: 50%; transform: translateX(-50%); display: none; align-items: center; color: white; max-width: 384px; width: 100%; background-color: #f87171; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1); border-radius: 8px; overflow: hidden; z-index: 50;'>
        <div style='width: 40px; border-right: 1px solid rgba(255, 255, 255, 0.3); padding: 0 8px; display: flex; align-items: center; justify-content: center; height: 100%;'>
            <svg style="width: 16px; height: 16px;" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636"></path>
            </svg>
        </div>
        <div style='display: flex; align-items: center; padding: 8px;'>
            <div style='margin: 0 12px;'>
                <p id="commonAlertMessage" style='margin: 0; font-size: 14px;'>Your message</p>
            </div>
        </div>
    </div>
    `;
    document.body.append(container);
})();

// =====================
// 2. 공통 알럿 표시 함수
// =====================
function showAlert(message, duration = 3000) {
    const alertBox = document.getElementById('commonAlert');
    const alertMessage = document.getElementById('commonAlertMessage');

    alertMessage.textContent = message;
    alertBox.style.display = 'flex'; // flex로 표시

    clearTimeout(alertBox.timeout);
    alertBox.timeout = setTimeout(() => {
        alertBox.style.display = 'none'; // 숨기기
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
            credentials: 'include',
        });

        const contentType = response.headers.get('content-type');
        let responseData = null;

        if (contentType?.includes('application/json')) {
            responseData = await response.json();
            return responseData;
        } else {
            showAlert('서버에서 JSON 응답을 받지 못했습니다.');
            return {
                httpStatus : {
                    value : response.status
                },
                success: false,
                data: null,
                exception: {
                    code: 'NETWORK_ERROR_JSON_PARSING_ERROR',
                    message: '서버에서 JSON 응답을 받지 못했습니다.'
                }
            };
        }

        // HTTP 에러 처리
        if (!response.ok) {
            showAlert(responseData?.message || '요청 실패');
        }

        return responseData;

    } catch (error) {
        let message = '알 수 없는 오류가 발생했습니다.';
        if (error.name === 'TypeError' && error.message.includes('fetch')) {
            message = '네트워크 연결을 확인해주세요.';
        } else if (!navigator.onLine) {
            message = '인터넷 연결을 확인해주세요.';
        } else {
            message = error.message;
        }

        showAlert(message);

        return { status: 0, data: null };
    }
}

