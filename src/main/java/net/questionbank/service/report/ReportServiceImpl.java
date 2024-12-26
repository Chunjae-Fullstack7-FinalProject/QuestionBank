package net.questionbank.service.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.annotation.Logging;
import net.questionbank.domain.File;
import net.questionbank.domain.Member;
import net.questionbank.domain.Report;
import net.questionbank.dto.report.ReportRegisterDTO;
import net.questionbank.exception.CustomRuntimeException;
import net.questionbank.repository.FileRepository;
import net.questionbank.repository.ReportRepository;
import net.questionbank.util.FileUploadUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Log4j2
@Logging
@Transactional
public class ReportServiceImpl implements ReportServiceIf{
    private final ReportRepository reportRepository;
    private final FileRepository fileRepository;
    private final FileUploadUtil fileUploadUtil;

    @Override
    public Report register(ReportRegisterDTO registerDTO) {
        try {
            if (registerDTO.getFile() != null) {
                String FILE_UPLOAD_SUBPATH = "report";
                String filePath = fileUploadUtil.uploadReportFile(registerDTO.getFile(), FILE_UPLOAD_SUBPATH);
                registerDTO.setNewFilePath(filePath);
            }
            Report report = reportRepository.save(
                    Report.builder()
                            .type(registerDTO.getType())
                            .description(registerDTO.getDescription())
                            .itemId(registerDTO.getItemId())
                            .member(Member.builder().memberId(registerDTO.getMemberId()).build())
                            .build()
            );
            fileRepository.save(File.builder()
                    .filePath(registerDTO.getNewFilePath())
                    .originalFileName(registerDTO.getFile().getName())
                    .report(report)
                    .build()
            );
            return report;
        }catch (Exception e){
            log.error(e);
            throw new CustomRuntimeException(e.getMessage());
        }
    }
}
