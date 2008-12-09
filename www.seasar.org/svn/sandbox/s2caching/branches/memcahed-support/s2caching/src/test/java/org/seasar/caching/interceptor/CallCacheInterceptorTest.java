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
        
        public String getValue(int addition) {
        	hoge=hoge+addition;
        	return "value-int";
        }
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        include("CallCacheInterceptorTest.dicon");
    }

    public void testSameNameDifferentSignature() throws IOException, ClassNotFoundException {
        TestClass mock = createCachedMock();

        assertEquals("value", mock.getValue());
        assertEquals("value", mock.getValue());
        assertEquals("value", mock.getValue());
        assertEquals("value-int", mock.getValue(100));
        assertEquals("value-int", mock.getValue(100));
        assertEquals("value-int", mock.getValue(100));
        assertEquals(101, mock.hoge);
    }

    public void testSameMethodDifferentArgument() throws IOException, ClassNotFoundException {
        TestClass mock = createCachedMock();

        assertEquals("value-int", mock.getValue(100));
        assertEquals("value-int", mock.getValue(300));
        assertEquals("value-int", mock.getValue(500));
        assertEquals("value-int", mock.getValue(100));
        assertEquals("value-int", mock.getValue(300));
        assertEquals(900, mock.hoge);
    }

    public void testSingleInstanceCache() throws IOException, ClassNotFoundException {
        TestClass mock = createCachedMock();

        assertEquals("value", mock.getValue());
        assertEquals("value", mock.getValue());
        assertEquals("value", mock.getValue());
        assertEquals(1, mock.hoge);
    }
    
    public void testMultipleInstanceIndependentCache() throws IOException, ClassNotFoundException {
        TestClass mock = createCachedMock();
        TestClass mock2 = createCachedMock();
        
        assertEquals("value", mock.getValue());
        assertEquals("value", mock.getValue());
        assertEquals("value", mock.getValue());
        assertEquals(1, mock.hoge);
        assertEquals(0, mock2.hoge);
        
        assertEquals("value", mock2.getValue());
        assertEquals("value", mock2.getValue());
        assertEquals(1, mock.hoge);
        assertEquals(1, mock2.hoge);        
    }

	private TestClass createCachedMock() {
		Pointcut pointcut = new PointcutImpl(new String[]{"getValue"});
        CallCacheInterceptor interceptor = (CallCacheInterceptor) getComponent("callCache");
        Aspect aspect = new AspectImpl(interceptor, pointcut);
        AopProxy aopProxy = new AopProxy(TestClass.class, new Aspect[]{aspect});
        TestClass mock = (TestClass) aopProxy.create();
		return mock;
	}
}
