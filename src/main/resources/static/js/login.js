async function handleLoginSubmit(event) {
    event.preventDefault();

    const form = event.target;
    const loginEmail = form.loginEmail.value;
    const loginPassword = form.loginPassword.value;
    const isLoginProlonging = document.getElementById('loginProlonging').checked;

    if (!loginEmail || !loginPassword) {
        showAlert('이메일과 비밀번호를 입력해주세요.');
        return;
    }

    const requestData = {
        loginEmail,
        loginPassword,
        rememberMe: isLoginProlonging
    };

    try {
        const responseData = await sendPost(`${NGROK_URL}/api/v1/users/login`, requestData);

        if (responseData?.success) {
            // 로그인 성공 시 페이지 리디렉션 전에 잠시 대기
            setTimeout(() => {
                window.location.href = '/';
            }, 100);
        } else if (responseData?.httpStatus?.value === 401
            || responseData?.httpStatus?.value === 403
            || responseData?.httpStatus?.value === 404
        ) {
            showAlert(responseData?.exception?.message || '비밀번호를 확인해주세요');
            openPasswordResetModal();
        } else {
            showAlert(responseData?.exception?.message || '로그인에 실패했습니다.');
        }
    } catch (error) {
        console.error('Login error:', error);
        showAlert('네트워크 오류가 발생했습니다.');
    }
}

// 비밀번호 찾기 관련 기능

let resetStep = 1;
let resetEmail = '';

// 비밀번호 찾기 모달 열기
function openPasswordResetModal() {
    document.getElementById('passwordResetModal').style.display = 'block';
    resetPasswordModal();
}

// 비밀번호 찾기 모달 닫기
function closePasswordResetModal() {
    document.getElementById('passwordResetModal').style.display = 'none';
    resetPasswordModal();
}

// 모달 초기화
function resetPasswordModal() {
    resetStep = 1;
    showResetStep(1);
    document.getElementById('resetEmail').value = '';
    document.getElementById('resetCodeInput').value = '';
    document.getElementById('newPassword').value = '';
    document.getElementById('confirmNewPassword').value = '';
    clearResetErrors();
}

// 오류 메시지 초기화
function clearResetErrors() {
    document.getElementById('resetEmailError').textContent = '';
    document.getElementById('resetCodeError').textContent = '';
    document.getElementById('newPasswordError').textContent = '';
}

// 단계 표시
function showResetStep(step) {
    document.querySelectorAll('.modal-step').forEach(el => el.classList.remove('active'));
    document.getElementById(`resetStep${step}`).classList.add('active');
    resetStep = step;
}

// 1단계: 이메일로 인증 코드 발송
async function sendPasswordResetEmail(event) {
    event.preventDefault();
    const email = document.getElementById('resetEmail').value.trim();
    const sendBtn = document.getElementById('sendResetBtn');

    if (!email) {
        document.getElementById('resetEmailError').textContent = '이메일을 입력해주세요.';
        return;
    }

    // 이메일 형식 검증
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        document.getElementById('resetEmailError').textContent = '올바른 이메일 형식을 입력해주세요.';
        return;
    }

    try {
        sendBtn.disabled = true;
        sendBtn.textContent = '발송 중...';

        const requestData = { email: email };
        const responseData = await sendPost(`${NGROK_URL}/api/v1/users/password-reset/send-code`, requestData);

        if (responseData?.success) {
            resetEmail = email;
            showResetStep(2);
            showAlert('인증 코드가 발송되었습니다.');
            document.getElementById('resetEmailError').textContent = '';
        } else {
            document.getElementById('resetEmailError').textContent =
                responseData?.message || '이메일 발송에 실패했습니다.';
        }
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('resetEmailError').textContent = '네트워크 오류가 발생했습니다.';
    } finally {
        sendBtn.disabled = false;
        sendBtn.textContent = '재설정 링크 발송';
    }
}

// 2단계: 인증 코드 확인
async function verifyResetCode(event) {
    event.preventDefault();
    const code = document.getElementById('resetCodeInput').value.trim();

    if (!code) {
        document.getElementById('resetCodeError').textContent = '인증 코드를 입력해주세요.';
        return;
    }

    if (code.length !== 6) {
        document.getElementById('resetCodeError').textContent = '6자리 인증 코드를 입력해주세요.';
        return;
    }

    try {
        const requestData = {
            email: resetEmail,
            verificationCode: code
        };
        const responseData = await sendPost(`${NGROK_URL}/api/v1/users/password-reset/verify-code`, requestData);

        if (responseData?.exception.message) {
            showResetStep(3);
            document.getElementById('resetCodeError').textContent = '';
        } else {
            document.getElementById('resetCodeError').textContent =
                responseData?.exception?.message || '인증 코드가 올바르지 않습니다.';
        }
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('resetCodeError').textContent = '네트워크 오류가 발생했습니다.';
    }
}

