package net.questionbank.service.test;


import net.questionbank.dto.main.SubjectRequestDTO;
import net.questionbank.dto.test.LargeDTO;
import net.questionbank.dto.test.Step1DTO;

import java.util.List;

public interface TestServiceIf {
    List<LargeDTO> step1(SubjectRequestDTO subjectRequestDTO);
}
