package com.tokioschool.ratingapp.securities.configurations.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * Custom access denied handler to handle access denied errors (403).
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Handles access denied errors by setting the response status to 403 and writing an error message.
     *
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @param accessDeniedException the access denied exception
     * @throws IOException if an input or output error occurs while writing the response
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("Acceso denegado: No tienes permisos.");
    }
}