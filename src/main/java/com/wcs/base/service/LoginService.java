/**
 * LoginService.java
 * Created: 2012-1-12 上午10:58:58
 */
package com.wcs.base.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import com.google.common.collect.Lists;
import com.wcs.base.util.StringUtils;
import com.wcs.common.model.O;
import com.wcs.common.model.P;
import com.wcs.common.model.Resourcemstr;
import com.wcs.common.model.Rolemstr;
import com.wcs.common.model.S;
import com.wcs.common.model.Usermstr;
import com.wcs.tms.exception.ServiceException;
import com.wcs.tms.model.Company;

/**
 * <p>Project: btcbase</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chengchao@wcs-global.com">ChengChao</a>
 */
@Stateless
public class LoginService {

	private Log logger = LogFactory.getLog(LoginService.class.getName());

	@PersistenceContext(unitName = "pu")
	private EntityManager em;
	@Inject
	private EntityService entityService;
	public static final String SESSION_KEY_CURRENTUSR = "CURRENT_USER";
	public static final String SESSION_KEY_USER = "USER";
	public static final String SESSION_KEY_ROLES = "ROLES";
	public static final String SESSION_KEY_RESOURCES = "RESOURCES";
	public static final String SESSION_KEY_MENUS = "MENUS";

	// add by liushengbin 2012-11-20 当前浏览器语言
	public static final String SESSION_KEY_LOCALE = "CURRENT_LOCALE";
	
