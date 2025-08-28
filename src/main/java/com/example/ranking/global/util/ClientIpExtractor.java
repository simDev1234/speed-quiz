package com.example.ranking.global.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientIpExtractor {

    public String getClientIp(HttpServletRequest request) {
        return firstNonUnknown(
                extractClientIpFromXForwardedFor(request),
                request.getHeader("Proxy-Client-IP"),
                request.getHeader("WL-Proxy-Client-IP"),
                request.getRemoteAddr()
        );
    }

    private String extractClientIpFromXForwardedFor(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff == null || xff.isBlank() || "unknown".equalsIgnoreCase(xff)) {
            return null;
        }
        int commaIndex = xff.indexOf(',');
        return commaIndex > 0 ? xff.substring(0, commaIndex).trim() : xff.trim();
    }

    private String firstNonUnknown(String... ips) {
        for (String ip : ips) {
            if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
                return normalizeIp(ip);
            }
        }
        return "0.0.0.0"; // 기본값, 필요시 변경 가능
    }

    private String normalizeIp(String ip) {
        return switch (ip) {
            case "0:0:0:0:0:0:0:1" -> "127.0.0.1";
            case "::1" -> "127.0.0.1";
            default -> ip.startsWith("::ffff:") ? ip.substring(7) : ip;
        };
    }
}
