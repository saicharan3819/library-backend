package com.library.config;

import com.library.exception.ApiException;
import com.library.model.Role;
import com.library.model.User;
import com.library.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Runs before every /api request: checks the token and enforces ADMIN-only URLs. */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenStore tokens;
    private final UserRepository users;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        if ("OPTIONS".equals(req.getMethod())) return true;       // CORS pre-flight

        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer "))
            throw new ApiException(401, "Please login first");

        Long userId = tokens.resolve(header.substring(7));
        if (userId == null) throw new ApiException(401, "Session expired, please login again");

        User user = users.findById(userId).orElseThrow(() -> new ApiException(401, "User not found"));

        if (req.getRequestURI().startsWith("/api/admin") && user.getRole() != Role.ADMIN)
            throw new ApiException(403, "Admin access only");

        req.setAttribute("user", user);   // controllers can read the logged-in user
        return true;
    }
}
