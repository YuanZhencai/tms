package com.wcs.common.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.common.controller.vo.RoleVo;
import com.wcs.common.controller.vo.UsermstrFormItemsVo;
import com.wcs.common.controller.vo.UsermstrVo;
import com.wcs.common.model.O;
import com.wcs.common.model.P;
import com.wcs.common.model.PU;
import com.wcs.common.model.Rolemstr;
import com.wcs.common.model.Usermstr;
import com.wcs.common.model.Userrole;

@Stateless
public class UserService implements Serializable {

	private static final long serialVersionUID = -4531023608569097125L;

	private Log logger = LogFactory.getLog(UserService.class);

	@PersistenceContext(unitName = "pu")
	private EntityManager em;

	@SuppressWarnings("unchecked")
	public List<UsermstrVo> getAllUsermstrVo(UsermstrFormItemsVo ufiv) {
		StringBuffer sb = new StringBuffer();
		sb.append("select u from Usermstr u where 1=1");

		if (ufiv.getAdAccount() != null && !"".equals(ufiv.getAdAccount())) {
			sb.append(" and upper(u.adAccount) like '%"
					+ ufiv.getAdAccount().trim().toUpperCase() + "%'");
		}
		if (ufiv.getUserName() != null && !"".equals(ufiv.getUserName())) {
			sb.append(" and EXISTS(select p.id from P p,PU pu where p.id=pu.pernr and u.adAccount=pu.id and upper(p.nachn) like '%"
					+ ufiv.getUserName().trim().toUpperCase()
					+ "%' and p.defunctInd='N')");
		}
		if (ufiv.getRolemstrId() != null && !"".equals(ufiv.getRolemstrId())) {
			sb.append(" and EXISTS(select ur.usermstr.id from Userrole ur where u.id=ur.usermstr.id and ur.rolemstr.id="
					+ ufiv.getRolemstrId() + " and ur.defunctInd='N')");
		}
		if (ufiv.getStatus() != null && !"".equals(ufiv.getStatus())) {
			sb.append("and u.defunctInd='" + ufiv.getStatus() + "'");
		}
		sb.append(" order by u.adAccount");

		String sql = sb.toString();
		List<Usermstr> list = this.em.createQuery(sql).getResultList();
		List<UsermstrVo> listUsermstrVo = new ArrayList<UsermstrVo>();
		UsermstrVo uv = null;
		for (Usermstr u : list) {
			PU pu = getPU(u.getAdAccount());
			if (pu == null) {
				continue;
			}
			uv = new UsermstrVo();
			uv.setId(u.getId());
			uv.setUsermstr(u);
			if ("Y".equals(pu.getDefunctInd())) {
				continue;
			} else {
				uv.setP(getP(getPU(u.getAdAccount()).getPernr()));
			}
			if (uv.getP() == null) {
				logger.info("Something wrong happened, PU & P unsynchronized!!!");
				continue;
			}
			uv.setO(getO(uv.getP().getBukrs()));
			List<Rolemstr> roles = getRoleByUser(u);
			uv.setRoles(roles);
			listUsermstrVo.add(uv);
		}
		return listUsermstrVo;
	}

	@SuppressWarnings("unchecked")
	public List<RoleVo> getAllRoleVo() {
		StringBuffer sb = new StringBuffer();
		sb.append("select r from Rolemstr r where r.defunctInd='N' order by r.name");
		String sql = sb.toString();
		List<Rolemstr> list = this.em.createQuery(sql).getResultList();
		List<RoleVo> roleVoList = new ArrayList<RoleVo>();
		RoleVo rv = null;
		if (list.size() != 0) {
			for (Rolemstr r : list) {
				rv = new RoleVo();
				rv.setId(r.getId());
				rv.setRolemstr(r);
				roleVoList.add(rv);
			}
		}
		return roleVoList;
	}

	public P getP(String id) {
		return this.em.find(P.class, id);
	}

	public PU getPU(String id) {
		return this.em.find(PU.class, id);
	}

	@SuppressWarnings("unchecked")
	public O getO(String bukrs) {
		O o = new O();
		String sql = "select o from O o where o.bukrs = :bukrs order by o.id";
		Query q = em.createQuery(sql);
		q.setParameter("bukrs", bukrs);
		List<O> oList = q.getResultList();
		if (oList.size() > 0) {
			o = oList.get(0);
		}
		return o;
	}
	
