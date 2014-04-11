package com.bandsoftware.persistence;

 /*
 *    Title:         PersistenceManager
 *    Description:   Main Class used to implement an interface betwwen the Data Objects (BSDDataObject)
 *                   and a persistence model like SQL, VLS (see plugins), or Espresso Logic REST
 *    Copyright:     Copyright (c) 2003-2014
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.1
 *
*/

import com.bandsoftware.data.*;
import com.bandsoftware.exception.BSDException;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;


/**
 * Created by Tyler on 3/31/14.
 */
public class RESTPersistenceManager extends RESTServicesManager {


    private String _eventCode = "";                 // event code (internal)
    private Hashtable errorStack;                   // used to hold all error messages
    public static final String _classpath = "com.versata.automationanalyzer";
    private static Logger log = Logger.getLogger(RESTPersistenceManager.class.getName());
    /**
     * *********************************************
     * NOTE:  statics are not thread-safe
     * These need to be addressed for thread-safety
     * when we increase to more than one thread.
     */
    private static final String apostrophe = "'";
    RepositoryDO repositoryDO;
    private String repositoryVersion = "1";

    EspressoRuleObjectImpl ruleObject;
    public Vector<EspressoRuleBean> allEsprssoRules = new Vector<EspressoRuleBean>();

    public static boolean testModeBypassInsert = false;

    //*************************************
    // Each event must implement a subclass of this class see EOPEvent102
    // VersataPersistenceManager eop = new VersataPersistenceManager(); // or pass (NGSSchemaRoot, Session)
    // eop.setRootDO(RepositoryRootDO);
    // eop.StartInbound(); 		    //to VLS
    //		or
    // eop.StartOutbound(); 		// from VLS
    //************************************
    public RESTPersistenceManager() {
        super();
    }

    public RESTPersistenceManager(RepositoryDO rootDataObject) {
        super();
        setRootDO(rootDataObject);
    }

    //this is the BSDDataObject ROOT that has the ability to return any named
    // child DataObject.  Used by VXML processor to insert/query
    public void setRootDataObject(RepositoryDO rootRepositoryDataObject) {
        this.repositoryDO = rootRepositoryDataObject;
    }

    public RepositoryDO getRootRepositoryDataObject() {
        return this.repositoryDO;
    }

    public void setRootDO(RepositoryDO rootRepositoryDataObject) {
        this.repositoryDO = rootRepositoryDataObject;
    }

    public void setRepositoryVersion(String version){
        this.repositoryVersion = version;
    }
    //The EOPEvent class will translate the event code into a specific
    //class name that will get the runInbound/runOutbound message.  This will
    //take the VXML processor and use it to insert, update, delete, query, etc the VLS interface
    //Any errors will cause the process to halt and throw and exception
    //*** Main entry point to process transactions from the VLS
    public void StartInbound() throws Exception {
        try {
            persistAll();
        } catch (Exception ex) {
            log.error(ex.getMessage(),ex);
            throw new Exception(ex);
        } finally {
            close();
        }
    }

