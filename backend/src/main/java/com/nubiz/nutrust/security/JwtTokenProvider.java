package com.nubiz.nutrust.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

	private static final String BLACKLIST_KEY_PREFIX = "blacklist:";
	private static final String REFRESH_LOCK_PREFIX = "refresh:lock:";

	private final SecretKey key;
	private final long accessTokenExpiration;
	private final long refreshTokenExpiration;
	private final StringRedisTemplate redisTemplate;
	private final boolean blacklistEnabled;
	private final long reuseDetectionDelay;

	public JwtTokenProvider(
		@Value("${app.jwt.secret}") String secret,
		@Value("${app.jwt.expiration}") long accessTokenExpiration,
		@Value("${app.jwt.refreshExpiration}") long refreshTokenExpiration,
		@Value("${app.jwt.blacklist.enabled:true}") boolean blacklistEnabled,
		@Value("${app.jwt.token.reuse-detection-delay:30000}") long reuseDetectionDelay,
		StringRedisTemplate redisTemplate
	) {
		this.key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret));
		this.accessTokenExpiration = accessTokenExpiration;
		this.refreshTokenExpiration = refreshTokenExpiration;
		this.redisTemplate = redisTemplate;
		this.blacklistEnabled = blacklistEnabled;
		this.reuseDetectionDelay = reuseDetectionDelay;
	}

	public String createAccessToken(Authentication authentication) {
		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		return Jwts.builder()
			.subject(authentication.getName())
			.claim("authorities", authorities)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
			.signWith(key)
			.compact();
	}

	public String createRefreshToken(Authentication authentication) {
		return Jwts.builder()
			.subject(authentication.getName())
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
			.signWith(key)
			.compact();
	}

	public Authentication getAuthentication(String token) {
		Claims claims = Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(token)
			.getPayload();

		String email = claims.getSubject();
		String authorities = claims.get("authorities", String.class);

		Collection<? extends GrantedAuthority> authorityList;
		if (authorities != null && !authorities.isEmpty()) {
			authorityList = Arrays.stream(authorities.split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
		} else {
			authorityList = Collections.emptyList();
		}

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
			.username(email)
			.password("")
			.authorities(authorityList)
			.build();

		return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
			userDetails, null, authorityList
		);
	}

	public boolean validateToken(String token) {
		if (blacklistEnabled && isBlacklisted(token)) {
			return false;
		}
		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void blacklistToken(String token) {
		try {
			Claims claims = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
			long remainingMs = claims.getExpiration().getTime() - System.currentTimeMillis();
			if (remainingMs > 0 && blacklistEnabled) {
				redisTemplate.opsForValue().set(
					BLACKLIST_KEY_PREFIX + token, "1", remainingMs, TimeUnit.MILLISECONDS
				);
			}
		} catch (Exception ignored) {
		}
	}

	public boolean isBlacklisted(String token) {
		return redisTemplate.hasKey(BLACKLIST_KEY_PREFIX + token);
	}

	public boolean tryAcquireRefreshLock(String email, long timeoutMs) {
		String lockKey = REFRESH_LOCK_PREFIX + email;
		Boolean acquired = redisTemplate.opsForValue()
			.setIfAbsent(lockKey, "1", timeoutMs, TimeUnit.MILLISECONDS);
		return Boolean.TRUE.equals(acquired);
	}

	public void releaseRefreshLock(String email) {
		redisTemplate.delete(REFRESH_LOCK_PREFIX + email);
	}

	public long getAccessTokenExpiration() {
		return accessTokenExpiration;
	}

	public long getRefreshTokenExpiration() {
		return refreshTokenExpiration;
	}
}
