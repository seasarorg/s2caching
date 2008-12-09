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
 * CallCacheInterceptor�̃L���b�V�����p�[�W���邽�߂Ɏg�p����interceptor.<br />
 * Dao�ɑ΂��ēK�p����ꍇ�A�ȉ��̂悤�ɗp����.
 * <ul>
 *  <li>get��find�Ȃǂ̎擾�n���\�b�h�� CallCacheInterceptor���Z�b�g����
 *  <li>update,insert,delete,set�Ȃǂ̍X�V�n���\�b�h�� CallCachePurgeInterceptor���Z�b�g����
 *  <li>��Interceptor�������L���b�V���̈�����ɍs���悤�� �R���X�g���N�^�̑������𓙂����L�[������ɂ���
 *  <li>���Ȃ��������CacheManager���Q�Ƃɂ����悤�ɑ������𓙂������̂ɂ���
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
     * @param cacheManager ehCache�̃L���b�V���}�l�[�W��
     * @param cacheName �L���b�V������(�L���b�V���}�l�[�W���ɑ΂���L�[)
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
