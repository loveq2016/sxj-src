package com.sxj.supervisor.service.rfid;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sxj.supervisor.entity.member.MemberEntity;
import com.sxj.supervisor.service.contract.IContractService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext.xml" })
//@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class ContractServiceImplTest
{
    @Autowired
    IContractService service;
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }
    
    @After
    public void tearDown() throws Exception
    {
    }
    
    @Test
    public void test()
    {
        MemberEntity member = new MemberEntity();
        service.updateContractLoss("AAAB13634,AAAB52969,AAAB52967",
                "CT14110098",
                "33b5XLlND7L0EA62c5qm0HYast3RJnXl",
                member,
                "AAAB52924");
    }
    
}
