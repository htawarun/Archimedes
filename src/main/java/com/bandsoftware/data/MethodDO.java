
/*
 *    Title:         MethodDO
 *    Description:   This holds the content of each method linked to a specific Object (DataObject, QueryObject, Form)
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 *
*/
package com.bandsoftware.data;

import java.util.Enumeration;

public class MethodDO extends BusinessObjectDO {
    private String RepositoryName;
    private String DataObjectName;
    private String MethodName;
    private String ReturnType;
    private String Signature;
    private String MethodSource;
    private boolean IsPublic = true;
    private String ObjectType;
    private boolean IsUserDefined = false;

    public MethodDO() {
        super("Method");
        WhereUsedAware = false;
    }

    public MethodDO(String reposName, String doName) {
        super("Method");
        setRepositoryName(reposName);
        setDataObjectName(doName);
        setObjectType("DATAOBJECT");
        setPublic(true);
        setUserDefined(true);
        WhereUsedAware = false;
    }

    public MethodDO(String reposName, String doName, String methodName) {
        super("Method");
        setRepositoryName(reposName);
        setDataObjectName(doName);
        setMethodName(methodName);
        setObjectType("DATAOBJECT");
        setPublic(true);
        setUserDefined(true);
        WhereUsedAware = false;
    }

    public MethodDO(String reposName, String doName, String methodName, String objType) {
        super("Method");
        setRepositoryName(reposName);
        setDataObjectName(doName);
        setMethodName(methodName);
        setObjectType(objType);
        setPublic(true);
        setUserDefined(true);
        WhereUsedAware = false;
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
        setAttrValue("ObjectName", dataObjectName);
        DataObjectName = dataObjectName;
    }

    public String getMethodName() {
        return MethodName;
    }

    public void setMethodName(String methodName) {
        setAttrValue("MethodName", methodName);
        MethodName = methodName;
    }

    public String getMethodSource() {
        return MethodSource;
    }

    public void setMethodSource(String methodSource) {
        setAttrValue("MethodSource", methodSource);
        MethodSource = methodSource;
    }

    public String getSignature() {
        return Signature;
    }

    public void setSignature(String signature) {
        setAttrValue("Signature", signature);
        Signature = signature;
    }

    public String getReturnType() {
        return ReturnType;
    }

    public void setReturnType(String returnType) {
        setAttrValue("ReturnType", returnType);
        ReturnType = returnType;
    }

    public void testString() {
        db("Object Name " + DataObjectName);

    }

    public boolean isPublic() {
        return IsPublic;
    }

    public void setPublic(boolean aPublic) {
        setAttrValue("IsPublic", aPublic == true ? "1" : "0");
        IsPublic = aPublic;
    }

    public void setPublic(String aPublic) {
        setPublic(aPublic.equalsIgnoreCase("public"));
    }

    public boolean getUserDefined() {
        return IsUserDefined;
    }

    public void setUserDefined(boolean userDefined) {
        setAttrValue("IsUserDefined", userDefined == true ? "1" : "0");
        IsUserDefined = userDefined;
    }

    public String getObjectType() {
        return ObjectType;
    }

    public void setObjectType(String objectType) {
        setAttrValue("ObjectType", objectType);
        ObjectType = objectType;
    }

    private Enumeration findAttributes() {
        return this.findChildren("Attribute");
    }

    public void writeSpecial(StringBuffer sb) {
        db("================ Start Research for DataObject: " + DataObjectName + " ============");
        newLine(sb);
        sb.append("DataObject: " + DataObjectName);
        newLine(sb);
        String dataObjectAbbr = "$" + createAttrVar(DataObjectName);
        AttributeDO attrDO;
        Enumeration e = findAttributes();
        Enumeration reln_attr = this.findChildRelnAttrs(DataObjectName);
        while (e.hasMoreElements()) {
            attrDO = (AttributeDO) e.nextElement();
            attrDO.writeSpecial(dataObjectAbbr, sb, reln_attr);
        }
    }

    public void write(StringBuffer sb) {
        // Methods do not have any rules to test
    }

    public String getDataObjectNameAndPkey() {
        // ans = ""; // DataObjectName + ".";
        Enumeration e = getChildren("IndexDO");
        DO_Index indx = null;
        DO_IndexAttr attr = null;
        while (e.hasMoreElements()) {
            indx = (DO_Index) e.nextElement();
            if (indx.isPrimary()) {
                Enumeration ee = indx.getChildren("IndexAttr");
                if (ee.hasMoreElements()) {
                    attr = (DO_IndexAttr) ee.nextElement();
                    return attr.getIndexAttrName();
                }
            }
        }
        return "";
    }

    //looking for a specific attribute by name
    public AttributeDO findAttribute(String attrName) {
        AttributeDO attrDO;
        Enumeration e = getChildren("Attribute");
        while (e.hasMoreElements()) {
            attrDO = (AttributeDO) e.nextElement();
            if (attrName.equals(attrDO.getAttrName()))
                return attrDO;
        }
        return null;
    }

    public void writeALL(StringBuffer sb) {

        String secondStr = "1";
        //int indx = DataObjectName.indexOf("_");
        String dataObjectAbbr = "$" + createAttrVar(DataObjectName); // DataObjectName.substring(0, 1) + secondStr;
        newLine(sb);
        sb.append("NOTE CHECK_ALL " + DataObjectName);// + " "+ dataObjectAbbr);
        newLine(sb);
        sb.append("LOOP OVER " + DataObjectName + " INTO " + dataObjectAbbr + " ");
        newLine(sb);
        sb.append("     NOTE " + dataObjectAbbr + "." + getDataObjectNameAndPkey());
        newLine(sb);
        sb.append("          RUN " + DataObjectName + "_GEN.asn");
        newLine(sb);

        newLine(sb);
        sb.append("END LOOP");
        newLine(sb);
        newLine(sb);
        sb.append("UNSET " + dataObjectAbbr);
        newLine(sb);
    }
}
