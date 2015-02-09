package com.sxj.cache.core;

import java.util.List;

public class NullCache implements Cache
{
    
    /* (non-Javadoc)
     * @see net.oschina.j2cache.Cache#get(java.lang.Object)
     */
    @Override
    public Object get(Object key) throws CacheException
    {
        return null;
    }
    
    /* (non-Javadoc)
     * @see net.oschina.j2cache.Cache#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public void put(Object key, Object value) throws CacheException
    {
        return;
    }
    
    /* (non-Javadoc)
     * @see net.oschina.j2cache.Cache#update(java.lang.Object, java.lang.Object)
     */
    @Override
    public void update(Object key, Object value) throws CacheException
    {
        return;
    }
    
    /* (non-Javadoc)
     * @see net.oschina.j2cache.Cache#keys()
     */
    @Override
    @SuppressWarnings("rawtypes")
    public List keys() throws CacheException
    {
        return null;
    }
    
    /* (non-Javadoc)
     * @see net.oschina.j2cache.Cache#remove(java.lang.Object)
     */
    @Override
    public void evict(Object key) throws CacheException
    {
        return;
    }
    
    /* (non-Javadoc)
     * @see net.oschina.j2cache.Cache#batchRemove(java.util.List)
     */
    @Override
    @SuppressWarnings("rawtypes")
    public void evict(List keys) throws CacheException
    {
        return;
    }
    
    /* (non-Javadoc)
     * @see net.oschina.j2cache.Cache#clear()
     */
    @Override
    public void clear() throws CacheException
    {
        return;
    }
    
    /* (non-Javadoc)
     * @see net.oschina.j2cache.Cache#destroy()
     */
    @Override
    public void destroy() throws CacheException
    {
        return;
    }
    
    @Override
    public Long size() throws CacheException
    {
        return 0l;
    }
    
    @Override
    public List values() throws CacheException
    {
        return null;
    }
    
    @Override
    public void put(Object key, Object value, int seconds)
            throws CacheException
    {
        return;
    }
    
    @Override
    public Boolean exists(Object key) throws CacheException
    {
        return false;
    }
    
}
