// This software is the confidential and proprietary information
// of Versata, Inc. ("Confidential Information").  You
// shall not disclose such Confidential Information and shall use
// it only in accordance with the terms of the license agreement
// you entered into with Band Software Design, LLC.
//
// THE SOFTWARE IS PROVIDED AS IS, WITHOUT ANY EXPRESS OR IMPLIED
// WARRANTY BY VERSATA, INC. OR ITS SUPPLIERS, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS FOR A PARTICULAR PURPOSE. NEITHER VERSATA, INC. NOR ITS
// SUPPLIERS PROMISE THAT THE SOFTWARE WILL BE ERROR FREE OR WILL
// OPERATE WITHOUT INTERRUPTION. IN NO EVENT SHALL VERSATA, INC. BE
// LIABLE FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL OR CONSEQUENTIAL
// DAMAGES OF ANY KIND INCLUDING, WITHOUT LIMITATION, LOST PROFITS.
// Copyright (c) 2003, Band Software Design, LLC
//

package com.versata.automationanalyzer.reports;

import com.bandsoftware.data.*;
import com.versata.automationanalyzer.DataObjectAnalysis;
import com.versata.automationanalyzer.RepositoryAnalysis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Enumeration;
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

public class DataObjectReport {
    public Vector errors = new Vector();
    private RepositoryAnalysis ra;
    private RepositoryDO repos;
    private DataObjectDO dataObjectDO;
    private StringBuffer o = new StringBuffer();
    private DataObjectAnalysis doa;
    public String reportPath = "./";
    ///////////////////////////////////////////////////////////////////////

    public DataObjectReport(RepositoryAnalysis ra, String dataObjectName) {
        this.ra = ra;
        if (this.ra == null)
            throw new IllegalArgumentException("DataObjectReport cannot be built without a RepositoryAnalysis");
        this.repos = ra.repos;
        if (this.repos == null)
            throw new IllegalArgumentException("DataObjectReport cannot be built without a RepositoryDO object");
        this.dataObjectDO = repos.findDataObject(dataObjectName);
        if (this.dataObjectDO == null)
            throw new IllegalArgumentException("DataObjectReport cannot be built. Invalid DataObject Named: " + dataObjectName);
        doa = findDataObjectAnalysis(dataObjectName);
        //System.out.println("DataObject Report for :"+ dataObjectName);
    }

    ///////////////////////////////////////////////////////////////////////

