package com.bandsoftware.data;

/*
 *    Title:         BSDDataObject
 *    Description:   Abstract Super class used to implement a data mapping between XML and VLS
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 *
*/

import java.io.PrintStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


public class BSDDataObject {

    public String businessObjectName;                    // Name of this Business Object
    public String queryObjectName;                      // Used only if Query Object instead of Business Object
    public boolean WhereUsedAware = true;               // only sends messages to where used objects
    public Vector<BSDDataObject> usedBy = null;                        // each object contains a collection of WhereUsedDO objects
    private Hashtable<String, Object> attrList = new Hashtable();       // List of Attributes
    private Vector<BSDDataObject> myChildren = new Vector();           // List of Child BSDDataObjects
    private BSDDataObject myParent = null;              // Link to parent BSDDataObject
    private Hashtable<String, String> keys = new Hashtable();           //primary keys


    public BSDDataObject() {
        super();
        initDefaults();
    }

    public BSDDataObject(String oName) {
        super();
        setName(oName);
        initDefaults();
    }

    public void initDefaults() {
        //implemented in subclass to stuff default values
    }

    public String getName() {
        return businessObjectName;
    }

    public void setName(String oName) {
        businessObjectName = oName;
    }

    public String getBOName() {
        return businessObjectName;
    }

    //Return the Query Object name to use if found, else return the businessObjectName base table name
    public String getQueryName() {
        if (queryObjectName != null)
            return queryObjectName;
        else
            return businessObjectName;
    }

    public void setQueryName(String qryname) {
        queryObjectName = qryname;
    }

    // Add ALl Set/Get methods for this class
    // internal attrbiute name maps to Versata BO attr name.
    public String getAttrValue(String attrName) {
        return (String) attrList.get(attrName);
    }

    public void removeAttrValue(String attrName) {
        attrList.remove(attrName);
    }

    // Return "" instead of Null if not found - VxmlField blows up with new String(null)
    public String getAttrValueBlank(String attrName) {
        String ans = " ";
        if (attrList.get(attrName) != null)
            ans = (String) attrList.get(attrName);
        return ans;
    }

    public void setAttr(String name, String value) {
        if (value != null)
            attrList.put(name, value);
    }

    public void setAttrValue(String name, int value) {
        attrList.put(name, (new Integer(value).toString()));
    }

    public void setAttrValue(String name, boolean value) {
        attrList.put(name, (value) ? "1" : "0");
    }

    public void setAttrValue(String name, String value) {
        if (value != null)
            attrList.put(name, value);
    }

    public Enumeration getAttrList() {
        return attrList.keys();
    }

    // Return a list of children (BSDDataObjects)
    public Enumeration<BSDDataObject> getChildren() {
        return myChildren.elements();
    }

    // Find ALL Child BSDDataObject by Name
    public Enumeration<BSDDataObject> getChildren(String name) {
        Vector v = new Vector();
        Enumeration e = getChildren();
        while (e.hasMoreElements()) {
            BSDDataObject child = (BSDDataObject) e.nextElement();
            if (child.getName().equals(name))
                v.addElement(child);
        }
        return v.elements();
    }

    public BSDDataObject getParent() {
        return myParent;
    }

    public void setParent(BSDDataObject eop) {
        myParent = eop;
    }

    public Hashtable<String, String> getKeys() {
        return keys;
    }

    public Enumeration getKeyAttrs() {
        return keys.keys();
    }

    public void addKey(String attrName) {
        keys.put(attrName, attrName);
        //assumes parent and child have same name
    }

    public void setKeyValue(String parentAttrName, String fkeyAttrName) {
        keys.put(parentAttrName, fkeyAttrName);
    }

    public String getKeyValue(String attrName) {
        return (String) keys.get(attrName);
    }

    //Add an child object
    public void setChild(BSDDataObject edo) {
        myChildren.addElement(edo);
        edo.setParent(this);
    }

    // overloaded from set method
    public void addChild(BSDDataObject obj) {
        this.setChild(obj);
    }

    public void removeChild(BSDDataObject obj) {
        obj.setParent(null);
        myChildren.removeElement(obj);
    }

    //need to remove attributes from attrList
    public void removeAttr(String removeAttrName) {
        Hashtable newList = new Hashtable();
        String attrName;
        Enumeration e = getAttrList();
        while (e.hasMoreElements()) {
            attrName = (String) e.nextElement();
            if (!attrName.equals(removeAttrName))
                newList.put(attrName, getAttrValue(attrName));
        }
        attrList = newList;
    }

    // abstract method implement in subclass
    public void createWhereUsed() {
        //WhereUsedDO wu =  new WhereUsedDO(ReposName, DataObject, Type, UsedBy, Used In);
        // getRepos.getChildren().addElement(wu); // bypass whereused
    }

