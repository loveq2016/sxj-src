// Copyright (C) 2011 - Will Glozer.  All rights reserved.

package com.sxj.redis.protocol;

/**
 * Redis commands.
 *
 * @author Will Glozer
 */
public enum CommandType
{
    // Connection
    
    AUTH, ECHO, PING, QUIT, SELECT,
    
    // Server
    
    BGREWRITEAOF, BGSAVE, CLIENT, CONFIG, DBSIZE, DEBUG, FLUSHALL, FLUSHDB, INFO, LASTSAVE, MONITOR, SAVE, SHUTDOWN, SLAVEOF, SLOWLOG, SYNC,
    
    // Keys
    
    DEL, DUMP, EXISTS, EXPIRE, EXPIREAT, KEYS, MIGRATE, MOVE, OBJECT, PERSIST, PEXPIRE, PEXPIREAT, PTTL, RANDOMKEY, RENAME, RENAMENX, RESTORE, TTL, TYPE,
    
    // String
    
    APPEND, GET, GETRANGE, GETSET, MGET, MSET, MSETNX, SET, SETEX, SETNX, SETRANGE, STRLEN, PSETEX,
    
    // Numeric
    
    DECR, DECRBY, INCR, INCRBY, INCRBYFLOAT,
    
    // List
    
    BLPOP, BRPOP, BRPOPLPUSH, LINDEX, LINSERT, LLEN, LPOP, LPUSH, LPUSHX, LRANGE, LREM, LSET, LTRIM, RPOP, RPOPLPUSH, RPUSH, RPUSHX, SORT,
    
    // Hash
    
    HDEL, HEXISTS, HGET, HGETALL, HINCRBY, HINCRBYFLOAT, HKEYS, HLEN, HMGET, HMSET, HSET, HSETNX, HVALS,
    
    // Transaction
    
    DISCARD, EXEC, MULTI, UNWATCH, WATCH,
    
    // Pub/Sub
    
    PSUBSCRIBE, PUBLISH, PUNSUBSCRIBE, SUBSCRIBE, UNSUBSCRIBE,
    
    // Sets
    
    SADD, SCARD, SDIFF, SDIFFSTORE, SINTER, SINTERSTORE, SISMEMBER, SMEMBERS, SMOVE, SPOP, SRANDMEMBER, SREM, SUNION, SUNIONSTORE,
    
    // Sorted Set
    
    ZADD, ZCARD, ZCOUNT, ZINCRBY, ZINTERSTORE, ZRANGE, ZRANGEBYSCORE, ZRANK, ZREM, ZREMRANGEBYRANK, ZREMRANGEBYSCORE, ZREVRANGE, ZREVRANGEBYSCORE, ZREVRANK, ZSCORE, ZUNIONSTORE,
    
    TIME,
    
    // Scripting
    
    EVAL, EVALSHA, SCRIPT,
    
    // Bits
    
    BITCOUNT, BITOP, GETBIT, SETBIT,
    
    // HyperLogLog
    PFADD, PFCOUNT, PFMERGE,
    
    SENTINEL,
    
    SSCAN, ZSCAN, HSCAN,
    
    ASKING, CLUSTER;
    
    public byte[] bytes;
    
    private CommandType()
    {
        bytes = name().getBytes(Charsets.ASCII);
    }
}
