package com.tokioschool.storeapp.security.filter;

import com.tokioschool.storeapp.redis.service.RedisJwtBlackListService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class JwtBlackListFilterUTest {

    @Mock
    RedisJwtBlackListService redisJwtBlackListService;

    @InjectMocks
    JwtBlackListFilter jwtBlackListFilter;


    @Test
    void givenBlacklistedToken_whenFilterRequest_thenRespondUnauthorized() throws ServletException, IOException {
        // Mocking HttpServletRequest and HttpServletResponse
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        // Simulating Authorization header with a blacklisted token
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer blacklisted-token");
        Mockito.when(redisJwtBlackListService.isBlacklisted("blacklisted-token")).thenReturn(true);

        // Execute filter
        jwtBlackListFilter.doFilterInternal(request, response, filterChain);

        // Verify response sends error and filter chain is NOT executed
        Mockito.verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token está en la lista negra");
        Mockito.verify(filterChain, Mockito.never()).doFilter(Mockito.any(HttpServletRequest.class), Mockito.any(HttpServletResponse.class));
    }

    @Test
    void givenValidToken_whenFilterRequest_thenContinueChain() throws ServletException, IOException {
        // Mocking HttpServletRequest and HttpServletResponse
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        // Simulating a valid token in the request
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        Mockito.when(redisJwtBlackListService.isBlacklisted("valid-token")).thenReturn(false);

        // Execute filter
        jwtBlackListFilter.doFilterInternal(request, response, filterChain);

        // Verify filter chain continues and response is NOT affected
        Mockito.verify(filterChain).doFilter(request, response);
        Mockito.verify(response, Mockito.never()).sendError(Mockito.anyInt(), Mockito.anyString());
    }

}