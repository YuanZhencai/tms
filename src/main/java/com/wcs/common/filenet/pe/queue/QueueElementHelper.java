package com.wcs.common.filenet.pe.queue;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.wcs.common.filenet.pe.WorkElementHelper;
import com.wcs.common.filenet.pe.exception.P8BpmException;

import filenet.vw.api.VWException;
import filenet.vw.api.VWQueueElement;

/**
 * Helper of queue element.
 */
public class QueueElementHelper {
    /**
     * Private constructor to block instantiation.
     */
    private QueueElementHelper() {
    }

    /**
     * Returns a meaningful string representation of the queue element.
     * <tt>VWQueueElement.getString()</tt> is not meaningful.
     * 
     * @param qElem The queue element.
     * @return The string representation of the queue element.
     */
    public static String vwString(VWQueueElement qElem) throws VWException {
        ToStringBuilder b =  new ToStringBuilder(qElem, ToStringStyle.MULTI_LINE_STYLE); 

        b.append("=== System Defined Fields ===");
        String[] sysFieldNames = qElem.getSystemDefinedFieldNames();
        WorkElementHelper.appendFields(qElem, sysFieldNames, b);
        b.append("=== ------------------------------------------ ==");
        b.append("=== User Defined Fields ===");
        String[] userFieldNames = qElem.getUserDefinedFieldNames();
        WorkElementHelper.appendFields(qElem, userFieldNames, b);
        b.append("=== ------------------------------------------ ==");

        WorkElementHelper.append(qElem, b);
        b.append("CurrentQueueName", qElem.getQueueName());

        int ivalue = qElem.getLockedStatus();
        String svalue = null;
        switch (ivalue) {
        case VWQueueElement.LOCKED_BY_USER:
            svalue = "Locked by user";
            break;
        case VWQueueElement.LOCKED_BY_SYSTEM:
            svalue = "Locked by System";
            break;
        case VWQueueElement.LOCKED_BY_NONE:
            svalue = "Locked by No one";
            break;
        default:
            throw new P8BpmException();
        }
        b.append("CurrentLockStatus", svalue);
        b.append("CurrentInstructionSheetName", qElem.getLockedMachine());
        return b.toString();
    }

}
