package net.questionbank.dto.textbook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextBookResponseDTO {
    private String successYn;
    private List<TextBookApiDTO> subjectInfoList;
}