    //remove trailing blanks afer clone
    public void cloneOutbound(BSDDataObject existingObject) {
        this.clone(existingObject);
        this.trimAttrValues();
    }

    public void clone(BSDDataObject existingObject) {
        //this is the new object - copy out old values
        // TMB - added functionality to link new object to existing
        // double link list so we can navigate between the 2 hierarchies
        // this.addCloneLink(existingObject);
        // existingObject.addCloneLink(this);
        Enumeration e;
        e = existingObject.getAttrList();
        while (e.hasMoreElements()) {
            String attrName = (String) e.nextElement();
            if (existingObject.getAttrValue(attrName) != null) {
                //&& attrList.get(attrName) != null) // may need to do reflection to see if setter(String) exists
                this.setAttrValue(attrName, existingObject.getAttrValue(attrName));
            }
        }
        //this.setParent(existingObject.getParent());
        // don't add children - it should be the callers job
        //keys should be done in constructors
    }


    //	method: findChild
    // 	returns: BSDDataObject or null
    //  param:	String childName
    // 	description: find first child by string name
    // 	Can only be used on a single instance
    public BSDDataObject findChild(String childName) {
//	db("find child ... " + childName);
        //do a depth first recursive search for an object by this name
        Enumeration e = getChildren();
        BSDDataObject child = null;
        while (e.hasMoreElements()) {
            child = (BSDDataObject) e.nextElement();
            if (child.getName().equals(childName)) {
//	db("Child found " + child.getClass().getName());
                return child;
            }
            //return child.findChild(name); // recursive call to this method (depth first)
        }
        return null;
    }

    // Can only be used on multiple instances
    public Enumeration findChildren(String name) {
        //do a depth first recursive search for an object by this name
//	db("Find Children..." + name);
        Vector v = new Vector();
        Enumeration e = getChildren();
        while (e.hasMoreElements()) {
            BSDDataObject o = (BSDDataObject) e.nextElement();
            if (o.getName().equals(name)) {
                v.addElement(o); // this is the child or granchild
//	db("Child found " + o.getClass().getName());
                //Enumeration cv = findChildren(name);
                //while(cv.hasMoreElements()){
                //	BSDDataObject cdo = (BSDDataObject) cv.nextElement();
                //	v.addElement(cdo);
                //}
            }// recursive call to this method (depth first)
        }
        return v.elements();
    }

    /**
     * walk the tree up until find no parent or dataobject
     *
     * @return
     */
    public DataObjectDO getDataObjectRoot() {
        DataObjectDO dataObjectRoot = null;
        if (this instanceof DataObjectDO) {
            return (DataObjectDO) this;
        } else {
            if (myParent != null) {
                return myParent.getDataObjectRoot();
            }
        }
        return dataObjectRoot;
    }

