package top.zsmile.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InvokePersonHandler implements InvocationHandler {

    private Object person;

    public InvokePersonHandler(Object person){
        this.person = person;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("执行前");

        Object invoke = method.invoke(person, args);

        System.out.println("执行后");
        return invoke;
    }
}
