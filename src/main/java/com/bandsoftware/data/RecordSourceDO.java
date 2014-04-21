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

import java.util.Vector;

public class RecordSourceDO extends BusinessObjectDO {
    private String RepositoryName;
    private String ApplicationName;
    private String BusinessObject;
    private String FormName;
    private String Description;
    private String Caption;
    private String Comments;
    private String AllowInsert;
    private String AllowUpdate;
    private String AllowDelete;
    private String ClassName;
    private String TargetLanguage;
    private String Relationship;
    private IXMLDOMNamedNodeMap nnodeMap;
    private Vector<String> rsAttributes = new Vector<String>();

    public RecordSourceDO() {
        super("RecordSource");
        addKey("TargetLanguage");
    }

    public RecordSourceDO(String reposName, String appName, String formName) {
        super("RecordSource");
        setRepositoryName(reposName);
        setFormName(formName);
        setApplicationName(appName);
    }

    public RecordSourceDO(String reposName, String appName, String formName, IXMLDOMNode nnode) {
        super("RecordSource");
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
        setBusinessObject(nnode.getAttributes().getNamedItem("BusinessObject").getNodeValue());
        if (nnode.getAttributes().getNamedItem("AllowInsert") != null)
            setAllowInsert(nnode.getAttributes().getNamedItem("AllowInsert").getNodeValue());
        if (nnode.getAttributes().getNamedItem("AllowUpdate") != null)
            setAllowUpdate(nnode.getAttributes().getNamedItem("AllowUpdate").getNodeValue());
        if (nnode.getAttributes().getNamedItem("AllowDelete") != null)
            setAllowDelete(nnode.getAttributes().getNamedItem("AllowDelete").getNodeValue());
        if (nnode.getAttributes().getNamedItem("Relationship") != null)
            setRelationship(nnode.getAttributes().getNamedItem("Relationship").getNodeValue());
        // for this recordsource find the RSAttributesMeta and extract the details
        IXMLDOMNode anode = null;
        int i = 0;
        RSAttributesDO rsAttr;
        IXMLDOMNode rsNode = null;
        //nodes include additiona where, orderby, parameterized where and archetype exteneded properties
        while ((anode = (IXMLDOMNode) nnode.getChildNodes().item(i)) != null) {
            i++;
            if (anode.getNodeName().equals("RSAttributesMeta")) {
                rsNode = anode;
                break;
            }
        }
        try {
            if (rsNode != null) {
                i = 0;
                IXMLDOMNodeList nodelist = (IXMLDOMNodeList) rsNode.getChildNodes();
                // there are three kinds of subtags in RSAttributesMeta
                //DefaultAttributes
                //ScalerAttributes
                //GridAttributes
                String scalar = "true";
                boolean created = false;
                if (nodelist != null) {
                    while ((anode = (IXMLDOMNode) nodelist.item(i)) != null) {
                        //nodelist.item(2).getChildNodes().item(0).getAttributes().getNamedItem("UnPlaced").getNodeValue()
                        scalar = ("ScalerAttributes".equals(anode.getNodeName())) ? "1" : "0";
                        // right now I only do scalar or grid but not both (pkey is on business object/attrname
                        if ("ScalerAttributes".equals(anode.getNodeName()) && !created) {
                            created = true;
                            createChildRSAttrs(anode, scalar);
                        }
                        if ("GridAttributes".equals(anode.getNodeName()) && !created) {
                            created = true;
                            createChildRSAttrs(anode, scalar);
                        }
                        //TransitionDO trans = new TransitionDO(  RepositoryName, ApplicationName, FormName, getBusinessObject(), anode);
                        i++;
                    }
                }
            }
        } catch (Exception ex) {
            db("RSAttribute Error");
        } // tcb

        //
        //}
        //doRSAttributes()
        //IXMLDOMNode cap = getNamedNode("Description",nnode);
        // if(cap != null)
        //     setDescription(cap.getChildNodes().item(0).getNodeValue());


    }

