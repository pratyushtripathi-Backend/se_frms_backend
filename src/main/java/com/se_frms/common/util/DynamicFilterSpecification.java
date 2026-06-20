package com.se_frms.common.util;

import com.se_frms.auth.exception.InvalidRequestException;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public final class DynamicFilterSpecification {

    private static final int MAX_PAGE_SIZE = 100;

    private static final Set<String> RESERVED_PARAMS =
            Set.of(
                    "page",
                    "size",
                    "sort",
                    "sortBy",
                    "sortDir",
                    "direction"
            );

    private DynamicFilterSpecification() {
    }

    public static <T> Specification<T> build(
            Map<String, String> requestParams,
            Map<String, String> allowedFields
    ) {

        Specification<T> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.conjunction();

        for (Map.Entry<String, String> entry : requestParams.entrySet()) {

            String rawKey = entry.getKey();
            String rawValue = entry.getValue();

            if (isReserved(rawKey) || rawValue == null || rawValue.isBlank()) {
                continue;
            }

            FilterToken filterToken =
                    parseFilterToken(
                            rawKey,
                            allowedFields
                    );

            String entityField =
                    allowedFields.get(filterToken.fieldName());

            if (entityField == null) {
                throw new InvalidRequestException(
                        "Invalid filter field: " + filterToken.fieldName()
                );
            }

            specification =
                    specification.and(
                            (root, query, criteriaBuilder) ->
                                    buildPredicate(
                                            getPath(root, entityField),
                                            filterToken.operator(),
                                            rawValue.trim(),
                                            criteriaBuilder
                                    )
                    );
        }

        return specification;
    }

    public static <T> Specification<T> equal(
            String entityField,
            Object value
    ) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        getPath(root, entityField),
                        value
                );
    }

    public static Pageable createPageable(
            int page,
            int size,
            Map<String, String> requestParams,
            Map<String, String> allowedSortFields,
            String defaultSortField,
            Sort.Direction defaultDirection
    ) {

        int pageNumber =
                Math.max(
                        page,
                        0
                );

        int pageSize =
                Math.min(
                        Math.max(
                                size,
                                1
                        ),
                        MAX_PAGE_SIZE
                );

        Sort sort =
                resolveSort(
                        requestParams,
                        allowedSortFields,
                        defaultSortField,
                        defaultDirection
                );

        return PageRequest.of(
                pageNumber,
                pageSize,
                sort
        );
    }

    private static Sort resolveSort(
            Map<String, String> requestParams,
            Map<String, String> allowedSortFields,
            String defaultSortField,
            Sort.Direction defaultDirection
    ) {

        String sortBy =
                requestParams.get(
                        "sortBy"
                );

        String resolvedSortField =
                defaultSortField;

        if (sortBy != null && !sortBy.isBlank()) {
            resolvedSortField =
                    allowedSortFields.get(
                            sortBy.trim()
                    );

            if (resolvedSortField == null) {
                throw new InvalidRequestException(
                        "Invalid sort field: " + sortBy
                );
            }
        }

        Sort.Direction direction =
                defaultDirection;

        String sortDir =
                requestParams.getOrDefault(
                        "sortDir",
                        requestParams.get("direction")
                );

        if (sortDir != null && !sortDir.isBlank()) {
            try {
                direction =
                        Sort.Direction.fromString(
                                sortDir.trim()
                        );
            } catch (IllegalArgumentException ex) {
                throw new InvalidRequestException(
                        "Invalid sort direction: " + sortDir
                );
            }
        }

        return Sort.by(
                direction,
                resolvedSortField
        );
    }

    private static boolean isReserved(
            String key
    ) {

        return RESERVED_PARAMS.contains(
                key
        );
    }

    private static FilterToken parseFilterToken(
            String rawKey,
            Map<String, String> allowedFields
    ) {

        if (rawKey.contains("__")) {
            String[] parts =
                    rawKey.split(
                            "__",
                            2
                    );

            return new FilterToken(
                    parts[0],
                    normalizeOperator(parts[1])
            );
        }

        if (rawKey.endsWith("From")) {
            String fieldName =
                    rawKey.substring(
                            0,
                            rawKey.length() - 4
                    );

            if (allowedFields.containsKey(fieldName)) {
                return new FilterToken(
                        fieldName,
                        "gte"
                );
            }
        }

        if (rawKey.endsWith("To")) {
            String fieldName =
                    rawKey.substring(
                            0,
                            rawKey.length() - 2
                    );

            if (allowedFields.containsKey(fieldName)) {
                return new FilterToken(
                        fieldName,
                        "lte"
                );
            }
        }

        return new FilterToken(
                rawKey,
                "auto"
        );
    }

    private static String normalizeOperator(
            String operator
    ) {

        return operator
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private static Path<?> getPath(
            Path<?> root,
            String fieldPath
    ) {

        Path<?> path =
                root;

        for (String field : fieldPath.split("\\.")) {
            path =
                    path.get(field);
        }

        return path;
    }

    @SuppressWarnings({
            "unchecked",
            "rawtypes"
    })
    private static jakarta.persistence.criteria.Predicate buildPredicate(
            Path<?> path,
            String operator,
            String value,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder
    ) {

        Class<?> javaType =
                path.getJavaType();

        if (String.class.equals(javaType)) {
            Expression<String> expression =
                    criteriaBuilder.lower(
                            path.as(String.class)
                    );

            String normalizedValue =
                    value.toLowerCase(Locale.ROOT);

            if ("eq".equals(operator)) {
                return criteriaBuilder.equal(
                        expression,
                        normalizedValue
                );
            }

            if ("auto".equals(operator) || "contains".equals(operator)) {
                return criteriaBuilder.like(
                        expression,
                        "%" + normalizedValue + "%"
                );
            }

            if ("starts".equals(operator) || "startswith".equals(operator)) {
                return criteriaBuilder.like(
                        expression,
                        normalizedValue + "%"
                );
            }

            throw new InvalidRequestException(
                    "Invalid string filter operator: " + operator
            );
        }

        Object typedValue =
                convertValue(
                        javaType,
                        value,
                        operator
                );

        if ("auto".equals(operator) || "eq".equals(operator)) {
            return criteriaBuilder.equal(
                    path,
                    typedValue
            );
        }

        if (!Comparable.class.isAssignableFrom(javaType)) {
            throw new InvalidRequestException(
                    "Filter operator not supported for field type"
            );
        }

        Expression<Comparable> expression =
                (Expression<Comparable>) path.as((Class) javaType);

        Comparable comparableValue =
                (Comparable) typedValue;

        return switch (operator) {
            case "gt" ->
                    criteriaBuilder.greaterThan(
                            expression,
                            comparableValue
                    );
            case "gte", "from" ->
                    criteriaBuilder.greaterThanOrEqualTo(
                            expression,
                            comparableValue
                    );
            case "lt" ->
                    criteriaBuilder.lessThan(
                            expression,
                            comparableValue
                    );
            case "lte", "to" ->
                    criteriaBuilder.lessThanOrEqualTo(
                            expression,
                            comparableValue
                    );
            default ->
                    throw new InvalidRequestException(
                            "Invalid filter operator: " + operator
                    );
        };
    }

    private static Object convertValue(
            Class<?> javaType,
            String value,
            String operator
    ) {

        try {
            if (Boolean.class.equals(javaType) || boolean.class.equals(javaType)) {
                if (!"true".equalsIgnoreCase(value)
                        && !"false".equalsIgnoreCase(value)) {
                    throw new InvalidRequestException(
                            "Invalid boolean filter value: " + value
                    );
                }

                return Boolean.parseBoolean(value);
            }

            if (Integer.class.equals(javaType) || int.class.equals(javaType)) {
                return Integer.valueOf(value);
            }

            if (Long.class.equals(javaType) || long.class.equals(javaType)) {
                return Long.valueOf(value);
            }

            if (Double.class.equals(javaType) || double.class.equals(javaType)) {
                return Double.valueOf(value);
            }

            if (BigDecimal.class.equals(javaType)) {
                return new BigDecimal(value);
            }

            if (LocalDate.class.equals(javaType)) {
                return LocalDate.parse(value);
            }

            if (LocalTime.class.equals(javaType)) {
                return LocalTime.parse(value);
            }

            if (LocalDateTime.class.equals(javaType)) {
                return convertLocalDateTime(
                        value,
                        operator
                );
            }
        } catch (RuntimeException ex) {
            throw new InvalidRequestException(
                    "Invalid filter value: " + value
            );
        }

        return value;
    }

    private static LocalDateTime convertLocalDateTime(
            String value,
            String operator
    ) {

        if (value.length() == 10) {
            LocalDate date =
                    LocalDate.parse(value);

            if ("lte".equals(operator) || "to".equals(operator)) {
                return date.atTime(
                        LocalTime.MAX
                );
            }

            return date.atStartOfDay();
        }

        return LocalDateTime.parse(value);
    }

    private record FilterToken(
            String fieldName,
            String operator
    ) {
    }
}
