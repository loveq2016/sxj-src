
//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : 私享家架构设计  
//  @ File Name : IMemberDao.java
//  @ Date : 2014/8/11
//  @ Author : 
//
//
package com.sxj.supervisor.dao.member;

import java.util.List;

import com.sxj.supervisor.entity.member.MemberEntity;

public interface IMemberDao {
	/**
	 * 新增会员
	 *
	 * @param member
	 **/
	public void addMember(MemberEntity member);

	/**
	 * 更新会员
	 *
	 * @param member
	 **/
	public void updateMember(MemberEntity member);

	/**
	 * 获取会员信息
	 *
	 * @param id
	 **/
	public MemberEntity getMember(String id);

	/**
	 * 删除会员
	 *
	 * @param id
	 **/
	public void deleteMember(String id);

	/**
	 * 查询会员
	 *
	 * @param member
	 * @param memberList
	 **/
	public List<MemberEntity> queryMenbers(MemberEntity member);
}
