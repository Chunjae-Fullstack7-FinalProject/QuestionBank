package net.questionbank.dto.main;

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
public class ResponseDTO {
    private String status;
    private Object data;
}
