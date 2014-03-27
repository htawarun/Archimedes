package com.versata.automationanalyzer.reports;

import com.versata.automationanalyzer.RepositoryAnalysis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

public class XMLReport {
    private RepositoryAnalysis ra;

    public XMLReport(RepositoryAnalysis repAnalysis) {
        this.ra = repAnalysis;
    }

    ////////////////////////////////////////////////////////////////////////

    public void saveReport(File outFile) {
        System.out.println("Saving XML report as : " + outFile.getAbsolutePath());

        FileOutputStream outStr = null;
        PrintWriter out = null;
        try {
            outStr = new FileOutputStream(outFile);
            out = new PrintWriter(outStr);
        } catch (Exception ex) {
            System.err.println("ERROR while opening file for XML report : " + outFile.getAbsolutePath());
            ex.printStackTrace();
            return;
        }

        out.println("<?xml version=\"1.0\"?>");

        out.println("<repository");
        out.println("  name=\"" + ra.name + "\"");
        out.println("  location=\"" + ra.rootDirectory + "\"");
        out.println("  analysisDate=\"" + (new Date()).toString() + "\"");
        out.println("  numberOfQueryObjects=\"" + ra.queryObjects.size() + "\"");
        out.println("  numberOfHTMLApplications=\"" + ra.htmlApps.size() + "\"");
        out.println("  numberOfJavaApplications=\"" + ra.javaApps.size() + "\"");
        out.println("  numberOfOtherFiles=\"" + ra.otherFiles.size() + "\"");
        out.println("  numberTotalMethods=\"" + ra.totalMethods + "\"");
        out.println("  numberUserMethods=\"" + ra.totalUserMethods + "\"");
        out.println("  numberOfGeneratedMethods=\"" + ra.totalGenMethods + "\"");

        out.println("  >");
        out.println();

        out.println("  <repository_statistics");
        out.println("    totalLinesOfCode=\"" + ra.totalLines + "\"");
        out.println("    totalLinesOfGeneratedCode=\"" + ra.totalGeneratedLines + "\"");
        out.println("    percentGeneratedCode=\"" + formatPercent(ra.totalGeneratedLines, ra.totalLines) + "\"");
        out.println("    totalLinesOfUserCode=\"" + ra.totalUserLines + "\"");
        out.println("    percentUserCode=\"" + formatPercent(ra.totalUserLines, ra.totalLines) + "\"");
        out.println("    percentUserMethods=\"" + formatPercent(ra.totalUserMethods, ra.totalMethods) + "\"");
        out.println("    />");
        out.println();

        out.println("  <data_objects>");
        out.println("    <data_objects_statistics");
        out.println("      numberOfDataObjects=\"" + ra.dataObjects.size() + "\"");
        out.println("  </data_objects>");

        out.println("</repository>");

        try {
            out.close();
            outStr.close();
        } catch (Exception ex) {
            System.err.println("ERROR while closing file for XML report : " + outFile.getAbsolutePath());
            ex.printStackTrace();
        }
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
}