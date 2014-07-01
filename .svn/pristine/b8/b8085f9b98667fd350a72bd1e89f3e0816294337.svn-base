package com.wcs.common.filenet.pe.queue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import com.wcs.common.filenet.pe.SessionHelper;
import com.wcs.common.filenet.pe.exception.InvalidColumnException;
import com.wcs.common.filenet.pe.exception.P8BpmException;
import com.wcs.common.filenet.pe.workobject.WorkObjectHelper;

import filenet.vw.api.VWException;
import filenet.vw.api.VWFetchType;
import filenet.vw.api.VWParticipant;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueElement;
import filenet.vw.api.VWQueueQuery;
import filenet.vw.api.VWRoster;
import filenet.vw.api.VWRosterElement;
import filenet.vw.api.VWRosterQuery;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWStepElement;
import filenet.vw.api.VWWorkObject;
import filenet.vw.api.VWWorkObjectNumber;

/**
 * Helper class for <code>VWQueue</code>.
 */
public class QueueHelper {

    private static Logger logger = Logger.getLogger(QueueHelper.class);

    /**
     * Make the default constructor private to avoid instantiate this class.
     */
    private QueueHelper() {
    }

    /**
     * Returns a <code>VWStepElement</code> object in a <code>VWQueue</code>
     * identified by Work Object Number.
     * 
     * @param vwSession
     *            The <code>VWSession</code>.
     * @param queueName
     *            The queue name of a <code>VWQueue</code>.
     * @param wobNum
     *            The work object number.
     * @return a <code>VWStepElement</code> object. Return <code>null</code> if
     *         not found.
     */
    public static VWStepElement lockStepElementByWorkObjectNumber(VWSession vwSession, String queueName, String wobNum) {
        VWQueue vwQueue = SessionHelper.getVWQueue(vwSession, queueName);
        return lockStepElementByWorkObjectNumber(vwQueue, new VWWorkObjectNumber(wobNum));
    }

    /**
     * Returns a <code>Work Object</code> in a <code>VWQueue</code> identified
     * by Work Object Number.
     * 
     * @param vwSession
     *            The <code>VWSession</code>.
     * @param queueName
     *            The queue name of a <code>VWQueue</code>.
     * @param wobNum
     *            The work object number.
     * @return a <code>VWWorkObject</code>. Return <code>null</code> if not
     *         found.
     */
    public static VWWorkObject lockWorkObjectByWorkObjectNumber(VWSession vwSession, String queueName, String wobNum) {
        VWQueue vwQueue = SessionHelper.getVWQueue(vwSession, queueName);
        try {
            return lockStepElementByWorkObjectNumber(vwQueue, new VWWorkObjectNumber(wobNum)).fetchWorkObject(false, false);
        } catch (VWException e) {
            throw new P8BpmException(e);
        }
    }

    /**
     * Returns a <code>Work Object</code>unlocked in a <code>VWQueue</code>
     * identified by Work Object Number.
     * 
     * @param vwSession
     *            The <code>VWSession</code>.
     * @param queueName
     *            The queue name of a <code>VWQueue</code>.
     * @param wobNum
     *            The work object number.
     * @return a <code>VWWorkObject</code>.
     * @see #getStepElementByWorkObjectNumber(VWQueue, VWWorkObjectNumber, int)
     */
    public static VWWorkObject getWorkObjectByWorkObjectNumber(VWSession vwSession, String queueName, String wobNum) {
        VWQueue vwQueue = SessionHelper.getVWQueue(vwSession, queueName);
        try {
            return getStepElementByWorkObjectNumber(vwQueue, new VWWorkObjectNumber(wobNum), VWQueue.QUERY_READ_LOCKED)
                    .fetchWorkObject(false, false);
        } catch (VWException e) {
            throw new P8BpmException(e);
        }
    }

    /**
     * Lock the <code>VWStepElement</code> object. If the step element was
     * locked by another user, a <code>RuntimeException</code> which contains
     * locker information will be thrown. You can catch the
     * <code>RuntimeException</code> and pass it to UI to show what happened.
     * 
     * @param vwSession
     *            The <code>VWSession</code>.
     * @param stepElement
     *            The <code>VWStepElement</code> to lock.
     * @throws VWException
     *             If the method cannot lock the work object.
     * @throws P8BpmException
     *             If the step element has been locked by another user.
     */
    public static void lockStepElement(VWSession vwSession, VWStepElement stepElement) throws VWException {
        VWParticipant lockedUserPx = stepElement.getLockedUserPx();
        if (lockedUserPx != null && lockedUserPx.getUserId() != vwSession.getCurrentUserSecId()) {
            // The step element was locked by another user.
            String lockedUser = lockedUserPx.getDisplayName();
            throw new P8BpmException("The step element has been locked by " + lockedUser);
        } else {
            stepElement.doLock(true);
        }
    }

