package com.se_frms.common.security.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.Locale;

public class SafeTextValidator implements ConstraintValidator<SafeText, String> {

    private static final List<String> BLOCKED_PATTERNS = List.of(
            "<script",
            "</script",
            "javascript:",
            "vbscript:",
            "onerror=",
            "onload=",
            "<iframe",
            "</iframe",
            "<object",
            "</object"
    );

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ) {
        if (value == null || value.isBlank()) {
            return true;
        }

        String normalized = value.toLowerCase(Locale.ROOT);

        if (normalized.contains("<") || normalized.contains(">")) {
            return false;
        }

        return BLOCKED_PATTERNS.stream().noneMatch(normalized::contains);
    }
}
