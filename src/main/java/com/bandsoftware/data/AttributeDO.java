/*
 *    Title:         AttributeDO
 *    Description:   Attribute definition
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

public class AttributeDO extends BusinessObjectDO {
    private String RepositoryName;
    private String DataObjectName;
    private String AttrName;
    private boolean Persistent;
    private boolean PreventUserUpdate = false;
    private boolean ValueRequired = false;
    private String DataType;
    private int Size = 0;
    private int Precision = 0;
    private int Scale = 0;
    private String Description;
    private String Caption;
    private String Comments;
    private String Format;
    private String MicroHelp;
    private IXMLDOMNodeList childNodes;
    private IXMLDOMNode childNode;
    private Vector<DerivationDO> derivations = new Vector<DerivationDO>();
    private Vector<ValidationDO> validations = new Vector<ValidationDO>();

    public AttributeDO() {
        super("Attribute");
        //addKey("RepositoryName");
        //addKey("DataObjectName");
        WhereUsedAware = false;
    }

    public AttributeDO(String reposName, String doName, String anAttr) {
        super("Attribute");
        setRepositoryName(reposName);
        setDataObjectName(doName);
        setAttrName(anAttr);
        WhereUsedAware = false;
    }

    public AttributeDO(String reposName, String doName, String anAttr, IXMLDOMNode nnode) {
        super("Attribute");
        setRepositoryName(reposName);
        setDataObjectName(doName);
        setAttrName(anAttr);
        WhereUsedAware = false;
        init(nnode);
    }

    public void init(IXMLDOMNode nnode) {
        IXMLDOMNamedNodeMap nnodeMap = (IXMLDOMNamedNodeMap) nnode.getAttributes();
        if (nnodeMap != null) {
            if (nnodeMap.getNamedItem("Persistent") != null)
                this.setPersistent(nnodeMap.getNamedItem("Persistent").getNodeValue().equalsIgnoreCase("True"));
            if (nnodeMap.getNamedItem("ValueRequired") != null)
                this.setValueRequired(nnodeMap.getNamedItem("ValueRequired").getNodeValue().equalsIgnoreCase("True"));
            if (nnodeMap.getNamedItem("PreventUserUpdates") != null)
                this.setPreventUserUpdate(nnodeMap.getNamedItem("PreventUserUpdates").getNodeValue().equalsIgnoreCase("True"));
        }
        doValidaitons(nnode);
        doDerivations(nnode);
        //String dt = nnode.getChildNodes().item(0).getAttributes().item(0).getNodeValue();
        IXMLDOMNode dt = getNamedNode("DataType", nnode);
        if (dt != null) {
            IXMLDOMNode dt2 = getNamedNode("DataType", dt); // set into object
            if (dt2 != null) {
                this.setDataType(dt2.getAttributes().item(0).getNodeValue());
                if (getNamedNode("Size", dt) != null) {
                    IXMLDOMNode inode = (IXMLDOMNode) getNamedNode("Size", dt);
                    if (inode != null) {
                        String size = inode.getNodeValue();
                        //db("Size = " + size);
                        setSize((new Integer(size)).intValue());
                    }
                }
                if (dt.getAttributes().getNamedItem("Scale") != null) {
                    String scale = dt.getAttributes().getNamedItem("Scale").getNodeValue();
                    //db("Scale = " + scale);
                    setScale((new Integer(scale)).intValue());
                }
            }

            if (dt.getAttributes().getNamedItem("Precision") != null) {
                String size = dt.getAttributes().getNamedItem("Precision").getNodeValue();
                //db("Precision = " + Precision);
                setPrecision((new Integer(Precision)).intValue());
            }
        }
        IXMLDOMNode anode;
        childNodes = (IXMLDOMNodeList) nnode.getChildNodes();
        while ((anode = childNodes.nextNode()) != null) {
            if (anode.getNodeName().equals("Caption"))
                setCaption(anode.getChildNodes().item(0).getNodeValue());
        }
        childNodes = (IXMLDOMNodeList) nnode.getChildNodes();
        while ((anode = childNodes.nextNode()) != null) {
            if (anode.getNodeName().equals("Description"))
                setDescription(anode.getChildNodes().item(0).getNodeValue());
        }
        while ((anode = childNodes.nextNode()) != null) {
            if (anode.getNodeName().equals("Comments"))
                setDescription(anode.getChildNodes().item(0).getNodeValue());
        }
        //db("DataType " + this.getDataType());
        //db("Size "+ getNamedNode("Size", nnode));

    }

    private void doValidaitons(IXMLDOMNode nnode) {
        // db("Validaitons");
        String cvl = null;
        String type = null;
        if (getNamedNode("ValidationType", nnode) != null) {
            type = getNamedNode("ValidationType", nnode).getNodeValue();

            if (type != null && ("CodedValuesList".equalsIgnoreCase(type))) {
                IXMLDOMNode inode = (IXMLDOMNode) getNamedNode("CodedValuesList", nnode);
                if (inode != null)
                    cvl = inode.getNodeValue();
            }

            if (type != null && cvl != null) {
                //db("Type = " + type + " CVL: " + cvl);
                ValidationDO valid = new ValidationDO(RepositoryName, DataObjectName, AttrName);
                valid.setParent(this);
                addValidations(valid);
                valid.setValidationType(type);
                valid.setCodedValueList(cvl);
            }
        }
    }

    private void doDerivations(IXMLDOMNode nnode) {
        // db("Derivations");
        if (nnode.hasChildNodes()) {
            IXMLDOMNodeList aNodeList = (IXMLDOMNodeList) nnode.getChildNodes();
            IXMLDOMNode aNode = getNamedNode("DerivationType", nnode);
            IXMLDOMNode qnode = null;
            IXMLDOMNode fnode = null;
            IXMLDOMNode type = null;
            IXMLDOMNode src = null;
            if (aNode != null) {
                String der = aNode.getNodeValue();
                db("DerivationType = " + der);

                if (!"Default".equalsIgnoreCase(der)) { //&& src  !=null){
                    //db("Type = " + type.getNodeValue() + " derivation " + der); // + " Source: "+ src.getNodeValue());
                    DerivationDO deriv = new DerivationDO(RepositoryName, DataObjectName, AttrName);
                    deriv.setParent(this);
                    addDerivations(deriv);
                    deriv.setDerivationType(der);
                    // need to get default value
                    if (!"none".equalsIgnoreCase(der)) {
                        IXMLDOMNode pnode = (IXMLDOMNode) aNode.getParentNode();
                        deriv.setDefaultValue("");
                        fnode = getNamedNode("Formula", pnode);
                        if (fnode != null) {
                            String formula = fnode.getChildNodes().item(0).getNodeValue();// fnode.getNodeValue();
                            deriv.setFormula(strip(formula));
                        }
                        qnode = getNamedNode("QualificationExpression", pnode);
                        if (qnode != null)
                            deriv.setQualificationExpression(qnode.getChildNodes().item(0).getNodeValue());
                        qnode = getNamedNode("SourceAttribute", pnode);
                        if (qnode != null)
                            deriv.setSourceAttr(qnode.getChildNodes().item(0).getNodeValue());
                        qnode = getNamedNode("SourceDataObject", pnode);
                        if (qnode != null)
                            deriv.setSourceDataObject(qnode.getChildNodes().item(0).getNodeValue());
                        qnode = getNamedNode("SourceAttribute", pnode);
                        if (qnode != null)
                            deriv.setSourceAttr(qnode.getChildNodes().item(0).getNodeValue());
                        qnode = getNamedNode("ParentReplicateIsMaintained", pnode);
                        if (qnode != null)
                            deriv.setParentReplicateIsMaintained("True".equalsIgnoreCase(qnode.getChildNodes().item(0).getNodeValue()));
                    }
                }
            }
        }
    }


    public void testValidations(IXMLDOMNamedNodeMap nnodeMap) {
        //  if (nnodeMap.getNamedItem("ValidationType") != null) {
        //     if (nnodeMap.getNamedItem("ValidationType").getNodeValue().equalsIgnoreCase("Condition")) {
        //         childNodes = (IXMLDOMNodeList) nnode.getChildNodes();
        //        if ((childNode = childNodes.nextNode()) != null) {
        //           if (childNode.getText().length() > 1)
        //                addValidations(new ValidationDO(RepositoryName, DataObjectName, AttrName, childNode));
        //         }
        //    }
        //}
        // find derivations
        // if we have one ... a


    }

    public void testDerivations(IXMLDOMNamedNodeMap nnodeMap) {
        if (nnodeMap != null) {
            if (nnodeMap.getNamedItem("DerivationType") != null) {
                //DerivaitonDO derivDO = new DerivationDO(RepositoryName, DataObjectName, AttrName);
                //addDerivations();
                //numDerivedAttribs++;
                String derivType = nnodeMap.getNamedItem("DerivationType").getNodeValue();
                if (derivType.equalsIgnoreCase("default")) {
                    //numDefaultAttribs++;
                } else if (derivType.equalsIgnoreCase("formula")) {
                    //numFormulaAttribs++;
                } else if (derivType.equalsIgnoreCase("sum")) {
                    //numSumAttribs++;
                } else if (derivType.equalsIgnoreCase("count")) {
                    //numCountAttribs++;
                } else if (derivType.equalsIgnoreCase("parentreplicate")) {
                    //numParentReplicateAttribs++;
                } else if (derivType.equalsIgnoreCase("none")) // Oops -- decrement
                {
                    //numDerivedAttribs--;
                }
            }
        }
    }

    public void testString() {
        //   super.testStringAll();
        // overrride per class
        db("=========Attribute ============");
        db("Parent DO " + ((DataObjectDO) getParent()).getDataObjectName());
        db("Attr Name: " + AttrName);
        db("Persistent " + Persistent);
        db("Value Required " + ValueRequired);
        db("Data Type " + DataType);
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

    public String getMicroHelp() {
        return MicroHelp;
    }

    public void setMicroHelp(String microHelp) {
        MicroHelp = microHelp;
    }

    public String getAttrName() {
        return AttrName;
    }

    public void setAttrName(String attrName) {
        setAttrValue("AttrName", attrName);
        AttrName = attrName;
    }

    public boolean isPersistent() {
        return Persistent;
    }

    public void setPersistent(boolean persistent) {
        setAttrValue("Persistent", persistent == true ?true: false);
        Persistent = persistent;
    }

    public boolean isPreventUserUpdate() {
        return PreventUserUpdate;
    }

    public void setPreventUserUpdate(boolean preventUserUpdate) {
        setAttrValue("PreventUserUpdate", preventUserUpdate == true ? true: false);
        PreventUserUpdate = preventUserUpdate;
    }

    public boolean isValueRequired() {
        return ValueRequired;
    }

    public void setValueRequired(boolean valueRequired) {
        setAttrValue("ValueRequired", valueRequired == true ? true: false);
        ValueRequired = valueRequired;
    }

    public String getDataType() {
        return DataType;
    }

    public String getFullDataType() {
        String sfx = ((Size > 0 && this.dataTypeIsString(this)) ? "(" + Size : "");
        sfx = sfx + ((Precision > 0) ? "," + Precision : "");
        sfx = sfx + ((Precision > 0 && Scale > 0) ? "," + Scale : "");
        if (!sfx.equals(""))
            sfx = sfx + ")";

        return DataType + sfx;
    }

    public void setDataType(String dataType) {
        setAttrValue("DataType", dataType);
        DataType = dataType;
    }

    public int getSize() {
        return Size;
    }

    /*

    */
    public void setSize(int size) {
        setAttrValue("AttrSize", new Integer(size).toString());
        Size = size;
    }

    public int getPrecision() {
        return Precision;
    }

    public void setPrecision(int precision) {
        setAttrValue("Precision1", precision);
        Precision = precision;
    }

    public int getScale() {
        return Scale;
    }

    public void setScale(int scale) {
        setAttrValue("Scale", scale);
        Scale = scale;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        setAttrValue("Description", stripReplace(description));
        Description = description;
    }

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String caption) {
        setAttrValue("Caption", caption);
        Caption = caption;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        setAttrValue("Comments", comments);
        Comments = comments;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        setAttrValue("Format", format);
        Format = format;
    }

    public void addValidations(ValidationDO aValidation) {
        this.validations.add(aValidation);
    }

    public Vector<DerivationDO> getDerivations() {
        return derivations;
    }

    public void addDerivations(DerivationDO aDerivation) {
        this.derivations.add(aDerivation);
    }

    public Vector<ValidationDO> getValidations() {
        return validations;
    }

    public String formatRule() {
        //create a specific syntax for reporting purposes
        // sum(Object, attribute, where)
        // count(Object, where)
        // replicate(Object, where, maintained)
        // formula(expression)
        // default(value)
        StringBuffer sb = new StringBuffer();
        String type = getRuleType();
        if (!"".equals(type))
            sb.append(type);
        //else
        //  sb.append(this.getDefault);
        return sb.toString();
    }

    //override the parent to include these children
    public void recursiveReposVersion(String repositoryVersion) {

        for (DerivationDO dervDO : this.getDerivations()) {
            db(dervDO.getName() + " BO NAME: " + dervDO.getBOName());
            dervDO.setAttrValue("RepositoryVersion", repositoryVersion);
        }
        for (ValidationDO valDO : this.getValidations()) {
            db(valDO.getName() + " BO NAME: " + valDO.getBOName());
            valDO.setAttrValue("RepositoryVersion", repositoryVersion);
        }
    }

    public boolean isValidation_Sum_Or_Fomrula() {
        Enumeration e = getDerivations().elements();
        if (e.hasMoreElements()) {
            DerivationDO valid = (DerivationDO) e.nextElement();
            String dt = valid.getDerivationType();
            if ("Sum".equalsIgnoreCase(dt) ||
                    "Fomrula".equalsIgnoreCase(dt) ||
                    "ParentReplicate".equalsIgnoreCase(dt))
                return true;
        }
        return false;
    }

    private String getRuleType() {
        Enumeration e = getDerivations().elements();
        String dt = "";
        if (e.hasMoreElements()) {
            DerivationDO valid = (DerivationDO) e.nextElement();
            dt = valid.formatRule();
        } else {
            e = getValidations().elements();
            if (e.hasMoreElements()) {
                ValidationDO valid = (ValidationDO) e.nextElement();
                dt = valid.formatRule();
            }
        }

        return dt;
    }

    // Public method to append attribute information to DICE Script
    public void write(String dataObjectAbbr, StringBuffer sb) {
        if (ValueRequired) {
            sb.append("  ASSERT " + dataObjectAbbr + "." + AttrName + " IS NOT NULL");
            sb.append("\n");
        }
    }

    public void writeValidation(String dataObjectAbbr, StringBuffer sb) {
        ValidationDO valDO;
        Enumeration e = getValidations().elements();
        if (Persistent) {
            while (e.hasMoreElements()) {
                valDO = (ValidationDO) e.nextElement();
                valDO.write(dataObjectAbbr, sb);
            }
        }
    }

    public void writeDerivation(String dataObjectAbbr, StringBuffer sb) {
        DerivationDO derDO;
        Enumeration e = getDerivations().elements();
        while (e.hasMoreElements()) {
            derDO = (DerivationDO) e.nextElement();
            derDO.write(dataObjectAbbr, sb);
        }
    }

    public void writeIsAttr(String dataObjectAbbr, StringBuffer sb) {
        sb.append("ASSERT " + dataObjectAbbr + "." + AttrName + " IS NOT NULL");
        newLine(sb);
    }

    public void writeSpecial(String dataObjectAbbr, StringBuffer sb, Enumeration reln_attr) {

        // Also check to make sure this attribute is not used in PKEY
        // or has a relationship for which this is the FKEY
        String relnAttrName;
        String sfx = "";
        String parentRepl = null;
        boolean found = false;

        if (!this.isPersistent()) {
            sb.append(DataObjectName);
            sb.append("\t");
            sb.append(AttrName);
            sb.append("\t");
            sb.append(this.getDataType());
            sb.append("\t");
            sb.append("Non Persistent Attribute");
            sb.append("\t");
            this.newLine(sb);
        } else {
            Vector v = getDerivations();
            if (!v.isEmpty()) {
                DerivationDO deriv = (DerivationDO) v.firstElement();
                if (deriv != null) {
                    if ("ParentReplicate".equalsIgnoreCase(deriv.getDerivationType()))
                        parentRepl = " Used in Parent Relicate";
                }
            }
            if (reln_attr != null) {
                while (reln_attr.hasMoreElements()) {
                    relnAttrName = (String) reln_attr.nextElement();
                    if (relnAttrName.equalsIgnoreCase(AttrName))
                        found = true;
                }
            }
            if (!ValueRequired) {
                sfx = " ALLOWS NULL";
            } else
                sfx = " NOT NULL";
            if (AttrName.endsWith("ID") && !ValueRequired) {
                sb.append(DataObjectName);
                sb.append("\t");
                sb.append(AttrName);
                sb.append("\t");
                sb.append(this.getDataType());
                sb.append("\t");
                sb.append(" ALLOWS NULL values");

                if (found)
                    sb.append(" FKEY used in Relationship");

                if (parentRepl != null)
                    sb.append(parentRepl);

                newLine(sb);
            }
            String attrNameStart = AttrName.substring(0, 2);
            if (!ValueRequired) {
                sfx = " ALLOWS NULL";
            } else
                sfx = " NOT NULL";
            if (("IS".equalsIgnoreCase(attrNameStart) || "Boolean".equalsIgnoreCase(this.getDataType())) && !ValueRequired) {
                sb.append(DataObjectName);
                sb.append("\t");
                sb.append(AttrName);
                sb.append("\t");
                sb.append(this.getDataType());
                sb.append("\t");
                sb.append("ALLOWS NULL VALUES and NOT REQUIRED");

                newLine(sb);
            }
        }
    }
}

