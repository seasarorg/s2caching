package org.seasar.caching.interceptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.aop.Aspect;
import org.seasar.framework.aop.Pointcut;
import org.seasar.framework.aop.impl.AspectImpl;
import org.seasar.framework.aop.impl.PointcutImpl;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;
import org.seasar.framework.aop.proxy.AopProxy;

public class CallDescrptionTest extends S2TestCase {
    private static class DebugInterceptor extends AbstractInterceptor {
        public CallDescription description;
        public Object invoke(MethodInvocation arg0) throws Throwable {
            description = new CallDescription(arg0);
            return arg0.proceed();
        }
    }
    
    public void testシリアライズ復元性() throws IOException, ClassNotFoundException {
        Pointcut pointcut = new PointcutImpl(new String[]{"setTime"});
        DebugInterceptor debugInterceptor = new DebugInterceptor();
        Aspect aspect = new AspectImpl(debugInterceptor, pointcut);
        AopProxy aopProxy = new AopProxy(Date.class, new Aspect[]{aspect});
        Date proxy = (Date) aopProxy.create();
        proxy.setTime(500); // intercept

        CallDescription desc = debugInterceptor.description;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(desc);
        oos.close();
        byte[] result = baos.toByteArray();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(result);
        ObjectInputStream ois = new ObjectInputStream(bais);
        CallDescription actual = (CallDescription) ois.readObject();
        
        System.out.println(desc);
        System.out.println(actual);
        
    }
}
