
/*
 *    Title:         QueryObjectDO
 *    Description:   Query Object is the main Versata object used to define joins
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

import java.util.Enumeration;
import java.util.Vector;

public class QueryObjectDO extends BusinessObjectDO {
    private String RepositoryName;
    private String QueryObjectName;
    private String InternalPathName;
    private String ChildMostDataObject;
    private String SuperClass;
    private String RuntimeSql;


    private IXMLDOMNamedNodeMap nnodeMap;
    private IXMLDOMNodeList childNodes;
    private IXMLDOMNode childNode;

    public QueryObjectDO() {
        super("QueryObject");
    }

    public QueryObjectDO(String reposName, String qoName) {
        super("QueryObject");
        setRepositoryName(reposName);
        setQueryObjectName(qoName);
    }

    public QueryObjectDO(String reposName, String qoName, IXMLDOMNode nnode) {
        super("QueryObject");
        setRepositoryName(reposName);
        setQueryObjectName(qoName);
        init(nnode);
    }

    private void init(IXMLDOMNode nnode) {
        //for each data object
        //QueryDataObjectDO qdo;
        //qdo = new QueryDataObjectDO(getRepositoryName(), getQueryObjectName(), anode);
    }

    //
    public void createJoinDataObjects(IXMLDOMNodeList joinDataObjNodes) {
        IXMLDOMNode doNode;

        String leftDataObjName;
        String rightDataObjName;
        String joinType;
        while ((doNode = (IXMLDOMNode) joinDataObjNodes.nextNode()) != null) {
            leftDataObjName = (String) doNode.getAttributes().getNamedItem("LeftDataObject").getNodeValue();
            rightDataObjName = (String) doNode.getAttributes().getNamedItem("RightDataObject").getNodeValue();
            joinType = (String) doNode.getAttributes().getNamedItem("JoinType").getNodeValue();
            QueryJoinDataObjectDO qdo = new QueryJoinDataObjectDO(RepositoryName, QueryObjectName);
            this.addChild(qdo);
            qdo.setLeftDataObjectName(leftDataObjName);
            qdo.setRightDataObjectName(rightDataObjName);
            // qdo.setJoinType(joinType);
        }

    }

    public void createDataObjects(IXMLDOMNodeList dataObjNodes) {
        IXMLDOMNode doNode;

        String dataObjName;
        String alias;
        while ((doNode = (IXMLDOMNode) dataObjNodes.nextNode()) != null) {
            dataObjName = (String) doNode.getAttributes().getNamedItem("DataObjectName").getNodeValue();
            alias = (String) doNode.getAttributes().getNamedItem("Alias").getNodeValue();
            QueryDataObjectDO qdo = new QueryDataObjectDO(RepositoryName, QueryObjectName, dataObjName);
            this.addChild(qdo);
            qdo.setAlias(alias);
        }

    }

    public String getRepositoryName() {
        return RepositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        setAttrValue("RepositoryName", repositoryName);
        RepositoryName = repositoryName;
    }

    public String getQueryObjectName() {
        return QueryObjectName;
    }

    public void setQueryObjectName(String queryObjectName) {
        setAttrValue("QueryObjectName", queryObjectName);
        QueryObjectName = queryObjectName;
    }

    public String getChildMostDataObject() {
        return ChildMostDataObject;
    }

    public void setChildMostDataObject(String childMostDataObject) {
        setAttrValue("ChildMostDataObject", childMostDataObject);
        ChildMostDataObject = childMostDataObject;
    }

    public String getSuperClass() {
        return SuperClass;
    }

    public void setSuperClass(String superClass) {
        setAttrValue("SuperClass", superClass);
        SuperClass = superClass;
    }

    public String getRuntimeSql() {
        return RuntimeSql;
    }

    public void setRuntimeSql(String runtimeSql) {
        setAttrValue("RuntimeSql", runtimeSql);
        RuntimeSql = runtimeSql;
    }

    public String getInternalPathName() {
        return InternalPathName;
    }

    public void setInternalPathName(String internalPathName) {
        setAttrValue("InternalPathName", internalPathName);
        InternalPathName = internalPathName;
    }

    public void createWhereUsed() {
        QueryDataObjectDO qdo;
        WhereUsedDO wu;
        AttributeDO anAttr;
        for (Enumeration a = this.findChildren("QueryDataObject"); a.hasMoreElements(); ) {
            qdo = (QueryDataObjectDO) a.nextElement();
            wu = new WhereUsedDO(this.RepositoryName);
            qdo.populateWhereUsed(wu);
            addWhereUsed(wu);
        }
    }

    private void addWhereUsed(WhereUsedDO wu) {
        //need to find target object to hook this where used to.
        DataObjectDO source = (DataObjectDO) wu.getUsedInObject();
        if (source != null) {
            if (source.usedBy == null)
                source.usedBy = new Vector();
            source.usedBy.add(wu);
            db("adding whereUsed to " + source.getDataObjectName() + " wu: " + wu.toString());
        }
    }
}