    private void createChildRSAttrs(IXMLDOMNode anode, String scalar) {
        int i = 0;
        IXMLDOMNodeList nodelist = (IXMLDOMNodeList) anode.getChildNodes();
        RSAttributesDO rsAttr;
        if (nodelist != null) {
            while ((anode = (IXMLDOMNode) nodelist.item(i)) != null) {
                rsAttr = new RSAttributesDO(RepositoryName, ApplicationName, FormName, anode);
                this.addChild(rsAttr);
                String name = anode.getAttributes().getNamedItem("Name").getNodeValue();
                String placed = anode.getAttributes().getNamedItem("UnPlaced").getNodeValue();
                String disp = anode.getAttributes().getNamedItem("Displayed").getNodeValue();
                rsAttr.setAttrName(name);
                rsAttr.setPlaced(placed);
                rsAttr.setDisplayed(disp);
                rsAttr.setScalar(scalar);
                i++;
            }
        }
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

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String caption) {
        setAttrValue("Caption", caption);
        Caption = caption;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public String getAllowDelete() {
        return AllowDelete;
    }

    public void setAllowDelete(String allowDelete) {
        setAttrValue("AllowDelete", "True".equals(allowDelete) ? "1" : "0");
        AllowDelete = allowDelete;
    }

    public String getAllowUpdate() {
        return AllowUpdate;
    }

    public void setAllowUpdate(String allowUpdate) {
        setAttrValue("AllowUpdate", "True".equals(allowUpdate) ? "1" : "0");
        AllowUpdate = allowUpdate;
    }

    public String getAllowInsert() {
        return AllowInsert;
    }

    public void setAllowInsert(String allowInsert) {
        setAttrValue("AllowInsert", "True".equals(allowInsert) ? "1" : "0");
        AllowInsert = allowInsert;
    }

    public String getComments() {
        return Comments;
    }

    public String getRelationship() {
        return Relationship;
    }

    public void setRelationship(String relationship) {
        setAttrValue("Relationship", relationship);
        Relationship = relationship;
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

    public void write(String dataObjectAbbr, StringBuffer sb) {
        //    if (ValueRequired) {
        //        sb.append("  ASSERT " + dataObjectAbbr + "." + AttrName + " IS NOT NULL");
        //       sb.append("\n");
        //   }
    }

    public void populateWhereUsed(WhereUsedDO wu) {
        //implemented in subclasses
        wu.setSourceObject(this.getParent().getParent()); // Application
        wu.setSourceName(((ApplicationDO) this.getParent().getParent()).getApplicationName());
        wu.setSourceType("APPLICATION"); // QUERYOBJECT, RELN
        DataObjectDO sourceDO = findSourceDO(this.getBusinessObject());
        if (sourceDO != null) {
            wu.setUsedInObject(sourceDO);
        } else {
            wu.setUsedInQueryObject(findSourceQueryDO(this.getBusinessObject()));
        }
        wu.setSourceName(this.getBusinessObject());
        wu.setUsedInType("FORM"); //COUNT, REPL, RELN, FORM
        wu.setUsedInName(this.getApplicationName() + ":" + this.getFormName());
    }

    public DataObjectDO findSourceDO(String sourceName) {
        DataObjectDO sourceDO = null;
        String name = sourceName;
        if (sourceDO == null) {
            RepositoryDO repos = (RepositoryDO) this.getParent().getParent().getParent();
            sourceDO = repos.findDataObject(name);
        }
        return sourceDO;
    }

    public QueryObjectDO findSourceQueryDO(String sourceName) {
        QueryObjectDO sourceDO = null;
        String name = sourceName;
        if (sourceDO == null) {
            RepositoryDO repos = (RepositoryDO) this.getParent().getParent().getParent();
            sourceDO = repos.findQueryObject(name);
        }
        return sourceDO;
    }
}

