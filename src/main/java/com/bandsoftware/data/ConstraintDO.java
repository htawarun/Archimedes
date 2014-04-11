
package com.bandsoftware.data;

/*
 *    Title:         ConstraintDO
 *    Description:   Constraints used by Attributes
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 *
*/

import com.datachannel.xml.om.IXMLDOMNode;

public class ConstraintDO extends BusinessObjectDO {
    private String RepositoryName;
    private String DataObjectName;
    private String ConstraintName;
    private String ConditionType;

    private String _Condiiton;
    private String ErrorMessage;


    public ConstraintDO() {
        super("Constraints");
    }

    public ConstraintDO(String reposName, String doName, IXMLDOMNode childNode) {
        super("Constraint");
        setRepositoryName(reposName);
        setDataObjectName(doName);
        //setConstraintName(constraintName);
        init(childNode);
    }

    public ConstraintDO(String reposName, String doName, String constraintName) {
        super("Constraint");
        setRepositoryName(reposName);
        setDataObjectName(doName);
        setConstraintName(constraintName);
    }

    private void init(IXMLDOMNode childNode) {
        try {

            if (childNode.getParentNode().getChildNodes().item(1) != null) {
                String condiiton = childNode.getParentNode().getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
                this.set_Condiiton(condiiton);
            }
            if (childNode.getParentNode().getChildNodes().item(0) != null) {
                String errmsg = childNode.getParentNode().getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
                this.setErrorMessage(errmsg);
            }
            //IXMLDOMNode nameNode = getNamedNode("Name", (IXMLDOMNode) childNode.getParentNode());
            if (childNode.getParentNode().getAttributes().item(0) != null) {
                db("Constraint Name = " + childNode.getParentNode().getAttributes().item(0).getNodeValue());
                setConstraintName(childNode.getParentNode().getAttributes().item(0).getNodeValue());
            }
            //nameNode = getNamedNode("ConditionType", (IXMLDOMNode) childNode.getParentNode());
            if (childNode.getParentNode().getAttributes().item(1) != null) {
                db("Condition Type = " + childNode.getParentNode().getAttributes().item(1).getNodeValue());
                setConditionType(childNode.getParentNode().getAttributes().item(1).getNodeValue());
            }

            // setCondition();
            //setErrorMessage();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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


    public String getConstraintName() {
        return ConstraintName;
    }

    public void setConstraintName(String constraintName) {
        setAttrValue("ConstraintName", constraintName);
        ConstraintName = constraintName;
    }

    public String getConditionType() {
        return ConditionType;
    }

    public void setConditionType(String conditionType) {
        setAttrValue("ConditionType", conditionType);
        ConditionType = conditionType;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        setAttrValue("ErrorMessage", errorMessage);
        ErrorMessage = errorMessage;
    }

    public String get_Condiiton() {
        return _Condiiton;
    }

    public void set_Condiiton(String _Condiiton) {
        setAttrValue("Condition", _Condiiton);
        this._Condiiton = _Condiiton;
    }

    public void write(String abbr, StringBuffer sb) {
        sb.append("//Contraint Name: " + ConstraintName);
        sb.append("\n");
        sb.append("//Condiiton: " + _Condiiton);
        sb.append("\n");
        sb.append("//Error Msg: " + ErrorMessage);
        sb.append("\n");
    }

}
