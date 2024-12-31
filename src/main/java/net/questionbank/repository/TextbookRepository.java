package net.questionbank.repository;

import net.questionbank.domain.Subject;
import net.questionbank.domain.Textbook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TextbookRepository extends JpaRepository<Textbook, Integer> {
    List<Textbook> findAllBySubject(Subject subject);
}
