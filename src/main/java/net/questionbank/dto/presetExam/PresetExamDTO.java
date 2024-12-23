package net.questionbank.dto.presetExam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
@Data
public class PresetExamDTO {
    private int examId; //시험지번호
    private String examName; //시험지명
    private int itemCnt; //문항수
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PresetExamDTO that = (PresetExamDTO) obj;
        return examId == that.examId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(examId);
    }
}
