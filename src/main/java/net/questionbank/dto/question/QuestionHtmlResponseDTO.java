package net.questionbank.dto.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionHtmlResponseDTO {
    private String successYn;
    private List<QuestionHtmlApiDTO> itemList;
}
