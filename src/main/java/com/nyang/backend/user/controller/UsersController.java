package com.nyang.backend.user.controller;

import com.nyang.backend.global.exception.SuccessCode;
import com.nyang.backend.global.response.ResponseDto;
import com.nyang.backend.user.dto.LoginRequestDto;
import com.nyang.backend.user.dto.LoginResponseDto;
import com.nyang.backend.user.dto.SignupRequestDto;
import com.nyang.backend.user.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    // 로그인 api
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto requestDto) {
        LoginResponseDto result = usersService.login(requestDto);
        return ResponseEntity.ok(ResponseDto.success(SuccessCode.OK, result));
    }

    // 회원가입 api
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<String>> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        String result = usersService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success(SuccessCode.CREATED, result));
    }

    // 이메일 중복 확인 api
    @GetMapping("/check-email")
    public ResponseEntity<ResponseDto<String>> checkEmailDuplicate(@RequestParam String email) {
        return ResponseEntity.ok(ResponseDto.success(SuccessCode.OK, usersService.checkEmailDuplicate(email)));
    }
}
