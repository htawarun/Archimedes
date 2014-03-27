/*
 *    Title:         RepositoryDO
 *    Description:   This is the root java class to hold DataObjects, Query Objects, and Applications
 *    Copyright:     Copyright (c) 2003
 *    Company:       Band Software Design, LLC
 *    @author        Tyler Band
 *    @version       1.0
 *
*/
package com.bandsoftware.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class RepositoryDO extends BusinessObjectDO {
    private String RepositoryName;
    private String RepositoryVersion;
    private String ReposInternalName;
    private String RootDirectory;
    private String ReportPath = null;

    public RepositoryDO(String name) {
        super("Repository");
        setRepositoryName(name);
        setReposInternalName(name);
    }

    public String getRepositoryName() {
        return RepositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        setAttrValue("RepositoryName", repositoryName);
        RepositoryName = repositoryName;
    }

    public String getRepositoryVersion() {
        return RepositoryVersion;
    }

    public void setRepositoryVersion(String repositoryVersion) {
        // do not set this twice
        if (RepositoryVersion == null) {
            setAttrValue("RepositoryVersion", repositoryVersion);
            RepositoryVersion = repositoryVersion;
            setAttrValue("RepositoryName", this.getAttrValue("RepositoryName") + "_" + repositoryVersion);
            recursiveReposVersion();  //set all children to this new name
        }
    }

    public String getReposInternalName() {
        return ReposInternalName;
    }

    public void setReposInternalName(String reposInternalName) {
        setAttrValue("ReposInternalName", reposInternalName);
        ReposInternalName = reposInternalName;
    }

    public String getRootDirectory() {
        return RootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        setAttrValue("RootDirectory", rootDirectory);
        RootDirectory = rootDirectory;
    }

    public void analyzeWhereUsed() {
        Vector v;// = getWhereUsedVector();
        DataObjectDO dataObj;
        QueryObjectDO qo;
        ApplicationDO app;
        for (Enumeration e = this.findChildren("DataObject"); e.hasMoreElements(); ) {
            dataObj = (DataObjectDO) e.nextElement();
            dataObj.createWhereUsed();
        }
        for (Enumeration e = this.findChildren("QueryObject"); e.hasMoreElements(); ) {
            qo = (QueryObjectDO) e.nextElement();
            qo.createWhereUsed();
        }
        for (Enumeration e = this.findChildren("Application"); e.hasMoreElements(); ) {
            app = (ApplicationDO) e.nextElement();
            app.createWhereUsed();
        }
    }

    public void printFiles(String path) {
        BSDDataObject obj;
        DataObjectDO dataObj;
        setReportPath(path);
        Enumeration e = getChildren();
        StringBuffer sb;
        StringBuffer sbAll;
        // StringBuffer sbXML;
        int i = 1; // used to generate script ID
        StringBuffer sbTOC = new StringBuffer();
        sbTOC.append("NOTE Check ALL Generated structures");
        newLine(sbTOC);
        String tocNM = RepositoryName + "_TOC.txt";
        while (e.hasMoreElements()) {
            obj = (BSDDataObject) e.nextElement();
            if ("DataObject".equals(obj.getName())) {
                String nm = ((DataObjectDO) obj).getDataObjectName();
                nm = nm + "_GEN";
                String nmAll = nm + "_All";
                db("DataObject write " + nm);
                sb = new StringBuffer();
                sbAll = new StringBuffer();
                // sbXML = new StringBuffer();
                ((DataObjectDO) obj).write(sb);
                //((DataObjectDO)obj).writeXML(sbXML, i);
                ((DataObjectDO) obj).writeALL(sbAll);
                sbTOC.append("     //EXECUTE " + nm + "_ALL.xml");
                newLine(sbTOC);
                createFile(sb, nm + ".asn");
                // createFile(sbXML, nm +".xml");
                createFile(sbAll, nmAll + ".asn");
                i++;
            }
            createFile(sbTOC, tocNM + ".txt");

        }
        specialReport();
    }

    // do a quick pass at the meta-data to find some missing things
    public void specialReport() {

        BSDDataObject obj;
        DataObjectDO dataObj;
        Enumeration e = getChildren();
        StringBuffer sb = new StringBuffer();
        while (e.hasMoreElements()) {
            obj = (BSDDataObject) e.nextElement();
            if ("DataObject".equals(obj.getName())) {
                String nm = ((DataObjectDO) obj).getDataObjectName();
                ((DataObjectDO) obj).writeSpecial(sb);
                newLine(sb);

            }
            createFile(sb, RepositoryName + "_SpcialReport.txt");
        }
    }

    public void createFile(StringBuffer sb, String fileName) {
        // create a
        String dir = getDirectory();
        File file = new File(dir + fileName);
        writeReport(file, sb);

        //create a file output stream and write this sb.
    }

    private String getDirectory() {

        Properties aaProp = getProps();
        db("Directory " + aaProp.get("Archimedes_Script_Directory"));
        db("ReportPath =" + ReportPath);
        // Set VLS connection properties
        String dir = (String) aaProp.get("Archimedes_Script_Directory");
        if (ReportPath != null)
            return ReportPath;
        return dir != null ? dir : "c:/temp/";

    }

    private void setReportPath(String aPath) {
        int index = aPath.lastIndexOf("\\");
        ReportPath = aPath.substring(0, index) + "/";
    }

    public void writeReport(File outFile, StringBuffer sb) {
        FileOutputStream outStr = null;
        PrintWriter out = null;
        try {
            outStr = new FileOutputStream(outFile);
            out = new PrintWriter(outStr);
            out.print(sb.toString());
            out.flush();

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error while creating DataObject Assertion : " + ex.toString());
        } finally {
            try {
                if (out != null)
                    out.close();
                if (outStr != null)
                    outStr.close();
            } catch (Exception ex) {
            } //yes TCB
        }
    }
}
