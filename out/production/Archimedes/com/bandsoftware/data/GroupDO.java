
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

public class GroupDO extends BusinessObjectDO {
    private String RepositoryName;
    private String DataObjectName;
    private String GroupName;
    private String ParentGroupName;


    public GroupDO() {
        super("Groups");
        WhereUsedAware = false;
    }

    public GroupDO(String reposName, String doName, String groupName) {
        super("Groups");
        setRepositoryName(reposName);
        setDataObjectName(doName);
        setGroupName(groupName);
        WhereUsedAware = false;
    }

    public GroupDO(String reposName, String doName, IXMLDOMNode node) {
        super("Groups");
        setRepositoryName(reposName);
        setDataObjectName(doName);
        WhereUsedAware = false;
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


    public String getDataObjectName() {
        return DataObjectName;
    }

    public void setDataObjectName(String dataObjectName) {
        setAttrValue("DataObjectName", dataObjectName);
        DataObjectName = dataObjectName;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String grpName) {
        setAttrValue("GroupName", grpName);
        GroupName = grpName;
    }

    public String getParentGroupName() {
        return ParentGroupName;
    }

    public void setParentGroupName(String parentGroupName) {
        setAttrValue("ParentGroupName", parentGroupName);
        ParentGroupName = parentGroupName;
    }

    public void write(String abbr, StringBuffer sb) {
        sb.append("//Group Name =" + GroupName);
        sb.append("\n");
        sb.append("//Parent =" + strip(this.ParentGroupName));
        sb.append("\n");

    }
}
