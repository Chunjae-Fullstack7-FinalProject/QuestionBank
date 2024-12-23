package net.questionbank.service.textbook;

import net.questionbank.dto.main.MainDTO;
import net.questionbank.dto.textbook.TextbookApiResponse;
import net.questionbank.dto.textbook.TextbookDetailDTO;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TextbookServiceIf {
    List<MainDTO> mainList(String subjectId);
    TextbookDetailDTO getTextbookDetails(String subjectId);
}
