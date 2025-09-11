//-- 광고 모달 start--------------
// 모달 관련 변수
let modalCountdownInterval;

// 모달 표시 함수
function showCompletionModal(modalCountdown) {
    return new Promise((resolve) => {
        document.getElementById('completionModal').style.display = 'flex';
        startModalCountdown(modalCountdown, resolve); // resolve를 콜백으로 전달
    });
}

// 모달 카운트다운 시작
function startModalCountdown(modalCountdown) {

    updateCountdownDisplay(modalCountdown);

    modalCountdownInterval = setInterval(() => {
        modalCountdown--;
        updateCountdownDisplay();

        if (modalCountdown <= 0) {
            clearInterval(modalCountdownInterval);
            showCloseButton();
        }
    }, 1000);
}

// 카운트다운 표시 업데이트
function updateCountdownDisplay(modalCountdown) {
    const countdownElement = document.getElementById('countdownDisplay');
    if (modalCountdown > 0) {
        countdownElement.textContent = modalCountdown;
        countdownElement.style.color = '#007bff';
    } else {
        countdownElement.textContent = '✨';
        countdownElement.style.color = '#28a745';
    }
}

// 닫기 버튼 표시
function showCloseButton() {
    document.getElementById('closeModalBtn').classList.add('show');
}

// 모달 닫기
function closeModal() {
    document.getElementById('completionModal').style.display = 'none';
    // 결과 페이지로 이동
    const questionTitleId = document.getElementById('infoNumber').textContent.replace(/\[|\]/g, '');
    window.location.href = `${URL}/quiz/result?questionTitleId=` + questionTitleId;
}

//-- 광고 모달 end --------------

// 문제 문항 렌더링
function renderQuestion() {
    if (!questions || questions.length === 0) {
        console.error('퀴즈 데이터가 없습니다.');
        return;
    }

    clearInterval(timerInterval);
    timeLeft = totalTime;
    isTimeUp = false;
    selectedChoiceId = null;

    const question = questions[currentQuestionIndex];
    document.getElementById('infoNumber').textContent = `[${question.questionTitleId}]`;
    document.getElementById('infoNumber').classList.add('hidden');
    document.getElementById('infoSubject').textContent = `[${question.subject.subjectName}]`;
    document.getElementById('questionNumber').textContent = `문제 ${currentQuestionIndex + 1}/${questions.length}`;
    document.getElementById('questionText').textContent = question.questionText;

    const answersGrid = document.getElementById('answersGrid');
    answersGrid.innerHTML = '';
    question.choices.forEach(choice => {
        const btn = document.createElement('button');
        btn.className = 'answer-btn';
        btn.textContent = choice.choiceText;
        btn.dataset.choiceId = choice.choiceId;
        btn.onclick = () => selectChoice(btn);
        answersGrid.appendChild(btn);
    });

    document.getElementById('submitBtn').classList.add('hidden');
    document.getElementById('nextBtn').classList.add('hidden');

    startTimer();
}

function startTimer() {
    updateTimerDisplay();
    timerInterval = setInterval(() => {
        timeLeft--;
        updateTimerDisplay();
        if (timeLeft <= 0) {
            clearInterval(timerInterval);
            handleTimeOut();
        }
    }, 1000);
}

function updateTimerDisplay() {
    const percent = (timeLeft / totalTime) * 100;
    document.getElementById('timeFill').style.width = percent + '%';
    const indicator = document.getElementById('timeIndicator');
    indicator.textContent = `${timeLeft}초`;
    if (timeLeft <= 2) {
        indicator.classList.add('warning');
    } else {
        indicator.classList.remove('warning');
    }
}

function selectChoice(button) {
    if (isTimeUp) return;
    document.querySelectorAll('.answer-btn').forEach(btn => btn.classList.remove('selected'));
    button.classList.add('selected');
    selectedChoiceId = button.dataset.choiceId;
    document.getElementById('submitBtn').classList.remove('hidden');
}

function submitAnswer() {
    clearInterval(timerInterval);
    isTimeUp = true;
    saveUserAnswer();

    // 정답/오답 시각화
    showCorrectAndWrongAnswers();

    // 버튼 비활성화
    document.querySelectorAll('.answer-btn').forEach(btn => btn.disabled = true);

    // 버튼 토글
    document.getElementById('submitBtn').classList.add('hidden');
    document.getElementById('nextBtn').classList.remove('hidden');
}

function handleTimeOut() {
    isTimeUp = true;
    alert('시간 초과!');
    saveUserAnswer();

    // 정답/오답 시각화
    showCorrectAndWrongAnswers();

    document.getElementById('nextBtn').classList.remove('hidden');
    document.querySelectorAll('.answer-btn').forEach(btn => btn.disabled = true);
}

function saveUserAnswer() {
    const question = questions[currentQuestionIndex];
    const questionTitleId = document.getElementById('infoNumber').textContent.replace(/\[|\]/g, '');

    userAnswers.push({
        questionTitleId : questionTitleId,
        questionId: question.questionId,
        selectedChoiceId: selectedChoiceId ? Number(selectedChoiceId) : null
    });
    console.log(userAnswers);
}

function showCorrectAndWrongAnswers() {
    const question = questions[currentQuestionIndex];
    const answerButtons = document.querySelectorAll('.answer-btn');

    const selectedChoice = question.choices.find(c => c.choiceId == selectedChoiceId);
    const isCorrect = selectedChoice && selectedChoice.isCorrect;

    answerButtons.forEach(btn => {
        const btnChoiceId = btn.dataset.choiceId;
        const choice = question.choices.find(c => c.choiceId == btnChoiceId);

        if (isCorrect) {
            // 정답을 골랐다면 → 정답만 초록색, 나머지는 회색
            if (choice.isCorrect) {
                btn.classList.add('correct');
            } else {
                btn.classList.add('neutral');
            }
        } else {
            // 오답을 골랐다면
            if (btnChoiceId === selectedChoiceId) {
                btn.classList.add('wrong'); // 내가 고른 오답 → 빨간색
            } else if (choice.isCorrect) {
                btn.classList.add('correct'); // 정답 → 초록색
            } else {
                btn.classList.add('neutral'); // 나머지 보기 → 회색
            }
        }
    });
}

function goToNext() {
    currentQuestionIndex++;

    if (currentQuestionIndex < questions.length) {
        renderQuestion();
    } else {
        submitFinalAnswers();
    }
}

function submitFinalAnswers() {

    const questionTitleId = document.getElementById('infoNumber').textContent.replace(/\[|\]/g, '');

    try {
        const responseData = sendPost(`${URL}/api/v1/quiz/submit`, userAnswers, {
            credentials: 'include',
        });

        showCompletionModal(5).then(() => {
            window.location.href = `${URL}/quiz/result?questionTitleId=` + questionTitleId;
        });
    } catch (error) {
        console.error('제출 실패:', error);
        alert('답안 제출 중 문제가 발생했습니다.');
    }
}