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
 * メソッドに対する呼び出しをキャッシュするInterceptor.
 * 
 * Daoに対して適用する場合、以下のように用いる.
 * <ul>
 *  <li>getやfindなどの取得系メソッドに CallCacheInterceptorをセットする
 *  <li>update,insert,delete,setなどの更新系メソッドに CallCachePurgeInterceptorをセットする
 *  <li>両Interceptorが同じキャッシュ領域を見に行くように コンストラクタの第二引数を等しいキー文字列にする
 *  <li>おなじく同一のCacheManagerを参照にいくように第一引数を等しいものにする
 * </ul>
 *
 * 特に以下の点に注意する必要がある
 * <ul>
 *  <li><b>インターセプト先のインスタンスはキャッシュ領域に対してsingletonでなくてはならない.</b>
 *  キャッシュに格納されているキーに、対象インスタンスを識別する機能がなく、メソッドのシグネチャと引数
 *  のセットのみをキーとしているため.
 *   <li>メソッドの引数は全てSerializableでない場合はキャッシュしない.
 *   <li>メソッドの戻り値型がSerializableでない場合はキャッシュしない.
 *   <li>例外がスローされた場合はキャッシュしない.
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
     * @param cacheManager ehCacheのキャッシュマネージャ
     * @param cacheName キャッシュ名称(キャッシュマネージャに対するキー)
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
        // 全ての引数がSerializableでないとキャッシュの問い合わせ・追加に意味はない
        if (!isAllArgumentsSerializable(invocation)) {
            return invocation.proceed();
        }
        
        // 引数は全てSerializableなので CallDescriptionが生成可能。キャッシュ問い合わせ
        CallDescription description = new CallDescription(invocation);
        Element element = cache.get(description);
        if (element != null) {
            // cache あり
            // can assume originalResult instanceof Serializable.
            Serializable originalResult = element.getValue();
            return SerializationUtils.clone(originalResult);
        } else {
            // cache なし
            Object result = invocation.proceed(); // 例外発生時は上位へそのままスロー、キャッシュされない
            
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
