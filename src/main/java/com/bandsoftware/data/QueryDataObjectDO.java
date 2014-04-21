/*
 *    Title:         QueryDataObjectDO
 *    Description:   Query Data Object used to join multiple DataObjects  (Used by Query Object)
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 *
*/
package com.bandsoftware.data;

import com.datachannel.xml.om.IXMLDOMNamedNodeMap;
import com.datachannel.xml.om.IXMLDOMNode;

public class QueryDataObjectDO extends BusinessObjectDO {
    private String RepositoryName;
    private String QueryObjectName;
    private String DataObjectName;
    private String Alias;
    private IXMLDOMNamedNodeMap nnodeMap;

    public QueryDataObjectDO() {
        super("QueryDataObject");
        //addKey("RepositoryName");
        //addKey("DataObjectName");
    }

    public QueryDataObjectDO(String reposName, String queryName) {
        super("QueryDataObject");
        setRepositoryName(reposName);
        setQueryObjectName(queryName);
    }

    public QueryDataObjectDO(String reposName, String queryName, String dataObjName) {
        super("QueryDataObject");
        setRepositoryName(reposName);
        setQueryObjectName(queryName);
        setDataObjectName(dataObjName);
    }

    public QueryDataObjectDO(String reposName, String queryName, IXMLDOMNode nnode) {
        super("QueryDataObject");
        setRepositoryName(reposName);
        setQueryObjectName(queryName);
        init(nnode);
    }

    public void init(IXMLDOMNode nnode) {
        nnodeMap = (IXMLDOMNamedNodeMap) nnode.getAttributes();
        if (nnodeMap != null) {
            String qryObj = "";
            setQueryObjectName(qryObj);
        }
    }


    public String getQueryObjectName() {
        return QueryObjectName;
    }

    public void setQueryObjectName(String queryObjectName) {
        setAttrValue("QueryObjectName", queryObjectName);
        QueryObjectName = queryObjectName;
    }

    public String getDataObjectName() {
        return DataObjectName;
    }

    public void setDataObjectName(String dataObjectName) {
        setAttrValue("DataObjectName", dataObjectName);
        DataObjectName = dataObjectName;
    }

    public String getRepositoryName() {
        return RepositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        setAttrValue("RepositoryName", repositoryName);
        RepositoryName = repositoryName;
    }

    public String getAlias() {
        return Alias;
    }

    public void setAlias(String alias) {
        setAttrValue("Alias", alias);
        Alias = alias;
    }

    public void write(String dataObjectAbbr, StringBuffer sb) {
        //    if (ValueRequired) {
        //        sb.append("  ASSERT " + dataObjectAbbr + "." + AttrName + " IS NOT NULL");
        //       sb.append("\n");
        //   }
    }

    public void populateWhereUsed(WhereUsedDO wu) {
        //implemented in subclasses
        wu.setSourceObject(this.getParent());
        wu.setSourceName(((QueryObjectDO) this.getParent()).getQueryObjectName());
        wu.setSourceType("QUERYOBJECT"); // QUERYOBJECT, RELN
        wu.setUsedInObject(findSourceDO(getDataObjectName()));
        wu.setSourceName(getDataObjectName());
        wu.setUsedInType("QUERY"); //COUNT, REPL, RELN, FORM
        wu.setUsedInName("");
    }

    public DataObjectDO findSourceDO(String sourceName) {
        DataObjectDO sourceDO = null;
        String name = sourceName;
        if (sourceDO == null) {
            RepositoryDO repos = (RepositoryDO) this.getParent().getParent();
            return repos.findDataObject(name);
        }
        return null;
    }
}

