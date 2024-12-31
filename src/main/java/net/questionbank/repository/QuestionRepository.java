package net.questionbank.repository;

import net.questionbank.domain.Question;
import net.questionbank.domain.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findAllByTestOrderByItemNo(Test test);
}
