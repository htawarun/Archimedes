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

import com.bandsoftware.data.ApplicationDO;
import com.bandsoftware.data.FormDO;
import com.bandsoftware.data.RepositoryDO;
import com.datachannel.xml.om.Document;
import com.datachannel.xml.om.IXMLDOMNamedNodeMap;
import com.datachannel.xml.om.IXMLDOMNode;
import com.datachannel.xml.om.IXMLDOMNodeList;
import com.datachannel.xml.util.IXMLDataType;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

/**
 * Title:
 * Description:
 * Copyright:    Copyright Versata, Inc. 2001
 * Company: Versata
 *
 * @author Max Tardiveau, Versata
 * @version $Id: ApplicationAnalysis.java,v 1.1.1.1 2003/04/07 21:48:39 Tyler Exp $
 */

public class ApplicationAnalysis extends ArtefactAnalysis {
    public String rootDirectory;
    public String name;

    public int numForms = 0;
    public int numNonRegenForms = 0;
    public int totalNumFormDataObjects = 0; //tmb
    public int totalNumFormPickObjects = 0; //tmb


    public int totalLines;
    public int totalGeneratedLines;
    public int totalUserLines;

    public int totalUserMethods;            //tmb
    public int totalGeneratedMethods;       //tmb
    public int totalMethods;                //tmb

    // How many lines are typically incorrectly counted as user lines in a form
    // This is set in the subclasses
    public int perFormAdjust = 0;
    public RepositoryDO repos = null;
    ApplicationDO app = null;

    public Map forms = new TreeMap();

    /////////////////////////////////////////////////////////////////////

    public ApplicationAnalysis(String rootDirectory, String name) {
        this.rootDirectory = rootDirectory;
        this.name = name;
        int slashIndex = name.lastIndexOf('\\');
        if (slashIndex != -1) {
            this.name = name.replace('\\', '/');
        }
    }

    ////////////////////////////////////////////////////////////////////

