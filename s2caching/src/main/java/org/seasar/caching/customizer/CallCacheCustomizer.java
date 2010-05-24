package org.seasar.caching.customizer;

import java.lang.reflect.Method;

import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.customizer.AbstractCustomizer;
import org.seasar.framework.container.factory.AspectDefFactory;
import org.seasar.framework.util.StringUtil;

public class CallCacheCustomizer extends AbstractCustomizer {
	public CallCacheCustomizer() {
		
	}
	
	@Override
	protected void doCustomize(ComponentDef componentDef) {
        final Class<?> componentClass = componentDef.getComponentClass();
        //　TODO? class annotation に Cache がつけられるように考慮すべきか
        for (final Method method : componentClass.getMethods()) {
            if (method.isSynthetic() || method.isBridge()) {
                continue;
            }
            if (method.getDeclaringClass() == Object.class) {
                continue;
            }
            final CallCache callCache = method.getAnnotation(CallCache.class);
            if (callCache != null) {
	            final String interceptorName = callCache.componentName();
	            if (!StringUtil.isEmpty(interceptorName)) {
	                componentDef.addAspectDef(AspectDefFactory.createAspectDef(
	                        interceptorName, method));
	            }
            }
        }

	}
}
