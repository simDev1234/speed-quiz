async function completeSignup(event) {
    event.preventDefault();

    const password = document.getElementById('signupPassword')?.value;
    const nickname = document.getElementById('nickname')?.value;

    if (!verifiedEmail) {
        showAlert('이메일 인증이 필요합니다.');
        return;
    }

    const requestData = {
        email: verifiedEmail,
        password,
        nickname
    };
    const responseData = await sendPost(`${NGROK_URL}/api/v1/users/signup`, requestData, { credentials: 'include' });

    if (responseData?.success) {
        // 회원가입 성공 시 다음 단계
        currentStep = 3;
        updateStepIndicator();
        showStep(3);
    }
}

function togglePasswordVisibility(inputId, iconElement) {
    const input = document.getElementById(inputId);
    const isPassword = input.type === 'password';
    input.type = isPassword ? 'text' : 'password';
    iconElement.textContent = isPassword ? '🙈' : '👁️'; // 눈 or 감은 눈으로 변경
}