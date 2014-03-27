
/*
 *    Title:         ActionDO
 *    Description:   Event Condition Action definition
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 *
*/
package com.bandsoftware.data;

import com.datachannel.xml.om.IXMLDOMNamedNodeMap;
import com.datachannel.xml.om.IXMLDOMNode;

public class ActionDO extends BusinessObjectDO {
    private String RepositoryName;
    private String DataObjectName;
    private String ActionName;
    private String Description;
    private String ActionToPerform;
    private String Condition;


    public ActionDO() {
        super("Action");
    }

    public ActionDO(String reposName, String doName, String actionName) {
        super("Action");
        setRepositoryName(reposName);
        setDataObjectName(doName);
        setActionName(actionName);
    }

    public ActionDO(String reposName, String doName, IXMLDOMNode node) {
        super("Action");
        setRepositoryName(reposName);
        setDataObjectName(doName);
        //setActionName(actionName);
        init(node);
    }

    private void init(IXMLDOMNode childNode) {
        try {
            IXMLDOMNamedNodeMap nodeMap = (IXMLDOMNamedNodeMap) childNode.getAttributes();
            //if(nodeMap != null){
            //  if(nodeMap.getNamedItem("Name") != null)
            String name = childNode.getParentNode().getAttributes().item(0).getNodeValue();
            if (name != null)
                setActionName(name);
            //}
            // ActionToPerform
            // Condiiton
            IXMLDOMNode nameNode = getNamedNode("Condition", (IXMLDOMNode) childNode.getParentNode());
            if (nameNode != null) {
                db("Conditon = " + nameNode.getChildNodes().item(0).getNodeValue());
                setCondition(nameNode.getChildNodes().item(0).getNodeValue());
            }
            nameNode = getNamedNode("ActionToPerform", childNode);
            if (nameNode != null) {
                db("ActionToPerform = " + nameNode.getChildNodes().item(0).getNodeValue());    //getNodeValue());
                setActionToPerform(nameNode.getChildNodes().item(0).getNodeValue());
            }
            nameNode = getNamedNode("Description", (IXMLDOMNode) childNode);
            if (nameNode != null) {
                db("Description = " + nameNode.getChildNodes().item(0).getNodeValue());
                setDescription(nameNode.getChildNodes().item(0).getNodeValue());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getRepositoryName() {
        return RepositoryName;
    }

    public void setRepositoryName(String reposName) {
        setAttrValue("RepositoryName", reposName);
        RepositoryName = reposName;
    }


    public String getDataObjectName() {
        return DataObjectName;
    }

    public void setDataObjectName(String dataObjectName) {
        setAttrValue("DataObjectName", dataObjectName);
        DataObjectName = dataObjectName;
    }

    public String getActionName() {
        return ActionName;
    }

    public void setActionName(String actionName) {
        setAttrValue("ActionName", actionName);
        ActionName = actionName;
    }

    public String getCondition() {
        return Condition;
    }

    public void setCondition(String condition) {
        setAttrValue("Condition", condition);
        Condition = condition;
    }

    public String getActionToPerform() {
        return ActionToPerform;
    }

    public void setActionToPerform(String actionToPerform) {
        setAttrValue("ActionToPerform", actionToPerform);
        ActionToPerform = actionToPerform;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        setAttrValue("Description", description);
        Description = description;
    }

    public void write(String abbr, StringBuffer sb) {
        sb.append("//Action Name =" + ActionName);
        sb.append("\n");
        sb.append("//Condition =" + strip(this.Condition));
        sb.append("\n");
        sb.append("//Action To Perform: " + ActionToPerform);
        sb.append("\n");
    }
}
