package com.wcs.common.filenet.pe;

import java.lang.reflect.Method;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.wcs.tms.exception.ServiceException;

import filenet.vw.api.VWLogElement;
import filenet.vw.api.VWQueueElement;
import filenet.vw.api.VWStepElement;
import filenet.vw.api.VWWorkObject;

/**
 * Helper of work element. The following PE API classes all extends
 * <tt>VWWorkElement</tt>:
 * <ul>
 * <li><tt>VWWorkObject</tt></li>
 * <li><tt>VWQueueElement</tt></li>
 * <li><tt>VWStepElement</tt></li>
 * <li><tt>VWRosterElement</tt></li>
 * <li><tt>VWLogElement</tt></li>
 * </ul>
 * But the common stuff among these classes are not specified in
 * <tt>VWWorkElement</tt>. This helper provides some common methods on these
 * classes.
 */
public class WorkElementHelper {

    /**
     * Private constructor to block instantiation.
     */
    private WorkElementHelper() {
    }

    /**
     * Appends the comment information of the work element to the
     * ToStringBuilder.
     * 
     * @param obj The work element (queue elemennt, step element and work
     *            object).
     * @param b The ToStringBuilder.
     * @throws IllegalArgumentException If the given object is not one of queue
     *             elememt, step element or work objec.
     * @throws P8BpmException If an error occurs when invoking methods on the
     *             work element with the use of reflection.
     */
    public static void append(Object obj, ToStringBuilder b) {
        if (!(obj instanceof VWQueueElement || obj instanceof VWStepElement || obj instanceof VWWorkObject)) {
        	throw new IllegalArgumentException(
                    obj
                            + " is not one of queue element, step element or work object");
        }
            
        b.append("=== work element common properties ===");
        b.append("WorkObjectNumber", invoke(obj, "getWorkObjectNumber"));
        b.append("WorkObjectName", invoke(obj, "getWorkObjectName"));
        b.append("WorkClassName", invoke(obj, "getWorkClassName"));
        b.append("Tag", invoke(obj, "getTag"));
        b.append("WorkflowName", invoke(obj, "getWorkflowName"));
        b.append("CurrentOperationName", invoke(obj, "getOperationName"));
        b.append("=== ------------------------------------------ ==");
    }

    /**
     * Appends the fields to the ToStringBuilder.
     * 
     * @param o The work element (log element and queue element).
     * @param fieldNames The field names.
     * @param b The string buffer.
     * @throws IllegalArgumentException If the given object is neither log
     *             element nor queue element.
     * @throws P8BpmException If an error occurs when invoking methods on the
     *             work element with the use of reflection.
     */
    public static void appendFields(Object o,
                                    String[] fieldNames,
                                    ToStringBuilder b) {
        if (!(o instanceof VWLogElement || o instanceof VWQueueElement)) {
        	throw new IllegalArgumentException(o
                    + " is neither queue element nor log element");
        }
        try {
            Class cls = o.getClass();
            Method m = cls.getMethod("getFieldValue",
                    new Class[] { String.class });
            for (int i = 0; i < fieldNames.length; i++) {
                if (fieldNames[i] != null) {
                    Object value = m.invoke(o, new Object[] { fieldNames[i] });
                    b.append(fieldNames[i], value);
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /*
     * Invoke the method on the object.
     */
    private static String invoke(Object o, String methodName) {
        try {
            Class cls = o.getClass();
            return (String) cls.getMethod(methodName, null).invoke(o, null);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

}
