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
public class TextbookDetailDTO {
    private Integer subjectId; //교재코드 ex)1167
    private String subjectName; //교재명 ex)"사회②(구정화)"
    private String curriculumName; //교육과정명 ex)"2015개정 교육과정"
    private String areaCode;  //과목코드 ex) "SO"
    private String areaName; //과목명 ex) "사회"

}

/*
            "subjectId": 1167,
            "subjectName": "사회②(구정화)",
            "curriculumCode": "15",
            "curriculumName": "2015개정 교육과정",
            "schoolLevelCode": "M0",
            "schoolLevelName": "중등",
            "gradeCode": "08",
            "gradeName": "중2",
            "termCode": "99",
            "termName": "0학기",
            "areaCode": "SO",
            "areaName": "사회"
 */