    //*** Main entry point to process transactions from the VLS
    public void StartOutbound() throws Exception {
        try {
            //retrieveAll();
        } catch (Exception ex) {
            throw new Exception(ex);
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

    //Convert an BSDDataObject into a Hashtable of BSDFields
    //Modified to skip pkey
    /*============================================================================*/
    public void persistAll() throws Exception {
        persistRepos();
      //  persistRules();
    }

    private void persistRules() {
        for(EspressoRuleBean bean : allEsprssoRules){
            try {
                if(bean.getIdent() != null){
                    insertRule(bean);
                    break; //only do 1 for testing
                }
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        }
    }

    /* ============================================================================ */
    private void persistRepos() throws Exception {
        if (this.repositoryDO != null) {
            insertRoot(this.repositoryDO);
            persistAllDataObjects();
            persistAllRelationships();
            persistGroups(this.repositoryDO);
            persistQueryObjects(this.repositoryDO);
            persistQueryJoins(this.repositoryDO);
            persistApplications(this.repositoryDO);
            persistOtherFiles(this.repositoryDO);
        } //else throw exception
    }

    private void persistAllRelationships() {
        Enumeration e;
        DataObjectDO dataObj;
        //need to link relns after we have inserted ALL parent relns (need both sides)
        e = this.repositoryDO.findChildren("DataObject");
        while (e.hasMoreElements()) {
            dataObj = (DataObjectDO) e.nextElement();
            persistRelns(dataObj);
            linkRelns(dataObj);
        }
    }

    private void persistAllDataObjects() throws Exception {
        Enumeration e = this.repositoryDO.findChildren("DataObject");
        DataObjectDO dataObj;
        while (e.hasMoreElements()) {
            dataObj = (DataObjectDO) e.nextElement();
            insert(dataObj);
            persistAttrs(dataObj);
            persistMethods(dataObj);
            persistIndexes(dataObj);
            persistActions(dataObj);
            persistConstraints(dataObj);
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
        AttributeDO attributeDO;
        Enumeration e = dataObj.findChildren("Attribute");
        while (e.hasMoreElements()) {
            attributeDO = (AttributeDO) e.nextElement();
            insert(attributeDO);
            persistValidation(attributeDO);
            persistDerivation(attributeDO);
        }
    }

    private void persistValidation(AttributeDO attr) {
        ValidationDO validationDO;
        Enumeration e = attr.getValidations().elements();
        if (e.hasMoreElements()) {
            validationDO = (ValidationDO) e.nextElement();
            if (validationDO.getValidationType() != null) {
                //val.testString();
                insert(validationDO);
                if(!"CodedValuesList".equals(validationDO.getValidationType())){
                    ruleObject = createNewRuleObject(validationDO,validationDO.getDataObjectName(),attr.getAttrName());
                    validationDO.createEspressoRule(ruleObject);
                }
            }
        }
    }

    private void persistDerivation(AttributeDO attr) {
        DerivationDO derivationDO;
        Enumeration e = attr.getDerivations().elements();
        if (e.hasMoreElements()) {
            derivationDO = (DerivationDO) e.nextElement();
            //derv.testString();
            try {
                insert(derivationDO);
                this.ruleObject = createNewRuleObject(derivationDO, derivationDO.getDataObjectName(),attr.getAttrName());
               derivationDO.createEspressoRule(ruleObject);
            } catch (Exception ex) {
                log.error(ex.getMessage(),ex); // try catch and keep running
            }
        }
    }

    private void persistActions(DataObjectDO dataObj) {
        ActionDO actionDO;
        Enumeration e = dataObj.findChildren("Action");
        while (e.hasMoreElements()) {
            actionDO = (ActionDO) e.nextElement();
            if (actionDO.getActionToPerform() != null) {
                //act.testString();
                insert(actionDO);
                this.ruleObject = createNewRuleObject(actionDO,actionDO.getDataObjectName(),null);
                //ruleObject.createEvent(actionDO.getCondition(),"INSERTING,UPDATING","");
               // ruleObject.setComments(actionDO.getActionName() +" : "+actionDO.getActionToPerform());
            }
        }
    }

    private void persistIndexes(DataObjectDO dataObj) {
        DO_Index index;
        Enumeration e = dataObj.findChildren("IndexDO");
        while (e.hasMoreElements()) {
            index = (DO_Index) e.nextElement();
            insert(index);
            persistIndexAttrs(index);
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
        ConstraintDO constraintDO;
        Enumeration e = dataObj.findChildren("Constraints");
        Hashtable constraintHT = new Hashtable();
        while (e.hasMoreElements()) {
            constraintDO = (ConstraintDO) e.nextElement();
            //con.testString();
            // duplicate constraint names in gen code
            if (constraintHT.get(constraintDO.getConstraintName()) == null) {
                insert(constraintDO);
                constraintHT.put(constraintDO.getConstraintName(), constraintDO);
                //this.ruleObject = createNewRuleObject(constraintDO, constraintDO.getDataObjectName(),null);
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
        RelationshipDO relationshipDO;
        Enumeration e = dataObj.getParentReln().elements();
        while (e.hasMoreElements()) {
            relationshipDO = (RelationshipDO) e.nextElement();
            insertLinkTable(dataObj, relationshipDO, true);
        }
        e = dataObj.getChildReln().elements();
        while (e.hasMoreElements()) {
            relationshipDO = (RelationshipDO) e.nextElement();
            insertLinkTable(dataObj, relationshipDO, false);
        }
    }

    private void insertLinkTable(DataObjectDO dataObj, RelationshipDO reln, boolean isParentRole) {
        // insert child/parent relationship link table
        String doName;
        BusinessObjectDO businessObjectDO = null;
        try {
            doName = isParentRole == true ? reln.getParentDOName() : reln.getChildDOName();
            businessObjectDO = new BusinessObjectDO("DataObject_Jn_Reln");
            businessObjectDO.setAttrValue("RepositoryName", reln.getRepositoryName());
            businessObjectDO.setAttrValue("RepositoryVersion", this.repositoryVersion);
            businessObjectDO.setAttrValue("DataObjectName", doName);
            businessObjectDO.setAttrValue("RelationshipName", reln.getRelationshipName());
            businessObjectDO.setAttrValue("IsParentRole", isParentRole == true ? "true" : "false");
            insert(businessObjectDO);
        } catch (Exception ex) {
            reln.testString();
            if (businessObjectDO != null)
                businessObjectDO.testString();
            log.error(ex.getMessage(),ex);
        }
    }

    private void persistQueryObjects(BSDDataObject root) {
        QueryObjectDO qryDO;
        Enumeration e = root.findChildren("QueryObject");
        while (e.hasMoreElements()) {
            qryDO = (QueryObjectDO) e.nextElement();
            insert(qryDO);
            persistQueryDataObjects(qryDO);
        }
    }
    private void persistQueryJoins(BSDDataObject root) {
        QueryObjectDO qryDO;
        Enumeration e = root.findChildren("QueryObject");
        while (e.hasMoreElements()) {
            qryDO = (QueryObjectDO) e.nextElement();
            persistQueryJoin(qryDO);
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

    private void persistQueryJoin(QueryObjectDO qryDO) {
        QueryJoinDataObjectDO qryJNDO;
        Enumeration e = qryDO.findChildren("QueryJoinDataObject");
        while (e.hasMoreElements()) {
            qryJNDO = (QueryJoinDataObjectDO) e.nextElement();
            qryJNDO.setRelationshipName(qryJNDO.getLeftDataObjectName()+"_JN_"+qryJNDO.getRightDataObjectName());
            insert(qryJNDO);
        }
    }

    private void persistApplications(BSDDataObject root) {
        ApplicationDO applicationDO;
        Enumeration e = root.findChildren("Application");
        while (e.hasMoreElements()) {
            applicationDO = (ApplicationDO) e.nextElement();
            try {
                insert(applicationDO);
                persistForms(applicationDO);
            } catch (Exception ex) {
                log.error(ex.getMessage(),ex); //skip this app
            }
        }
    }

    private void persistGroups(BSDDataObject root) {
        GroupDO groupDO;
        Enumeration e = root.findChildren("Groups");
        while (e.hasMoreElements()) {
            groupDO = (GroupDO) e.nextElement();
            try {
                insert(groupDO);
            } catch (Exception ex) {
                groupDO.testString();
                // log.error(ex.getMessage(),ex); //skip this if duplicate
            }
        }
        persistGroupJnObject(root);
    }

    private void persistGroupJnObject(BSDDataObject root) {
        GroupDO group;
        Object_JN_Group object_jn_group;
        Enumeration e = root.findChildren("Groups");
        while (e.hasMoreElements()) {
            group = (GroupDO) e.nextElement();
            Enumeration ee = group.findChildren("Object_JN_Group");
            while (ee.hasMoreElements()) {
                object_jn_group = (Object_JN_Group) ee.nextElement();
                try {
                    insert(object_jn_group);
                } catch (Exception ex) {
                    object_jn_group.testString();
                    //log.error(ex.getMessage(),ex); //skip this app
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
        RecordSourceDO recordSourceDO;
        Enumeration e = form.findChildren("RecordSource");
        while (e.hasMoreElements()) {
            recordSourceDO = (RecordSourceDO) e.nextElement();
            try {
                if (tryInsert(recordSourceDO)) {
                    recordSourceDO.setBusinessObject(recordSourceDO.getBusinessObject() + "_1");
                    if (tryInsert(recordSourceDO)) {
                        recordSourceDO.setBusinessObject(recordSourceDO.getBusinessObject() + "_2");
                        insert(recordSourceDO);
                    }
                }
            } catch (Exception ex) {
                //if we have a duplicate name - let's try to alter tha name and try again

            }
            persistRecordSourceAttr(recordSourceDO);
        }
    }

    private void persistRecordSourceAttr(RecordSourceDO rs) {
        RSAttributesDO rsAttributesDO;
        Enumeration e = rs.findChildren("RSAttribute");
        while (e.hasMoreElements()) {
            rsAttributesDO = (RSAttributesDO) e.nextElement();
            rsAttributesDO.setAttrValue("BusinessObject", rs.getAttrValue("BusinessObject")); //propage new name
            insert(rsAttributesDO);
        }
    }

    public void persistOtherFiles(BSDDataObject root) {
        OtherFileDO otherFileDO;
        Enumeration e = root.findChildren("OtherFile");
        while (e.hasMoreElements()) {
            otherFileDO = (OtherFileDO) e.nextElement();
            try {
                insert(otherFileDO);
            } catch (Exception ex) {
                log.error(ex.getMessage(),ex); //skip this file
            }
        }
    }

    //========================================================================================
    public HashMap<String, BSDField> getHashMap(BSDDataObject bsdDataObject) {
        HashMap map = new HashMap();
        if(bsdDataObject.getParent() == null){
            //fix unlinked objects
            bsdDataObject.setAttrValue("RepositoryVersion",this.repositoryVersion);
        }
        Enumeration e = bsdDataObject.getAttrList(); // list of named attributes
        Enumeration keys = getKeyAttrs(bsdDataObject);
        while (e.hasMoreElements()) {
            String attrName = (String) e.nextElement();
            BSDField f = new BSDField(attrName, bsdDataObject.getAttrValue(attrName));
            if (containsKey(keys, attrName)){
                map.put(attrName, f);
            }
        }
        return map;
    }

    // need to get the PROTECTED attributes from the VSMetaColumn
    private Enumeration getKeyAttrs(BSDDataObject obj) {
        String bo_name = obj.getName();
        //this code below is only used for debug/print purposes
        //Enumeration e = getProtectedAttrs(bo_name);
        //while(e.hasMoreElements()){
        //	String str = (String) e.nextElement();
        //	db("Protected ATTR NAME = " + str);
        //}
       // db("VLSPersistence Instance " + getProcessor());
        return null;//getProtectedAttrs(bo_name);
    }

    // does this enumeration of keys contain this attribute name
    private boolean containsKey(Enumeration e, String attrName) {
        String key;
        if (e != null) {
            while (e.hasMoreElements()) {
                key = (String) e.nextElement();
                if (key.equals(attrName))
                    return false;
            }
        }
        return true;
    }

    // After an insert - we get the new values so let's update the attribute
    // cool thing here is that we have ALL the attributes after insert (not just the modeled attributes)
    // can now use getAttrList and getAttrName(aName)
    public void setHashMap(HashMap<String, BSDField> map, BSDDataObject bsdDataObject) {
        Set<String> keys = map.keySet();
        for (String key : keys) {
            BSDField f = (BSDField) map.get(key);
            bsdDataObject.setAttr(f.getName(), f.getValue());
        }
    }

    public void setAttrs(BSDDataObject bsdDataObject, BSDDataObject row) {
        //VSMetaColumn Mc = null;
       // VSQueryColumnDefinition qc = null;
       // VSMetaQuery Mq = getSession().getMetaQuery(bsdDataObject.getName());
        Enumeration e = null;//Mq.getQueryColumns();
        while (e != null && e.hasMoreElements()) {
           // qc = (VSQueryColumnDefinition) e.nextElement();
           // String attrName = (String) qc.getMetaColumn().getName();
           // VSData d = row.getData(attrName);
            //bsdDataObject.setAttrValue(attrName, d.getString());
        }
    }
    public void insertRoot(RepositoryDO rootDataObject){
        //insertParentIfNone(rootDataObject);
        rootDataObject.recursiveReposVersion(this.repositoryVersion);
        insert(rootDataObject);
    }
    /*
    this is to replace the rule insertParentIfNone only if REST server does not provide rule
    */
    private void insertParentIfNone(BSDDataObject rootDataObject){

        BSDDataObject reposName = new BSDDataObject("ReposName");
        reposName.setAttr("RepositoryInternalName",rootDataObject.getAttrValue("ReposInternalName"));

        BSDDataObject reposVersion = new BSDDataObject("ReposVersion");
        reposVersion.setAttr("Version",rootDataObject.getAttrValue("RepositoryVersion"));

        insert(reposName);
        insert(reposVersion);
        //end InsertParentifNone

    }
    public void insert(BSDDataObject bsdDataObject) {
        if (bsdDataObject == null) {
            return;
        }
        try {
            raiseBeforeInsert(bsdDataObject);
            if(!testModeBypassInsert){
              insertData(bsdDataObject.getQueryName(), getHashMap(bsdDataObject));
            }

            // setHashMap(ht, bsdDataObject); // after insert we get back pkeys and calculated values!
            raiseAfterInsert(bsdDataObject);
        } catch (Exception ex) {
            log.error(ex.getMessage(),ex);
            throw new BSDException("Insert of DataObject :" + bsdDataObject.businessObjectName + " failed", ex);
        }
    }

    // Utility function to let caller know if insert succeeded or failed
    public boolean tryInsert(BSDDataObject obj) {
        try {
            if(!testModeBypassInsert){
                insert(obj);
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    // update all children by doing multiple inserts
    public void insertAll(Enumeration e) {
        BSDDataObject bsdDataObject = null;
        while (e.hasMoreElements()) {
            bsdDataObject = (BSDDataObject) e.nextElement();
            insert(bsdDataObject);
        }
    }

    public void update(BSDDataObject bsdDataObject) {
        if (bsdDataObject == null) return;
        try {
            raiseBeforeUpdate(bsdDataObject);
            HashMap<String, BSDField> map = getHashMap(bsdDataObject);
            insertUpdateData(bsdDataObject.getQueryName(), map, getKeys(bsdDataObject));
            setHashMap(map, bsdDataObject); // after update we get back calculated values!
            raiseAfterUpdate(bsdDataObject);
        } catch (Exception ex) {
            throw new BSDException("updateObjec:" + bsdDataObject.getName() + " failed", ex);
        }
    }

    public void updateAll(Enumeration e) {
        BSDDataObject bsdDataObject = null;
        while (e.hasMoreElements()) {
            bsdDataObject = (BSDDataObject) e.nextElement();
            update(bsdDataObject);
        }
    }

    public void query(BSDDataObject bsdDataObject, String where) {
        if (bsdDataObject == null) return;
        BSDDataObject row = null;

        try {
            HashMap<?,?> ht = getHashMap(bsdDataObject);
            //VSResultSet rs = QueryBusinessObject(bsdDataObject.getName(),where);
            row = getBusinessObjectRow(bsdDataObject.getQueryName(), where);
            //if(rs != null){
            //	VSRow row = rs.next();
            if (row != null) { // should be a loop -
                setAttrs(bsdDataObject, row);
            }
        } catch (Exception ex) {
            throw new BSDException("Query Object:" + bsdDataObject.getName() + " failed", ex);
        } finally {

        }

    }

    //this does not have any objects so we pass the a placeholder object
    // and then allow the system to build clones using the class loader.
    // this should hook to this objects parent so be sure to create the linked clone and then remove
    public void queryBuild(BSDDataObject parentObject, String query_name, String bo_name, String where) {
        HashMap<?,?> rs = new HashMap<Object, Object>();
        BSDDataObject newEOP = null;
        try {
            rs = findAllBusinessObjects(query_name, where);
            BSDDataObject row = null ;
            while (rs != null && !rs.isEmpty()) {
                String className = _classpath + bo_name;
                Class c = Class.forName(className);
                newEOP = (BSDDataObject) c.newInstance();
                parentObject.addChild(newEOP);
                setAttrs(newEOP, row);
                //row = rs.next();
            }
        } catch (Exception ex) {
            throw new BSDException("QueryBuild failed on BO Name " + newEOP.getName(), ex);
        } finally {

        }
    }

    private HashMap<String, BSDField> getKeys(BSDDataObject bsdDataObject) {
        HashMap<String, BSDField> map = new HashMap<String, BSDField>();
        Enumeration keys = bsdDataObject.getKeyAttrs();
        while (keys.hasMoreElements()) {
            String attrName = (String) keys.nextElement();
            BSDField f = new BSDField(attrName, bsdDataObject.getAttrValue(attrName));
            map.put(attrName, f);
        }
        return map;
    }

    public String formatDate(String date) {
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(date));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            return sdf.format(cal.getTime());
        } else
            return getFormattedDate();
    }

    public String getFormattedDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        return sdf.format(cal.getTime());
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

    public void BeginInboundTest() {
        // do transform
    }

    public void raiseAfterQuery(BSDDataObject dataObject) {
        dataObject.beforeQuery();
    }

    public void raiseBeforeQuery(BSDDataObject dataObject) {
        dataObject.afterQuery();
    }

    public void raiseAfterInsert(BSDDataObject dataObject) {
        dataObject.afterInsert();
    }

    public void raiseBeforeInsert(BSDDataObject dataObject) {
        dataObject.beforeInsert();
    }

    public void raiseAfterUpdate(BSDDataObject dataObject) {
        dataObject.afterUpdate();
    }

    public void raiseBeforeUpdate(BSDDataObject eop) {
        eop.beforeUpdate();
    }

    public void beginTransaction() {
        db("Begin Transaction....");
        //getSession().beginTrans();
    }

    public void commitTransaction() {
        db("Start Commit....");
        // getSession().commit();
    }

    public void rollbackTransaction() {
        db("Start Rollback");
        // getSession().rollback();
    }

    //** Clean up after running code
    public void close() {
        _eventCode = null;                // event code (internal)
        //super.close();

    }

    private EspressoRuleObjectImpl createNewRuleObject(BSDDataObject dataObject,String doNme,String attrName){
        EspressoRuleObjectImpl ruleObject = new EspressoRuleObjectImpl(doNme,attrName);
        List<String> attributeNameList = getDataObjectAttrNames(dataObject);
        ruleObject.setAttrList(attributeNameList);
        allEsprssoRules.add(ruleObject.getRuleBean());
        return ruleObject;
    }

    /**
     * find the root dataobject and ask for the child attributes by name
     * @param dataObject
     * @return
     */
    private List<String> getDataObjectAttrNames(BSDDataObject dataObject) {
        List<String> names = new Vector<String>();
        DataObjectDO dataObjectRoot = dataObject.getDataObjectRoot();
        for(AttributeDO attributeDO : dataObjectRoot.findAttributeList()){
            names.add(attributeDO.getAttrName());
        }
        return names;
    }

    public void db(String msg) {
        //System.out.println(msg);
        if(log.isDebugEnabled()){
            log.debug("RESTPersistenceManager Message: "+msg);
        }
    }
}

