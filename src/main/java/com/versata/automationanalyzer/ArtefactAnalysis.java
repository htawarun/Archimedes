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

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Vector;

/**
 * Title:
 * Description:
 * Copyright:    Copyright Versata, Inc. 2001
 * Company: Versata
 *
 * @author Max Tardiveau, Versata
 * @version $Id: ArtefactAnalysis.java,v 1.1.1.1 2003/04/07 21:48:39 Tyler Exp $
 */

public class ArtefactAnalysis {
    public Vector errors = new Vector();
    public Vector notes = new Vector();

    public ArtefactAnalysis() {
    }

    ////////////////////////////////////////////////////////////////////

    public int[] countUserVersataLines(String sourceFile) {
        int versataLines = 0;
        int userLines = 0;
        int textLines = 0;
        int nonBlankLines = 0;
        int commentLines = 0;
        int commentEventLines = 0;
        int miscLines = 0;
        int countUserMethods = 0;
        FileReader reader = null;
        LineNumberReader lineReader = null;

        boolean isBase = false;
        if (sourceFile.endsWith("BaseImpl.java"))
            isBase = true;

        String _VERSATA_CODE = "//{{";
        String _END_VERSATA_CODE = "//END_";
        String _EVENT_CODE = "//{{EVENT_CODE";
        String _END_EVENT_CODE = "//END_EVENT_CODE}}";

        boolean versata = true;
        boolean isExtendedComment = false;
        boolean eventcode = false;
        boolean isComment = false;
        boolean isEventCodeCommentedOut = false;
        String lineText;

        try {
            reader = new FileReader(sourceFile);
            lineReader = new LineNumberReader(reader);

            // read and count lines
            int construct_level = 0;
            while ((lineText = lineReader.readLine()) != null) {
                textLines++;
                // remove leading and trailing blanks
                String trimText = lineText.trim();
                if (trimText.length() == 0)
                    continue;

                nonBlankLines++;

                // If this is // DELETED_BEGIN then count user-written lines that could be deleted
                if (trimText.startsWith("// DELETED_BEGIN")) {
                    isEventCodeCommentedOut = true;
                    continue;
                }
                if (trimText.startsWith("// DELETED_END")) {
                    isEventCodeCommentedOut = true;
                    continue;
                }

                // Check for comments
                if (trimText.startsWith("/*")) {        // starts a larger comment section
                    isExtendedComment = true;
                }

                if ((isExtendedComment == false)) {        // if not an extended comment section

                    // check to see if this is Versata code or User code

                    // check to see if versata code starts
                    // switch to versata code line counter
                    // count lines then switch back to user counter

                    if (trimText.startsWith(_EVENT_CODE)) {
                        eventcode = true;
                        versata = false;
                    }
                    if (trimText.startsWith(_END_EVENT_CODE)) {
                        eventcode = false;
                        versata = true;
                    }
                    if (!eventcode) {
                        if (trimText.startsWith(_VERSATA_CODE)) {
                            versata = true;
                            construct_level++; // could be nested versata code
                        }
                        if (trimText.startsWith(_END_VERSATA_CODE)) {
                            if (construct_level < 2) {  // lowest nest level ?
                                versata = false;
                                construct_level = 0;// set nest level to 0
                            } else
                                construct_level--;  // decrement nest level
                        }
                    }
                    if (!trimText.startsWith("//")) {    // is not a comment line
                        if (trimText.equals("{") || trimText.equals("}")) {  // do not count these
                            miscLines++;
                        } else {
                            if (versata)
                                versataLines++;            // is a versata generated line
                            else {
                                if (isBase)
                                    versataLines++;
                                else
                                    userLines++;            // is a user written line
                            }
                        }
                    } else {
                        commentLines++;
                        if (isEventCodeCommentedOut)
                            commentEventLines++;
                    }
                } else
                    commentLines++;
                if (trimText.endsWith("*/")) {            // ends a user comment section
                    isExtendedComment = false;
                }

                if (trimText.startsWith("public") || trimText.startsWith("private")) {
                    countUserMethods++;
                }
            }
            int result[] = new int[8];
            result[0] = versataLines;
            result[1] = userLines;
            result[2] = textLines;
            result[3] = nonBlankLines;
            result[4] = commentLines;
            result[5] = miscLines;
            result[6] = commentEventLines;
            result[7] = countUserMethods;
            return result;
        } catch (Exception e) {
            this.errors.add("Error while counting user vs. generated lines for file " + sourceFile + " : " + e.getMessage());
            return null;
        } finally {
            if (lineReader != null) {
                //noinspection EmptyCatchBlock
                try {
                    lineReader.close();
                } catch (Exception ex) {
                }
            }
            if (reader != null) {
                //noinspection EmptyCatchBlock
                try {
                    reader.close();
                } catch (Exception ex) {
                }
            }
        }
    }
}