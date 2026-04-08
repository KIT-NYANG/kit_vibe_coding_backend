package com.nyang.backend.lectureList.repository;

import com.nyang.backend.lectureClass.entity.QLectureClass;
import com.nyang.backend.lectureList.dto.MyLectureListResponseDto;
import com.nyang.backend.lectureList.entity.QLectureList;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class LectureListRepositoryImpl implements LectureListRepositoryCustom {

    private final JPAQueryFactory queryFactory; // Querydsl 쿼리 만드는 도구

    @Override
    public Page<MyLectureListResponseDto> findLectureListsByUserId(
            Long userId,
            String category,
            String keyword,
            Pageable pageable
    ) {
        // Querydsl용 엔티티 메타객체
        QLectureList lectureList = QLectureList.lectureList;
        QLectureClass lectureClass = QLectureClass.lectureClass;

        // category 값이 있으면 category로 필터링
        BooleanExpression categoryCondition = hasText(category) ? lectureClass.category.eq(category) : null;
        // keyword 값이 있으면 title에 keyword가 포함된 강좌만 조회
        BooleanExpression keywordCondition = hasText(keyword) ? lectureClass.title.contains(keyword) : null;

        // 실제 페이지 내용(content) 조회
        List<MyLectureListResponseDto> content = queryFactory
                .select(Projections.constructor( // 조회 결과를 LectureListResponseDto에 담음
                        MyLectureListResponseDto.class,
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
                        lectureClass.isDeleted.eq(false),
                        categoryCondition, // 카테고리 조건
                        keywordCondition // 검색어 조건
                )
                .orderBy(lectureList.createdAt.desc()) // 최근 신청한 강좌가 위로 오도록 정렬
                .offset(pageable.getOffset()) // 몇 번째 데이터부터 가져올지
                .limit(pageable.getPageSize()) // 한 페이지에 몇 개 가져올지
                .fetch(); // 최종적으로 List<LectureListResponseDto> 반환

        // 전체 데이터 개수 조회 - Page 객체 만들 때 total count가 필요하기 때문
        Long total = queryFactory
                .select(lectureList.count())
                .from(lectureList)
                .join(lectureList.lectureClass, lectureClass)
                .where(
                        lectureList.users.userId.eq(userId),
                        lectureList.isDeleted.eq(false),
                        lectureClass.isDeleted.eq(false),
                        categoryCondition,
                        keywordCondition
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    // 문자열이 null이 아니고, 공백만 있는 값도 아닌지 확인하는 유틸 메서드
    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
