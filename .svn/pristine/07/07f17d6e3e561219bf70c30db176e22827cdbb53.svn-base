package com.wcs.tms.service.process.common.patch.pe;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.wcs.common.filenet.pe.exception.P8BpmException;

import filenet.vw.api.VWException;
import filenet.vw.api.VWParticipant;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWSession;

/**
 * Helper class for <code>VWSession</code>. Provides logon and logoff methods.
 */
public class PatchSessionHelper {

	private PatchSessionHelper() {
		
	}
	
    static final String INBOX_NAME = "Inbox(0)";

    private static Logger logger = Logger.getLogger(PatchSessionHelper.class);

    /**
     * Creates a logon session.
     * 
     * @return The <code>VWSession</code>.
     * @throws P8BpmException
     */
    public static VWSession logon(String userName, String password) {
        try {
        	String connectionPointName = ConfigUtils.getString("PE.ConnectionPointName");
        	String ceUrl = ConfigUtils.getString("CE.CEURL");
        	logger.info("connectionpoint:"+connectionPointName);
        	logger.info("ceUrl:"+ceUrl);
            VWSession vwSession = new VWSession(userName,
                                                password,
                                                connectionPointName);
            logger.info("User '" + userName + "' logon filenet pe successfully");
            return vwSession;
        } catch (VWException vwe) {
        	logger.error("logon方法 Creates a logon session出现异常", vwe);
            throw new P8BpmException(vwe);
        }
    }

    /**
     * Logoffs a <code>VWSession</code>.
     * 
     * @param vwSession the <code>VWSession</code> to logoff.
     */
    public static void logoff(VWSession vwSession) {
        try {
            if (vwSession != null) {
                String username = vwSession.convertIdToUserName((int) vwSession.getCurrentUserSecId());
                if (logger.isDebugEnabled()) {
                	logger.info("User \"" + username + "\" logoff.");
                }
                vwSession.logoff();
            }
        } catch (VWException e) {
        	logger.error("logoff方法 Logoffs a <code>VWSession</code>出现异常", e);
            throw new P8BpmException(e);
        }
    }
    
    /**
     * Returns the names of all the queues (user queue, work queue and system
     * queue) for the given session.
     * 
     * @param vwSession The session.
     * @return The queue names.
     */
    public static List getAllQueueNames(VWSession vwSession) throws VWException {
        int nFlags = VWSession.QUEUE_PROCESS | 
                     VWSession.QUEUE_USER_CENTRIC| 
                     VWSession.QUEUE_IGNORE_SECURITY | 
                     VWSession.QUEUE_SYSTEM;
        String[] queueNames = vwSession.fetchQueueNames(nFlags);
        return Arrays.asList(queueNames);
    }

    /**
     * Returns the <tt>VWQueue</tt> in the <tt>session</tt> with the given queue
     * name.
     * 
     * @param session The <tt>VWSession</tt>
     * @param queueName The queue name
     * @return The <tt>VWQueue</tt>
     */
    public static VWQueue getVWQueue(VWSession session, String queueName) {
        try {
            return session.getQueue(queueName);
        } catch (VWException vwe) {
        	logger.error("getVWQueue方法出现异常", vwe);
            throw new P8BpmException(vwe);
        }
    }

    /**
     * Returns the inbox user queue in the <tt>session</tt>.
     * 
     * @param session The <tt>VWSession</tt>
     * @return The inbox user queue
     */
    public static VWQueue getUserQueue(VWSession session) {
        return getVWQueue(session, INBOX_NAME);
    }

    public static VWParticipant getParticipant(VWSession session, long userId) {
        try {
            return session.convertIdToUserNamePx(userId);
        } catch (VWException vwe) {
        	logger.error("VWParticipant方法出现异常", vwe);
            throw new P8BpmException(vwe);
        }
    }
}
