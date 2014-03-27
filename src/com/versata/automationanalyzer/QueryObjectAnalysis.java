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

import com.bandsoftware.data.QueryObjectDO;
import com.bandsoftware.data.RepositoryDO;
import com.datachannel.xml.om.Document;
import com.datachannel.xml.om.IXMLDOMNamedNodeMap;
import com.datachannel.xml.om.IXMLDOMNode;
import com.datachannel.xml.om.IXMLDOMNodeList;

/**
 * Title:
 * Description:
 * Copyright:    Copyright Versata, Inc. 2001
 * Company: Versata
 *
 * @author Max Tardiveau, Versata
 * @version $Id: QueryObjectAnalysis.java,v 1.1.1.1 2003/04/07 21:48:39 Tyler Exp $
 */

public class QueryObjectAnalysis extends BusinessObjectAnalysis {
    public int numObjects = 0;
    public int numJoins = 0;
    public int numAttribs = 0;
    public int numPersistentAttribs = 0;
    public int numNonPersistentAttribs = 0;
    public int numAggregatedAttribs = 0;
    public int numDerivedAttribs = 0;
    public RepositoryDO repos;
    private QueryObjectDO qryDO;

    ///////////////////////////////////////////////////////////////////

    public QueryObjectAnalysis(String rootDirectory, String name) {
        super(rootDirectory, name);
    }

    ////////////////////////////////////////////////////////////////////

    public void analyze() {
        // Analyze the object first
        qryDO = new QueryObjectDO(repos.getRepositoryName(), this.simpleName);
        repos.addChild(qryDO);

        countQueryObjectRules(rootDirectory + "/Source/VLS/" + name + ".xml");
        qryDO.setInternalPathName(name);
        countCodeLines();
        qryDO.setCounts(this.totalUserLines, this.totalGeneratedLines, this.totalUserMethods, this.totalGeneratedMethods);
    }

    /////////////////////////////////////////////////////////////////////

    public void countQueryObjectRules(String sourceFile) {
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

//qryDO.set other properties here like superclass and sqltext
// String sc = dboDocument.getElementsByTagName("Superclass");
            String sc = (String) dboDocument.getElementsByTagName("QueryObject").item(0).getAttributes().getNamedItem("SuperClass").getNodeValue();
            qryDO.setSuperClass(sc);
            String childMostDO = (String) dboDocument.getElementsByTagName("QueryObject").item(0).getAttributes().getNamedItem("ChildMostDataObject").getNodeValue();
            qryDO.setChildMostDataObject(childMostDO);
//            String sql = (String) dboDocument.getElementsByTagName("RuntimeSql").item(0).getChildNodes().item(0).getNodeValue();

//<<>> removed this because it blows up DB2 - the "" cause some problems
//            qryDO.setRuntimeSql(sql);
            // Count the objects involved in this query
            IXMLDOMNodeList objNodes = (IXMLDOMNodeList) dboDocument.getElementsByTagName("DataObject");
            if (objNodes != null) {
                numObjects = objNodes.getLength();
                qryDO.createDataObjects(objNodes);

            }
            // Count the joins in this query
            IXMLDOMNodeList joinNodes = (IXMLDOMNodeList) dboDocument.getElementsByTagName("Joins");
            if (joinNodes != null) {
                numJoins = joinNodes.getLength();
                qryDO.createJoinDataObjects(joinNodes);

            }
            // If there are less that numObjects - 1 joins, set a warning.
            if (numJoins < (numObjects - 1)) {
                StringBuffer sb = new StringBuffer();
                sb.append("<h3>Query object " + name + " has too few joins</h3>\n");
                sb.append("This query objects involves " + numObjects + " data objects, but \n");
                sb.append("has only " + numJoins + " joins. This is extremely suspicious, as this will \n");
                sb.append("normally result in a cartesian product with a large number of mostly repetitive rows.\n");
                notes.add(sb.toString());
            }

            // Count attributes
            ndbobj = (IXMLDOMNodeList) dboDocument.getElementsByTagName("Attribute");
            if (ndbobj != null) {
                while ((nnode = ndbobj.nextNode()) != null) {
                    numAttribs++;
                    nnodeMap = (IXMLDOMNamedNodeMap) nnode.getAttributes();
                    if (nnodeMap != null) {
                        if (nnodeMap.getNamedItem("Persistent") != null) {
                            if (nnodeMap.getNamedItem("Persistent").getNodeValue().equalsIgnoreCase("true")) {
                                numPersistentAttribs++;
                            } else {
                                numNonPersistentAttribs++;
                            }
                        } else
                            numPersistentAttribs++;  // Attributes are persistent unless otherwise noted

                        if (nnodeMap.getNamedItem("AggregationType") != null) {
                            if (!nnodeMap.getNamedItem("AggregationType").getNodeValue().equalsIgnoreCase("none")) {
                                numAggregatedAttribs++;
                            }
                        }
                    }
                }
            }

            ndbobj = (IXMLDOMNodeList) dboDocument.getElementsByTagName("Formula");
            if (ndbobj != null) {
                numDerivedAttribs = ndbobj.getLength();
            }

        } catch (Exception e) {
            errors.add("Error while counting rules for query object " + name + " : " + e.getMessage());
        }
    }
}