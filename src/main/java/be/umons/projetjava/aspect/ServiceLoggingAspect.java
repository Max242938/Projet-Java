package be.umons.projetjava.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect 
@Component 
public class ServiceLoggingAspect {
    private static final Logger log = 
        LoggerFactory.getLogger(ServiceLoggingAspect.class); 
    @Around("execution(public * com.example.smarthome.service..*(..))") 
    public Object logExecution(ProceedingJoinPoint joinPoint) 
            throws Throwable { 
        long start = System.currentTimeMillis(); 
        try { 
            Object result = joinPoint.proceed(); 
            log.info("{} exécutée en {} ms", 
                joinPoint.getSignature().toShortString(), 
                System.currentTimeMillis() - start); 
            return result; 
        } catch (Throwable ex) { 
            log.error("Échec de {}", joinPoint.getSignature(), ex); 
            throw ex; 
        } 
    } 
}
