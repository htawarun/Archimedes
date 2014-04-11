/*
 *    Title:         RecordSourceDO
 *    Description:   Used to define both Java and HTML properties
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

public class TransitionDO extends BusinessObjectDO {
    private String RepositoryName;
    private String ApplicationName;
    private String BusinessObject;
    private String FormName;
    private String TargetFormName;
    private String FormTransNum;
    private String Relationship;
    private IXMLDOMNamedNodeMap nnodeMap;


    public TransitionDO() {
        super("Transition");
        addKey("TargetLanguage");
    }

    public TransitionDO(String reposName, String appName, String formName) {
        super("Transition");
        setRepositoryName(reposName);
        setFormName(formName);
        setApplicationName(appName);
    }

    public TransitionDO(String reposName, String appName, String formName, IXMLDOMNode nnode) {
        super("Transition");
        setRepositoryName(reposName);
        setFormName(formName);
        setApplicationName(appName);
        init(nnode);
    }

    public String getBusinessObject() {
        return BusinessObject;
    }

    public void setBusinessObject(String businessObject) {
        setAttrValue("BusinessObject", businessObject);
        BusinessObject = businessObject;
    }

    public void init(IXMLDOMNode nnode) {
        nnodeMap = (IXMLDOMNamedNodeMap) nnode.getAttributes();

        // for this recordsource find the RSAttributesMeta and extract the details


    }


    public String getRepositoryName() {
        return RepositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        setAttrValue("RepositoryName", repositoryName);
        RepositoryName = repositoryName;
    }

    public String getFormName() {
        return FormName;
    }

    public void setFormName(String formName) {
        setAttrValue("FormName", formName);
        FormName = formName;
    }

    public void setApplicationName(String attrName) {
        setAttrValue("ApplicationName", attrName);
        ApplicationName = attrName;
    }

    public String getApplicationName() {
        return ApplicationName;
    }

    public String getTargetFormName() {
        return TargetFormName;
    }

    public void setTargetFormName(String targetFormName) {
        setAttrValue("TargetFormName", targetFormName);
        TargetFormName = targetFormName;
    }

    public String getRelationship() {
        return Relationship;
    }

    public void setRelationship(String relationship) {
        setAttrValue("Relationship", relationship);
        Relationship = relationship;
    }

    public String getFormTransNum() {
        return FormTransNum;
    }

    public void setFormTransNum(String formTransNum) {
        setAttrValue("FormTransNum", formTransNum);
        FormTransNum = formTransNum;
    }

    public void write(String dataObjectAbbr, StringBuffer sb) {
        //    if (ValueRequired) {
        //        sb.append("  ASSERT " + dataObjectAbbr + "." + AttrName + " IS NOT NULL");
        //       sb.append("\n");
        //   }
    }
}

