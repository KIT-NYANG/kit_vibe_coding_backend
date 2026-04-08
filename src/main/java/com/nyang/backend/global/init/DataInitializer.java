package com.nyang.backend.global.init;

import com.nyang.backend.user.entity.Role;
import com.nyang.backend.user.entity.Users;
import com.nyang.backend.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.nyang.backend.lecture.entity.Lecture;
import com.nyang.backend.lecture.repository.LectureRepository;
import com.nyang.backend.lectureClass.entity.LectureClass;
import com.nyang.backend.lectureClass.repository.LectureClassRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsersRepository usersRepository;
    private final LectureClassRepository lectureClassRepository;
    private final LectureRepository lectureRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        Users teacher = usersRepository.findByEmail("teacher@test.com").orElse(null);

        // 선생님 계정
        if (teacher == null) {
            teacher = usersRepository.save(
                    Users.builder()
                            .email("teacher@test.com")
                            .password(passwordEncoder.encode("1234"))
                            .name("선생님")
                            .age(30)
                            .phone("010-1111-1111")
                            .role(Role.TEACHER)
                            .build()
            );
        }

        Users student = usersRepository.findByEmail("student@test.com").orElse(null);

        // 학생 계정
        if (student == null) {
            student = usersRepository.save(
                    Users.builder()
                            .email("student@test.com")
                            .password(passwordEncoder.encode("1234"))
                            .name("학생")
                            .age(20)
                            .phone("010-2222-2222")
                            .role(Role.STUDENT)
                            .build()
            );
        }
        Users user = Users.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("string"))
                .name("선생2")
                .age(20)
                .phone("010-2222-2222")
                .role(Role.TEACHER)
                .build();
        usersRepository.save(user);

        if (lectureClassRepository.count() > 0 || lectureRepository.count() > 0) {
            return;
        }

        List<LectureClassSeed> lectureClassSeeds = List.of(
                new LectureClassSeed("스프링 부트 입문", "백엔드", "스프링 부트 기초를 배우는 강좌", "/test/thumbnails/class1.jpg"),
                new LectureClassSeed("자바 객체지향 프로그래밍", "백엔드", "자바 OOP 핵심 개념을 배우는 강좌", "/test/thumbnails/class2.jpg"),
                new LectureClassSeed("웹 보안 기초", "보안", "웹 보안의 기본 개념을 배우는 강좌", "/test/thumbnails/class3.jpg"),
                new LectureClassSeed("데이터베이스 입문", "백엔드", "데이터베이스와 SQL 기초 강좌", "/test/thumbnails/class4.jpg"),
                new LectureClassSeed("리눅스 기초", "인프라", "리눅스 기본 명령어와 환경 설정", "/test/thumbnails/class5.jpg"),
                new LectureClassSeed("네트워크 개론", "인프라", "네트워크의 기본 구조와 원리", "/test/thumbnails/class6.jpg"),
                new LectureClassSeed("알고리즘 기초", "CS", "기초 알고리즘 문제 해결", "/test/thumbnails/class7.jpg"),
                new LectureClassSeed("자료구조 입문", "CS", "스택, 큐, 트리 등 자료구조 학습", "/test/thumbnails/class8.jpg"),
                new LectureClassSeed("프론트엔드 HTML/CSS", "프론트엔드", "웹 화면 구성의 기초", "/test/thumbnails/class9.jpg"),
                new LectureClassSeed("자바스크립트 입문", "프론트엔드", "자바스크립트 기본 문법과 DOM", "/test/thumbnails/class10.jpg")
        );

        List<LectureClass> savedLectureClasses = new ArrayList<>();

        for (LectureClassSeed seed : lectureClassSeeds) {
            LectureClass savedLectureClass = lectureClassRepository.save(
                    LectureClass.create(
                            teacher,
                            seed.title(),
                            seed.category(),
                            seed.description(),
                            seed.thumbnailPath()
                    )
            );
            savedLectureClasses.add(savedLectureClass);
        }

        int lectureNumber = 1;

        for (LectureClass lectureClass : savedLectureClasses) {
            for (int i = 1; i <= 3; i++) {
                lectureRepository.save(
                        Lecture.create(
                                teacher,
                                lectureClass.getTitle() + " " + i + "강",
                                lectureClass.getTitle() + "의 " + i + "강 설명",
                                lectureClass,
                                500 + (i * 100),
                                "/test/videos/lecture-" + lectureNumber + ".mp4",
                                "/test/thumbnails/lecture-" + lectureNumber + ".jpg"
                        )
                );
                lectureNumber++;
            }
        }
    }

    private record LectureClassSeed(
            String title,
            String category,
            String description,
            String thumbnailPath
    ) {
    }
}