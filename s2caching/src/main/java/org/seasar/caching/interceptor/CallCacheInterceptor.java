package org.seasar.caching.interceptor;

import java.io.Serializable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;

/**
 * ���\�b�h�ɑ΂���Ăяo�����L���b�V������Interceptor.
 * 
 * Dao�ɑ΂��ēK�p����ꍇ�A�ȉ��̂悤�ɗp����.
 * <ul>
 *  <li>get��find�Ȃǂ̎擾�n���\�b�h�� CallCacheInterceptor���Z�b�g����
 *  <li>update,insert,delete,set�Ȃǂ̍X�V�n���\�b�h�� CallCachePurgeInterceptor���Z�b�g����
 *  <li>��Interceptor�������L���b�V���̈�����ɍs���悤�� �R���X�g���N�^�̑������𓙂����L�[������ɂ���
 *  <li>���Ȃ��������CacheManager���Q�Ƃɂ����悤�ɑ������𓙂������̂ɂ���
 * </ul>
 *
 * ���Ɉȉ��̓_�ɒ��ӂ���K�v������
 * <ul>
 *  <li><b>�C���^�[�Z�v�g��̃C���X�^���X�̓L���b�V���̈�ɑ΂���singleton�łȂ��Ă͂Ȃ�Ȃ�.</b>
 *  �L���b�V���Ɋi�[����Ă���L�[�ɁA�ΏۃC���X�^���X�����ʂ���@�\���Ȃ��A���\�b�h�̃V�O�l�`���ƈ���
 *  �̃Z�b�g�݂̂��L�[�Ƃ��Ă��邽��.
 *   <li>���\�b�h�̈����͑S��Serializable�łȂ��ꍇ�̓L���b�V�����Ȃ�.
 *   <li>���\�b�h�̖߂�l�^��Serializable�łȂ��ꍇ�̓L���b�V�����Ȃ�.
 *   <li>��O���X���[���ꂽ�ꍇ�̓L���b�V�����Ȃ�.
 * </ul> 
 *  
 * @author taniguchi
 */
public class CallCacheInterceptor extends AbstractInterceptor {
    private static final Log logger = LogFactory.getLog(CallCacheInterceptor.class);
    
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
    public CallCacheInterceptor(CacheManager cacheManager, String cacheName) throws CacheException {
        this.cacheManager = cacheManager;
        this.cacheName = cacheName;
        if (!cacheManager.cacheExists(cacheName)) {
            cacheManager.addCache(cacheName);
        }
        this.cache = cacheManager.getCache(cacheName);
    }
    
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // �S�Ă̈�����Serializable�łȂ��ƃL���b�V���̖₢���킹�E�ǉ��ɈӖ��͂Ȃ�
        if (!isAllArgumentsSerializable(invocation)) {
            return invocation.proceed();
        }
        
        // �����͑S��Serializable�Ȃ̂� CallDescription�������\�B�L���b�V���₢���킹
        CallDescription description = new CallDescription(invocation);
        Element element = cache.get(description);
        if (element != null) {
            // cache ����
            // can assume originalResult instanceof Serializable.
            Serializable originalResult = element.getValue();
            return SerializationUtils.clone(originalResult);
        } else {
            // cache �Ȃ�
            Object result = invocation.proceed(); // ��O�������͏�ʂւ��̂܂܃X���[�A�L���b�V������Ȃ�
            
            if (result instanceof Serializable) {
                Element insertElement = new Element(description, (Serializable) result);
                cache.put(insertElement);
                Object copiedResult = SerializationUtils.clone((Serializable)result);
                return copiedResult;
            } else {
                logger.warn("return type is not Serializable (cache disable) : " + description);
                return result;
            }
        }
    }

    /**
     * @param invocation
     * @return
     */
    private boolean isAllArgumentsSerializable(MethodInvocation invocation) {
        Object[] arguments = invocation.getArguments();
        for (int i=0; i<arguments.length; i++) {
            if (!(arguments[i] instanceof Serializable)) {
                return false;
            }
        }
        return true;
    }
}
