package com.sxj.util.comet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.sxj.redis.core.RAtomicLong;
import com.sxj.redis.core.RSet;
import com.sxj.redis.core.RTopic;
import com.sxj.redis.core.collections.RedisCollections;
import com.sxj.redis.core.concurrent.RedisConcurrent;
import com.sxj.redis.core.pubsub.RedisTopics;

public class CometServiceImpl implements BeanFactoryPostProcessor
{
    
    private static RedisCollections collections;
    
    private static RedisConcurrent redisConcurrent;
    
    private static RedisTopics redisTopics;
    
    @Override
    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory beanFactory) throws BeansException
    {
        collections = beanFactory.getBean(RedisCollections.class);
        redisConcurrent = beanFactory.getBean(RedisConcurrent.class);
        redisTopics = beanFactory.getBean(RedisTopics.class);
    }
    
    public static void add(String key, String obj)
    {
        // RLock lock = redisConcurrent.getLock(key + "_lock");
        try
        {
            // lock.lock();
            RSet<String> set = collections.getSet(key);
            set.add(obj);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            // lock.unlock();
        }
        
    }
    
    public static List<String> get(String key)
    {
        try
        {
            List<String> list = new ArrayList<String>();
            RSet<String> set = collections.getSet(key);
            if (set != null)
            {
                Iterator<String> iterator = set.iterator();
                if (iterator != null)
                {
                    while (iterator.hasNext())
                    {
                        list.add(iterator.next());
                    }
                }
            }
            
            return list;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static void remove(String key, String obj)
    {
        // RLock lock = redisConcurrent.getLock(key + "_lock");
        try
        {
            // lock.lock();
            RSet<String> set = collections.getSet(key);
            set.remove(obj);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            // lock.unlock();
        }
        
    }
    
    public static void clear(String key)
    {
        // RLock lock = redisConcurrent.getLock(key + "_lock");
        try
        {
            // lock.lock();
            RSet<String> set = collections.getSet(key);
            set.clear();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            // lock.unlock();
        }
        
    }
    
    public static Long getCount(String key)
    {
        RAtomicLong atomicLong = redisConcurrent.getAtomicLong(key);
        return atomicLong.get();
    }
    
    public static void setCount(String key, Long count)
    {
        RAtomicLong atomicLong = redisConcurrent.getAtomicLong(key);
        atomicLong.set(count);
    }
    
    public static Long takeCount(String key)
    {
        RAtomicLong atomicLong = redisConcurrent.getAtomicLong(key);
        long incrementAndGet = atomicLong.incrementAndGet();
        return incrementAndGet;
        
    }
    
    public static Long subCount(String key)
    {
        RAtomicLong atomicLong = redisConcurrent.getAtomicLong(key);
        //        Long now = atomicLong.get();
        //        if (now == 0)
        //            return 0L;
        //        while (!atomicLong.compareAndSet(atomicLong.get() - 1, now - 1))
        //        {
        //            now = atomicLong.get();
        //            if (now == 0)
        //                return 0L;
        //        }
        long incrementAndGet = atomicLong.decrementAndGet();
        if (incrementAndGet < 0)
            atomicLong.set(0L);
        return incrementAndGet;
        
    }
    
    public static void sendMessage(String channel, Object message)
    {
        RTopic<Object> topic = redisTopics.getTopic(channel);
        topic.publish(message);
    }
}
