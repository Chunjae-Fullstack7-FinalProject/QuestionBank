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
    @Pattern(regexp = "^[a-z0-9]{5,20}$", message = "회원 ID는 영어 소문자 또는 숫자만을 포함하고, 5~20자여야합니다.")
    private String memberId; // 회원 ID

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()-+=])[a-zA-Z\\d!@#$%^&*()-+=]{8,20}$", message = "비밀번호는 영어, 숫자, 특수문자를 포함해야 하며, 8~20자여야 합니다.")
    private String pwd;      // 비밀번호

    @NotBlank(message = "이름은 필수입니다.")
    @Pattern(regexp = "^[가-힣]{2,5}$", message="이름은 한글로만 2~5자여야합니다.")
    private String name;     // 이름

    @NotBlank(message = "이메일은 필수입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]{1,20}@[a-zA-Z0-9.-]{1,20}(\\.[a-zA-Z]{2,3})+$", message = "올바른 이메일형식이 아닙니다.")
    private String email;    // 이메일
}
