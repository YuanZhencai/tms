package com.wcs.sys.ejbtimer.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wcs.base.util.DateUtil;
import com.wcs.base.util.StringUtils;
import com.wcs.common.util.ExceptionUtil;
import com.wcs.sys.ejbtimer.annotation.WCSTimerClass;
import com.wcs.sys.ejbtimer.annotation.WCSTimerMethod;
import com.wcs.sys.ejbtimer.interfaces.EJBTimerLogContext;
import com.wcs.sys.ejbtimer.interfaces.EJBTimerLogContextHolder;
import com.wcs.sys.ejbtimer.model.SysJobInfo;
import com.wcs.sys.ejbtimer.model.SysJobLog;
import com.wcs.sys.ejbtimer.util.AnnotationUtils;
import com.wcs.sys.ejbtimer.util.ClassLoaderUtil;
import com.wcs.sys.ejbtimer.util.JNDIUtil;

/**
 * <p>Project: wcsoa</p>
 * <p>Description: 定时任务服务类</p>
 * <p>Copyright © 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
@Stateless
public class SysJobService implements Serializable {

	private static final long serialVersionUID = 2277953366081365040L;

	private static final Logger logger = LoggerFactory.getLogger(SysJobService.class);

	@PersistenceContext(unitName = "pu")
	EntityManager em;

	@EJB
	SysEjbTimerService ejbTimerService;

	/**
	 * 
	 * <p>Description: 导入定时任务到数据库</p>
	 * @return
	 */
	public int importJob2DB() {
		// 新增加的定时任务计数
		int importCount = 0;
		List<SysJobInfo> dbJobList = findJobList("");
		// 找出所有包含 @WCSTimerClass注解的类
		//OAClassLoaderUtil.loadClassesFromPath();
		List<Class<?>> timerClasses = ClassLoaderUtil.findAnnotationClasses(WCSTimerClass.class);
		SysJobInfo jobInfo;
		for (Class<?> clazz : timerClasses) {
			// 是否为EJB的class
			boolean isEJB = false;
			if (AnnotationUtils.isAnnotationDeclaredLocally(Stateless.class, clazz)
					|| AnnotationUtils.isAnnotationDeclaredLocally(Stateful.class, clazz)) {
				isEJB = true;
			}

			Method[] methods = clazz.getMethods();
			outloop: for (Method method : methods) {
				if (method.isAnnotationPresent(WCSTimerMethod.class)) {
					WCSTimerMethod wcsTimer = method.getAnnotation(WCSTimerMethod.class);
					if (wcsTimer == null) {
						continue;
					}
					String className = clazz.getName();
					String methodName = method.getName();

					for (int i = 0; i < dbJobList.size(); i++) {
						jobInfo = dbJobList.get(i);
						// 数据库中已有的数据，更新成最新的任务标题和描述
						if (jobInfo.getJobService().equals(className) && jobInfo.getJobMethod().equals(methodName)) {
							dbJobList.remove(i);
							jobInfo.setJobSubject(wcsTimer.subject());
							jobInfo.setJobDesc(wcsTimer.desc());
							jobInfo.setIsEJB(isEJB);
							em.merge(jobInfo);
							continue outloop;
						}
					}

					// 把没导入过的记录，添加新记录
					jobInfo = new SysJobInfo();
					jobInfo.setJobSubject(wcsTimer.subject());
					jobInfo.setJobDesc(wcsTimer.desc());
					jobInfo.setCronExpression(wcsTimer.cronExpression());
					jobInfo.setJobService(className);
					jobInfo.setJobMethod(methodName);
					jobInfo.setIsEJB(isEJB);
					jobInfo.setIsEnabled(wcsTimer.isEnabled());
					// 把任务导入到数据库，并分配定时任务
					add(jobInfo);
					importCount++;
				}
			}
		}

		// 删除多余的定时任务
		for (SysJobInfo sysJobInfo : dbJobList) {
			em.remove(sysJobInfo);
		}

		return importCount;
	}

	/**
	 * 
	 * <p>Description: 查询数据库所有job</p> 
	 * @param whereSql where语句  
	 * @return
	 */
	public List<SysJobInfo> findJobList(String whereSql) {
		StringBuilder strSql = new StringBuilder("SELECT s FROM SysJobInfo s  ");
		if (!StringUtils.isBlankOrNull(whereSql)) {
			strSql.append(whereSql);
		}
		List<SysJobInfo> dbJobList = new ArrayList<SysJobInfo>();
		dbJobList.addAll(em.createQuery(strSql.toString()).getResultList());
		return dbJobList;
	}

	/**
	 * 
	 * <p>Description: 分页查询定时任务</p>
	 * @param first
	 * @param pageSize
	 * @param sortField
	 * @param sortOrder
	 * @param filters
	 * @return
	 */
	public Map findAllJobs(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
		Map map = new HashMap();

		StringBuilder strCountSql = new StringBuilder("SELECT count(s.id) FROM SysJobInfo s WHERE 1=1 ");
		StringBuilder strSql = new StringBuilder("SELECT s FROM SysJobInfo s WHERE 1=1 ");

		StringBuilder whereSql = new StringBuilder();
		if (filters.get("jobSubject") != null) {
			whereSql.append(" AND s.jobSubject LIKE '%" + filters.get("jobSubject") + "%' ");
		}
		if (filters.get("isEnabled") != null) {
			whereSql.append(" AND s.isEnabled =" + filters.get("isEnabled"));
		}

		strCountSql.append(whereSql);
		strSql.append(whereSql);

		// 得到总记录数
		Query countQuery = em.createQuery(strCountSql.toString());
		Object countObj = countQuery.getSingleResult();

		// 得到查询结果
		Query query = em.createQuery(strSql.toString());
		query.setFirstResult(first);
		query.setMaxResults(pageSize);
		List resultList = query.getResultList();
		map.put("list", resultList);
		map.put("count", ((Long) countObj).intValue());
		return map;
	}

	/**
	 * 
	 * <p>Description: 新增定时任务，并安排任务</p>
	 * @param jobModel
	 */
	public void add(SysJobInfo jobModel) {
		em.persist(jobModel);
		ejbTimerService.createTimer(jobModel);
	}

	/**
	 * 
	 * <p>Description: 修改定时任务配置，并重新安排任务</p>
	 * @param jobModel
	 */
	public void update(SysJobInfo jobModel) {
		em.merge(jobModel);
		ejbTimerService.createTimer(jobModel);
	}

	/**
	 * 
	 * <p>Description:删除定时任务 </p>
	 * @param jobModel
	 */
	public void delete(SysJobInfo jobModel) {
		ejbTimerService.cancelTimer(jobModel.getId().toString());
		em.remove(jobModel);
	}

	/**
	 * 
	 * <p>Description:根据定时任务配置表的规则，执行定时任务 </p>
	 * @param id
	 * @throws Exception
	 */
	public void runJob(SysJobInfo sysJobInfo) throws Exception {
		// 设置日志信息
		SysJobLog logModel = new SysJobLog();
		logModel.setSysJobInfo(sysJobInfo);
		logModel.setStartTime(new Date());
		logModel.setJobSubject(sysJobInfo.getJobSubject());
		logModel.setIsSuccess(Boolean.TRUE);
		EJBTimerLogContext logContext = new EJBTimerLogContextImp(logModel);
		EJBTimerLogContextHolder.set(logContext);
		try {

			if (invokeMethod(logContext, sysJobInfo)) {
				if (!sysJobInfo.getIsEnabled()) {
					return;
				}
				// 任务执行成功了，设置已经执行过标识字段值，和下次运行时间
				String nextTime = ejbTimerService.getNextRunTimeByTimerID(sysJobInfo.getId().toString(), true);
				if (!StringUtils.isBlankOrNull(nextTime)) {
					sysJobInfo.setNextRunTime(DateUtil.strToDateLong(nextTime).toDate());
					em.merge(sysJobInfo);
				}
			} else {
				// 任务执行出错了，无法执行，将任务停止
				sysJobInfo.setIsEnabled(Boolean.FALSE);
				em.merge(sysJobInfo);
				ejbTimerService.cancelTimer(sysJobInfo.getId().toString());
			}
		} catch (Exception e) {
			logContext.logError(e);
			logger.error(ExceptionUtil.getExceptionString(e));
			throw e;
		} finally {
			logModel.setEndTime(new Date());
			String costTime = String.valueOf((logModel.getEndTime().getTime()
					- logModel.getStartTime().getTime() + 0.0) / 1000);
			logContext.logMessage("任务总耗时：" + costTime + "秒");
			// 保存日志
			try {
				em.persist(logModel);
			} catch (Exception e) {
				logger.error("保存日志发生错误：" + ExceptionUtil.getExceptionString(e));
			}
			EJBTimerLogContextHolder.remove();
		}
	}

	/**
	 * 查找bean的方法并执行
	 * 
	 * @param logContext 日志上下文
	 * @param jobModel
	 * @return true 表示成功；false表示找不到bean或方法，或者执行失败
	 * @throws Exception
	 */
	private boolean invokeMethod(EJBTimerLogContext logContext, SysJobInfo jobModel) throws Exception {

		if (jobModel.getIsEJB()) {
			if (invokeEJBMethod(jobModel.getJobService(), jobModel.getJobMethod(), new Class[] {}, new Object[] {},
					jobModel.getJobSubject() + "（不带参数）")) {
				return true;
			}
			if (invokeEJBMethod(jobModel.getJobService(), jobModel.getJobMethod(), new Class[] { EJBTimerLogContext.class },
					new Object[] { logContext }, jobModel.getJobSubject() + "（带参数）")) {
				return true;
			}

		} else {
			if (invokeBeanMethod(jobModel.getJobService(), jobModel.getJobMethod(), new Class[] {}, new Object[] {})) {
				return true;
			}
			if (invokeBeanMethod(jobModel.getJobService(), jobModel.getJobMethod(), new Class[] { EJBTimerLogContext.class },
					new Object[] { logContext })) {
				return true;
			}
		}
		logContext.logError("无法找到方法：" + jobModel.getJobService() + "."
				+ jobModel.getJobMethod() + "()或"
				+ jobModel.getJobService() + "." + jobModel.getJobMethod()
				+ "(EJBTimerLogContext)");
		return false;
	}

	/**
	 * 
	 * <p>Description:根据EJB的JNDI和方法名,调用EJB的方法 </p>
	 * @param ejbJNDI
	 * @param methodName
	 * @param argTypes
	 * @param arguments
	 * @param title
	 * @return
	 * @throws Exception
	 */
	private boolean invokeEJBMethod(String ejbClassName, String methodName, Class[] argTypes, Object[] arguments, String title)
			throws Exception {
		if (arguments == null) {
			arguments = new Object[0];
		}
		try {
			// 获取EJB的jndi中的Context上下文信息
			Map dataMap = new HashMap();
			Context jndiContext = JNDIUtil.getEnvContext(dataMap);
			// 获取EJB的代理对象
			Object ejbobject = jndiContext.lookup(JNDIUtil.findJNDI(ejbClassName));
			// 通过反射执行方法的信息
			Class ejbClass = ejbobject.getClass();
			Method methodExecute = null;

			// 调用方法的输入参数类型
			if (argTypes == null) {
				argTypes = new Class[arguments.length];
				for (int i = 0; i < arguments.length; i++) {
					argTypes[i] = arguments[i].getClass();
				}
			}
			// 执行EJB方法
			methodExecute = ejbClass.getMethod(methodName, argTypes);

			// 存储EJB的执行结果
			Object returnObj = methodExecute.invoke(ejbobject, arguments);
			logger.debug(title + "返回值：" + returnObj);

			// 关闭上下文的信息
			if (jndiContext != null) {
				jndiContext.close();
			}
		} catch (NoSuchMethodException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable t = e.getCause();
			if (t instanceof Exception) {
				throw (Exception) t;
			}
			throw new RuntimeException(t);
		}
		return true;
	}

	/**
	 * 
	 * <p>Description:执行一般的java方法</p>
	 * @param className
	 * @param methodName
	 * @param argType
	 * @param args
	 * @param title
	 * @return
	 * @throws Exception
	 */
	private boolean invokeBeanMethod(String className, String methodName, Class[] argType, Object[] args)
			throws Exception {
		try {
			Class beanClass = Class.forName(className);
			Method method = beanClass.getMethod(methodName, argType);
			method.invoke((Object) beanClass.newInstance(), args);
		} catch (NoSuchMethodException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable t = e.getCause();
			if (t instanceof Exception) {
				throw (Exception) t;
			}
			throw new RuntimeException(t);
		}
		return true;
	}
}
