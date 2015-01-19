package com.sxj.redis.advance.collections;

import io.netty.util.concurrent.Future;

import java.util.concurrent.TimeUnit;

import com.sxj.redis.RedisAsyncConnection;
import com.sxj.redis.advance.RedisExpirable;
import com.sxj.redis.advance.async.ResultOperation;
import com.sxj.redis.advance.async.VoidOperation;
import com.sxj.redis.advance.connection.manager.ConnectionManager;
import com.sxj.redis.advance.core.RBucket;

public class RedisBucket<V> extends RedisExpirable implements RBucket<V>
{
    
    public RedisBucket(ConnectionManager connectionManager, String name)
    {
        super(connectionManager, name);
    }
    
    @Override
    public V get()
    {
        return getConnectionManager().get(getAsync());
    }
    
    @Override
    public Future<V> getAsync()
    {
        return getConnectionManager().readAsync(new ResultOperation<V, V>()
        {
            @Override
            public Future<V> execute(RedisAsyncConnection<Object, V> async)
            {
                return async.get(getName());
            }
        });
    }
    
    @Override
    public void set(V value)
    {
        getConnectionManager().get(setAsync(value));
    }
    
    @Override
    public Future<Void> setAsync(final V value)
    {
        return getConnectionManager().writeAsync(new VoidOperation<V, String>()
        {
            @Override
            public Future<String> execute(RedisAsyncConnection<Object, V> async)
            {
                return async.set(getName(), value);
            }
        });
    }
    
    @Override
    public void set(V value, long timeToLive, TimeUnit timeUnit)
    {
        getConnectionManager().get(setAsync(value, timeToLive, timeUnit));
    }
    
    @Override
    public Future<Void> setAsync(final V value, final long timeToLive,
            final TimeUnit timeUnit)
    {
        return getConnectionManager().writeAsync(new VoidOperation<V, String>()
        {
            @Override
            public Future<String> execute(RedisAsyncConnection<Object, V> async)
            {
                return async.setex(getName(),
                        timeUnit.toSeconds(timeToLive),
                        value);
            }
        });
    }
    
    @Override
    public boolean exists()
    {
        return getConnectionManager().get(existsAsync());
    }
    
    @Override
    public Future<Boolean> existsAsync()
    {
        return getConnectionManager().readAsync(new ResultOperation<Boolean, V>()
        {
            @Override
            public Future<Boolean> execute(RedisAsyncConnection<Object, V> async)
            {
                return async.exists(getName());
            }
        });
    }
    
}
