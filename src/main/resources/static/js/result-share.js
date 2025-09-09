// 텍스트로 결과 복사
function copyResult() {
    // 문항별 결과 요약 생성
    const questionResults = [];
    document.querySelectorAll('.question-result').forEach((element, index) => {
        const questionNumber = index + 1;
        const subjectName = element.querySelector('.question-number').textContent.match(/\[(.*?)\]/)?.[1] || '';
        const isCorrect = element.classList.contains('correct');
        const isUnanswered = element.classList.contains('unanswered');

        // 내 답안과 정답 찾기
        const answerLines = element.querySelectorAll('.answer-line');
        let myAnswer = '';
        let correctAnswer = '';

        answerLines.forEach(line => {
            const label = line.querySelector('.answer-label').textContent;
            const answerText = line.querySelector('.answer-text');

            if (label.includes('내 답안')) {
                myAnswer = answerText.textContent.trim();
            } else if (label.includes('정답')) {
                correctAnswer = answerText.textContent.trim();
            }
        });

        let status = '';
        let answerInfo = '';

        if (isCorrect) {
            status = '✅ 정답';
            answerInfo = `(선택: ${myAnswer})`;
        } else if (isUnanswered || myAnswer === '미선택') {
            status = '❌ 무응답';
            answerInfo = `(정답: ${correctAnswer})`;
        } else {
            status = '❌ 오답';
            answerInfo = `(선택: ${myAnswer} / 정답: ${correctAnswer})`;
        }

        questionResults.push(`${questionNumber}번 [${subjectName}]: ${status} ${answerInfo}`);
    });

    const questionSummary = questionResults.length > 0 ?
        `\n\n📋 문항별 결과:\n${questionResults.join('\n')}` : '';

    const text = `🎉 퀴즈 결과 🎉

📊 총 점수: ${resultData.score}점
🎯 정답률: ${resultData.accuracy}%
✅ 정답 수: ${resultData.correct}/${resultData.total}${questionSummary}

퀴즈를 완료했어요! 🚀`;

    navigator.clipboard.writeText(text).then(() => {
        showCopySuccess();
    }).catch(err => {
        console.error('복사 실패:', err);
        // fallback
        const textArea = document.createElement('textarea');
        textArea.value = text;
        document.body.appendChild(textArea);
        textArea.select();
        document.execCommand('copy');
        document.body.removeChild(textArea);
        showCopySuccess();
    });
}

// 이미지로 저장
function downloadImage() {
    const shareCard = document.getElementById('shareCard');
    shareCard.style.display = 'block';

    html2canvas(shareCard, {
        backgroundColor: null,
        scale: 2,
        useCORS: true
    }).then(canvas => {
        shareCard.style.display = 'none';

        // 이미지 다운로드
        const link = document.createElement('a');
        link.download = `quiz-result-${new Date().getTime()}.png`;
        link.href = canvas.toDataURL();
        link.click();
    });
}

// 카카오톡 공유
function shareKakao() {
    if (typeof Kakao !== 'undefined') {
        Kakao.Share.sendDefault({
            objectType: 'feed',
            content: {
                title: '🎉 퀴즈 결과',
                description: `점수: ${resultData.score}점 | 정답률: ${resultData.accuracy}% | 정답수: ${resultData.correct}/${resultData.total}`,
                imageUrl: window.location.origin + '/images/quiz-share.png',
                link: {
                    mobileWebUrl: window.location.href,
                    webUrl: window.location.href
                }
            }
        });
    } else {
        alert('카카오톡 SDK가 로드되지 않았습니다.');
    }
}

// 복사 성공 메시지 표시
function showCopySuccess() {
    const successMsg = document.getElementById('copySuccess');
    successMsg.classList.add('show');
    setTimeout(() => {
        successMsg.classList.remove('show');
    }, 2000);
}

// 페이지 로드 시 카카오 SDK 초기화 (필요한 경우)
// Kakao.init('YOUR_KAKAO_APP_KEY');