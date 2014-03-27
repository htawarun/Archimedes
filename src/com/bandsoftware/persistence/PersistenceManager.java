/*
 *    Title:         PersistenceManager
 *    Description:   Main Class used to implement an interface betwwen the Data Objects (BSDDataObject)
 *                  and Versata Transaction Logic Engine (TLE)
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 *
*/

package com.bandsoftware.persistence;

import com.bandsoftware.data.*;
import com.bandsoftware.exception.BSDException;
import versata.common.*;
import versata.vfc.VSData;
import versata.vfc.VSResultSet;
import versata.vfc.VSRow;

import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

public class PersistenceManager {

    private String _eventCode = "";                // event code (internal)
    private VSSession _session;                    // passed in and used to create the VxmlProcessor
    public VLSPersistence _vls;                    //used to process in and outbound with VLS
    private Hashtable errorStack;               // used to hold all error messages
    public static final String _classpath = "com.versata.automationanalyzer";
    /**
     * *********************************************
     * NOTE:  statics are not thread-safe
     * These need to be addressed for thread-safety
     * when we increase to more than one thread.
     */
    private static final String apostrophe = "'";
    RepositoryDO _rootDO;

    //*************************************
    // Each event must implement a subclass of this class see EOPEvent102
    // PersistenceManager eop = new PersistenceManager(); // or pass (NGSSchemaRoot, Session)
    // eop.setRootDO(RepositoryRootDO);
    // eop.StartInbound(); 		    //to VLS
    //		or
    // eop.StartOutbound(); 		// from VLS
    //************************************
    public PersistenceManager() {
        super();
    }

    public PersistenceManager(RepositoryDO root) {
        super();
        setRootDO(root);
    }

    //*******************************************
    // This is the preferred form - pass in the root
    // and have the Root Data Object lookup and
    // return the event code (or have it set before
    // calling the EOPEventHandler)
    //*******************************************
    public PersistenceManager(RepositoryDO root, Object sess) {
        super();
        setRootDO(root);
        setSession(sess);
    }

    // this constructor is used by the test harness only
    public PersistenceManager(String event, RepositoryDO root, Object sess) {
        super();
        setSession(sess);
        setRootDO(root);
    }

    public void setSession(Object sess) {
        if (sess == null)
            _session = (VSSession) getNewSession();
        else
            _session = (VSSession) sess;
        //_session.setProperty("Time_Zone","CST"); // required per Ron Thomsons
    }

    // This is indirected because the way in which we instantiate the
    // subclass by passing the _vlsProcess (which has this session)
    public VSSession getSession() {
        return _vls.getSession();
    }

    //this is the BSDDataObject ROOT that has the ability to return any named
    // child DataObject.  Used by VXML processor to insert/query
    public void setRootDataObject(RepositoryDO edo) {
        _rootDO = edo;
    }

    public RepositoryDO getRootDO() {
        return _rootDO;
    }

    public void setRootDO(RepositoryDO ado) {
        _rootDO = ado;
    }

    public VLSPersistence getProcessor() {
        return _vls;
    }

    public void setProcessor(VLSPersistence vxml) {
        _vls = vxml;
    }

    //The EOPEvent class will translate the event code into a specific
    //class name that will get the runInbound/runOutbound message.  This will
    //take the VXML processor and use it to insert, update, delete, query, etc the VLS interface
    //Any errors will cause the process to halt and throw and exception
    //*** Main entry point to process transactions from the VLS
    public void StartInbound() {
        try {
            if (_session == null)
                _session = (VSSession) getNewSession();
            _vls = new VLSPersistence(_session);
            persistAll();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new VSException(ex);
        } finally {
            close();
        }
    }

    //*** Main entry point to process transactions from the VLS
    public void StartOutbound() {
        try {
            if (_session == null)
                _session = (VSSession) getNewSession();

            _vls = new VLSPersistence(_session);
            //retrieveAll();
        } catch (Exception ex) {
            throw new VSException(ex);
        } finally {
            close();
        }
    }


    // Abstract method - implement in subclass
    public void BeginInboundEvent() {
        //implement in subclass for each event class
        db("Subclass Must Implement::Begin INBOUND Event " + this.getClass().getName());
    }

