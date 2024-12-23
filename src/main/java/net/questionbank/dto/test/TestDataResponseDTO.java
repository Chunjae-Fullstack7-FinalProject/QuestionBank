package net.questionbank.dto.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDataResponseDTO {
     private Boolean success; //성공 여부
     private String message; //성공 또는 에러 메시지
}
