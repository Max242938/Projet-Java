package be.umons.projetjava.aspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Aspect
@Component
public class ServiceLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(ServiceLoggingAspect.class);
    @Pointcut("execution(public * be.umons.projetjava.service..*(..))")
    public void serviceMethods() {}
    @Around("serviceMethods()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            log.info("{} executee en {} ms", joinPoint.getSignature().toShortString(), System.currentTimeMillis() - start);
            return result;
        } catch (Throwable ex) {
            log.error("Echec de {} : {}", joinPoint.getSignature().toShortString(), ex.getMessage());
            throw ex;
        }
    }
}
