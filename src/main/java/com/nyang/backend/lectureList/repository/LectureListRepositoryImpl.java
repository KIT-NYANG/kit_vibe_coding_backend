package com.nyang.backend.lectureList.repository;

import com.nyang.backend.lectureList.dto.LectureListResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LectureListRepositoryImpl implements LectureListRepositoryCustom {

    private final JPAQueryFactory queryFactory; // Querydsl 쿼리 만드는 도구

    @Override
    public List<LectureListResponseDto> findLectureListsByUserId(Long userId) {
        // Querydsl용 엔티티 메타객체
        QLectureList lectureList = QLectureList.lectureList;
        QLecture lecture = QLecture.lecture;

        return queryFactory
                .select(Projections.constructor( // 조회 결과를 LectureListResponseDto에 담음
                        LectureListResponseDto.class,
                        lectureList.lectureListId,
                        lecture.lectureId,
                        lecture.title,
                        lecture.thumbnail,
                        lectureList.progressPercent,
                        lectureList.watchTimeSeconds,
                        lectureList.startedAt,
                        lectureList.completedAt
                ))
                .from(lectureList)
                .join(lectureList.lecture, lecture) // lecture_list.lecture_id = lecture.lecture_id로 조인
                .where(
                        lectureList.users.userId.eq(userId), // 해당 userId의 수강 목록만 가져오고
                        lectureList.isDeleted.eq(false) // 삭제되지 않은 것만 가져오라는 뜻입니다.
                )
                .fetch(); // 최종적으로 List<LectureListResponseDto> 반환
    }
}
