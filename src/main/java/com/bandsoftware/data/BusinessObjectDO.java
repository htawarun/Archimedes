package com.bandsoftware.data;

/*
 *    Title:         BusinessObjectDO
 *    Description:   Abstract helper Class - contains many methods used by subclasses
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 *
*/

import com.datachannel.xml.om.IXMLDOMNamedNodeMap;
import com.datachannel.xml.om.IXMLDOMNode;
import com.datachannel.xml.om.IXMLDOMNodeList;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class BusinessObjectDO extends BSDDataObject {

    private int nestLevel = 0;
    private int maxNestLevel = 100;
    private static Hashtable AllRelnHT = null;
    private boolean debug = true; // set this to false to turn off messages to console
    public static Properties aaProp;

    public BusinessObjectDO() {
        super("UNKNOWN");
    }

    public BusinessObjectDO(String aName) {
        super(aName);
    }

    public void testStringAll() {
        super.testStringAll();
        // overrride per class
    }

    public void setCounts(int userLine, int genLine, int userMethod, int genMethod) {
        setAttrValue("User_Line_Cnt", (userLine < 0 ? 0 : userLine));
        setAttrValue("Gen_Lines_Cnt", (genLine < 0 ? 0 : genLine));
        setAttrValue("User_Method_Cnt", (userMethod < 0 ? 0 : userMethod));
        setAttrValue("Gen_Method_Cnt", (genMethod < 0 ? 0 : genMethod));
    }

    // a new type of findChild
    public DataObjectDO findDataObject(String objName) {
        Enumeration e = findChildren("DataObject");
        DataObjectDO obj = null;
        while (e.hasMoreElements()) {
            obj = (DataObjectDO) e.nextElement();
            if (objName.equals(obj.getDataObjectName()))
                return obj;
        }
        return null;
    }

    public QueryObjectDO findQueryObject(String objName) {
        Enumeration e = findChildren("QueryObject");
        QueryObjectDO obj = null;
        while (e.hasMoreElements()) {
            obj = (QueryObjectDO) e.nextElement();
            if (objName.equals(obj.getQueryObjectName()))
                return obj;
        }
        return null;
    }

    public String stripParensDO(String aName) {
        if (aName != null) {
            int indx = aName.indexOf("(") + 1;
            if (indx > 0) {
                int endIndx = aName.indexOf(")");
                return aName.substring(indx, endIndx);
            } else
                return aName;
        }
        return null;

    }

    public IXMLDOMNode getNamedNode(String name, IXMLDOMNode aNode) {
        IXMLDOMNode ans = null;
        IXMLDOMNodeList aNodeList;

        if (aNode != null && name.equalsIgnoreCase(aNode.getNodeName())) {
            //db("Found Node Named = " + name);
            return aNode;
        } else {
            nestLevel++;
            if (nestLevel > maxNestLevel) {
                db("Max NestLevel Exceeded");
                //throw new BSDException("Max Nest Level");
                return null;
            }
            if (aNode != null) {
                ans = getNamedNode(name, (IXMLDOMNodeList) aNode.getChildNodes());
                if (ans == null)
                    ans = getNamedNode(name, (IXMLDOMNamedNodeMap) aNode.getAttributes());
            }
        }
        return ans;
    }

    public int parseMethods(String reposName, String objName, String objPath, boolean isUserDefined) {
        // this will do the actual work of setting return type and signature
        try {
            Class cl = Class.forName(objPath);             //(objName);
            Method[] methods = cl.getDeclaredMethods();
            int methodCount = methods.length;
            for (int i = 0; i < methodCount; i++) {
                boolean isAccessible = methods[i].isAccessible();
                String mname = methods[i].getName();
                Class returnType = methods[i].getReturnType();
                Class[] parms = methods[i].getParameterTypes();
                //db("Method Name "+ mname);
                //db("Return Type" + returnType.getName());
                //db("Signature "+ methods[i].toString());
                //Create a persistent object to hold the method signature
                MethodDO meth = new MethodDO(reposName, objName, mname);
                meth.setSignature(methods[i].toString());
                meth.setReturnType(returnType.getName());
                meth.setUserDefined(isUserDefined);

                int mod = methods[i].getModifiers();
                if (mod != 0) {
                    meth.setPublic(Modifier.toString(mod));
                }
                this.addChild(meth);

            }
            return methodCount;
        } catch (ClassNotFoundException ex) {
            //ex.printStackTrace();
            db("Compiled Class not found " + objName);
            return 0;
        }
    }

    public void display(IXMLDOMNode aNode) {
        if (aNode != null) {
            db("Node Name " + aNode.getNodeName() + " Value " + aNode.getNodeValue());
            display((IXMLDOMNodeList) aNode.getChildNodes());
            display((IXMLDOMNamedNodeMap) aNode.getAttributes());
        }
    }

    public IXMLDOMNode getNamedNode(String nodeName, IXMLDOMNodeList aNodeList) {

        IXMLDOMNode aNewNode = null;
        if (aNodeList != null) {
            //db("NodeList");
            int size = aNodeList.getLength();
            IXMLDOMNode aNode = null;
            for (int i = 0; i < size; i++) {

                aNode = (IXMLDOMNode) aNodeList.item(i);
                if (aNode != null) {
                    //while( aNode != null ){
                    //display(aNode);
                    aNewNode = getNamedNode(nodeName, aNode);
                    if (aNewNode != null)
                        return aNewNode;
                }
            }
        }
        return null;
    }

    public IXMLDOMNode getNamedNode(String nodeName, IXMLDOMNamedNodeMap aNodeMap) {

        IXMLDOMNode newNode = null;
        if (aNodeMap != null) {   //db("NodeMap");
            int size = aNodeMap.getLength();
            IXMLDOMNode aNode = null;
            for (int i = 0; i < size; i++) {
                aNode = (IXMLDOMNode) aNodeMap.item(i);
                if (aNode != null) {
                    //display(aNode);
                    newNode = getNamedNode(nodeName, (IXMLDOMNode) aNode);
                    if (newNode != null)
                        return newNode;

                    //    String attrName =   nnodeMap.getNamedItem("Name").getNodeValue();
                }
            }
        }
        return null;
    }

    public void display(IXMLDOMNodeList aNodeList) {

        if (aNodeList != null) {
            db("NodeList");
            IXMLDOMNode aNode = aNodeList.nextNode();
            while (aNode != null) {
                display(aNode);
                aNode = aNodeList.nextNode();
            }
        }
    }

    public void display(IXMLDOMNamedNodeMap aNodeMap) {

        if (aNodeMap != null) {
            db("NodeMap");
            IXMLDOMNode aNode = aNodeMap.nextNode();
            while (aNode != null) {
                display(aNode);
                aNode = aNodeMap.nextNode();
                //    String attrName =   nnodeMap.getNamedItem("Name").getNodeValue();
            }
        }
    }

    // Walk the linked tree to find a specific object
    public DataObjectDO findCodedValueListDO(ValidationDO anObj, String aCodedValueList) {
        DataObjectDO doObj;
        AttributeDO attrDO;
        RepositoryDO reposDO;
        attrDO = (AttributeDO) anObj.getParent();
        doObj = (DataObjectDO) attrDO.getParent();
        reposDO = (RepositoryDO) doObj.getParent();
        doObj = (DataObjectDO) reposDO.findDataObject(aCodedValueList);
        return doObj;
    }

    public DataObjectDO findSourceDO(DerivationDO anObj, String aCodedValueList) {
        DataObjectDO doObj;
        AttributeDO attrDO;
        RepositoryDO reposDO;
        attrDO = (AttributeDO) anObj.getParent();
        doObj = (DataObjectDO) attrDO.getParent();
        reposDO = (RepositoryDO) doObj.getParent();
        doObj = (DataObjectDO) reposDO.findDataObject(aCodedValueList);
        return doObj;
    }

    public void write(StringBuffer sb) {
        sb.append("Name " + getName());
        newLine(sb);
    }

    // remove carriage retrun and line feed from input stream
    public String strip(String aName) {
        StringBuffer ans = new StringBuffer();
        String letter = "";
        if (aName != null) {
            int length = aName.length();
            for (int i = 0; i < length; i++) {
                letter = aName.substring(i, i + 1);
                if (!letter.equalsIgnoreCase("\n") &&
                        !letter.equalsIgnoreCase("\r"))
                    ans.append(letter);
            }
        }
        return ans.toString();
    }

    // remove carriage retrun and line feed from input stream
    public String stripReplace(String aName) {
        StringBuffer ans = new StringBuffer();
        String letter = "";
        if (aName != null) {
            int length = aName.length();
            for (int i = 0; i < length; i++) {
                letter = aName.substring(i, i + 1);
                if (!letter.equalsIgnoreCase("\n") &&
                        !letter.equalsIgnoreCase("\r"))
                    ans.append(letter);
                else
                    ans.append(" ");// put a blank instead of the carriage return or linefeed
            }
        }
        return ans.toString();
    }

    //remove _ AEIOU from a string name to create a variable for DICE
    public String createAttrVar(String aName) {
        String letter = "";
        int length = aName.length();
        StringBuffer ans = new StringBuffer();
        ans.append(aName.substring(0, 1)); // put first letter in name
        for (int i = 1; i < length; i++) {
            letter = aName.substring(i, i + 1);
            if (!letter.equalsIgnoreCase("_") &&
                    !letter.equals("a") &&
                    !letter.equals("e") &&
                    !letter.equals("i") &&
                    !letter.equals("o") &&
                    !letter.equals("u"))
                ans.append(letter);
        }
        return ans.toString();
    }

    public boolean dataTypeIsString(AttributeDO attrDO) {
        String dt = attrDO.getDataType();
        db("DataType check is " + dt);
        return "Char".equals(dt) || "VarChar".equals(dt) || "Date".equals(dt) || "DateTime".equals(dt);
    }

    //given this data object, return the named attribute
    public AttributeDO getPkeyAttr(DataObjectDO sourceDO, String attrName) {
        AttributeDO attrDO = null;
        Enumeration e = sourceDO.findChildren("Attribute");
        while (e.hasMoreElements()) {
            attrDO = (AttributeDO) e.nextElement();
            if (attrName.equals(attrDO.getAttrName()))
                return attrDO;
        }
        return null;
    }

    public Enumeration findChildRelnAttrs(String dataObjectName) {
        Vector attrs = new Vector();
        RelationshipDO relnDO;
        Enumeration e = findChildRelationships(dataObjectName);
        if (e != null) {
            while (e.hasMoreElements()) {
                relnDO = (RelationshipDO) e.nextElement();
                return relnDO.getChildAttributes().elements();
            }
        }
        return null;
    }

    public Enumeration findChildRelationships(String dataObjectName) {
        if (AllRelnHT == null)
            AllRelnHT = findAllChildRelationships();
        Vector v = (Vector) AllRelnHT.get(dataObjectName);
        if (v != null)
            return v.elements();
        return null;
    }

    //loop through all relns that claim this object as
    private Hashtable<String,RelationshipDO> findAllChildRelationships() {
        RepositoryDO reposDO = getReposDO();
        Hashtable relnHT = new Hashtable();
        if (reposDO != null) {
            Enumeration e = reposDO.findChildren("DataObject");
            RelationshipDO relnDO;
            DataObjectDO dataObjDO;
            Vector v;
            String relnName;
            while (e.hasMoreElements()) {
                dataObjDO = (DataObjectDO) e.nextElement();
                Enumeration ee = dataObjDO.getChildReln().elements();
                while (ee.hasMoreElements()) {
                    relnDO = (RelationshipDO) ee.nextElement();
                    relnName = relnDO.getChildDO().getDataObjectName();
                    v = (Vector) relnHT.get(relnName);
                    if (v == null)
                        v = new Vector();
                    v.addElement(relnDO);
                    relnHT.put(relnName, v);
                }
            }
        }
        return relnHT;
    }

    private RepositoryDO getReposDO() {
        if (this instanceof RepositoryDO)
            return (RepositoryDO) this;
        if (this instanceof DataObjectDO)
            return (RepositoryDO) this.getParent();
        if (this instanceof AttributeDO)
            return (RepositoryDO) this.getParent().getParent();
        if (this instanceof DerivationDO)
            return (RepositoryDO) this.getParent().getParent().getParent();
        if (this instanceof RelationshipDO)
            return (RepositoryDO) this.getParent();
        if (this instanceof ApplicationDO)
            return (RepositoryDO) this.getParent();
        if (this instanceof QueryObjectDO)
            return (RepositoryDO) this.getParent();
        return null;

    }

    public String getWhereUsedObjectName() {
        if (this instanceof DataObjectDO)
            return (String) ((DataObjectDO) this).getDataObjectName();
        if (this instanceof AttributeDO)
            return (String) ((AttributeDO) this).getDataObjectName();
        if (this instanceof DerivationDO)
            return (String) ((DataObjectDO) this).getDataObjectName();
        if (this instanceof ActionDO)
            return (String) ((ActionDO) this).getDataObjectName();
        if (this instanceof ValidationDO)
            return (String) ((ValidationDO) this).getDataObjectName();
        if (this instanceof RelationshipDO)
            return (String) ((RelationshipDO) this).getRelationshipName();
        if (this instanceof ApplicationDO)
            return (String) ((ApplicationDO) this).getApplicationName();
        if (this instanceof QueryObjectDO)
            return (String) ((QueryObjectDO) this).getQueryObjectName();
        if (this instanceof FormDO)
            return (String) ((FormDO) this).getApplicationName();
        if (this instanceof QueryDataObjectDO)
            return (String) ((QueryDataObjectDO) this).getDataObjectName();

        return null;
    }

    public void newLine(StringBuffer sb) {
        sb.append("\n");
    }

    public void db(String msg) {
        // comment out this line to turn off all debug messages
        if (debug)
            super.db(msg);
    }

    public Properties getProps() {
        try {
            if (aaProp == null) {
                aaProp = new Properties();
                FileInputStream fis = new FileInputStream("Archimedes.properties");
                aaProp.load(fis);
                fis.close();
            }
            return aaProp;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
