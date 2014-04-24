
/*
 *    Title:         DerivationDO
 *    Description:   Derivations used by attributes
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 *
*/
package com.bandsoftware.data;

import com.datachannel.xml.om.IXMLDOMNode;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class DerivationDO extends BusinessObjectDO {
    private String RepositoryName;
    private String DataObjectName;
    private String AttrName;
    private String DerivationType = null;  // sum,count, parent replicate, formula, default
    private String DefaultValue = null;
    private String Formula = null;
    private String QualificationExpression = null;
    private String SourceAttr;
    private boolean ParentReplicateIsMaintained = true;
    private String RelnSurrid; // used to identify which relationship is used to enforce Sum/Count/Replicate
    private String SourceDataObject;  //format: roleName(dataObjectName)
    private String relationshipRole;
    private DataObjectDO sourceDO;

    public DerivationDO() {
        super("Derivation");
        addKey("RepositoryName");
        addKey("DataObjectName");
        addKey("AttrName");
    }

    public DerivationDO(String reposName, String doName, String anAttr) {
        super("Derivation");
        addKey("RepositoryName");
        addKey("DataObjectName");
        addKey("AttrName");
        setRepositoryName(reposName);
        setDataObjectName(doName);
        setAttrName(anAttr);
    }

    public DerivationDO(String reposName, String doName, String anAttr, IXMLDOMNode node) {
        super("Derivation");
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

    public String getQualificationExpression() {
        return QualificationExpression;
    }

    public void setQualificationExpression(String qualificationExpression) {
        setAttrValue("QualificationExpression", qualificationExpression);
        QualificationExpression = qualificationExpression;
    }

    public String getFormula() {
        return Formula;
    }

    public void setFormula(String formula) {
        setAttrValue("Formula", formula);
        Formula = formula;
    }

    public String getDefaultValue() {
        return DefaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        setAttrValue("DefaultValue", defaultValue);
        DefaultValue = defaultValue;
    }

    public String getDerivationType() {
        return DerivationType;
    }

    public void setDerivationType(String derivationType) {
        setAttrValue("DerivationType", derivationType);
        DerivationType = derivationType;
    }

    public String getAttrName() {
        return AttrName;
    }

    public void setAttrName(String attrName) {
        setAttrValue("AttrName", attrName);
        AttrName = attrName;
    }

    public String getSourceAttr() {
        return SourceAttr;
    }

    public void setSourceAttr(String sourceAttr) {
        setAttrValue("SourceAttr", sourceAttr);
        SourceAttr = sourceAttr;
    }

    public boolean isParentReplicateIsMaintained() {
        return ParentReplicateIsMaintained;
    }

    public void setParentReplicateIsMaintained(boolean parentReplicateIsMaintained) {
        setAttrValue("ParentReplicateIsMaintained", true == parentReplicateIsMaintained ? "1" : "0");
        ParentReplicateIsMaintained = parentReplicateIsMaintained;
    }

    public String getRelnSurrid() {
        return RelnSurrid;
    }

    public void setRelnSurrid(String relnSurrid) {
        RelnSurrid = relnSurrid;
    }

    public String getSourceDataObject() {
        return SourceDataObject;
    }

    /**
     * from metadata roleName(ObjectName) - need to split apart
     *
     * @param sourceDataObject
     */
    public void setSourceDataObject(String sourceDataObject) {
        int idx = (sourceDataObject.indexOf("(") > 0) ? sourceDataObject.indexOf("(") : sourceDataObject.length();
        String relnDO = sourceDataObject.substring(0, idx);
        String realDO = sourceDataObject.substring(idx + 1, sourceDataObject.length() - 1);
        setAttrValue("SourceDataObject", realDO);
        setRelationshipRole(relnDO);
        SourceDataObject = realDO;
    }

    public DataObjectDO getSourceDO() {
        return sourceDO;
    }

    public void setSourceDO(DataObjectDO sourceDO) {
        this.sourceDO = sourceDO;
    }

    public void populateWhereUsed(WhereUsedDO wu) {
        //implemented in subclasses
        wu.setSourceObject(this.getDataObjectParent());
        wu.setSourceName(this.getDataObjectParent().getDataObjectName());
        wu.setSourceType("DATAOBJECT"); // QUERYOBJECT, RELN
        wu.setUsedInObject(findSourceDO(this.getSourceDataObject()));
        // wu.setSourceName(stripParens(this.getSourceDataObject()));
        wu.setUsedInType(this.getDerivationType()); //COUNT, REPL, RELN, FORM
        if (!"COUNT".equals(this.getDerivationType()))
            wu.setUsedInName(this.getSourceAttr());
        else
            wu.setUsedInName("");
    }

    public DataObjectDO findSourceDO(String sourceName) {
        DataObjectDO sourceDO = this.getSourceDO();
        String name = stripParens(sourceName); // Role(ObjectName)
        if (sourceDO == null) {
            RepositoryDO repos = (RepositoryDO) this.getDataObjectParent().getParent();
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

    public String formatRule() {

        StringBuffer sb = new StringBuffer();
        sb.append(this.getDerivationType());
        sb.append("(");
        if ("Count".equalsIgnoreCase(DerivationType)) {
            sb.append(this.getSourceDataObject());
            sb.append(", ");
            sb.append(this.getWhereClause());
        }
        if ("Sum".equalsIgnoreCase(DerivationType)) {
            sb.append(this.getSourceDataObject());
            sb.append(", ");
            sb.append(this.getSourceAttr());
            sb.append(", ");
            sb.append(this.getWhereClause());
        }
        if (Formula != null) {
            sb.append(this.strip(this.getFormula()));
        }
        if ("ParentReplicate".equalsIgnoreCase(DerivationType)) {
            sb.append(this.getSourceDataObject());
            sb.append(", ");
            sb.append(this.getSourceAttr());
            sb.append(", ");
            sb.append(this.isParentReplicateIsMaintained() ? "Maintained" : "Not Maintained");
        }
        sb.append(")");
        return sb.toString();
    }

    public void transformRules(StringBuffer sb) {

        write("", sb);
    }

    public void write(String abbr, StringBuffer sb) {

        //sb.append("//Derivation Type: "+ DerivationType);
        if (getSourceDataObject() != null && getSourceDO() == null) {
            setSourceDO(findSourceDO(this, stripParensDO(getSourceDataObject()))); // Late binding requires doing this after all objects parsed
            //sb.append("//PR Source DO : "+ getSourceDO().getDataObjectName());
            newLine(sb);
        }
        sb.append("\n");
        if (DefaultValue != null) {
            //sb.append("//Default Value: "+ DefaultValue );
            //sb.append("\n");
        }
        if ("Count".equalsIgnoreCase(DerivationType)) {
            writeCount(abbr, sb);
        }
        if ("Sum".equalsIgnoreCase(DerivationType)) {
            writeSum(abbr, sb);
        }
        if (Formula != null) {
            writeFormula(abbr, sb);
        }
        if ("ParentReplicate".equalsIgnoreCase(DerivationType)) {
            writePR(abbr, sb);
        }

    }

    private void writeSum(String abbr, StringBuffer sb) {
        //sb.append("//ValidationType "+ ValidationType);
        newLine(sb);
        sb.append("//SUM on AttrName " + AttrName);
        newLine(sb);
        AttributeDO parentAttrDO = (AttributeDO) getParent();
        String datatype = parentAttrDO.getDataType();
        //Make sure currency fields are initialized and not ull
        String dblqt = "\"";
        if ("Currency".equals(datatype)) {
            newLine(sb);
            sb.append("ASSERT " + abbr + "." + AttrName + " IS NOT NULL");
            newLine(sb);
            //sb.append("ASSERT " + abbr + AttrName + " >= 0");
        }
        if (getSourceDO() != null) {
            String sourceDOName = stripParensDO(SourceDataObject);
            String sourceDOPkeyAttrName = this.getSourceDO().getDataObjectNameAndPkey();
            String parentPkeyAttrName = getDataObjectParent().getDataObjectNameAndPkey();
            String relnName = DataObjectName + "_JN_" + sourceDOName;
            RelationshipDO reln = findReln(relnName);
            String childFkeyAttrNanme = "";
            String prefix = "";
            if (expressionIsClean(QualificationExpression))
                prefix = "//";

            if (!"Currency".equals(datatype)) {
                sb.append("ASSERT " + abbr + "." + AttrName + " IS NOT NULL");
                newLine(sb);
            }
            sb.append("IF " + abbr + "." + AttrName + " IS NOT NULL");
            newLine(sb);
            sb.append(prefix + "    ASSERT " + abbr + "." + AttrName + " = sum(" + dblqt + sourceDOName + dblqt + "," + dblqt + SourceAttr + dblqt + ",");

            if (reln != null) {
                sb.append(dblqt);
                Vector vc = reln.getChildAttributes();
                Vector vp = reln.getParentAttributes();
                for (int i = 0; i < vc.size(); i++) {
                    childFkeyAttrNanme = (String) vc.elementAt(i);
                    parentPkeyAttrName = (String) vp.elementAt(i);

                    sb.append(childFkeyAttrNanme + " = " + abbr + "." + parentPkeyAttrName);
                    if (vc.size() > 1 && i + 1 < vc.size()) {
                        sb.append(" AND ");
                    }
                }
            }
            if (QualificationExpression != null)
                sb.append(" AND " + strip(QualificationExpression));
            sb.append(dblqt + ")");
            newLine(sb);
            sb.append("END IF");
            newLine(sb);
        }
        newLine(sb);
    }

    private void writeCount(String abbr, StringBuffer sb) {
        //sb.append("//ValidationType "+ ValidationType);
        // newLine(sb);
        // Need to know the relationship to naviagate to child and the attribute to use

        // check $Bank.Cnt_Bank_Branch
        //ASSERT $bank.Cnt_Bank_Branch = count("Bank_Branch WHERE Bank_ID = $bank.Bank_ID")
        String dblqt = "\"";
        sb.append("//COUNT on AttrName " + AttrName);
        newLine(sb);

        if (getSourceDO() != null) {
            String sourceDOName = stripParensDO(SourceDataObject);
            String sourceDOPkeyAttrName = this.getSourceDO().getDataObjectNameAndPkey();
            String parentPkeyAttrName = getDataObjectParent().getDataObjectNameAndPkey();
            // need to find reln for
            String relnName = DataObjectName + "_JN_" + sourceDOName;
            RelationshipDO reln = findReln(relnName);
            String childFkeyAttrNanme = "";

            String prefix = "";
            if (expressionIsClean(QualificationExpression))
                prefix = "//";
            sb.append("ASSERT " + abbr + "." + AttrName + " IS NOT NULL");
            newLine(sb);
            sb.append("IF " + abbr + "." + AttrName + " IS NOT NULL AND " + abbr + "." + AttrName + " > 0");
            newLine(sb);
            sb.append(prefix + "    ASSERT " + abbr + "." + AttrName + " = count(" + dblqt + sourceDOName + dblqt);
            sb.append(", ");
            if (reln != null) {
                sb.append(dblqt);
                Vector vc = reln.getChildAttributes();
                Vector vp = reln.getParentAttributes();
                for (int i = 0; i < vc.size(); i++) {
                    childFkeyAttrNanme = (String) vc.elementAt(i);
                    parentPkeyAttrName = (String) vp.elementAt(i);

                    sb.append(childFkeyAttrNanme + " = " + abbr + "." + parentPkeyAttrName);
                    if (vc.size() > 1 && i + 1 < vc.size()) {
                        sb.append(" AND ");
                    }
                }
            }
            if (QualificationExpression != null) {
                sb.append(", ");
                sb.append(" AND" + strip(QualificationExpression));
            }
            sb.append(dblqt + ")");
            newLine(sb);
            sb.append("END IF");
            newLine(sb);
        }
    }

    private void writeFormula(String abbr, StringBuffer sb) {
        //sb.append("//ValidationType "+ ValidationType);
        sb.append("//ASSERT Formula on Attribute: " + AttrName + " formulaValue is: " + strip(Formula));
        newLine(sb);
        if (QualificationExpression != null) {
            sb.append("//QualificationExpression: " + QualificationExpression);
            newLine(sb);
        }
    }

    private void writePR(String abbr, StringBuffer sb) {

        // Write out the things we nned for a ParentReplicate lookup
        if (getSourceDO() != null) {

            String parentDOName = getSourceDO().getDataObjectName();

            String dataObjAbbr2 = createAttrVar(AttrName);
            String srcDOName = stripParensDO(getSourceDataObject());

            String parentAttrName = this.getSourceDO().getDataObjectNameAndPkey();

            DataObjectDO myParentDO = getDataObjectParent(); //this.findDataObject(DataObjectName);
            AttributeDO parentAttrDO = getSourceDO().findAttribute(SourceAttr);
            String myParentPkeyName = myParentDO.getDataObjectNameAndPkey();
            AttributeDO attrDO = (AttributeDO) getParent();
            String dataObjAbbr = createAttrVar(myParentDO.getDataObjectName()); //  + indx;
            //& ParentReplicateIsMaintained Information
            //String abbr = "$" + abbr2;
            //sb.append("//Parent Replicate " + srcDOName + " Source Attr: "+ SourceAttr);
            String parentAttribute = this.getSourceDO().getDataObjectNameAndPkey();
            sb.append("//Parent Replicate: is maintained? " + ParentReplicateIsMaintained);
            newLine(sb);
            // Added this code to prevent type mismatch errors - need special report in future
            String prefix = "";
            if (parentAttrDO == null) {
                db("***********Parent Attribute " + parentAttrName + " not found on Object " + myParentDO.getDataObjectName() + " *****");
                return;
            }
            if (!parentAttrDO.isPersistent()) {
                sb.append("//***********Parent Replicate using Parent Attribute " + parentAttrName + " is not a Persistent Attribute *****");
                newLine(sb);
                prefix = "//";
            }
            if (!attrDO.getFullDataType().equals(parentAttrDO.getFullDataType())) {
                if ("Autonumber".equals(attrDO.getDataType()) ||
                        "Autonumber".equals(parentAttrDO.getDataType())) {
                    db("Attribute or Parent is Autonumber");
                } else {
                    this.writeDataTypeCheck(sb, attrDO, parentAttrDO);
                    prefix = "//";
                }
            }

            sb.append(prefix + "IF " + abbr + "." + AttrName + " IS NOT NULL");
            newLine(sb);
            sb.append(prefix + " NOTE " + DataObjectName + " " + abbr + "." + myParentPkeyName + " , " + abbr + "." + AttrName + " , " + dataObjAbbr2 + "= " + abbr + "." + AttrName);
            newLine(sb);
            sb.append(prefix + "    FETCH FROM " + srcDOName + " INTO " + dataObjAbbr2);
            sb.append(" WHERE " + getSourceAttr() + " = " + getQuotedDataAttrName(abbr));
            sb.append(" AND " + parentAttribute + "= " + abbr + "." + parentAttrName);
            newLine(sb);
            sb.append(prefix + "    ASSERT EXISTS $" + dataObjAbbr2);
            newLine(sb);
            sb.append(prefix + "END IF");
            newLine(sb);

        }
    }

    private String getQuotedDataAttrName(String abbr) {
        String qt = ""; // (dataTypeIsString((AttributeDO) getParent()))?"'":"";
        return qt + abbr + "." + AttrName + qt;
    }

    private String getQuotedDataAttrName(String abbr, AttributeDO attrDO) {
        String qt = ""; // (dataTypeIsString((AttributeDO) attrDO))?"'":"";
        return qt + abbr + "." + attrDO.getAttrName() + qt;
    }

    private DataObjectDO getDataObjectParent() {
        return (DataObjectDO) getParent().getParent();
    }

    public void writeDataTypeCheck(StringBuffer sb, AttributeDO anAttr, AttributeDO anAttr2) {
        if (!anAttr.getFullDataType().equals(anAttr2.getFullDataType())) {
            sb.append("// Attribute Type Mismatch " + anAttr.getAttrName() + " (" + anAttr.getFullDataType() + ") " + " and " + anAttr2.getAttrName() + " (" + anAttr2.getFullDataType() + ") ");
            newLine(sb);
        }
    }

    private RelationshipDO findReln(String relName) {
        DataObjectDO dataObj = (DataObjectDO) getParent().getParent(); // attr.dataobj
        Enumeration e = dataObj.getParentReln().elements();
        RelationshipDO relnDO;
        while (e.hasMoreElements()) {
            relnDO = (RelationshipDO) e.nextElement();
            if (relName.equalsIgnoreCase(relnDO.getRelationshipName()))
                return relnDO;
        }
        return null;
    }

    public boolean expressionIsClean(String expression) {
        //if expression has get... or ends with () or date() or user() or 'True' or 'False' then return false;
        if (expression != null) {
            if (expression.indexOf(" get") > 0 ||
                    (expression.indexOf("()") > 0) ||
                    (expression.indexOf("user()") > 0) ||
                    (expression.indexOf("date()") > 0) ||
                    (expression.indexOf("true") > 0) ||
                    (expression.indexOf("*/") > 0) ||
                    (expression.indexOf(".get") > 0) ||
                    (expression.indexOf(" is") > 0) ||
                    (expression.indexOf("false") > 0))
                return true;
        }
        return false;
    }

    public String getRelationshipRole() {
        return relationshipRole;
    }

    public void setRelationshipRole(String relationshipRole) {
        setAttrValue("RelationshipRole", relationshipRole);
        this.relationshipRole = relationshipRole;
    }

    public void createEspressoRule(EspressoRuleObjectImpl ruleObject) {
        if (getDerivationType().equals("Sum")) {
            ruleObject.setAttrList(getAttrList());
            ruleObject.createSum(getSourceDataObject(), getQualificationExpression(), getSourceAttr());
        } else {
            if (getDerivationType().equals("Count")) {
                ruleObject.setAttrList(getAttrList());
                ruleObject.createCount(getSourceDataObject(), getQualificationExpression());
            } else {
                if (getDerivationType().equals("Formula")) {
                    ruleObject.setAttrList(getAttrList());
                    ruleObject.createFormula(getFormula());
                } else {
                    if (getDerivationType().equals("ParentReplicate")) {
                        ruleObject.createParentCopy(findRealRelnFromRole(relationshipRole), this.SourceAttr);
                    } else {
                        ruleObject = null;
                    }
                }
            }
        }
    }

    private String findRealRelnFromRole(String relationshipRole) {
        String relationshipName = relationshipRole;
        RelationshipDO aRelationshipDO;
       Enumeration e =  getDataObjectRoot().getParent().findChildren("Relationship");
        while( e.hasMoreElements()){
            aRelationshipDO = (RelationshipDO) e.nextElement();
            if(aRelationshipDO.getParentRoleName().equalsIgnoreCase(relationshipRole)){
                relationshipName = aRelationshipDO.getRelationshipName();
                break;
            }
        }
        return relationshipName;
    }
}
