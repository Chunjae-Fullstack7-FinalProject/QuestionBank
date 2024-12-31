package net.questionbank.repository;

import net.questionbank.domain.Member;
import net.questionbank.domain.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestRepository extends JpaRepository<Test, Integer> {
    @Query("select T.filePath from Test T where T.testId = :id")
    String findFilePathById(int id);
    List<Test> findAllByMember(Member member);
}