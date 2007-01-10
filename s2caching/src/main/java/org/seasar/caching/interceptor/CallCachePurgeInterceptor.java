/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.caching.interceptor;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;

/**
 * CallCacheInterceptorのキャッシュをパージするために使用するinterceptor.<br />
 * Daoに対して適用する場合、以下のように用いる.
 * <ul>
 *  <li>getやfindなどの取得系メソッドに CallCacheInterceptorをセットする
 *  <li>update,insert,delete,setなどの更新系メソッドに CallCachePurgeInterceptorをセットする
 *  <li>両Interceptorが同じキャッシュ領域を見に行くように コンストラクタの第二引数を等しいキー文字列にする
 *  <li>おなじく同一のCacheManagerを参照にいくように第一引数を等しいものにする
 * </ul>
 * 
 * @author taniguchi
 */
public class CallCachePurgeInterceptor extends AbstractInterceptor {
    private static final Log logger = LogFactory.getLog(CallCachePurgeInterceptor.class);
    
    private final Cache cache;
    private final CacheManager cacheManager;
    private final String cacheName;

    /**
     * constructor
     * 
     * @param cacheManager ehCacheのキャッシュマネージャ
     * @param cacheName キャッシュ名称(キャッシュマネージャに対するキー)
     * @throws CacheException
     */
    public CallCachePurgeInterceptor(CacheManager cacheManager, String cacheName) throws CacheException {
        this.cacheManager = cacheManager;
        this.cacheName = cacheName;
        if (!cacheManager.cacheExists(cacheName)) {
            cacheManager.addCache(cacheName);
        }
        this.cache = cacheManager.getCache(cacheName);
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        cache.removeAll();
        logger.debug("cache[" + cacheName + "] is purged.");
        return invocation.proceed();
    }
}
