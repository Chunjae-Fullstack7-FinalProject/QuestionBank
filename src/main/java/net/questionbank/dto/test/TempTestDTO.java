package net.questionbank.dto.test;

import lombok.*;
import net.questionbank.dto.question.QuestionImageApiDTO;
import net.questionbank.dto.textbook.TextBookApiDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempTestDTO {
    private TextBookApiDTO textbookApiDTO;
    private List<QuestionImageApiDTO> questions;
    private Map<String, Integer> code;
    private Map<String,List<String>> imageList;
//    private List<String> imageList2;
//    private List<String> imageList3;

    public Map<String, Integer> getCode() {
        if(code != null) { return code; }
        this.code = new HashMap<>();
        questions.forEach(question -> {
            this.code.put(question.getDifficultyCode(), this.code.getOrDefault(question.getDifficultyCode(), 0) + 1);
            String formCode = "12";
            if (question.getQuestionFormCode().compareTo("10") >= 0 && question.getQuestionFormCode().compareTo("50") <= 0) {
                formCode = "11";
            }

//            else if (question.getQuestionFormCode() >= '60' && question.getQuestionFormCode() <= '99') {
//                formCode = "11";
//            } else {
//                formCode = "11";// 다른 형식이 있다면 여기에 추가
//            }

            this.code.put(formCode, this.code.getOrDefault(formCode, 0) + 1);
        });
        return this.code;
    }
}
