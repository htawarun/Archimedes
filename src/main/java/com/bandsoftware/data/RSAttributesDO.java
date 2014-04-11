
/*
 *    Title:         RSAttributesDO
 *    Description:   Holds the pointer to record source attributes
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

public class RSAttributesDO extends BusinessObjectDO {
    private String RepositoryName;
    private String ApplicationName;
    private String FormName;
    private String RecordSource;
    private String AttrName;
    private String Description;
    private String Caption;
    private String Comments;
    private String TargetLanguage;
    private String isScalar; //scalar or grid??
    private String isPlaced;
    private String isDisplayed;
    private IXMLDOMNamedNodeMap nnodeMap;


    public RSAttributesDO() {
        super("RSAttribute");
        addKey("TargetLanguage");
    }

    public RSAttributesDO(String reposName, String appName, String formName) {
        super("RSAttribute");
        setRepositoryName(reposName);
        setFormName(formName);
        setApplicationName(appName);
    }

    public RSAttributesDO(String reposName, String appName, String formName, IXMLDOMNode nnode) {
        super("RSAttribute");
        setRepositoryName(reposName);
        setFormName(formName);
        setApplicationName(appName);
        init(nnode);
    }

    public void init(IXMLDOMNode nnode) {
        nnodeMap = (IXMLDOMNamedNodeMap) nnode.getAttributes();
        // if (nnodeMap != null) {

        // }

        IXMLDOMNode cap = getNamedNode("Description", nnode);
        if (cap != null)
            setDescription(cap.getChildNodes().item(0).getNodeValue());


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

    public String getRecordSource() {
        return RecordSource;
    }

    public void setRecordSource(String recordSource) {
        setAttrValue("RecordSource", recordSource);
        RecordSource = recordSource;
    }

    public String getAttrName() {
        return AttrName;
    }

    public void setAttrName(String attrName) {
        setAttrValue("AttrName", attrName);
        AttrName = attrName;
    }

    public void setApplicationName(String attrName) {
        setAttrValue("ApplicationName", attrName);
        ApplicationName = attrName;
    }

    public String getApplicationName() {
        return ApplicationName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        setAttrValue("Description", description);
        Description = description;
    }


    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        setAttrValue("Comments", comments);
        Comments = comments;
    }

    public String getTargetLanguage() {
        return TargetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        setAttrValue("TargetLanguage", targetLanguage);
        TargetLanguage = targetLanguage;
    }

    public String getScalar() {
        return isScalar;
    }

    public void setScalar(String scalar) {
        setAttrValue("isScalar", "False".equals(scalar) ? "0" : "1");
        isScalar = scalar;
    }

    public String getPlaced() {
        return isPlaced;
    }

    public void setPlaced(String placed) {
        setAttrValue("isPlaced", "False".equals(placed) ? "0" : "1");
        isPlaced = placed;
    }

    public String getDisplayed() {
        return isDisplayed;
    }

    public void setDisplayed(String displayed) {
        setAttrValue("isDisplayed", "False".equals(displayed) ? "0" : "1");
        isDisplayed = displayed;
    }

    public void write(String dataObjectAbbr, StringBuffer sb) {
        //    if (ValueRequired) {
        //        sb.append("  ASSERT " + dataObjectAbbr + "." + AttrName + " IS NOT NULL");
        //       sb.append("\n");
        //   }
    }
}

