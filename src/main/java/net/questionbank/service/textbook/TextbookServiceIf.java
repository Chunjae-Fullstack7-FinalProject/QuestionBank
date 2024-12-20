package net.questionbank.service.textbook;

import net.questionbank.dto.main.MainDTO;

import java.util.List;

public interface TextbookServiceIf {
    List<MainDTO> mainList(String subjectId);
}
