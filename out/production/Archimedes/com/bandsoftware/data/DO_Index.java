
/*
 *    Title:         DO_Index
 *    Description:   Index is used to identify primary and foreign keys on Data Objects
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 *
*/
package com.bandsoftware.data;

import java.util.Enumeration;

public class DO_Index extends BusinessObjectDO {
    private String RepositoryName;
    private String DataObjectName;
    private boolean Primary;
    private boolean Unique;
    private String IndexName;
    private boolean IgnoreNulls;


    public DO_Index() {
        super("IndexDO");
        addKey("RepositoryName");
        addKey("DataObjectName");
        WhereUsedAware = false;
    }

    public DO_Index(String reposName, String doName, String indxName) {
        super("IndexDO");
        addKey("RepositoryName");
        addKey("DataObjectName");
        setRepositoryName(reposName);
        setDataObjectName(doName);
        setIndexName(indxName);
        WhereUsedAware = false;
    }

    public String getRepositoryName() {
        return RepositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        setAttrValue("RepositoryName", repositoryName);
        RepositoryName = repositoryName;
    }

    public String getDataObjectName() {
        return DataObjectName;
    }

    public void setDataObjectName(String dataObjectName) {
        setAttrValue("DataObjectName", dataObjectName);
        DataObjectName = dataObjectName;
    }

    public boolean isPrimary() {
        return Primary;
    }

    public void setPrimary(boolean primary) {
        setAttrValue("PrimaryIndx", primary == true ? "true" : "false");
        Primary = primary;
    }

    public boolean isUnique() {
        return Unique;
    }

    public void setUnique(boolean unique) {
        setAttrValue("UniqueIndx", unique == true ? "true" : "false");
        Unique = unique;
    }

    public String getIndexName() {
        return IndexName;
    }

    public void setIndexName(String indexName) {
        setAttrValue("IndexName", indexName);
        IndexName = indexName;
    }

    public boolean isIgnoreNulls() {
        return IgnoreNulls;
    }

    public void setIgnoreNulls(boolean ignoreNulls) {
        setAttrValue("IgnoreNulls", ignoreNulls == true ? "true" : "false");
        IgnoreNulls = ignoreNulls;
    }

    public void write(String abbr, StringBuffer sb) {
        sb.append("\n");
        sb.append("//Index Name: " + IndexName);
        sb.append("\n");
        sb.append("//Primary: " + Primary);
        sb.append("\n");
        sb.append("//Unique: " + Unique);
        sb.append("\n");
        Enumeration e = getChildren("IndexAttr");
        while (e.hasMoreElements()) {
            DO_IndexAttr attr = (DO_IndexAttr) e.nextElement();
            attr.write(abbr, sb);
        }

    }
}
