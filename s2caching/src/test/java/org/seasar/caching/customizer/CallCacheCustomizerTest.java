package org.seasar.caching.customizer;

import org.seasar.caching.interceptor.CallCacheInterceptor;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.container.AspectDef;
import org.seasar.framework.container.ComponentDef;

public class CallCacheCustomizerTest extends S2TestCase {
    protected void setUp() throws Exception {
        super.setUp();
        include("CallCacheCustomizerTest.dicon");
    }

    public void testPartialSetup() throws NoSuchMethodException {
        ComponentDef cd = getComponentDef(CallCacheStab1.class);
        assertEquals(1, cd.getAspectDefSize());

        AspectDef ad = cd.getAspectDef(0);
        assertEquals(CallCacheInterceptor.class, ad.getAspect()
                .getMethodInterceptor().getClass());
        
        assertFalse(ad.getPointcut().isApplied( CallCacheStab1.class.getMethod("hoge")));
        assertTrue(ad.getPointcut().isApplied( CallCacheStab1.class.getMethod("fuga")));
        
        CallCacheStab1 ccs = (CallCacheStab1) getComponent(CallCacheStab1.class);
        assertEquals(0, ccs.hoge());
        assertEquals(1, ccs.hoge());
        assertEquals(2, ccs.fuga());
        assertEquals(3, ccs.hoge());
        assertEquals(4, ccs.hoge());
        assertEquals(2, ccs.fuga());
        
    }
    
    
    public static class CallCacheStab1 {
    	private int a = 0;
    	
    	public int hoge() {
    		return a++;
    	}
    	
    	@CallCache(componentName="callCache")
    	public int fuga() {
    		return a++;
    	}
    }
}
