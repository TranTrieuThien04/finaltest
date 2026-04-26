package com.planbookai.service.impl;

import com.planbookai.dto.exam.ExamCreateRequest;
import com.planbookai.dto.exam.ExamResponse;
import com.planbookai.entity.*;
import com.planbookai.repository.ExamQuestionRepository;
import com.planbookai.repository.ExamRepository;
import com.planbookai.repository.QuestionRepository;
import com.planbookai.repository.UserRepository;
import com.planbookai.security.CurrentUserService;
import com.planbookai.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public ExamResponse create(ExamCreateRequest request) {
        Long currentUserId = currentUserService.requireUserId();
        User teacher = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUserId));

        List<Question> source = request.getTopicId() == null
                ? questionRepository.findAll()
                : questionRepository.findByTopic_TopicId(request.getTopicId());
        if (source.size() < request.getTotalQuestions()) {
            throw new IllegalArgumentException("Not enough questions to generate exam");
        }
        Collections.shuffle(source);
        List<Question> selected = source.stream().limit(request.getTotalQuestions()).toList();

        Exam exam = new Exam();
        exam.setTeacher(teacher);
        exam.setTitle(request.getTitle());
        exam.setDuration(request.getDuration());
        exam.setTotalQuestions(request.getTotalQuestions());
        exam.setCreatedAt(LocalDateTime.now());
        Exam saved = examRepository.save(exam);

        int index = 1;
        for (Question question : selected) {
            ExamQuestion link = new ExamQuestion();
            link.setId(new ExamQuestionId(saved.getExamId(), question.getQuestionId()));
            link.setExam(saved);
            link.setQuestion(question);
            link.setOrderIndex(index++);
            examQuestionRepository.save(link);
        }
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> listMine() {
        Long currentUserId = currentUserService.requireUserId();
        if (currentUserService.hasRole("ADMIN")) {
            return examRepository.findAll().stream().map(this::toResponse).toList();
        }
        return examRepository.findByTeacher_UserId(currentUserId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResponse getById(@NonNull Long examId) {
        Exam exam = getExam(examId);
        ensureOwnerOrAdmin(exam);
        return toResponse(exam);
    }

    @Override
    @Transactional
    public void delete(@NonNull Long examId) {
        Exam exam = getExam(examId);
        ensureOwnerOrAdmin(exam);
        examRepository.delete(exam);
    }

    private @NonNull Exam getExam(@NonNull Long examId) {
        return Objects.requireNonNull(
                examRepository.findById(examId)
                        .orElseThrow(() -> new IllegalArgumentException("Exam not found: " + examId))
        );
    }

    private void ensureOwnerOrAdmin(@NonNull Exam exam) {
        if (currentUserService.hasRole("ADMIN")) {
            return;
        }
        if (!currentUserService.requireUserId().equals(exam.getTeacher().getUserId())) {
            throw new IllegalArgumentException("You can only access your own exam");
        }
    }

    private ExamResponse toResponse(Exam exam) {
        List<Long> questionIds = examQuestionRepository.findByExam_ExamIdOrderByOrderIndexAsc(exam.getExamId())
                .stream()
                .map(link -> link.getQuestion().getQuestionId())
                .toList();
        return ExamResponse.builder()
                .examId(exam.getExamId())
                .title(exam.getTitle())
                .duration(exam.getDuration())
                .totalQuestions(exam.getTotalQuestions())
                .teacherId(exam.getTeacher().getUserId())
                .createdAt(exam.getCreatedAt())
                .questionIds(questionIds)
                .build();
    }
}
