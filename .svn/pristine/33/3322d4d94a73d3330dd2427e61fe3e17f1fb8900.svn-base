package com.wcs.common.filenet.constants;

public class PEConstants {
	
	private PEConstants() {
		
	}
	
	public static final String DEFAULT_ROSTER_NAME = "DefaultRoster";
	
	public static final String DEFAULT_EVENT_LOG = "DefaultEventLog";
	// System Fields defined by PE
	/** The name of the work class. */
	public static final String PE_SYSTEM_FIELD_WORK_CLASS_NAME = "F_Class";
	/**
	 * This field is initialized to null every time that a step starts. It
	 * allows the users participating in a step to share information about the
	 * step. For example, when a user reassigns the step, he can write a comment
	 * for the user receiving the step.
	 */
	public static final String PE_SYSTEM_FIELD_COMMENT = "F_Comment";
	/** The time at which the work item was created. */
	public static final String PE_SYSTEM_FIELD_CREATE_TIME = "F_CreateTime";
	/**
	 * The time at which the work item entered the queue or was updated in the
	 * queue.
	 */
	public static final String PE_SYSTEM_FIELD_ENQUEUE_TIME = "F_EnqueueTime";
	public static final String PE_SYSTEM_FIELD_START_TIME = "F_StartTime";
	public static final String PE_SYSTEM_FIELD_ORIGINATOR = "F_Originator";
	/**
	 * The work item's lock status:
	 * <p>
	 * <li>0=not locked</li>
	 * <li>1=locked by user</li>
	 * <li>2=locked by system</li>
	 * </p>
	 */
	public static final String PE_SYSTEM_FIELD_LOCKED = "F_Locked";

	/**
	 * 
	 */
	public static final String PE_SYSTEM_FIELD_LOCKUSERNAME = "F_LockUser";

	/**
	 * Whether the work item is overdue:
	 * <p>
	 * <li>0=work item is not overdue</li>
	 * <li>1=reminder has expired for this step</li>
	 * <li>2=deadline has expired for this step</li>
	 */
	public static final String PE_SYSTEM_FIELD_OVERDUE = "F_Overdue";
	/**
	 * Transient field. It only has a value if the work item has been saved with
	 * a response. Derived from F_Responses.
	 */
	public static final String PE_SYSTEM_FIELD_RESPONSE = "F_Response";
	/**
	 * The name of the step that is either in process or (if no step is
	 * currently in process) is next to be executed for the work item.
	 */
	public static final String PE_SYSTEM_FIELD_STEP_NAME = "F_StepName";
	/**
	 * The status of the step. Valid values are Complete, In progress, or
	 * Deleted.
	 */
	public static final String PE_SYSTEM_FIELD_STEP_STATUS = "F_StepStatus";
	/**
	 * The subject entered by the user when a workflow is launched. This field
	 * is used in the out-of-the-box Workplace application to populate the Name
	 * field on the Tasks page.
	 */
	public static final String PE_SYSTEM_FIELD_SUBJECT = "F_Subject";
	/**
	 * A 16-byte binary field that is actually a GUID (global unique
	 * identifier). It uniquely identifies a single work item.
	 */
	public static final String PE_SYSTEM_FIELD_WOBNUM = "F_WobNum";
	
	/**
	 * eventLog中流程号为空的常�?
	 */
	public static final String EMPTY_WORKFLOWNUM_IN_EVENTLOG = "00000000000000000000000000000000";
	
	/**
	 * eventLog中代表流程step记录的常量?
	 */
	//352 (WPWorkObjectQueued)
	public static final String EVENT_TYPE_WORKOBJECT_QUEUED = "352";
	//510 (WOCompleteSysStepMsg)
	public static final String EVENT_TYPE_COMPLETE_MSG = "510";
	//165 (WFTerminationMsg)
	public static final String EVENT_TYPE_WF_TERMINATION_MSG = "165";
	//350 (WPBeginService)
	public static final String EVENT_TYPE_BEGIN_SERVICE_NORMAL = "350";
	//360 (WPEndServiceNormal)
	public static final String EVENT_TYPE_END_SERVICE_NORMAL = "360";
	
	/**
	 * eventLog中代表流程状态的常来那个完成
	 */
	public static final String EVENT_LOG_COMPLETE_FLAG = "CompleteFlag";
	
	/**
	 * eventLog中代表流程取消
	 */
	public static final String EVENT_LOG_WORKFLOW_CANCEL = "cancel";
	
	/**
	 * eventLog中代表流程结束
	 */
	public static final String EVENT_LOG_WORKFLOW_COMPLETE = "complete";
	
	/**
	 * 取影像pid的queueName
	 */
	public static final String ICM_OPERATIONS_QUEUE_NAME = "ICM_Operations";
	/**
	 * 共享索引
	 */
	public static final String SHARE_QUEUE_NAMEINDEX = "createIdx";

}