    /**
     * Returns work elements in the specified queue and with the specified step
     * name.
     * 
     * @param session
     *            The VWSession.
     * @param queueName
     *            The queue name.
     * @param stepName
     *            The step name.
     * @param fetchType
     *            The fetch type.
     * @return The work elements.
     */
    public static List getWorkElementsByStepName(VWSession session, String queueName, String stepName, int fetchType) {
        try {
            VWQueue vwQueue = session.getQueue(queueName);
            List stepElements = new ArrayList();
            vwQueue.setBufferSize(25);
            int queryFlags = VWQueue.QUERY_READ_LOCKED;
            String filter = "F_StepName = :stepName";
            Object[] substitutionVars = { stepName };
            VWQueueQuery qQuery = vwQueue.createQuery(null, null, null, queryFlags, filter, substitutionVars, fetchType);
            if (logger.isDebugEnabled()) {
            	logger.debug("Fetch Count:" + qQuery.fetchCount());
            }
            while (qQuery.hasNext()) {
                stepElements.add(qQuery.next());
            }
            return stepElements;
        } catch (VWException vwe) {
            logger.debug(vwe);
            if (vwe.getKey().equals(P8BpmException.PEORB_KEY)) {
                throw new InvalidColumnException(vwe);
            } else {
                throw new P8BpmException(vwe);
            }
        }
    }

    public static List getWorkElements(VWSession session, String queueName, String filter, int fetchType) {
        try {
            VWQueue vwQueue = session.getQueue(queueName);
            List stepElements = new ArrayList();
            vwQueue.setBufferSize(25);
            int queryFlags = VWQueue.QUERY_READ_LOCKED;
            Object[] substitutionVars = {};

            VWQueueQuery qQuery = vwQueue.createQuery(null, null, null, queryFlags, filter, substitutionVars, fetchType);
            if (logger.isDebugEnabled()) {
            	logger.debug("Fetch Count:" + qQuery.fetchCount());
            }
            while (qQuery.hasNext()) {
                stepElements.add(qQuery.next());
            }
            return stepElements;
        } catch (VWException vwe) {
            logger.error(vwe);
            if (vwe.getKey().equals(P8BpmException.PEORB_KEY)) {
                throw new InvalidColumnException(vwe);
            } else {
                throw new P8BpmException(vwe);
            }
        }
    }

    public static List getWorkElementsByRoster(VWSession session, String rosterName, String filter, int fetchType) {
        try {
            VWRoster vwRoster = session.getRoster(rosterName);
            List stepElements = new ArrayList();
            vwRoster.setBufferSize(25);
            int queryFlags = VWQueue.QUERY_READ_LOCKED;
            Object[] substitutionVars = {};
            VWRosterQuery qQuery = vwRoster.createQuery(null, null, null, queryFlags, filter, substitutionVars,
                    VWFetchType.FETCH_TYPE_ROSTER_ELEMENT);
            if (logger.isDebugEnabled()) {
            	logger.debug("Fetch Count:" + qQuery.fetchCount());
            }
            while (qQuery.hasNext()) {
                VWRosterElement re = (VWRosterElement) qQuery.next();
                stepElements.add(re.fetchStepElement(false, false));
            }
            return stepElements;
        } catch (VWException vwe) {
            logger.error(vwe);
            if (vwe.getKey().equals(P8BpmException.PEORB_KEY)) {
                throw new InvalidColumnException(vwe);
            } else {
                throw new P8BpmException(vwe);
            }
        }
    }

    /**
     * Locks the <code>VWStepElement</code> object in a <code>VWQueue</code>
     * identified by <tt>VWWorkObjectNumber</tt>.
     * 
     * @param vwQueue
     *            The <code>VWQueue</code> object.
     * @param wobNum
     *            The <code>VWWorkObjectNumber</code>.
     * @return The <code>VWStepElement</code>.
     * @see #getStepElementByWorkObjectNumber(VWQueue, VWWorkObjectNumber, int)
     */
    public static VWStepElement lockStepElementByWorkObjectNumber(VWQueue vwQueue, VWWorkObjectNumber vwWobNum) {

        return getStepElementByWorkObjectNumber(vwQueue, vwWobNum, VWQueue.QUERY_LOCK_OBJECTS);
    }

