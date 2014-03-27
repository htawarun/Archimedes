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

import com.bandsoftware.data.OtherFileDO;
import com.bandsoftware.data.RepositoryDO;
import com.datachannel.xml.om.Document;
import com.datachannel.xml.om.IXMLDOMNode;
import com.datachannel.xml.om.IXMLDOMNodeList;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

/**
 * Title:
 * Description:
 * Copyright:    Copyright Versata, Inc. 2001
 * Company: Versata
 *
 * @author Max Tardiveau, Versata
 * @version $Id: RepositoryAnalysis.java,v 1.2 2003/04/25 22:21:48 Tyler Exp $
 */

public class RepositoryAnalysis {
    //**** Tyler's Classes
    public static RepositoryDO repos;


    //////
    public String rootDirectory;
    public String name;
    public Vector errors = new Vector();
    public Vector notes = new Vector();
    public Map dataObjects = new TreeMap();
    public Map queryObjects = new TreeMap();
    public Map otherFiles = new TreeMap();

    public int totalLines = 0;
    public int totalGeneratedLines = 0;
    public int totalUserLines = 0;

    public int totalMethods = 0;         //added by tmb     Data Object Count Only
    public int totalGenMethods = 0;      //tmb
    public int totalUserMethods = 0;     //tmb

    public int totalDOlines = 0;
    public int totalDOgeneratedLines = 0;
    public int totalDOuserLines = 0;

    public int totalQOlines = 0;
    public int totalQOgeneratedLines = 0;
    public int totalQOuserLines = 0;

    public int totalNumAttribs = 0;
    public int largestNumAttribs = 0;
    public String largestNumAttribsName = "?";
    public int totalNumPersistentAttribs = 0;
    public int totalNumNonPersistentAttribs = 0;

    public int totalNumRequiredAttribs = 0;
    public int totalNumPreventUserUpdateAttribs = 0;
    public int totalNumCodedValueAttribs = 0;
    public int totalNumValidationAttribs = 0;

    public int totalNumDerivedAttribs = 0;
    public int totalNumSumAttribs = 0;
    public int totalNumCountAttribs = 0;
    public int totalNumParentReplicateAttribs = 0;
    public int totalNumFormulaAttribs = 0;
    public int totalNumFormDataObjects = 0; //tmb
    public int totalNumFormPickObjects = 0; //tmb
    public int totalNumDefaultAttribs = 0;

    public int totalNumConstraints = 0;
    public int totalNumActions = 0;

    public int totalNumQueryAttribs = 0;
    public int totalNumQueryPersistentAttribs = 0;
    public int totalNumQueryNonPersistentAttribs = 0;
    public int totalNumQueryAggregatedAttribs = 0;
    public int totalNumQueryDerivedAttribs = 0;

    // Relationships
    public int numRelationships = 0;
    public int numEnforcedRelationships = 0;

    // Apps
    public int totalNumForms = 0;
    public Map htmlApps = new TreeMap();
    public Map javaApps = new TreeMap();
    public int totalAppMethods = 0;         //tmb added for Forms and Pages
    public int totalAppUserMethods = 0;     //tmb
    public int totalAppGenMethods = 0;      //tmb

    // HTML apps
    public int totalNumHTMLpages = 0;
    public int totalNumRegenHTMLpages = 0;
    public int totalHTMLlines = 0;
    public int totalHTMLgeneratedLines = 0;
    public int totalHTMLuserLines = 0;
    public int totalHTMLUserMethods = 0;       //tmb
    public int totalHTMLGeneratedMethods = 0;  //tmb
    public int totalHTMLMethods = 0;           //tmb

    // Java apps
    public int totalNumJavaForms = 0;
    public int totalNumRegenJavaForms = 0;
    public int totalJavaLines = 0;
    public int totalJavaGeneratedLines = 0;
    public int totalJavaUserLines = 0;
    public int totalJavaMethods = 0;    // tmb
    public int totalJavaUserMethods = 0; //tmb
    public int totalJavaGeneratedMethods = 0; //tmb

    // Other files
    public int totalNumOtherFiles = 0;
    public int totalOtherFilesLines = 0;
    public int totalOtherFileMethods = 0;

    public int totalDOerrors = 0;
    public int totalQOerrors = 0;
    public int totalHTMLAppsErrors = 0;
    public int totalJavaAppsErrors = 0;
    public int totalOtherErrors = 0;

