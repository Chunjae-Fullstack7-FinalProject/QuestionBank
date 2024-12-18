package net.questionbank.dto;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import net.questionbank.domain.Member;
import net.questionbank.domain.Question;
import net.questionbank.domain.Textbook;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDTO {
    private int testId;
    private String title;
    private LocalDateTime createdAt;
    private Textbook textbook;
    private Member member;
    private List<QuestionDTO> questions;
    private Map<String, Integer> code;

    public void countCode() {
        this.code = new HashMap<>();
        questions.forEach(question -> {
            this.code.put(question.getDifficultyCode(), this.code.getOrDefault(question.getDifficultyCode(), 0) + 1);
            this.code.put(question.getQuestionFormCode(), this.code.getOrDefault(question.getQuestionFormCode(), 0) + 1);
        });
    }
}
