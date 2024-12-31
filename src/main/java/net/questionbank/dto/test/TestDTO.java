package net.questionbank.dto.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDTO {
    private int testId;
    private String testTitle;
    private LocalDateTime createdAt;
    private String filePath;
    private String textbookTitle;
    private String author;
    private int count;
}
