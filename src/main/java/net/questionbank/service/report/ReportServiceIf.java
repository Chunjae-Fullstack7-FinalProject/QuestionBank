package net.questionbank.service.report;

import net.questionbank.domain.Report;
import net.questionbank.dto.report.ReportRegisterDTO;

public interface ReportServiceIf {
    Report register(ReportRegisterDTO registerDTO);
}
