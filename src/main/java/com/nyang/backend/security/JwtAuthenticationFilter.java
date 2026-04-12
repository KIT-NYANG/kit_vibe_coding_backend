package com.nyang.backend.security;

import com.nyang.backend.global.exception.BusinessException;
import com.nyang.backend.global.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Authorization 헤더 추출
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken)) {
            if (!bearerToken.startsWith("Bearer ")) {
                throw new BusinessException(ErrorCode.INVALID_TOKEN);
            }

            // "Bearer " 제거 후 실제 토큰 추출
            String token = bearerToken.substring(7);

            if (!jwtTokenProvider.validateToken(token)) {
                throw new BusinessException(ErrorCode.INVALID_TOKEN);
            }

            // 토큰에서 email 추출
            String email = jwtTokenProvider.getEmail(token);
            // email로 사용자 정보 조회
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            // 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication); // 인증 정보 저장
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}