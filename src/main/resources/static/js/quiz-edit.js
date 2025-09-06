// =====================
// 퀴즈 편집 페이지 스크립트
// =====================

let questionCount = 0;
let isEditMode = false;

// =====================
// 초기화
// =====================
$(document).ready(function() {
    initializeQuizEdit();
    bindEvents();

    // 기존 문제가 있으면 카운트 설정
    const existingQuestions = $('.question-item').length;
    if (existingQuestions > 0) {
        questionCount = existingQuestions;
        isEditMode = window.quizData && window.quizData.isEditMode;
    }

    console.log('퀴즈 편집 페이지 초기화 완료');
    console.log('편집 모드:', isEditMode);
    console.log('현재 문제 수:', questionCount);
});

// =====================
// 이벤트 바인딩
// =====================
function bindEvents() {
    // 새 문제 추가 버튼
    $('#addQuestionBtn').on('click', addQuestion);

    // 폼 제출
    $('#quizEditForm').on('submit', handleFormSubmit);

    // 문제 삭제 (동적 이벤트)
    $(document).on('click', '.delete-question-btn', function() {
        deleteQuestion($(this).closest('.question-item'));
    });

    // 라디오 버튼 변경 시 정답 표시
    $(document).on('change', '.choice-radio', function() {
        updateCorrectChoice($(this));
    });

    // 정답 체크박스 클릭 (새로 추가된 문제용)
    $(document).on('click', '.correct-indicator', function() {
        setCorrectAnswer($(this));
    });
}

// =====================
// 초기화 함수
// =====================
function initializeQuizEdit() {
    // 기존 문제들의 번호 재정렬
    renumberQuestions();
}

// =====================
// 새 문제 추가
// =====================
function addQuestion() {
    questionCount++;
    const questionsContainer = $('#questionsContainer');

    // 빈 상태 메시지 제거
    questionsContainer.find('.empty-state').remove();

    const questionHtml = createQuestionHtml(questionCount);
    questionsContainer.append(questionHtml);

    console.log('새 문제 추가:', questionCount);
}

// =====================
// 문제 HTML 생성
// =====================
function createQuestionHtml(questionNumber) {
    return `
        <div class="question-item" data-question-number="${questionNumber}">
            <div class="question-header">
                <span class="question-number">문제 ${questionNumber}</span>
                <button type="button" class="delete-question-btn">🗑️</button>
            </div>

            <div class="form-group">
                <input type="text"
                       class="question-input form-input"
                       placeholder="문제를 입력하세요"
                       required>
            </div>

            <div class="choices-container">
                ${createChoicesHtml(questionNumber)}
            </div>
        </div>
    `;
}

// =====================
// 선택지 HTML 생성
// =====================
function createChoicesHtml(questionNumber) {
    let choicesHtml = '';

    for (let i = 0; i < 4; i++) {
        choicesHtml += `
            <div class="choice-item">
                <div class="choice-wrapper">
                    <input type="radio"
                           name="correct_${questionNumber}"
                           value="${i}"
                           class="choice-radio">
                    <input type="text"
                           class="choice-input form-input"
                           placeholder="선택지 ${i + 1}"
                           required>
                    <label class="correct-label">정답</label>
                </div>
            </div>
        `;
    }

    return choicesHtml;
}

// =====================
// 문제 삭제
// =====================
function deleteQuestion($questionItem) {
    if (!confirm('이 문제를 삭제하시겠습니까?')) {
        return;
    }

    $questionItem.remove();

    // 남은 문제가 없으면 빈 상태 표시
    const remainingQuestions = $('.question-item').length;
    if (remainingQuestions === 0) {
        showEmptyState();
    }

    // 문제 번호 재정렬
    renumberQuestions();

    console.log('문제 삭제 완료, 남은 문제 수:', remainingQuestions);
}

// =====================
// 빈 상태 표시
// =====================
function showEmptyState() {
    const emptyStateHtml = `
        <div class="empty-state">
            <div class="empty-state-icon">📝</div>
            <p>아직 문제가 없습니다.<br>아래 버튼을 클릭하여 첫 번째 문제를 추가해보세요!</p>
        </div>
    `;

    $('#questionsContainer').html(emptyStateHtml);
}

// =====================
// 정답 선택 업데이트 (라디오 버튼용)
// =====================
function updateCorrectChoice($radioButton) {
    const $questionItem = $radioButton.closest('.question-item');
    const $choiceWrappers = $questionItem.find('.choice-wrapper');

    // 모든 선택지에서 is-correct 클래스 제거
    $choiceWrappers.removeClass('is-correct');

    // 선택된 선택지에만 is-correct 클래스 추가
    $radioButton.closest('.choice-wrapper').addClass('is-correct');
}

// =====================
// 정답 설정 (체크박스 방식, 새로 추가된 문제용)
// =====================
function setCorrectAnswer($indicator) {
    const $choiceGroup = $indicator.closest('.choice-group');
    const $questionItem = $indicator.closest('.question-item');
    const questionNumber = $questionItem.data('question-number');

    // 해당 문제의 모든 체크 해제
    $questionItem.find('.correct-indicator').removeClass('active');

    // 선택된 항목만 체크
    $indicator.addClass('active');

    // 라디오 버튼도 함께 선택
    const choiceIndex = $indicator.closest('.choice-item').index();
    const $radioButton = $questionItem.find(`input[name="correct_${questionNumber}"]`).eq(choiceIndex);
    $radioButton.prop('checked', true);

    // 스타일 업데이트
    updateCorrectChoice($radioButton);
}

