package com.nyang.backend.security;

import com.nyang.backend.global.exception.BusinessException;
import com.nyang.backend.global.exception.ErrorCode;
import com.nyang.backend.user.entity.Users;
import com.nyang.backend.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // email로 사용자 조회
        Users users = usersRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // UserDetails 객체로 변환
        return new org.springframework.security.core.userdetails.User(
                users.getEmail(),
                users.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + users.getRole().name()))
        );
    }
}