// 인증 코드 재발송
async function resendResetCode() {
    try {
        const requestData = {
            email: resetEmail
        };
        const responseData = await sendPost(`${NGROK_URL}/api/v1/users/password-reset/send-code`, requestData);

        if (responseData?.exception.message) {
            showAlert('인증 코드가 재발송되었습니다.');
        } else {
            showAlert(responseData?.message || '인증 코드 재발송에 실패했습니다.');
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('네트워크 오류가 발생했습니다.');
    }
}

// 새 비밀번호 유효성 검사
function checkNewPassword() {
    const password = document.getElementById('newPassword').value;
    const requirements = {
        newLengthReq: password.length >= 8,
        newUpperReq: /[A-Z]/.test(password),
        newLowerReq: /[a-z]/.test(password),
        newNumberReq: /\d/.test(password),
        newSpecialReq: /[!@#$%^&*(),.?":{}|<>]/.test(password)
    };

    for (let id in requirements) {
        const element = document.getElementById(id);
        if (element) {
            element.classList.toggle('valid', requirements[id]);
        }
    }

    checkNewPasswordMatch();
    updateResetPasswordButton();
}

// 새 비밀번호 확인 일치 검사
function checkNewPasswordMatch() {
    const pw = document.getElementById('newPassword').value;
    const cpw = document.getElementById('confirmNewPassword').value;

    if (pw && cpw && pw !== cpw) {
        document.getElementById('newPasswordError').textContent = '비밀번호가 일치하지 않습니다.';
    } else {
        document.getElementById('newPasswordError').textContent = '';
    }

    updateResetPasswordButton();
}

// 비밀번호 재설정 버튼 활성화/비활성화
function updateResetPasswordButton() {
    const pw = document.getElementById('newPassword').value;
    const cpw = document.getElementById('confirmNewPassword').value;
    const validPw = pw.length >= 8 &&
        /[A-Z]/.test(pw) &&
        /[a-z]/.test(pw) &&
        /\d/.test(pw) &&
        /[!@#$%^&*(),.?":{}|<>]/.test(pw);
    const isValid = validPw && pw === cpw;

    const resetBtn = document.getElementById('resetPasswordBtn');
    if (resetBtn) {
        resetBtn.disabled = !isValid;
    }
}

// 3단계: 새 비밀번호 설정
async function resetPassword(event) {
    event.preventDefault();
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmNewPassword').value;
    const resetBtn = document.getElementById('resetPasswordBtn');

    if (!newPassword || !confirmPassword) {
        document.getElementById('newPasswordError').textContent = '새 비밀번호를 입력해주세요.';
        return;
    }

    if (newPassword !== confirmPassword) {
        document.getElementById('newPasswordError').textContent = '비밀번호가 일치하지 않습니다.';
        return;
    }

    try {
        resetBtn.disabled = true;
        resetBtn.textContent = '변경 중...';

        const requestData = {
            email: resetEmail,
            newPassword: newPassword
        };
        const responseData = await sendPost(`${NGROK_URL}/api/v1/users/password-reset/reset`, requestData);

        if (responseData?.exception.message) {
            showResetStep(4);
            document.getElementById('newPasswordError').textContent = '';
        } else {
            document.getElementById('newPasswordError').textContent =
                responseData?.message || '비밀번호 변경에 실패했습니다.';
        }
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('newPasswordError').textContent = '네트워크 오류가 발생했습니다.';
    } finally {
        resetBtn.disabled = false;
        resetBtn.textContent = '비밀번호 변경';
    }
}

// 모달 외부 클릭시 닫기
window.addEventListener('click', function(event) {
    const modal = document.getElementById('passwordResetModal');
    if (event.target === modal) {
        closePasswordResetModal();
    }
});

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        const modal = document.getElementById('passwordResetModal');
        if (modal && modal.style.display === 'block') {
            closePasswordResetModal();
        }
    }
});