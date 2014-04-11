package com.bandsoftware.persistence;


/*
 *    Title:         VxmlField
 *    Description:   Data holder for a field name/value pair. Additionally it
 *                   can also hold the name of the parent field that will hold
 *                   the value in case of nested inserts.
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 */


public class BSDField {
    private String Name = null;
    private Object Value = null;
    private String ParentName = null;

    public BSDField() {
    }

    /**
     * Set the name and value of this field
     *
     * @param Name  The name of this field
     * @param Value The value of this field
     */
    public BSDField(String Name, Object Value) {
        this.Name = Name;
        this.Value = Value;
    }

    /**
     * Set the name and value of this field
     *
     * @param Name       The name of this field
     * @param Value      The value of this field
     * @param ParentName The name of the parent field to be used to retrieve the actual value
     */
    public BSDField(String Name, Object Value, String ParentName) {
        this.Name = Name;
        this.Value = Value;
        this.ParentName = ParentName;
    }

    /**
     * Sets a new name for this field
     *
     * @param Name The new name for this field
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * Sets a new value for this field
     *
     * @param Value The new value for this field
     */
    public void setValue(Object Value) {
        this.Value = Value;
    }

    /**
     * Sets a new parent name for this field
     *
     * @param ParentName The new parent name for this field
     */
    public void setParentName(String ParentName) {
        this.ParentName = ParentName;
    }

    /**
     * @return The name of this field
     */
    public String getName() {
        return (Name);
    }

    /**
     * @return The value of this field
     */
    public String getValue() {
        String v = Value.toString();
        if (Value == null && ParentName != null) {
            return ("[" + ParentName + "]");
        }
        if(Value instanceof Boolean){
            v = (((Boolean) Value).booleanValue())?"1":"0";
        }
        return v ;
    }

    /**
     * @return The parent name of this field
     */
    public String getParentName() {
        return (ParentName);
    }

    /**
     * Convert this field to something printable, used for debugging mainly
     *
     * @return A human readable version of this field
     */
    public String toString() {
        return ((Value == null) ? ("[" + ParentName + "]") : getValue());
    }
}
