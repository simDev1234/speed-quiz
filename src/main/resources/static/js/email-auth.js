async function sendVerificationCode(event) {
    event.preventDefault();
    const email = document.getElementById('signupEmail').value;

    if (!email) {
        showAlert('이메일을 입력해주세요.');
        return;
    }

    await sendPost(`${NGROK_URL}/api/v1/users/email/auth`, { email });
    document.getElementById('verificationSection')?.style?.setProperty('display', 'block');
    document.getElementById('sendCodeBtn')?.style?.setProperty('display', 'none');
    verifiedEmail = email;
}

async function verifyCode(event) {
    event.preventDefault();
    const email = document.getElementById('signupEmail')?.value;
    const inputCode = document.getElementById('codeInput')?.value;

    if (!inputCode) {
        showAlert('인증 코드를 입력해주세요.');
        return;
    }

    const responseData = await sendPost(`${NGROK_URL}/api/v1/users/email/code`, {
        email,
        verificationCode: inputCode
    });

    if (responseData?.success) {
        verifiedEmail = email;
        currentStep = 2;
        updateStepIndicator();
        showStep(2);
    } else {
        alert('인증 실패 ! 모바일이야?' + inputCode);
    }

}