-- 사용자 테이블
CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       nickname VARCHAR(100),
                       password VARCHAR(255),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 과목 테이블
CREATE TABLE subjects (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 문제 테이블
CREATE TABLE questions (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           subject_id INT,
                           question_type ENUM('MULTIPLE_CHOICE', 'SUBJECTIVE'),
                           question_text LONGTEXT,
                           status ENUM('CREATED', 'RUNNING', 'DELETED'),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);

-- 선택지 테이블
CREATE TABLE choices (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         subject_id INT,
                         question_id INT,
                         is_correct BOOLEAN,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
                         FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- 퀴즈 시도 이력 테이블
CREATE TABLE quiz_attempt_histories (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        user_id INT,
                                        question_id INT,
                                        attempt_count INT,
                                        started_at TIMESTAMP,
                                        ended_at TIMESTAMP,
                                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                        FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- 선택지 시도 이력 테이블 (오타 수정: attemps -> attempts)
CREATE TABLE choice_attempts_histories (
                                           id INT AUTO_INCREMENT PRIMARY KEY,
                                           quiz_attempt_id INT,
                                           choice_id INT,
                                           is_selected BOOLEAN,
                                           answered_at TIMESTAMP,
                                           FOREIGN KEY (quiz_attempt_id) REFERENCES quiz_attempt_histories(id) ON DELETE CASCADE,
                                           FOREIGN KEY (choice_id) REFERENCES choices(id) ON DELETE CASCADE
);

-- 인덱스 추가 (성능 최적화)
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_questions_subject_id ON questions(subject_id);
CREATE INDEX idx_choices_question_id ON choices(question_id);
CREATE INDEX idx_quiz_attempts_user_id ON quiz_attempt_histories(user_id);
CREATE INDEX idx_choice_attempts_quiz_id ON choice_attempts_histories(quiz_attempt_id);