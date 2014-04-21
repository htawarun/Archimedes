package com.bandsoftware.exception;

/*
 *    Title:         BSDException
 *    Description:   Subclass of runtime exception, used to catch/throw exceptions and hold prior execption stack
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *	  @date			 January 2003
 *    @version       1.0
 */

/*
 *	usage:  catch(Exception ex){
	 		1. throw new BSDException("Message",ex);
	 		2. throw new BSDException("MessageName",9999,"Message",ex);
	 		3. throw new BSDException("Message");
	 		4. throw new BSDException(9999,"Message",ex);

    where: 9999 - BSDErrors.MESSAGE_TITLE (this is a message number)

    caller can ask this exception for the ex.priorException() to walk the
    stack (ex instanceof BSDException only).
 */

public class BSDException extends RuntimeException {


    public String name = "";
    /**
     * This is the error message string for the exception
     */
    public String message = "";
    /**
     * The nested exception inside this exception.
     */
    public Exception object;
    /**
     * This is the error message number index used to get the exception's message from EOPErrors class
     *
     * @see
     */
    public int number = 0;


    public BSDException(String argMessage) {
        super(argMessage);
        init("", argMessage, null);
    }

    //Keep the last exception
    public BSDException(String argMessage, Exception exObj) {
        super(argMessage);
        init("", argMessage, exObj);
    }

    public BSDException(int msgNum, String argMessage, Exception ex) {
        super(argMessage);
        init("", argMessage, ex);
        setMessageNum(msgNum);
    }

    public BSDException(String msgName, int msgNum, String argMessage, Exception ex) {
        super(argMessage);
        init(msgName, argMessage, ex);
        setMessageNum(msgNum);
    }

    public BSDException(String msgName, String argMessage, Exception ex) {
        super(argMessage);
        init(msgName, argMessage, ex);
    }

    private void init(String exName, String exMsg, Exception exObj) {
        name = exName;
        message = exMsg;
        object = exObj;
    }

    public void setMessageNum(int num) {
        number = num;
    }

    public int getMessageNum() {
        return number;
    }
    // public void setStackTrace(Outputstream os) { stackTrace = os; }
    // public String getStackTrace(){ return stacktrace.toString(); }

    /**
     * This method returns a string which is the error messages for this exception and it's
     * nested exceptions.
     */
    public String toString() {

        StringBuffer buf = new StringBuffer();
        if (message != null && message.trim().length() > 0) {
            buf.append("msg no + " + number);
            buf.append(" message: " + message);
            if (object != null)
                buf.append(" error: " + ((Exception) object).getMessage());
            buf.append("\n");
        }
        if (object != null) {
            if (object instanceof Throwable) {
                buf.append('\n').append((Throwable) object).toString();
            }
            // if its anything else, just print it
            else {
                buf.append("Object:   ")
                        .append(object.toString()).append("\n");
            }
        }

        return (buf.toString());
    }

    /**
     * Replace the message stored in exception
     */
    public void setMessage(String msg) {
        message = msg;
    }

    public String getMessage() {
        if (message == null)
            return "";

        return message;
    }

    public String getMessageName() {
        return name;
    }

    public Exception getPriorException() {
        return object;
    }
}
