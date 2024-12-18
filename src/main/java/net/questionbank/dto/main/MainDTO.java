package net.questionbank.dto.main;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.dto.textbook.TextbookDTO;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
@Data
public class MainDTO {
    private String authorName;
    private List<TextbookDTO> textbooks;
}
