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

    const responseData = await sendPost(`${NGROK_URL}/api/v1/users/login`, requestData);
    if (responseData?.success) {
        window.location.href = '/';
    } else {
        showAlert(responseData?.exception?.message || '로그인 실패');
    }
}
