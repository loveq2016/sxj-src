package com.sxj.supervisor.jsoup;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sxj.spring.modules.util.ClassLoaderUtil;
import com.sxj.supervisor.service.tasks.impl.WindDoorServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext_2.xml" })
public class SpringJsoupTest
{
    @Autowired
    WindDoorServiceImpl service;
    
    public void test()
    {
        service.WindDoorGather();
    }
    
    @Test
    public void testClassLoader() throws IOException
    {
        //        ResourcePatternUtils.getResourcePatternResolver(new PathMatchingResourcePatternResolver())
        //                .getResource("classpath*:/entities-base.properties")
        //                .getInputStream();
        //        new PathMatchingResourcePatternResolver().getResource("classpath:/entities-base.properties");
        ClassLoaderUtil.getResource("entities-base.properties");
    }
}