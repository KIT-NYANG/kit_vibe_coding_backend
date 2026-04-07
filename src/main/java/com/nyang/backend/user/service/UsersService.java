package com.nyang.backend.user.service;

import com.nyang.backend.global.exception.BusinessException;
import com.nyang.backend.global.exception.ErrorCode;
import com.nyang.backend.security.JwtTokenProvider;
import com.nyang.backend.user.dto.LoginRequestDto;
import com.nyang.backend.user.dto.LoginResponseDto;
import com.nyang.backend.user.dto.SignupRequestDto;
import com.nyang.backend.user.entity.Users;
import com.nyang.backend.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public String signup(SignupRequestDto requestDto) {
        // 이메일 중복 검사
        if (usersRepository.existsByEmail(requestDto.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
        }

        Users users = Users.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword())) // 암호화해서 저장
                .name(requestDto.getName())
                .age(requestDto.getAge())
                .phone(requestDto.getPhone())
                .role(requestDto.getRole())
                .build();

        usersRepository.save(users);
        return "회원가입 성공";
    }

    // 이메일 중복 검사 메서드 분리
    @Transactional
    public String checkEmailDuplicate(String email) {
        if (usersRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
        }
        return "사용 가능한 이메일입니다.";
    }

    public LoginResponseDto login(LoginRequestDto requestDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Users users = usersRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // JWT 생성
        String accessToken = jwtTokenProvider.createToken(
                users.getEmail(),
                users.getRole().name()
        );

        return new LoginResponseDto(users, accessToken);
    }
}