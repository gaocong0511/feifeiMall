package com.fMall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by 高琮 on 2018/5/3.
 */
public class TokenCache {

    public static String TOKEN_PREFIX="token_";
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
    //LRU算法
    private static LoadingCache<String, String> localCache = CacheBuilder.
            newBuilder().
            initialCapacity(1000).
            maximumSize(10000).
            expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
        //默认的数据加载实现,当调用get时，如果key没有对应的值的话，就调用这个方法进行加载
        @Override
        public String load(String s) throws Exception {
            return "null";
        }
    });


    /**
     * 向cache之中添加一个值的方法
     * @param key 键
     * @param value 值
     */
    public static void setKey(String key,String value){
        localCache.put(key,value);
    }

    /**
     * 从cache之中取值
     * @param key 键
     * @return 返回取出的值
     */
    public static String getKey(String key){
        String value=null;
        try {
            value= localCache.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        } catch (ExecutionException e) {
            logger.error("localCache get error",e);
        }
        return value;
    }
}
