package com.example.campusmanagement.interceptor;

import com.example.campusmanagement.entity.User;
import com.example.campusmanagement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    private final UserService userService;

    public AdminInterceptor(UserService userService) {this.userService = userService;}

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String username = (String) request.getAttribute("username");

        User currentUser = userService.getUserByUsername(username);

        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }
}
