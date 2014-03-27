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
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

public class OtherFileAnalysis extends ArtefactAnalysis {
    public String rootDirectory;
    public String fileName;
    public String simpleName;
    public int numCodeLines = 0;
    public int numMethods = 0;

    ///////////////////////////////////////////////////////////////////////

    public OtherFileAnalysis(String rootDirectory, String fileName) {
        this.rootDirectory = rootDirectory;
        this.fileName = fileName.replace('\\', '/');
        int lastSlash = this.fileName.lastIndexOf('/');
        if (lastSlash != -1)
            simpleName = this.fileName.substring(lastSlash + 1);
        else
            simpleName = this.fileName;
    }

    //////////////////////////////////////////////////////////////////////

    public void analyze() {
        String fullFileName = rootDirectory + "/Source/VLS/" + fileName;
        int baseCount[] = countUserVersataLines(fullFileName);
        if (baseCount != null) {
            numCodeLines = baseCount[0] + baseCount[1];
            numMethods = baseCount[7]; // tmb
        }
    }
}