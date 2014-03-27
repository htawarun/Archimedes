
package com.bandsoftware.data;

/*
 *    Title:         ValidationDO
 *    Description:   Defines the Attribute Validation properties
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 *
*/

import com.datachannel.xml.om.IXMLDOMNode;

public class ValidationDO extends BusinessObjectDO {
    private String RepositoryName;
    private String DataObjectName;
    private String AttrName;
    private String ValidationType;
    private String Description;
    private String CodedValueList;
    private DataObjectDO CodedValueListDO;


    public ValidationDO() {
        super("Validation");
        addKey("RepositoryName");
        addKey("DataObjectName");
        addKey("AttrName");
    }

    public ValidationDO(String reposName, String doName, String anAttr) {
        super("Validation");
        addKey("RepositoryName");
        addKey("DataObjectName");
        addKey("AttrName");
        setRepositoryName(reposName);
        setDataObjectName(doName);
        setAttrName(anAttr);
    }

    public ValidationDO(String reposName, String doName, String anAttr, IXMLDOMNode node) {
        super("Validation");
        addKey("RepositoryName");
        addKey("DataObjectName");
        addKey("AttrName");
        setRepositoryName(reposName);
        setDataObjectName(doName);
        setAttrName(anAttr);
        init(node);
    }

    private void init(IXMLDOMNode node) {

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

    public void populateWhereUsed(WhereUsedDO wu) {
        //implemented in subclasses
        wu.setSourceObject(this.getParent());
        wu.setSourceName(((DataObjectDO) this.getParent().getParent()).getDataObjectName());
        wu.setSourceType("DATAOBJECT"); // QUERYOBJECT, RELN
        wu.setUsedInObject(findSourceDO(this.getCodedValueList()));
        wu.setSourceName(stripParens(this.getCodedValueList()));
        wu.setUsedInType(getValidationType()); //COUNT, REPL, RELN, FORM
        wu.setUsedInName(this.getAttrName());
    }

    public DataObjectDO findSourceDO(String sourceName) {
        DataObjectDO sourceDO = this.getCodedValueListDO();
        String name = stripParens(sourceName); // Role(ObjectName)
        if (sourceDO == null) {
            RepositoryDO repos = (RepositoryDO) this.getParent().getParent().getParent();
            return repos.findDataObject(name);
        }
        return null;
    }

    private String stripParens(String aName) {
        String newName = aName;
        if (aName.indexOf("(") > 0) {
            newName = aName.substring(aName.indexOf("(") + 1);
            if (newName.indexOf(")") > 0) {
                newName = newName.substring(0, newName.indexOf(")"));
            }
        }
        return newName;
    }

    public String getAttrName() {
        return AttrName;
    }

    public void setAttrName(String attrName) {
        setAttrValue("AttrName", attrName);
        AttrName = attrName;
    }

    public String getValidationType() {
        return ValidationType;
    }

    public void setValidationType(String validationType) {
        setAttrValue("ValidationType", validationType);
        ValidationType = validationType;
    }

    public String getCodedValueList() {
        //This is the name of a parent - let's find it
        return CodedValueList;
    }

    public void setCodedValueList(String codedValueList) {
        setAttrValue("CodedValueList", codedValueList);
        CodedValueList = codedValueList;
    }

    public String getDescription() {
        return Description;
    }

    public DataObjectDO getCodedValueListDO() {
        return CodedValueListDO;
    }

    public void setCodedValueListDO(DataObjectDO codedValueListDO) {
        CodedValueListDO = codedValueListDO;
    }

    public void setDescription(String description) {
        setAttrValue("Description", description);
        Description = description;
    }

    public String formatRule() {
        StringBuffer sb = new StringBuffer();
        sb.append("CodedValueList( ");
        sb.append(CodedValueList);
        sb.append(" )");
        return sb.toString();
    }

    //Note: Create a new relationship object and print
    public void write(String abbr, StringBuffer sb) {
        //Late binding of this object since we need to scan all tables first.
        setCodedValueListDO(findCodedValueListDO(this, CodedValueList));

        if (this.getCodedValueListDO() != null) {
            newLine(sb);
            sb.append("//CodedValueList Table Name: " + CodedValueList);   // name of parent table
            newLine(sb);
            String parentDOName = getCodedValueListDO().getDataObjectName();

            //String abbr = "$" + abbr2;
            String parentAttribute = this.getCodedValueListDO().getDataObjectNameAndPkey();
            String dataObjAbbr = createAttrVar(parentDOName); //  + indx;
            sb.append("IF " + abbr + "." + AttrName + " IS NOT NULL");
            newLine(sb);
            sb.append("  NOTE " + DataObjectName + " " + abbr + "." + AttrName + ", " + dataObjAbbr + "= " + abbr + "." + AttrName);
            newLine(sb);

            // need to test the parentAttriobute for persistence and generate different code if not persisted.
            sb.append("    FETCH FROM  " + CodedValueList + " INTO " + dataObjAbbr + " WHERE " + parentAttribute + " = " + getQuotedDataAttrName(abbr));
            newLine(sb);
            sb.append("    ASSERT EXISTS $" + dataObjAbbr);
            newLine(sb);
            sb.append("END IF");
            newLine(sb);
        }

    }

    private String getQuotedDataAttrName(String abbr) {
        String qt = ""; // (dataTypeIsString((AttributeDO) getParent()))?"'":""; // save for future useage
        return qt + abbr + "." + AttrName + qt;
    }
}
