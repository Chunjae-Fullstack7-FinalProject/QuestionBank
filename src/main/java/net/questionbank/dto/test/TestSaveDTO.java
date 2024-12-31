package net.questionbank.dto.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestSaveDTO {
    private int testId;
    private String title;
    private LocalDateTime createdAt;
    private Long subjectId; //교재 아이디
    private String subjectName; //과목 이름
    private String userId; //만든사람 아이디
    private String userName; //만든사람 이름
    private List<Long> itemIdList; //문항 아이디 리스트
    private String pdfFileId; //파일 uuid

    private boolean descriptive; // 서술형 유무
}
