package com.tokioschool.filmapp.core.filter;

import com.tokioschool.filmapp.services.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
@Order(value = Ordered.LOWEST_PRECEDENCE) // Crea e inyecta en la ultima posicion posible
@RequiredArgsConstructor
public class LogRequestFilter extends OncePerRequestFilter {

    private final UserService userService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTimeMs = System.currentTimeMillis();

        // logger the user authenticated
        considerSecurity();

        try{
            // continue to request
            filterChain.doFilter(request, response);

            // Obtener la autenticación actual
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if( response.getStatus() == 200 &&
                    (getRequestPath(request).equals("/film/api/auth") ||
                            getRequestPath(request).equals("/film/api/auth/") ||
                            getRequestPath(request).equals("/film/api/auth/login") ) ){
                userService.updateLastLoginTime();
            }

        }finally {
            // logger the time in executed the request
            long endTimeMs = System.currentTimeMillis() - startTimeMs;
            log.info("Request: time.ms: {}, method: {}, status: {}, path: {}",
                    endTimeMs, request.getMethod(), response.getStatus(), getRequestPath(request));
        }
    }

    /**
     * Get the user authenticated
     */
    protected void considerSecurity(){
        if(SecurityContextHolder.getContext().getAuthentication() != null){
            org.slf4j.MDC.put("user-ID", SecurityContextHolder.getContext().getAuthentication().getName());
        }
    }
    private String getRequestPath(final HttpServletRequest request) {
        final StringBuilder requestURI = new StringBuilder(request.getRequestURI());
        // get arguments
        Optional.ofNullable(request.getQueryString())
                .ifPresent(qs -> requestURI.append("?").append(qs));

        return requestURI.toString();
    }
}
