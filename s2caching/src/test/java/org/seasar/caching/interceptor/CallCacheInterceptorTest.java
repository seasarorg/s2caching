package org.seasar.caching.interceptor;

import java.io.IOException;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.aop.Aspect;
import org.seasar.framework.aop.Pointcut;
import org.seasar.framework.aop.impl.AspectImpl;
import org.seasar.framework.aop.impl.PointcutImpl;
import org.seasar.framework.aop.proxy.AopProxy;

public class CallCacheInterceptorTest extends S2TestCase {
    public static class TestClass {
        public int hoge = 0;
        
        public String getValue() {
            hoge=hoge+1;
            return "value";
        }
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        include("CallCacheInterceptorTest.dicon");
    }
    
    public void testシリアライズ復元性() throws IOException, ClassNotFoundException {
        Pointcut pointcut = new PointcutImpl(new String[]{"getValue"});
        CallCacheInterceptor interceptor = (CallCacheInterceptor) getComponent("callCache");
        Aspect aspect = new AspectImpl(interceptor, pointcut);
        AopProxy aopProxy = new AopProxy(TestClass.class, new Aspect[]{aspect});
        TestClass mock = (TestClass) aopProxy.create();

        assertEquals("value", mock.getValue());
        assertEquals("value", mock.getValue());
        assertEquals("value", mock.getValue());
        assertEquals(1, mock.hoge);
    }
}
