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

import javax.swing.*;
import java.awt.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright Versata, Inc. 2001
 * Company: Versata
 *
 * @author Max Tardiveau, Versata
 * @version $Id: AutomationAnalyzer.java,v 1.1.1.1 2003/04/07 21:48:39 Tyler Exp $
 */

public class AutomationAnalyzer {
    boolean packFrame = false;

    static public String versionString = "0.6";

    /**
     * Construct the application
     */
    public AutomationAnalyzer(String repDirName, String repFileName) {
        MainWindow frame = new MainWindow(repDirName, repFileName);
        //Validate frames that have preset sizes
        //Pack frames that have useful preferred size info, e.g. from their layout
        if (packFrame) {
            frame.pack();
        } else {
            frame.validate();
        }
        //Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        frame.setVisible(true);
    }

    /////////////////////////////////////////////////////////////////////////

    /**
     * Main method
     */

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (args.length == 0)
            new AutomationAnalyzer(null, null);
        else if (args.length == 1)
            new AutomationAnalyzer(args[0], null);
        else if (args.length >= 2)
            new AutomationAnalyzer(args[0], args[1]);
    }
}