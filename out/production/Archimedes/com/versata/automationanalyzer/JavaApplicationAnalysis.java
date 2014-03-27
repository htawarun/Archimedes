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

/**
 * Title:
 * Description:
 * Copyright:    Copyright Versata, Inc. 2001
 * Company: Versata
 *
 * @author Max Tardiveau, Versata
 * @version $Id: JavaApplicationAnalysis.java,v 1.1.1.1 2003/04/07 21:48:39 Tyler Exp $
 */

public class JavaApplicationAnalysis extends ApplicationAnalysis {
    public JavaApplicationAnalysis(String rootDirectory, String name) {
        super(rootDirectory, name);
        perFormAdjust = 5;
    }

    /////////////////////////////////////////////////////////////////////

    public void analyze() {
        app = new ApplicationDO(repos.getRepositoryName(), this.name);
        app.setIsJava("True");
        repos.addChild(app);
        countPages(true);
        app.setCounts(this.totalUserLines, this.totalGeneratedLines, this.totalUserMethods, this.totalGeneratedMethods);

    }

    public String getAppType() {
        return "Java";
    }
}