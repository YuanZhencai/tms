package com.wcs.common.filenet.pe.queue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.wcs.common.filenet.pe.exception.P8BpmException;

import filenet.vw.api.VWException;
import filenet.vw.api.VWExposedFieldDefinition;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueDefinition;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWSystemConfiguration;

/**
 * A helper for operations related to <tt>VWQueueDefinition</tt>.
 */
public class QueueDefinitionHelper {
	
	private QueueDefinitionHelper() {
		
	}
    private static Map nameToSystemFields;

    static {
        try {
            nameToSystemFields = new HashMap();
            VWExposedFieldDefinition[] fields = VWQueueDefinition.optionalSystemFields();
            for (int i = 0; i < fields.length; i++) {
                VWExposedFieldDefinition feild = fields[i];
                nameToSystemFields.put(feild.getName(), feild);
            }
        } catch (VWException vwe) {
            throw new P8BpmException(vwe);
        }
    }

    public static void createWorkQueueQueue(VWSession vwSession,
                                            String queueName) {
        try {
            VWSystemConfiguration sysConfig = vwSession.fetchSystemConfiguration();

            VWQueueDefinition queueDef = sysConfig.createQueueDefinition(
                    queueName, VWQueue.QUEUE_TYPE_PROCESS);
            String[] fieldNames = { "F_Overdue", "F_Subject", "F_StepName",
                    "F_TimeOut" };
            VWExposedFieldDefinition[] systemFields = QueueDefinitionHelper.toOptionalSystemFields(fieldNames);
            queueDef.createFieldDefinitions(systemFields);
            sysConfig.commit();
        } catch (VWException vwe) {
            throw new P8BpmException(vwe);
        }
    }

    private static VWExposedFieldDefinition[] toOptionalSystemFields(String[] fieldNames)
        throws VWException {
        return toOptionalSystemFields(Arrays.asList(fieldNames));
    }

    /**
     * Returns the optional system fields whose names are contained in the given
     * names.
     * 
     * @param fieldNames
     * @return The exposed queue fields.
     * @throws IllegalArgumentException If one of the field names does not
     *             specify an optional system field.
     * @throws VWException
     */
    private static VWExposedFieldDefinition[] toOptionalSystemFields(Collection fieldNames)
        throws VWException {
        List systemFields = new ArrayList();
        for (Iterator it = fieldNames.iterator(); it.hasNext();) {
            String fieldName = (String) it.next();
            VWExposedFieldDefinition field = (VWExposedFieldDefinition) nameToSystemFields.get(fieldName);
            if (field != null) {
                systemFields.add(field);
            } else {
                throw new IllegalArgumentException("'" + fieldName
                        + "' is not an optional system field");
            }
        }
        return (VWExposedFieldDefinition[]) systemFields.toArray(new VWExposedFieldDefinition[systemFields.size()]);
    }

}
