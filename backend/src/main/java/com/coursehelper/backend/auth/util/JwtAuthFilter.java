

package com.coursehelper.backend.auth.util;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.coursehelper.backend.auth.CustomUserPrincipal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JwtAuthFilter extends OncePerRequestFilter{

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
            String header = request.getHeader("Authorization");

            if(header != null && header.startsWith("Bearer ")){
                String token = header.substring(7);

                try {

                    String username = jwtUtil.extractUsername(token);
                    Long userId =jwtUtil.extractUserId(token);

                    CustomUserPrincipal userDetails = new CustomUserPrincipal(userId, username);

                    if(jwtUtil.validateToken(token)){
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, List.of());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } 
                    
                } catch (Exception e) {
                     SecurityContextHolder.clearContext();
                }
            }

            filterChain.doFilter(request, response);
            

        }


                    
    
    
}
