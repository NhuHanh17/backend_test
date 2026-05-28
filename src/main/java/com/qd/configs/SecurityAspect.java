package com.qd.configs;

import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.qd.annotation.CheckOwnership;
import com.qd.annotation.RateLimiter;
import com.qd.pojo.Users;
import com.qd.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import org.springframework.security.core.Authentication;

@Aspect
@Component
public class SecurityAspect {
    @Autowired
    private UserRepository userRepository;

    private final Map<String, Long> limitMap = new ConcurrentHashMap<>();


    private Users getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            throw new RuntimeException("Lỗi bảo mật: Bạn chưa đăng nhập hoặc Token đã hết hạn!");
        }
        Users user = userRepository.findByUsername(auth.getName());
        if (user == null) {
            throw new RuntimeException("Lỗi: Tài khoản không tồn tại trên hệ thống!");
        }
        return user;
    }

    @Before("@annotation(com.qd.annotation.RequireLogin) || @within(com.qd.annotation.RequireLogin)")
    public void checkLogin() {
        getCurrentUser(); 
    }


    @Before("@annotation(com.qd.annotation.RequiresApprovedProvider) || @within(com.qd.annotation.RequiresApprovedProvider)")
    public void checkApprovedProvider() {
        this.checkLogin();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users user = userRepository.findByUsername(auth.getName());
        if (user == null || user.getProviders() == null) {
            throw new RuntimeException("Tài khoản của bạn không phải Nhà cung cấp!");
        }
        if (user.getRoleId() == null || user.getRoleId().getRoleName() == null 
                || !user.getRoleId().getRoleName().toUpperCase().contains("PROVIDER")) {
            throw new RuntimeException("Lỗi phân quyền: Tài khoản này không mang tư cách PROVIDER!");
        }
        if (!user.getProviders().getIsApproved()) {
            throw new RuntimeException("Tài khoản Đối tác của bạn đang ở trạng thái CHỜ DUYỆT hoặc BỊ KHÓA!");
        }
    }

    @Before("@annotation(com.qd.annotation.RequireAdmin) || @within(com.qd.annotation.RequireAdmin)")
    public void checkAdmin() {
        Users user = getCurrentUser();

                if (user.getRoleId() == null || !user.getRoleId().getRoleName().toUpperCase().contains("ADMIN")) {
            throw new RuntimeException("Chỉ Admin mới được thực hiện hành động này!");
        }
    }

    @Before("@annotation(com.qd.annotation.CheckOwnership)")
    public void checkOwnership(JoinPoint joinPoint) {
        this.checkLogin(); 

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String tokenUsername = auth.getName(); 

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().toUpperCase().contains("ADMIN"));
        if (isAdmin) {
            return; 
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CheckOwnership annotation = method.getAnnotation(CheckOwnership.class);
        String targetParamName = annotation.paramName(); 

        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();
        String urlTargetUsername = null;

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(PathVariable.class)) {
                PathVariable pathVar = parameters[i].getAnnotation(PathVariable.class);
                String value = pathVar.value().isEmpty() ? pathVar.name() : pathVar.value();
                if (value.isEmpty()) {
                    value = parameters[i].getName();
                }
                if (targetParamName.equalsIgnoreCase(value)) {
                    urlTargetUsername = String.valueOf(args[i]);
                    break;
                }
            }
        }

        if (urlTargetUsername == null) {
            throw new RuntimeException("Không tìm thấy tham số " + targetParamName + " trên URL!");
        }

        if (!tokenUsername.equalsIgnoreCase(urlTargetUsername)) {
            throw new RuntimeException("Bạn không có quyền can thiệp vào tài nguyên dữ liệu của người khác!");
        }
    }

    @Before("@annotation(com.qd.annotation.RateLimiter)")
    public void checkRateLimit(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimiter limiter = method.getAnnotation(RateLimiter.class);

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String clientIp = request.getRemoteAddr();
            String key = method.getName() + "_" + clientIp;

            long currentTime = System.currentTimeMillis();
            Long lastRequestTime = limitMap.get(key);

            if (lastRequestTime != null) {
                long timePassed = currentTime - lastRequestTime;
                if (timePassed < (limiter.seconds() * 1000L)) {
                    throw new RuntimeException("Thao tác quá nhanh! Vui lòng đợi vài giây rồi thử lại!");
                }
            }
            limitMap.put(key, currentTime); 
        }
    }
}
