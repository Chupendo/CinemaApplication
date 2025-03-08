package com.tokioschool.ratingapp.securities.configurations.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * Custom authentication entry point to handle authentication errors (401).
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Handles authentication errors by setting the response status to 401 and writing an error message.
     *
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @param authException the authentication exception
     * @throws IOException if an input or output error occurs while writing the response
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         org.springframework.security.core.AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Error de autenticación: Credenciales inválidas.");
    }
}