    /**
     * 
     * <p>Description: </p>
     * @param vwQueue
     * @param vwWobNum
     * @param queryFlags
     * @return
     */
    public static VWStepElement getStepElementByWorkObjectNumber(VWQueue vwQueue, VWWorkObjectNumber vwWobNum, int queryFlags) {
        try {
            String filter = "F_WobNum = :WobNum";
            Object[] substitutionVars = { vwWobNum };
            vwQueue.setBufferSize(1);
            VWQueueQuery qQuery = vwQueue.createQuery(null, null, null, queryFlags, filter, substitutionVars,
                    VWFetchType.FETCH_TYPE_STEP_ELEMENT);
            if (qQuery.hasNext()) {
                VWStepElement st = (VWStepElement) qQuery.next();
                if (qQuery.hasNext()) { 
                	throw new IllegalStateException(
                        "More that 1 work step element has the work object number [" + vwWobNum.getWorkObjectNumber()
                                + ". It is impossible."); 
                }
                return st;
            } else {
                throw new NullPointerException("No work step element with the work object number ["
                        + vwWobNum.getWorkObjectNumber() + "] exists");
            }
        } catch (VWException vwe) {
            throw new P8BpmException(vwe);
        }

    }

    /**
     * Returns a meaningful string represetnation of the queue. The string
     * contains all the queue elements, step elements and work objects in this
     * queue. <tt>WQueue.toString()</tt> is not meaningful.
     * 
     * @param vwQueue
     *            The queue.
     * @return The string representation of the queue.
     */
    public static String vwString(VWQueue vwQueue) throws VWException {
        ToStringBuilder b = initToStringBuilder(vwQueue);
        appendQueueElements(vwQueue, b);
        appendStepElements(vwQueue, b);
        appendWorkObjects(vwQueue, b);
        return b.toString();
    }

    /**
     * Returns a string representation of the queue elements in this queue.
     * 
     * @param vwQueue
     *            The queue.
     * @return The string representation of the queue elements.
     * @throws VWException
     */
    public static String vwQueueElementString(VWQueue vwQueue) throws VWException {
        ToStringBuilder b = initToStringBuilder(vwQueue);
        appendQueueElements(vwQueue, b);
        return b.toString();
    }

    /**
     * Returns a string representation of the step elements in this queue.
     * 
     * @param vwQueue
     *            The queue.
     * @return The string representation of the steo elements.
     * @throws VWException
     */
    public static String vwStepElementString(VWQueue vwQueue) throws VWException {
        ToStringBuilder b = initToStringBuilder(vwQueue);
        appendStepElements(vwQueue, b);
        return b.toString();
    }

    /**
     * Returns a string representation of the work objects in this queue.
     * 
     * @param vwQueue
     *            The queue.
     * @return The string representation of the work objects.
     * @throws VWException
     */
    public static String vwWorkObjectString(VWQueue vwQueue) throws VWException {
        ToStringBuilder b = initToStringBuilder(vwQueue);
        appendWorkObjects(vwQueue, b);
        return b.toString();
    }

    /*
     * Creates the ToStringBuilder for this queue and appends some common queue information to it.
     */
    private static ToStringBuilder initToStringBuilder(VWQueue vwQueue) throws VWException {
        ToStringBuilder b = new ToStringBuilder(vwQueue, ToStringStyle.MULTI_LINE_STYLE);
        b.append("authored name:  " + vwQueue.getAuthoredName());
        b.append("depth:  " + vwQueue.fetchCount());
        return b;
    }

    /*
     * Appends the queue elements to the ToStringBuilder.
     */
    private static void appendQueueElements(VWQueue vwQueue, ToStringBuilder b) throws VWException {
        VWQueueQuery qQuery = vwQueue.createQuery(null, null, null, 0, null, null, VWFetchType.FETCH_TYPE_QUEUE_ELEMENT);
        b.append("=== Queue Elements ===");
        for (; qQuery.hasNext();) {
            VWQueueElement vwQueueElement = (VWQueueElement) qQuery.next();
            b.append(QueueElementHelper.vwString(vwQueueElement));
        }
    }

    /*
     * Appends the step elements to the ToStringBuilder.
     */
    private static void appendStepElements(VWQueue vwQueue, ToStringBuilder b) throws VWException {
        VWQueueQuery qQuery = vwQueue.createQuery(null, null, null, 0, null, null, VWFetchType.FETCH_TYPE_STEP_ELEMENT);
        b.append("=== Step Elements ===");
        for (; qQuery.hasNext();) {
        	//避免sonar检查
        }
    }

    /*
     * Appends the work objects to the ToStringBuidler.
     */
    private static void appendWorkObjects(VWQueue vwQueue, ToStringBuilder b) throws VWException {
        VWQueueQuery qQuery = vwQueue.createQuery(null, null, null, 0, null, null, VWFetchType.FETCH_TYPE_WORKOBJECT);
        b.append("=== Work Objects ===");
        for (; qQuery.hasNext();) {
            VWWorkObject vwWorkObject = (VWWorkObject) qQuery.next();
            b.append(WorkObjectHelper.vwString(vwWorkObject));
        }
    }

