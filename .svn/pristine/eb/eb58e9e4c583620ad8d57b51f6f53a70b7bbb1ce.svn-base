package com.wcs.tms.mail;

import java.util.EnumMap;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:邮件工具类</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
public class MailUtil {
	
	private MailUtil() {
		
	}

	/**
	 * <p>Project: tms</p>
	 * <p>Description:邮件发送类型enum</p>
	 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
	 * <p>All Rights Reserved.</p>
	 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
	 */
	public enum MailTypeEnum {

		/**
		 * 退回到申请人模式
		 */
		Request,
		/**
		 * 待接收人模式
		 */
		Next,

		/**
		 * 所有人模式
		 */
		All,

		/**
		 * 终止模式
		 */
		Terminal,

		/**
		 * 其他提醒模式：1.告知授信提供方(银行授信调剂流程)
		 */
		Notice,

		/**
		 * 集团项目经理模式:外债和注册资本金流程特殊告知
		 */
		GroupPM,

		/**
		 * 集团授信、银行授信在申请人确认时给处理过的人发邮件
		 */
		Handled,

		/**
		 * 内部担保、申请人确认（或最终确认）时，给之前处理过的人发送邮件
		 */
		Assure,
		
		/**
		 * 付款提醒
		 */
		payNotice,

		/**
		 * 内部担保、申请人确认提高授信额度后第二次审批，给下一步人员人发送邮件
		 */
		Vouchfor;

	}

	/**
	 * Event Subject Map
	 */
	public static final EnumMap<MailTypeEnum, String> MAIL_SUBJECT_MAP = new EnumMap<MailTypeEnum, String>(MailTypeEnum.class);
	static {
		MAIL_SUBJECT_MAP.put(MailTypeEnum.Request, "TMS流程重新申请提醒-[pName]-[pId]");
		MAIL_SUBJECT_MAP.put(MailTypeEnum.Next, "TMS流程待接收任务提醒-[pName]-[pId]");
		MAIL_SUBJECT_MAP.put(MailTypeEnum.All, "TMS流程结束提醒-[pName]-[pId]");
		MAIL_SUBJECT_MAP.put(MailTypeEnum.Terminal, "TMS流程终止提醒-[pName]-[pId]");
		MAIL_SUBJECT_MAP.put(MailTypeEnum.Notice, "TMS流程告知-[pName]-[pId]");
		MAIL_SUBJECT_MAP.put(MailTypeEnum.GroupPM, "TMS流程结束告知-[pName]-[pId]");
		MAIL_SUBJECT_MAP.put(MailTypeEnum.Handled, "TMS流程确认告知-[pName]-[pId]");
		MAIL_SUBJECT_MAP.put(MailTypeEnum.Assure, "TMS流程结束提醒-[pName]-[pId]");
		MAIL_SUBJECT_MAP.put(MailTypeEnum.Vouchfor, "TMS流程待接收任务提醒-[pName]-[pId]");
		MAIL_SUBJECT_MAP.put(MailTypeEnum.payNotice, "BPM-TMS付款状态提醒-[pId]-[pDetail]-[pName]");
	}

	/**
	 * Event Content Map
	 */
	public static final EnumMap<MailTypeEnum, String> MAIL_CONTENT_MAP = new EnumMap<MailTypeEnum, String>(MailTypeEnum.class);
	static {
		MAIL_CONTENT_MAP.put(MailTypeEnum.Request, "TMS流程-[pName]-实例编号[pId]: 步骤[pDone]完成，执行人[pUser]，此流程需重新申请，请查看待接收任务。备注: pComment");
		MAIL_CONTENT_MAP.put(MailTypeEnum.Next, "TMS流程-[pName]-实例编号[pId]: 步骤[pDone]完成，执行人[pUser]，下一步为[pNext]，请查看待接收任务。备注: pComment");
		MAIL_CONTENT_MAP.put(MailTypeEnum.All, "TMS流程-[pName]-实例编号[pId]：步骤[pDone]完成，执行人[pUser]，流程业务完成，流程将在[pDone]结束后10分钟自动终止。备注: pComment");
		MAIL_CONTENT_MAP.put(MailTypeEnum.Terminal, "TMS流程-[pName]-实例编号[pId]：流程被手动终止，执行人[pUser]。");
		MAIL_CONTENT_MAP.put(MailTypeEnum.Notice, "TMS流程-[pName]-实例编号[pId]：步骤[pDone]完成，执行人[pUser]，");
		MAIL_CONTENT_MAP.put(MailTypeEnum.GroupPM, "TMS流程-[pName]-实例编号[pId]：步骤[pDone]完成，执行人[pUser]，流程业务完成，流程将在[pDone]结束后10分钟自动终止，特此告知集团项目经理。备注: pComment");
		MAIL_CONTENT_MAP.put(MailTypeEnum.Handled, "TMS流程-[pName]-实例编号[pId]：步骤[pDone]完成，执行人[pUser]，流程业务数据已确认，特此告知！");
		MAIL_CONTENT_MAP.put(MailTypeEnum.Assure, "TMS流程-[pName]-实例编号[pId]：步骤[pDone]完成，执行人[pUser]，流程业务完成,将自动结束，特此告知！");
		MAIL_CONTENT_MAP.put(MailTypeEnum.Vouchfor, "TMS流程-[pName]-实例编号[pId]：步骤[pDone]完成，执行人[pUser]，下一步为[pNext]，流程因申请人确认实际担保额度大于申请担保额度，故需重新审批，请查看待接收任务。备注: pComment");
	}
}
