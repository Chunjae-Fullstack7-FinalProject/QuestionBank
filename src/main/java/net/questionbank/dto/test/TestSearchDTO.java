package net.questionbank.dto.test;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestSearchDTO {
    private String userId;
    private String subject;
    private Integer textbookId;
    @Size(max = 20, message = "20자 이내로 입력해주세요.")
    private String keyword;
}
