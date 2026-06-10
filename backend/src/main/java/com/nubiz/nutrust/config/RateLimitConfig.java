package com.nubiz.nutrust.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;

@Configuration
public class RateLimitConfig {

    @Value("${resilience4j.ratelimiter.instances.auth-limiter.limit-for-period:20}")
    private int authLimit;

    @Value("${resilience4j.ratelimiter.instances.auth-limiter.limit-refresh-period:1s}")
    private Duration authRefreshPeriod;

    @Value("${resilience4j.ratelimiter.instances.auth-refresh-limiter.limit-for-period:5}")
    private int authRefreshLimit;

    @Value("${resilience4j.ratelimiter.instances.auth-refresh-limiter.limit-refresh-period:1s}")
    private Duration authRefreshRefreshPeriod;

    @Value("${resilience4j.ratelimiter.instances.ticket-limiter.limit-for-period:50}")
    private int ticketLimit;

    @Value("${resilience4j.ratelimiter.instances.ticket-limiter.limit-refresh-period:1s}")
    private Duration ticketRefreshPeriod;

    @Value("${resilience4j.ratelimiter.instances.chat-limiter.limit-for-period:30}")
    private int chatLimit;

    @Value("${resilience4j.ratelimiter.instances.chat-limiter.limit-refresh-period:1s}")
    private Duration chatRefreshPeriod;

    @Value("${resilience4j.ratelimiter.instances.default-limiter.limit-for-period:100}")
    private int defaultLimit;

    @Value("${resilience4j.ratelimiter.instances.default-limiter.limit-refresh-period:1s}")
    private Duration defaultRefreshPeriod;

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig defaultConfig = RateLimiterConfig.custom()
                .timeoutDuration(Duration.ofMillis(0))
                .limitRefreshPeriod(defaultRefreshPeriod)
                .limitForPeriod(defaultLimit)
                .build();

        RateLimiterRegistry registry = RateLimiterRegistry.of(defaultConfig);

        // Auth endpoints: login/register
        registry.rateLimiter("auth-limiter",
            RateLimiterConfig.custom()
                .limitRefreshPeriod(authRefreshPeriod)
                .limitForPeriod(authLimit)
                .timeoutDuration(Duration.ofMillis(0))
                .build());

        // Refresh token: stricter limit
        registry.rateLimiter("auth-refresh-limiter",
            RateLimiterConfig.custom()
                .limitRefreshPeriod(authRefreshRefreshPeriod)
                .limitForPeriod(authRefreshLimit)
                .timeoutDuration(Duration.ofMillis(0))
                .build());

        // Ticket endpoints
        registry.rateLimiter("ticket-limiter",
            RateLimiterConfig.custom()
                .limitRefreshPeriod(ticketRefreshPeriod)
                .limitForPeriod(ticketLimit)
                .timeoutDuration(Duration.ofMillis(0))
                .build());

        // Chat endpoints
        registry.rateLimiter("chat-limiter",
            RateLimiterConfig.custom()
                .limitRefreshPeriod(chatRefreshPeriod)
                .limitForPeriod(chatLimit)
                .timeoutDuration(Duration.ofMillis(0))
                .build());

        return registry;
    }
}
