package net.questionbank.dto.test;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.questionbank.domain.Member;
import net.questionbank.domain.Textbook;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDTO {
    private int testId;
    private String title;
    private LocalDateTime createdAt;
    private Long subjectId;
    private String userId;
    private String userName;
}
