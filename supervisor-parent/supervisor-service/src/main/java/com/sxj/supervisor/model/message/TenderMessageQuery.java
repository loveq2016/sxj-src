package com.sxj.supervisor.model.message;

import java.io.Serializable;

import com.sxj.mybatis.pagination.Pagable;

public class TenderMessageQuery extends Pagable implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 3504609484830822738L;
    
    private String memberNo;
    
    public String getMemberNo()
    {
        return memberNo;
    }
    
    public void setMemberNo(String memberNo)
    {
        this.memberNo = memberNo;
    }
    
}
