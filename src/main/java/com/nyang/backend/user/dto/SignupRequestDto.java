package com.nyang.backend.user.dto;

import com.nyang.backend.user.entity.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequestDto {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String passwordConfirm;

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 30, message = "이름은 10자 이하로 입력해주세요.")
    private String name;

    @NotNull(message = "나이는 필수입니다.")
    private Integer age;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phone;

    @NotNull(message = "역할은 필수입니다.")
    private Role role;
}