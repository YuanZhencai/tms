package com.wcs.tms.util;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

@Singleton
public class ProcessDefineUtil {
	// --------------------------------CODE定义------------------------------------------//
	// 外债申请流程CODE
	public static final String DEBT_BORROW_CODE = "TMS_BPM_RA_007";
	// 注册资本金CODE
	public static final String REGISTER_CAPTIAL_CODE = "TMS_BPM_RA_002";

	// --------------------------------页面定义------------------------------------------//
	// 外债申请流程页面
	public static final String DEBT_BORROW_PAGE = "/faces/process/debtBorrow/debtBorrow-new.xhtml";
	// 注册资本金申请页面
	public static final String REGISTER_CAPTIAL_PAGE = "/faces/process/regiCapital/registerCaptial-list.xhtml";

	// --------------------------------流程实例名称------------------------------------------//
	// 外债申请流程FN流程WorkClassName
	public static final String DEBT_BORROW_PECLASS = "TMS_DebtBorrow";
	// 注册资本金工作流程
	public static final String REGISTER_CAPTIAL_PECLASS = "TMS_RegiCapital";

	// --------------------------------流程FN路径------------------------------------------//
	private static final String TMS_RegiCapital_Path = "/TMS/Process/注册资本金申请审批流程";
	private static final String TMS_DbBrow_Path = "/TMS/Process/外债申请和外债展期申请审批流程";
	private static final String HR_DEMO = "/chentms1/员工报销申请 ";

	/**
	 * <p>
	 * Project: ncp
	 * </p>
	 * <p>
	 * Description: 流程类型类型
	 * </p>
	 * <p>
	 * Copyright (c) 2011 Wilmar Consultancy Services
	 * </p>
	 * <p>
	 * All Rights Reserved.
	 * </p>
	 * 
	 * @author <a href="mailto:huhan@wcs-global.com">HuHan</a>
	 */
	public enum ProcessEnum {
		/**
		 * 外债申请流程
		 */
		DebtBorrow,
		/** 注册资本金 */
		RegiCapital;
	}

	/**
	 * 流程PE WorkClassName Map
	 */
	public static final EnumMap<ProcessEnum, String> PROCESS_PECLASS_MAP = new EnumMap<ProcessEnum, String>(
			ProcessEnum.class);
	static {
		PROCESS_PECLASS_MAP.put(ProcessEnum.DebtBorrow, DEBT_BORROW_PECLASS);
		PROCESS_PECLASS_MAP.put(ProcessEnum.RegiCapital,
				REGISTER_CAPTIAL_PECLASS);
	}

	/**
	 * 流程页面 Map 流程Code 得到 流程表单页面
	 */
	public static final Map<String, String> PROCESS_CODE_PAGE_MAP = new HashMap<String, String>();
	static {
		PROCESS_CODE_PAGE_MAP.put(DEBT_BORROW_CODE, DEBT_BORROW_PAGE);
		PROCESS_CODE_PAGE_MAP.put(REGISTER_CAPTIAL_CODE, REGISTER_CAPTIAL_PAGE);
	}

	/**
	 * 流程页面 Map 流程PE WorkClassName 得到 流程Code
	 */
	public static final Map<String, String> PROCESS_CLASS_CODE_MAP = new HashMap<String, String>();
	static {
		PROCESS_CLASS_CODE_MAP.put(DEBT_BORROW_PECLASS, DEBT_BORROW_CODE);
		PROCESS_CLASS_CODE_MAP.put(REGISTER_CAPTIAL_PECLASS,
				REGISTER_CAPTIAL_CODE);
	}

	/**
	 * FN流程在CE的存储路径
	 */
	public static Map<String, String> pressCePathMap = new HashMap<String, String>();
	static {
		pressCePathMap.put(REGISTER_CAPTIAL_PECLASS, TMS_RegiCapital_Path);
		pressCePathMap.put("EmployeeReimbursement", HR_DEMO);
		pressCePathMap.put(DEBT_BORROW_PECLASS, TMS_DbBrow_Path);

	}

	/**
	 * 
	 * <p>
	 * Project: tms
	 * </p>
	 * <p>
	 * Description:日志事件类型
	 * </p>
	 * <p>
	 * Copyright (c) 2012 Wilmar Consultancy Services
	 * </p>
	 * <p>
	 * All Rights Reserved.
	 * </p>
	 * 
	 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
	 */
	public enum EventTypeEnum {

		/**
		 * 锁定事件
		 */
		Lock,
		/**
		 * 步骤结束事件
		 */
		StepEnd,

		/**
		 * 流程结束事件
		 */
		ProcessTerminal,
		/**
		 * 流程强行终止
		 */
		ForcedToTerminate;

	}

	/**
	 * Event Map
	 */
	public static final EnumMap<EventTypeEnum, Integer> PROCESS_EVENTTYPE_MAP = new EnumMap<EventTypeEnum, Integer>(
			EventTypeEnum.class);
	static {
		// WPEndServiceNormal
		PROCESS_EVENTTYPE_MAP.put(EventTypeEnum.StepEnd, 360);
		// WOParentTermination
		PROCESS_EVENTTYPE_MAP.put(EventTypeEnum.ProcessTerminal, 160);
		// WPWOBSaveWithLock
		PROCESS_EVENTTYPE_MAP.put(EventTypeEnum.Lock, 365);
		// ForcedToTerminate
		PROCESS_EVENTTYPE_MAP.put(EventTypeEnum.ForcedToTerminate, 190);
	}
}
