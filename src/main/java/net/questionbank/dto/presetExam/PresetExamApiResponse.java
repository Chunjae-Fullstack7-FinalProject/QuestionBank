package net.questionbank.dto.presetExam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresetExamApiResponse {
    private String successYn;
    private List<PresetExamResponseDTO> examList;
    private String errCode;
    private String errorMessage;
}
