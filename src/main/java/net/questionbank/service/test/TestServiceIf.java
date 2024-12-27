package net.questionbank.service.test;

import net.questionbank.dto.main.SubjectRequestDTO;
import net.questionbank.dto.test.LargeDTO;
import net.questionbank.dto.test.Step1DTO;
import net.questionbank.dto.presetExam.LargeChapterDTO;
import net.questionbank.dto.presetExam.PresetExamApiResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TestServiceIf {
    List<LargeDTO> step1(SubjectRequestDTO subjectRequestDTO);
    Mono<PresetExamApiResponse> getPresetExam(String subjectId);
    List<LargeChapterDTO> getPresetExamList(String subjectId);
    String[] getPresetExamQuestions(String[] examIds);
    List<Long> getPresetExamItemList(String examId);
}
