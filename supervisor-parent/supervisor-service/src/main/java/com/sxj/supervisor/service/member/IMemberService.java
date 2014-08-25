package com.sxj.supervisor.service.member;

import java.util.List;

import com.sxj.supervisor.entity.member.MemberEntity;
import com.sxj.supervisor.model.member.MemberQuery;
import com.sxj.util.exception.ServiceException;

public interface IMemberService {

	public void addMember(MemberEntity member) throws ServiceException;

	public void modifyMember(MemberEntity member) throws ServiceException;

	public String initializePwd(String memberId) throws ServiceException;

	public MemberEntity getMember(String id) throws ServiceException;

	public List<MemberEntity> queryMembers(MemberQuery query)
			throws ServiceException;

	public void removeMember(String id) throws ServiceException;
	
	public String editState(String id);
	
	public String editCheckState(String id);
}
