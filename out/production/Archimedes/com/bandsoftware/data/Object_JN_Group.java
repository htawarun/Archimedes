
/*
 *    Title:         GroupDO
 *    Description:   Group File Definitions (packages)
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 *
*/
package com.bandsoftware.data;

import com.datachannel.xml.om.IXMLDOMNamedNodeMap;
import com.datachannel.xml.om.IXMLDOMNode;

public class Object_JN_Group extends BusinessObjectDO {
    private String RepositoryName;
    private String ObjectName;
    private String GroupName;
    private String ObjectType;


    public Object_JN_Group() {
        super("Object_JN_Group");
    }

    public Object_JN_Group(String reposName, String groupName, String doName) {
        super("Object_JN_Group");
        setRepositoryName(reposName);
        setObjectName(doName);
        setGroupName(groupName);
    }

    public Object_JN_Group(String reposName, String doName, IXMLDOMNode node) {
        super("Object_JN_Group");
        setRepositoryName(reposName);
        setObjectName(doName);

        init(node);
    }

    private void init(IXMLDOMNode childNode) {
        try {
            IXMLDOMNamedNodeMap nodeMap = (IXMLDOMNamedNodeMap) childNode.getAttributes();
            String name = childNode.getParentNode().getAttributes().item(0).getNodeValue();

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


    public String getObjectName() {
        return ObjectName;
    }

    public void setObjectName(String objectName) {
        setAttrValue("ObjectName", objectName);
        ObjectName = objectName;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String grpName) {
        setAttrValue("GroupName", grpName);
        GroupName = grpName;
    }

    public String getObjectType() {
        return ObjectType;
    }

    public void setObjectType(String objectType) {
        setAttrValue("ObjectType", objectType);
        ObjectType = objectType;
    }

    public void write(String abbr, StringBuffer sb) {
        sb.append("//Group Name =" + GroupName);
        sb.append("\n");
        sb.append("\n");

    }
}
