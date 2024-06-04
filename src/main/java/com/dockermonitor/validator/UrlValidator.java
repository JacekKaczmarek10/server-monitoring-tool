package com.dockermonitor.validator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class UrlValidator {

    private static final int MIN_URL_LENGTH = 10;
    private static final int MAX_URL_LENGTH = 2048;
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("^[a-zA-Z0-9.-]+$");

    public void validateUrl(final String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

        if (url.length() < MIN_URL_LENGTH || url.length() > MAX_URL_LENGTH) {
            throw new IllegalArgumentException("URL length should be between " + MIN_URL_LENGTH + " and " + MAX_URL_LENGTH);
        }

        if (!url.startsWith("https://")) {
            throw new IllegalArgumentException("URL should use HTTPS protocol");
        }

        if (url.contains(" ")) {
            throw new IllegalArgumentException("URL should not contain spaces");
        }

        try {
            final var parsedUrl = new URL(url);

            final var host = parsedUrl.getHost();
            if (host == null || !DOMAIN_PATTERN.matcher(host).matches()) {
                throw new IllegalArgumentException("URL should contain a valid domain name");
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("URL is not well-formed", e);
        }
    }
}