    public void testString() {
        //print attrNames, values and children
        PrintStream anOutputStream = System.out;
        //String businessObjectName; 		// Name of this Business Object
        // Hashtable attrList		// List of Attributes
        // Vector myChildren 		// List of Child BSDDataObjects
        // BSDDataObject myParent// Link to parent BSDDataObject
        // Hashtable keys			//primary keys
        db("*******************************", anOutputStream);
        db("Business Object Name " + businessObjectName, anOutputStream);
        db("Class = " + this.getClass().getName(), anOutputStream);

        if (myParent != null) {
            db("MyParent Class = " + myParent.getClass().getName(), anOutputStream);
            db("Parent name = " + myParent.getName(), anOutputStream);
        } else
            db("My Parent is null", anOutputStream);
        db("Attributes ....", anOutputStream);
        Enumeration e = getAttrList();
        while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            db("Attr " + s + " value = " + getAttrValue(s), anOutputStream);
        }
        db("Keys ....", anOutputStream);
        e = getKeyAttrs();
        while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            db("Key " + s + " value = " + getAttrValue(s), anOutputStream);
        }
        db("Children ....", anOutputStream);
        printAllChildren(anOutputStream);
        db("***********************************", anOutputStream);
        e = getChildren();
        while (e.hasMoreElements()) {
            BSDDataObject child = (BSDDataObject) e.nextElement();
            //db( "Child class = " +  child.getClass().getName());
            //child.testString(); // recurse the tree
        }
    }

    private void printAllChildren(PrintStream anOutputStream) {
        Enumeration e = getChildren();
        String ans = ">>> ";
        while (e.hasMoreElements()) {
            BSDDataObject child = (BSDDataObject) e.nextElement();
            ans = ans + "::" + child.getName();
        }
        db(ans, anOutputStream);
    }

    public void testStringAll() {
        testStringAll(System.out);
/*
      this.testString();
      Enumeration e = getChildren();
      BSDDataObject child;
      while(e.hasMoreElements()){
         child = (BSDDataObject) e.nextElement();
         child.testStringAll();
      }
*/
    }

    public void testStringAll(PrintStream anOutputStream) {
        this.testString();
        Enumeration e = getChildren();
        BSDDataObject child;
        while (e.hasMoreElements()) {
            child = (BSDDataObject) e.nextElement();
            child.testStringAll(anOutputStream);
        }
    }

    // Perhaps this could be used to populate the tree since each object could call there own
    // after query event.
    public void afterQuery() {
    }

    // Need to be able to move key values from parent to child
    public void propagateFkeys() {
        // so we need to ask the parent for key pair values
        // that we need to do our job.
        if (myParent != null) {
            Enumeration e = getKeyAttrs();
            while (e.hasMoreElements()) {
                // This assumes the parent and child have same key name
                String attrKeyName = (String) e.nextElement();
                if (attrKeyName != null)
                    setAttrValue(attrKeyName, myParent.getAttrValue(attrKeyName));
            }
        }
    }

    public void populateWhereUsed(WhereUsedDO wu) {
        //implemented in subclasses
        /*
        wu.setSourceObject(this);
        wu.setSourceName(this.getSomeName);
        wu.setSourceType("DATAOBJECT"); // QUERYOBJECT, RELN
        wu.setUsedInObject(someRefer);
        wu.setUsedInType("SUM"); //COUNT, REPL, RELN, FORM
        */
    }

    // Create a where clause to find objects of this type linked to parent
    public String getWhereClause() {
        String ans = "";
        String join = "";
        if (myParent != null) {
            Enumeration e = getKeyAttrs();
            while (e.hasMoreElements()) {
                // This assumes the parent and child have same key name
                String attrKeyName = (String) e.nextElement();
                ans = ans + join + " " + attrKeyName + " = ";
                ans = ans + getAttrValue(attrKeyName); // myParent.getAttrValue() - would not need to propagate!
                join = " and ";
            }
        }
        return ans;
    }

    // remove trailing spaces
    //NOTE: We do a trim on every non-null value
    //this is a performance hit but the FIXED length coded values
    //cause a problem with XML enumerated types if we don't trim
    public void trimAttrValues() {
        Enumeration e = getAttrList();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            this.setAttrValue(key, (this.getAttrValue(key)).trim());
        }
    }

    // Return date in XML Format yyyy-mm-ddThh:mm:ss-mm:00
    public String getFormattedDate() {
        Date d = new Date();
        String ans = "";
        ans = ans + "T";

        return "2002-06-13T14:27:04-07:00"; // hard coded for now
    }

    public boolean isEmpty() {
        return (attrList.size() > 0) ? false : true;
    }
    // Default event handlers

    /**
     * Default event handler for the BEFORE_INSERT event. Fires before an insert.
     */
    public void beforeInsert() {
        propagateFkeys();
    }

    /**
     * Default event handler for the BEFORE_UPDATE event. Fires before an update.
     */
    public void beforeUpdate() {
        propagateFkeys();
    }

    /**
     * Default event handler for the BEFORE_DELETE event. Fires before a delete.
     */
    public void beforeDelete() {
    }

    /**
     * Default event handler for the AFTER_INSERT event. Fires after an insert.
     */
    public void afterInsert() {
    }

    /**
     * Default event handler for the AFTER_UPDATE event. Fires after an update.
     */
    public void afterUpdate() {
    }

    public void beforeQuery() {
    }

    //** Remove all references to this object, attribute, parent and child
    //** recursive call from Root all the way down through the children (depth first)
    public void close() {
        attrList = null;                // List of Attributes
        myParent = null;                // Link to parent BSDDataObject
        keys = null;                    //primary keys
        Enumeration e = myChildren.elements();
        while (e.hasMoreElements()) {
            BSDDataObject child = (BSDDataObject) e.nextElement();
            child.close();
        }
        myChildren = null;
    }

    // debug wrapper
    public void db(String msg) {

        db(msg, System.out);
    }

    public void db(String msg, PrintStream anOutputStream) {
        anOutputStream.println(msg);
    }

    public void recursiveReposVersion(String repositoryVersion) {
        //walk the tree and set all chindren to this versioni
        Enumeration<BSDDataObject> myChildren = this.getChildren();
        BSDDataObject thisChildObject;
        BSDDataObject child;
        while (myChildren.hasMoreElements()) {
            thisChildObject = (BSDDataObject) myChildren.nextElement();
            db(thisChildObject.getName() + " BO NAME: " + thisChildObject.getBOName());
            thisChildObject.setAttrValue("RepositoryVersion", repositoryVersion);
            // walk the tree down for each object and call this method again
            Enumeration<BSDDataObject> children = thisChildObject.getChildren();
            while (children.hasMoreElements()) {
                child = (BSDDataObject) children.nextElement();
                child.setAttrValue("RepositoryVersion", repositoryVersion);
                db(child.getName() + " BO NAME: " + child.getBOName());
                child.recursiveReposVersion(repositoryVersion);
            }
        }
    }
}