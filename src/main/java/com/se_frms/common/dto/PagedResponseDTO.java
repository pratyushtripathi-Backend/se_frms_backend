package com.se_frms.common.dto;

import lombok.Builder;
import lombok.Getter;

import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PagedResponseDTO<T> {

    private List<T> content;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;

    private boolean first;

    private boolean last;

    private boolean empty;

    public static <T> PagedResponseDTO<T> from(
            Page<T> page
    ) {

        return PagedResponseDTO
                .<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}
