package net.questionbank.dto.test;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.questionbank.domain.Member;
import net.questionbank.domain.Textbook;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDTO {
    private int testId;
    private String title;
    private LocalDateTime createdAt;
    private Long subjectId; //교재 아이디
    private String subjectName; //과목 이름
    private String userId; //만든사람 아이디
    private String userName; //만든사람 이름
    private List<Long> itemIdList; //문항 아이디 리스트
    private String pdfFileId; //파일 uuid
}
