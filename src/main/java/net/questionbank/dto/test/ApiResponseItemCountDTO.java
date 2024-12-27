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
public class ApiResponseItemCountDTO {

    private List<SmallItemCount> listSmallItemCount;
    private List<TopicItemCount> listTopicItemCount;

    // getters and setters

    @Data
    public static class SmallItemCount {
        private Long smallChapterId;
        private Integer itemCount;


    }

    @Data
    public static class TopicItemCount {
        private Long topicChapterId;
        private Integer itemCount;

    }
}

