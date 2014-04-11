// This software is the confidential and proprietary information
// of Versata, Inc. ("Confidential Information").  You
// shall not disclose such Confidential Information and shall use
// it only in accordance with the terms of the license agreement
// you entered into with Versata.
//
// THE SOFTWARE IS PROVIDED AS IS, WITHOUT ANY EXPRESS OR IMPLIED
// WARRANTY BY VERSATA, INC. OR ITS SUPPLIERS, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS FOR A PARTICULAR PURPOSE. NEITHER VERSATA, INC. NOR ITS
// SUPPLIERS PROMISE THAT THE SOFTWARE WILL BE ERROR FREE OR WILL
// OPERATE WITHOUT INTERRUPTION. IN NO EVENT SHALL VERSATA, INC. BE
// LIABLE FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL OR CONSEQUENTIAL
// DAMAGES OF ANY KIND INCLUDING, WITHOUT LIMITATION, LOST PROFITS.
// Copyright (c) 2001, Versata, Inc.

package com.versata.automationanalyzer;

import com.bandsoftware.data.DataObjectDO;
import com.bandsoftware.data.RelationshipDO;
import com.bandsoftware.data.RepositoryDO;
import com.datachannel.xml.om.Document;
import com.datachannel.xml.om.IXMLDOMNamedNodeMap;
import com.datachannel.xml.om.IXMLDOMNode;
import com.datachannel.xml.om.IXMLDOMNodeList;

import java.util.Vector;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001-2014
 * Company: Band Software Design, LLC
 *
 * @author: Tyler Band
 * @version 1.0
 */

public class RelationshipAnalysis extends ArtefactAnalysis {
    public String rootDirectory;
    public String name;  // Qualified name, with packages
    public String parentName;
    public String childName;
    public int numRelationships = 0;
    public int numEnforcedRels = 0;

    // tmb added these attributes
    public RepositoryDO repos;
    public RelationshipDO relnDO;
    public DataObjectDO parentDO;
    public DataObjectDO childDO;

    // Contains String[3] : parent role, child role, "true" or "false" for enforced
    public Vector relationships = new Vector();

    //////////////////////////////////////////////////////////////////////

    public RelationshipAnalysis(String rootDirectory, String name) {
        this.rootDirectory = rootDirectory;
        this.name = name;
        String unqualName = name;
        int lastSlash = name.lastIndexOf('\\');
        if (lastSlash != -1)
            unqualName = name.substring(lastSlash + 1);
        int dashIndex = unqualName.indexOf('-');
        if (dashIndex == -1) {
            errors.add("Unable to parse relationship name : " + name);
            return;
        }
        parentName = unqualName.substring(4, dashIndex);
        childName = unqualName.substring(dashIndex + 1);

    }

    ///////////////////////////////////////////////////////////////////

