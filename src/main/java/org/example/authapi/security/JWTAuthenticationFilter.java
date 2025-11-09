package org.example.authapi.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.authapi.service.JWTBlacklistService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final JWTUtils jwtUtils;
    private final JWTBlacklistService jwtBlacklistService;

    public JWTAuthenticationFilter(JWTUtils jwtUtils, JWTBlacklistService jwtBlacklistService) {
        this.jwtUtils = jwtUtils;
        this.jwtBlacklistService = jwtBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        try {
            if (token != null && jwtUtils.validateJwtToken(token)) {
                if (jwtBlacklistService.isBlacklisted(token)) {
                    throw new JwtException("JWT token expired");
                }
            }

            String username = jwtUtils.getUsernameFromJwtToken(token);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.emptyList()
                    );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or blacklist JWT");
            return;
        }
    }
}
