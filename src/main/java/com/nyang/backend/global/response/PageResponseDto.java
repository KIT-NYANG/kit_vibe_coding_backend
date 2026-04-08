package com.nyang.backend.global.response;

import lombok.*;
import org.springframework.data.domain.Page;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PageResponseDto<T> {
    // 페이지네이션을 위한 dto

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public static <T> PageResponseDto<T> from(Page<T> page) {
        return PageResponseDto.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
