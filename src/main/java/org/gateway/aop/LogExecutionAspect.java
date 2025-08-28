package org.gateway.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogExecutionAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogExecutionAspect.class);
    
    @Around("within(@org.springframework.stereotype.Service *) || " +
            "within(@org.springframework.stereotype.Controller *) || " +
            "within(@org.springframework.web.bind.annotation.RestController *) || " +
            "within(@org.springframework.web.bind.annotation.RestControllerAdvice *) || " +
            "within(@org.springframework.web.bind.annotation.Component *) || " +
            "within(@org.springframework.stereotype.Repository *)")
    public Object logClassMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        logger.info("Entry into {} inside Method {} started", className, methodName);
        try {
            Object result = joinPoint.proceed();
            logger.info("Exit from {} inside Method {} completed successfully", className, methodName);
            return result;
        } catch (Exception e) {
            logger.error("Exception occurs into {} inside Method: {}", className, methodName, e.getMessage(), e);
            throw e;
        }
    }
}
