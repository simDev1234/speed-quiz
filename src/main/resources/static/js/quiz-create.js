let questionCount = 0;

// 새 문제 추가
function addQuestion() {
    questionCount++;
    const questionsContainer = document.getElementById('questionsContainer');

    // 빈 상태 메시지 제거
    const emptyState = questionsContainer.querySelector('.empty-state');
    if (emptyState) {
        emptyState.remove();
    }

    const questionHtml = `
                    <div class="question-item" data-question-id="${questionCount}">
                        <div class="question-header">
                            <span class="question-number">문제 ${questionCount}</span>
                            <button type="button" class="delete-question" onclick="deleteQuestion(${questionCount})">삭제</button>
                        </div>

                        <div class="form-group">
                            <label class="form-label">문제*</label>
                            <textarea class="form-input form-textarea" placeholder="문제를 입력하세요" required></textarea>
                        </div>

                        <div class="form-group">
                            <label class="form-label">정답 선택지 (체크 표시를 클릭하여 정답 설정)</label>
                            <div class="choices-container">
                                <div class="choice-group">
                                    <input type="text" class="choice-input" placeholder="선택지 1" required>
                                    <span class="correct-indicator" onclick="setCorrectAnswer(${questionCount}, 1)">✓</span>
                                </div>
                                <div class="choice-group">
                                    <input type="text" class="choice-input" placeholder="선택지 2" required>
                                    <span class="correct-indicator" onclick="setCorrectAnswer(${questionCount}, 2)">✓</span>
                                </div>
                                <div class="choice-group">
                                    <input type="text" class="choice-input" placeholder="선택지 3" required>
                                    <span class="correct-indicator" onclick="setCorrectAnswer(${questionCount}, 3)">✓</span>
                                </div>
                                <div class="choice-group">
                                    <input type="text" class="choice-input" placeholder="선택지 4" required>
                                    <span class="correct-indicator" onclick="setCorrectAnswer(${questionCount}, 4)">✓</span>
                                </div>
                            </div>
                        </div>
                    </div>
                `;

    questionsContainer.insertAdjacentHTML('beforeend', questionHtml);
}

// 문제 삭제
function deleteQuestion(questionId) {
    if (confirm('이 문제를 삭제하시겠습니까?')) {
        const questionItem = document.querySelector(`[data-question-id="${questionId}"]`);
        questionItem.remove();

        // 남은 문제가 없으면 빈 상태 표시
        const remainingQuestions = document.querySelectorAll('.question-item').length;
        if (remainingQuestions === 0) {
            const questionsContainer = document.getElementById('questionsContainer');
            questionsContainer.innerHTML = `
                            <div class="empty-state">
                                <div class="empty-state-icon">📝</div>
                                <p>아직 문제가 없습니다.<br>아래 버튼을 클릭하여 첫 번째 문제를 추가해보세요!</p>
                            </div>
                        `;
        }

        // 문제 번호 재정렬
        renumberQuestions();
    }
}

// 정답 설정
function setCorrectAnswer(questionId, choiceNumber) {
    const questionItem = document.querySelector(`[data-question-id="${questionId}"]`);
    const indicators = questionItem.querySelectorAll('.correct-indicator');

    // 모든 체크 해제
    indicators.forEach(indicator => {
        indicator.classList.remove('active');
    });

    // 선택된 항목만 체크
    indicators[choiceNumber - 1].classList.add('active');
}

// 문제 번호 재정렬
function renumberQuestions() {
    const questionItems = document.querySelectorAll('.question-item');
    questionItems.forEach((item, index) => {
        const questionNumber = item.querySelector('.question-number');
        questionNumber.textContent = `문제 ${index + 1}`;
    });
}

// 폼 데이터 수집
function collectFormData() {
    const title = document.getElementById('quizTitle').value;
    const description = document.getElementById('quizDescription').value;
    const subject = document.getElementById('quizSubject').value;
    const timeLimit = document.getElementById('timeLimit').value;

    const questions = [];
    const questionItems = document.querySelectorAll('.question-item');

    questionItems.forEach(item => {
        const questionText = item.querySelector('textarea').value;
        const choices = [];
        const choiceInputs = item.querySelectorAll('.choice-input');
        const correctIndicators = item.querySelectorAll('.correct-indicator');

        let correctAnswerIndex = -1;

        choiceInputs.forEach((input, index) => {
            choices.push(input.value);
            if (correctIndicators[index].classList.contains('active')) {
                correctAnswerIndex = index;
            }
        });

        questions.push({
            questionText: questionText,
            choices: choices,
            correctAnswerIndex: correctAnswerIndex
        });
    });

    return {
        subjectId : subject,
        title : title,
        description : description,
        timeLimit   : parseInt(timeLimit),
        questions   : questions
    };
}

// 폼 제출
document.getElementById('quizCreateForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const formData = collectFormData();

    // 유효성 검사
    if (!formData.title.trim()) {
        showAlert('퀴즈 제목을 입력해주세요.');
        return;
    }

    if (!formData.subjectName) {
        showAlert('과목을 선택해주세요.');
        return;
    }

    if (formData.questions.length === 0) {
        showAlert('최소 1개의 문제를 추가해야 합니다.');
        return;
    }

    // 각 문제의 유효성 검사
    for (let i = 0; i < formData.questions.length; i++) {
        const question = formData.questions[i];

        if (!question.questionText.trim()) {
            showAlert(`문제 ${i + 1}의 질문을 입력해주세요.`);
            return;
        }

        for (let j = 0; j < question.choices.length; j++) {
            if (!question.choices[j].trim()) {
                showAlert(`문제 ${i + 1}의 선택지 ${j + 1}을 입력해주세요.`);
                return;
            }
        }

        if (question.correctAnswerIndex === -1) {
            showAlert(`문제 ${i + 1}의 정답을 선택해주세요.`);
            return;
        }
    }

    const result = await sendPost(`${NGROK_URL}/api/v1/quiz`, formData);

    if (result) {
        showAlert('퀴즈가 성공적으로 저장되었습니다!');
        // 성공 시 리다이렉트 또는 폼 초기화
        setTimeout(() => {
            window.location.href = '/';
        }, 1000);
    }
});

// 새 문제 추가 버튼 이벤트
document.getElementById('addQuestionBtn').addEventListener('click', addQuestion);