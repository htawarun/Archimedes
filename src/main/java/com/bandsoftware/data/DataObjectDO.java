/*
 *    Title:         DataObjectDO
 *    Description:   Data Object is the main element to hold attributes, relationships and rules
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
import com.sun.javafx.collections.transformation.SortedList;

import javax.swing.*;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class DataObjectDO extends BusinessObjectDO {
    private String RepositoryName;
    private String DataObjectName;
    private String InternalPathName;
    private boolean isCodedValueList = false;
    private String Alias;
    private String SuperClass;
    private String XdaConnector;
    private String DTDVersion;
    private String SingularCaption;
    private String PluralCaption;
    private String Description;
    private IXMLDOMNamedNodeMap nnodeMap;
    private Vector<RelationshipDO> parentReln = new Vector<RelationshipDO>();
    private Vector<RelationshipDO> childReln = new Vector<RelationshipDO>();
    private Vector<AttributeDO> persistentAttrs = null;
    public int totalUserMethodCount;
    public int totalGenMethodCount;

    IXMLDOMNodeList ndbobj;
    IXMLDOMNode nnode;
    IXMLDOMNodeList childNodes;
    IXMLDOMNode childNode;


    public DataObjectDO() {
        super("DataObject");
    }

    public DataObjectDO(String reposName, String doName) {
        super("DataObject");
        setRepositoryName(reposName);
        setDataObjectName(doName);
    }

    public DataObjectDO(String reposName, String doName, IXMLDOMNodeList nodeList) {
        super("DataObject");
        setRepositoryName(reposName);
        setDataObjectName(doName);
        createAttributes(nodeList);
        //display(nodeList);
    }

    public DataObjectDO(String reposName, String doName, Document dboDocument) {
        super("DataObject");
        setRepositoryName(reposName);
        setDataObjectName(doName);
        init(dboDocument);
    }

    public void createAttributes(IXMLDOMNodeList nodeList) {
        //loop through the nodelist and add attributes

    }

    public void createIndexes(Document dboDocument) {
        ndbobj = (IXMLDOMNodeList) dboDocument.getElementsByTagName("Index");
        if (ndbobj != null) {
            if ((nnode = ndbobj.nextNode()) != null) {
                nnodeMap = (IXMLDOMNamedNodeMap) nnode.getAttributes();
                if (nnodeMap != null) {
                    DO_Index indx = new DO_Index(this.RepositoryName, this.DataObjectName, nnodeMap.getNamedItem("Name").getNodeValue());
                    indx.setPrimary("True".equals(nnodeMap.getNamedItem("Primary").getNodeValue()));
                    indx.setUnique("True".equals(nnodeMap.getNamedItem("Unique").getNodeValue()));
                    indx.setIgnoreNulls("True".equals(nnodeMap.getNamedItem("IgnoreNulls").getNodeValue()));
                    this.addChild(indx);
                    int i = 0;
                    DO_IndexAttr attr;
                    while (nnode.getChildNodes().item(i) != null) {
                        attr = new DO_IndexAttr(this.RepositoryName, this.DataObjectName, nnodeMap.getNamedItem("Name").getNodeValue());
                        attr.setIndexAttrName(nnode.getChildNodes().item(i).getAttributes().getNamedItem("Name").getNodeValue());
                        indx.addChild(attr);
                        i++;
                    }
                }
            }
        }
    }

    public void init(Document dboDocument) {
        ndbobj = (IXMLDOMNodeList) dboDocument.getElementsByTagName("DataObject");
        if (ndbobj != null) {
            if ((nnode = ndbobj.nextNode()) != null) {
                nnodeMap = (IXMLDOMNamedNodeMap) nnode.getAttributes();
                if (nnodeMap != null) {
                    setSuperClass(nnodeMap.getNamedItem("SuperClass").getNodeValue());
                    setCodedValueList(nnodeMap.getNamedItem("CodedValuesList").getNodeValue().equalsIgnoreCase("true"));
                }
                //db("DataObject...");
                //display(ndbobj);
                //These are in sub tags
                //IXMLDOMNode n = (IXMLDOMNode) nnodeMap.getNamedItem("SingularCaption");
                IXMLDOMNode n = (IXMLDOMNode) nnode.getChildNodes().item(2).getChildNodes().item(0);
                if (n != null)
                    setSingularCaption(n.getNodeValue());
                n = (IXMLDOMNode) nnode.getChildNodes().item(3).getChildNodes().item(0);
                if (n != null)
                    setPluralCaption(n.getNodeValue());
                n = (IXMLDOMNode) nnode.getChildNodes().item(4).getChildNodes().item(0);
                if (n != null)
                    setDescription(n.getNodeValue());


            }
        }
        ndbobj = (IXMLDOMNodeList) dboDocument.getElementsByTagName("Index");
        if (ndbobj != null) {
            if ((nnode = ndbobj.nextNode()) != null) {
                nnodeMap = (IXMLDOMNamedNodeMap) nnode.getAttributes();
                if (nnodeMap != null) {
                    DO_Index indx = new DO_Index(this.RepositoryName, this.DataObjectName, nnodeMap.getNamedItem("Name").getNodeValue());
                }
            }
        }
    }

    public void setMethodCount(int cnt, boolean userDefined) {
        if (userDefined == true) {
            this.totalUserMethodCount = cnt;
        } else {
            this.totalGenMethodCount = cnt;
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

    public boolean isCodedValueList() {
        return isCodedValueList;
    }

    public void setCodedValueList(boolean codedValueList) {
        setAttrValue("isCodedValueList", (codedValueList == true) ? "1" : "0");
        isCodedValueList = codedValueList;
    }

    public String getAlias() {
        return Alias;
    }

    public void setAlias(String alias) {
        setAttrValue("Alias", alias);
        Alias = alias;
    }

    public String getSuperClass() {
        return SuperClass;
    }

    public void setSuperClass(String superClass) {
        setAttrValue("SuperClass", superClass);
        SuperClass = superClass;
    }

    public String getXdaConnector() {
        return XdaConnector;
    }

    public void setXdaConnector(String xdaConnector) {
        setAttrValue("XdaConnector", xdaConnector);
        XdaConnector = xdaConnector;
    }

    public String getDTDVersion() {
        return DTDVersion;
    }

    public void setDTDVersion(String DTDVersion) {
        setAttrValue("DTDVersion", DTDVersion);
        this.DTDVersion = DTDVersion;
    }

    public String getSingularCaption() {
        return SingularCaption;
    }

    public void setSingularCaption(String singularCaption) {
        setAttrValue("SingularCaption", singularCaption);
        SingularCaption = singularCaption;
    }

    public String getPluralCaption() {
        return PluralCaption;
    }

    public void setPluralCaption(String pluralCaption) {
        setAttrValue("PluralCaption", pluralCaption);
        PluralCaption = pluralCaption;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        setAttrValue("Description", stripReplace(description));
        Description = description;
    }

    public String getInternalPathName() {
        return InternalPathName;
    }

    public void setInternalPathName(String internalPathName) {
        setAttrValue("InternalPathName", internalPathName);
        InternalPathName = internalPathName;
    }

    public String getKeyAttrName() {
        // need to write something to get the PKEY DO_Index and DO_IndexAttr attrName
        return "";
    }

    public Vector<RelationshipDO> getParentReln() {
        return parentReln;
    }

    public void addParentReln(RelationshipDO aParentReln) {
        //db(DataObjectName + " Parent Reln added " + aParentReln.getRelationshipName());
        this.parentReln.add(aParentReln);
    }

    public Vector<RelationshipDO> getChildReln() {
        return childReln;
    }

    public void addChildReln(RelationshipDO aChildReln) {
        //db(DataObjectName + " Child Reln added " + aChildReln.getRelationshipName());
        this.childReln.add(aChildReln);
    }

    public void setGroup(String grpName) {
        // parse name space to get group and parent
        String thisGroupName = "";
        String rootGroup = null;
        String parentGroup = null;
        StringTokenizer st = new StringTokenizer(grpName, "/");
        if (st.countTokens() > 0) {
            thisGroupName = st.nextToken();
            while (st.hasMoreTokens()) {
                rootGroup = parentGroup;
                parentGroup = thisGroupName;
                thisGroupName = st.nextToken();  // only do the last level and parent
            }
            // s/b for each level create a group
            GroupDO group = new GroupDO(RepositoryName, DataObjectName, parentGroup);
            group.setParentGroupName(rootGroup);
            getParent().addChild(group); // add this object to repos root
            // link this data object to this group name
            Object_JN_Group ojg = new Object_JN_Group(RepositoryName, parentGroup, DataObjectName);
            ojg.setObjectType("DATAOBJECT");
            group.addChild(ojg);
            // next
        }
    }

    public void testString() {
        db("Object Name " + DataObjectName);
        db("isCodedValueList Name " + isCodedValueList);
        db("Alias Name " + Alias);
        db("SuperClass Name " + SuperClass);
        db("SingularCaption Name " + SingularCaption);
        db("PluralCaption Name " + PluralCaption);
        db("Description Name " + Description);
        db("Parent Relns .");
        Enumeration e = parentReln.elements();
        String str = "";
        while (e.hasMoreElements()) {
            RelationshipDO obj = (RelationshipDO) e.nextElement();
            str = str + " : " + obj.getRelationshipName();
        }
        db(str);
        db("Child Relns .");
        e = childReln.elements();
        str = "";
        while (e.hasMoreElements()) {
            RelationshipDO obj = (RelationshipDO) e.nextElement();
            str = str + ": " + obj.getRelationshipName();
        }
        db(str);
        //super.testString();

    }

    private Enumeration findAttributes() {
        return this.findChildren("Attribute");
    }

    public List<AttributeDO> findAttributeList() {
        List<AttributeDO> attrList = new Vector<AttributeDO>();
         Enumeration e = this.findChildren("Attribute");
        while(e.hasMoreElements()){
            attrList.add((AttributeDO)e.nextElement());
        }
        return attrList;
    }

    public List<ConstraintDO> findConstraintsList() {
        List<ConstraintDO> constraintList = new Vector<ConstraintDO>();
        Enumeration e = this.findChildren("Constraints");
        while(e.hasMoreElements()){
            constraintList.add((ConstraintDO)e.nextElement());
        }
        return constraintList;
    }

    public List<ActionDO> findActionList() {
        List<ActionDO> actionDOList = new Vector<ActionDO>();
        Enumeration e = this.findChildren("Constraints");
        while(e.hasMoreElements()){
            actionDOList.add((ActionDO)e.nextElement());
        }
        return actionDOList;
    }

    public void createWhereUsed() {
        ValidationDO val;
        DerivationDO der;
        WhereUsedDO wu;
        AttributeDO anAttr;
        for (Enumeration a = this.findChildren("Attribute"); a.hasMoreElements(); ) {
            anAttr = (AttributeDO) a.nextElement();
            for (Enumeration ev = anAttr.getValidations().elements(); ev.hasMoreElements(); ) {
                val = (ValidationDO) ev.nextElement();
                wu = new WhereUsedDO(this.RepositoryName);
                val.populateWhereUsed(wu);
                addWhereUsed(wu);
            }
            for (Enumeration ed = anAttr.getDerivations().elements(); ed.hasMoreElements(); ) {
                der = (DerivationDO) ed.nextElement();
                if (der.getDerivationType().equals("Sum") ||
                        der.getDerivationType().equals("Count") ||
                        der.getDerivationType().equals("ParentReplicate")) {
                    wu = new WhereUsedDO(this.RepositoryName);
                    der.populateWhereUsed(wu);
                    addWhereUsed(wu);
                }
            }
        }
    }

    private void addWhereUsed(WhereUsedDO wu) {
        //need to find target object to hook this where used to.
        DataObjectDO source = (DataObjectDO) wu.getUsedInObject();
        if (source != null) {
            if (source.usedBy == null)
                source.usedBy = new Vector();
            source.usedBy.add(wu);
            db("adding whereUsed to " + source.getDataObjectName() + " wu: " + wu.toString());
        }
    }

    private Enumeration findPersistentAttrs() {
        if (persistentAttrs == null) {
            Enumeration e = findAttributes();
            persistentAttrs = new Vector<AttributeDO>();
            AttributeDO attrDO;
            while (e.hasMoreElements()) {
                attrDO = (AttributeDO) e.nextElement();
                if (attrDO.isPersistent())
                    persistentAttrs.addElement(attrDO);
            }
        }
        return persistentAttrs.elements();
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

    public void writeXML(StringBuffer sb, int scriptID) {

        sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
        sb.append("<il:Script il:ID='Script" + genScriptID(scriptID) + "'\n");
        sb.append("il:Status='Ready il:TopLevel='true'\n");
        sb.append("xmlns:il='http://www.integrity-logic.com/Euclid1.0/XMLSchema'\n");
        sb.append("xmlns:ilc='http://www.integrity-logic.com/Euclid1.0/XMLSchema/Common'\n");
        sb.append("xmlns:xlink='http://www.w3.org/1999/xlink'\n");
        sb.append("xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n");
        sb.append("xsi:schemaLocation='http://www.integrity-logic.com/Euclid1.0/XMLSchema Script.xsd'>\n");

        sb.append("<il:Name>'" + this.getDataObjectName() + " script generated by Archidemes</il:Name>\n");
        sb.append("<il:Description>This is a generated script !</il:Description>\n");
        sb.append("<il:NextAvailableID>1</il:NextAvailableID>\n");

        sb.append("</il:Script>\n");


    }

    private String genScriptID(int id) {
        int idx = (id > 9) ? 12 : 11;
        int strt = idx == 12 ? 0 : 1;
        return ("00000000000" + id).substring(strt, idx);

    }

    public void write(StringBuffer sb) {
        db("================ Start Write for file " + DataObjectName + "_GEN.asn ============");
        //this.testString();

        //String secondStr = "1";
        //int indx = DataObjectName.indexOf("_");
        // if(indx > 0)
        //    secondStr = DataObjectName.substring(indx,1);
        String dataObjectAbbr = "$" + createAttrVar(DataObjectName);  // DataObjectName.substring(0, 1) + secondStr;
        // need to get DataObjectNamePKEY

        sb.append("// assumes " + DataObjectName + " record is in " + dataObjectAbbr);
        newLine(sb);
        newLine(sb);


        //sb.append("BEGIN CONTEXT ***CRITICAL*** " + DataObjectName + " " + dataObjectAbbr + "." + getDataObjectNameAndPkey()); // to do
        // newLine(sb);
        sb.append("ASSERT EXISTS " + dataObjectAbbr);
        newLine(sb);


        AttributeDO attrDO;
        Enumeration e = findPersistentAttrs();
        while (e.hasMoreElements()) {
            attrDO = (AttributeDO) e.nextElement();
            attrDO.write(dataObjectAbbr, sb);
        }

        //sb.append("END CONTEXT");
        newLine(sb);

        String attrName;
        e = findPersistentAttrs();
        sb.append("//Check for IS_Attributes  or Null Boolean values and not required)");
        newLine(sb);
        String datatype;
        while (e.hasMoreElements()) {
            attrDO = (AttributeDO) e.nextElement();
            datatype = attrDO.getDataType();
            attrName = attrDO.getAttrName().substring(0, 2);
            if ("IS".equalsIgnoreCase(attrName) &&
                    attrDO.isValueRequired() == false) {
                attrDO.writeIsAttr(dataObjectAbbr, sb);
            }
            if (!"IS".equalsIgnoreCase(attrName) &&
                    "Boolean".equalsIgnoreCase(datatype) &&
                    attrDO.isValueRequired() == false) {
                attrDO.writeIsAttr(dataObjectAbbr, sb);
            }
        }
        e = findPersistentAttrs();
        newLine(sb);
        sb.append("//Check for Currency Attributes not used in Sum or Formula ");
        newLine(sb);

        while (e.hasMoreElements()) {
            attrDO = (AttributeDO) e.nextElement();
            datatype = attrDO.getDataType();
            if ("Currency".equalsIgnoreCase(datatype) &&
                    attrDO.isValidation_Sum_Or_Fomrula() == false) {
                attrDO.writeIsAttr(dataObjectAbbr, sb);
            }
        }
        // do relns (Parent/child checks)
        newLine(sb);
        newLine(sb);
        sb.append("//Checks - Child relationships to Parent Object");
        newLine(sb);
        e = getChildReln().elements();
        RelationshipDO r;
        int abbrIndex = 10;
        while (e.hasMoreElements()) {
            r = (RelationshipDO) e.nextElement();
            r.write(dataObjectAbbr, sb, abbrIndex);
            abbrIndex++;
        }
        /* Don't do this for now
        newLine(sb);
        sb.append("//Parent Relns");
        newLine(sb);
        e = getParentReln().elements();
        while(e.hasMoreElements()){
            r = (RelationshipDO) e.nextElement();
            r.write(dataObjectAbbr, sb);  // create a child writer
        }
        */
        newLine(sb);

        sb.append("//Validations...");
        newLine(sb);
        e = this.findPersistentAttrs();
        while (e.hasMoreElements()) {
            attrDO = (AttributeDO) e.nextElement();
            attrDO.writeValidation(dataObjectAbbr, sb);
            //newLine(sb);
        }
        newLine(sb);
        sb.append("//Derivations...");
        newLine(sb);
        e = this.findPersistentAttrs();
        while (e.hasMoreElements()) {
            attrDO = (AttributeDO) e.nextElement();
            attrDO.writeDerivation(dataObjectAbbr, sb);
            //newLine(sb);
        }
        newLine(sb);
        e = findChildren("Index");
        DO_Index indexDO = null;
        while (e.hasMoreElements()) {
            indexDO = (DO_Index) e.nextElement();
            indexDO.write(dataObjectAbbr, sb);
            newLine(sb);
        }


        //sb.append("//Constraints");
        newLine(sb);
        // do constraints
        e = findChildren("Constraint");
        ConstraintDO con;
        while (e.hasMoreElements()) {
            con = (ConstraintDO) e.nextElement();
            //con.write(dataObjectAbbr, sb);
            //newLine(sb);
        }
        newLine(sb);
        //sb.append("// Actions");
        newLine(sb);
        // do actions
        ActionDO act;
        e = findChildren("Action");
        while (e.hasMoreElements()) {
            act = (ActionDO) e.nextElement();
            if (act.getActionToPerform() != null) {
                // act.write(dataObjectAbbr, sb); // turn off for now  ???
                // newLine(sb);
            }
        }
        newLine(sb);

    }

    public String getDataObjectNameAndPkey() {
        // ans = ""; // DataObjectName + ".";
        Enumeration e = getChildren("IndexDO");
        DO_Index indx = null;
        DO_IndexAttr attr = null;
        String ans = "";
        while (e.hasMoreElements()) {
            indx = (DO_Index) e.nextElement();
            if (indx.isPrimary()) {
                Enumeration ee = indx.getChildren("IndexAttr");
                while (ee.hasMoreElements()) {
                    attr = (DO_IndexAttr) ee.nextElement();
                    ans = ans + attr.getIndexAttrName() + " ";
                }
            }
        }
        return ans;
    }

    public String getDataObjectNameAndPkey(String dataObjectAbbr) {
        // ans = ""; // DataObjectName + ".";
        Enumeration e = getChildren("IndexDO");
        DO_Index indx = null;
        DO_IndexAttr attr = null;
        String ans = "";
        String connector = "";
        while (e.hasMoreElements()) {
            indx = (DO_Index) e.nextElement();
            if (indx.isPrimary()) {
                Enumeration ee = indx.getChildren("IndexAttr");
                while (ee.hasMoreElements()) {
                    attr = (DO_IndexAttr) ee.nextElement();
                    ans = ans + connector + dataObjectAbbr + "." + attr.getIndexAttrName();
                    connector = " AND ";
                }
            }
        }
        return ans;
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

    // This is the wrapper that will call each of the child scripts
    // If you want to make the scripts self contained - move the LOOP OVER command
    // to the individual scripts (however, this maes them less flexibile)

    public void writeALL(StringBuffer sb) {


        String dataObjectAbbr = createAttrVar(DataObjectName);
        newLine(sb);
        sb.append("//This EUCLID (tm) script from Integrity Logic, LLC was Generated By ARCHIMEDES from Band Software Design, LLC ");
        newLine(sb);
        sb.append("NOTE CHECK_ALL " + DataObjectName);
        newLine(sb);
        sb.append("LOOP OVER " + DataObjectName + " INTO " + dataObjectAbbr); //+" WHERE 1 = 1 MAXROWS 500");
        newLine(sb);
        dataObjectAbbr = "$" + dataObjectAbbr;
        sb.append("     NOTE Checking " + DataObjectName + " RecordId = " + getDataObjectNameAndPkey(dataObjectAbbr));
        newLine(sb);
        sb.append("          EXECUTE " + DataObjectName + "_GEN.xml");   // first we do this data object
        newLine(sb);
        insertChildRelns(sb, this);
        sb.append("END LOOP");
        newLine(sb);
        newLine(sb);
        // NOTE I could add calls to Child Relationships via navigation on Parent to Child Role
    }

    // create sub calls to child relationships
    //LOOP OVER OrderItem INTO oi WHERE OrderNum = $order.OrderNumber
    // Call the script to check the order item
    // EXECUTE OrderItem.xml
    // END LOOP  /
    private void insertChildRelns(StringBuffer sb, DataObjectDO parentDO) {
        Enumeration e = this.getParentReln().elements();
        String doName = "";
        DataObjectDO objectDo;
        RelationshipDO reln;
        while (e.hasMoreElements()) {
            reln = (RelationshipDO) e.nextElement();
            objectDo = reln.getChildDO();
            doName = objectDo.getDataObjectName();
            String dataObjectAbbr = createAttrVar(doName);
            sb.append("     LOOP OVER " + doName + " INTO " + dataObjectAbbr + " WHERE ");  // Parent_PKEY = this.FKEY
            dataObjectAbbr = "$" + dataObjectAbbr;
            String relnJn = reln.formatDataObjectNameAndPkey("$" + createAttrVar(this.getDataObjectName()));
            sb.append(relnJn);    // TO DO - need a different method to get parent info
            newLine(sb);
            sb.append("           NOTE Checking " + doName + " RecordId = " + relnJn);
            newLine(sb);
            sb.append("                //EXECUTE " + doName + "_GEN.xml");
            newLine(sb);
            sb.append("     END LOOP");
            newLine(sb);
        }
    }

}
