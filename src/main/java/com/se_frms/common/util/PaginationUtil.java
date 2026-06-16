package com.se_frms.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PaginationUtil {

    private static final int MAX_PAGE_SIZE = 100;

    private PaginationUtil() {
    }

    public static Pageable createPageable(
            Integer page,
            Integer size,
            Sort sort
    ) {

        int pageNumber =
                page == null
                        ? 0
                        : page;

        int pageSize =
                size == null
                        ? 10
                        : Math.min(size, MAX_PAGE_SIZE);

        return PageRequest.of(
                pageNumber,
                pageSize,
                sort
        );
    }
}
