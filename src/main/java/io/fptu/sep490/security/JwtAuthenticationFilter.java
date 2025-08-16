package io.fptu.sep490.security;

import io.fptu.sep490.utils.JsonUtils;
import io.fptu.sep490.utils.LocalizedTextUtils;
import io.fptu.sep490.utils.ResponseUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenStoreService tokenStoreService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        final var authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }


        final var token = authHeader.substring(7);
        final var username = jwtService.extractUsername(token);


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!userDetails.isEnabled()) {
                revokeTokenIfExists(token);
                sendErrorResponse(response, "account.is.disabled");
                return;
            }

            if (!userDetails.isAccountNonLocked()) {
                revokeTokenIfExists(token);
                sendErrorResponse(response, "account.is.locked");
                return;
            }

            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void revokeTokenIfExists(String token) {
        if (jwtService.isRefreshToken(token)) {
            tokenStoreService.revokeRefreshToken(token);
        } else {
            tokenStoreService.revokeAccessToken(token);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String messageKey) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        var errorBody = ResponseUtils.error(
                HttpServletResponse.SC_FORBIDDEN,
                LocalizedTextUtils.getLocalizedText(messageKey)
        );

        response.getWriter().write(JsonUtils.marshal(errorBody));
    }

}