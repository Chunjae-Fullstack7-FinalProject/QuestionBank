package net.questionbank.dto.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
@Data
public class MinorClassificationDTO {
    private String subject;
    private String large;
    private String medium;
    private String small;
    private String topic;
}
