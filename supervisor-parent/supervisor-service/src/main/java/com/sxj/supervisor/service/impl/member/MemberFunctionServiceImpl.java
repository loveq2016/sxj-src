package com.sxj.supervisor.service.impl.member;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sxj.supervisor.dao.member.IMemberFunctionDao;
import com.sxj.supervisor.entity.member.MemberFunctionEntity;
import com.sxj.supervisor.model.member.MemberFunctionModel;
import com.sxj.supervisor.service.member.IMemberFunctionService;
import com.sxj.util.common.StringUtils;
import com.sxj.util.exception.ServiceException;
import com.sxj.util.logger.SxjLogger;
import com.sxj.util.persistent.QueryCondition;

@Service
public class MemberFunctionServiceImpl implements IMemberFunctionService
{
    
    @Autowired
    private IMemberFunctionDao functionDao;
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<MemberFunctionEntity> queryChildrenFunctions(String parentId,
            Integer flag) throws ServiceException
    {
        try
        {
            QueryCondition<MemberFunctionEntity> query = new QueryCondition<MemberFunctionEntity>();
            query.addCondition("parentId", parentId);
            query.addCondition("flag", flag);
            List<MemberFunctionEntity> list = functionDao.queryFunctions(query);
            return list;
        }
        catch (Exception e)
        {
            SxjLogger.error("查询会员菜单错误", e, this.getClass());
            throw new ServiceException("查询会员菜单错误", e);
        }
        
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<MemberFunctionModel> queryFunctions(Integer flag)
            throws ServiceException
    {
        try
        {
            QueryCondition<MemberFunctionEntity> query = new QueryCondition<MemberFunctionEntity>();
            query.addCondition("parentId", "0");
            query.addCondition("flag", flag);
            List<MemberFunctionEntity> functionList = functionDao.queryFunctions(query);
            List<MemberFunctionModel> list = new ArrayList<MemberFunctionModel>();
            for (MemberFunctionEntity functionEntity : functionList)
            {
                if (functionEntity == null)
                {
                    continue;
                }
                QueryCondition<MemberFunctionEntity> childrenQuery = new QueryCondition<MemberFunctionEntity>();
                childrenQuery.addCondition("parentId", functionEntity.getId());
                query.addCondition("flag", flag);
                List<MemberFunctionEntity> childrenList = functionDao.queryFunctions(childrenQuery);
                MemberFunctionModel model = new MemberFunctionModel();
                model.setFunction(functionEntity);
                model.setChildren(childrenList);
                list.add(model);
                
            }
            return list;
        }
        catch (Exception e)
        {
            SxjLogger.error("查询所有会员菜单错误", e, this.getClass());
            throw new ServiceException("查询所有会员菜单错误", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public MemberFunctionEntity getFunction(String functionId)
            throws ServiceException
    {
        try
        {
            if (StringUtils.isEmpty(functionId))
            {
                return null;
            }
            MemberFunctionEntity function = functionDao.getFunction(functionId);
            return function;
        }
        catch (Exception e)
        {
            SxjLogger.error("查询会员菜单错误", e, this.getClass());
            throw new ServiceException("查询会员菜单错误", e);
        }
    }
    
}
