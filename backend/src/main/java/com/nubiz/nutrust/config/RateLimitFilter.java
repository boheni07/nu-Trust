package com.nubiz.nutrust.config;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${app.ratelimit.paths:/api/:default-limiter}")
    private String pathConfigs;

    private final Map<String, String> pathRateLimiterMap = new ConcurrentHashMap<>();

    private final RateLimiterRegistry injectedRateLimiterRegistry;

    @Autowired
    public RateLimitFilter(RateLimiterRegistry rateLimiterRegistry) {
        this.injectedRateLimiterRegistry = rateLimiterRegistry;
        parsePathConfigs();
    }

    private void parsePathConfigs() {
        if (pathRateLimiterMap.isEmpty()) {
            String[] configs = pathConfigs.split(",");
            for (String config : configs) {
                String[] parts = config.trim().split(":");
                if (parts.length == 2) {
                    pathRateLimiterMap.put(parts[0], parts[1]);
                }
            }
        }
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/actuator")
                || path.startsWith("/h2-console")
                || path.startsWith("/ws")
                || path.equals("/error");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String limiterName = resolveLimiterName(path);

        if (limiterName == null) {
            filterChain.doFilter(request, response);
            return;
        }

        RateLimiter rateLimiter = injectedRateLimiterRegistry.rateLimiter(limiterName);

        try {
            rateLimiter.acquirePermission();
            filterChain.doFilter(request, response);
        } catch (RequestNotPermitted e) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                "{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"요청이 너무 많습니다 잠시 후 다시 시도해주세요.\"}"
            );
        }
    }

    private String resolveLimiterName(String path) {
        for (Map.Entry<String, String> entry : pathRateLimiterMap.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
