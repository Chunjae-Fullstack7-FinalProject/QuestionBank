package net.questionbank.dto.presetExam;

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
public class PresetExamResponseDTO {
    private int largeChapterId; //대단원 코드
    private String largeChapterName; //대단원명
    private int examId; //시험지번호
    private String examName; //시험지명
    private int itemCnt; //문항수
}
/*
            "largeChapterId": 113801,
            "largeChapterName": "Ⅰ. 제곱근과 실수",
            "examId": 1770,
            "examName": "중_수학3(류)_1-1-1_형성평가_1회_T셀파",
            "itemCnt": 10
 */