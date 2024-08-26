package com.project.rolechecker;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;




@Aspect
@Component
public class RoleCheckAspect {


    @Before("@annotation(roleCheck)")
    public void checkRole(RoleCheck roleCheck) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String userRole = request.getHeader("X-User-Role");
            System.out.println("User Role from header: " + userRole); // Debug line

            if (!roleCheck.value().equals(userRole)) {
                throw new UnauthorizedException("User does not have the required role: " + roleCheck.value());
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





