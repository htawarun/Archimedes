/*
 *    Title:         ApplicationDO
 *    Description:   Root Appplication Container (HTML or Java)
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

public class ApplicationDO extends BusinessObjectDO {
    private String RepositoryName;
    private String ApplicationName;
    private String Description;
    private String Caption;
    private String Comments;
    private String TargetLanguage;
    private IXMLDOMNodeList childNodes;
    private IXMLDOMNode childNode;
    private String isJava;
    private Vector<FormDO> forms = new Vector<FormDO>();

    public ApplicationDO() {
        super("Application");
        //addKey("RepositoryName");
        //addKey("DataObjectName");
    }

    public ApplicationDO(String reposName, String appName) {
        super("Application");
        setRepositoryName(reposName);
        setApplicationName(appName);
    }

    public ApplicationDO(String reposName, String appName, IXMLDOMNode nnode) {
        super("Application");
        setRepositoryName(reposName);
        setApplicationName(appName);
        init(nnode);
    }

    public void init(IXMLDOMNode nnode) {
        IXMLDOMNamedNodeMap nnodeMap = (IXMLDOMNamedNodeMap) nnode.getAttributes();
        if (nnodeMap != null) {

        }
        //doForms()
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

    public Vector<FormDO> getForms() {
        return forms;
    }

    public String getTargetLanguage() {
        return TargetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        setAttrValue("TargetLanguage", targetLanguage);
        TargetLanguage = targetLanguage;
    }

    public void setForms(Vector<FormDO> forms) {
        this.forms = forms;
    }

    public void addForm(FormDO form) {
        this.forms.addElement(form);
    }

    //override the parent to include these children
    public void recursiveReposVersion(String repositoryVersion) {

        for (FormDO aForm : this.getForms()) {
            db(aForm.getName() + " BO NAME: " + aForm.getBOName());
            aForm.setAttrValue("RepositoryVersion", repositoryVersion);
        }
    }

    public void write(String dataObjectAbbr, StringBuffer sb) {
        //    if (ValueRequired) {
        //        sb.append("  ASSERT " + dataObjectAbbr + "." + AttrName + " IS NOT NULL");
        //       sb.append("\n");
        //   }
    }

    public String isJava() {
        return isJava;
    }

    public void setIsJava(String java) {
        setAttrValue("isJava", "True".equals(java) ? "1" : "0");
        isJava = java;
    }

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String caption) {
        setAttrValue("Caption", caption);
        Caption = caption;
    }

    public void createWhereUsed() {
        FormDO form;
        RecordSourceDO rsDO;
        WhereUsedDO wu;
        AttributeDO anAttr;
        for (Enumeration frm = this.findChildren("Form"); frm.hasMoreElements(); ) {
            form = (FormDO) frm.nextElement();
            for (Enumeration rs = form.findChildren("RecordSource"); rs.hasMoreElements(); ) {
                rsDO = (RecordSourceDO) rs.nextElement();
                wu = new WhereUsedDO(this.RepositoryName);
                rsDO.populateWhereUsed(wu);
                addWhereUsed(wu);
            }
        }
    }

    private void addWhereUsed(WhereUsedDO wu) {
        //need to find target object to hook this where used to.
        if (wu.getUsedInObject() instanceof DataObjectDO) {
            DataObjectDO source = (DataObjectDO) wu.getUsedInObject();
            if (source != null) {
                if (source.usedBy == null)
                    source.usedBy = new Vector();
                source.usedBy.add(wu);
                db("adding whereUsed to " + source.getDataObjectName() + " wu: " + wu.toString());
            }
        } else {
            QueryObjectDO source = (QueryObjectDO) wu.getUsedInQueryObject();
            if (source != null) {
                if (source.usedBy == null)
                    source.usedBy = new Vector();
                source.usedBy.add(wu);
                db("adding whereUsed to " + source.getQueryObjectName() + " wu: " + wu.toString());
            }
        }

    }
}

