package com.wcs.common.filenet.pe.workobject;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.wcs.common.filenet.pe.WorkElementHelper;
import com.wcs.common.filenet.pe.exception.P8BpmException;

import filenet.vw.api.VWException;
import filenet.vw.api.VWWorkObject;

/**
 * Helper of work object.
 */
public class WorkObjectHelper {

    /**
     * Private constructor to block instantiation.
     */
    private WorkObjectHelper() {
    }

    /**
     * Returns a meaning string representation of the work object.
     * <tt>VWWorkObject.toString()</tt> is not meaningful.
     * 
     * @param wo The work object.
     * @return The string representation of the work object.
     */
    public static String vwString(VWWorkObject wo) throws VWException {
        ToStringBuilder b =  new ToStringBuilder(wo, ToStringStyle.MULTI_LINE_STYLE); 
        b.append("work object name", wo.getWorkObjectName());

        String[] fNames = wo.getFieldNames();
        fNames = fNames == null ? new String[] {} : fNames;
        b.append("=== Fields ===");
        for (int i = 0; i < fNames.length; i++) {
            Object value = wo.getFieldValue(fNames[i]);
            b.append(fNames[i], value);
        }
        b.append("=== ------------------------------------------ ==");

        WorkElementHelper.append(wo, b);
        b.append("CurrentWorkPerformerClassName",
                 wo.getWorkPerformerClassName());
        return b.toString();
    }

    public static void doLock(VWWorkObject wo) {
        try {
            wo.doLock(true);
        } catch (VWException e) {
            throw new P8BpmException(e);
        }
    }

    public static void doSave(VWWorkObject wo) {
        try {
            wo.doSave(true);
        } catch (VWException e) {
            throw new P8BpmException(e);
        }
    }

    public static void doDispatch(VWWorkObject wo) {
        try {
            wo.doDispatch();
        } catch (VWException e) {
            throw new P8BpmException(e);
        }
    }

    public static void setSelectedResponse(VWWorkObject wo, String response) {
        try {
            wo.setSelectedResponse(response);
        } catch (VWException e) {
            throw new P8BpmException(e);
        }
    }

    public static Object getFieldValue(VWWorkObject wo, String name) {
        try {
            return wo.getFieldValue(name);
        } catch (VWException e) {
            throw new P8BpmException(e);
        }
    }
    
    public static String getFieldString(VWWorkObject wo, String name) {
        return (String) getFieldValue(wo, name);
    }
    
    public static Date getFieldDate(VWWorkObject wo, String name) {
        return (Date) getFieldValue(wo, name);
    }
    
    public static Integer getFieldInteger(VWWorkObject wo, String name) {
        return (Integer) getFieldValue(wo, name);
    }
    
    public static String[] getFieldStringArray(VWWorkObject wo, String name) {
        return (String[]) getFieldValue(wo, name);
    }
}
