package net.questionbank.dto.report;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import net.questionbank.domain.Member;
import org.springframework.web.multipart.MultipartFile;

@Valid
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportRegisterDTO {
    private int reportId;
    @NotBlank(message = "오류 분류 값은 필수입니다.")
    @Pattern(regexp = "question|answer|explain|image|etc", message = "허용되지 않는 오류 분류입니다.")
    private String type;
    @NotBlank(message = "오류 신고 내용 작성은 필수입니다.")
    @Size(max = 50, message = "내용은 50자까지만 작성가능합니다.")
    private String description;
    private String memberId;
    @Min(0)
    private int itemId;
    private MultipartFile file;
    private String newFilePath;
}
