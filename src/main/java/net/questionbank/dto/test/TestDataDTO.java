package net.questionbank.dto.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDataDTO {
    private Long examId; //시험아이디
    private String examName; //시험이름
    private String teacherId; //선생님Id
    private String teacherName; //선생님 이름
    private String subjectName; //과목 이름
    private List<Long> itemList; //문항아이디 리스트
}
