$(document).ready(function() {
    let deleteQuizId = null;

    // CSRF 토큰 설정
    const csrfToken = $('meta[name="_csrf"]').attr('content');
    const csrfHeader = $('meta[name="_csrf_header"]').attr('content');

    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        }
    });

    // 편집 버튼 클릭
    $('.edit-btn').click(function() {
        const quizId = $(this).data('quiz-id');
        window.location.href = `/quiz/edit/${quizId}`;
    });

    // 삭제 버튼 클릭
    $('.delete-btn').click(function() {
        deleteQuizId = $(this).data('quiz-id');
        $('#deleteModal').show();
    });

    // 공유 버튼 클릭
    $('.share-btn').click(function() {
        const quizId = $(this).data('quiz-id');
        const shareUrl = `${window.location.origin}/quiz?questionTitleId=${quizId}`;

        // 클립보드에 복사
        navigator.clipboard.writeText(shareUrl).then(function() {
            showAlert('퀴즈 링크가 클립보드에 복사되었습니다!', 2000);
        }).catch(function() {
            // 클립보드 API가 지원되지 않는 경우
            const textArea = document.createElement('textarea');
            textArea.value = shareUrl;
            document.body.appendChild(textArea);
            textArea.select();
            document.execCommand('copy');
            document.body.removeChild(textArea);
            showAlert('퀴즈 링크가 클립보드에 복사되었습니다!', 2000);
        });
    });

    // 모달 외부 클릭시 닫기
    $('#deleteModal').click(function(e) {
        if (e.target === this) {
            closeDeleteModal();
        }
    });

    // ESC 키로 모달 닫기
    $(document).keydown(function(e) {
        if (e.keyCode === 27) {
            closeDeleteModal();
        }
    });
});

// 삭제 모달 닫기
function closeDeleteModal() {
    $('#deleteModal').hide();
    deleteQuizId = null;
}

// 삭제 확인
function confirmDelete() {
    if (!deleteQuizId) return;

    $.ajax({
        url: `/api/quiz/${deleteQuizId}`,
        type: 'DELETE',
        success: function(response) {
            showAlert('퀴즈가 성공적으로 삭제되었습니다.', 2000);
            closeDeleteModal();

            // 페이지 새로고침 또는 카드 제거
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        },
        error: function(xhr, status, error) {
            let errorMessage = '퀴즈 삭제에 실패했습니다.';

            if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMessage = xhr.responseJSON.message;
            } else if (xhr.status === 403) {
                errorMessage = '삭제 권한이 없습니다.';
            } else if (xhr.status === 404) {
                errorMessage = '퀴즈를 찾을 수 없습니다.';
            }

            showAlert(errorMessage, 3000);
            closeDeleteModal();
        }
    });
}

// 알럿 표시 함수 (common.js에서 가져옴)
function showAlert(message, duration = 3000) {
    // 알럿박스가 없으면 생성
    if (!document.getElementById('commonAlert')) {
        createAlertBox();
    }

    const alertBox = document.getElementById('commonAlert');
    const alertMessage = document.getElementById('commonAlertMessage');

    alertMessage.textContent = message;
    alertBox.style.display = 'flex';

    clearTimeout(alertBox.timeout);
    alertBox.timeout = setTimeout(() => {
        alertBox.style.display = 'none';
    }, duration);
}

// 알럿박스 생성 함수
function createAlertBox() {
    const container = document.createElement('div');
    container.innerHTML = `
    <div id="commonAlert" style='display: none; position: fixed; top: 20px; left: 50%; transform: translateX(-50%); display: flex; align-items: center; color: white; max-width: 384px; width: 100%; background-color: #f87171; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1); border-radius: 8px; overflow: hidden; z-index: 50;'>
        <div style='width: 40px; border-right: 1px solid rgba(255, 255, 255, 0.3); padding: 0 8px; display: flex; align-items: center; justify-content: center; height: 100%;'>
            <svg style="width: 16px; height: 16px;" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
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
}