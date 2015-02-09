package com.sxj.redis.core.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.sxj.redis.core.RMap;
import com.sxj.redis.core.exception.RedisException;
import com.sxj.redis.core.impl.RedisExpirable;
import com.sxj.redis.core.provider.RedisProvider;

public class RedisMap<K, V> extends RedisExpirable implements RMap<K, V>
{
    
    public RedisMap(RedisProvider provider, String name)
    {
        super(provider, name);
    }
    
    @Override
    public V putIfAbsent(K key, V value)
    {
        Jedis jedis = provider.getResource();
        boolean broken = false;
        try
        {
            int hsetnx = jedis.hsetnx(name,
                    K_SERIALIZER.serialize(key),
                    V_SERIALIZER.serialize(value)).intValue();
            switch (hsetnx)
            {
                case 1:
                    return value;
                    
                default:
                    return null;
            }
        }
        catch (Exception e)
        {
            broken = true;
            throw new RedisException("", e);
        }
        finally
        {
            provider.returnResource(jedis, broken);
        }
    }
    
    private boolean isEqual(Transaction multi, Object key, Object object)
    {
        if (multi.exists(K_SERIALIZER.serialize(key)).get())
        {
            String hget = multi.hget(name, K_SERIALIZER.serialize(key)).get();
            Object deserialize = V_SERIALIZER.deserialize(hget);
            return deserialize.equals(object);
        }
        return false;
    }
    
    @Override
    public boolean remove(Object key, Object value)
    {
        Jedis jedis = provider.getResource();
        boolean broken = false;
        boolean retValue = false;
        try
        {
            Transaction multi = jedis.multi();
            
            if (isEqual(multi, key, value))
            {
                int intValue = multi.hdel(name, K_SERIALIZER.serialize(key))
                        .get()
                        .intValue();
                switch (intValue)
                {
                    case 1:
                        retValue = true;
                    default:
                        break;
                }
            }
            multi.exec();
        }
        catch (Exception e)
        {
            broken = true;
            throw new RedisException("", e);
        }
        finally
        {
            provider.returnResource(jedis, broken);
        }
        return retValue;
    }
    
    @Override
    public boolean replace(K key, V oldValue, V newValue)
    {
        Jedis jedis = provider.getResource();
        boolean retValue = false;
        boolean broken = false;
        try
        {
            Transaction multi = jedis.multi();
            if (isEqual(multi, key, oldValue))
            {
                int intValue = multi.hset(name,
                        K_SERIALIZER.serialize(key),
                        V_SERIALIZER.serialize(newValue))
                        .get()
                        .intValue();
                switch (intValue)
                {
                    case 1:
                        retValue = true;
                    default:
                        break;
                }
            }
        }
        catch (Exception e)
        {
            broken = true;
            throw new RedisException("", e);
        }
        finally
        {
            provider.returnResource(jedis, broken);
        }
        return retValue;
    }
    
    @Override
    public V replace(K key, V value)
    {
        Jedis jedis = provider.getResource();
        boolean broken = false;
        try
        {
            Transaction multi = jedis.multi();
            int intValue = multi.hset(name,
                    K_SERIALIZER.serialize(key),
                    V_SERIALIZER.serialize(value))
                    .get()
                    .intValue();
            switch (intValue)
            {
                case 1:
                    return value;
                default:
                    return null;
            }
        }
        catch (Exception e)
        {
            broken = true;
            throw new RedisException("", e);
        }
        finally
        {
            provider.returnResource(jedis, broken);
        }
        
    }
    
    @Override
    public int size()
    {
        Jedis jedis = provider.getResource();
        boolean broken = false;
        try
        {
            return jedis.hlen(name).intValue();
        }
        catch (Exception e)
        {
            broken = true;
            throw new RedisException("", e);
        }
        finally
        {
            provider.returnResource(jedis, broken);
        }
    }
    
    @Override
    public boolean isEmpty()
    {
        return size() == 0;
    }
    
    @Override
    public boolean containsKey(Object key)
    {
        Jedis jedis = provider.getResource();
        boolean broken = false;
        try
        {
            return jedis.hexists(name, K_SERIALIZER.serialize(key));
        }
        catch (Exception e)
        {
            broken = true;
            throw new RedisException("", e);
            
        }
        finally
        {
            provider.returnResource(jedis, broken);
        }
    }
    
    @Override
    public boolean containsValue(Object value)
    {
        Jedis jedis = provider.getResource();
        boolean broken = false;
        try
        {
            List<String> hvals = jedis.hvals(name);
            String serialize = V_SERIALIZER.serialize(value);
            return hvals.contains(serialize);
        }
        catch (Exception e)
        {
            broken = true;
            throw new RedisException("", e);
        }
        finally
        {
            provider.returnResource(jedis, broken);
        }
    }
    