    public void analyze() {
        System.out.println("Analyzing relationships between " + parentName + " and " + childName);
// begin tmb
        if (repos != null) {
            parentDO = repos.findDataObject(parentName);
            childDO = repos.findDataObject(childName);
            relnDO = new RelationshipDO(repos.getRepositoryName(), parentName, childName);
            if (parentDO != null && childDO != null) {
                relnDO.setParentDO(parentDO);
                relnDO.setChildDO(childDO);
                childDO.addChildReln(relnDO);
                parentDO.addParentReln(relnDO);
                repos.addChild(relnDO);
            }
        } else {
            System.out.println("Repos not set on RelationshipAnalysis");
        }
// end tmb

        try {
            Document dboDocument = new Document();
            dboDocument.setValidateOnParse(false);
            dboDocument.setParserClassName("com.datachannel.xml.tokenizer.parser.XMLDOMParser");
            dboDocument.load(rootDirectory + "\\Source\\VLS\\" + name + ".xml");

            // Count constraints
            IXMLDOMNodeList ndbobj = (IXMLDOMNodeList) dboDocument.getElementsByTagName("Relationship");
            if (ndbobj != null) {
                IXMLDOMNode nnode;
                while ((nnode = ndbobj.nextNode()) != null) {
                    numRelationships++;
                    System.out.println("Num. relationships " + numRelationships);
                    IXMLDOMNamedNodeMap nnodeMap = (IXMLDOMNamedNodeMap) nnode.getAttributes();
                    if (nnodeMap != null) {
                        System.out.println("Relationship... ");
                        String relEntry[] = new String[3];
                        if (nnodeMap.getNamedItem("ParentRoleName") != null) {
                            relEntry[0] = nnodeMap.getNamedItem("ParentRoleName").getNodeValue();
                            relnDO.setParentRoleName(relEntry[0]);
                        } else
                            relEntry[0] = childName;
                        if (nnodeMap.getNamedItem("ChildRoleName") != null) {
                            relEntry[1] = nnodeMap.getNamedItem("ChildRoleName").getNodeValue();
                            relnDO.setChildRoleName(relEntry[1]);
                        } else
                            relEntry[1] = parentName;

                        // Check if relationship is enforced
                        relEntry[2] = "true";
                        if (nnodeMap.getNamedItem("IsEnforce") != null) {
                            String isEnforce = nnodeMap.getNamedItem("IsEnforce").getNodeValue();
                            if (isEnforce.equalsIgnoreCase("true")){
                                numEnforcedRels++;
                            }
                            relEntry[2] = isEnforce;
                            relnDO.setIsEnforce(isEnforce.equalsIgnoreCase("true"));
                        }
                        if (nnodeMap.getNamedItem("OnParentUpdate") != null) {
                            String onParentUpdate = nnodeMap.getNamedItem("OnParentUpdate").getNodeValue();
                            relnDO.setOnParentUpdate(onParentUpdate);
                        }
                        if (nnodeMap.getNamedItem("OnParentDelete") != null) {
                            String onParentDelete = nnodeMap.getNamedItem("OnParentDelete").getNodeValue();
                            relnDO.setOnParentDelete(onParentDelete);
                        }
                        if (nnodeMap.getNamedItem("OnChildInsertOrUpdate") != null) {
                            String onChildInsertOrUpdate = nnodeMap.getNamedItem("OnChildInsertOrUpdate").getNodeValue();
                            relnDO.setOnChildInsertOrUpdate(onChildInsertOrUpdate);
                        }

                        if (nnodeMap.getNamedItem("ErrPreventInsertOrUpdateChild") != null) {
                            String errPreventInsertOrUpdateChild = nnodeMap.getNamedItem("ErrPreventInsertOrUpdateChild").getNodeValue();
                            relnDO.setErrPreventInsertOrUpdateChild(errPreventInsertOrUpdateChild);
                        }
                        relationships.add(relEntry);
                    }
                }
            }
// Count constraints
            ndbobj = (IXMLDOMNodeList) dboDocument.getElementsByTagName("AttributePair");
            if (ndbobj != null) {   //get the first pair only - multi part keys we don;t handle yet   tmb
                IXMLDOMNode nnode;
                while ((nnode = ndbobj.nextNode()) != null) {
                    IXMLDOMNamedNodeMap nnodeMap = (IXMLDOMNamedNodeMap) nnode.getAttributes();
                    if (nnodeMap != null) {
                        if (nnodeMap.getNamedItem("ParentAttribute") != null)
                            relnDO.setParentAttribute(nnodeMap.getNamedItem("ParentAttribute").getNodeValue());
                        if (nnodeMap.getNamedItem("ChildAttribute") != null)
                            relnDO.setChildAttribute(nnodeMap.getNamedItem("ChildAttribute").getNodeValue());
                    }
                }
            }
        } catch (Exception ex) {
            errors.add("Error while analyzing relationship file " + name + " : " + ex.toString());
        }
    }
}