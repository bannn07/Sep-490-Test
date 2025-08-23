package io.fptu.sep490.aop;

import io.fptu.sep490.model.AuditLog;
import io.fptu.sep490.service.AuditLogService;
import io.fptu.sep490.utils.UserInfoUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogService auditLogService;
    private static final Logger log = LoggerFactory.getLogger(AuditLogAspect.class);

    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object logAction(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())).getRequest();
        String userName = UserInfoUtils.getCurrentUser().account().getUsername();
        String endpoint = request.getRequestURI();
        String actionType = request.getMethod();

        AuditLog auditLog = AuditLog.builder()
                .actionType(actionType)
                .endPoint(endpoint)
                .log(userName + " called " + endpoint)
                .status("PENDING")
                .build();

        log.info("[PENDING] {} called {}", userName, endpoint);

        auditLogService.logAction(List.of(auditLog));

        try {
            Object result = joinPoint.proceed();
            auditLog.setStatus("SUCCESS");
            auditLog.setLog(userName + " successfully called " + endpoint);

            log.info("[SUCCESS] {} successfully called {}", userName, endpoint);

            auditLogService.logAction(List.of(auditLog));
            return result;
        } catch (Throwable ex) {
            auditLog.setStatus("FAIL");
            auditLog.setLog(userName + " exception: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());

            log.error("[FAIL] {} called {} -> {}: {}", userName, endpoint,
                    ex.getClass().getSimpleName(), ex.getMessage(), ex);

            auditLogService.logAction(List.of(auditLog));
            throw ex;
        }
    }
}

