package com.sxj.supervisor.dao.message;

import java.util.List;

import com.sxj.mybatis.orm.annotations.BatchInsert;
import com.sxj.supervisor.entity.message.SystemMessageEntity;
import com.sxj.supervisor.model.message.SystemMessageModel;
import com.sxj.util.persistent.QueryCondition;

public interface ISystemMessageDao
{
    @BatchInsert
    public void addMessage(List<SystemMessageEntity> message);
    
    public void deleteMessage(String infoId);
    
    public List<SystemMessageModel> queryMessageInfo(
            QueryCondition<SystemMessageModel> query);
}