    public void writeReport(File outFile) {
        FileOutputStream outStr;
        PrintWriter out;
        try {
            outStr = new FileOutputStream(outFile);
            out = new PrintWriter(outStr);
        } catch (Exception ex) {
            errors.add("Error while creating DataObject reports : " + ex.toString());
            return;
        }

        o = new StringBuffer();
        //System.out.println("DataObject Report starting...");
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
        o.append("  <o:Author>Tyler Band</o:Author>\n");
        o.append("  <o:LastAuthor>Tyler Band</o:LastAuthor>\n");
        o.append("  <o:LastPrinted>2001-12-14T01:41:51Z</o:LastPrinted>\n");
        o.append("  <o:Created>2001-12-13T22:01:08Z</o:Created>\n");
        o.append("  <o:LastSaved>2003-03-14T01:43:07Z</o:LastSaved>\n");
        o.append("  <o:Company>Versata, Inc. & Band Software Design, LLC</o:Company>\n");
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
        o.append("	font-family:Arial;sans-serif;\n");
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
        o.append("	color:white;\n");
        o.append("	font-family:Arial, sans-serif;\n");
        o.append("	mso-font-charset:0;\n");
        o.append("	background:white;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl25\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	background:blue;\n");
        o.append("	color:white;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl26\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("   color:white;\n");
        o.append("	font-weight:700;\n");
        o.append("	font-family:Arial, sans-serif;\n");
        o.append("	mso-font-charset:0;\n");
        o.append("	font-size:10.0pt;\n");
        o.append("	background:blue;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl27\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("   color:white;\n");
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
        o.append("   color:white;\n");
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
        o.append("   color:white;\n");
        o.append("	background:blue;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl33\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	background:blue;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl34\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	font-weight:700;\n");
        o.append("	font-family:Arial, sans-serif;\n");
        o.append("	mso-font-charset:0;\n");
        o.append("	mso-number-format:0%;\n");
        o.append("   color:white;\n");
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
        o.append("   color:white;\n");
        o.append("	mso-number-format:Percent;\n");
        o.append("	background:blue;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl37_2\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	font-weight:700;\n");
        o.append("	font-family:Arial, sans-serif;\n");
        o.append("	mso-font-charset:0;\n");
        o.append("   color:white;\n");
        o.append("	mso-number-format:Percent;\n");
        o.append("	text-align:right;\n");
        o.append("	background:blue;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl38\n");
        o.append("	{mso-style-parent:style0;\n");
        o.append("	font-weight:700;\n");
        o.append("   color:white;\n");
        o.append("	font-family:Arial, sans-serif;\n");
        o.append("	mso-font-charset:0;\n");
        o.append("	background:blue;\n");
        o.append("	mso-pattern:auto none;}\n");
        o.append(".xl39\n");
        o.append("	{mso-style-parent:style0;\n");

        o.append("	mso-number-format:0%;\n");
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
        o.append("  mso-ignore:colspan;width:283pt'>" + dataObjectDO.getDataObjectName() + "</td>\n");
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

        // Data Object Name
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl26 colspan=3 style='height:12.75pt;mso-ignore:colspan'>DataObject Name :</td>\n");
        o.append("  <td colspan=4 style='mso-ignore:colspan'>");
        o.append(dataObjectDO.getDataObjectName());
        o.append("</td>\n");
        o.append("  <td colspan=11 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // Data Object Description
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 class=xl26 colspan=3 style='height:12.75pt;mso-ignore:colspan'>Description :</td>\n");
        o.append("  <td colspan=11 style='mso-ignore:colspan'>");
        o.append(dataObjectDO.getDescription());
        o.append("</td>\n");
        o.append("  <td colspan=11 style='mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");

        // Attribute Title

        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=3 style='height:14.75pt;mso-ignore:colspan'><font size='4'>Attributes</td>\n");
        //o.append("<tr height=18 style='height:13.2pt'>\n");
        //o.append("<td height=18 colspan=3 style='height:13.2pt;mso-ignore:colspan'></td>\n");
        o.append("<td class=xl25>Caption</td>  \n");
        o.append("<td class=xl25></td>    \n");
        o.append("<td class=xl25>Nullability</td> \n");
        o.append("<td class=xl25>Persistent</td>  \n");
        o.append("<td class=xl25>DataType</td>   \n");
        o.append("<td class=xl25 colspan=10>Business Rule</td>   \n");
        o.append(" </tr>\n");

        // Do attibues
        AttributeDO attr;
        Enumeration ae = this.dataObjectDO.findChildren("Attribute");
        while (ae.hasMoreElements()) {
            attr = (AttributeDO) ae.nextElement();

            o.append(" <tr height=17 style='height:12.75pt'>\n");
            o.append("  <td height=17 class=xl26 colspan=3 style='height:12.75pt;mso-ignore:colspan'>" + attr.getAttrName() + "</td>\n");
            o.append("  <td colspan=2 style='mso-ignore:colspan'>");
            if (attr.getCaption() != null)
                o.append(attr.getCaption());
            else
                o.append(attr.getAttrName());
            // is required
            // datatype

            // rule (sum(Object, co.Attr, where) count(object,where), default(value), replicate(Object,attr,maintained) )
            o.append("</td>\n");
            o.append("  <td align=center valign='middle'  x:num>");
            o.append("" + (attr.isValueRequired() ? "Required" : "Optional"));
            o.append("</td>\n");
            o.append("  <td  align=left valign='middle' x:num>");
            o.append("" + (attr.isPersistent() ? "Persistent" : "Derived"));
            o.append("</td>\n");
            o.append("  <td  align=center valign='middle' x:num>");
            o.append("" + attr.getFullDataType());
            o.append("</td>\n");
            o.append("  <td colspan=10 style='mso-ignore:colspan'>");
            o.append(" ");
            o.append(attr.formatRule());

            o.append("</td>\n");
            o.append(" </tr>\n");

        }


        // Do Constraints
        // Blank row
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=3 style='height:14.75pt;mso-ignore:colspan'><font size='4'>Constraints</td>\n");
        o.append("<td class=xl25>When</td>  \n");
        o.append("<td class=xl25  colspan=6>Constraint</td> \n");
        o.append("<td colspan=5 class=xl25 >Error Message</td>\n");
        o.append(" </tr>\n");
        ConstraintDO con;
        Enumeration ce = this.dataObjectDO.findChildren("Constraint");
        while (ce.hasMoreElements()) {
            con = (ConstraintDO) ce.nextElement();

            o.append(" <tr height=17 style='height:12.75pt'>\n");
            o.append("  <td height=17 class=xl26 colspan=3 style='height:12.75pt;mso-ignore:colspan'>" + con.getConstraintName() + "</td>\n");
            o.append("  <td colspan=1 style='mso-ignore:colspan'>");
            o.append("" + con.getConditionType());
            o.append("</td>\n");
            o.append("  <td colspan=6 style='mso-ignore:colspan'>");
            o.append("" + con.get_Condiiton());
            o.append("</td>\n");
            o.append("  <td colspan=6 style='mso-ignore:colspan'>");
            o.append("" + con.getErrorMessage());
            o.append("</td>\n");

            o.append(" </tr>\n");

        }

        // Do Actions
        // Blank row
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=3 style='height:14.75pt;mso-ignore:colspan'><font size='4'>Event Condition Action</td>\n");
        o.append("<td class=xl25 colspan=6>Condition</td>  \n");
        o.append("<td class=xl25 colspan=10>Action</td>   \n");
        o.append(" </tr>\n");
        ActionDO act;
        Enumeration eca = this.dataObjectDO.findChildren("Action");
        while (eca.hasMoreElements()) {
            act = (ActionDO) eca.nextElement();
            if (act.getActionToPerform() != null) {
                o.append(" <tr height=17 style='height:12.75pt'>\n");
                o.append("  <td height=17 class=xl26 colspan=3 style='height:12.75pt;mso-ignore:colspan'>" + act.getActionName() + "</td>\n");
                o.append("  <td colspan=6 style='mso-ignore:colspan'>");
                o.append("" + act.getCondition());
                o.append("</td>\n");
                o.append("  <td colspan=10 style='mso-ignore:colspan'>");
                o.append("" + act.getActionToPerform());
                o.append("</td>\n");

                o.append(" </tr>\n");
            }
        }
        // Do parent/child relationships
        // Blank row
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=3 style='height:14.75pt;mso-ignore:colspan'><font size='4'>Parent Relationships</td>\n");
        o.append(" <td class=xl25 colspan=2>Parent</td> \n");
        o.append(" <td class=xl25>Child</td> \n");
        o.append("<td class=xl26></td> \n");
        o.append("<td class=xl25>Enforced</td> \n");
        o.append(" </tr>\n");
        RelationshipDO rel;
        Enumeration re = this.dataObjectDO.getParentReln().elements();
        while (re.hasMoreElements()) {
            rel = (RelationshipDO) re.nextElement();

            o.append(" <tr height=17 style='height:12.75pt'>\n");
            o.append("  <td height=17 class=xl26 colspan=3 style='height:12.75pt;mso-ignore:colspan'>" + rel.getRelationshipName() + "</td>\n");
            o.append("  <td colspan=2 style='mso-ignore:colspan'>");
            o.append("<a href=\"./" + rel.getParentDOName() + ".html\" >" + rel.getParentDOName() + "</a>");   // add hyperlink to new drill down feature

            o.append("</td>\n");
            o.append("  <td colspan=2 style='mso-ignore:colspan'>");
            o.append("<a href=\"./" + rel.getChildDOName() + ".html\" >" + rel.getChildDOName() + "</a>");   // add hyperlink to new drill down feature
            o.append("</td>\n");
            o.append("  <td colspan=2 style='mso-ignore:colspan'>");
            o.append(rel.isEnforce() ? "Enforced" : "Not Enforced");
            o.append("</td>\n");
            o.append(" </tr>\n");

        }

        // Do parent/child relationships
        // Blank row
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=3 style='height:14.75pt;mso-ignore:colspan'><font size='4'>Child Relationships</td>\n");
        o.append(" <td class=xl25 colspan=2>Child</td> \n");
        o.append(" <td class=xl25>Parent</td> \n");
        o.append("<td class=xl26></td> \n");
        o.append("<td class=xl25>Enforced</td> \n");
        o.append(" </tr>\n");
        re = this.dataObjectDO.getChildReln().elements();
        while (re.hasMoreElements()) {
            rel = (RelationshipDO) re.nextElement();

            o.append(" <tr height=17 style='height:12.75pt'>\n");
            o.append("  <td height=17 class=xl26 colspan=3 style='height:12.75pt;mso-ignore:colspan'>" + rel.getRelationshipName() + "</td>\n");
            o.append("  <td colspan=2 style='mso-ignore:colspan'>");
            o.append("<a href=\"./" + rel.getChildDOName() + ".html\" >" + rel.getChildDOName() + "</a>");   // add hyperlink to new drill down feature

            o.append("</td>\n");
            o.append("  <td colspan=2 style='mso-ignore:colspan'>");
            o.append("<a href=\"./" + rel.getParentDOName() + ".html\" >" + rel.getParentDOName() + "</a>");   // add hyperlink to new drill down feature
            o.append("</td>\n");
            o.append("  <td colspan=2 style='mso-ignore:colspan'>");
            o.append(rel.isEnforce() ? "Enforced" : "Not Enforced");
            o.append("</td>\n");
            o.append(" </tr>\n");

        }
        // Do Indexes
        // Blank row
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=10 style='height:14.75pt;mso-ignore:colspan'><font size='4'>Indexes</td>\n");
        o.append(" </tr>\n");
        Enumeration ie = this.dataObjectDO.findChildren("IndexDO");
        DO_Index idx;
        DO_IndexAttr idxAttr;
        while (ie.hasMoreElements()) {
            idx = (DO_Index) ie.nextElement();
            o.append(" <tr height=17 style='height:12.75pt'>\n");
            o.append("  <td height=17 class=xl26 colspan=3 style='height:12.75pt;mso-ignore:colspan'>" + idx.getIndexName() + "</td>\n");
            Enumeration ea = idx.findChildren("IndexAttr");
            o.append("  <td colspan=6 style='mso-ignore:colspan'>");
            while (ea.hasMoreElements()) {
                idxAttr = (DO_IndexAttr) ea.nextElement();
                if (idxAttr != null) {
                    o.append(idxAttr.getIndexAttrName());
                    o.append(" || ");
                }
            }
            o.append("</td>\n");
            if (idx.isPrimary()) {
                o.append("  <td colspan=2 style='mso-ignore:colspan'>");
                o.append((idx.isPrimary() == true) ? "Pimary Key" : "");
                o.append("</td>\n");
            }
            if ((!idx.isPrimary()) && idx.isUnique()) {
                o.append("  <td colspan=2 style='mso-ignore:colspan'>");
                o.append((idx.isPrimary() == true) ? "Unique " : "");
                o.append("</td>\n");
            }
            o.append(" </tr>\n");
        }

        // Do Methods
        // Blank row
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=3 style='height:14.75pt;mso-ignore:colspan'><font size='4'>User Defined Methods</td>\n");
        o.append("  <td class=xl25 colspan=6 style='mso-ignore:colspan'>Method Signature</td>\n");
        o.append(" </tr>\n");
        Enumeration me = this.dataObjectDO.findChildren("Method");
        MethodDO meth;
        while (me.hasMoreElements()) {
            meth = (MethodDO) me.nextElement();
            if (meth.getUserDefined()) {
                o.append(" <tr height=17 style='height:12.75pt'>\n");
                o.append("  <td height=17 class=xl26 colspan=3 style='height:12.75pt;mso-ignore:colspan'>" + meth.getMethodName() + "</td>\n");
                o.append("  <td colspan=10 style='mso-ignore:colspan'>");
                o.append("" + meth.getSignature());
                o.append("</td>\n");
                o.append(" </tr>\n");
            }
        }
        // Do Where Used and Used By
        // Blank row
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=18 style='height:12.75pt;mso-ignore:colspan'></td>\n");
        o.append(" </tr>\n");
        o.append(" <tr height=17 style='height:12.75pt'>\n");
        o.append("  <td height=17 colspan=3 style='height:14.75pt;mso-ignore:colspan'><font size='4'>Used By</td>\n");
        o.append("  <td class=xl25 colspan=6 style='mso-ignore:colspan'>Used In </td>\n");
        o.append(" </tr>\n");
        if (this.dataObjectDO.usedBy != null) {
            Enumeration we = this.dataObjectDO.usedBy.elements();
            WhereUsedDO wu;
            while (we.hasMoreElements()) {
                wu = (WhereUsedDO) we.nextElement();
                //if (wu.getUserDefined()) {
                o.append(" <tr height=17 style='height:12.75pt'>\n");
                o.append("  <td height=17 class=xl26 colspan=3 style='height:12.75pt;mso-ignore:colspan'>" + wu.getUsedInObjectName() + "</td>\n");
                o.append("  <td colspan=10 style='mso-ignore:colspan'>");
                o.append("" + wu.getUsedInDescription());
                o.append("</td>\n");
                o.append(" </tr>\n");
                //}
            }
        }
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

        //---- moved this here for local testing
        // Flush and close all streams
        out.print(o.toString());
        out.flush();
        try {
            out.close();
            outStr.close();
            return;
        } catch (Exception ex) {
            errors.add("Error while closing DataObject report file : " + ex.toString());
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

    public String formatDecimal(int num, int denum) {
        double resNum;
        if (denum == 0)
            resNum = 0;
        else
            resNum = num * 100 / denum;
        return decForm.format(resNum / 100);
    }

    private DataObjectAnalysis findDataObjectAnalysis(String simpleName) {
        DataObjectAnalysis dataObj = null;
        Iterator doIter = ra.dataObjects.values().iterator();
        while (doIter.hasNext()) {
            dataObj = (DataObjectAnalysis) doIter.next();
            if (dataObj.simpleName.equalsIgnoreCase(simpleName))
                return dataObj;
        }
        return null;
    }
}