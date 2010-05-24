package org.seasar.caching.customizer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * このアノテーションを付加しているメソッドは、CallCacheCustomizerによって
 * メモ化/キャッシュのpointcutが追加されます
 * 
 * @author tanigon
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CallCache {
    String componentName();
}
