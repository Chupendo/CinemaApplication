package com.tokioschool.storeapp.security.filter;

import com.tokioschool.storeapp.redis.service.RedisJwtBlackListService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtBlackListFilter extends OncePerRequestFilter {

    private final RedisJwtBlackListService redisJwtBlackListService;

    /**
     * Filter that execute before authentication for verify that token of request isn't in the black list and can continue with request,
     * if the filter is in the black list, then throw a response wit error
     *
     * @param request data with token to verify
     * @param response data with information of response
     * @param filterChain chain of filters of context security spring
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.debug("Verify that is the token is present in black list");

        String token = extractToken(request);

        if (token != null && redisJwtBlackListService.isBlacklisted(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
            return;
        }

        // Continuar con el filtro de autenticación
        filterChain.doFilter(request, response);
    }

    /**
     * Method for extract to jwt token of request
     *
     * @param request data with token in Authorization header
     * @return jwt token without text chain 'Bearer or ' or null if this isn't present
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