    @Override
    public V get(Object key)
    {
        Jedis jedis = provider.getResource();
        boolean broken = false;
        try
        {
            String hget = jedis.hget(name, K_SERIALIZER.serialize(key));
            return (V) V_SERIALIZER.deserialize(hget);
        }
        catch (Exception e)
        {
            broken = true;
            throw new RedisException("", e);
        }
        finally
        {
            provider.returnResource(jedis, broken);
        }
    }
    
    @Override
    public V put(K key, V value)
    {
        Jedis jedis = provider.getResource();
        boolean broken = false;
        try
        {
            int hset = jedis.hset(name,
                    K_SERIALIZER.serialize(key),
                    V_SERIALIZER.serialize(value)).intValue();
            switch (hset)
            {
                case 1:
                    return value;
                    
                default:
                    return null;
            }
        }
        catch (Exception e)
        {
            broken = true;
            throw new RedisException("", e);
        }
        finally
        {
            provider.returnResource(jedis, broken);
        }
    }
    
    @Override
    public V remove(Object key)
    {
        
        Jedis jedis = provider.getResource();
        boolean broken = false;
        try
        {
            Transaction multi = jedis.multi();
            V retValue = null;
            String serializeKey = K_SERIALIZER.serialize(key);
            if (multi.exists(serializeKey).get())
            {
                V deserialize = (V) V_SERIALIZER.deserialize(multi.hget(name,
                        serializeKey).get());
                int hdel = multi.hdel(name, serializeKey).get().intValue();
                switch (hdel)
                {
                    case 1:
                        retValue = deserialize;
                        
                    default:
                        break;
                }
            }
            return retValue;
        }
        catch (Exception e)
        {
            broken = true;
            throw new RedisException("", e);
        }
        finally
        {
            provider.returnResource(jedis, broken);
        }
    }
    
    @Override
    public void putAll(Map<? extends K, ? extends V> m)
    {
        Jedis jedis = provider.getResource();
        boolean broken = false;
        try
        {
            Set<? extends K> keySet = m.keySet();
            Map<String, String> map = new HashMap<String, String>();
            for (K key : keySet)
            {
                map.put(K_SERIALIZER.serialize(key),
                        V_SERIALIZER.serialize(m.get(key)));
            }
            jedis.hmset(name, map);
        }
        catch (Exception e)
        {
            broken = true;
            throw new RedisException("", e);
        }
        finally
        {
            provider.returnResource(jedis, broken);
        }
    }
    
    @Override
    public void clear()
    {
        delete();
    }
    
    @Override
    public Set<K> keySet()
    {
        Jedis jedis = provider.getResource();
        boolean broken = false;
        try
        {
            Set<String> hkeys = jedis.hkeys(name);
            Set<K> retValue = new HashSet<K>();
            for (String key : hkeys)
            {
                retValue.add((K) K_SERIALIZER.deserialize(key));
            }
            return retValue;
        }
        catch (Exception e)
        {
            broken = true;
            throw new RedisException("", e);
        }
        finally
        {
            provider.returnResource(jedis, broken);
        }
    }
    
    @Override
    public Collection<V> values()
    {
        Jedis jedis = provider.getResource();
        boolean broken = false;
        try
        {
            List<String> hvals = jedis.hvals(name);
            List<V> retValue = new ArrayList<V>();
            for (String value : hvals)
            {
                retValue.add((V) V_SERIALIZER.deserialize(value));
            }
            return retValue;
        }
        catch (Exception e)
        {
            broken = true;
            throw new RedisException("", e);
        }
        finally
        {
            provider.returnResource(jedis, broken);
        }
    }
    
    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet()
    {
        
        Jedis jedis = provider.getResource();
        boolean broken = false;
        try
        {
            Map<String, String> hgetAll = jedis.hgetAll(name);
            Map<K, V> map = new HashMap<K, V>();
            Set<Entry<String, String>> entrySet = hgetAll.entrySet();
            for (Map.Entry<String, String> entry : entrySet)
            {
                map.put((K) K_SERIALIZER.deserialize(entry.getKey()),
                        (V) V_SERIALIZER.deserialize(entry.getValue()));
            }
            return map.entrySet();
        }
        catch (Exception e)
        {
            broken = true;
            throw new RedisException("", e);
        }
        finally
        {
            provider.returnResource(jedis, broken);
        }
    }
    
    @Override
    public Map<K, V> getAll(Set<K> keys)
    {
        Jedis jedis = provider.getResource();
        boolean broken = false;
        try
        {
            Set<String> serialzeKeys = new HashSet<String>();
            for (K key : keys)
            {
                serialzeKeys.add(K_SERIALIZER.serialize(key));
            }
            List<String> hmget = jedis.hmget(name,
                    serialzeKeys.toArray(new String[serialzeKeys.size()]));
            Map<K, V> map = new HashMap<K, V>();
            int index = 0;
            for (K key : keys)
            {
                map.put(key, (V) V_SERIALIZER.deserialize(hmget.get(index)));
            }
            return map;
        }
        catch (Exception e)
        {
            broken = true;
            throw new RedisException("", e);
        }
        finally
        {
            provider.returnResource(jedis, broken);
        }
    }
    
}