    // Abstract method - implement in subclass
    public void BeginOutboundEvent() {
        //implement in subclass for each event class
        db("Subclass Must Implement::Begin OUTBOUND Event " + this.getClass().getName());
    }
    // not used ???
    //public void insert(VxmlProcessor vxmlp){
    //	_vls = vxmlp;
    //	log("Insert called on this ->"+ this.getClass().getName());
    //}

    //Convert an BSDDataObject into a Hashtable of BSDFields
    //Modified to skip pkey
    //============================================================================
    public void persistAll() {
        persistRepos();
    }

    //============================================================================
    private void persistRepos() {
        if (_rootDO != null) {
            insert(_rootDO);
            Enumeration e = _rootDO.findChildren("DataObject");
            DataObjectDO dataObj;
            while (e.hasMoreElements()) {
                dataObj = (DataObjectDO) e.nextElement();
                insert(dataObj);
                persistMethods(dataObj);
                persistIndexes(dataObj);
                persistAttrs(dataObj);
                persistActions(dataObj);
                persistConstraints(dataObj);
                persistRelns(dataObj);
            }
            //need to link relns after we have inserted ALL parent relns (need both sides)
            e = _rootDO.findChildren("DataObject");
            while (e.hasMoreElements()) {
                dataObj = (DataObjectDO) e.nextElement();
                linkRelns(dataObj);
            }
            persistGroups(_rootDO);
            persistQueryObjects(_rootDO);
            persistApplications(_rootDO);
            persistOtherFiles(_rootDO);
        }
    }

    private void persistMethods(DataObjectDO dataObj) {
        MethodDO meth;
        Enumeration e = dataObj.findChildren("Method");
        while (e.hasMoreElements()) {
            meth = (MethodDO) e.nextElement();
            insert(meth);
        }
    }

    private void persistAttrs(DataObjectDO dataObj) {
        AttributeDO attr;
        Enumeration e = dataObj.findChildren("Attribute");
        while (e.hasMoreElements()) {
            attr = (AttributeDO) e.nextElement();
            insert(attr);
            persistValidation(attr);
            persistDerivation(attr);
        }
    }

    private void persistValidation(AttributeDO attr) {
        ValidationDO val;
        Enumeration e = attr.getValidations().elements();
        if (e.hasMoreElements()) {
            val = (ValidationDO) e.nextElement();
            if (val.getValidationType() != null) {
                //val.testString();
                insert(val);
            }
        }
    }

    private void persistDerivation(AttributeDO attr) {
        DerivationDO derv;
        Enumeration e = attr.getDerivations().elements();
        if (e.hasMoreElements()) {
            derv = (DerivationDO) e.nextElement();
            //derv.testString();
            try {
                insert(derv);
            } catch (Exception ex) {
                ex.printStackTrace(); // try catch and keep running
            }
        }
    }

    private void persistActions(DataObjectDO dataObj) {
        ActionDO act;
        Enumeration e = dataObj.findChildren("Action");
        while (e.hasMoreElements()) {
            act = (ActionDO) e.nextElement();
            if (act.getActionToPerform() != null) {
                //act.testString();
                insert(act);
            }
        }
    }

    private void persistIndexes(DataObjectDO dataObj) {
        DO_Index idx;
        Enumeration e = dataObj.findChildren("IndexDO");
        while (e.hasMoreElements()) {
            idx = (DO_Index) e.nextElement();
            insert(idx);
            persistIndexAttrs(idx);
        }

    }

    private void persistIndexAttrs(DO_Index idx) {
        DO_IndexAttr attr;
        Enumeration e = idx.findChildren("IndexAttr");
        while (e.hasMoreElements()) {
            attr = (DO_IndexAttr) e.nextElement();
            insert(attr);
        }

    }

    private void persistConstraints(DataObjectDO dataObj) {
        ConstraintDO con;
        Enumeration e = dataObj.findChildren("Constraint");
        Hashtable constraintHT = new Hashtable();
        while (e.hasMoreElements()) {
            con = (ConstraintDO) e.nextElement();
            //con.testString();
            // duplicate constraint names in gen code
            if (constraintHT.get(con.getConstraintName()) == null) {
                insert(con);
                constraintHT.put(con.getConstraintName(), con);
            }
        }
        constraintHT = null;
    }

    private void persistRelns(DataObjectDO dataObj) {
        RelationshipDO reln;
        Enumeration e = dataObj.getParentReln().elements();
        while (e.hasMoreElements()) {
            reln = (RelationshipDO) e.nextElement();
            insert(reln);
        }
    }

