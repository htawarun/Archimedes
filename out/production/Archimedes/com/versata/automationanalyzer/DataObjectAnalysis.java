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

import com.bandsoftware.data.*;
import com.datachannel.xml.om.Document;
import com.datachannel.xml.om.IXMLDOMNamedNodeMap;
import com.datachannel.xml.om.IXMLDOMNode;
import com.datachannel.xml.om.IXMLDOMNodeList;

import java.util.Vector;

/**
 * Title:
 * Description:
 * Copyright:    Copyright Versata, Inc. 2001
 * Company: Versata
 *
 * @author Max Tardiveau, Versata
 * @version $Id: DataObjectAnalysis.java,v 1.3 2003/05/01 22:28:40 Tyler Exp $
 */

public class DataObjectAnalysis extends BusinessObjectAnalysis {
    public int numAttribs = 0;
    public int numPersistentAttribs = 0;
    public int numNonPersistentAttribs = 0;

    public int numRequiredAttribs = 0;
    public int numPreventUserUpdateAttribs = 0;
    public int numCodedValueAttribs = 0;
    public int numValidationAttribs = 0;

    public int numDerivedAttribs = 0;
    public int numSumAttribs = 0;
    public int numCountAttribs = 0;
    public int numParentReplicateAttribs = 0;
    public int numFormulaAttribs = 0;
    public int numDefaultAttribs = 0;

    public int numConstraints = 0;
    public int numActions = 0;


    // Tylers added code
    public RepositoryDO repos;
    public DataObjectDO dataObj;

    // Contains String[3] : parent name, parent role name, enforced
    public Vector parents = new Vector();

    // Contains String[3] : child name, child role name, enforced
    public Vector children = new Vector();

    public int numEnforcedRelationships = 0;

    private boolean debug = true; // set to false to turn off messages and output display
    ///////////////////////////////////////////////////////////////////////

    public DataObjectAnalysis(String rootDirectory, String name) {
        super(rootDirectory, name);
    }

    //////////////////////////////////////////////////////////////

    public void analyze() {
        // Analyze the object first
        countDataObjectRules(rootDirectory + "/Source/VLS/" + name + ".xml");

        countCodeLines();
        dataObj.setCounts(this.totalUserLines, this.totalGeneratedLines, this.totalUserMethods, this.totalGeneratedMethods);
    }

    //////////////////////////////////////////////////////////////////////////

