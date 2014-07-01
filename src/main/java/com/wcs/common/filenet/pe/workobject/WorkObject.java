package com.wcs.common.filenet.pe.workobject;

import java.io.Serializable;
import java.util.Date;

/**
 * This class is used to wrapper the DTO, such as <code>AutoClaim</code>, to
 * show work items to user in his queue. It contains other information related
 * to step element in PE , such as work object number, enqueueTime.
 * 
 * @author Zhao ErXing
 */
public class WorkObject implements Serializable
{

    private static final long serialVersionUID = 3711260606964991008L;

    /**
     * Work object number, which is the identifier of a work item in a queue.
     */
    private String workObjectNumber;

    /**
     * The subject of the workflow.
     */
    private String subject;

    /**
     * The name of the workflow.
     */
    private String workflowName;

    /**
     * The name of the step.
     */
    private String stepName;
    
    /**
     * The description of the stepName.
     */
    private String stepNameDesc;

    /**
     * 锁定流程的用户的名字
     * 
     */

    private String lockUsername;


    
    /**
     * The work item's lock status:
     * <p>
     * <li>0=not locked</li>
     * <li>1=locked by user</li>
     * <li>2=locked by system</li>
     * </p>
     */
    private int locked;

    /**
     * The time at which the work item entered the queue or was updated in the
     * queue.
     */
    private Date enqueueTime;

    /**
     * The name of the queue.
     */
    private String queueName;

    /**
     * The target DTO to wrapper.
     */
    private Object target;
    
    private String comment;
    
    private Date dateReceived;
    
    private Date launchDate;
    
    private String originator;
    
    private String selectedResponse;
    
    private String[] stepResponses;
    
    private String className;
    
    private String fund;

    public String getFund() {
		return fund;
	}

	public void setFund(String fund) {
		this.fund = fund;
	}

	/**
     * @return the target DTO
     */
    public Object getTarget()
    {
        return target;
    }

    /**
     * @param target the target DTO to set
     */
    public void setTarget( Object target )
    {
        this.target = target;
    }

    public String getWorkObjectNumber()
    {
        return workObjectNumber;
    }

    public void setWorkObjectNumber( String workObjectNumber )
    {
        this.workObjectNumber = workObjectNumber;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject( String subject )
    {
        this.subject = subject;
    }

    public String getWorkflowName()
    {
        return workflowName;
    }

    public void setWorkflowName( String workflowName )
    {
        this.workflowName = workflowName;
    }

    public String getStepName()
    {
        return stepName;
    }

    public void setStepName( String stepName )
    {
        this.stepName = stepName;
    }

    public int getLocked()
    {
        return locked;
    }

    public void setLocked( int locked )
    {
        this.locked = locked;
    }

    public Date getEnqueueTime()
    {
        return (Date) enqueueTime.clone();
    }

    public void setEnqueueTime( Date enqueueTime )
    {
        this.enqueueTime = new Date( enqueueTime.getTime() );
    }

    public String getQueueName()
    {
        return queueName;
    }

    public void setQueueName( String queueName )
    {
        this.queueName = queueName;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment( String comment )
    {
        this.comment = comment;
    }

    public Date getDateReceived()
    {
        return dateReceived;
    }

    public void setDateReceived( Date dateReceived )
    {
        this.dateReceived = dateReceived;
    }

    public Date getLaunchDate()
    {
        return launchDate;
    }

    public void setLaunchDate( Date launchDate )
    {
        this.launchDate = launchDate;
    }

    public String getOriginator()
    {
        return originator;
    }

    public void setOriginator( String originator )
    {
        this.originator = originator;
    }

    public String getSelectedResponse()
    {
        return selectedResponse;
    }

    public void setSelectedResponse( String selectedResponse )
    {
        this.selectedResponse = selectedResponse;
    }

    public String[] getStepResponses()
    {
        return stepResponses;
    }

    public void setStepResponses( String[] stepResponses )
    {
        this.stepResponses = stepResponses;
    }

    public static long getSerialVersionUID()
    {
        return serialVersionUID;
    }

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getLockUsername() {
		return lockUsername;
	}

	public void setLockUsername(String lockUsername) {
		this.lockUsername = lockUsername;
	}

    public String getStepNameDesc() {
        return stepNameDesc;
    }

    public void setStepNameDesc(String stepNameDesc) {
        this.stepNameDesc = stepNameDesc;
    }

}
