package com.project.onlinecoursemanagement.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);


    @Pointcut("execution(* com.project.onlinecoursemanagement.controller.*.*(..))")
    public void controllerLayerExecution() {
    }

    @Pointcut("execution(* com.project.onlinecoursemanagement.service.*.*(..))")
    public void serviceLayerExecution() {
    }

    @Before("controllerLayerExecution() || serviceLayerExecution()")
    public void logBefore(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        //MDC stands for Mapped Diagnostic Context
        MDC.put("requestId", UUID.randomUUID().toString());

        logger.info("[{}] Entering {}.{}() with arguments: {}",
                MDC.get("requestId"), className, methodName, Arrays.toString(joinPoint.getArgs()));
    }


    @AfterReturning(pointcut = "controllerLayerExecution() || serviceLayerExecution()",returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        logger.info("[{}] Method {}.{}() returned: {}",
                MDC.get("requestId"), className, methodName, result);

        MDC.clear();
    }

    @AfterThrowing(pointcut = "controllerLayerExecution() || serviceLayerExecution()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        logger.error("[{}] Exception in {}.{}(): {}",
                MDC.get("requestId"), className, methodName, ex.getMessage(), ex);

        MDC.clear();
    }

}