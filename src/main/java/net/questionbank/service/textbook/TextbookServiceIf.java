package net.questionbank.service.textbook;

import net.questionbank.dto.main.MainDTO;
import net.questionbank.dto.textbook.TextBookApiDTO;

import java.util.List;

public interface TextbookServiceIf {
    List<MainDTO> mainList(String subjectId);
    TextBookApiDTO getTextbookDetails(String subjectId);
}
