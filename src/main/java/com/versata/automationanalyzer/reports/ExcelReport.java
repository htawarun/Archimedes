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

package com.versata.automationanalyzer.reports;

import com.versata.automationanalyzer.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Vector;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

public class ExcelReport {
    public Vector errors = new Vector();
    public String reportPath = "./";
    private RepositoryAnalysis ra;
    private StringBuffer o = new StringBuffer();

    ///////////////////////////////////////////////////////////////////////

    public ExcelReport(RepositoryAnalysis ra) {
        if (ra == null)
            throw new IllegalArgumentException("ExcelReport cannot be built without a RepositoryAnalysis");
        this.ra = ra;
    }

    ///////////////////////////////////////////////////////////////////////

    public void writeReport(File outFile) {
        FileOutputStream outStr;
        PrintWriter out;
        try {
            outStr = new FileOutputStream(outFile);
            out = new PrintWriter(outStr);
        } catch (Exception ex) {
            errors.add("Error while creating Excel report : " + ex.toString());
            return;
        }

        o = new StringBuffer();

        // HTML Head
        o.append("<html xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n");
        o.append("xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n");
        o.append("xmlns=\"http://www.w3.org/TR/REC-html40\">\n");
        o.append("\n");
        o.append("<head>\n");
        o.append("<meta http-equiv=Content-Type content=\"text/html; charset=windows-1252\">\n");
        o.append("<meta name=ProgId content=Excel.Sheet>\n");
        o.append("<meta name=Generator content=\"Microsoft Excel 9\">\n");
        o.append("<link rel=File-List href=\"./TestSpreadsheet_files/filelist.xml\">\n");
        o.append("<link rel=Edit-Time-Data href=\"./TestSpreadsheet_files/editdata.mso\">\n");
        o.append("<link rel=OLE-Object-Data href=\"./TestSpreadsheet_files/oledata.mso\">\n");
        o.append("<!--[if gte mso 9]><xml>\n");
        o.append(" <o:DocumentProperties>\n");
        o.append("  <o:Author>Max Tardiveau</o:Author>\n");
        o.append("  <o:LastAuthor>Max Tardiveau</o:LastAuthor>\n");
        o.append("  <o:LastPrinted>2001-12-14T01:41:51Z</o:LastPrinted>\n");
        o.append("  <o:Created>2001-12-13T22:01:08Z</o:Created>\n");
        o.append("  <o:LastSaved>2001-12-14T01:43:07Z</o:LastSaved>\n");
        o.append("  <o:Company>Versata, Inc.</o:Company>\n");
        o.append("  <o:Version>9.3821</o:Version>\n");
        o.append(" </o:DocumentProperties>\n");
        o.append(" <o:OfficeDocumentSettings>\n");
        o.append("  <o:DownloadComponents/>\n");
        o.append("  <o:LocationOfComponents HRef=\"file:E:\\msowc.cab\"/>\n");
        o.append(" </o:OfficeDocumentSettings>\n");
        o.append("</xml><![endif]-->\n");
        o.append("<style>\n");
        o.append("<!--table\n");
        o.append("	{mso-displayed-decimal-separator:\"\\.\";\n");
        o.append("	mso-displayed-thousand-separator:\"\\,\";}\n");
        o.append("@page\n");
        o.append("	{margin:1.0in .75in 1.0in .75in;\n");
        o.append("	mso-header-margin:.5in;\n");
        o.append("	mso-footer-margin:.5in;}\n");
        o.append("tr\n");
        o.append("	{mso-height-source:auto;}\n");
        o.append("col\n");
        o.append("	{mso-width-source:auto;}\n");
        o.append("br\n");
        o.append("	{mso-data-placement:same-cell;}\n");
        o.append(".style0\n");
        o.append("	{mso-number-format:General;\n");
        o.append("	text-align:general;\n");
        o.append("	vertical-align:bottom;\n");
        o.append("	white-space:nowrap;\n");
        o.append("	mso-rotate:0;\n");
        o.append("	mso-background-source:auto;\n");
        o.append("	mso-pattern:auto;\n");
        o.append("	color:windowtext;\n");
        o.append("	font-size:10.0pt;\n");
        o.append("	font-weight:400;\n");
        o.append("	font-style:normal;\n");
        o.append("	text-decoration:none;\n");
        o.append("	font-family:Arial;\n");
        o.append("	mso-generic-font-family:auto;\n");
        o.append("	mso-font-charset:0;\n");
        o.append("	border:none;\n");
        o.append("	mso-protection:locked visible;\n");
        o.append("	mso-style-name:Normal;\n");
        o.append("	mso-style-id:0;}\n");
        o.append("td\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	padding-top:1px;\n");
        o.append("	padding-right:1px;\n");
        o.append("	padding-left:1px;\n");
        o.append("	mso-ignore:padding;\n");
        o.append("	color:windowtext;\n");
        o.append("	font-size:10.0pt;\n");
        o.append("	font-weight:400;\n");
        o.append("	font-style:normal;\n");
        o.append("	text-decoration:none;\n");
        o.append("	font-family:Arial;\n");
        o.append("	mso-generic-font-family:auto;\n");
        o.append("	mso-font-charset:0;\n");
        o.append("	mso-number-format:General;\n");
        o.append("	text-align:general;\n");
        o.append("	vertical-align:bottom;\n");
        o.append("	border:none;\n");
        o.append("	mso-background-source:auto;\n");
        o.append("	mso-pattern:auto;\n");
        o.append("	mso-protection:locked visible;\n");
        o.append("	white-space:nowrap;\n");
        o.append("	mso-rotate:0;}\n");
        o.append(".xl24\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	font-size:14.0pt;\n");
        o.append("	font-family:Arial, sans-serif;\n");
        o.append("	mso-font-charset:0;\n");
        o.append("	background:silver;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl25\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	background:silver;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl26\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	font-weight:700;\n");
        o.append("	font-family:Arial, sans-serif;\n");
        o.append("	mso-font-charset:0;\n");
        o.append("	font-size:10.0pt;\n");
        o.append("	color:white;\n");
        o.append("	background:blue;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl27\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	color:white;\n");
        o.append("	background:blue;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl28\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	mso-number-format:Percent;}\n");
        o.append(".xl29\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	font-size:18.0pt;\n");
        o.append("	font-family:Arial, sans-serif;\n");
        o.append("	mso-font-charset:0;}\n");
        o.append(".xl30\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	color:white;\n");
        o.append("	background:blue;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl31\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	mso-number-format:0%;}\n");
        o.append(".xl32\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	font-weight:700;\n");
        o.append("	font-family:Arial, sans-serif;\n");
        o.append("	mso-font-charset:0;\n");
        o.append("	color:white;\n");
        o.append("	background:blue;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl33\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	color:white;\n");
        o.append("	background:blue;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl34\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	font-weight:700;\n");
        o.append("	font-family:Arial, sans-serif;\n");
        o.append("	mso-font-charset:0;\n");
        o.append("	mso-number-format:0%;\n");
        o.append("	color:white;\n");
        o.append("	background:blue;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl35\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	font-weight:700;\n");
        o.append("	font-family:Arial, sans-serif;\n");
        o.append("	mso-font-charset:0;\n");
        o.append("	text-align:center;\n");
        o.append("	background:silver;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl36\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	font-family:Arial, sans-serif;\n");
        o.append("	mso-font-charset:0;\n");
        o.append("	background:silver;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl37\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	font-weight:700;\n");
        o.append("	font-family:Arial, sans-serif;\n");
        o.append("	mso-font-charset:0;\n");
        o.append("	mso-number-format:Percent;\n");
        o.append("	color:white;\n");
        o.append("	background:blue;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl37_2\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	font-weight:700;\n");
        o.append("	font-family:Arial, sans-serif;\n");
        o.append("	mso-font-charset:0;\n");
        o.append("	mso-number-format:Percent;\n");
        o.append("	text-align:right;\n");
        o.append("	color:white;\n");
        o.append("	background:blue;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl38\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	font-weight:700;\n");
        o.append("	font-family:Arial, sans-serif;\n");
        o.append("	mso-font-charset:0;\n");
        o.append("	color:white;\n");
        o.append("	background:blue;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl39\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	mso-number-format:0%;\n");
        o.append("   color:white;\n");
        o.append("	background:blue;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append("-->\n");
        o.append("</style>\n");
        o.append("<!--[if gte mso 9]><xml>\n");
        o.append(" <x:ExcelWorkbook>\n");
        o.append("  <x:ExcelWorksheets>\n");
        o.append("   <x:ExcelWorksheet>\n");
        o.append("    <x:Name>Sheet1</x:Name>\n");
        o.append("    <x:WorksheetOptions>\n");
        o.append("     <x:Print>\n");
        o.append("      <x:ValidPrinterInfo/>\n");
        o.append("      <x:VerticalResolution>600</x:VerticalResolution>\n");
        o.append("     </x:Print>\n");
        o.append("     <x:Selected/>\n");
        o.append("     <x:TopRowVisible>94</x:TopRowVisible>\n");
        o.append("     <x:Panes>\n");
        o.append("      <x:Pane>\n");
        o.append("       <x:Number>3</x:Number>\n");
        o.append("       <x:ActiveRow>136</x:ActiveRow>\n");
        o.append("       <x:ActiveCol>3</x:ActiveCol>\n");
        o.append("      </x:Pane>\n");
        o.append("     </x:Panes>\n");
        o.append("     <x:ProtectContents>False</x:ProtectContents>\n");
        o.append("     <x:ProtectObjects>False</x:ProtectObjects>\n");
        o.append("     <x:ProtectScenarios>False</x:ProtectScenarios>\n");
        o.append("    </x:WorksheetOptions>\n");
        o.append("   </x:ExcelWorksheet>\n");
        o.append("   <x:ExcelWorksheet>\n");
        o.append("    <x:Name>Sheet2</x:Name>\n");
        o.append("    <x:WorksheetOptions>\n");
        o.append("     <x:ProtectContents>False</x:ProtectContents>\n");
        o.append("     <x:ProtectObjects>False</x:ProtectObjects>\n");
        o.append("     <x:ProtectScenarios>False</x:ProtectScenarios>\n");
        o.append("    </x:WorksheetOptions>\n");
        o.append("   </x:ExcelWorksheet>\n");
        o.append("   <x:ExcelWorksheet>\n");
        o.append("    <x:Name>Sheet3</x:Name>\n");
        o.append("    <x:WorksheetOptions>\n");
        o.append("     <x:ProtectContents>False</x:ProtectContents>\n");
        o.append("     <x:ProtectObjects>False</x:ProtectObjects>\n");
        o.append("     <x:ProtectScenarios>False</x:ProtectScenarios>\n");
        o.append("    </x:WorksheetOptions>\n");
        o.append("   </x:ExcelWorksheet>\n");
        o.append("  </x:ExcelWorksheets>\n");
        o.append("  <x:WindowHeight>9255</x:WindowHeight>\n");
        o.append("  <x:WindowWidth>19830</x:WindowWidth>\n");
        o.append("  <x:WindowTopX>105</x:WindowTopX>\n");
        o.append("  <x:WindowTopY>4920</x:WindowTopY>\n");
        o.append("  <x:ProtectStructure>False</x:ProtectStructure>\n");
        o.append("  <x:ProtectWindows>False</x:ProtectWindows>\n");
        o.append(" </x:ExcelWorkbook>\n");
        o.append("</xml><![endif]-->\n");
        o.append("</head>\n");
        o.append("\n");
        o.append("<body link=blue vlink=purple>\n");
        o.append("\n");
        o.append("<table x:str border=0 cellpadding=0 cellspacing=0 width=1334 style='border-collapse:\n");
        o.append(" collapse;table-layout:fixed;width:1002pt'>\n");
        o.append(" <col width=64 span=2 style='width:48pt'>\n");
        o.append(" <col width=74 style='mso-width-source:userset;mso-width-alt:2706;width:56pt'>\n");
        o.append(" <col width=79 style='mso-width-source:userset;mso-width-alt:2889;width:59pt'>\n");
        o.append(" <col width=96 style='mso-width-source:userset;mso-width-alt:3510;width:72pt'>\n");
        o.append(" <col width=93 style='mso-width-source:userset;mso-width-alt:3401;width:70pt'>\n");
        o.append(" <col width=79 style='mso-width-source:userset;mso-width-alt:2889;width:59pt'>\n");
        o.append(" <col width=84 style='mso-width-source:userset;mso-width-alt:3072;width:63pt'>\n");
        o.append(" <col width=87 style='mso-width-source:userset;mso-width-alt:3181;width:65pt'>\n");
        o.append(" <col width=64 style='width:48pt'>\n");
        o.append(" <col width=78 style='mso-width-source:userset;mso-width-alt:2852;width:59pt'>\n");
        o.append(" <col width=64 span=5 style='width:48pt'>\n");
        o.append(" <col width=74 style='mso-width-source:userset;mso-width-alt:2706;width:56pt'>\n");
        o.append(" <col width=78 style='mso-width-source:userset;mso-width-alt:2852;width:59pt'>\n");

        // Document title
        o.append(" <tr height=31 style='height:23.25pt'>\n");
        o.append("  <td height=31 class=xl29 colspan=5 width=377 style='height:23.25pt;\n");
        o.append("  mso-ignore:colspan;width:283pt'>Versata repository analysis</td>\n");
        o.append("  <td width=93 style='width:70pt'></td>\n");
        o.append("  <td width=79 style='width:59pt'></td>\n");
        o.append("  <td width=84 style='width:63pt'></td>\n");
        o.append("  <td width=87 style='width:65pt'></td>\n");
        o.append("  <td width=64 style='width:48pt'></td>\n");
        o.append("  <td width=78 style='width:59pt'></td>\n");
        o.append("  <td width=64 style='width:48pt'></td>\n");
        o.append("  <td width=64 style='width:48pt'></td>\n");
        o.append("  <td width=64 style='width:48pt'></td>\n");
        o.append("  <td width=64 style='width:48pt'></td>\n");
        o.append("  <td width=64 style='width:48pt'></td>\n");
        o.append("  <td width=74 style='width:56pt'></td>\n");
        o.append("  <td width=78 style='width:59pt'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // Repository name
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl26 colspan=2 style='height:12.75pt;mso-ignore:colspan'>Repository name :</td>\n");
        o.append("  <td class=xl26>&nbsp;</td>\n");
        o.append("  <td colspan=4 style='mso-ignore:colspan'>");
        o.append(ra.name);
        o.append("</td>\n");
        o.append("  <td colspan=11 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // Repository location
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl26 colspan=3 style='height:12.75pt;mso-ignore:colspan'>Repository location :</td>\n");
        o.append("  <td colspan=7 style='mso-ignore:colspan'>");
        o.append(ra.rootDirectory);
        o.append("</td>\n");
        o.append("  <td colspan=18 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // Analysis date
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl26 colspan=2 style='height:12.75pt;mso-ignore:colspan'>Analysis date :</td>\n");
        o.append("  <td class=xl26>&nbsp;</td>\n");
        o.append("  <td colspan=2 style='mso-ignore:colspan'>");
        o.append(ra.analysisDate.toString());
        o.append("</td>\n");
        o.append("  <td colspan=13 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // Analysis host
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl26 colspan=3 style='height:12.75pt;mso-ignore:colspan'>Analysis run on machine :</td>\n");
        o.append("  <td colspan=4 style='mso-ignore:colspan'>");
        o.append(ra.analysisHost);
        o.append("</td>\n");
        o.append("  <td colspan=11 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // Analysis user
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl26 colspan=3 style='height:12.75pt;mso-ignore:colspan'>Analysis run by user :</td>\n");
        o.append("  <td colspan=4 style='mso-ignore:colspan'>");
        o.append(ra.analysisUserName);
        o.append("</td>\n");
        o.append("  <td colspan=11 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // AA version
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl26 colspan=3 style='height:12.75pt;mso-ignore:colspan'>Automation analyzer version :</td>\n");
        o.append("  <td colspan=4 style='mso-ignore:colspan'>v");
        o.append(AutomationAnalyzer.versionString);
        o.append("</td>\n");
        o.append("  <td colspan=11 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // Blank row
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        //////////////////////////////////////////////////////////////////////
        // Summary

        o.append(" <tr height=24 style='height:18.0pt'>\n");
        o.append("  <td height=24 class=xl24 colspan=2 style='height:18.0pt;mso-ignore:colspan'>Summary</td>\n");
        o.append("  <td colspan=7 class=xl25 style='mso-ignore:colspan'>&nbsp;</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // Artefact count
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl26 colspan=2 style='height:12.75pt;mso-ignore:colspan'>Artefact count</td>\n");
        o.append("  <td colspan=16 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30 colspan=2 style='mso-ignore:colspan'>Data objects</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.dataObjects.size());
        o.append("</td>\n");
        o.append("  <td colspan=13 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30 colspan=2 style='mso-ignore:colspan'>Query objects</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.queryObjects.size());
        o.append("</td>\n");
        o.append("  <td colspan=13 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30>HTML apps</td>\n");
        o.append("  <td class=xl30>&nbsp;</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.htmlApps.size());
        o.append("</td>\n");
        o.append("  <td colspan=13 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30>Java apps</td>\n");
        o.append("  <td class=xl30>&nbsp;</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.javaApps.size());
        o.append("</td>\n");
        o.append("  <td colspan=13 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30>Other files</td>\n");
        o.append("  <td class=xl30>&nbsp;</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.otherFiles.size());
        o.append("</td>\n");
        o.append("  <td colspan=13 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // Code count
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl26 colspan=2 style='height:12.75pt;mso-ignore:colspan'>Code count</td>\n");
        o.append("  <td colspan=16 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl35>Category</td>\n");
        o.append("  <td class=xl35>&nbsp;</td>\n");
        o.append("  <td class=xl35>Total lines</td>\n");
        o.append("  <td class=xl35>Generated</td>\n");
        o.append("  <td class=xl35>Percent</td>\n");
        o.append("  <td class=xl35>User</td>\n");
        o.append("  <td class=xl35>Percent</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        // Server lines
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl25>Server lines</td>\n");
        o.append("  <td></td>\n");
        int totalServerLines = ra.totalDOlines + ra.totalQOlines;
        int totalGenServerLines = ra.totalDOgeneratedLines + ra.totalQOgeneratedLines;
        int totalUserServerLines = ra.totalDOuserLines + ra.totalQOuserLines;
        o.append("  <td align=right x:num>");
        o.append("" + totalServerLines);
        o.append("</td>\n");
        o.append("  <td align=right x:num>");
        o.append("" + totalGenServerLines);
        o.append("</td>\n");
        o.append("  <td class=xl28 align=right x:num>");
        o.append(formatPercent(totalGenServerLines, totalServerLines));
        o.append("%</td>\n");
        o.append("  <td align=right x:num>");
        o.append("" + totalUserServerLines);
        o.append("</td>\n");
        o.append("  <td class=xl28 align=right x:num>");
        o.append(formatPercent(totalUserServerLines, totalServerLines));
        o.append("%</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        // Client lines
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl25>Client lines</td>\n");
        o.append("  <td></td>\n");
        int totalClientLines = ra.totalHTMLlines + ra.totalJavaLines;
        int totalGenClientLines = ra.totalHTMLgeneratedLines + ra.totalJavaGeneratedLines;
        int totalUserClientLines = ra.totalHTMLuserLines + ra.totalJavaUserLines;
        o.append("  <td align=right x:num>");
        o.append("" + totalClientLines);
        o.append("</td>\n");
        o.append("  <td align=right x:num>");
        o.append("" + totalGenClientLines);
        o.append("</td>\n");
        o.append("  <td class=xl28 align=right x:num>");
        o.append(formatPercent(totalGenClientLines, totalClientLines));
        o.append("%</td>\n");
        o.append("  <td align=right x:num>");
        o.append("" + totalUserClientLines);
        o.append("</td>\n");
        o.append("  <td class=xl28 align=right x:num>");
        o.append(formatPercent(totalUserClientLines, totalClientLines));
        o.append("%</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // Other files
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl25>Other files</td>\n");
        o.append("  <td></td>\n");
        o.append("  <td align=right x:num>");
        o.append("" + ra.totalOtherFilesLines);
        o.append("</td>\n");
        o.append("  <td align=right x:num>0</td>\n");
        o.append("  <td class=xl31 align=right x:num>0%</td>\n");
        o.append("  <td align=right x:num>");
        o.append("" + ra.totalOtherFilesLines);
        o.append("</td>\n");
        o.append("  <td class=xl31 align=right x:num>100%</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        // Total code
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl32>Total lines</td>\n");
        o.append("  <td class=xl32>&nbsp;</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalLines);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalGeneratedLines);
        o.append("</td>\n");
        o.append("  <td class=xl37 align=right x:num>");
        o.append(formatPercent(ra.totalGeneratedLines, ra.totalLines));
        o.append("%</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalUserLines);
        o.append("</td>\n");
        o.append("  <td class=xl37 align=right x:num>");
        o.append(formatPercent(ra.totalUserLines, ra.totalLines));
        o.append("%</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=34 style='height:25.5pt;mso-xlrowspan:2'>\n");
        o.append("  <td height=34 colspan=18 style='height:25.5pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // method count
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl26 colspan=2 style='height:12.75pt;mso-ignore:colspan'>Method count</td>\n");
        o.append("  <td colspan=16 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl35>Category</td>\n");
        o.append("  <td class=xl35>&nbsp;</td>\n");
        o.append("  <td class=xl35>Total Methods</td>\n");
        o.append("  <td class=xl35>Generated</td>\n");
        o.append("  <td class=xl35>Percent</td>\n");
        o.append("  <td class=xl35>User</td>\n");
        o.append("  <td class=xl35>Percent</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        // Server methods
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl25>Server Methods</td>\n");
        o.append("  <td></td>\n");
        int totalServerMethods = ra.totalMethods;
        int totalGenServerMethods = ra.totalGenMethods;
        int totalUserServerMethods = ra.totalUserMethods;
        o.append("  <td align=right x:num>");
        o.append("" + totalServerMethods);
        o.append("</td>\n");
        o.append("  <td align=right x:num>");
        o.append("" + totalGenServerMethods);
        o.append("</td>\n");
        o.append("  <td class=xl28 align=right x:num>");
        o.append(formatPercent(totalGenServerMethods, totalServerMethods));
        o.append("%</td>\n");
        o.append("  <td align=right x:num>");
        o.append("" + totalUserServerMethods);
        o.append("</td>\n");
        o.append("  <td class=xl28 align=right x:num>");
        o.append(formatPercent(totalUserServerMethods, totalServerMethods));
        o.append("%</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // Client Methods
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl25>Client</td>\n");
        o.append("  <td></td>\n");
        //   int totalClientLines = ra.totalHTMLlines + ra.totalJavaLines;
        //   int totalGenClientLines = ra.totalHTMLgeneratedLines + ra.totalJavaGeneratedLines;
        //   int totalUserClientLines = ra.totalHTMLuserLines + ra.totalJavaUserLines;
        o.append("  <td align=right x:num>");
        o.append("" + ra.totalAppMethods);
        o.append("</td>\n");
        o.append("  <td align=right x:num>");
        o.append("" + ra.totalAppGenMethods);
        o.append("</td>\n");
        o.append("  <td class=xl28 align=right x:num>");
        o.append(formatPercent(ra.totalAppGenMethods, ra.totalAppMethods));
        o.append("%</td>\n");
        o.append("  <td align=right x:num>");
        o.append("" + ra.totalAppUserMethods);
        o.append("</td>\n");
        o.append("  <td class=xl28 align=right x:num>");
        o.append(formatPercent(ra.totalAppUserMethods, ra.totalAppMethods));
        o.append("%</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // Other files  methods
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl25>Other files</td>\n");
        o.append("  <td></td>\n");
        o.append("  <td align=right x:num>");
        o.append("" + ra.totalOtherFileMethods);
        o.append("</td>\n");
        o.append("  <td align=right x:num>0</td>\n");
        o.append("  <td class=xl31 align=right x:num>0%</td>\n");
        o.append("  <td align=right x:num>");
        o.append("" + ra.totalOtherFileMethods);
        o.append("</td>\n");
        o.append("  <td class=xl31 align=right x:num>100%</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // Total methods
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl32>Total Methods</td>\n");
        o.append("  <td class=xl32>&nbsp;</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + (ra.totalMethods + ra.totalOtherFileMethods + ra.totalAppMethods));
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + (ra.totalGenMethods + ra.totalAppGenMethods));
        o.append("</td>\n");
        o.append("  <td class=xl37 align=right x:num>");
        o.append(formatPercent((ra.totalGenMethods + ra.totalAppGenMethods), (ra.totalMethods + ra.totalOtherFileMethods + ra.totalAppMethods)));
        o.append("%</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + (ra.totalUserMethods + ra.totalOtherFileMethods + ra.totalAppUserMethods));
        o.append("</td>\n");
        o.append("  <td class=xl37 align=right x:num>");
        o.append(formatPercent((ra.totalUserMethods + ra.totalOtherFileMethods + ra.totalAppUserMethods), (ra.totalMethods + ra.totalOtherFileMethods + ra.totalAppMethods)));
        o.append("%</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=34 style='height:25.5pt;mso-xlrowspan:2'>\n");
        o.append("  <td height=34 colspan=18 style='height:25.5pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        // Form summary
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl26 colspan=2 style='height:12.75pt;mso-ignore:colspan'>Form summary</td>\n");
        o.append("  <td colspan=16 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl35>App type</td>\n");
        o.append("  <td class=xl35>&nbsp;</td>\n");
        o.append("  <td class=xl35>Total forms</td>\n");
        o.append("  <td class=xl35>Rebuildable</td>\n");
        o.append("  <td class=xl35>Percent</td>\n");
        o.append("  <td class=xl35>Customized</td>\n");
        o.append("  <td class=xl35>Percent</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        // HTML pages
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl36>HTML</td>\n");
        o.append("  <td></td>\n");
        o.append("  <td align=right x:num>");
        o.append("" + ra.totalNumHTMLpages);
        o.append("</td>\n");
        o.append("  <td align=right x:num>");
        o.append("" + ra.totalNumRegenHTMLpages);
        o.append("</td>\n");
        o.append("  <td class=xl31 align=right x:num>");
        o.append(formatPercent(ra.totalNumRegenHTMLpages, ra.totalNumHTMLpages));
        o.append("%</td>\n");
        int numNonRegenHtml = ra.totalNumHTMLpages - ra.totalNumRegenHTMLpages;
        o.append("  <td align=right x:num>");
        o.append("" + numNonRegenHtml);
        o.append("</td>\n");
        o.append("  <td class=xl31 align=right x:num>");
        o.append(formatPercent(numNonRegenHtml, ra.totalNumHTMLpages));
        o.append("%</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        // Java forms
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl36>Java</td>\n");
        o.append("  <td></td>\n");
        o.append("  <td align=right x:num>");
        o.append("" + ra.totalNumJavaForms);
        o.append("</td>\n");
        o.append("  <td align=right x:num>");
        o.append("" + ra.totalNumRegenJavaForms);
        o.append("</td>\n");
        o.append("  <td class=xl31 align=right x:num>");
        o.append(formatPercent(ra.totalNumRegenJavaForms, ra.totalNumJavaForms));
        o.append("%</td>\n");
        int numNonRegenJava = ra.totalNumJavaForms - ra.totalNumRegenJavaForms;
        o.append("  <td align=right x:num>");
        o.append("" + numNonRegenJava);
        o.append("</td>\n");
        o.append("  <td class=xl31 align=right x:num>");
        o.append(formatPercent(numNonRegenJava, ra.totalNumJavaForms));
        o.append("%</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        // Total forms
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl32>Total</td>\n");
        o.append("  <td class=xl32>&nbsp;</td>\n");
        int totalForms = ra.totalNumJavaForms + ra.totalNumHTMLpages;
        int totalRegenForms = ra.totalNumRegenHTMLpages + ra.totalNumRegenJavaForms;
        int totalNonRegenForms = totalForms - totalRegenForms;
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + totalForms);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + totalRegenForms);
        o.append("</td>\n");
        o.append("  <td class=xl34 align=right x:num>");
        o.append(formatPercent(totalRegenForms, totalForms));
        o.append("%</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + totalNonRegenForms);
        o.append("</td>\n");
        o.append("  <td class=xl34 align=right x:num>");
        o.append(formatPercent(totalNonRegenForms, totalForms));
        o.append("%</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // Attributes summary
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl26 colspan=2 style='height:12.75pt;mso-ignore:colspan'>Attributes summary</td>\n");
        o.append("  <td colspan=16 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl32 colspan=3 style='mso-ignore:colspan'>Total number of\n");
        o.append("  attributes</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalNumAttribs);
        o.append("</td>\n");
        o.append("  <td colspan=12 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30 colspan=2 style='mso-ignore:colspan'>Persistent attributes</td>\n");
        o.append("  <td class=xl30>&nbsp;</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.totalNumPersistentAttribs);
        o.append("</td>\n");
        o.append("  <td colspan=12 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30 colspan=2 style='mso-ignore:colspan'>Non-persistent attributes</td>\n");
        o.append("  <td class=xl30>&nbsp;</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.totalNumNonPersistentAttribs);
        o.append("</td>\n");
        o.append("  <td colspan=12 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30 colspan=2 style='mso-ignore:colspan'>Required attributes</td>\n");
        o.append("  <td class=xl30>&nbsp;</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.totalNumRequiredAttribs);
        o.append("</td>\n");
        o.append("  <td colspan=12 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30 colspan=3 style='mso-ignore:colspan'>Prevent user update\n");
        o.append("  attributes</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.totalNumPreventUserUpdateAttribs);
        o.append("</td>\n");
        o.append("  <td colspan=12 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30 colspan=2 style='mso-ignore:colspan'>Derived attributes</td>\n");
        o.append("  <td class=xl30>&nbsp;</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.totalNumDerivedAttribs);
        o.append("</td>\n");
        o.append("  <td colspan=12 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30 colspan=2 style='mso-ignore:colspan'>Coded value</td>\n");
        o.append("  <td class=xl30>&nbsp;</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.totalNumCodedValueAttribs);
        o.append("</td>\n");
        o.append("  <td colspan=12 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // Rules summary
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl26 colspan=2 style='height:12.75pt;mso-ignore:colspan'>Rules summary</td>\n");
        o.append("  <td colspan=16 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30 colspan=2 style='mso-ignore:colspan'>Relationships</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.numRelationships);
        o.append("</td>\n");
        o.append("  <td colspan=13 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30>Constraints</td>\n");
        o.append("  <td class=xl30>&nbsp;</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.totalNumConstraints);
        o.append("</td>\n");
        o.append("  <td colspan=13 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30>Actions</td>\n");
        o.append("  <td class=xl30>&nbsp;</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.totalNumActions);
        o.append("</td>\n");
        o.append("  <td colspan=13 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30>Sums</td>\n");
        o.append("  <td class=xl30>&nbsp;</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.totalNumSumAttribs);
        o.append("</td>\n");
        o.append("  <td colspan=13 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30>Counts</td>\n");
        o.append("  <td class=xl30>&nbsp;</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.totalNumCountAttribs);
        o.append("</td>\n");
        o.append("  <td colspan=13 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30 colspan=2 style='mso-ignore:colspan'>Parent replicates</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.totalNumParentReplicateAttribs);
        o.append("</td>\n");
        o.append("  <td colspan=13 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30>Formulas</td>\n");
        o.append("  <td class=xl30>&nbsp;</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.totalNumFormulaAttribs);
        o.append("</td>\n");
        o.append("  <td colspan=13 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30>Defaults</td>\n");
        o.append("  <td class=xl30>&nbsp;</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.totalNumDefaultAttribs);
        o.append("</td>\n");
        o.append("  <td colspan=13 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30>Conditions</td>\n");
        o.append("  <td class=xl30>&nbsp;</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.totalNumValidationAttribs);
        o.append("</td>\n");
        o.append("  <td colspan=13 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl30 colspan=2 style='mso-ignore:colspan'>Coded values</td>\n");
        o.append("  <td class=xl30 align=right x:num>");
        o.append("" + ra.totalNumCodedValueAttribs);
        o.append("</td>\n");
        o.append("  <td colspan=13 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=2 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append("  <td class=xl32>Total rules</td>\n");
        o.append("  <td class=xl33>&nbsp;</td>\n");
        int numRules = ra.numRelationships + ra.totalNumConstraints + ra.totalNumActions +
                ra.totalNumSumAttribs + ra.totalNumCountAttribs + ra.totalNumParentReplicateAttribs +
                ra.totalNumFormulaAttribs + ra.totalNumDefaultAttribs + ra.totalNumValidationAttribs +
                ra.totalNumCodedValueAttribs;
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + numRules);
        o.append("</td>\n");
        o.append("  <td colspan=13 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=51 style='height:38.25pt;mso-xlrowspan:3'>\n");
        o.append("  <td height=51 colspan=18 style='height:38.25pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        ///////////////////////////////////////////////////////////////////////////////////////////////
        // Data objects
        o.append(" <tr height=24 style='height:18.0pt'>\n");
        o.append("  <td height=24 class=xl24 colspan=2 style='height:18.0pt;mso-ignore:colspan'>Data objects</td>\n");
        o.append("  <td colspan=16 class=xl25 style='mso-ignore:colspan'>&nbsp;</td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td colspan=3 height=17 class=xl37 style='height:12.75pt'>Name</td>\n");
        o.append("  <td class=xl37_2>Gen. Code</td>\n");
        o.append("  <td class=xl37_2>User code</td>\n");
        o.append("  <td class=xl37_2>Constraints</td>\n");
        o.append("  <td class=xl37_2>Actions</td>\n");
        o.append("  <td class=xl37_2>Pers. Attribs</td>\n");
        o.append("  <td class=xl37_2>Non pers.</td>\n");
        o.append("  <td class=xl37_2>Required</td>\n");
        o.append("  <td class=xl37_2>No update</td>\n");
        o.append("  <td class=xl37_2>Sum</td>\n");
        o.append("  <td class=xl37_2>Count</td>\n");
        o.append("  <td class=xl37_2>Replicate</td>\n");
        o.append("  <td class=xl37_2>Formula</td>\n");
        o.append("  <td class=xl37_2>Default</td>\n");
        o.append("  <td class=xl37_2>Condition</td>\n");
        o.append("  <td class=xl37_2>Coded val</td>\n");
        o.append(" </tr>\n");

        Iterator doIter = ra.dataObjects.values().iterator();
        while (doIter.hasNext()) {
            DataObjectAnalysis doa = (DataObjectAnalysis) doIter.next();
            o.append(" <tr height=17 style='height:12.75pt'>\n");
            o.append("  <td colspan=3 height=17 style='height:12.75pt'>");
            o.append("<a href=\"./" + doa.simpleName + ".html\" >" + doa.simpleName + "</a>");   // add hyperlink to new drill down feature
            generateHTMLPage(doa.simpleName); // create a speical output page for this dataobject
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + doa.totalGeneratedLines);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + doa.totalUserLines);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + doa.numConstraints);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + doa.numActions);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + doa.numPersistentAttribs);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + doa.numNonPersistentAttribs);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + doa.numRequiredAttribs);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + doa.numPreventUserUpdateAttribs);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + doa.numSumAttribs);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + doa.numCountAttribs);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + doa.numParentReplicateAttribs);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + doa.numFormulaAttribs);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + doa.numDefaultAttribs);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + doa.numValidationAttribs);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + doa.numCodedValueAttribs);
            o.append("</td>\n");
            o.append(" </tr>\n");
        }
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl32 style='height:12.75pt'>Total</td>\n");
        o.append("  <td colspan=2 class=xl32 style='mso-ignore:colspan'>&nbsp;</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalDOgeneratedLines);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalDOuserLines);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalNumConstraints);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalNumActions);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalNumPersistentAttribs);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalNumNonPersistentAttribs);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalNumRequiredAttribs);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalNumPreventUserUpdateAttribs);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalNumSumAttribs);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalNumCountAttribs);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalNumParentReplicateAttribs);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalNumFormulaAttribs);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalNumDefaultAttribs);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalNumValidationAttribs);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalNumCodedValueAttribs);
        o.append("</td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=51 style='height:38.25pt;mso-xlrowspan:3'>\n");
        o.append("  <td height=51 colspan=18 style='height:38.25pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        /////////////////////////////////////////////////////////////////////
        // Query objects

        o.append(" <tr height=24 style='height:18.0pt'>\n");
        o.append("  <td height=24 class=xl24 colspan=2 style='height:18.0pt;mso-ignore:colspan'>Query objects</td>\n");
        o.append("  <td colspan=5 class=xl25 style='mso-ignore:colspan'>&nbsp;</td>\n");
        o.append("  <td colspan=11 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td colspan=3 height=17 class=xl37 style='height:12.75pt'>Name</td>\n");
        o.append("  <td class=xl37_2>Num attribs</td>\n");
        o.append("  <td class=xl37_2>Num objects</td>\n");
        o.append("  <td class=xl37_2>Gen. Code</td>\n");
        o.append("  <td class=xl37_2>User code</td>\n");
        o.append("  <td colspan=11 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        Iterator qoIter = ra.queryObjects.values().iterator();
        while (qoIter.hasNext()) {
            QueryObjectAnalysis qoa = (QueryObjectAnalysis) qoIter.next();
            o.append(" <tr height=17 style='height:12.75pt'>\n");
            o.append("  <td colspan=3 height=17 style='height:12.75pt'>");
            o.append("" + qoa.simpleName);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + qoa.numAttribs);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + qoa.numObjects);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + qoa.totalGeneratedLines);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + qoa.totalUserLines);
            o.append("</td>\n");
            o.append("  <td colspan=11 style='mso-ignore:colspan'></td>\n");
            o.append(" </tr>\n");
        }
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl32 style='height:12.75pt'>Total</td>\n");
        o.append("  <td colspan=2 class=xl32 style='mso-ignore:colspan'>&nbsp;</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalNumQueryAttribs);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>&nbsp;</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalQOgeneratedLines);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append("" + ra.totalQOuserLines);
        o.append("</td>\n");
        o.append("  <td colspan=11 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=51 style='height:38.25pt;mso-xlrowspan:3'>\n");
        o.append("  <td height=51 colspan=18 style='height:38.25pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        //////////////////////////////////////////////////////////////////
        // Relationships

        o.append(" <tr height=24 style='height:18.0pt'>\n");
        o.append("  <td height=24 class=xl24 colspan=2 style='height:18.0pt;mso-ignore:colspan'>Relationships</td>\n");
        o.append("  <td colspan=7 class=xl25 style='mso-ignore:colspan'>&nbsp;</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // Parent/children
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td colspan=3 height=17 class=xl37 style='height:12.75pt'>Parent</td>\n");
        o.append("  <td colspan=3 class=xl37>Children</td>\n");
        o.append("  <td colspan=2 class=xl37>Role name</td>\n");
        o.append("  <td class=xl37>Enforced</td>\n");
        o.append("  <td colspan=11 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        Iterator dorIter = ra.dataObjects.values().iterator();
        while (dorIter.hasNext()) {
            DataObjectAnalysis doa = (DataObjectAnalysis) dorIter.next();
            if (doa.children.size() == 0)
                continue;
            for (int childIter = 0; childIter < doa.children.size(); childIter++) {
                String childEntry[] = (String[]) doa.children.elementAt(childIter);
                o.append(" <tr height=17 style='height:12.75pt'>\n");
                if (childIter == 0) {
                    o.append("  <td colspan=3 height=17 style='height:12.75pt'>");
                    o.append("" + doa.simpleName);
                    o.append("</td>\n");
                } else {
                    o.append("  <td colspan=3 height=17 style='height:12.75pt'>&nbsp;</td>\n");
                }
                o.append("  <td colspan=3>");
                o.append("" + childEntry[0]);
                o.append("</td>\n");
                o.append("  <td colspan=2>");
                o.append("" + childEntry[1]);
                o.append("</td>\n");
                o.append("  <td><span style=\"mso-spacerun: yes\">�</span>");
                o.append("" + childEntry[2]);
                o.append("</td>\n");
                o.append("  <td colspan=11 style='mso-ignore:colspan'></td>\n");
                o.append(" </tr>\n");
            }
            o.append(" <tr height=17 style='height:12.75pt'>\n");
            o.append("  <td colspan=3 height=17 style='height:12.75pt'></td>\n");
            o.append("  <td colspan=3></td>\n");
            o.append("  <td colspan=12 style='mso-ignore:colspan'></td>\n");
            o.append(" </tr>\n");
        }

        // Child/parents
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td colspan=3 height=17 class=xl37 style='height:12.75pt'>Child</td>\n");
        o.append("  <td colspan=3 class=xl37>Parents</td>\n");
        o.append("  <td colspan=2 class=xl37>Role name</td>\n");
        o.append("  <td class=xl37>Enforced</td>\n");
        o.append("  <td colspan=11 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        dorIter = ra.dataObjects.values().iterator();
        while (dorIter.hasNext()) {
            DataObjectAnalysis doa = (DataObjectAnalysis) dorIter.next();
            if (doa.parents.size() == 0)
                continue;
            for (int parIter = 0; parIter < doa.parents.size(); parIter++) {
                String parentEntry[] = (String[]) doa.parents.elementAt(parIter);
                o.append(" <tr height=17 style='height:12.75pt'>\n");
                if (parIter == 0) {
                    o.append("  <td colspan=3 height=17 style='height:12.75pt'>");
                    o.append("" + doa.simpleName);
                    o.append("</td>\n");
                } else {
                    o.append("  <td colspan=3 height=17 style='height:12.75pt'>&nbsp;</td>\n");
                }
                o.append("  <td colspan=3>");
                o.append("" + parentEntry[0]);
                o.append("</td>\n");
                o.append("  <td colspan=2>");
                o.append("" + parentEntry[1]);
                o.append("</td>\n");
                o.append("  <td><span style=\"mso-spacerun: yes\">�</span>");
                o.append("" + parentEntry[2]);
                o.append("</td>\n");
                o.append("  <td colspan=11 style='mso-ignore:colspan'></td>\n");
                o.append(" </tr>\n");
            }
            o.append(" <tr height=17 style='height:12.75pt'>\n");
            o.append("  <td colspan=3 height=17 style='height:12.75pt'></td>\n");
            o.append("  <td colspan=3></td>\n");
            o.append("  <td colspan=12 style='mso-ignore:colspan'></td>\n");
            o.append(" </tr>\n");
        }

        //////////////////////////////////////////////////////////////////
        // HTML apps

        o.append(" <tr height=24 style='height:18.0pt'>\n");
        o.append("  <td height=24 class=xl24 colspan=3 style='height:18.0pt;mso-ignore:colspan'>HTML applications</td>\n");
        o.append("  <td colspan=6 class=xl25 style='mso-ignore:colspan'>&nbsp;</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td colspan=3 height=17 class=xl37 style='height:12.75pt'>App name</td>\n");
        o.append("  <td class=xl37>Pages</td>\n");
        o.append("  <td class=xl37>Page name</td>\n");
        o.append("  <td class=xl37>&nbsp;</td>\n");
        o.append("  <td class=xl37>Gen. Code</td>\n");
        o.append("  <td class=xl37>User code</td>\n");
        o.append("  <td class=xl37>Rebuildable</td>\n");
        o.append("  <td class=xl37>DataObjects</td>\n");
        o.append("  <td class=xl37>&nbsp;</td>\n");
        o.append("  <td class=xl37>Picks</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        Iterator htaIter = ra.htmlApps.values().iterator();
        while (htaIter.hasNext()) {
            HTMLApplicationAnalysis hta = (HTMLApplicationAnalysis) htaIter.next();

            o.append(" <tr height=17 style='height:12.75pt'>\n");
            o.append("  <td height=17 colspan=3 style='height:12.75pt;mso-ignore:colspan'>");
            o.append("" + hta.name);
            o.append("</td>\n");
            //o.append("  <td></td>\n");
            o.append("  <td align=right x:num>");
            o.append("" + hta.numForms);
            o.append("</td>\n");
            o.append("  <td colspan=14 style='mso-ignore:colspan'></td>\n");
            o.append(" </tr>\n");

            Iterator formIter = hta.forms.values().iterator();
            while (formIter.hasNext()) {
                FormAnalysis fa = (FormAnalysis) formIter.next();

                o.append(" <tr height=17 style='height:12.75pt'>\n");
                o.append("  <td height=17 colspan=4 style='height:12.75pt;mso-ignore:colspan'></td>\n");
                o.append("  <td colspan=2>");
                o.append("" + fa.formName);
                o.append("</td>\n");
                o.append("  <td align=right x:num>");
                o.append("" + fa.numGenLines);
                o.append("</td>\n");
                o.append("  <td align=right x:num>");
                o.append("" + fa.numUserLines);
                o.append("</td>\n");

//o.append("  <td></td>\n");
                if (fa.isForm) {
                    o.append("  <td><span style=\"mso-spacerun: yes\">�</span>");
                    o.append("" + fa.isRegen);
                    o.append("</td>\n");
                } else {
                    o.append("  <td><span style=\"mso-spacerun: yes\">�</span></td>\n");
                }
                o.append("  <td align=right x:num>");
                o.append("" + fa.totalNumFormDataObjects);
                o.append("</td>\n");
                o.append("  <td align=right x:num>");
                o.append("" + fa.totalNumFormPickObjects);
                o.append("</td>\n");

                o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
                o.append(" </tr>\n");
            }
            o.append(" <tr height=17 style='height:12.75pt'>\n");
            o.append("  <td height=17 colspan=5 style='height:12.75pt;mso-ignore:colspan'></td>\n");
            o.append("  <td class=xl38>Total</td>\n");
            o.append("  <td class=xl38 align=right x:num>");
            o.append("" + hta.totalGeneratedLines);
            o.append("</td>\n");
            o.append("  <td class=xl38 align=right x:num>");
            o.append("" + hta.totalUserLines);
            o.append("</td>\n");
            String regenPercent = formatPercent((hta.numForms - hta.numNonRegenForms), hta.numForms);
            o.append("  <td class=xl39 align=right x:num>");
            o.append(regenPercent);
            o.append("%</td>\n");
            o.append("  <td class=xl39 align=right x:num>");
            o.append("" + hta.totalNumFormDataObjects);
            o.append("</td>\n");
            o.append("  <td class=xl39 align=right x:num>");
            o.append("" + hta.totalNumFormPickObjects);
            o.append("</td>\n");
            o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
            o.append(" </tr>\n");
            o.append(" <tr height=17 style='height:12.75pt'>\n");
            o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
            o.append(" </tr>\n");
        }

        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl32 colspan=4 style='height:12.75pt;mso-ignore:colspan'>Grand total</td>\n");
        o.append("  <td class=xl32>&nbsp;</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append(ra.totalNumHTMLpages);
        o.append("</td>\n");
        o.append("  <td colspan=2 class=xl32 style='mso-ignore:colspan'>&nbsp;</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append(ra.totalHTMLgeneratedLines);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append(ra.totalHTMLuserLines);
        o.append("</td>\n");
        String regenPercent = formatPercent(ra.totalNumRegenHTMLpages, ra.totalNumHTMLpages);
        o.append("  <td class=xl32 align=right x:num>");
        o.append(regenPercent);
        o.append("%</td>\n");
//o.append("  <td class=xl38 align=right x:num>"); o.append("" + ra.totalNumFormDataObjects); o.append("</td>\n");
//o.append("  <td class=xl38 align=right x:num>"); o.append("" + ra.totalNumFormPickObjects); o.append("</td>\n");

        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=51 style='height:38.25pt;mso-xlrowspan:3'>\n");
        o.append("  <td height=51 colspan=18 style='height:38.25pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        ///////////////////////////////////////////////////////////////////
        // Java apps

        o.append(" <tr height=24 style='height:18.0pt'>\n");
        o.append("  <td height=24 class=xl24 colspan=3 style='height:18.0pt;mso-ignore:colspan'>Java applications</td>\n");
        o.append("  <td colspan=6 class=xl24 style='mso-ignore:colspan'>&nbsp;</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td colspan=3 height=17 class=xl37 style='height:12.75pt'>App name</td>\n");
        o.append("  <td class=xl37>Pages</td>\n");
        o.append("  <td class=xl37>Form name</td>\n");
        o.append("  <td class=xl37>&nbsp;</td>\n");
        o.append("  <td class=xl37>Gen. Code</td>\n");
        o.append("  <td class=xl37>User code</td>\n");
        o.append("  <td class=xl37>Rebuildable</td>\n");
        o.append("  <td class=xl37>DataObjects</td>\n");
        o.append("  <td class=xl37>&nbsp;</td>\n");
        o.append("  <td class=xl37>Picks</td>\n");
        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        Iterator jaaIter = ra.javaApps.values().iterator();
        while (jaaIter.hasNext()) {
            JavaApplicationAnalysis jaa = (JavaApplicationAnalysis) jaaIter.next();
            o.append(" <tr height=17 style='height:12.75pt'>\n");
            o.append("  <td height=17 style='height:12.75pt'>");
            o.append(jaa.name);
            o.append("</td>\n");
            o.append("  <td colspan=2 style='mso-ignore:colspan'></td>\n");
            o.append("  <td align=right x:num>");
            o.append(jaa.numForms);
            o.append("</td>\n");
            o.append("  <td colspan=14 style='mso-ignore:colspan'></td>\n");
            o.append(" </tr>\n");


            Iterator jfIter = jaa.forms.values().iterator();
            while (jfIter.hasNext()) {
                FormAnalysis fa = (FormAnalysis) jfIter.next();
                o.append(" <tr height=17 style='height:12.75pt'>\n");
                o.append("  <td height=17 colspan=4 style='height:12.75pt;mso-ignore:colspan'></td>\n");
                o.append("  <td colspan=2>");
                o.append(fa.formName);
                o.append("</td>\n");
                o.append("  <td align=right x:num>");
                o.append(fa.numGenLines);
                o.append("</td>\n");
                o.append("  <td align=right x:num>");
                o.append(fa.numUserLines);
                o.append("</td>\n");

                o.append("  <td><span style=\"mso-spacerun: yes\">�</span>");
                o.append(fa.isRegen);
                o.append("</td>\n");
                o.append("  <td align=right x:num>");
                o.append(fa.totalNumFormDataObjects);
                o.append("</td>\n");
                o.append("  <td align=right x:num>");
                o.append(fa.totalNumFormPickObjects);
                o.append("</td>\n");

                o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
                o.append(" </tr>\n");
            }

            o.append(" <tr height=17 style='height:12.75pt'>\n");
            o.append("  <td height=17 colspan=5 style='height:12.75pt;mso-ignore:colspan'></td>\n");
            o.append("  <td class=xl38>Total</td>\n");
            o.append("  <td class=xl38 align=right x:num>");
            o.append(jaa.totalGeneratedLines);
            o.append("</td>\n");
            o.append("  <td class=xl38 align=right x:num>");
            o.append(jaa.totalUserLines);
            o.append("</td>\n");
            String regenFormsPct = formatPercent(jaa.numForms - jaa.numNonRegenForms, jaa.numForms);
            o.append("  <td class=xl39 align=right x:num>");
            o.append(regenFormsPct);
            o.append("%</td>\n");
            o.append("  <td align=right x:num>");
            o.append(jaa.totalNumFormDataObjects);
            o.append("</td>\n");
            o.append("  <td align=right x:num>");
            o.append(jaa.totalNumFormPickObjects);
            o.append("</td>\n");

            o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
            o.append(" </tr>\n");
            o.append(" <tr height=17 style='height:12.75pt'>\n");
            o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
            o.append(" </tr>\n");
        }

        // Total for all Java apps
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl32 colspan=2 style='height:12.75pt;mso-ignore:colspan'>Grand total</td>\n");
        o.append("  <td class=xl32>&nbsp;</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append(ra.totalNumJavaForms);
        o.append("</td>\n");
        o.append("  <td colspan=2 class=xl32 style='mso-ignore:colspan'>&nbsp;</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append(ra.totalJavaGeneratedLines);
        o.append("</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append(ra.totalJavaUserLines);
        o.append("</td>\n");
        String pctTotalRegenJava = formatPercent(ra.totalNumRegenJavaForms, ra.totalNumJavaForms);
        o.append("  <td class=xl32 align=right x:num>");
        o.append(pctTotalRegenJava);
        o.append("%</td>\n");
        o.append("  <td align=right x:num>");
        o.append(ra.totalNumFormDataObjects);
        o.append("</td>\n");
        o.append("  <td align=right x:num>");
        o.append(ra.totalNumFormPickObjects);
        o.append("</td>\n");

        o.append("  <td colspan=9 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=51 style='height:38.25pt;mso-xlrowspan:3'>\n");
        o.append("  <td height=51 colspan=18 style='height:38.25pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        ////////////////////////////////////////////////////////////////////
        // Other files
        o.append(" <tr height=24 style='height:18.0pt'>\n");
        o.append("  <td height=24 class=xl24 colspan=2 style='height:18.0pt;mso-ignore:colspan'>Other files</td>\n");
        o.append("  <td colspan=2 class=xl24 style='mso-ignore:colspan'>&nbsp;</td>\n");
        o.append("  <td colspan=14 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl37 colspan=2 style='height:12.75pt;mso-ignore:colspan'>File\n");
        o.append("  name</td>\n");
        o.append("  <td class=xl37>&nbsp;</td>\n");
        o.append("  <td class=xl37>Code lines</td>\n");
        o.append("  <td colspan=14 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        Iterator ofIter = ra.otherFiles.values().iterator();
        while (ofIter.hasNext()) {
            OtherFileAnalysis ofa = (OtherFileAnalysis) ofIter.next();
            o.append(" <tr height=17 style='height:12.75pt'>\n");
            o.append("  <td height=17 style='height:12.75pt' colspan=2>");
            o.append(ofa.simpleName);
            o.append(".java</td>\n");
            o.append("  <td style='mso-ignore:colspan'></td>\n");
            o.append("  <td align=right x:num>");
            o.append(ofa.numCodeLines);
            o.append("</td>\n");
            o.append("  <td colspan=14 style='mso-ignore:colspan'></td>\n");
            o.append(" </tr>\n");
        }

        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl32 style='height:12.75pt'>Total</td>\n");
        o.append("  <td colspan=2 class=xl32 style='mso-ignore:colspan'>&nbsp;</td>\n");
        o.append("  <td class=xl32 align=right x:num>");
        o.append(ra.totalOtherFilesLines);
        o.append("</td>\n");
        o.append("  <td colspan=14 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        //////////////////////////////////////////////////////////////////
        // Finish up

        o.append(" <![if supportMisalignedColumns]>\n");
        o.append(" <tr height=0 style='display:none'>\n");
        o.append("  <td width=64 style='width:48pt'></td>\n");
        o.append("  <td width=64 style='width:48pt'></td>\n");
        o.append("  <td width=74 style='width:56pt'></td>\n");
        o.append("  <td width=79 style='width:59pt'></td>\n");
        o.append("  <td width=96 style='width:72pt'></td>\n");
        o.append("  <td width=93 style='width:70pt'></td>\n");
        o.append("  <td width=79 style='width:59pt'></td>\n");
        o.append("  <td width=84 style='width:63pt'></td>\n");
        o.append("  <td width=87 style='width:65pt'></td>\n");
        o.append("  <td width=64 style='width:48pt'></td>\n");
        o.append("  <td width=78 style='width:59pt'></td>\n");
        o.append("  <td width=64 style='width:48pt'></td>\n");
        o.append("  <td width=64 style='width:48pt'></td>\n");
        o.append("  <td width=64 style='width:48pt'></td>\n");
        o.append("  <td width=64 style='width:48pt'></td>\n");
        o.append("  <td width=64 style='width:48pt'></td>\n");
        o.append("  <td width=74 style='width:56pt'></td>\n");
        o.append("  <td width=78 style='width:59pt'></td>\n");
        o.append(" </tr>\n");
        o.append(" <![endif]>\n");
        o.append("</table>\n");
        o.append("\n");
        o.append("</body>\n");
        o.append("\n");
        o.append("</html>\n");

        // Flush and close all streams
        out.print(o.toString());
        out.flush();
        try {
            out.close();
            outStr.close();
        } catch (Exception ex) {
            errors.add("Error while closing Excel report file : " + ex.toString());
        }
    }

    //////////////////////////////////////////////////////////////////////

    public String getText() {
        String s = o.toString();
        System.out.println("HTML size : " + s.length());
        return o.toString();
    }

    //////////////////////////////////////////////////////////////////////

    private static DecimalFormat decForm = new DecimalFormat("###.##");

    public String formatPercent(int num, int denum) {
        double resNum;
        if (denum == 0)
            resNum = 0;
        else
            resNum = num * 100.0 / denum;
        return decForm.format(resNum);
    }

    public void generateHTMLPage(String dataObjectName) {

        DataObjectReport dor = new DataObjectReport(ra, dataObjectName);
        File selFile = new File(reportPath + dataObjectName + ".html");
        dor.writeReport(selFile);

    }

    public String formatDecimal(int num, int denum) {
        double resNum;
        if (denum == 0)
            resNum = 0;
        else
            resNum = num * 100 / denum;
        return decForm.format(resNum / 100);
    }

}