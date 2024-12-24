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
public class QuestionResponseDTO {
    private String successYn;
    private List<QuestionImageApiDTO> itemImageList;
    private List<QuestionHtmlApiDTO> itemList;
}
