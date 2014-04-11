package com.bandsoftware.data;

import java.util.Enumeration;
import java.util.Vector;

/*
 *    Title:         RelationshipDO
 *    Description:   Defines the properties for a relationship between 2 data objects
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 *
*/

public class RelationshipDO extends BusinessObjectDO {
    private String RepositoryName;
    private String RelationshipName;
    private String parentDOName;
    private String childDOName;
    private String ParentRoleName;
    private String OnParentUpdate;
    private String OnParentDelete;
    private String OnChildInsertOrUpdate;
    private String ErrPreventInsertOrUpdateChild;
    private String ChildRoleName;
    private DataObjectDO parentDO;
    private DataObjectDO childDO;
    private boolean IsEnforce = false;
    private String ParentAttribute = null;
    private String ChildAttribute = null;
    private Vector<String> ChildAttributes = new Vector<String>();
    private Vector<String> ParentAttributes = new Vector<String>();
    private String relnSurrId;
    // need to get AttributePairs

    public RelationshipDO() {
        super("Relationship");
        addKey("RepositoryName");
    }

    public RelationshipDO(String reposName, String parentName, String childName) {
        super("Relationship");
        addKey("RepositoryName");
        setRepositoryName(reposName);
        setParentDOName(parentName);
        setChildDOName(childName);
        setRelationshipName(parentName + "_JN_" + childName);
    }

    public void init() {
        //this code will be used to parse the Relationship and populate local values

    }

    public String getParentDOName() {
        return parentDOName;
    }

    public void setParentDOName(String aParentDOName) {
        setAttrValue("ParentDOName", aParentDOName);
        this.parentDOName = aParentDOName;
    }

    public String getChildDOName() {
        return childDOName;
    }

    public void setChildDOName(String aChildDOName) {
        setAttrValue("ChildDOName", aChildDOName);
        this.childDOName = aChildDOName;
    }

    public String getParentAttribute() {
        return ParentAttribute;
    }

    public Vector getParentAttributes() {
        return ParentAttributes;
    }

    public void setParentAttribute(String parentAttribute) {
        setAttrValue("ParentAttribute", parentAttribute);
        ParentAttributes.addElement(parentAttribute);
        // save first one for backwards compataibility
        if (ParentAttribute == null)
            ParentAttribute = parentAttribute;
    }

    public String getChildAttribute() {
        return ChildAttribute;
    }

    public Vector getChildAttributes() {
        return ChildAttributes;
    }

    public void setChildAttribute(String childAttribute) {
        setAttrValue("ChildAttribute", childAttribute);
        ChildAttributes.addElement(childAttribute);
        // save first one for backwards compataibility
        if (ChildAttribute == null)
            ChildAttribute = childAttribute;
    }

    public String getRelationshipName() {
        return getAttrValue("RelationshipName");
    }

    public void setRelationshipName(String relationshipName) {
        setAttrValue("RelationshipName", relationshipName);
        RelationshipName = relationshipName;
    }

    public String getParentRoleName() {
        return ParentRoleName;
    }

    public void setParentRoleName(String parentRoleName) {
        setAttrValue("ParentRoleName", parentRoleName);
        ParentRoleName = parentRoleName;
    }

    public String getChildRoleName() {
        return ChildRoleName;
    }

    public void setChildRoleName(String childRoleName) {
        setAttrValue("ChildRoleName", childRoleName);
        ChildRoleName = childRoleName;
    }

    public DataObjectDO getParentDO() {
        return parentDO;
    }

    public void setParentDO(DataObjectDO parent) {
        setAttrValue("ParentDOName", parent.getDataObjectName());
        this.parentDO = parent;
    }

    public DataObjectDO getChildDO() {
        return childDO;
    }

    public void setChildDO(DataObjectDO child) {
        setAttrValue("ChildDOName", child.getDataObjectName());
        this.childDO = child;
    }

    public boolean isEnforce() {
        return IsEnforce;
    }

