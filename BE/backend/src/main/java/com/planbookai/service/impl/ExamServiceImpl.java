package com.planbookai.service.impl;

import com.planbookai.dto.exam.ExamCreateRequest;
import com.planbookai.dto.exam.ExamResponse;
import com.planbookai.dto.question.QuestionChoiceResponse;
import com.planbookai.dto.question.QuestionResponse;
import com.planbookai.entity.*;
import com.planbookai.entity.enums.QuestionStatus;
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

        int requested = request.getTotalQuestions();
        List<Question> selected;

        // Nếu giáo viên chọn câu thủ công từ ngân hàng
        if (request.getQuestionIds() != null && !request.getQuestionIds().isEmpty()) {
            if (request.getQuestionIds().size() != requested) {
                throw new IllegalArgumentException(
                    "Số câu hỏi chọn (" + request.getQuestionIds().size() +
                    ") phải bằng totalQuestions (" + requested + ")"
                );
            }
            selected = request.getQuestionIds().stream()
                    .map(qid -> questionRepository.findById(qid)
                            .orElseThrow(() -> new IllegalArgumentException("Question not found: " + qid)))
                    .peek(q -> {
                        if (q.getStatus() != QuestionStatus.APPROVED) {
                            throw new IllegalArgumentException(
                                "Câu hỏi ID=" + q.getQuestionId() + " chưa được duyệt, không thể thêm vào đề thi");
                        }
                    })
                    .toList();
        } else {
            // Random từ ngân hàng — CHỈ lấy câu hỏi đã APPROVED
            List<Question> source;
            if (request.getTopicId() != null) {
                source = questionRepository.findByTopic_TopicIdAndStatus(
                        request.getTopicId(), QuestionStatus.APPROVED);
            } else {
                source = questionRepository.findByStatus(QuestionStatus.APPROVED);
            }

            if (source.size() < requested) {
                throw new IllegalArgumentException(
                    "Ngân hàng câu hỏi chỉ có " + source.size() + " câu được duyệt" +
                    (request.getTopicId() != null ? " trong chủ đề này" : "") +
                    ". Bạn yêu cầu " + requested + " câu. " +
                    "Hãy thêm câu hỏi hoặc giảm số câu xuống."
                );
            }

            Collections.shuffle(source);
            selected = source.stream().limit(requested).toList();
        }

        Exam exam = new Exam();
        exam.setTeacher(teacher);
        exam.setTitle(request.getTitle());
        exam.setDuration(request.getDuration());
        exam.setTotalQuestions(requested);
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
    return examRepository.findAllWithQuestions()
        .stream()
        .map(this::toResponse)
        .toList();
}

    @Override
    @Transactional(readOnly = true)
    public ExamResponse getById(@NonNull Long examId) {
        Exam exam = getExam(examId);
        ensureOwnerOrAdmin(exam);
        return toResponseWithQuestions(exam);
    }

    @Override
    @Transactional
    public void delete(@NonNull Long examId) {
        Exam exam = getExam(examId);
        ensureOwnerOrAdmin(exam);
        examQuestionRepository.deleteByExam_ExamId(examId);
        examRepository.delete(exam);
    }

    // ==================== PRIVATE ====================

    private @NonNull Exam getExam(@NonNull Long examId) {
        return examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found: " + examId));
    }

    private void ensureOwnerOrAdmin(@NonNull Exam exam) {
        if (currentUserService.hasRole("ADMIN")) return;
        if (!currentUserService.requireUserId().equals(exam.getTeacher().getUserId())) {
            throw new IllegalArgumentException("Bạn chỉ có thể truy cập đề thi của mình");
        }
    }

    /** Trả về thông tin cơ bản (không kèm nội dung câu hỏi) — dùng cho list */
    private ExamResponse toResponse(Exam exam) {
        List<Long> questionIds = examQuestionRepository
                .findByExam_ExamIdOrderByOrderIndexAsc(exam.getExamId())
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

    /** Trả về kèm nội dung câu hỏi đầy đủ — dùng cho getById */
    private ExamResponse toResponseWithQuestions(Exam exam) {
        List<ExamQuestion> links = examQuestionRepository
                .findByExam_ExamIdOrderByOrderIndexAsc(exam.getExamId());

        List<Long> questionIds = links.stream()
                .map(link -> link.getQuestion().getQuestionId())
                .toList();

        List<QuestionResponse> questions = links.stream()
                .map(link -> {
                    Question q = link.getQuestion();
                    List<QuestionChoiceResponse> choices = q.getChoices().stream()
                            .map(ch -> QuestionChoiceResponse.builder()
                                    .questionChoiceId(ch.getQuestionChoiceId())
                                    .content(ch.getContent())
                                    .correct(Boolean.TRUE.equals(ch.getIsCorrect()))
                                    .build())
                            .toList();
                    return QuestionResponse.builder()
                            .questionId(q.getQuestionId())
                            .topicId(q.getTopic().getTopicId())
                            .topicName(q.getTopic().getName())
                            .createdByUserId(q.getCreatedBy().getUserId())
                            .content(q.getContent())
                            .type(q.getType())
                            .difficulty(q.getDifficulty())
                            .status(q.getStatus())
                            .createdAt(q.getCreatedAt())
                            .choices(choices)
                            .build();
                })
                .toList();

        return ExamResponse.builder()
                .examId(exam.getExamId())
                .title(exam.getTitle())
                .duration(exam.getDuration())
                .totalQuestions(exam.getTotalQuestions())
                .teacherId(exam.getTeacher().getUserId())
                .createdAt(exam.getCreatedAt())
                .questionIds(questionIds)
                .questions(questions)
                .build();
    }
}