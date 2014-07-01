package com.wcs.sys.ejbtimer.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wcs.base.util.DateUtil;
import com.wcs.base.util.StringUtils;
import com.wcs.common.util.ExceptionUtil;
import com.wcs.common.util.MessageUtils;
import com.wcs.sys.ejbtimer.model.SysJobInfo;
import com.wcs.sys.ejbtimer.service.SysEjbTimerService;
import com.wcs.sys.ejbtimer.service.SysJobService;
import com.wcs.sys.ejbtimer.util.CronExpression;

/**
 * <p>Project: wcsoa</p>
 * <p>Description: </p>
 * <p>Copyright © 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
@ManagedBean(name = "sysJobBean")
@ViewScoped
public class SysJobBean {

	private static final Logger log = LoggerFactory.getLogger(SysJobBean.class);

	// 查询页面参数
	private Map<String, String> query = new HashMap<String, String>(5);

	private LazyDataModel<SysJobInfo> jobInfoLazyModel;

	// insertdialog页面参数
	private SysJobInfo addSysJobInfo = null;

	// updatedialog页面参数
	private SysJobInfo selectData = null;

	// 确认框，要处理的业务类型，如修改状态，即时执行任务
	private String confirmType = "";
	@EJB
	private SysJobService sysJobService;

	@EJB
	private SysEjbTimerService ejbTimerService;

	public void initAddJob() {
		addSysJobInfo = new SysJobInfo();
	}

	public SysJobBean() {
		serachData();
		addSysJobInfo = new SysJobInfo();
		selectData = new SysJobInfo();
	}

	/**
	 *  查找所有定时任务
	 *  void
	 */
	public void serachData() {

		jobInfoLazyModel = new LazyDataModel<SysJobInfo>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<SysJobInfo> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, String> filters) {
				Map<String, Object> map = new HashMap<String, Object>();
				map = sysJobService.findAllJobs(first, pageSize, sortField, sortOrder, query);
				List<SysJobInfo> list = new ArrayList<SysJobInfo>();
				if (map.size() != 0) {
					this.setRowCount((Integer) map.get("count"));
					list = (List<SysJobInfo>) map.get("list");
				} else {
					this.setRowCount(0);
				}
				return list;
			}

		};
	}

	/**
	 * 
	 * <p>Description: 读取代码中，用了@WCSTimer的类和方法，导入到定时任务表</p>
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void importSysJob2DB() throws InstantiationException, IllegalAccessException {
		int addCount = sysJobService.importJob2DB();
		MessageUtils.addInfoMessage("msg", "成功导入定时任务 " + addCount + " 条!");
	}

	// 添加
	public void insertData() {
		sysJobService.add(addSysJobInfo);
		// 添加成功关闭dialog窗口
		RequestContext.getCurrentInstance().addCallbackParam("addInfo", "yes");
	}

	// 编辑
	public void updateData() {
		sysJobService.update(selectData);
		RequestContext.getCurrentInstance().addCallbackParam("updateInfo", "yes");
	}

	// 确认框中，点击确认执行的方法
	public void confirmDoSth() {
		SysJobInfo jobInfo = selectData;

		// 设置状态，启用或禁用
		if ("setEnableStatus".equals(confirmType)) {
			if (jobInfo.getIsEnabled()) {
				jobInfo.setIsEnabled(Boolean.FALSE);

			} else {
				jobInfo.setIsEnabled(Boolean.TRUE);
			}
			sysJobService.update(jobInfo);
		}

		if ("runJob".equals(confirmType)) {
			runJob();
		}

	}

	/**
	 * 
	 * <p>Description:即时执行任务代码 </p>
	 */
	public void runJob() {
		try {
			sysJobService.runJob(selectData);
			MessageUtils.addInfoMessage("msg", "【" + selectData.getJobSubject() + "】执行成功！");
		} catch (Exception e) {
			MessageUtils.addErrorMessage("msg",
					"执行定时任务[" + selectData.getJobSubject() + "]时，发生异常：" + ExceptionUtil.getExceptionString(e));
			log.debug("执行定时任务[" + selectData.getJobSubject() + "]时，发生异常：" + ExceptionUtil.getExceptionString(e));
		}
	}

	/**
	 * 
	 * <p>Description:根据cronExpression,获取下次运行的时间 </p>
	 * @param cronExpression
	 * @return
	 * @throws Exception
	 */
	public String getNextRunTimeByCron(String cronExpression, Boolean isEnabled) throws Exception {
		if (!isEnabled) {
			return "";
		}
		CronExpression expression = new CronExpression(cronExpression);
		Date nextTime = expression.getNextValidTimeAfter(new Date());
		return DateUtil.convertDateToString(nextTime, null);
	}

	/**
	 * 
	 * <p>Description: </p>
	 * @param jobId 定时任务表中的主键值
	 * @param isEnabled 是否启用
	 * @return
	 * @throws Exception
	 */
	public String getNextRunTimeByTimerID(Long jobId, Boolean isEnabled) throws Exception {

		return ejbTimerService.getNextRunTimeByTimerID(String.valueOf(jobId), isEnabled);
	}

	/**
	 * 
	 * <p>Description:根据cronExpression，获取友好的中文描述 </p>
	 * @param cronExpression
	 * @return
	 */
	public String getChineseByCron(String cronExpression) {

		if (StringUtils.isBlankOrNull(cronExpression)) {
			return "";
		}

		String[] fieldNames = "秒,分,时,日,月,周,年".split(",");

		String[] weekdayNames = "星期一,星期二,星期三,星期四,星期五,星期六,星期日".split(",");

		String rtnVal = "";
		String[] fields = cronExpression.split("\\s+");
		if (fields.length == 7 && !fields[6].equals("*")) {
			rtnVal = fields[6] + fieldNames[6];
		}
		if (!fields[4].equals("*")) {
			rtnVal += fields[4] + fieldNames[4];
		}
		if (fields[5].equals("?")) {
			if (!fields[3].equals("*")) {
				rtnVal += fields[3] + fieldNames[3];
			}
		} else {
			if (!fields[5].equals("*")) {
				for (int i = 0; i < fields[5].length(); i++) {
					char c = fields[5].charAt(i);
					if (c >= '1' && c <= '7') {
						rtnVal += weekdayNames[c - '1'];
					}
					else {
						rtnVal += c;
					}
				}
			}
		}
		for (int i = 2; i >= 0; i--) {
			if (!rtnVal.equals("") || !fields[i].equals("*")) {
				rtnVal += fields[i] + fieldNames[i];
			}
		}
		return rtnVal;
	}

	public void reset() {
		query.clear();
	}

	public Map<String, String> getQuery() {
		return query;
	}

	public void setQuery(Map<String, String> query) {
		this.query = query;
	}

	public LazyDataModel<SysJobInfo> getJobInfoLazyModel() {
		return jobInfoLazyModel;
	}

	public void setJobInfoLazyModel(LazyDataModel<SysJobInfo> jobInfoLazyModel) {
		this.jobInfoLazyModel = jobInfoLazyModel;
	}

	public SysJobInfo getSelectData() {
		return selectData;
	}

	public void setSelectData(SysJobInfo selectData) {
		this.selectData = selectData;
	}

	public SysJobInfo getAddSysJobInfo() {
		return addSysJobInfo;
	}

	public void setAddSysJobInfo(SysJobInfo addSysJobInfo) {
		this.addSysJobInfo = addSysJobInfo;
	}

	public String getConfirmType() {
		return confirmType;
	}

	public void setConfirmType(String confirmType) {
		this.confirmType = confirmType;
	}

}
