package com.project.extractUserId;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class UserIdCheckAspect {

    @Before("@annotation(userIdCheck)")
    public void checkUserId(UserIdCheck userIdCheck) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String userId = request.getHeader("X-User-Id");
            System.out.println("User ID from header: " + userId);
            if (userId == null || userId.isEmpty()) {
                throw new UnauthorizedException("User ID is missing in the request headers");
            }

        } else {
            throw new UnauthorizedException("No request attributes found");
        }
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}
