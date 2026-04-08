package com.nyang.backend.lecture.event;

import com.nyang.backend.lecture.service.LectureSttService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class LectureSttEventListener {

    private final LectureSttService lectureSttService;

//    @TransactionalEventListener
//    public void handleLectureCreated(LectureCreatedEvent event) {
//        lectureSttService.requestSttAsync(event.getLectureId());
//    }

    //기존 이벤트 발행 방식-> 임시파일 생성으로 인한 주석처리
}