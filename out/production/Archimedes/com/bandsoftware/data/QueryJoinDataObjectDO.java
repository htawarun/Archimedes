
/*
 *    Title:         QueryJoinDataObjectDO
 *    Description:   Query Data Object - this is a join defeinition between 2 QueryDataObjects
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 *
*/
package com.bandsoftware.data;

import com.datachannel.xml.om.IXMLDOMNamedNodeMap;
import com.datachannel.xml.om.IXMLDOMNode;
import com.datachannel.xml.om.IXMLDOMNodeList;

public class QueryJoinDataObjectDO extends BusinessObjectDO {
    private String RepositoryName;
    private String QueryObjectName;
    private String LeftDataObjectName;
    private String RightDataObjectName;
    private String JoinType;
    private IXMLDOMNamedNodeMap nnodeMap;
    private IXMLDOMNodeList childNodes;
    private IXMLDOMNode childNode;

    public QueryJoinDataObjectDO() {
        super("QueryJoinDataObject");
        WhereUsedAware = false;
        //addKey("RepositoryName");
        //addKey("DataObjectName");
    }

    public QueryJoinDataObjectDO(String reposName, String queryName) {
        super("QueryJoinDataObject");
        setRepositoryName(reposName);
        setQueryObjectName(queryName);
        WhereUsedAware = false;
    }

    public QueryJoinDataObjectDO(String reposName, String queryName, IXMLDOMNode nnode) {
        super("QueryDataObject");
        setRepositoryName(reposName);
        setQueryObjectName(queryName);
        WhereUsedAware = false;
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

    public String getLeftDataObjectName() {
        return LeftDataObjectName;
    }

    public void setLeftDataObjectName(String dataObjectName) {
        setAttrValue("LeftDataObjectName", dataObjectName);
        LeftDataObjectName = dataObjectName;
    }

    public String getRightDataObjectName() {
        return RightDataObjectName;
    }

    public void setRightDataObjectName(String dataObjectName) {
        setAttrValue("RightDataObjectName", dataObjectName);
        RightDataObjectName = dataObjectName;
    }

    public String getRepositoryName() {
        return RepositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        setAttrValue("RepositoryName", repositoryName);
        RepositoryName = repositoryName;
    }

    public String getJoinType() {
        return JoinType;
    }

    public void setJoinType(String joinType) {
        setAttrValue("JoinType", joinType);
        JoinType = joinType;
    }

    public void write(String dataObjectAbbr, StringBuffer sb) {
        //    if (ValueRequired) {
        //        sb.append("  ASSERT " + dataObjectAbbr + "." + AttrName + " IS NOT NULL");
        //       sb.append("\n");
        //   }
    }

}

