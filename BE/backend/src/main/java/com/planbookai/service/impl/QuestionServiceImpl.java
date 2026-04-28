package com.planbookai.service.impl;

import com.planbookai.dto.question.QuestionChoiceCreateRequest;
import com.planbookai.dto.question.QuestionChoiceResponse;
import com.planbookai.dto.question.QuestionCreateRequest;
import com.planbookai.dto.question.QuestionResponse;
import com.planbookai.dto.question.QuestionUpdateRequest;
import com.planbookai.entity.Approval;
import com.planbookai.entity.Question;
import com.planbookai.entity.QuestionChoice;
import com.planbookai.entity.Topic;
import com.planbookai.entity.User;
import com.planbookai.entity.enums.ApprovalContentType;
import com.planbookai.entity.enums.ApprovalStatus;
import com.planbookai.entity.enums.QuestionDifficulty;
import com.planbookai.entity.enums.QuestionStatus;
import com.planbookai.entity.enums.QuestionType;
import com.planbookai.repository.ApprovalRepository;
import com.planbookai.repository.QuestionRepository;
import com.planbookai.repository.TopicRepository;
import com.planbookai.repository.UserRepository;
import com.planbookai.security.CurrentUserService;
import com.planbookai.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final ApprovalRepository approvalRepository;   // FIX: audit trail

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> findAll() {
        return questionRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuestionResponse> findById(@NonNull Long questionId) {
        return questionRepository.findById(questionId).map(this::toResponse);
    }

    @Override
    @Transactional
    public QuestionResponse create(QuestionCreateRequest request) {
        Long topicId = Objects.requireNonNull(request.getTopicId(), "topicId is required");
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found: " + topicId));
        Long currentUserId = currentUserService.requireUserId();
        User author = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUserId));

        validateChoicesForType(request.getType(), request.getChoices());

        Question entity = new Question();
        entity.setTopic(topic);
        entity.setCreatedBy(author);
        entity.setContent(request.getContent());
        entity.setType(request.getType());
        entity.setDifficulty(request.getDifficulty());
        // ADMIN tạo thì tự duyệt luôn, còn lại PENDING
        entity.setStatus(currentUserService.hasRole("ADMIN")
                ? QuestionStatus.APPROVED
                : QuestionStatus.PENDING);
        entity.setCreatedAt(LocalDateTime.now());

        if (request.getChoices() != null) {
            for (QuestionChoiceCreateRequest c : request.getChoices()) {
                QuestionChoice choice = new QuestionChoice();
                choice.setQuestion(entity);
                choice.setContent(c.getContent());
                choice.setIsCorrect(c.getCorrect());
                entity.getChoices().add(choice);
            }
        }

        return toResponse(questionRepository.save(entity));
    }

    @Override
    @Transactional
    public QuestionResponse update(@NonNull Long questionId, QuestionUpdateRequest request) {
        Question question = getQuestion(questionId);
        ensureOwnerOrAdmin(question);

        Long topicId = Objects.requireNonNull(request.getTopicId(), "topicId is required");
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found: " + topicId));
        validateChoicesForType(request.getType(), request.getChoices());

        question.setTopic(topic);
        question.setContent(request.getContent());
        question.setType(request.getType());
        question.setDifficulty(request.getDifficulty());

        question.getChoices().clear();
        if (request.getChoices() != null) {
            for (QuestionChoiceCreateRequest c : request.getChoices()) {
                QuestionChoice choice = new QuestionChoice();
                choice.setQuestion(question);
                choice.setContent(c.getContent());
                choice.setIsCorrect(c.getCorrect());
                question.getChoices().add(choice);
            }
        }

        return toResponse(questionRepository.save(question));
    }

    @Override
    @Transactional
    public void delete(@NonNull Long questionId) {
        Question question = getQuestion(questionId);
        ensureOwnerOrAdmin(question);
        questionRepository.delete(question);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> filter(Long topicId, Long subjectId,
                                          QuestionStatus status, QuestionDifficulty difficulty) {
        Stream<Question> stream;
        if (topicId != null) {
            stream = questionRepository.findByTopic_TopicId(topicId).stream();
        } else {
            stream = questionRepository.findAll().stream();
        }
        if (subjectId != null) {
            stream = stream.filter(q -> q.getTopic() != null
                    && q.getTopic().getSubject() != null
                    && subjectId.equals(q.getTopic().getSubject().getSubjectId()));
        }
        if (status != null) stream = stream.filter(q -> status.equals(q.getStatus()));
        if (difficulty != null) stream = stream.filter(q -> difficulty.equals(q.getDifficulty()));
        return stream.map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponse> filterPaged(Long topicId, Long subjectId,
                                               QuestionStatus status, QuestionDifficulty difficulty,
                                               Pageable pageable) {
        return questionRepository.filterPaged(topicId, subjectId, status, difficulty, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public QuestionResponse approve(@NonNull Long questionId) {
        Question question = getQuestion(questionId);
        question.setStatus(QuestionStatus.APPROVED);
        questionRepository.save(question);
        // FIX: ghi audit trail vào bảng approval
        writeApprovalRecord(questionId, ApprovalStatus.APPROVED, null);
        return toResponse(question);
    }

    @Override
    @Transactional
    public QuestionResponse reject(@NonNull Long questionId) {
        Question question = getQuestion(questionId);
        question.setStatus(QuestionStatus.REJECTED);
        questionRepository.save(question);
        // FIX: ghi audit trail vào bảng approval
        writeApprovalRecord(questionId, ApprovalStatus.REJECTED, null);
        return toResponse(question);
    }

    // ==================== PRIVATE ====================

    /**
     * Ghi record vào bảng approval để lưu audit trail.
     */
    private void writeApprovalRecord(Long contentId, ApprovalStatus status, String comment) {
        Long approverId = currentUserService.requireUserId();
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new IllegalArgumentException("Approver not found: " + approverId));

        Approval approval = new Approval();
        approval.setApprover(approver);
        approval.setContentId(contentId);
        approval.setContentType(ApprovalContentType.QUESTION);
        approval.setStatus(status);
        approval.setComment(comment);
        approval.setCreatedAt(LocalDateTime.now());
        approvalRepository.save(approval);
    }

    private @NonNull Question getQuestion(@NonNull Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));
    }

    private void ensureOwnerOrAdmin(Question question) {
        if (currentUserService.hasRole("ADMIN")) return;
        Long currentUserId = currentUserService.requireUserId();
        if (!currentUserId.equals(question.getCreatedBy().getUserId())) {
            throw new IllegalArgumentException("Bạn chỉ có thể chỉnh sửa câu hỏi của mình");
        }
    }

    private void validateChoicesForType(QuestionType type, List<QuestionChoiceCreateRequest> choices) {
        if (type == QuestionType.MCQ) {
            if (choices == null || choices.isEmpty()) {
                throw new IllegalArgumentException("Câu hỏi trắc nghiệm cần ít nhất 1 lựa chọn");
            }
            long correctCount = choices.stream()
                    .filter(c -> Boolean.TRUE.equals(c.getCorrect()))
                    .count();
            if (correctCount != 1) {
                throw new IllegalArgumentException("Câu hỏi trắc nghiệm phải có đúng 1 đáp án đúng");
            }
        } else {
            if (choices != null && !choices.isEmpty()) {
                throw new IllegalArgumentException("Chỉ câu hỏi MCQ mới có thể có lựa chọn");
            }
        }
    }

    private QuestionResponse toResponse(Question q) {
        List<QuestionChoiceResponse> choiceDtos = q.getChoices().stream()
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
                .choices(choiceDtos)
                .build();
    }
}
