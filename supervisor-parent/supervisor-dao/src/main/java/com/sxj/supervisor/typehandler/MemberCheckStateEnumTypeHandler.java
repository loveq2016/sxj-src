package com.sxj.supervisor.typehandler;

import org.apache.ibatis.type.EnumOrdinalTypeHandler;

import com.sxj.supervisor.entity.member.MemberTypeEnum;

public class MemberCheckStateEnumTypeHandler extends EnumOrdinalTypeHandler<MemberTypeEnum>{

	public MemberCheckStateEnumTypeHandler(Class<MemberTypeEnum> type) {
		super(type);
	}

}