    private void linkRelns(DataObjectDO dataObj) {
        // link child relationships to this data object
        RelationshipDO reln;
        Enumeration e = dataObj.getParentReln().elements();
        while (e.hasMoreElements()) {
            reln = (RelationshipDO) e.nextElement();
            insertLinkTable(dataObj, reln, true);
        }
        e = dataObj.getChildReln().elements();
        while (e.hasMoreElements()) {
            reln = (RelationshipDO) e.nextElement();
            insertLinkTable(dataObj, reln, false);
        }
    }

    private void insertLinkTable(DataObjectDO dataObj, RelationshipDO reln, boolean isParentRole) {
        // insert child/parent relationship link table
        String doName;
        BusinessObjectDO bo = null;
        try {
            doName = isParentRole == true ? reln.getParentDOName() : reln.getChildDOName();
            bo = new BusinessObjectDO("DataObject_Jn_Reln");
            bo.setAttrValue("RepositoryName", reln.getRepositoryName());
            bo.setAttrValue("DataObjectName", doName);
            bo.setAttrValue("RelationshipName", reln.getRelationshipName());
            bo.setAttrValue("IsParentRole", isParentRole == true ? "true" : "false");
            insert(bo);
        } catch (Exception ex) {
            reln.testString();
            if (bo != null)
                bo.testString();
            ex.printStackTrace();
        }
    }

    private void persistQueryObjects(BSDDataObject root) {
        QueryObjectDO qryDO;
        Enumeration e = root.findChildren("QueryObject");
        while (e.hasMoreElements()) {
            qryDO = (QueryObjectDO) e.nextElement();
            insert(qryDO);
            persistQueryDataObjects(qryDO);
            persistQueryJoins(qryDO);
        }
    }

    private void persistQueryDataObjects(QueryObjectDO qryDO) {
        QueryDataObjectDO qryDataDO;
        Enumeration e = qryDO.findChildren("QueryDataObject");
        while (e.hasMoreElements()) {
            qryDataDO = (QueryDataObjectDO) e.nextElement();
            try {
                insert(qryDataDO);
            } catch (Exception ex) {
                qryDataDO.setDataObjectName(qryDataDO.getDataObjectName() + "_1");
                insert(qryDataDO);
            }
        }
    }

    private void persistQueryJoins(QueryObjectDO qryDO) {
        QueryJoinDataObjectDO qryJNDO;
        Enumeration e = qryDO.findChildren("QueryJoinDataObject");
        while (e.hasMoreElements()) {
            qryJNDO = (QueryJoinDataObjectDO) e.nextElement();
            insert(qryJNDO);
        }
    }

    private void persistApplications(BSDDataObject root) {
        ApplicationDO app;
        Enumeration e = root.findChildren("Application");
        while (e.hasMoreElements()) {
            app = (ApplicationDO) e.nextElement();
            try {
                insert(app);
                persistForms(app);
            } catch (Exception ex) {
                ex.printStackTrace(); //skip this app
            }
        }
    }

    private void persistGroups(BSDDataObject root) {
        GroupDO grp;
        Enumeration e = root.findChildren("Groups");
        while (e.hasMoreElements()) {
            grp = (GroupDO) e.nextElement();
            try {
                insert(grp);
            } catch (Exception ex) {
                grp.testString();
                // ex.printStackTrace(); //skip this if duplicate
            }
        }
        persistGroupJnObject(root);
    }

    private void persistGroupJnObject(BSDDataObject root) {
        GroupDO group;
        Object_JN_Group ojg;
        Enumeration e = root.findChildren("Groups");
        while (e.hasMoreElements()) {
            group = (GroupDO) e.nextElement();
            Enumeration ee = group.findChildren("Object_JN_Group");
            while (ee.hasMoreElements()) {
                ojg = (Object_JN_Group) ee.nextElement();
                try {
                    insert(ojg);
                } catch (Exception ex) {
                    ojg.testString();
                    //ex.printStackTrace(); //skip this app
                }
            }
        }
    }

    private void persistForms(ApplicationDO app) {
        FormDO form;
        Enumeration e = app.findChildren("Form");
        while (e.hasMoreElements()) {
            form = (FormDO) e.nextElement();
            insert(form);
            persistRecordSource(form);
        }
    }

