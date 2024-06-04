package com.dockermonitor.validator;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UrlValidatorTest {

    private final UrlValidator urlValidator = new UrlValidator();

    @Nested
    class ValidateUrlTest {

        @Test
        void shouldValidateHttpsUrl() {
            assertThatCode(() -> urlValidator.validateUrl("https://www.example.com"))
                .doesNotThrowAnyException();
        }

        @Test
        void shouldThrowExceptionForNullUrl() {
            assertThatThrownBy(() -> urlValidator.validateUrl(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("URL cannot be null or empty");
        }

        @Test
        void shouldThrowExceptionForEmptyUrl() {
            assertThatThrownBy(() -> urlValidator.validateUrl(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("URL cannot be null or empty");
        }

        @Test
        void shouldThrowExceptionForNonHttpsUrl() {
            assertThatThrownBy(() -> urlValidator.validateUrl("http://www.example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("URL should use HTTPS protocol");
        }

        @Test
        void shouldThrowExceptionForUrlWithSpaces() {
            assertThatThrownBy(() -> urlValidator.validateUrl("https://www.example .com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("URL should not contain spaces");
        }

        @Test
        void shouldThrowExceptionForShortUrl() {
            final var shortUrl = "http://a.co";

            assertThrows(IllegalArgumentException.class, () -> {
                urlValidator.validateUrl(shortUrl);
            });
        }

        @Test
        void shouldThrowExceptionForLongUrl() {
            final var longUrl = "https://www." + "a".repeat(2040) + ".com";

            assertThatThrownBy(() -> urlValidator.validateUrl(longUrl))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("URL length should be between");
        }

        @Test
        void shouldThrowExceptionForInvalidDomain() {
            final var urlValidator = new UrlValidator();

            assertThatThrownBy(() -> urlValidator.validateUrl("https://www.exa mple.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("URL should not contain spaces");
        }

        @Test
        void shouldThrowExceptionForMalformedUrl() {
            assertThatThrownBy(() -> urlValidator.validateUrl("https://"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("URL length should be between 10 and 2048");
        }

        @Test
        void shouldValidateComplexValidUrl() {
            assertThatCode(() -> urlValidator.validateUrl("https://sub.domain.example.com/path?query=param"))
                .doesNotThrowAnyException();
        }

        @Test
        void shouldThrowExceptionForUrlWithInvalidCharacters() {
            assertThatThrownBy(() -> urlValidator.validateUrl("https://www.exa$mple.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("URL should contain a valid domain name");
        }

    }

}