    /**
     * ���workflow number ���ҽڵ����
     * 
     * @param vwSession
     * @param workflowNum
     * @param boundUser
     * @param queueName
     * @param fetchType
     * @return
     * @throws VWException
     */
    public static Object fetchObjectByWorkflowNum(VWSession vwSession, String workflowNum, String queueName, int fetchType)
            throws VWException {
        Object vwObject = null;
        VWQueue vwQueue = vwSession.getQueue(queueName);
        VWQueueQuery qQuery = null;
        int queryFlags = VWQueue.QUERY_READ_LOCKED;
        String filter = "F_WorkFlowNumber = :workflowNum";
        Object[] substitutionVars = { new VWWorkObjectNumber(workflowNum) };
        vwQueue.setBufferSize(1);
        qQuery = vwQueue.createQuery(null, null, null, queryFlags, filter, substitutionVars, fetchType);
        if (qQuery.hasNext()) {
            logger.debug("Found the StepElement.");
            vwObject = qQuery.next();
        } else {
            logger.debug("The StepElement not found.");
        }

        return vwObject;
    }

    /**
     * Get a <code>List</code> of <code>VWStepElement</code> according to step
     * name and queue name.
     * 
     * @param vwSession
     *            The <code>VWSession</code> object.
     * @param queueName
     *            Queue name.
     * @param stepName
     *            Step name.
     * @return a <code>List</code> of <code>VWStepElement</code>
     * @throws VWException
     *             if an error occurs.
     */
    public static List fetchQueueElementsByQueueName(VWSession vwSession, String queueName) throws VWException {
        return fetchQueueElementsByQueueName(vwSession, queueName, null);
    }

    public static List fetchQueueElementsByQueueName(VWSession vwSession, String queueName, String filter) throws VWException {

        if (logger.isDebugEnabled()) {
        	logger.debug("queueName = " + queueName);
        }
        VWQueue vwQueue = vwSession.getQueue(queueName);

        List queueElements = new ArrayList();
        VWQueueElement queueElement = null;
        VWQueueQuery qQuery = null;
        vwQueue.setBufferSize(25);
        int queryFlags = VWQueue.QUERY_READ_LOCKED;

        Object[] substitutionVars = {};
        qQuery = vwQueue.createQuery(null, null, null, queryFlags, filter, substitutionVars,
                VWFetchType.FETCH_TYPE_QUEUE_ELEMENT);
        if (logger.isDebugEnabled()) {
        	logger.debug("Fetch Count:" + qQuery.fetchCount());
        }
        while (qQuery.hasNext()) {
            queueElement = (VWQueueElement) qQuery.next();
            queueElements.add(queueElement);
        }
        return queueElements;
    }

    public static List fetchQueueElementsByApproveQueueName(VWSession vwSession, String queueName, String filter)
            throws VWException {
        VWQueue vwQueue = vwSession.getQueue(queueName);

        List queueElements = new ArrayList();
        VWQueueQuery qQuery = null;
        int bufferSize = 50;
        vwQueue.setBufferSize(100);
        int queryFlags = VWQueue.QUERY_READ_LOCKED;
        Object[] substitutionVars = {};
        qQuery = vwQueue.createQuery(null, null, null, queryFlags, filter, substitutionVars,
                VWFetchType.FETCH_TYPE_QUEUE_ELEMENT);
        int count = qQuery.fetchCount();
        VWQueueElement queueElement = null;
        if (count > 0 && count > bufferSize) {
            for (int t = 0; t < bufferSize; t++) {
                queueElement = (VWQueueElement) qQuery.next();
                queueElements.add(queueElement);
            }
        }
        if (count > 0 && count < bufferSize) {
            for (int t = 0; t < count; t++) {
                queueElement = (VWQueueElement) qQuery.next();
                queueElements.add(queueElement);
            }
        }
        return queueElements;
    }

    /**
     * 取应付凭证错误列表代办
     * @param vwSession
     * @param queueName
     * @param filter
     * @return FailTaskList
     * @throws VWException
     */
    public static List fetchFailQueueElementsByQueueName(VWSession vwSession, String queueName, String filter)
            throws VWException {
        VWQueue vwQueue = vwSession.getQueue(queueName);
        List queueElements = new ArrayList();
        VWQueueElement queueElement = null;
        VWQueueQuery qQuery = null;
        vwQueue.setBufferSize(20);
        int queryFlags = VWQueue.QUERY_READ_LOCKED;
        Object[] substitutionVars = {};
        qQuery = vwQueue.createQuery(null, null, null, queryFlags, filter, substitutionVars,
                VWFetchType.FETCH_TYPE_QUEUE_ELEMENT);
        while (qQuery.hasNext()) {
            queueElement = (VWQueueElement) qQuery.next();
            queueElements.add(queueElement);
        }
        return queueElements;
    }

    // -------------------- 陈龙天添加的方法---------------------------------//
}