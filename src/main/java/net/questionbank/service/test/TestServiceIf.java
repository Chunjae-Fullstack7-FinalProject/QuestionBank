package net.questionbank.service.test;

import net.questionbank.dto.presetExam.LargeChapterDTO;
import net.questionbank.dto.presetExam.PresetExamApiResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TestServiceIf {
    Mono<PresetExamApiResponse> getPresetExam(String subjectId);
    List<LargeChapterDTO> getPresetExamList(String subjectId);
    String[] getPresetExamQuestions(String[] examIds);
}
