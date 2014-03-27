package com.bandsoftware.persistence;


/*
 *    Title:         BSDLogger
 *    Description:   General Interface to handle message logging
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 *
*/
public class BSDLogger {
    public static long TYPE_ERROR = 1;
    public static long TYPE_INFO = 2;

    /**
     * @return True when the minimum priority is at least on info level
     */
    public static boolean isInfo() {
        return true;
    }

    /**
     * @return True when the minimum priority is at least on warning level
     */
    public static boolean isWarning() {
        return true;
    }

    /**
     * @return True when the minimum priority is at least on error level
     */
    public static boolean isError() {
        return true;
    }

    /**
     * @return True when the minimum priority is at least on exception level
     */
    public static boolean isException() {
        return true;
    }

    /**
     * Creates an <code>BSDLogger</code> object while logging the <code>priority</code>,
     * <code>className</code>, <code>methodName</code> and <code>message</code>.
     *
     * @param priority   the priority level of the message (NONE, INFO, WARNING, ERROR, FATAL).
     * @param className  the class name of the calling procedure for tracking purposes.
     * @param methodName the method name of the calling procedure for tracking purposes.
     * @param message    the message to be logged.
     */
    public static void log(long priority, Object className, String methodName, String message) {
        db(priority, className, methodName, null, message);
    }

    /**
     * Creates an <code>BSDLogger</code> object while logging the <code>priority</code>,
     * <code>className</code>, <code>methodName</code> and <code>exception</code>.
     *
     * @param priority   the priority level of the message (NONE, INFO, WARNING, ERROR, FATAL).
     * @param className  the class name of the calling procedure for tracking purposes.
     * @param methodName the method name of the calling procedure for tracking purposes.
     * @param exception  the exception to log.
     */
    public static void log(long priority, Object className, String methodName, Exception exception) {
        db(priority, className, methodName, exception, null);
    }

    /**
     * Creates an <code>BSDLogger</code> object while logging the <code>priority</code>,
     * <code>className</code>, <code>methodName</code>, <code>exception</code> and <code>message</code>.
     *
     * @param priority   the priority level of the message (NONE, INFO, WARNING, ERROR, FATAL).
     * @param className  the class name of the calling procedure for tracking purposes.
     * @param methodName the method name of the calling procedure for tracking purposes.
     * @param exception  the exception to log.
     * @param message    the message to be logged.
     */
    public static void log(long priority, Object className, String methodName, Exception exception, String message) {
        db(priority, className, methodName, exception, message);
    }

    /**
     * Creates an <code>BSDLogger</code> object, logging the <code>exception</code>,
     * while automatically deriving the <code>className</code> and <code>methodName</code>.
     *
     * @param exception the exception to be logged.
     */
    public static void log(Exception exception) {
        db(0, null, null, exception, null);
    }

    private static void db(long priority, Object className, String methodName, Exception ex, String message) {

        System.out.println("Priority = " + priority);
        System.out.println("ClassName = " + className.getClass());
        System.out.println("MethodName = " + methodName);
        System.out.println("Message = " + message);
        System.out.println("Exception ");
        if (ex != null)
            ex.printStackTrace();

    }

}
