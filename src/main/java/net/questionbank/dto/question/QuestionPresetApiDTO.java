package net.questionbank.dto.question;

import lombok.*;

/**
 * api 응답 받은 문제 항목 저장 용도
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionPresetApiDTO extends QuestionApiDTO{
    private Long examId;
    private String examName;
}