// =====================
// 문제 번호 재정렬
// =====================
function renumberQuestions() {
    $('.question-item').each(function(index) {
        const newNumber = index + 1;
        $(this).attr('data-question-number', newNumber);
        $(this).find('.question-number').text(`문제 ${newNumber}`);

        // 라디오 버튼 name 속성도 업데이트
        $(this).find('.choice-radio').attr('name', `correct_${newNumber}`);
    });

    questionCount = $('.question-item').length;
}

// =====================
// 폼 데이터 수집
// =====================
function collectFormData() {
    const questionTitleId = $('#questionTitleId').val() || null;
    const titleText = $('#quizTitle').val().trim();
    const description = $('#quizDescription').val().trim();
    const subjectId = $('#quizSubject').val();
    const timeLimit = parseInt($('#timeLimit').val());

    const questions = [];

    $('.question-item').each(function() {
        const $questionItem = $(this);
        const questionId = $questionItem.data('question-id') || null;
        const questionText = $questionItem.find('.question-input').val().trim();

        const choices = [];
        let correctAnswerIndex = -1;

        $questionItem.find('.choice-item').each(function(index) {
            const $choiceItem = $(this);
            const choiceId = $choiceItem.find('.choice-radio').val().trim();
            const choiceText = $choiceItem.find('.choice-input').val().trim();
            const isCorrect = $choiceItem.find('.choice-radio').is(':checked');

            if (isCorrect) {
                correctAnswerIndex = index;
            }

            choices.push({
                choiceId: choiceId,
                choiceText: choiceText,
                isCorrect: isCorrect
            });
        });

        questions.push({
            questionId: questionId,
            questionText: questionText,
            choices: choices,
            correctAnswerIndex: correctAnswerIndex
        });
    });

    return {
        questionTitleId: questionTitleId,
        titleText: titleText,
        description: description,
        subjectId: subjectId ? parseInt(subjectId) : null,
        timeLimit: timeLimit,
        questions: questions
    };
}

// =====================
// 폼 유효성 검사
// =====================
function validateForm(formData) {
    if (!formData.titleText) {
        showAlert('퀴즈 제목을 입력해주세요.');
        return false;
    }

    if (!formData.subjectId) {
        showAlert('과목을 선택해주세요.');
        return false;
    }

    if (formData.questions.length === 0) {
        showAlert('최소 1개의 문제를 추가해야 합니다.');
        return false;
    }

    // 각 문제의 유효성 검사
    for (let i = 0; i < formData.questions.length; i++) {
        const question = formData.questions[i];

        if (!question.questionText) {
            showAlert(`문제 ${i + 1}의 질문을 입력해주세요.`);
            return false;
        }

        // 모든 선택지가 입력되었는지 확인
        for (let j = 0; j < question.choices.length; j++) {
            if (!question.choices[j].choiceText) {
                showAlert(`문제 ${i + 1}의 선택지 ${j + 1}을 입력해주세요.`);
                return false;
            }
        }

        // 정답이 선택되었는지 확인
        if (question.correctAnswerIndex === -1) {
            showAlert(`문제 ${i + 1}의 정답을 선택해주세요.`);
            return false;
        }
    }

    return true;
}

// =====================
// 폼 제출 처리
// =====================
async function handleFormSubmit(e) {
    e.preventDefault();

    console.log('폼 제출 시작...');

    const formData = collectFormData();
    console.log('수집된 폼 데이터:', formData);

    if (!validateForm(formData)) {
        return;
    }

    // 버튼 비활성화
    const $submitButton = $('.btn-primary');
    const originalText = $submitButton.html();
    $submitButton.prop('disabled', true).html('저장 중...');

    try {
        let result;

        if (isEditMode && formData.questionTitleId) {
            // 수정 모드
            result = await sendPost(`${NGROK_URL}/api/v1/quiz/${formData.questionTitleId}`, formData, {
                method: 'PUT'
            });
        } else {
            // 생성 모드
            result = await sendPost(`${NGROK_URL}/api/v1/quiz`, formData);
        }

        if (result) {
            const successMessage = isEditMode ? '퀴즈가 성공적으로 수정되었습니다!' : '퀴즈가 성공적으로 저장되었습니다!';
            showAlert(successMessage);

            // 성공 시 리다이렉트
            setTimeout(() => {
                window.location.href = '/quiz/myquiz';
            }, 1500);
        }

    } catch (error) {
        console.error('폼 제출 오류:', error);
        showAlert('저장 중 오류가 발생했습니다. 다시 시도해주세요.');

    } finally {
        // 버튼 복원
        $submitButton.prop('disabled', false).html(originalText);
    }
}

// =====================
// 디버깅용 함수들
// =====================
function debugFormData() {
    const formData = collectFormData();
    console.log('현재 폼 데이터:', formData);
    return formData;
}

function debugQuestionCount() {
    const actualCount = $('.question-item').length;
    console.log('실제 문제 수:', actualCount, '카운터 값:', questionCount);
    return { actual: actualCount, counter: questionCount };
}

// 전역으로 노출 (디버깅용)
window.debugFormData = debugFormData;
window.debugQuestionCount = debugQuestionCount;