    public void countPages(boolean isJava) {
        Document appDocument = new Document();
        FormDO form = null;
//Turn off the validation flag for better performance.
        appDocument.setValidateOnParse(false);
/*
        turn on the validation flag if a DTD is provided internally or externally
        for document validation. The default of validateOnParse is "true".
        */
//myDocument.setValidateOnParse(true);

//set the validating parser that supports data types
        appDocument.setParserClassName("com.datachannel.xml.tokenizer.parser.XMLDOMParser");
//define a dataType
        IXMLDataType nodeTypedValue;

        try {
            //load and parse the XML document
            String appXmlFileName;
            if (isJava)
                appXmlFileName = rootDirectory + "/Source/ClientApps/JavaApps/" + name + ".app/" + name + ".xml";
            else
                appXmlFileName = rootDirectory + "/Source/ClientApps/HTMLApps/" + name + ".app/" + name + ".xml";
            appDocument.load(appXmlFileName);

            // declare and initialize objects / vars
            IXMLDOMNodeList dbObj;
            IXMLDOMNode node;

            // Count lines for the app and the servlet
            if (!isJava) {
                FormAnalysis foa = new FormAnalysis(name);
                foa.isForm = false;
                forms.put(name, foa);
                String appFileName = rootDirectory + "/Source/ClientApps/HTMLApps/" +
                        name + ".app/" + name + ".java";
                int appCount[] = countUserVersataLines(appFileName);
                if (appCount != null) {
                    foa.isRegen = true;
                    foa.numLines = appCount[0] + appCount[1];
                    foa.numGenLines = appCount[0] + 7;
                    foa.numUserLines = appCount[1] - 7; // Typically 7 lines are incorectly counted as user-written
                    foa.totalUserMethods = appCount[7] - 6;
                    totalLines += foa.numLines;
                    totalGeneratedLines += foa.numGenLines;
                    totalUserLines += appCount[1] - 7;
                    totalUserMethods += appCount[7] - 6;  //tmb adjust for constructors
                }

                String appBaseFileName = rootDirectory + "/Cache/ClientApps/HTMLApps/" +
                        name + ".app/" + name + "Base.java";
                int appBaseCount[] = countUserVersataLines(appBaseFileName);
                if (appBaseCount != null) {
                    foa.numLines += appBaseCount[0] + appBaseCount[1];
                    foa.numGenLines += appBaseCount[0] + appBaseCount[1];
                    foa.totalGeneratedMethods = appBaseCount[7] - 6;
                    totalLines += foa.numLines;
                    totalGeneratedLines += foa.numGenLines;
                    totalGeneratedMethods += appBaseCount[7] - 6; // adjust for constructors tmb
                }

                foa = new FormAnalysis(name + "Servlet");
                foa.isForm = false;
                forms.put(name + "Servlet", foa);
                String servletFileName = rootDirectory + "/Source/ClientApps/HTMLApps/" +
                        name + ".app/" + name + "Servlet.java";
                int servletCount[] = countUserVersataLines(servletFileName);
                if (servletCount != null) {
                    foa.isRegen = true;
                    foa.numLines = servletCount[0] + servletCount[1];
                    foa.numGenLines = servletCount[0] + 13;
                    foa.numUserLines = servletCount[1] - 13; // Typically 13 lines are incorectly counted as user-written
                    foa.totalGeneratedMethods = servletCount[7] - 6;
                    totalLines += foa.numLines;
                    totalGeneratedLines += foa.numGenLines;
                    totalUserLines += foa.numUserLines;
                    totalUserMethods += servletCount[7] - 6;
                }

                String servletBaseFileName = rootDirectory + "/Cache/ClientApps/HTMLApps/" +
                        name + ".app/" + name + "BaseServlet.java";
                int servletBaseCount[] = countUserVersataLines(servletBaseFileName);
                if (servletBaseCount != null) {
                    foa.numLines += servletBaseCount[0] + servletBaseCount[1];
                    foa.numGenLines += servletBaseCount[0] + servletBaseCount[1];
                    foa.totalGeneratedMethods = servletBaseCount[7];
                    totalLines += servletBaseCount[0] + servletBaseCount[1];
                    totalGeneratedLines += servletBaseCount[0] + servletBaseCount[1];
                    totalGeneratedMethods += servletBaseCount[7] - 2;
                }
            } else // Count the Java applet
            {
                FormAnalysis foa = new FormAnalysis(name);
                foa.isForm = false;

                forms.put(name, foa);
                String appFileName = rootDirectory + "/Source/ClientApps/JavaApps/" +
                        name + ".app/" + name + ".java";
                //cnt = dataObj.parseMethods(repos.getRepositoryName(), this.simpleName, repos.getRepositoryName()+ "."+ this.simpleName + "Impl",true);
                //dataObj.totalUserMethodCount = cnt;
                //cnt = dataObj.parseMethods(repos.getRepositoryName(), this.simpleName, repos.getRepositoryName()+ "."+ this.simpleName + "BaseImpl",false);
                //dataObj.totalGenMethodCount = cnt;


                int appCount[] = countUserVersataLines(appFileName);
                if (appCount != null) {
                    foa.isRegen = true;
                    foa.numLines = appCount[0] + appCount[1];
                    foa.numGenLines = appCount[0] + 0;
                    foa.numUserLines = appCount[1] - 0;
                    foa.totalUserMethods = appCount[7] - 6;
                    totalLines += appCount[0] + appCount[1];
                    totalGeneratedLines += appCount[0] + 0;
                    totalUserLines += appCount[1] - 0; // Typically ? lines are incorectly counted as user-written
                    totalUserMethods += (appCount[7] - 6); // adjust constructors tmb
                }

                String appBaseFileName = rootDirectory + "/Cache/ClientApps/JavaApps/" +
                        name + ".app/" + name + "Base.java";
                int appBaseCount[] = countUserVersataLines(appBaseFileName);
                if (appBaseCount != null) {
                    foa.numLines += appBaseCount[0] + appBaseCount[1];
                    foa.numGenLines += appBaseCount[0] + appBaseCount[1];
                    foa.totalGeneratedMethods = appBaseCount[7] - 6;
                    totalLines += appBaseCount[0] + appBaseCount[1];
                    totalGeneratedLines += appBaseCount[0] + appBaseCount[1];
                    totalGeneratedMethods += appBaseCount[7] - 6; //tmb adjust
                }
            }

            //looping on all the objects that contain the form tag
            dbObj = (IXMLDOMNodeList) appDocument.getElementsByTagName("Form");
            while ((node = dbObj.nextNode()) != null) {
                String formName = node.getText();

                // If name of form contains a dot, ignore -- it's an external form
                if (formName.indexOf('.') != -1)
                    continue;

                numForms++;
                FormAnalysis foa = new FormAnalysis(formName);
                forms.put(formName, foa);

                String javaFile = formName + ".java";
                String srceFile = rootDirectory + "/Source/ClientApps";
                if (!isJava)
                    srceFile = srceFile + "/HTMLApps/" + name + ".app/" + javaFile; //tmb
                else
                    srceFile = srceFile + "/JavaApps/" + name + ".app/" + javaFile; //tmb

                // Ignore if StartupPage or StartupPageBase not found
                if (srceFile.endsWith("/StartupPage.java") || srceFile.endsWith("/StartupForm.java")) {
                    File f = new File(srceFile);
                    if (!f.exists())
                        continue;
                }

                //countUserVersataLines(srceFile);
                int userCount[] = countUserVersataLines(srceFile);
                if (userCount != null) {
                    foa.numLines = userCount[0] + userCount[1];
                    foa.numGenLines = userCount[0] + perFormAdjust;
                    foa.numUserLines = userCount[1] - perFormAdjust;
                    foa.totalUserMethods = userCount[7];
                    totalLines += userCount[0] + userCount[1];
                    totalGeneratedLines += userCount[0] + perFormAdjust;
                    totalUserLines += userCount[1] - perFormAdjust;
                    //totalUserMethods += userCount[7]; // tmb no way to tell user metthods
                    totalUserMethods += userCount[7];
                    //userFileNumTextLines += userCount[2];
                    //userFileNumNonBlankLines += userCount[3];
                    //userFileNumCommentLines += userCount[4];
                    //userFileNumMiscLines += userCount[5];
                }

                // ----tmb added this to count Base Versata Lines----
                javaFile = formName + "Base.java";
                srceFile = rootDirectory + "/Cache/ClientApps";
                if (!isJava)
                    srceFile = srceFile + "/HTMLApps/" + name + ".app/" + javaFile; //tmb
                else
                    srceFile = srceFile + "/JavaApps/" + name + ".app/" + javaFile; //tmb

                // Ignore if StartupPage or StartupPageBase not found
                if (srceFile.endsWith("/StartupPageBase.java") || srceFile.endsWith("/StartupFormBase.java")) {
                    File f = new File(srceFile);
                    if (!f.exists())
                        continue;
                }

                int baseCount[] = countUserVersataLines(srceFile);
                if (baseCount != null) {
                    foa.numLines += baseCount[0] + baseCount[1];
                    foa.numGenLines += baseCount[0] + baseCount[1];
                    foa.totalGeneratedMethods = baseCount[7];
                    totalLines += baseCount[0] + baseCount[1];
                    totalGeneratedLines += baseCount[0] + baseCount[1];
                    totalGeneratedMethods += baseCount[7];
                    //totalUserLines += baseCount[1];
                    //baseFileNumTextLines += baseCount[2];
                    //baseFileNumNonBlankLines += baseCount[3];
                    //baseFileNumCommentLines += baseCount[4];
                    //baseFileNumMiscLines += baseCount[5];
                }
                //---------------------------------------------------

                // and open the corresponding XML file to check if this is a customized form
                try {
                    if (!isJava)
                        srceFile = rootDirectory + "/Source/ClientApps/HTMLApps/" + name + ".app/" + formName + ".xml"; //  tmb
                    else
                        srceFile = rootDirectory + "/Source/ClientApps/JavaApps/" + name + ".app/" + formName + ".xml"; // tmb

                    Document frmDocument = new Document();
                    frmDocument.setValidateOnParse(false);
                    frmDocument.setParserClassName("com.datachannel.xml.tokenizer.parser.XMLDOMParser");
                    frmDocument.load(srceFile);


                    IXMLDOMNodeList ndbobj = (IXMLDOMNodeList) frmDocument.getElementsByTagName("Form");
                    IXMLDOMNode nnode = ndbobj.nextNode();

                    form = new FormDO(repos.getRepositoryName(), app.getApplicationName(), formName, nnode); // tmb
                    app.addChild(form);
                    totalNumFormDataObjects += form.countDataObjects(); //tmb
                    totalNumFormPickObjects += form.countPickObjects(); //tmb

                    foa.totalNumFormDataObjects = form.countDataObjects();
                    foa.totalNumFormPickObjects = form.countPickObjects();
                    form.setCounts(this.totalUserLines, this.totalGeneratedLines, this.totalUserMethods, this.totalGeneratedMethods);
                    IXMLDOMNamedNodeMap nnodeMap = (IXMLDOMNamedNodeMap) nnode.getAttributes();
                    IXMLDOMNode customForm = (IXMLDOMNode) nnodeMap.getNamedItem("HasCustomFormLayout");
                    if (customForm != null && customForm.getNodeValue().equalsIgnoreCase("true")) {
                        numNonRegenForms++;
                        foa.isRegen = false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    errors.add("Error while checking whether form is regeneratable : " + srceFile + " : " + e.getMessage());
                }
            }
            totalMethods = totalGeneratedMethods + totalUserMethods;

        } catch (Exception e) {
            e.printStackTrace();
            errors.add("CountForms : " + e.toString() + " : " + e.getMessage());
        }
    }
}