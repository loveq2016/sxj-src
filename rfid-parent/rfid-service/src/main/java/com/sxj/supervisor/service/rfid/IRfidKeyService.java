package com.sxj.supervisor.service.rfid;

import com.sxj.util.exception.ServiceException;

public interface IRfidKeyService {

	public Long getKey() throws ServiceException;

}
