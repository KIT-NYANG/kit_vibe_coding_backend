package com.nyang.backend.global.init;

import com.nyang.backend.user.entity.Role;
import com.nyang.backend.user.entity.Users;
import com.nyang.backend.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // 선생님 계정
        if (!usersRepository.existsByEmail("teacher@test.com")) {
            Users teacher = Users.builder()
                    .email("teacher@test.com")
                    .password(passwordEncoder.encode("1234"))
                    .name("선생님")
                    .age(30)
                    .phone("010-1111-1111")
                    .role(Role.TEACHER)
                    .build();

            usersRepository.save(teacher);
        }

        // 학생 계정
        if (!usersRepository.existsByEmail("student@test.com")) {
            Users student = Users.builder()
                    .email("student@test.com")
                    .password(passwordEncoder.encode("1234"))
                    .name("학생")
                    .age(20)
                    .phone("010-2222-2222")
                    .role(Role.STUDENT)
                    .build();

            usersRepository.save(student);
        }
    }
}