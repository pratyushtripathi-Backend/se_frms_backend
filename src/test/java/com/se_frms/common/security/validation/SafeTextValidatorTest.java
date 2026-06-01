package com.se_frms.common.security.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SafeTextValidatorTest {

    private final SafeTextValidator validator = new SafeTextValidator();

    @Test
    void shouldRejectHtmlAndScriptPatterns() {
        assertThat(validator.isValid("<script>alert('xss')</script>", null)).isFalse();
        assertThat(validator.isValid("javascript:alert(1)", null)).isFalse();
        assertThat(validator.isValid("<img src=x onerror=alert(1)>", null)).isFalse();
    }

    @Test
    void shouldAllowPlainTextValues() {
        assertThat(validator.isValid("Alice Johnson", null)).isTrue();
        assertThat(validator.isValid("alice@example.com", null)).isTrue();
    }
}