    public void setIsEnforce(boolean enforce) {
        setAttrValue("IsEnforce", enforce == true ? "1" : "0");
        IsEnforce = enforce;
    }

    public String getRepositoryName() {
        return getAttrValue("RepositoryName");
    }

    public void setRepositoryName(String repositoryName) {
        setAttrValue("RepositoryName", repositoryName);
        RepositoryName = repositoryName;
    }

    public void isEnforce(boolean enforce) {
        IsEnforce = enforce;
    }

    public String getRelnSurrId() {
        return relnSurrId;
    }

    public void setRelnSurrId(String relnSurrId) {
        this.relnSurrId = relnSurrId;
    }

    // Public method to append attribute information to DICE Script
    public void write(String dataObjAbbr, StringBuffer sb, int indx) {
        // only test enforced relationships
        String abbr2 = createAttrVar(parentDOName); //  + indx;
        String abbr = "$" + abbr2;
        if (IsEnforce) {
            newLine(sb);
            sb.append("//Check " + ChildAttribute);
            newLine(sb);
            sb.append("IF " + dataObjAbbr + "." + ChildAttribute + " IS NOT NULL");
            newLine(sb);
            sb.append("  NOTE Checking Relationship " + childDOName + " " + dataObjAbbr + "." + this.parentDO.getDataObjectNameAndPkey() + " " + abbr2 + "=" + dataObjAbbr + "." + ChildAttribute);
            newLine(sb);
            sb.append("    FETCH FROM " + parentDOName + " INTO " + abbr2 + " WHERE " + ParentAttribute + " = " + dataObjAbbr + "." + ChildAttribute);
            newLine(sb);
            sb.append("    ASSERT EXISTS " + abbr);
            newLine(sb);

            sb.append("END IF");
            newLine(sb);
        }

    }

    public String formatDataObjectNameAndPkey(String dataObjectAbbr) {
        // ans = ""; // DataObjectName + ".";
        Enumeration e = this.getChildAttributes().elements();
        Enumeration ee = this.getParentAttributes().elements();
        int size = this.getChildAttributes().size();
        int cnt = 0;
        String childAttr = "";
        String parentAttr = "";
        String ans = "";
        String connector = "";
        while (e.hasMoreElements()) {
            cnt++;
            childAttr = (String) e.nextElement();
            parentAttr = (String) ee.nextElement();
            ans = ans + connector + childAttr + " = " + dataObjectAbbr + "." + parentAttr;
            connector = " AND ";
            // don't include the last one
            if ((cnt + 1) == size)
                break;
        }
        return ans;
    }

    public String getOnParentUpdate() {
        return OnParentUpdate;
    }

    public void setOnParentUpdate(String onParentUpdate) {
        setAttrValue("onParentUpdate", stripReplace(onParentUpdate));
        OnParentUpdate = onParentUpdate;
    }

    public String getOnParentDelete() {
        return OnParentDelete;
    }

    public void setOnParentDelete(String onParentDelete) {
        setAttrValue("onParentDelete", stripReplace(onParentDelete));
        OnParentDelete = onParentDelete;
    }

    public String getOnChildInsertOrUpdate() {
        return OnChildInsertOrUpdate;
    }

    public void setOnChildInsertOrUpdate(String onChildInsertOrUpdate) {
        setAttrValue("onChildInsertOrUpdate", stripReplace(onChildInsertOrUpdate));
        OnChildInsertOrUpdate = onChildInsertOrUpdate;
    }

    public String getErrPreventInsertOrUpdateChild() {
        return ErrPreventInsertOrUpdateChild;
    }

    public void setErrPreventInsertOrUpdateChild(String errPreventInsertOrUpdateChild) {
        setAttrValue("errPreventInsertOrUpdateChild", stripReplace(errPreventInsertOrUpdateChild));
        ErrPreventInsertOrUpdateChild = errPreventInsertOrUpdateChild;
    }
}
