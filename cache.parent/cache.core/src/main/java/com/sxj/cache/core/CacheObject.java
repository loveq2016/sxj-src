package com.sxj.cache.core;

public class CacheObject
{
    
    private String region;
    
    private Object key;
    
    private Object value;
    
    private byte level;
    
    public String getRegion()
    {
        return region;
    }
    
    public void setRegion(String region)
    {
        this.region = region;
    }
    
    public Object getKey()
    {
        return key;
    }
    
    public void setKey(Object key)
    {
        this.key = key;
    }
    
    public Object getValue()
    {
        return value;
    }
    
    public void setValue(Object value)
    {
        this.value = value;
    }
    
    public byte getLevel()
    {
        return level;
    }
    
    public void setLevel(byte level)
    {
        this.level = level;
    }
    
}
