package net.questionbank.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Valid
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberRegisterDTO {
    @NotBlank(message = "회원 ID는 필수입니다.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "회원 ID는 영어 소문자와 숫자만 포함할 수 있습니다.")
    private String memberId; // 회원 ID

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{10,20}$", message = "비밀번호는 영어, 숫자, 특수문자를 포함해야 하며, 10~20자여야 합니다.")
    private String pwd;      // 비밀번호

    @NotBlank(message = "이름은 필수입니다.")
    private String name;     // 이름

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;    // 이메일
}
