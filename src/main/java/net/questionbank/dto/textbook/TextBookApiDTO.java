package net.questionbank.dto.textbook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextBookApiDTO {
    private Long subjectId;
    private String subjectName;
    private String curriculumCode;
    private String curriculumName;
    private String schoolLevelCode;
    private String schoolLevelName;
    private String gradeCode;
    private String gradeName;
    private String termCode;
    private String termName;
    private String areaCode;
    private String areaName;
}
