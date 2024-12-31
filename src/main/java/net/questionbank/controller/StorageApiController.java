package net.questionbank.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.dto.main.SubjectRequestDTO;
import net.questionbank.dto.textbook.TextbookDTO;
import net.questionbank.service.storage.StorageServiceIf;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/storage")
public class StorageApiController {
    private final StorageServiceIf storageService;

    @PostMapping("/textbook")
    public ResponseEntity<?> textbookList(@RequestBody SubjectRequestDTO subjectRequestDTO) {
        List<TextbookDTO> list = storageService.getTextbookBySubjectId(subjectRequestDTO.getSubjectId());
        return ResponseEntity.ok().body(list);
    }
}
