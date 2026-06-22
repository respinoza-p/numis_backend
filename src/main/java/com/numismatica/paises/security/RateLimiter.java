package com.numismatica.paises.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiter simple basado en IP para proteger endpoints de autenticación.
 * EH-04: Protección contra ataques de fuerza bruta.
 *
 * Límite: MAX_ATTEMPTS intentos por IP en WINDOW_MS milisegundos.
 */
@Slf4j
@Component
public class RateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MS = 15 * 60 * 1000; // 15 minutos

    private final Map<String, RateEntry> attempts = new ConcurrentHashMap<>();

    /**
     * Verifica si la IP ha excedido el límite de intentos.
     * @return true si se permite el request, false si está bloqueado.
     */
    public boolean isAllowed(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        long now = System.currentTimeMillis();

        attempts.entrySet().removeIf(entry ->
                now - entry.getValue().windowStart > WINDOW_MS);

        RateEntry entry = attempts.computeIfAbsent(clientIp,
                k -> new RateEntry(now, new AtomicInteger(0)));

        if (now - entry.windowStart > WINDOW_MS) {
            entry.windowStart = now;
            entry.count.set(0);
        }

        int currentCount = entry.count.incrementAndGet();
        if (currentCount > MAX_ATTEMPTS) {
            log.warn("Rate limit excedido para IP: {} ({} intentos)", clientIp, currentCount);
            return false;
        }

        return true;
    }

    /**
     * Resetea el contador para una IP después de un login exitoso.
     */
    public void resetFor(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        attempts.remove(clientIp);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class RateEntry {
        volatile long windowStart;
        final AtomicInteger count;

        RateEntry(long windowStart, AtomicInteger count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
