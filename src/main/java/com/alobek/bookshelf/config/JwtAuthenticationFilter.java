package com.alobek.bookshelf.config;

import com.alobek.bookshelf.dto.JwtDTO;
import com.alobek.bookshelf.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request
            , HttpServletResponse response
            , FilterChain filterChain) throws ServletException, IOException {

        final String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Continue filter chain ...
            return;
        }

        try{
            final String token = header.substring(7).trim();
            JwtDTO jwtDTO = JwtUtil.decode(token);

            // Get username from JWT
            String username = jwtDTO.getUsername();


            // Load CustomUserDetails from DB
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);


            // Get the roles string from JWT
            String roleStr = jwtDTO.getRoleList().toString();

            // Split by comma in case of multiple roles
            List<GrantedAuthority> authorities = jwtDTO.getRoleList()
                    .stream()
                    .map(role -> new SimpleGrantedAuthority(role.name()))
                    .collect(Collectors.toList());


            // create Authentication with authorities
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                            userDetails.getAuthorities()
            );

            // set into SecurityContext ...
            SecurityContextHolder.getContext().setAuthentication(authentication);

            /*String username = jwtDTO.getUsername();
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
*/

            // continue filter chain ...
            filterChain.doFilter(request, response);

        }catch (JwtException | UsernameNotFoundException e) {
            filterChain.doFilter(request, response); // Continue filter chain ...
            return;
        }

    }

    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return Arrays
                .stream(SpringConfig.AUTH_WHITELIST)
                .anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
    }
}
