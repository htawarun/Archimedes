
package com.bandsoftware.data;

/*
 *    Title:         DO_IndexAttr
 *    Description:   These are the attributes used by DO_Index
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 *
*/

public class DO_IndexAttr extends BusinessObjectDO {
    private String RepositoryName;
    private String DataObjectName;
    private String IndexName;
    private String IndexAttrName;


    public DO_IndexAttr() {
        super("IndexAttr");
        addKey("RepositoryName");
        addKey("DataObjectName");
        addKey("IndexName");
    }

    public DO_IndexAttr(String reposName, String doName, String indxName) {
        super("IndexAttr");
        addKey("RepositoryName");
        addKey("DataObjectName");
        addKey("IndexName");
        setRepositoryName(reposName);
        setDataObjectName(doName);
        setIndexName(indxName);
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


    public String getIndexName() {
        return IndexName;
    }

    public void setIndexName(String indexName) {
        setAttrValue("IndexName", indexName);
        IndexName = indexName;
    }

    public String getIndexAttrName() {
        return IndexAttrName;
    }

    public void setIndexAttrName(String indexAttrName) {
        setAttrValue("IndexAttrName", indexAttrName);
        IndexAttrName = indexAttrName;
    }

    public void write(String abbr, StringBuffer sb) {
        sb.append("//Index Attr Name: " + IndexAttrName);
        sb.append("\n");
    }
}
