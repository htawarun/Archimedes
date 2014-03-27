/*
 *    Title:         WhereUsedDO
 *    Description:   Used to hold pointer references for usedBy and whereUsed analysis
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 *
*/
package com.bandsoftware.data;

public class WhereUsedDO extends BusinessObjectDO {
    private String RepositoryName;
    private String ReposInternalName;
    private BSDDataObject SourceObject;
    private String SourceName;
    private String SourceType = "DATAOBJECT";  //DataObject, QueryObject, App, Form
    private DataObjectDO UsedInObject;
    private QueryObjectDO UsedInQueryObject;
    private String usedInType;
    private String usedInName;


    public WhereUsedDO(String name) {
        super("WhereUsed");
        setRepositoryName(name);
        setReposInternalName(name);
    }

    public String getRepositoryName() {
        return RepositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        setAttrValue("RepositoryName", repositoryName);
        RepositoryName = repositoryName;
    }


    public String getReposInternalName() {
        return ReposInternalName;
    }

    public BSDDataObject getSourceObject() {
        return SourceObject;
    }

    public void setSourceObject(BSDDataObject sourceObject) {
        SourceObject = sourceObject;
    }

    public String getSourceType() {
        return SourceType;
    }

    public void setSourceType(String sourceType) {
        SourceType = sourceType;
    }

    public DataObjectDO getUsedInObject() {
        return UsedInObject;
    }

    public void setUsedInObject(DataObjectDO usedInObject) {
        UsedInObject = usedInObject;
    }

    public String getUsedInType() {
        return usedInType;
    }

    public void setUsedInType(String usedInType) {
        this.usedInType = usedInType;
    }

    public void setReposInternalName(String reposInternalName) {
        setAttrValue("ReposInternalName", reposInternalName);
        ReposInternalName = reposInternalName;
    }

    public String getSourceName() {
        return SourceName;
    }

    public void setSourceName(String sourceName) {
        SourceName = sourceName;
    }

    public String getUsedInName() {
        return usedInName;
    }

    public void setUsedInName(String usedInName) {
        this.usedInName = usedInName;
    }

    public String toString() {
        return "Source = " + this.getSourceName() + " , UsedInName " + this.getUsedInName() + " , UsedInType " + this.getUsedInType() + " ,UsedInObject " + this.getUsedInObject() + " or " + this.getUsedInQueryObject();
    }

    public QueryObjectDO getUsedInQueryObject() {
        return UsedInQueryObject;
    }

    public void setUsedInQueryObject(QueryObjectDO usedInQueryObject) {
        UsedInQueryObject = usedInQueryObject;
    }

    public String getUsedInObjectName() {
        if (this.getSourceType().equals("QUERYOBJECT"))
            return ((QueryObjectDO) this.getSourceObject()).getQueryObjectName();
        else
            return this.getSourceName();
        /*
        if(this.getUsedInObject() != null)
            return UsedInObject.getDataObjectName();
        return this.getUsedInObjectName();
        */

    }

    public String getUsedInDescription() {
        if (!"Count".equals(this.getUsedInType()))
            return this.getUsedInType() + " " + this.getUsedInName();
        else
            return this.getUsedInType();
    }
}