	@SuppressWarnings("unchecked")
	public List<O> getAllO() {
		String sql = "select o from O o order by o.id";
		return this.em.createQuery(sql).getResultList();
	}

	public int getUserCount(String adAccount) {
		int num = 0;
		String sql = "select u from Usermstr u where u.adAccount = :adAccount";
		Query q = em.createQuery(sql);
		q.setParameter("adAccount", adAccount.trim());
		num = q.getResultList().size();
		return num;
	}

	@SuppressWarnings("unchecked")
	public boolean saveUserRole(List<Long> selectedRoleVos, Usermstr u,
			String userName) {
		boolean b = false;
		try {
			Userrole userrole = null;
			List<Userrole> selectedUserroleList = new ArrayList<Userrole>();
			if (selectedRoleVos != null && selectedRoleVos.size() != 0) {
				for (int i = 0; i < selectedRoleVos.size(); i++) {
					userrole = new Userrole();
					userrole.setUsermstr(u);
					userrole.setRolemstr(this.em.find(Rolemstr.class,
							selectedRoleVos.get(i)));
					selectedUserroleList.add(userrole);
				}
			}

			StringBuffer sb = new StringBuffer();
			sb.append("select ur from Userrole ur where ur.usermstr.id=")
					.append(u.getId());
			String sql = sb.toString();
			List<Userrole> existUserroleList = this.em.createQuery(sql)
					.getResultList();

			if (selectedUserroleList.size() != 0) {
				boolean bb = false;
				for (Userrole ur1 : selectedUserroleList) {
					bb = false;
					if (existUserroleList.size() != 0) {
						for (Userrole ur2 : existUserroleList) {
							if (ur1.getRolemstr().getId()
									.compareTo(ur2.getRolemstr().getId()) == 0) {
								bb = true;
								break;
							}
						}
					}
					if (!bb) {
						ur1.setDefunctInd("N");
						ur1.setCreatedBy(userName);
						ur1.setCreatedDatetime(new Date());
						ur1.setUpdatedBy(userName);
						ur1.setUpdatedDatetime(new Date());
						this.em.persist(ur1);
					}
				}
			}

			if (existUserroleList.size() != 0) {
				boolean bb = false;
				for (Userrole ur1 : existUserroleList) {
					bb = false;
					if (selectedUserroleList.size() != 0) {
						for (Userrole ur2 : selectedUserroleList) {
							if (ur1.getRolemstr().getId()
									.compareTo(ur2.getRolemstr().getId()) == 0) {
								bb = true;
								break;
							}
						}
					}
					userrole = this.em.find(Userrole.class, ur1.getId());
					if (bb) {
						userrole.setDefunctInd("N");
					} else {
						userrole.setDefunctInd("Y");
					}
				}
			}

			b = true;
		} catch (Exception e) {
			b = false;
			logger.error("saveUserRole方法出现异常", e);
		}
		return b;
	}

	/**
	 * 获取用户的所有角色
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Rolemstr> getRoleByUser(Usermstr user) {
		String jpql = "SELECT r FROM Rolemstr r, Userrole ur, Usermstr u where ur.rolemstr = r AND ur.usermstr = u AND u.id = :id";
		return em.createQuery(jpql).setParameter("id", user.getId())
				.getResultList();
	}
	/**
	 * 获取序列数据
	 * @return
	 */
	public Integer getPSeq() {
		String sql = "select nextval for SEQ_P from sysibm.sysdummy1";
		return (Integer)em.createNativeQuery(sql).getSingleResult();
	}
	
	/**
	 * 查询当前P机构最大ID
	 * @param sap
	 * @return
	 */
	public String getPersonMaxID(String sap) {
		String jpql = "SELECT max(p.id) FROM P p where p.id like '" + sap + "%'";;
		return (String)em.createQuery(jpql).getSingleResult();
	}
	
	public void updateUser(Usermstr user) {
		em.merge(user);
	}
	
	public void updatePerson(P person) {
		em.merge(person);
	}
	
	public void saveUser(Usermstr user) {
		em.persist(user);
	}
	
	public void savePerson(P person) {
		em.persist(person);
	}
	
	public void savePersonUser(PU pu) {
		em.persist(pu);
	}
}
