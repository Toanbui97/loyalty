package vn.com.loyalty.core.aop;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import vn.com.loyalty.core.annotation.HasRole;
import vn.com.loyalty.core.dto.request.BodyRequest;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RoleFilter {

    @Pointcut("@annotation(vn.com.loyalty.core.annotation.HasRole)")
    public void restApiPointCut() {}

    @Before("restApiPointCut()")
    public void filter(JoinPoint point){
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();

        HasRole hasRole = method.getAnnotation(HasRole.class);
        String[] roles = hasRole.roles();

        if (point.getArgs()[0] instanceof BodyRequest<?> request) {
            List<String> roleList = Arrays.stream(roles).toList();
            List<String> userRoles = Arrays.stream(request.getHeader().getRoles()).toList();
            if (!CollectionUtils.isEmpty(roleList) && Collections.disjoint(roleList, userRoles)) {
                throw new SecurityException();
            }
        }
    }
}
