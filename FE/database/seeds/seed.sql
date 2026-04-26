USE planbook_ai;

-- =========================
-- ROLE
-- =========================
INSERT INTO role (name) VALUES 
('ADMIN'),
('MANAGER'),
('STAFF'),
('TEACHER');

-- =========================
-- USER
-- =========================
INSERT INTO user (email, password_hash, full_name, status)
VALUES 
('admin@gmail.com', '123456', 'Admin System', 'active'),
('teacher1@gmail.com', '123456', 'Nguyen Van A', 'active'),
('staff1@gmail.com', '123456', 'Tran Thi B', 'active');

-- =========================
-- USER ROLE
-- =========================
-- admin -> ADMIN
INSERT INTO user_role (user_id, role_id) VALUES (1, 1);

-- teacher -> TEACHER
INSERT INTO user_role (user_id, role_id) VALUES (2, 4);

-- staff -> STAFF
INSERT INTO user_role (user_id, role_id) VALUES (3, 3);

-- =========================
-- SUBJECT
-- =========================
INSERT INTO subject (name) VALUES 
('Chemistry'),
('Physics');

-- =========================
-- TOPIC
-- =========================
INSERT INTO topic (subject_id, name) VALUES 
(1, 'Organic Chemistry'),
(1, 'Inorganic Chemistry'),
(2, 'Mechanics');

-- =========================
-- QUESTION
-- =========================
INSERT INTO question (content, type, difficulty, topic_id, created_by, status)
VALUES 
('What is H2O?', 'mcq', 'easy', 1, 3, 'approved'),
('What is NaCl?', 'mcq', 'easy', 2, 3, 'approved');

-- =========================
-- QUESTION CHOICE
-- =========================
INSERT INTO question_choice (question_id, content, is_correct) VALUES
(1, 'Water', TRUE),
(1, 'Oxygen', FALSE),
(2, 'Salt', TRUE),
(2, 'Sugar', FALSE);

-- =========================
-- EXAM
-- =========================
INSERT INTO exam (teacher_id, title, duration, total_questions)
VALUES 
(2, 'Test Chemistry 1', 60, 2);

-- =========================
-- EXAM QUESTION
-- =========================
INSERT INTO exam_question (exam_id, question_id, order_index)
VALUES 
(1, 1, 1),
(1, 2, 2);

-- =========================
-- EXERCISE
-- =========================
INSERT INTO exercise (teacher_id, title)
VALUES 
(2, 'Homework 1');

-- =========================
-- EXERCISE QUESTION
-- =========================
INSERT INTO exercise_question (exercise_id, question_id)
VALUES 
(1, 1),
(1, 2);

-- =========================
-- LESSON PLAN TEMPLATE
-- =========================
INSERT INTO lesson_plan_template (title, structure_json, created_by, status)
VALUES 
('Basic Lesson Plan', '{"sections": ["intro", "content", "summary"]}', 3, 'approved');

-- =========================
-- LESSON PLAN
-- =========================
INSERT INTO lesson_plan (teacher_id, template_id, title, content_json)
VALUES 
(2, 1, 'Lesson 1', '{"topic": "Water"}');

-- =========================
-- PROMPT TEMPLATE
-- =========================
INSERT INTO prompt_template (title, content, type, created_by)
VALUES 
('Generate Lesson', 'Create a lesson about chemistry', 'lesson', 3);

-- =========================
-- APPROVAL
-- =========================
INSERT INTO approval (content_type, content_id, approved_by, status, comment)
VALUES 
('question', 1, 1, 'approved', 'OK');

-- =========================
-- OCR RESULT
-- =========================
INSERT INTO ocr_result (exam_id, student_name, score, result_json, graded_at)
VALUES 
(1, 'Student A', 9.5, '{"answers": ["A","B"]}', NOW());