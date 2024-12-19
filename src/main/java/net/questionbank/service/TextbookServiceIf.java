package net.questionbank.service;

import net.questionbank.dto.main.MainDTO;

import java.util.List;

public interface TextbookServiceIf {
    List<MainDTO> mainList(String subjectId);
}
