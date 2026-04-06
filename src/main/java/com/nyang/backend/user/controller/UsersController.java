package com.nyang.backend.user.controller;

import com.nyang.backend.global.response.ResponseDto;
import com.nyang.backend.user.dto.LoginRequestDto;
import com.nyang.backend.user.dto.LoginResponseDto;
import com.nyang.backend.user.dto.SignupRequestDto;
import com.nyang.backend.user.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto requestDto) {
        LoginResponseDto result = usersService.login(requestDto);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<String>> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        String result = usersService.signup(requestDto);
        return ResponseEntity.ok(ResponseDto.success(result));
    }

}