    // Multiple record sources can exist on same page
    // try to munge the name and repeat the isnert on failure
    private void persistRecordSource(FormDO form) {
        RecordSourceDO rs;
        Enumeration e = form.findChildren("RecordSource");
        while (e.hasMoreElements()) {
            rs = (RecordSourceDO) e.nextElement();
            try {
                if (tryInsert(rs)) {
                    rs.setBusinessObject(rs.getBusinessObject() + "_1");
                    if (tryInsert(rs)) {
                        rs.setBusinessObject(rs.getBusinessObject() + "_2");
                        insert(rs);
                    }
                }
            } catch (Exception ex) {
                //if we have a duplicate name - let's try to alter tha name and try again

            }
            persistRecordSourceAttr(rs);
        }
    }

    private void persistRecordSourceAttr(RecordSourceDO rs) {
        RSAttributesDO rsa;
        Enumeration e = rs.findChildren("RSAttribute");
        while (e.hasMoreElements()) {
            rsa = (RSAttributesDO) e.nextElement();
            rsa.setAttrValue("BusinessObject", rs.getAttrValue("BusinessObject")); //propage new name
            insert(rsa);
        }
    }

    public void persistOtherFiles(BSDDataObject root) {
        OtherFileDO of;
        Enumeration e = root.findChildren("OtherFile");
        while (e.hasMoreElements()) {
            of = (OtherFileDO) e.nextElement();
            try {
                insert(of);
            } catch (Exception ex) {
                ex.printStackTrace(); //skip this file
            }
        }
    }

    //========================================================================================
    public Hashtable getHashtable(BSDDataObject edo) {
        Hashtable ht = new Hashtable();
        Enumeration e = edo.getAttrList(); // list of named attributes
        Enumeration keys = getKeyAttrs(edo);
        while (e.hasMoreElements()) {
            String attrName = (String) e.nextElement();
            BSDField f = new BSDField(attrName, edo.getAttrValue(attrName));
            if (!containsKey(keys, attrName))
                ht.put(attrName, f);
        }
        return ht;
    }

    // need to get the PROTECTED attributes from the VSMetaColumn
    private Enumeration getKeyAttrs(BSDDataObject obj) {
        String bo_name = obj.getName();
        //this code below is only used for debug/print purposes
        //Enumeration e = getProcessor().getProtectedAttrs(bo_name);
        //while(e.hasMoreElements()){
        //	String str = (String) e.nextElement();
        //	db("Protected ATTR NAME = " + str);
        //}
        db("VLSPersistence Instance " + getProcessor());
        return getProcessor().getProtectedAttrs(bo_name);
    }

    // does this enumeration of keys contain this attribute name
    private boolean containsKey(Enumeration e, String attrName) {
        String key;
        if (e != null) {
            while (e.hasMoreElements()) {
                key = (String) e.nextElement();
                if (key.equals(attrName))
                    return true;
            }
        }
        return false;
    }

    // After an insert - we get the new values so let's update the attribute
    // cool thing here is that we have ALL the attributes after insert (not just the modeled attributes)
    // can now use getAttrList and getAttrName(aName)
    public void setHashtable(Hashtable ht, BSDDataObject edo) {
        Enumeration e = ht.elements();
        while (e.hasMoreElements()) {
            BSDField f = (BSDField) e.nextElement();
            edo.setAttr(f.getName(), f.getValue());
        }
    }

    public void setAttrs(BSDDataObject edo, VSRow row) {
        VSMetaColumn Mc = null;
        VSQueryColumnDefinition qc = null;
        VSMetaQuery Mq = getSession().getMetaQuery(edo.getName());
        Enumeration e = Mq.getQueryColumns();
        while (e.hasMoreElements()) {
            qc = (VSQueryColumnDefinition) e.nextElement();
            String attrName = (String) qc.getMetaColumn().getName();
            VSData d = row.getData(attrName);
            edo.setAttrValue(attrName, d.getString());
        }
    }

    public void insert(BSDDataObject edo) {
        if (edo == null) {
            return;
        }
        try {
            raiseBeforeInsert(edo);
            Hashtable ht = getProcessor().insertData(edo.getQueryName(), getHashtable(edo));
            // setHashtable(ht, edo); // after insert we get back pkeys and calculated values!
            raiseAfterInsert(edo);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BSDException("inser Object :" + edo.BO_Name + " failed", ex);
        }
    }

