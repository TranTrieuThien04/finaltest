package com.planbookai.service.impl;

import com.planbookai.dto.question.QuestionChoiceCreateRequest;
import com.planbookai.dto.question.QuestionChoiceResponse;
import com.planbookai.dto.question.QuestionCreateRequest;
import com.planbookai.dto.question.QuestionResponse;
import com.planbookai.dto.question.QuestionUpdateRequest;
import com.planbookai.entity.Question;
import com.planbookai.entity.QuestionChoice;
import com.planbookai.entity.Topic;
import com.planbookai.entity.User;
import com.planbookai.entity.enums.QuestionStatus;
import com.planbookai.entity.enums.QuestionType;
import com.planbookai.repository.QuestionRepository;
import com.planbookai.repository.TopicRepository;
import com.planbookai.repository.UserRepository;
import com.planbookai.security.CurrentUserService;
import com.planbookai.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

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
        entity.setStatus(QuestionStatus.PENDING);
        entity.setCreatedAt(LocalDateTime.now());

        for (QuestionChoiceCreateRequest c : request.getChoices()) {
            QuestionChoice choice = new QuestionChoice();
            choice.setQuestion(entity);
            choice.setContent(c.getContent());
            choice.setIsCorrect(c.getCorrect());
            entity.getChoices().add(choice);
        }

        Question saved = questionRepository.save(entity);
        return toResponse(saved);
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
        for (QuestionChoiceCreateRequest c : request.getChoices()) {
            QuestionChoice choice = new QuestionChoice();
            choice.setQuestion(question);
            choice.setContent(c.getContent());
            choice.setIsCorrect(c.getCorrect());
            question.getChoices().add(choice);
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

    private @NonNull Question getQuestion(@NonNull Long questionId) {
        return Objects.requireNonNull(
                questionRepository.findById(questionId)
                        .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> filter(Long topicId, Long subjectId) {
        if (topicId != null) {
            return questionRepository.findByTopic_TopicId(topicId).stream().map(this::toResponse).toList();
        }
        if (subjectId != null) {
            return questionRepository.findAll().stream()
                    .filter(q -> q.getTopic() != null && q.getTopic().getSubject() != null
                            && subjectId.equals(q.getTopic().getSubject().getSubjectId()))
                    .map(this::toResponse)
                    .toList();
        }
        return findAll();
    }

    private void ensureOwnerOrAdmin(Question question) {
        Long currentUserId = currentUserService.requireUserId();
        if (currentUserService.hasRole("ADMIN")) {
            return;
        }
        if (!currentUserId.equals(question.getCreatedBy().getUserId())) {
            throw new IllegalArgumentException("You can only modify your own question");
        }
    }

    private void validateChoicesForType(QuestionType type, List<QuestionChoiceCreateRequest> choices) {
        if (type == QuestionType.MCQ) {
            if (choices == null || choices.isEmpty()) {
                throw new IllegalArgumentException("MCQ questions require at least one choice");
            }
            long correct = choices.stream().filter(c -> Boolean.TRUE.equals(c.getCorrect())).count();
            if (correct != 1) {
                throw new IllegalArgumentException("MCQ must have exactly one correct choice");
            }
        } else if (choices != null && !choices.isEmpty()) {
            throw new IllegalArgumentException("Choices are only allowed for MCQ type");
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
