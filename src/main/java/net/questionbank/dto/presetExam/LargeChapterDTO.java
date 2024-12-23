package net.questionbank.dto.presetExam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
@Data
public class LargeChapterDTO {
    private int largeChapterId; //대단원 코드
    private String largeChapterName; //대단원명
    List<PresetExamDTO> presetExams;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LargeChapterDTO that = (LargeChapterDTO) obj;
        return largeChapterId == that.largeChapterId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(largeChapterId);
    }
}
