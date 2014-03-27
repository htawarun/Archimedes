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


/**
 * Title:
 * Description:
 * Copyright:    Copyright Versata, Inc. 2001
 * Company: Versata
 *
 * @author Max Tardiveau, Versata
 *         modifed: Tyler Band, Band Software Desing, LLC    (added method count)
 * @version $Id: BusinessObjectAnalysis.java,v 1.1.1.1 2003/04/07 21:48:39 Tyler Exp $
 */

public abstract class BusinessObjectAnalysis extends ArtefactAnalysis {
    public String rootDirectory;
    public String name;  // Qualified name, with packages
    public String simpleName;   // Name of the object

    public String baseFileName;
    public int baseFileNumLines;
    public int baseFileNumUserLines;
    public int baseFileNumGenLines;
    public int baseFileNumTextLines;
    public int baseFileNumNonBlankLines;
    public int baseFileNumCommentLines;
    public int baseFileNumMiscLines;
    public int baseFileNumMethods;  //tmb

    public String userFileName;
    public int userFileNumLines;
    public int userFileNumUserLines;
    public int userFileNumGenLines;
    public int userFileNumTextLines;
    public int userFileNumNonBlankLines;
    public int userFileNumCommentLines;
    public int userFileNumMiscLines;
    public int userFileNumMethods;     //tmb

    public int totalLines;
    public int totalGeneratedLines;
    public int totalUserLines;

    public int totalUserMethods = 0;            //tmb
    public int totalGeneratedMethods = 0;       //tmb
    public int totalMethods = 0;                //tmb

    ///////////////////////////////////////////////////////////////////////

    public BusinessObjectAnalysis(String rootDirectory, String name) {
        this.rootDirectory = rootDirectory;
        this.name = name;
        int slashIndex = name.lastIndexOf('\\');
        if (slashIndex != -1) {
            this.name = name.replace('\\', '/');
            simpleName = name.substring(slashIndex + 1);
        } else
            simpleName = name;
    }

    //////////////////////////////////////////////////////////////////////

    public abstract void analyze();

    //////////////////////////////////////////////////////////////////////

    public void countCodeLines() {
        // Now analyze the code
        baseFileName = rootDirectory + "/Cache/VLS/" + name + "BaseImpl.java";
        int baseCount[] = countUserVersataLines(baseFileName);
        if (baseCount != null) {
            baseFileNumLines = baseCount[0] + baseCount[1];
            baseFileNumGenLines = baseCount[0];
            baseFileNumUserLines = baseCount[1];
            baseFileNumTextLines = baseCount[2];
            baseFileNumNonBlankLines = baseCount[3];
            baseFileNumCommentLines = baseCount[4];
            baseFileNumMiscLines = baseCount[5];
            baseFileNumMethods = baseCount[7] - 3; // tmb adjust base constructor and variables
        }

        userFileName = rootDirectory + "/Source/VLS/" + name + "Impl.java";
        int userCount[] = countUserVersataLines(userFileName);
        if (userCount != null) {
            userFileNumLines = userCount[0] + userCount[1];
            userFileNumGenLines = userCount[0];
            userFileNumUserLines = userCount[1];
            userFileNumTextLines = userCount[2];
            userFileNumNonBlankLines = userCount[3];
            userFileNumCommentLines = userCount[4];
            userFileNumMiscLines = userCount[5];
            userFileNumMethods = userCount[7] - 5; // tmb adjust out the constrcutors and min methods used to create the class
        }

        totalLines = baseFileNumLines + userFileNumLines;
        totalGeneratedLines = baseFileNumGenLines + userFileNumGenLines;
        totalUserLines = baseFileNumUserLines + userFileNumUserLines;

        //only use the source count if reflection did not find the compiled code
        if (totalMethods == 0) {
            totalMethods = baseFileNumMethods + userFileNumMethods;
            totalGeneratedMethods = baseFileNumMethods;
            totalUserMethods = userFileNumMethods;
        }
    }
}