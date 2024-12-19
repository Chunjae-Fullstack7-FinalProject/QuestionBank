package net.questionbank.dto.textbook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
@Data
public class TextbookDTO {
    private Integer textbookId;
    private String title;
    private String author;
    private String subjectId;
}
