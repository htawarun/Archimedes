
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

public class OtherFileDO extends BusinessObjectDO {
    private String RepositoryName;
    private String FileName;


    public OtherFileDO() {
        super("OtherFile");
    }

    public OtherFileDO(String reposName, String fileName) {
        super("OtherFile");
        setRepositoryName(reposName);
        setFileName(fileName);
    }

    public OtherFileDO(String reposName, String fName, IXMLDOMNode node) {
        super("OtherFile");
        setRepositoryName(reposName);
        setFileName(fName);
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

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fName) {
        setAttrValue("OtherFileName", fName);
        FileName = fName;
    }


    public void write(String abbr, StringBuffer sb) {
        sb.append("//Other File Name =" + FileName);
        sb.append("\n");


    }
}