    // Utility function to let caller know if insert succeeded or failed
    public boolean tryInsert(BSDDataObject obj) {
        try {
            insert(obj);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    // update all children by doing multiple inserts
    public void insertAll(Enumeration e) {
        BSDDataObject edo = null;
        while (e.hasMoreElements()) {
            edo = (BSDDataObject) e.nextElement();
            insert(edo);
        }
    }

    public void update(BSDDataObject edo) {
        if (edo == null) return;
        try {
            raiseBeforeUpdate(edo);
            Hashtable ht = getHashtable(edo);
            ht = getProcessor().insertUpdateData(edo.getQueryName(), ht, getKeys(edo));
            setHashtable(ht, edo); // after update we get back calculated values!
            raiseAfterUpdate(edo);
        } catch (Exception ex) {
            throw new BSDException("updateObjec:" + edo.getName() + " failed", ex);
        }
    }

    public void updateAll(Enumeration e) {
        BSDDataObject edo = null;
        while (e.hasMoreElements()) {
            edo = (BSDDataObject) e.nextElement();
            update(edo);
        }
    }

    public void query(BSDDataObject edo, String where) {
        if (edo == null) return;
        VSRow row = null;

        try {
            Hashtable ht = getHashtable(edo);
            //VSResultSet rs = getProcessor().QueryBusinessObject(edo.getName(),where);
            row = getProcessor().getBusinessObjectRow(edo.getQueryName(), where);
            //if(rs != null){
            //	VSRow row = rs.next();
            if (row != null) { // should be a loop -
                setAttrs(edo, row);
            }
        } catch (Exception ex) {
            throw new BSDException("Query Object:" + edo.getName() + " failed", ex);
        } finally {
            if (row != null) {
                VSResultSet rs = row.getResultSet();
                if (rs != null)
                    rs.getQuery().close();
            }
        }

    }

    //this does not have any objects so we pass the a placeholder object
    // and then allow the system to build clones using the class loader.
    // this should hook to this objects parent so be sure to create the linked clone and then remove
    public void queryBuild(BSDDataObject parentObject, String query_name, String bo_name, String where) {
        VSResultSet rs = null;
        BSDDataObject newEOP = null;
        try {
            rs = getProcessor().QueryBusinessObject(query_name, where);
            VSRow row = rs.first();
            while (row != null) {
                String className = _classpath + bo_name;
                Class c = Class.forName(className);
                newEOP = (BSDDataObject) c.newInstance();
                parentObject.addChild(newEOP);
                setAttrs(newEOP, row);
                row = rs.next();
            }
        } catch (Exception ex) {
            throw new BSDException("QueryBuild failed on BO Name " + newEOP.getName(), ex);
        } finally {
            if (rs != null)
                rs.getQuery().close();
        }
    }

    private Hashtable getKeys(BSDDataObject edo) {
        Hashtable ht = new Hashtable();
        Enumeration keys = edo.getKeyAttrs();
        while (keys.hasMoreElements()) {
            String attrName = (String) keys.nextElement();
            BSDField f = new BSDField(attrName, edo.getAttrValue(attrName));
            ht.put(attrName, f);
        }
        return ht;
    }

    public String formatDate(String date) {
        if (date != null) {
            VSDate d = new VSDate(date + " 00:00:00");
            return getFormattedDate(d);
        } else
            return getFormattedDate();
    }

    public String getFormattedDate() {
        VSDate d = new VSDate();
        return getFormattedDate(d);
    }

    //tmb replace apostrophe on selected search
    // strings causes SQL to blow up
    public String replaceApostrophe(String anArg) {
        //apostrophe is private final static variable
        //this handles the case of lester's lender's ( muliple's on single's)
        //using self recursive call
        if (anArg != null) {
            //int indx = anArg.indexOf(apostrophe); // find it and double it up
            //if(indx >0) {
            //	String newString = anArg.substring(indx); // check the remainder
            //	return anArg.substring(0,indx) + apostrophe + replaceApostrophe(newString);
            //}
            int i = anArg.indexOf(apostrophe);
            if (i == -1)
                return anArg;
            StringBuffer sb = new StringBuffer();
            int oldi = 0;
            while (i != -1) {
                sb.append(anArg.substring(oldi, i));
                sb.append(apostrophe); // double it
                oldi = i + 1;
                i = anArg.indexOf(apostrophe, i + 1);
                sb.append(apostrophe); // double it
            }
            if (oldi < anArg.length())
                sb.append(anArg.substring(oldi));
            return sb.toString();
        }
        return anArg;
    }

    // Return date in XML Format yyyy-mm-ddThh:mm:ss-mm:offset to GMT
    public String getFormattedDate(VSDate d) {
        String month;
        String day;
        String ans = "" + (d.getYear() + 1900); ///base 1900
        int mon = d.getMonth() + 1;
        if (mon < 10)
            month = "0" + mon;
        else
            month = "" + mon;
        ans = ans + "-" + month; // base 0
        int dd = d.getDate();
        if (dd < 10)
            day = "0" + dd;
        else
            day = "" + dd;
        ans = ans + "-" + day; // who wrote this VSDate???
        int hrs = d.getHours();
        String hours;
        if (hrs < 10)
            hours = "0" + hrs;
        else
            hours = "" + hrs;
        String minutes;
        int min = d.getMinutes();
        if (min < 10)
            minutes = "0" + min;
        else
            minutes = "" + min;
        ans = ans + "T" + hours + ":" + minutes + ":00-07:00";  //+ d.getHours()+ ":" + d.getMinutes() + ":"+ d.getSeconds() + "-07:00"; // should I now add + 7 to the time???

        return ans; //"2002-06-13T14:27:04-07:00"
    }

    public void BeginInboundTest() {
        // do transform
    }

    public void raiseAfterQuery(BSDDataObject eop) {
        eop.beforeQuery();
    }

    public void raiseBeforeQuery(BSDDataObject eop) {
        eop.afterQuery();
    }

    public void raiseAfterInsert(BSDDataObject eop) {
        eop.afterInsert();
    }

    public void raiseBeforeInsert(BSDDataObject eop) {
        eop.beforeInsert();
    }

    public void raiseAfterUpdate(BSDDataObject eop) {
        eop.afterUpdate();
    }

    public void raiseBeforeUpdate(BSDDataObject eop) {
        eop.beforeUpdate();
    }

    public void beginTransaction() {
        db("Begin Transaction....");
        getSession().beginTrans();
    }

    public void commitTransaction() {
        db("Start Commit....");
        getSession().commit();
    }

    public void rollbackTransaction() {
        db("Start Rollback");
        getSession().rollback();
    }

    //** Clean up after running code
    public void close() {
        _eventCode = null;                // event code (internal)
        if (_session != null) {
            _session.close();
        }
        _session = null;                // passed in and used to create the VxmlProcessor
        //_rootDO.close(); // do not close here - this may be needed by the caller to process additional stuff like errors and acks
        //  if(_rootDO ! =null)
        //     _rootDO.close();
        _rootDO = null;                // link to alldata parsed into and out of VLS - only one instance of this for the entire system
        _vls = null;                //used to process in and outbound with VLS

    }

    private Object getNewSession() {

        Properties Props = new Properties();
        try {
            Properties aaProp = new Properties();
            FileInputStream fis = new FileInputStream("Archimedes.properties");
            aaProp.load(fis);
            fis.close();
            db("Connection to server with these properties...");
            db("Server " + aaProp.get("ARCHIMEDES_SERVER"));
            db("Package " + aaProp.get("ARCHIMEDES_PACKAGE"));
            db("Application " + aaProp.get("ARCHIMEDES_APPLICATION"));
            db("User " + aaProp.get("ARCHIMEDES_USER"));

            // Create the connection pool

            VSSession _session = null;
            String server = (String) aaProp.get("ARCHIMEDES_SERVER"); // "<localVLSServer>");
            String pkg = (String) aaProp.get("ARCHIMEDES_PACKAGE"); // "MetaRepos");
            String app = (String) aaProp.get("ARCHIMEDES_APPLICATION"); //"Project1");
            String user = (String) aaProp.get("ARCHIMEDES_USER"); // "sa");
            String pw = (String) aaProp.get("ARCHIMEDES_PW"); // "eop");
            VSSession s = new VSSession(
                    server,
                    user,
                    pw,
                    new AppEnvironment(pkg,
                            app,
                            null, // applet ? - no
                            true, // application ? - yes
                            false));  // browser ? - no

            // Try to connect to the VLS
            if (!s.connect()) {
                // Unsuccessful!
                s = null;
            }
            _session = s;
            return s;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void db(String msg) {
        System.out.println(msg);
    }
}

