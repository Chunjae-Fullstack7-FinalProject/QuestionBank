package net.questionbank.dto.textbook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextbookApiResponse {
    private String successYn;
    private List<TextbookDetailDTO> subjectInfoList;
    private String errCode;
    private String errorMessage;
}