    public void countDataObjectRules(String sourceFile) {
        try {
            Document dboDocument = new Document();
            dboDocument.setValidateOnParse(false);
            dboDocument.setParserClassName("com.datachannel.xml.tokenizer.parser.XMLDOMParser");
            dboDocument.load(sourceFile);
            IXMLDOMNodeList ndbobj;
            IXMLDOMNode nnode;
            IXMLDOMNamedNodeMap nnodeMap;
            IXMLDOMNodeList childNodes;
            IXMLDOMNode childNode;
            int cnt;
            if (repos != null) {
//  start  meta repos code
                dataObj = new DataObjectDO(repos.getRepositoryName(), this.simpleName);

                repos.addChild(dataObj);
                dataObj.init(dboDocument);
                dataObj.createIndexes(dboDocument);
                dataObj.setGroup(name);
                dataObj.setInternalPathName(name);
                //  dataObj.parseMethods(rootDirectory + "/Lib/VLS/" +repos.getRepositoryName()+ "/"+ this.simpleName + "Impl.class");
                cnt = dataObj.parseMethods(repos.getRepositoryName(), this.simpleName, repos.getRepositoryName() + "." + this.simpleName + "Impl", true);
                dataObj.totalUserMethodCount = cnt;
                this.totalUserMethods = cnt;
                cnt = dataObj.parseMethods(repos.getRepositoryName(), this.simpleName, repos.getRepositoryName() + "." + this.simpleName + "BaseImpl", false);
                dataObj.totalGenMethodCount = cnt;
                //if the classpath is not set - it will not find any methods to count so use source code and count method body
                if (cnt != 0) {
                    this.totalGeneratedMethods = cnt;
                    this.totalMethods = this.totalGeneratedMethods + this.totalUserMethods;
                }
                //db("AttrVar Name: " + data.createAttrVar(this.simpleName));
                db(this.simpleName + " User Methods = " + dataObj.totalUserMethodCount + " Generated " + dataObj.totalGenMethodCount);
            } else {
                System.out.println("Repos has not been set inside DataObjectAnalysis");
            }
            //Added name checking to prevent duplicate constraint names
            Vector constraintNames = new Vector();
            ndbobj = (IXMLDOMNodeList) dboDocument.getElementsByTagName("Constraint");
            if (ndbobj != null) {
                while ((nnode = ndbobj.nextNode()) != null) {
                    childNodes = (IXMLDOMNodeList) nnode.getChildNodes();
                    childNode = childNodes.nextNode();
                    if (childNode != null) {
                        ConstraintDO conDO = new ConstraintDO(repos.getRepositoryName(), this.simpleName, childNode);
                        if (!constraintNames.contains((String) conDO.getConstraintName())) {
                            dataObj.addChild(conDO);
                            db("A Constraint.." + conDO.getConstraintName());
                            numConstraints++;
                            constraintNames.addElement((String) conDO.getConstraintName());
                        }
                        childNode = childNodes.nextNode();
                    }
                }
            }

            // Count actions
            ndbobj = (IXMLDOMNodeList) dboDocument.getElementsByTagName("Action");
            if (ndbobj != null) {
                while ((nnode = ndbobj.nextNode()) != null) {
                    childNodes = (IXMLDOMNodeList) nnode.getChildNodes();
                    while ((childNode = childNodes.nextNode()) != null) {
                        db("An Action..");
                        ActionDO actDO = new ActionDO(repos.getRepositoryName(), this.simpleName, childNode);
                        if (actDO.getActionToPerform() != null) {
                            dataObj.addChild(actDO); // found that sometimes we duplicate actions
                            numActions++;
                        }
                    }
                }
            }

            // Count derivations
            ndbobj = (IXMLDOMNodeList) dboDocument.getElementsByTagName("Derivation");
            if (ndbobj != null) {
                while ((nnode = ndbobj.nextNode()) != null) {
                    nnodeMap = (IXMLDOMNamedNodeMap) nnode.getAttributes();
                    if (nnodeMap != null) {
                        if (nnodeMap.getNamedItem("DerivationType") != null) {
                            numDerivedAttribs++;
                            String derivType = nnodeMap.getNamedItem("DerivationType").getNodeValue();
                            if (derivType.equalsIgnoreCase("default")) {
                                numDefaultAttribs++;
                            } else if (derivType.equalsIgnoreCase("formula")) {
                                numFormulaAttribs++;
                            } else if (derivType.equalsIgnoreCase("sum")) {
                                numSumAttribs++;
                            } else if (derivType.equalsIgnoreCase("count")) {
                                numCountAttribs++;
                            } else if (derivType.equalsIgnoreCase("parentreplicate")) {
                                numParentReplicateAttribs++;
                            } else if (derivType.equalsIgnoreCase("none")) // Oops -- decrement
                            {
                                numDerivedAttribs--;
                            }
                        }
                    }
                }
            }

            // Count validations
            ndbobj = (IXMLDOMNodeList) dboDocument.getElementsByTagName("Validation");
            if (ndbobj != null) {
                while ((nnode = ndbobj.nextNode()) != null) {
                    nnodeMap = (IXMLDOMNamedNodeMap) nnode.getAttributes();
                    if (nnodeMap != null) {
                        if (nnodeMap.getNamedItem("ValidationType") != null) {
                            if (nnodeMap.getNamedItem("ValidationType").getNodeValue().equalsIgnoreCase("Condition")) {
                                childNodes = (IXMLDOMNodeList) nnode.getChildNodes();
                                if ((childNode = childNodes.nextNode()) != null) {
                                    if (childNode.getText().length() > 1) {
                                        //	System.out.println (childNode.getText());
                                        numValidationAttribs++;
                                    }
                                }
                            } else {
                                numCodedValueAttribs++;
                            }
                        }
                    }
                }
            }

            // Count attributes
            ndbobj = (IXMLDOMNodeList) dboDocument.getElementsByTagName("Attribute");
            if (ndbobj != null) {
                while ((nnode = ndbobj.nextNode()) != null) {
                    numAttribs++;
                    nnodeMap = (IXMLDOMNamedNodeMap) nnode.getAttributes();
                    if (nnodeMap != null) {
                        String attrName = nnodeMap.getNamedItem("Name").getNodeValue();
                        db("Attr Name " + attrName);

                        try {
                            AttributeDO attrDO = new AttributeDO(repos.getRepositoryName(), this.simpleName, attrName);
                            dataObj.addChild(attrDO);
                            attrDO.init(nnode);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        if (nnodeMap.getNamedItem("Persistent") != null) {
                            if (nnodeMap.getNamedItem("Persistent").getNodeValue().equalsIgnoreCase("true")) {
                                numPersistentAttribs++;
                            } else {
                                numNonPersistentAttribs++;
                            }
                        } else
                            numPersistentAttribs++;  // Attributes are persistent unless otherwise noted
                        if (nnodeMap.getNamedItem("ValueRequired") != null) {
                            if (nnodeMap.getNamedItem("ValueRequired").getNodeValue().equalsIgnoreCase("true")) {
                                numRequiredAttribs++;
                            }
                        }
                        if (nnodeMap.getNamedItem("PreventUserUpdates") != null) {
                            if (nnodeMap.getNamedItem("PreventUserUpdates").getNodeValue().equalsIgnoreCase("true")) {
                                numPreventUserUpdateAttribs++;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            errors.add("Error while counting rules for object " + name + " : " + e.getMessage());
        }
    }

    // local method to redirect all output and messaeges
    private void db(String msg) {
        if (debug)
            System.out.println(msg);
    }

    public void setRepos(RepositoryDO reposObj) {
        repos = reposObj;
    }

}