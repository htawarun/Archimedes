/*
 *    Title:         FormDO
 *    Description:   Abstract class used for both Java Forms and HTML Pages
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 *
*/
package com.bandsoftware.data;

import com.datachannel.xml.om.Document;
import com.datachannel.xml.om.IXMLDOMNamedNodeMap;
import com.datachannel.xml.om.IXMLDOMNode;
import com.datachannel.xml.om.IXMLDOMNodeList;

import java.util.Enumeration;

public class FormDO extends BusinessObjectDO {
    private String RepositoryName;
    private String ApplicationName;
    private String FormName;
    private String Description;
    private String Caption;
    private String Comments;
    private String ClassName;
    private String Archetype;
    private String TargetLanguage;
    private int totalNumFormDataObjects = 0; //tmb
    private int totalNumFormPickObjects = 0; //tmb
    public int totalUserMethodCount;
    public int totalGenMethodCount;

    private String FormLayout;
    private IXMLDOMNamedNodeMap nnodeMap;
    private IXMLDOMNodeList childNodes;
    private IXMLDOMNode childNode;

    public FormDO() {
        super("Form");
        addKey("TargetLanguage");
    }

    public FormDO(String reposName, String appName, String formName) {
        super("Form");
        setRepositoryName(reposName);
        setFormName(formName);
        setApplicationName(appName);
    }

    public FormDO(String reposName, String appName, String formName, IXMLDOMNode nnode) {
        super("Form");
        setRepositoryName(reposName);
        setFormName(formName);
        setApplicationName(appName);
        db("Application " + appName + " new FormName is: " + formName);
        init(nnode);
    }

    public void init(IXMLDOMNode nnode) {
        nnodeMap = (IXMLDOMNamedNodeMap) nnode.getAttributes();
        //nnode.getAttributes().getNamedItem("NoDataForm").getNodeValue()
        Document frmDocument = (Document) nnode.getParentNode();
        IXMLDOMNodeList ndbobj = (IXMLDOMNodeList) frmDocument.getElementsByTagName("RecordSource");
        IXMLDOMNode rnode;
        RecordSourceDO rs;
        while ((rnode = (IXMLDOMNode) ndbobj.nextNode()) != null) {
            rs = new RecordSourceDO(RepositoryName, ApplicationName, FormName, rnode);
            Enumeration rsattrs = rs.findChildren("RSAttribute");
            //Test to see if this records source has attributes (ignore nodata and dups)
            if (rsattrs.hasMoreElements()) {
                this.addChild(rs);
                totalNumFormDataObjects += 1;
            }
        }
        ndbobj = (IXMLDOMNodeList) frmDocument.getElementsByTagName("PickObject");
        while ((rnode = (IXMLDOMNode) ndbobj.nextNode()) != null) {
            totalNumFormPickObjects += 1;
        }
        if (nnodeMap != null) {
            //nnode.getChildNodes().item(2).getAttributes().getNamedItem("Prefix").getNodeValue()
            // for each record source
            //RecordSourceDO rs = new RecordSourceDO(rewposName, appName, formName, nodeMap); // this will map the attributes
            //}
            try {
                //  String cap = getNamedNode("FormCaption", nnode).getChildNodes().item(0).getNodeValue();//    == Customer
                IXMLDOMNode anode;
                //=   (IXMLDOMNode)  nnode.getChildNodes().item(3); //getNamedNode("Caption", nnode);
                childNodes = (IXMLDOMNodeList) nnode.getChildNodes();
                while ((anode = childNodes.nextNode()) != null) {
                    if (anode.getNodeName().equals("Caption"))
                        setCaption(anode.getChildNodes().item(0).getNodeValue());
                }
            } catch (Exception ex) {
            } // TCB - if not found skip
            //nnode.getChildNodes().item(2).getChildNodes().item(0).getNodeValue() == BEGIN. VSForm
            String layout = "";
            //if(nnode.getChildNodes().item(2) != null){
            //    layout = nnode.getChildNodes().item(2).getChildNodes().item(0).getNodeValue();
            //    setFormLayout(layout);
            // }
        }
        //IXMLDOMNode cap = getNamedNode("Description",nnode);
        // if(cap != null)
        //     setDescription(cap.getChildNodes().item(0).getNodeValue());


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

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String caption) {
        setAttrValue("FormCaption", caption);
        Caption = caption;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        setAttrValue("ClassName", className);
        ClassName = className;
    }

    public String getArchetype() {
        return Archetype;
    }

    public void setArchetype(String archetype) {
        setAttrValue("Archetype", archetype);
        Archetype = archetype;
    }

    public String getFormLayout() {
        return FormLayout;
    }

    public void setFormLayout(String formLayout) {
        setAttrValue("FormLayout", formLayout);
        FormLayout = formLayout;
    }

    public int countDataObjects() {
        return totalNumFormDataObjects; //tmb
    }

    public int countPickObjects() {
        return totalNumFormPickObjects; //tmb
    }

    public void write(String dataObjectAbbr, StringBuffer sb) {
        //    if (ValueRequired) {
        //        sb.append("  ASSERT " + dataObjectAbbr + "." + AttrName + " IS NOT NULL");
        //       sb.append("\n");
        //   }
    }
}

