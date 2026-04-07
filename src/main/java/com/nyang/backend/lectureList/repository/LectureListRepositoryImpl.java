package com.nyang.backend.lectureList.repository;

import com.nyang.backend.lectureClass.entity.QLectureClass;
import com.nyang.backend.lectureList.dto.LectureListResponseDto;
import com.nyang.backend.lectureList.entity.QLectureList;
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
        QLectureClass lectureClass = QLectureClass.lectureClass;

        return queryFactory
                .select(Projections.constructor( // 조회 결과를 LectureListResponseDto에 담음
                        LectureListResponseDto.class,
                        lectureList.lectureListId,
                        lectureClass.lectureClassId,
                        lectureClass.title,
                        lectureClass.category,
                        lectureClass.description,
                        lectureClass.thumbnailPath,
                        lectureList.createdAt
                ))
                .from(lectureList)
                .join(lectureList.lectureClass, lectureClass) // lecture_list.lecture_class_id = lecture_class.lecture_class_id로 조인
                .where(
                        lectureList.users.userId.eq(userId), // 해당 userId의 수강 목록만 가져오고
                        lectureList.isDeleted.eq(false), // 삭제되지 않은 것만 가져오라는 뜻입니다.
                        lectureClass.isDeleted.eq(false)
                )
                .fetch(); // 최종적으로 List<LectureListResponseDto> 반환
    }
}
