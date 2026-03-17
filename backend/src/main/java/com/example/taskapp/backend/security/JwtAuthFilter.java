package com.example.taskapp.backend.security;



import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService uds;

    public JwtAuthFilter(JwtUtil jwtUtil, CustomUserDetailsService uds) {
        this.jwtUtil = jwtUtil;
        this.uds = uds;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain
    ) throws ServletException, IOException {

        String h = req.getHeader("Authorization");

        if (h != null && h.startsWith("Bearer ")
            && SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = h.substring(7);

            if (jwtUtil.isValid(token)) {

                String email = jwtUtil.extractEmail(token);
                var ud = uds.loadUserByUsername(email);

                var auth = new UsernamePasswordAuthenticationToken(
                        ud, null, ud.getAuthorities());

                auth.setDetails(
                    new WebAuthenticationDetailsSource()
                        .buildDetails(req));

                SecurityContextHolder.getContext()
                        .setAuthentication(auth);
            }
        }

        chain.doFilter(req, res);
    }

}
