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

import com.bandsoftware.data.RepositoryDO;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

public class FormAnalysis extends ArtefactAnalysis {
    public String formName;

    public int numLines = 0;
    public int numGenLines = 0;
    public int numUserLines = 0;

    public int totalUserMethods;            //tmb
    public int totalGeneratedMethods;       //tmb
    public int totalMethods;                //tmb

    public int totalNumFormDataObjects = 0; //tmb
    public int totalNumFormPickObjects = 0; //tmb

    public boolean isRegen = true;
    public boolean isForm = true;
    public RepositoryDO repos;

    /////////////////////////////////////////////////////////////////////
    // isForm = false for non-forms like app and servlet

    public FormAnalysis(String formName) {
        this.formName = formName;
    }
}