	public static final String SESSION_KEY_SAPCODE = "SAP_CODE";
	/**
	 * <p>Description: 返回当前用户</p>
	 * @return
	 */
	public String getCurrentUserName() {
		try {
			Subject currentUser = SecurityUtils.getSubject();
			return currentUser == null ? null : currentUser.getPrincipal().toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * <p>Description: 从session中，获取当前用户sapCode,多个用分号隔开</p>
	 * @return
	 */
	public static String getCurrentUserSapCode() {
		Subject currentUser = SecurityUtils.getSubject();
		Object obj = currentUser.getSession().getAttribute(LoginService.SESSION_KEY_SAPCODE);
		return obj == null ? "all" : (String)obj;
	}

	/**
	 * 
	 * <p>Description: 得到用户的工作流程队列</p>
	 * @return
	 */
	public List<String> getQueueByUser() {
		String account = getCurrentUserName();
		List<Rolemstr> roleList = getRoles(account);
		List<String> queueList = Lists.newArrayList();
		for (Rolemstr role : roleList) {
			String queueName = role.getQueueName();
			if (queueName != null) {
				String[] queue = queueName.split(",");
				int size = queue.length;
				for (int i = 0; i < size; i++) {
					if (!queueList.contains(queue[i])) {
						queueList.add(queue[i]);
					}
				}

			}
		}
		// add by Liushengbin 2012-10-30 增加 个人收件箱，每个用户增加用户队列Inbox(0)
		queueList.add("Inbox");
		return queueList;
	}

	/**
	 * 
	 * <p>Description: 根据用户账户得到所属公司列表</p>
	 * @param userAccount
	 * @return
	 * @throws ServiceException
	 */
	public List<O> finUserCompany(String userAccount) throws ServiceException {
		Validate.notNull(userAccount, "用户账户为空");
		List<O> os = new ArrayList<O>();
		try {
			StringBuilder puJpql = new StringBuilder();
			puJpql.append("select distinct pu.pernr from PU pu where pu.defunctInd='N' and pu.id=?1");
			List<String> pids = this.entityService.find(puJpql.toString(), userAccount);
			if (pids == null || pids.size() == 0) {
				return os;
			}

			StringBuilder psJpql = new StringBuilder();
			psJpql.append("select distinct ps.sid from PS ps where ps.defunctInd='N' and ps.pid in (?1)");
			List<String> sids = this.entityService.find(psJpql.toString(), pids);
			if (sids == null || sids.size() == 0) {
				return os;
			}

			StringBuilder oJpql = new StringBuilder();
			oJpql.append("select o from O o where o.defunctInd='N'");
			oJpql.append(" and o.id in (select s.oid from S s where s.id in (?1) )");
			os = this.entityService.find(oJpql.toString(), sids);

			return os;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * <p>Description: 根据Pid得到所属公司sap列表</p>
	 * @param userAccount
	 * @return
	 * @throws ServiceException
	 */
	public List<String> finSapByPid(String pid) throws ServiceException {
		try {
			List<String> saps = new ArrayList<String>();
			StringBuilder psJpql = new StringBuilder();
			psJpql.append("select distinct ps.sid from PS ps where ps.defunctInd='N' and ps.pid in (?1)");
			List<String> sids = this.entityService.find(psJpql.toString(), pid);
			if (sids == null || sids.size() == 0) {
				return saps;
			}

			StringBuilder oJpql = new StringBuilder();
			oJpql.append("select o.bukrs from O o where o.defunctInd='N'");
			oJpql.append(" and o.id in (select s.oid from S s where s.id in (?1) )");
			saps = this.entityService.find(oJpql.toString(), sids);

			return saps;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * PID得到ADCount
	 * @param pid
	 * @return
	 */
	public String getAdCountByPid(String pid) {
		StringBuilder jpql = new StringBuilder();
		jpql.append("select pu.id from PU pu where pu.pernr = '" + pid + "'");
		List<String> pids = this.entityService.find(jpql.toString());
		if (pids != null && pids.size() > 0) {
			return pids.get(0);
		}
		return null;
	}

	/**
	 * 
	 * <p>Description: 根据用户账户得到所属岗位列表</p>
	 * @param userAccount
	 * @return
	 * @throws ServiceException
	 */
	public List<S> finUserPositon(String userAccount) throws ServiceException {
		Validate.notNull(userAccount, "用户账户为空");
		List<S> ss = new ArrayList<S>();
		try {
			StringBuilder puJpql = new StringBuilder();
			puJpql.append("select distinct pu.pernr from PU pu where pu.defunctInd='N' and pu.id=?1");
			List<String> pids = this.entityService.find(puJpql.toString(), userAccount);
			if (pids == null || pids.size() == 0) {
				return ss;
			}

			StringBuilder psJpql = new StringBuilder();
			psJpql.append("select distinct ps.sid from PS ps where ps.defunctInd='N' and ps.pid in (?1)");
			List<String> sids = this.entityService.find(psJpql.toString(), pids);
			if (sids == null || sids.size() == 0) {
				return ss;
			}

			StringBuilder sJpql = new StringBuilder();
			sJpql.append("select s from S s where s.defunctInd='N' and s.id in (?1)");
			ss = this.entityService.find(sJpql.toString(), sids);
			return ss;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * <p>Description: 得到公司SAP代码</p>
	 * @param userAccount
	 * @return
	 */
	public List<String> findCompanySapCode() throws ServiceException {
		try {
			String userAccount = getCurrentUserName();
			List<O> oList = finUserCompany(userAccount);
			List<String> sapList = Lists.newArrayList();
			if (oList.isEmpty()) {
				return sapList;
			}
			for (O o : oList) {
				sapList.add(o.getBukrs());
			}
			return sapList;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 
	 * <p>Description: 得到某个用户的公司SAP代码字符串，多个用分号隔开</p>
	 * 
	 * 如果是集团用户返回all
	 * @param userAccount 用户帐号，传入空的字符串则取当前登录的帐号
	 * @return
	 */
	public String findSapCodeByAccount(String acccount) throws ServiceException {
		try {
			String userAccount = StringUtils.isBlankOrNull(acccount) ? getCurrentUserName():acccount;
			List<O> oList = finUserCompany(userAccount);
			List<String> saps = new ArrayList<String>();
			
			StringBuffer sapStr = new StringBuffer();
			
			if (oList.isEmpty()) {
				return "all";
			}
			for (O o : oList) {
				sapStr.append(o.getBukrs()).append(";");
				saps.add(o.getBukrs());
			}		
			//用户是否为集团用户,集团用户返回‘all’
			Boolean isGroupUser = Boolean.FALSE;
			
			String jpql = "select c from Company c where c.defunctInd='N' and c.sapCode in (?1)";
			List<Company> cs = entityService.find(jpql, saps);
			if (cs != null && cs.size() > 0) {
				for (Company c : cs) {
					if ("Y".equals(c.getCorporationFlag())) {
						isGroupUser = Boolean.TRUE;
					}
				}
			}			
			if(isGroupUser){
				return "all";
			}			
			
			return sapStr.toString();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * <p>Description:查询一个用户所在公司SAP代码 </p>
	 * @param account
	 * @return
	 * @throws ServiceException
	 */
	public List<String> findCompanySapCode(String account) throws ServiceException {
		try {
			List<O> oList = finUserCompany(account);
			List<String> sapList = Lists.newArrayList();
			if (oList.isEmpty()) {
				return sapList;
			}
			for (O o : oList) {
				sapList.add(o.getBukrs());
			}
			return sapList;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 当前登录用户是否为集团用户
	 * @return
	 */
	public boolean isCopUser() {
		List<String> saps = findCompanySapCode();
		String jpql = "select c from Company c where c.defunctInd='N' and c.sapCode in (?1)";
		List<Company> cs = entityService.find(jpql, saps);
		if (cs != null && cs.size() > 0) {
			for (Company c : cs) {
				if ("Y".equals(c.getCorporationFlag())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * AD账号得到用户姓名
	 * @param userAccount
	 * @return
	 */
	public String getCNNameByAccount(String userAccount) {
		StringBuilder puJpql = new StringBuilder();
		puJpql.append("select distinct pu.pernr from PU pu where pu.defunctInd='N' and pu.id=?1");
		List<String> pids = this.entityService.find(puJpql.toString(), userAccount);
		if (pids == null || pids.size() == 0) {
			return "";
		}

		StringBuilder pJpql = new StringBuilder();
		pJpql.append("select distinct p.nachn from P p where p.defunctInd='N' and p.id in (?1)");
		List<String> cns = this.entityService.find(pJpql.toString(), pids);
		if (cns == null || cns.size() == 0) {
			return "";
		}

		if (cns.get(0) != null && !"".equals(cns.get(0))) {
			return cns.get(0);
		}
		return "";
	}

	/**
	 * <p>Description: 获得当前Usermstr</p>
	 * @return
	 */
	public Usermstr getCurrentUsermstr() {
		Subject currentUser = SecurityUtils.getSubject();
		Object obj = currentUser.getSession().getAttribute(SESSION_KEY_USER);
		return obj == null ? null : (Usermstr) obj;
	}

	/**
	 * <p>Description: 获得当前用户的rolemstr列表</p>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Rolemstr> getCurrentRoles() {
		Subject currentUser = SecurityUtils.getSubject();
		Object obj = currentUser.getSession().getAttribute(SESSION_KEY_ROLES);
		return obj == null ? null : (List<Rolemstr>) obj;
	}

	/**
	 * <p>Description: 获得当前用户的资源列表（菜单、功能）</p>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Resourcemstr> getCurrentResources() {
		Subject currentUser = SecurityUtils.getSubject();
		Object obj = currentUser.getSession().getAttribute(SESSION_KEY_RESOURCES);
		return obj == null ? null : (List<Resourcemstr>) obj;
	}

	/**
	 * <p>Description: 获得当前用户的菜单</p>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TreeNode> getCurrentMenus() {
		Subject currentUser = SecurityUtils.getSubject();
		Object obj = currentUser.getSession().getAttribute(SESSION_KEY_MENUS);
		return obj == null ? null : (List<TreeNode>) obj;
	}

	/**
	 * <p>Description: 设置当前用户session</p>
	 * @param key
	 * @param value
	 */
	private void setCurrentUserSession(String key, Object value) {
		Subject currentUser = SecurityUtils.getSubject();
		currentUser.getSession().setAttribute(key, value);
	}

	public Usermstr findUser(String principal) {
		Usermstr usermstr = null;
		Query q = entityService.getEm().createQuery("select u from Usermstr u where u.defunctInd <> 'Y' and u.adAccount=:principal", Usermstr.class);
		q.setParameter("principal", principal);
		List<Usermstr> userList = q.getResultList();
		if (!userList.isEmpty()) {
			return userList.get(0);
		}
		return usermstr;
	}

	/**
	 * <p>Description: 判定用户名是否属于本系统合法用户，不是则返回false；是则返回true,并将用户信息、角色信息、资源信息添加到session中</p>
	 * @return
	 */
	public boolean isAppUser(String principal) {
		Usermstr usermstr = null;
		logger.info("judging isAppUser, find user from app...param:" + principal);
		if (principal == null || principal.trim().isEmpty()) {
			return false;
		}
		Query q = entityService.getEm().createQuery("select u from Usermstr u where u.defunctInd <> 'Y' and u.adAccount=:principal", Usermstr.class);
		q.setParameter("principal", principal);
		List<Usermstr> userList = q.getResultList();
		logger.info("judging isAppUser, find user from app...result:" + (userList.isEmpty() ? null : userList.get(0).getAdAccount()));
		if (!userList.isEmpty()) {
			usermstr = userList.get(0);
			setCurrentUserSession(SESSION_KEY_USER, usermstr);
			List<Rolemstr> roles = getRoles(principal);
			setCurrentUserSession(SESSION_KEY_ROLES, roles);
			logger.info(principal + "'s roles:" + roles.size());
			List<Resourcemstr> resources = getResources(principal);
			setCurrentUserSession(SESSION_KEY_RESOURCES, resources);
			logger.info(principal + "'s resources:" + resources.size());
			List<TreeNode> menuTree = getMenuTree(resources);
			setCurrentUserSession(SESSION_KEY_MENUS, menuTree);
			logger.info(principal + "'s menuTree:" + menuTree);
		}
		return (userList.isEmpty()) ? false : true;
	}

	/**
	 * <p>Description: 获得用户的所有角色</p>
	 * @param principal 用户名
	 * @return
	 */
	private List<Rolemstr> getRoles(String principal) {
		if (principal == null || principal.trim().isEmpty()) {
			return null;
		}
		Query q = entityService
				.getEm()
				.createQuery(
						"select ur.rolemstr from Userrole ur where ur.defunctInd <> 'Y' and ur.rolemstr.defunctInd <> 'Y' and ur.usermstr.adAccount=:principal",
						Usermstr.class);
		q.setParameter("principal", principal);
		return q.getResultList();
	}

	/**
	 * <p>Description: 获得用户的所有资源（菜单、功能）</p>
	 * @param principal 用户名
	 * @return
	 */
	private List<Resourcemstr> getResources(String principal) {
		if (principal == null || principal.trim().isEmpty()) {
			return null;
		}
		String qs = "select DISTINCT rsr.resourcemstr" + " from Userrole ur,Roleresource rsr"
				+ " where ur.defunctInd <> 'Y' and ur.rolemstr=rsr.rolemstr and ur.usermstr.adAccount=:principal"
				+ " and rsr.defunctInd <> 'Y' and rsr.resourcemstr.defunctInd <> 'Y'" + " and rsr.rolemstr.defunctInd <> 'Y'"
				+ " order by rsr.resourcemstr.seqNo asc";
		Query q = entityService.getEm().createQuery(qs, Resourcemstr.class);
		q.setParameter("principal", principal);
		return q.getResultList();
	}

	/**
	 * <p>Description: 构建菜单树（仅菜单，需排序）</p>
	 * @param resources 
	 * @return
	 */
	private List<TreeNode> getMenuTree(List<Resourcemstr> resources) {
		// filter all resources which the type='MENU'
		List<Resourcemstr> res = new ArrayList<Resourcemstr>();
		for (Resourcemstr r : resources) {
			if ("MENU".equals(r.getType())) {
				res.add(r);
			}
		}

		// just mock temply
		if (res == null || res.isEmpty()) {
			return null;
		}

		List<TreeNode> list = new ArrayList<TreeNode>();

		for (Resourcemstr r : res) {
			// first level
			if (r.getParentId() == 0) { 
				list.add(new DefaultTreeNode(r, null));
			}
		}

		for (TreeNode tn : list) {
			recursive((Resourcemstr) tn.getData(), tn, res);
		}
		return list;
	}

	/**
	 * <p>Description: recursive tree constructor</p>
	 * @param r
	 * @param tn
	 * @param rs
	 */
	public void recursive(Resourcemstr r, TreeNode tn, List<Resourcemstr> rs) {
		for (Resourcemstr rm : rs) {
			if (rm.getParentId() == r.getId()) {
				TreeNode n = new DefaultTreeNode(rm, tn);
				recursive(rm, n, rs);
			}
		}
	}

	/**
	 * 根据公司ID，查询该公司的对应岗位人员用户名
	 * @param companyId 公司ID
	 * @param station 岗位名称
	 * @return
	 */
	public List<String> getUserNamesBy(Long companyId, String station) {
		long startTime = System.currentTimeMillis();

		StringBuilder comJpql = new StringBuilder("select c.sapCode from Company c where c.defunctInd='N'");
		comJpql.append(" and c.id=" + companyId);
		List<String> sapCodes = entityService.find(comJpql.toString());
		if (sapCodes.isEmpty()) {
			return null;
		}
		String sapCode = sapCodes.get(0);

		StringBuilder oJpql = new StringBuilder("select o.id from O o where o.defunctInd='N'");
		oJpql.append(" and o.bukrs='" + sapCode + "'");
		List<String> oIds = entityService.find(oJpql.toString());
		if (oIds.isEmpty()) {
			return null;
		}
		String oId = oIds.get(0);

		StringBuilder sJpql = new StringBuilder("select s.id from S s where s.defunctInd='N'");
		sJpql.append(" and s.oid='" + oId + "'");
		sJpql.append(" and s.stext like '%" + station + "%'");
		List<String> sIds = entityService.find(sJpql.toString());
		if (sIds.isEmpty()) {
			return null;
		}
		String sId = sIds.get(0);

		StringBuilder psJpql = new StringBuilder("select ps.pid from PS ps where ps.defunctInd='N'");
		psJpql.append(" and ps.sid='" + sId + "'");
		List<String> pIds = entityService.find(psJpql.toString());

		StringBuilder puJpql = new StringBuilder("select pu.id from PU pu where pu.defunctInd='N' and pu.pernr in (?1)");
		List<String> puIds = entityService.find(puJpql.toString(), pIds);

		long endTime = System.currentTimeMillis();
		logger.info("getUserNamesBy----耗时（毫秒）：" + (endTime - startTime));
		return puIds;
	}

	/**
	 * 
	 * <p>Description:根据公司，或岗位查找人员 </p>
	 * @param companyId 公司id Company.id
	 * @param postionName 岗位名称 S.stext
	 * @return
	 */
	public List<String> getAccountByComIdOrPosName(Long companyId, String postionName) {
		long startTime = System.currentTimeMillis();
		List<String> retList = new ArrayList<String>();
		StringBuilder puJpql = new StringBuilder("select distinct pu.id from Company c,O o,S s,PS ps,PU pu");
		puJpql.append(" where c.defunct_Ind='N' and o.defunct_Ind='N' and ps.defunct_Ind='N' and pu.defunct_Ind='N'");
		puJpql.append(" and c.sap_Code=o.bukrs and o.id=s.oid and s.id = ps.sid");
		puJpql.append(" and pu.pernr = ps.pid ");
		// 只查询某公司下的人员
		if (companyId != null && StringUtils.isBlankOrNull(postionName)) {
			puJpql.append(" and c.id=?1 ");
			retList = em.createNativeQuery(puJpql.toString()).setParameter(1, companyId).getResultList();
		}
		// 只查询岗位下的人员
		if (companyId == null && !StringUtils.isBlankOrNull(postionName)) {
			puJpql.append(" and s.stext like '%" + postionName + "%'");
			retList = em.createNativeQuery(puJpql.toString()).getResultList();
		}
		// 查询某公司下的某岗位下的人员
		if (companyId != null && !StringUtils.isBlankOrNull(postionName)) {
			puJpql.append("  and c.id=?1 and s.stext like '%" + postionName + "%'");
			retList = em.createNativeQuery(puJpql.toString()).setParameter(1, companyId).getResultList();
		}
		long endTime = System.currentTimeMillis();
		logger.info("getAccountByComIdOrPosName----耗时（毫秒）：" + (endTime - startTime));
		return retList;
	}

	/**
	 * 
	 * <p>Description:根据公司，或岗位查找人员 </p>
	 * @param companyId 公司id Company.id
	 * @param postionName 岗位名称 S.stext
	 * @return
	 */
	public List<String> getPernrByComIdOrPosName(Long companyId, String postionName) {
		long startTime = System.currentTimeMillis();
		List<String> retList = new ArrayList<String>();
		StringBuilder puJpql = new StringBuilder("select distinct pu.pernr from Company c,O o,S s,PS ps,PU pu");
		puJpql.append(" where c.defunct_Ind='N' and o.defunct_Ind='N' and ps.defunct_Ind='N' and pu.defunct_Ind='N'");
		puJpql.append(" and c.sap_Code=o.bukrs and o.id=s.oid and s.id = ps.sid");
		puJpql.append(" and pu.pernr = ps.pid ");
		// 只查询某公司下的人员
		if (companyId != null && StringUtils.isBlankOrNull(postionName)) {
			puJpql.append(" and c.id=?1 ");
			retList = em.createNativeQuery(puJpql.toString()).setParameter(1, companyId).getResultList();
		}
		// 只查询岗位下的人员
		if (companyId == null && !StringUtils.isBlankOrNull(postionName)) {
			puJpql.append(" and s.stext like '%" + postionName + "%'");
			retList = em.createNativeQuery(puJpql.toString()).getResultList();
		}
		// 查询某公司下的某岗位下的人员
		if (companyId != null && !StringUtils.isBlankOrNull(postionName)) {
			puJpql.append("  and c.id=?1 and s.stext like '%" + postionName + "%'");
			retList = em.createNativeQuery(puJpql.toString()).setParameter(1, companyId).getResultList();
		}
		long endTime = System.currentTimeMillis();
		logger.info("getAccountByComIdOrPosName----耗时（毫秒）：" + (endTime - startTime));
		return retList;
	}

	/**
	 * 根据PU表的PERNR字段，获得P中，对应的人（pu.PERNR = P.ID）
	 * @param pernrList
	 * @return
	 */
	public List<P> getPByPUPernr(List<String> pernrList) {
		String jpql = "select p from P p where p.defunctInd = 'N' and p.id in (?1)";
		return entityService.find(jpql, pernrList);
	}
}
