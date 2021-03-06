package com.sxj.supervisor.service.impl.open.member;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sxj.supervisor.dao.member.IAccountDao;
import com.sxj.supervisor.dao.member.IMemberDao;
import com.sxj.supervisor.entity.member.AccountEntity;
import com.sxj.supervisor.entity.member.MemberEntity;
import com.sxj.supervisor.enu.member.AccountStatesEnum;
import com.sxj.supervisor.model.member.AccountQuery;
import com.sxj.supervisor.service.open.member.IAccountService;
import com.sxj.supervisor.service.open.member.IMemberService;
import com.sxj.util.common.EncryptUtil;
import com.sxj.util.common.NumberUtils;
import com.sxj.util.common.StringUtils;
import com.sxj.util.exception.ServiceException;
import com.sxj.util.logger.SxjLogger;
import com.sxj.util.persistent.QueryCondition;

@Service(value = IAccountService.SERVICE_NAME)
@Transactional
public class AccountServiceImpl implements IAccountService {

	@Autowired
	private IAccountDao accountDao;

	@Autowired
	private IMemberDao memberDao;

	@Autowired
	private IMemberService memberService;

	/**
	 * 新增子会员
	 */
	@Override
	@Transactional
	public void addAccount(AccountEntity account, String[] functionIds)
			throws ServiceException {
	}

	/**
	 * 更新子会员
	 */
	@Override
	@Transactional
	public void modifyAccount(AccountEntity account, String[] functionIds)
			throws ServiceException {

	}

	/**
	 * 查询子会员
	 */
	@Override
	public AccountEntity getAccount(String id) throws ServiceException {
		try {
			AccountEntity account = accountDao.getAccount(id);
			return account;
		} catch (Exception e) {
			SxjLogger.error("子会员查询错误", e, this.getClass());
			throw new ServiceException("子会员查询错误错误", e);
		}

	}

	/**
	 * 子会员高级查询
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AccountEntity> queryAccounts(AccountQuery query)
			throws ServiceException {
		try {
			QueryCondition<AccountEntity> condition = new QueryCondition<AccountEntity>();
			List<AccountEntity> aacountList = new ArrayList<AccountEntity>();
			if (query == null) {
				return aacountList;
			}
			condition.addCondition("name", query.getName());// 姓名
			condition.addCondition("parentId", query.getMemberNo());// 父会员号
			condition.addCondition("accountNo", query.getAccountNo());// 子会员
			condition.addCondition("accountName", query.getAccountName());// 子会员名称
			condition.addCondition("state", query.getState());// 子账户状态
			condition.addCondition("delstate", query.getDelstate());// 删除标记
			condition.addCondition("startDate", query.getStartDate());// 开始时间
			condition.addCondition("endDate", query.getEndDate());// 结束时间
			condition.addCondition("functionId", query.getFunctionId());// 权限ＩＤ
			condition.setPage(query);
			aacountList = accountDao.queryAccount(condition);
			query.setPage(condition);
			return aacountList;
		} catch (Exception e) {
			SxjLogger.error("子会员高级查询错误", e, this.getClass());
			throw new ServiceException("子会员高级查询错误错误", e);
		}

	}

	/**
	 * 删除子会员
	 */
	@Override
	@Transactional
	public void reomveAccount(String id) {
		try {
			AccountEntity account = getAccount(id);
			accountDao.updateAccount(account);
			MemberEntity member = memberService.memberInfo(account
					.getParentId());
			member.setAccountNum(member.getAccountNum() - 1);
			memberService.modifyMember(member);
		} catch (Exception e) {
			SxjLogger.error("删除子账户信息错误", e, this.getClass());
			throw new ServiceException("删除子账户错误", e);
		}
	}

	/**
	 * 更新子会员状态
	 */
	@Override
	@Transactional
	public String editState(String id, Integer state) throws ServiceException {
		try {
			AccountEntity account = new AccountEntity();
			account.setId(id);
			account.setState(AccountStatesEnum.getEnum(state));
			account.setVersion(accountDao.getAccount(id).getVersion());
			accountDao.updateAccount(account);
			return AccountStatesEnum.getEnum(state).getName();

		} catch (Exception e) {
			SxjLogger.error("更新子账户状态错误", e, this.getClass());
			throw new ServiceException("更新子账户状态错误", e);
		}

	}

	/**
	 * 初始化密码
	 */
	@Override
	@Transactional
	public String initializePwd(String id) throws ServiceException {
		try {

			int rondom = NumberUtils.getRandomIntInMax(999999);
			String password = StringUtils.getLengthStr(rondom + "", 6, '0');
			AccountEntity account = new AccountEntity();
			account.setId(id);
			account.setPassword(EncryptUtil.md5Hex(password));
			account.setVersion(accountDao.getAccount(id).getVersion());
			accountDao.updateAccount(account);
			return password;
		} catch (Exception e) {
			SxjLogger.error("初始化密码错误", e, this.getClass());
			throw new ServiceException("初始化密码错误", e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public AccountEntity getAccountByName(String accountName, String memberNo)
			throws ServiceException {
		try {
			if (StringUtils.isEmpty(accountName)) {
				return null;
			}
			QueryCondition<AccountEntity> condition = new QueryCondition<AccountEntity>();
			condition.addCondition("parentId", memberNo);// 父会员号
			condition.addCondition("accountName", accountName);// 子会员名称
			List<AccountEntity> list = accountDao
					.getAccountByAccountName(condition);
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
			return null;
		} catch (Exception e) {
			SxjLogger.error("根据子账号查询信息错误", e, this.getClass());
			throw new ServiceException("根据子账号查询信息错误", e.getMessage());
		}
	}

	@Override
	public String edit_pwd(String id, String pwd) throws ServiceException {
		try {
			AccountEntity account = new AccountEntity();
			account.setId(id);
			account.setPassword(EncryptUtil.md5Hex(pwd));
			account.setVersion(accountDao.getAccount(id).getVersion());
			accountDao.updateAccount(account);
		} catch (Exception e) {
			SxjLogger.error("密码更新错误", e, this.getClass());
			throw new ServiceException("密码更新错误", e.getMessage());
		}
		return null;
	}

	@Override
	public void edit_Login(String id) throws ServiceException {
		try {
			AccountEntity account = new AccountEntity();
			account.setId(id);
			account.setLastLogin(new Date());
			account.setVersion(accountDao.getAccount(id).getVersion());
			accountDao.updateAccount(account);
		} catch (Exception e) {
			SxjLogger.error("登陆时间更新错误", e, this.getClass());
			throw new ServiceException("登陆时间更新错误", e.getMessage());
		}

	}

	@Override
	public AccountEntity getAccountByAccountNo(String userId)
			throws ServiceException {
		try {
			AccountQuery query = new AccountQuery();
			query.setAccountNo(userId);
			List<AccountEntity> list = queryAccounts(query);
			if (list.size() > 0) {
				return list.get(0);
			}
		} catch (Exception e) {
			SxjLogger.error("查询用户信息错误", e, this.getClass());
			throw new ServiceException("查询用户信息错误", e.getMessage());
		}
		return null;
	}
}
