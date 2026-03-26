package org.aurora.launcher.core.security;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Pattern;

public final class UrlValidator {

    private static final Pattern FILENAME_PATTERN = Pattern.compile("[\\\\/:*?\"<>|]");
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            Pattern.CASE_INSENSITIVE
    );

    private UrlValidator() {
    }

    public static boolean isValidUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        return URL_PATTERN.matcher(url).matches();
    }

    public static boolean isAllowedDomain(String url, List<String> allowedDomains) {
        if (!isValidUrl(url)) {
            return false;
        }
        if (allowedDomains == null || allowedDomains.isEmpty()) {
            return true;
        }
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (host == null) {
                return false;
            }
            for (String domain : allowedDomains) {
                if (host.equalsIgnoreCase(domain) || host.endsWith("." + domain)) {
                    return true;
                }
            }
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public static String sanitizeFilename(String filename) {
        if (filename == null) {
            return "";
        }
        return FILENAME_PATTERN.matcher(filename).replaceAll("_");
    }
}