CREATE DATABASE IF NOT EXISTS planbook_ai;
USE planbook_ai;

-- ROLE
CREATE TABLE role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- USER
CREATE TABLE user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255),
    full_name VARCHAR(255),
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- USER_ROLE
CREATE TABLE user_role (
    user_id INT,
    role_id INT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (role_id) REFERENCES role(role_id)
);

-- PACKAGE
CREATE TABLE package (
    package_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    price DECIMAL(10,2),
    duration_days INT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ORDER
CREATE TABLE `order` (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    package_id INT,
    status ENUM('pending','active','expired'),
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (package_id) REFERENCES package(package_id)
);

-- SUBJECT
CREATE TABLE subject (
    subject_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

-- TOPIC
CREATE TABLE topic (
    topic_id INT AUTO_INCREMENT PRIMARY KEY,
    subject_id INT,
    name VARCHAR(255),
    FOREIGN KEY (subject_id) REFERENCES subject(subject_id)
);

-- QUESTION
CREATE TABLE question (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    content TEXT,
    type ENUM('mcq','fill_blank','short_answer'),
    difficulty ENUM('easy','medium','hard'),
    topic_id INT,
    created_by INT,
    status ENUM('pending','approved'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (topic_id) REFERENCES topic(topic_id),
    FOREIGN KEY (created_by) REFERENCES user(user_id)
);

-- QUESTION CHOICE
CREATE TABLE question_choice (
    question_choice_id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT,
    content TEXT,
    is_correct BOOLEAN,
    FOREIGN KEY (question_id) REFERENCES question(question_id)
);

-- EXAM
CREATE TABLE exam (
    exam_id INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id INT,
    title VARCHAR(255),
    duration INT,
    total_questions INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES user(user_id)
);

-- EXAM QUESTION
CREATE TABLE exam_question (
    exam_id INT,
    question_id INT,
    order_index INT,
    PRIMARY KEY (exam_id, question_id),
    FOREIGN KEY (exam_id) REFERENCES exam(exam_id),
    FOREIGN KEY (question_id) REFERENCES question(question_id)
);

-- EXERCISE
CREATE TABLE exercise (
    exercise_id INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id INT,
    title VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES user(user_id)
);

-- EXERCISE QUESTION
CREATE TABLE exercise_question (
    exercise_id INT,
    question_id INT,
    PRIMARY KEY (exercise_id, question_id),
    FOREIGN KEY (exercise_id) REFERENCES exercise(exercise_id),
    FOREIGN KEY (question_id) REFERENCES question(question_id)
);

-- LESSON PLAN TEMPLATE
CREATE TABLE lesson_plan_template (
    lesson_plan_template_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    structure_json JSON,
    created_by INT,
    status ENUM('pending','approved'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES user(user_id)
);

-- LESSON PLAN
CREATE TABLE lesson_plan (
    lesson_plan_id INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id INT,
    template_id INT,
    title VARCHAR(255),
    content_json JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES user(user_id),
    FOREIGN KEY (template_id) REFERENCES lesson_plan_template(lesson_plan_template_id)
);

-- PROMPT TEMPLATE
CREATE TABLE prompt_template (
    prompt_template_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    content TEXT,
    type VARCHAR(50),
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES user(user_id)
);

-- APPROVAL
CREATE TABLE approval (
    approval_id INT AUTO_INCREMENT PRIMARY KEY,
    content_type VARCHAR(50),
    content_id INT,
    approved_by INT,
    status ENUM('approved','rejected'),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (approved_by) REFERENCES user(user_id)
);

-- OCR RESULT
CREATE TABLE ocr_result (
    ocr_result_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT,
    student_name VARCHAR(255),
    score FLOAT,
    result_json JSON,
    graded_at TIMESTAMP,
    FOREIGN KEY (exam_id) REFERENCES exam(exam_id)
);