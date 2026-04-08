package com.nyang.backend.lecture.service;

import com.nyang.backend.global.exception.BusinessException;
import com.nyang.backend.lecture.dto.SttResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.nyang.backend.global.exception.ErrorCode;
import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SttClientService {

    private final WebClient sttWebClient;
    // 비동기 처리 -> 동기
    //public Mono<SttResponseDto> sendToSttServer(File tempFile)
      public SttResponseDto sendToSttServer(File tempFile){
        FileSystemResource resource = new FileSystemResource(tempFile);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", resource);

//        return sttWebClient.post()
//                .uri("/api/stt/transcribe")
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .body(BodyInserters.fromMultipartData(builder.build()))
//                .retrieve()
//                .bodyToMono(SttResponseDto.class);
//    }
        return sttWebClient.post()
                .uri("/api/stt/transcribe")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(SttResponseDto.class)
                .block();
    }
}