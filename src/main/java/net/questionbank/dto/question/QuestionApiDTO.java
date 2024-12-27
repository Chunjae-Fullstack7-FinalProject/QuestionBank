package net.questionbank.dto.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * api 응답 받은 문제 항목 저장 용도
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionApiDTO {
    private int itemNo;
    private Long itemId;
    private String questionFormCode;
    private String questionFormName;
    private String difficultyCode;
    private String difficultyName;
    private Long largeChapterId;
    private String largeChapterName;
    private Long mediumChapterId;
    private String mediumChapterName;
    private Long smallChapterId;
    private String smallChapterName;
    private Long topicChapterId;
    private String topicChapterName;
    private Long passageId;
    private String passageUrl;
    private String questionUrl;
    private String answerUrl;
    private String explainUrl;
}
