package com.sxj.cache.manager;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sxj.cache.core.Cache;
import com.sxj.cache.core.CacheException;
import com.sxj.cache.core.CacheExpiredListener;
import com.sxj.cache.core.CacheProvider;
import com.sxj.cache.core.NullCacheProvider;
import com.sxj.cache.ehcache.EhCacheProvider;
import com.sxj.cache.redis.RedisCacheProvider;
import com.sxj.spring.modules.util.ClassLoaderUtil;

/**
 * 缓存管理器
 */
public class HierarchicalCacheManager
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HierarchicalCacheManager.class);
    
    private static final String CONFIG_FILE = "cache.properties";
    
    private static final String L1_PROVIDER_CLASS = "cache.L1.provider_class";
    
    private static final String L2_PROVIDER_CLASS = "cache.L2.provider_class";
    
    private String configFile;
    
    private String databaseId;
    
    private static CacheProvider l1_provider;
    
    private static CacheProvider l2_provider;
    
    private static CacheExpiredListener listener;
    
    public void initCacheProvider()
    {
        initCacheProvider(null);
    }
    
    public void initCacheProvider(CacheExpiredListener listener)
    {
        initCacheProvider(CONFIG_FILE, listener);
    }
    
    public void initCacheProvider(String configFile,
            CacheExpiredListener listener)
    {
        initCacheProvider(configFile, 1, listener);
    }
    
    public void initCacheProvider(String configFile, int databaseId,
            CacheExpiredListener listener)
    {
        final String path = getConfigFile() == null ? configFile
                : getConfigFile();
        InputStream configStream = null;
        try
        {
            configStream = ClassLoaderUtil.getResource(path);
            Properties props = new Properties();
            HierarchicalCacheManager.listener = listener;
            props.load(configStream);
            props.setProperty("redis.database",
                    props.getProperty("redis.database",
                            String.valueOf(databaseId)));
            configStream.close();
            if (props.getProperty(L1_PROVIDER_CLASS) == null
                    && props.getProperty(L2_PROVIDER_CLASS) == null)
                throw new CacheException(
                        "At lease one provider_class should be defined!");
            startL1Provider(props);
            startL2Provider(props);
            configStream.close();
        }
        catch (Exception e)
        {
            throw new CacheException("Unabled to initialize cache providers", e);
        }
    }
    
    private void startL2Provider(Properties props)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException
    {
        if (props.getProperty(L2_PROVIDER_CLASS) != null)
        {
            HierarchicalCacheManager.l2_provider = getProviderInstance(props.getProperty(L2_PROVIDER_CLASS));
            HierarchicalCacheManager.l2_provider.start(getProviderProperties(props,
                    HierarchicalCacheManager.l2_provider));
            LOGGER.info("Using L2 CacheProvider : "
                    + l2_provider.getClass().getName());
        }
    }
    
    private void startL1Provider(Properties props)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException
    {
        if (props.getProperty(L1_PROVIDER_CLASS) != null)
        {
            HierarchicalCacheManager.l1_provider = getProviderInstance(props.getProperty(L1_PROVIDER_CLASS));
            HierarchicalCacheManager.l1_provider.start(getProviderProperties(props,
                    HierarchicalCacheManager.l1_provider));
            LOGGER.info("Using L1 CacheProvider : "
                    + l1_provider.getClass().getName());
        }
    }
    
    private static final CacheProvider getProviderInstance(String value)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException
    {
        if ("ehcache".equalsIgnoreCase(value))
            return new EhCacheProvider();
        if ("redis".equalsIgnoreCase(value))
            return new RedisCacheProvider();
        if ("none".equalsIgnoreCase(value))
            return new NullCacheProvider();
        return (CacheProvider) Class.forName(value).newInstance();
    }
    
    private static final Properties getProviderProperties(Properties props,
            CacheProvider provider)
    {
        Properties tmp = new Properties();
        Enumeration<Object> keys = props.keys();
        String prefix = provider.name() + '.';
        while (keys.hasMoreElements())
        {
            String key = (String) keys.nextElement();
            if (key.startsWith(prefix))
                tmp.setProperty(key.substring(prefix.length()),
                        props.getProperty(key));
        }
        return tmp;
    }
    
    private static final Cache getCache(CacheLevel level, String cacheName,
            boolean autoCreate)
    {
        switch (level)
        {
            case EHCACHE:
                return l1_provider.buildCache(cacheName, autoCreate, listener);
            case REDIS:
                return l2_provider.buildCache(cacheName, autoCreate, listener);
            default:
                return new NullCacheProvider().buildCache(cacheName,
                        autoCreate,
                        listener);
        }
    }
    
    public static final void shutdown(int level)
    {
        ((level == 1) ? l1_provider : l2_provider).stop();
    }
    
    /**
     * 获取缓存中的数据
     * @param level
     * @param name
     * @param key
     * @return
     */
    public static final Object get(CacheLevel level, String name, Object key)
    {
        if (name != null && key != null)
        {
            Cache cache = getCache(level, name, false);
            if (cache != null)
                return cache.get(key);
        }
        return null;
    }
    
    /**
     * 获取缓存中的数据
     * @param <T>
     * @param level
     * @param resultClass
     * @param name
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public static final <T> T get(CacheLevel level, Class<T> resultClass,
            String name, Object key)
    {
        if (name != null && key != null)
        {
            Cache cache = getCache(level, name, false);
            if (cache != null)
            {
                Object object = cache.get(key);
                if (resultClass.isInstance(object))
                    throw new CacheException("Class mismatched!!");
                return (T) object;
            }
        }
        return null;
    }
    
    /**
     * 写入缓存
     * @param level
     * @param name
     * @param key
     * @param value
     */
    public static final void set(CacheLevel level, String name, Object key,
            Object value)
    {
        if (name != null && key != null && value != null)
        {
            Cache cache = getCache(level, name, true);
            if (cache != null)
                cache.put(key, value);
        }
    }
    
    public static final void set(CacheLevel level, String name, Object key,
            Object value, int seconds)
    {
        if (name != null && key != null && value != null)
        {
            Cache cache = getCache(level, name, true);
            if (cache != null)
                if (seconds <= 0)
                    cache.put(key, value);
                else
                    cache.put(key, value, seconds);
        }
    }
    
    /**
     * 清除缓存中的某个数据
     * @param level
     * @param name
     * @param key
     */
    public static final void evict(CacheLevel level, String name, Object key)
    {
        if (name != null && key != null)
        {
            Cache cache = getCache(level, name, false);
            if (cache != null)
                cache.evict(key);
        }
    }
    
    public static final Boolean exists(CacheLevel level, String name, Object key)
    {
        if (name != null && key != null)
        {
            Cache cache = getCache(level, name, false);
            if (cache != null)
                return cache.exists(key);
        }
        return false;
    }
    
    /**
     * 批量删除缓存中的一些数据
     * @param level
     * @param name
     * @param keys
     */
    @SuppressWarnings("rawtypes")
    public static final void batchEvict(CacheLevel level, String name, List keys)
    {
        if (name != null && CollectionUtils.isNotEmpty(keys))
        {
            Cache cache = getCache(level, name, false);
            if (cache != null)
                cache.evict(keys);
        }
    }
    
    /**
     * Clear the cache
     */
    public static final void clear(CacheLevel level, String name)
    {
        Cache cache = getCache(level, name, false);
        if (cache != null)
            cache.clear();
    }
    
    public static final List keys(CacheLevel level, String name)
    {
        Cache cache = getCache(level, name, false);
        return (cache != null) ? cache.keys() : null;
    }
    
    public static final Long size(CacheLevel level, String name)
    {
        Cache cache = getCache(level, name, false);
        return (cache != null) ? cache.size() : 0L;
    }
    
    public static final List values(CacheLevel level, String name)
    {
        Cache cache = getCache(level, name, false);
        return (cache != null) ? cache.values() : null;
    }
    
    public String getConfigFile()
    {
        return configFile;
    }
    
    public void setConfigFile(String configFile)
    {
        this.configFile = configFile;
    }
    
    public String getDatabaseId()
    {
        return databaseId;
    }
    
    public void setDatabaseId(String databaseId)
    {
        this.databaseId = databaseId;
    }
    
}
