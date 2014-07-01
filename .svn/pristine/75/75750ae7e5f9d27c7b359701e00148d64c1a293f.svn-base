package com.wcs.common.filenet.pe.exception;

import filenet.vw.api.VWException;

/**
 * Thrown to indicate that an error occurs during the P8 workflow operations.
 */
public class P8BpmException extends RuntimeException {
    public static final String PEORB_KEY = "filenet.pe.peorb.serverException";

    /**
     * Constructs a <code>WorkflowExceptino</code> without detail message.
     */
    public P8BpmException() {
        super();
    }

    /**
     * Constructs a <code>WorkflowExceptino</code> with the specified detail
     * message.
     * 
     * @param msg The detail message.
     */
    public P8BpmException(String msg) {
        super(msg);
    }

    public P8BpmException(String firstMsg, Object[] otherMsgs) {
        this(firstMsg, toStrings(otherMsgs));
    }

    /**
     * Constructs a <code>WorkflowException</code> with the given first message
     * and other messages.
     * 
     * @param firstMsg The first message.
     * @param otherMsgs The other message.
     */
    public P8BpmException(String firstMsg, String[] otherMsgs) {
        super(constructMsg(firstMsg, otherMsgs));
    }

    /**
     * Constructs a <code>WorkflowExceptino</code> with specified nested
     * <code>Throwable</code>.
     * 
     * @param cause The exception or error that caused this exception to be
     *            thrown.
     */
    public P8BpmException(VWException cause) {
        super(getDefaultMessage(cause), cause);
    }

    /**
     * Constructs a <code>WorkflowExceptino</code> with specified detail message
     * and nested <code>Throwable</code>.
     * 
     * @param msg The detail message.
     * @param cause The exception or error that caused this exception to be
     *            thrown.
     */
    public P8BpmException(String msg, VWException cause) {
        super(msg + "\n" + getDefaultMessage(cause), cause);
    }

    public P8BpmException(String msg, Throwable cause) {
        super(msg + "\n" + cause.getMessage(), cause);
    }
    
    /*
     * Constructs the message.
     */
    private static String constructMsg(String firstMsg, String[] otherMsgs) {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append(firstMsg).append("\n");
        for (int i = 0; i < otherMsgs.length; i++) {
            strBuf.append(otherMsgs[i]).append("\n");
        }
        return strBuf.toString();
    }

    /*
     * Creates strings from objects using toString.
     */
    private static String[] toStrings(Object[] objs) {
        int length = objs.length;
        String[] strs = new String[length];
        for (int i = 0; i < length; i++) {
            strs[i] = objs[i].toString();
        }
        return strs;
    }

    private static String getDefaultMessage(VWException cause) {
        return "VWException.getMessage():" + cause.getMessage()
                + "\nVWException.getKey():" + cause.getKey();
    }
}