    public Date analysisDate;
    public String analysisUserName = "?";
    public String analysisHost = "?";

    protected File xmlFile;

    ////////////////////////////////////////////////////////////////////////

    public RepositoryAnalysis(File xmlFile) {
        super();
        this.xmlFile = xmlFile;
        rootDirectory = xmlFile.getParentFile().getParent();
        name = xmlFile.getName();
        name = name.substring(0, name.length() - 4);
        repos = new RepositoryDO(name); // tmb
        repos.setRootDirectory(this.rootDirectory);
    }

    /////////////////////////////////////////////////////////////////////////

    public void analyzeRepository() {
        analysisDate = new Date();
        analysisUserName = System.getProperty("user.name");
        try {
            analysisHost = java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception ex) {
            System.err.println("Could not get local host name : " + ex.toString());
        }

        Document myDocument = new Document();

        //Turn off the validation flag for better performance.
        myDocument.setValidateOnParse(false);

        //set the validating parser that supports data types
        myDocument.setParserClassName("com.datachannel.xml.tokenizer.parser.XMLDOMParser");

        try {
            myDocument.load(xmlFile.getAbsolutePath());
        } catch (Throwable ex) {
            System.out.println("Catching parse error : " + ex.getMessage());
            errors.add("Error while opening repository : " + ex.getMessage());
            return;
        }
        //repos.init(myDocument);
        // Loop through data objects
        IXMLDOMNodeList dbObj = (IXMLDOMNodeList) myDocument.getElementsByTagName("DataObject");
        IXMLDOMNode node;

        //was WHILE
        while ((node = dbObj.nextNode()) != null) {
            //for each item in the loop we increment dataObjects counter
            // count the user/versata lines
            IXMLDOMNodeList childNodes = (IXMLDOMNodeList) node.getChildNodes();

            // get the childnode and check to see if it is the FileName node
            IXMLDOMNode childnode;

            while ((childnode = childNodes.nextNode()) != null) {
                String objectName = childnode.getText();
                System.out.println("Data object : " + objectName);

                DataObjectAnalysis doa = new DataObjectAnalysis(rootDirectory, objectName);
                doa.setRepos(repos);
                dataObjects.put(doa.simpleName, doa);
                // end tmb
                doa.analyze();

                totalDOerrors += doa.errors.size();
                if (doa.errors.size() > 0)
                    errors.addAll(doa.errors);

                totalDOlines += doa.totalLines;
                totalDOgeneratedLines += doa.totalGeneratedLines;
                totalDOuserLines += doa.totalUserLines;

                totalLines += doa.totalLines;
                totalGeneratedLines += doa.totalGeneratedLines;
                totalUserLines += doa.totalUserLines;

                totalMethods += doa.totalMethods;              //tmb
                totalGenMethods += doa.totalGeneratedMethods;  //tmb
                totalUserMethods += doa.totalUserMethods;      //tmb

                totalNumAttribs += doa.numAttribs;
                if (doa.numAttribs > largestNumAttribs) {
                    largestNumAttribs = doa.numAttribs;
                    largestNumAttribsName = doa.simpleName;
                }
                totalNumPersistentAttribs += doa.numPersistentAttribs;
                totalNumNonPersistentAttribs += doa.numNonPersistentAttribs;
                totalNumRequiredAttribs += doa.numRequiredAttribs;
                totalNumPreventUserUpdateAttribs += doa.numPreventUserUpdateAttribs;
                totalNumCodedValueAttribs += doa.numCodedValueAttribs;
                totalNumValidationAttribs += doa.numValidationAttribs;
                totalNumDerivedAttribs += doa.numDerivedAttribs;
                totalNumSumAttribs += doa.numSumAttribs;
                totalNumCountAttribs += doa.numCountAttribs;
                totalNumParentReplicateAttribs += doa.numParentReplicateAttribs;
                totalNumFormulaAttribs += doa.numFormulaAttribs;
                totalNumDefaultAttribs += doa.numDefaultAttribs;
                totalNumConstraints += doa.numConstraints;
                totalNumActions += doa.numActions;

            }
        }

        // Loop through relationships
        IXMLDOMNodeList relObj = (IXMLDOMNodeList) myDocument.getElementsByTagName("Relationship");
        IXMLDOMNode relnode;
        //new relns here
        while ((relnode = relObj.nextNode()) != null) {
            IXMLDOMNodeList childNodes = (IXMLDOMNodeList) relnode.getChildNodes();
            IXMLDOMNode childnode;
            while ((childnode = childNodes.nextNode()) != null) {
                String relFileName = childnode.getText();
                System.out.println("Relationship file : " + relFileName);

                // Archimedes
                RelationshipAnalysis ra = new RelationshipAnalysis(rootDirectory, relFileName);
                ra.repos = repos; // tmb
                ra.analyze();

                DataObjectAnalysis parent = (DataObjectAnalysis) dataObjects.get(ra.parentName);
                DataObjectAnalysis child = (DataObjectAnalysis) dataObjects.get(ra.childName);
                for (int i = 0; i < ra.numRelationships; i++) {

                    String relEntry[] = (String[]) ra.relationships.elementAt(i);
                    String childEntry[] = new String[3];
                    childEntry[0] = ra.childName;
                    childEntry[1] = relEntry[1];
                    childEntry[2] = relEntry[2];
                    parent.children.add(childEntry);
                    String parentEntry[] = new String[3];
                    parentEntry[0] = ra.parentName;
                    parentEntry[1] = relEntry[0];
                    parentEntry[2] = relEntry[2];
                    child.parents.add(parentEntry);
                }

                parent.numEnforcedRelationships += ra.numEnforcedRels;
                child.numEnforcedRelationships += ra.numEnforcedRels;
                numRelationships += ra.numRelationships;
                numEnforcedRelationships += ra.numEnforcedRels;
            }
        }
        // Loop through query objects
        dbObj = (IXMLDOMNodeList) myDocument.getElementsByTagName("QueryObject");
        while ((node = dbObj.nextNode()) != null) {
            IXMLDOMNodeList childNodes = (IXMLDOMNodeList) node.getChildNodes();
            IXMLDOMNode childnode;
            while ((childnode = childNodes.nextNode()) != null) {
                String objectName = childnode.getText();
                System.out.println("Query object : " + objectName);

                QueryObjectAnalysis qoa = new QueryObjectAnalysis(rootDirectory, objectName);
                qoa.repos = this.repos;

                queryObjects.put(qoa.simpleName, qoa);
                qoa.analyze();

                totalQOerrors += qoa.errors.size();
                if (qoa.errors.size() > 0)
                    errors.addAll(qoa.errors);

                // Add the notes to the repository analysis
                if (qoa.notes.size() > 0)
                    notes.addAll(qoa.notes);

                totalQOlines += qoa.totalLines;
                totalQOgeneratedLines += qoa.totalGeneratedLines;
                totalQOuserLines += qoa.totalUserLines;

                totalLines += qoa.totalLines;
                totalGeneratedLines += qoa.totalGeneratedLines;
                totalUserLines += qoa.totalUserLines;

                totalNumQueryAttribs += qoa.numAttribs;
                totalNumQueryPersistentAttribs += qoa.numPersistentAttribs;
                totalNumQueryNonPersistentAttribs += qoa.numNonPersistentAttribs;
                totalNumQueryAggregatedAttribs += qoa.numAggregatedAttribs;
                totalNumQueryDerivedAttribs += qoa.numDerivedAttribs;
            }
        }

        // Loop through the HTML apps
        dbObj = (IXMLDOMNodeList) myDocument.getElementsByTagName("HTMLApplications");
        while ((node = dbObj.nextNode()) != null) {
            IXMLDOMNodeList childNodes = (IXMLDOMNodeList) node.getChildNodes();
            IXMLDOMNode childnode;
            // get the childnode and check to see if it is the FileName node
            while ((childnode = childNodes.nextNode()) != null) {
                String appName = childnode.getText();
                System.out.println("HTML app : " + appName);

                HTMLApplicationAnalysis hta = new HTMLApplicationAnalysis(rootDirectory, appName);
                htmlApps.put(hta.name, hta);
                hta.repos = this.repos;
                hta.analyze();

                totalHTMLAppsErrors += hta.errors.size();
                if (hta.errors.size() > 0)
                    errors.addAll(hta.errors);

                totalNumHTMLpages += hta.numForms;
                totalNumRegenHTMLpages += (hta.numForms - hta.numNonRegenForms);
                totalNumForms += hta.numForms;

                totalHTMLlines += hta.totalLines;
                totalHTMLgeneratedLines += hta.totalGeneratedLines;
                totalHTMLuserLines += hta.totalUserLines;

                totalLines += hta.totalLines;
                totalGeneratedLines += hta.totalGeneratedLines;
                totalUserLines += hta.totalUserLines;
                totalNumFormDataObjects += hta.totalNumFormDataObjects; //tmb
                totalNumFormPickObjects += hta.totalNumFormPickObjects; //tmb
                this.totalHTMLMethods += hta.totalMethods;
                totalHTMLGeneratedMethods += hta.totalGeneratedMethods;
                totalHTMLUserMethods += hta.totalUserMethods;

            }
        }

        // Loop through the Java apps
        dbObj = (IXMLDOMNodeList) myDocument.getElementsByTagName("JavaApplications");
        while ((node = dbObj.nextNode()) != null) {
            IXMLDOMNodeList childNodes = (IXMLDOMNodeList) node.getChildNodes();
            IXMLDOMNode childnode;
            // get the childnode and check to see if it is the FileName node
            while ((childnode = childNodes.nextNode()) != null) {
                String appName = childnode.getText();
                System.out.println("Java app : " + appName);

                JavaApplicationAnalysis jaa = new JavaApplicationAnalysis(rootDirectory, appName);
                javaApps.put(jaa.name, jaa);
                jaa.repos = this.repos;
                jaa.analyze();

                totalJavaAppsErrors += jaa.errors.size();
                if (jaa.errors.size() > 0)
                    errors.addAll(jaa.errors);

                totalNumJavaForms += jaa.numForms;
                totalNumRegenJavaForms += (jaa.numForms - jaa.numNonRegenForms);
                totalNumForms += jaa.numForms;

                totalJavaLines += jaa.totalLines;
                totalJavaGeneratedLines += jaa.totalGeneratedLines;
                totalJavaUserLines += jaa.totalUserLines;

                totalLines += jaa.totalLines;
                totalGeneratedLines += jaa.totalGeneratedLines;
                totalUserLines += jaa.totalUserLines;
                totalNumFormDataObjects += jaa.totalNumFormDataObjects; //tmb
                totalNumFormPickObjects += jaa.totalNumFormPickObjects; //tmb

                totalJavaMethods += jaa.totalMethods;
                totalJavaUserMethods += jaa.totalUserMethods;
                totalJavaGeneratedMethods += jaa.totalGeneratedMethods;

            }
        }
        totalAppMethods = totalJavaMethods + totalHTMLMethods;
        this.totalAppGenMethods = totalJavaGeneratedMethods + totalHTMLGeneratedMethods;
        this.totalAppUserMethods = totalJavaUserMethods + totalHTMLUserMethods;

        // Loop through the other files
        dbObj = (IXMLDOMNodeList) myDocument.getElementsByTagName("SourceFile");
        while ((node = dbObj.nextNode()) != null) {
            IXMLDOMNodeList childNodes = (IXMLDOMNodeList) node.getChildNodes();
            IXMLDOMNode childnode;
            // get the childnode and check to see if it is the FileName node
            while ((childnode = childNodes.nextNode()) != null) {
                if (!childnode.getBaseName().equals("FileName"))
                    continue;
                String fileName = childnode.getText();
                if (!fileName.endsWith(".java"))
                    continue;
                System.out.println("Other file : " + fileName);

                OtherFileAnalysis ofa = new OtherFileAnalysis(rootDirectory, fileName);
                ofa.analyze();
                otherFiles.put(ofa.simpleName, ofa);
                OtherFileDO of = new OtherFileDO(repos.getRepositoryName(), ofa.simpleName);
                repos.addChild(of);
                totalOtherErrors += ofa.errors.size();
                if (ofa.errors.size() > 0)
                    errors.addAll(ofa.errors);

                totalNumOtherFiles++;
                totalOtherFilesLines += ofa.numCodeLines;
                totalLines += ofa.numCodeLines;
                totalUserLines += ofa.numCodeLines;
                totalOtherFileMethods += ofa.numMethods;
                of.setCounts(ofa.numCodeLines, 0, ofa.numMethods, 0);
            }
        }
        repos.setCounts(this.totalUserLines, this.totalGeneratedLines, this.totalUserMethods, this.totalGenMethods);
        repos.analyzeWhereUsed();
    }
}