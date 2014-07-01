package com.wcs.common.filenet.pe.workobject;

import java.util.Date;

import com.wcs.common.filenet.constants.PEConstants;


import filenet.vw.api.VWException;
import filenet.vw.api.VWQueueElement;
import filenet.vw.api.VWStepElement;
import filenet.vw.api.VWWorkObject;

public class WorkObjectUtil {
    private WorkObjectUtil() {
    }

    public static WorkObject getWorkObjectFromVWWorkObject(VWWorkObject vwWorkObject)
        throws VWException {
        WorkObject workObj = new WorkObject();
        workObj.setWorkObjectNumber(vwWorkObject.getWorkObjectNumber());
        workObj.setEnqueueTime((Date) vwWorkObject.getFieldValue(PEConstants.PE_SYSTEM_FIELD_ENQUEUE_TIME));
        workObj.setLocked(((Integer) vwWorkObject.getFieldValue(PEConstants.PE_SYSTEM_FIELD_LOCKED)).intValue());
        workObj.setLockUsername(vwWorkObject.getLockedUser());
        workObj.setQueueName(vwWorkObject.getCurrentQueueName());
        workObj.setSubject(vwWorkObject.getSubject());
        workObj.setWorkflowName(vwWorkObject.getWorkflowName());
        workObj.setComment(vwWorkObject.getComment());
        workObj.setDateReceived(vwWorkObject.getDateReceived());
        workObj.setLaunchDate(vwWorkObject.getLaunchDate());
        workObj.setOriginator(vwWorkObject.getOriginator());
        workObj.setSelectedResponse(vwWorkObject.getSelectedResponse());
        workObj.setStepResponses(vwWorkObject.getStepResponses());
        workObj.setStepName(vwWorkObject.getStepName());
        workObj.setClassName(vwWorkObject.getWorkflowName());
        return workObj;
    }

    public static WorkObject getWorkObjectFromVWQueueElement(VWQueueElement vwQueueElement)
        throws VWException {
        WorkObject workObj = new WorkObject();
        workObj.setWorkObjectNumber(vwQueueElement.getWorkObjectNumber());
        workObj.setEnqueueTime((Date) vwQueueElement.getFieldValue(PEConstants.PE_SYSTEM_FIELD_ENQUEUE_TIME));
        workObj.setLocked(((Integer) vwQueueElement.getFieldValue(PEConstants.PE_SYSTEM_FIELD_LOCKED)).intValue());
        workObj.setLockUsername(vwQueueElement.getLockedUser());
        workObj.setQueueName(vwQueueElement.getQueueName());
        workObj.setSubject(vwQueueElement.getSubject());
        workObj.setWorkflowName(vwQueueElement.getWorkflowName());
        workObj.setLaunchDate((Date) vwQueueElement.getFieldValue("F_StartTime"));
        workObj.setStepName(vwQueueElement.getStepName());
        workObj.setClassName(vwQueueElement.getWorkflowName());
        return workObj;
    }

    public static WorkObject getWorkObjectFromVWStepElement(VWStepElement vwStepElement)
        throws VWException {
        WorkObject workObj = new WorkObject();
        workObj.setWorkObjectNumber(vwStepElement.getWorkObjectNumber());
        workObj.setStepName(vwStepElement.getStepName());
        workObj.setWorkflowName(vwStepElement.getWorkflowName());
        return workObj;
    }
}
