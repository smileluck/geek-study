package top.zsmile.db.datasource.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.zsmile.db.datasource.annotation.ReadOnly;
import com.air.basis.datasource.config.DynamicContextHolder;
import top.zsmile.db.datasource.config.DynamicManage;

import javax.sql.DataSource;
import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class ReadOnlyAspect {

    @Autowired
    private DynamicManage dynamicManage;

    @Pointcut("@annotation(top.zsmile.db.datasource.annotation.ReadOnly)")
    public void dataSourcePointCut() {

    }

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();

        ReadOnly ds = method.getAnnotation(ReadOnly.class);
        if (ds == null || ds.name().equalsIgnoreCase("")) {
            String name = dynamicManage.getSlaveDataSource();
            System.out.println("读取数据库：" + name);
            DynamicContextHolder.push(name);
        } else {
            System.out.println("读取数据库：" + ds.name());
            DynamicContextHolder.push(ds.name());
        }

        try {
            return proceedingJoinPoint.proceed();
        } finally {
            DynamicContextHolder.poll();
        }

    }
}
