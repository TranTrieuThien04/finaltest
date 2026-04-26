package com.planbookai.repository;

import com.planbookai.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByTopic_TopicId(Long topicId);
}
