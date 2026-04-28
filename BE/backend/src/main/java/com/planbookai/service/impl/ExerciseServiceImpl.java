package com.planbookai.service.impl;

import com.planbookai.dto.exercise.ExerciseCreateRequest;
import com.planbookai.dto.exercise.ExerciseResponse;
import com.planbookai.entity.*;
import com.planbookai.repository.ExerciseQuestionRepository;
import com.planbookai.repository.ExerciseRepository;
import com.planbookai.repository.QuestionRepository;
import com.planbookai.repository.UserRepository;
import com.planbookai.security.CurrentUserService;
import com.planbookai.service.ExerciseService;
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
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseQuestionRepository exerciseQuestionRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Override
@Transactional
public ExerciseResponse create(ExerciseCreateRequest request) {
    Long currentUserId = currentUserService.requireUserId();
    User teacher = userRepository.findById(currentUserId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUserId));

    // FIX: Chỉ lấy câu hỏi đã được APPROVED — như ExamServiceImpl
    List<Question> source;
    if (request.getTopicId() != null) {
        source = questionRepository.findByTopic_TopicIdAndStatus(
                request.getTopicId(), QuestionStatus.APPROVED);
    } else {
        source = questionRepository.findByStatus(QuestionStatus.APPROVED);
    }

    if (source.size() < request.getQuestionCount()) {
        throw new IllegalArgumentException(
            "Ngân hàng câu hỏi chỉ có " + source.size() + " câu được duyệt" +
            (request.getTopicId() != null ? " trong chủ đề này" : "") +
            ". Bạn yêu cầu " + request.getQuestionCount() + " câu."
        );
    }

    Collections.shuffle(source);
    List<Question> selected = source.stream().limit(request.getQuestionCount()).toList();

    Exercise exercise = new Exercise();
    exercise.setTeacher(teacher);
    exercise.setTitle(request.getTitle());
    exercise.setCreatedAt(LocalDateTime.now());
    Exercise saved = exerciseRepository.save(exercise);

    for (Question question : selected) {
        ExerciseQuestion link = new ExerciseQuestion();
        link.setId(new ExerciseQuestionId(saved.getExerciseId(), question.getQuestionId()));
        link.setExercise(saved);
        link.setQuestion(question);
        exerciseQuestionRepository.save(link);
    }
    return toResponse(saved);
}
    @Override
    @Transactional(readOnly = true)
    public List<ExerciseResponse> listMine() {
        Long currentUserId = currentUserService.requireUserId();
        if (currentUserService.hasRole("ADMIN")) {
            return exerciseRepository.findAll().stream().map(this::toResponse).toList();
        }
        return exerciseRepository.findByTeacher_UserId(currentUserId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseResponse getById(@NonNull Long exerciseId) {
        Exercise exercise = getExercise(exerciseId);
        ensureOwnerOrAdmin(exercise);
        return toResponse(exercise);
    }

    @Override
    @Transactional
    public void delete(@NonNull Long exerciseId) {
        Exercise exercise = getExercise(exerciseId);
        ensureOwnerOrAdmin(exercise);
        exerciseRepository.delete(exercise);
    }

    private @NonNull Exercise getExercise(@NonNull Long exerciseId) {
        return Objects.requireNonNull(
                exerciseRepository.findById(exerciseId)
                        .orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + exerciseId))
        );
    }

    private void ensureOwnerOrAdmin(@NonNull Exercise exercise) {
        if (currentUserService.hasRole("ADMIN")) {
            return;
        }
        if (!currentUserService.requireUserId().equals(exercise.getTeacher().getUserId())) {
            throw new IllegalArgumentException("You can only access your own exercise");
        }
    }

    private ExerciseResponse toResponse(Exercise exercise) {
        List<Long> questionIds = exerciseQuestionRepository.findByExercise_ExerciseId(exercise.getExerciseId())
                .stream()
                .map(link -> link.getQuestion().getQuestionId())
                .toList();
        return ExerciseResponse.builder()
                .exerciseId(exercise.getExerciseId())
                .title(exercise.getTitle())
                .teacherId(exercise.getTeacher().getUserId())
                .createdAt(exercise.getCreatedAt())
                .questionIds(questionIds)
                .build();
    }
}
