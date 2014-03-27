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
import com.bandsoftware.persistence.PersistenceManager;
import com.versata.automationanalyzer.reports.ExcelReport;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Vector;

/**
 * Title:
 * Description:
 * Copyright:    Copyright Versata, Inc. 2001
 * Company: Versata
 *
 * @author Max Tardiveau, Versata
 * @version $Id: MainWindow.java,v 1.2 2003/04/19 16:10:22 Tyler Exp $
 */

public class MainWindow extends JFrame {
    private DefaultMutableTreeNode topResultNode;
    private DefaultMutableTreeNode doResultNode;
    private DefaultMutableTreeNode queryResultNode;
    private DefaultMutableTreeNode htmlResultNode;
    private DefaultMutableTreeNode javaResultNode;
    private DefaultMutableTreeNode otherResultNode;

    private RepositoryAnalysis repAnalysis = null;

    JPanel contentPane;
    JMenuBar jMenuBar1 = new JMenuBar();
    JMenu jMenuFile = new JMenu();
    JMenuItem MenuOpenRepository = new JMenuItem();
    JMenuItem MenuFileSaveReport = new JMenuItem();
    JMenuItem MenuFileEuclidReport = new JMenuItem();
    JMenuItem MenuFileSaveVLS = new JMenuItem();
    JMenuItem jMenuFileExit = new JMenuItem();
    JMenu jMenuHelp = new JMenu();
    JMenuItem MenuItemHelpHelp = new JMenuItem();
    JMenuItem jMenuHelpAbout = new JMenuItem();
    JTabbedPane ResultsTabbedPane = new JTabbedPane();
    JScrollPane ResultsScrollPane = new JScrollPane();
    JTree ResultsTree = new JTree();
    JScrollPane ErrorsScrollPane = new JScrollPane();
    JList ErrorsList = new JList();
    DefaultListModel errorsModel = null;
    private String repDirName = null;
    private String repFileName = null;

    private HelpSet helpSet = null;
    private HelpBroker helpBroker = null;

    ///////////////////////////////////////////////////////////////////////

    /**
     * Construct the frame
     */
    public MainWindow(String repDirName, String repFileName) {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            URL hsURL = HelpSet.findHelpSet(null, "help/AutoAnalyzer");
            helpSet = new HelpSet(null, hsURL);
            helpBroker = helpSet.createHelpBroker();

            helpBroker.enableHelp(ErrorsList, "errors", helpSet);
            helpBroker.enableHelpKey(ErrorsList, "errors", helpSet);
            CSH.setHelpIDString(ErrorsList, "errors");

            MenuItemHelpHelp.addActionListener(new CSH.DisplayHelpFromSource(helpBroker));
            helpBroker.enableHelpKey(this, "top", helpSet);
        } catch (Exception ee) {
            System.out.println("HelpSet not found : " + ee.toString());
            ee.printStackTrace();
        }

        // Check arguments
        this.repDirName = repDirName;
        this.repFileName = repFileName;

        // If repository name ends with slash, get rid of it
        if (this.repDirName != null &&
                (this.repDirName.endsWith("/") || this.repDirName.endsWith("\\"))) {
            this.repDirName = this.repDirName.substring(0, this.repDirName.length() - 1);
        }

        // If only one arg specified
        if ((this.repDirName != null && this.repDirName.length() > 0) &&
                (this.repFileName == null || this.repFileName.length() == 0)) {
            File f = new File(repDirName);
            if (!f.exists()) {
                System.err.println("Could not open repository file : " + this.repDirName);
                errorsModel.addElement("Could not open repository file : " + this.repDirName);
                ResultsTabbedPane.setSelectedIndex(1);
                repDirName = null;
            } else {
                this.repDirName = f.getParentFile().getParent();
                this.repFileName = f.getName();
                System.err.println("Opening repository file : " + this.repDirName + ", file " + this.repFileName);
            }
        }

        if (this.repDirName != null && this.repFileName != null) {
            String fullPath = this.repDirName + "\\Source\\" + this.repFileName;
            File repFile = new File(fullPath);
            if (!repFile.exists()) {
                Vector v = new Vector();
                v.add("No such file : " + fullPath);
                ErrorsList.setListData(v);
                ResultsTabbedPane.setSelectedIndex(1);
                return;
            }
            displayRepository(repFile);
        }
    }

    ////////////////////////////////////////////////////////////////////////

    /**
     * Component initialization
     */
    private void jbInit() throws Exception {
        //setIconImage(Toolkit.getDefaultToolkit().createImage(MainWindow.class.getResource("[Your Icon]")));
        contentPane = (JPanel) this.getContentPane();
        this.setSize(new Dimension(594, 501));
        this.setTitle("Archimedes Metadata Rules Analyzer");
        jMenuFile.setText("File");
        jMenuFileExit.setText("Exit");
        jMenuFileExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuFileExit_actionPerformed(e);
            }
        });
        jMenuHelp.setText("Help");
        jMenuHelpAbout.setText("About");
        jMenuHelpAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuHelpAbout_actionPerformed(e);
            }
        });
        MenuOpenRepository.setActionCommand("openRepository");
        MenuOpenRepository.setMnemonic('0');
        MenuOpenRepository.setText("Open repository...");
        MenuOpenRepository.setAccelerator(javax.swing.KeyStroke.getKeyStroke(79, java.awt.event.KeyEvent.CTRL_MASK, false));
        MenuOpenRepository.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MenuOpenRepository_actionPerformed(e);
            }
        });
        ResultsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        ResultsScrollPane.setPreferredSize(new Dimension(300, 400));
        ErrorsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        ResultsTabbedPane.setPreferredSize(new Dimension(0, 200));
        MenuItemHelpHelp.setText("Help");
        ResultsTree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                ResultsTree_keyPressed(e);
            }
        });
        MenuFileSaveReport.setText("Save HTML Analysis Report...");
        MenuFileSaveReport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(83, java.awt.event.KeyEvent.CTRL_MASK, false));
        MenuFileSaveReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MenuFileSaveReport_actionPerformed(e);
            }
        });

        JMenuItem MenuFileEuclidReport = new JMenuItem();
        JMenuItem MenuFileSaveVLS = new JMenuItem();


        MenuFileSaveVLS.setText("Save Meta Data to VLS...");
        MenuFileSaveVLS.setAccelerator(javax.swing.KeyStroke.getKeyStroke(86, java.awt.event.KeyEvent.CTRL_MASK, false));
        MenuFileSaveVLS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MenuFileSaveVLS_actionPerformed(e);
            }
        });

        MenuFileEuclidReport.setText("Generate Euclid Scripts...");
        MenuFileEuclidReport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(84, java.awt.event.KeyEvent.CTRL_MASK, false));
        MenuFileEuclidReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MenuFileEuclidReport_actionPerformed(e);
            }
        });

        NotesPane.setContentType("text/html");
        NotesPane.setEditable(false);
        NotesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jMenuFile.add(MenuOpenRepository);
        jMenuFile.add(MenuFileSaveReport);
        jMenuFile.add(MenuFileEuclidReport); // REMOVE THIS TO ACTIVATE EUCLID Report Output tmb 3-19-03
        jMenuFile.add(MenuFileSaveVLS);
        jMenuFile.add(jMenuFileExit);
        jMenuHelp.add(MenuItemHelpHelp);
        jMenuHelp.add(jMenuHelpAbout);
        jMenuBar1.add(jMenuFile);
        jMenuBar1.add(jMenuHelp);
        this.setJMenuBar(jMenuBar1);
        contentPane.add(ResultsTabbedPane, BorderLayout.CENTER);
        ResultsTabbedPane.add(ResultsScrollPane, "Results");
        ResultsTabbedPane.add(ErrorsScrollPane, "Errors");
        ResultsTabbedPane.add(NotesScrollPane, "Notes");
        NotesScrollPane.getViewport().add(NotesPane, null);
        ErrorsScrollPane.getViewport().add(ErrorsList, null);
        ResultsScrollPane.getViewport().add(ResultsTree, null);

        errorsModel = new DefaultListModel();
        ErrorsList.setModel(errorsModel);

        topResultNode = new AnalysisNode("", "repository_analysis_top");
        DefaultTreeModel model = new DefaultTreeModel(topResultNode);
        ResultsTree.setModel(model);

        NotesPane.setText("<html><body>Notes go here</body></html>");
    }

    ///////////////////////////////////////////////////////////////////////

    /**
     * File | Exit action performed
     */
    public void jMenuFileExit_actionPerformed(ActionEvent e) {
        System.exit(0);
    }

    ///////////////////////////////////////////////////////////////////////

    void MenuFileSaveReport_actionPerformed(ActionEvent e) {
        if (repAnalysis == null)
            return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Analysis Report");
        fileChooser.setCurrentDirectory(null);
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setFileFilter(new ExtensionFileFilter("html", "Analysis report (HTML file)"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION)
            return;

        File selFile = fileChooser.getSelectedFile();
        if (!selFile.getName().endsWith(".htm") && !selFile.getName().endsWith(".html"))
            selFile = new File(selFile.getAbsolutePath() + ".html");

        // If file already exists, ask confirmation to overwrite it
        if (selFile.exists()) {
            int res = JOptionPane.showConfirmDialog(null, "Are you sure you want to overwrite file " +
                    selFile.getAbsoluteFile() + " ?", "Confirm overwrite", JOptionPane.YES_NO_CANCEL_OPTION);
            if (res != JOptionPane.YES_OPTION)
                return;
        }

        ExcelReport xlRpt = new ExcelReport(repAnalysis);
        xlRpt.reportPath = selFile.getPath().substring(0, selFile.getPath().lastIndexOf(selFile.separator) + 1);
        xlRpt.writeReport(selFile);
        NotesPane.setText(xlRpt.getText());
        /*try {
            String fpath = selFile.getAbsolutePath();
			fpath = fpath.replace(':', '|');
			System.out.println("Displaying report...");
			ReportPane.setPage("file://" + fpath);
			System.out.println("Report displayed");
			}
		catch(IOException ex)
			{
			ex.printStackTrace();
			}*/

        if (xlRpt.errors.size() > 0) {
            JOptionPane.showMessageDialog(null, "There were errors during the generation of the report.");
        }

        JOptionPane.showMessageDialog(null, "The automation analyzer has saved a report as\n" +
                selFile.getAbsolutePath() + "\nThis file can be viewed in any Web browser, or\n" +
                "it can also be opened in Microsoft Excel", "Report complete", JOptionPane.INFORMATION_MESSAGE);
    }

    void MenuFileEuclidReport_actionPerformed(ActionEvent e) {
        if (repAnalysis == null)
            return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Euclid Scripts");
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setFileFilter(new ExtensionFileFilter("asn", "Euclid Analysis report (asn files)"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION)
            return;

        File selFile = fileChooser.getSelectedFile();
        if (!selFile.getName().endsWith(".asn") && !selFile.getName().endsWith(".asn"))
            selFile = new File(selFile.getAbsolutePath() + ".asn");

        // If file already exists, ask confirmation to overwrite it
        if (selFile.exists()) {
            int res = JOptionPane.showConfirmDialog(null, "Are you sure you want to overwrite file " +
                    selFile.getAbsoluteFile() + " ?", "Confirm overwrite", JOptionPane.YES_NO_CANCEL_OPTION);
            if (res != JOptionPane.YES_OPTION)
                return;
        }

        try {
            RepositoryDO repos = RepositoryAnalysis.repos;
            if (repos != null) {
                //repos.setFileDir(selFile.getAbsoulteFile());
                String path = selFile.getPath();
                repos.printFiles(path);
            }
            JOptionPane.showMessageDialog(null, "The Archimedes Metadata Rules Analyzer has saved the Euclid Scripts. \n" +
                    "\nThese file can be viewed in the Archimedes_Scripts_Directory and used with Euclid application (http://www.integrity-logic.com).", "Scripts Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void MenuFileSaveVLS_actionPerformed(ActionEvent e) {

        //prompt for Version
        String version = JOptionPane.showInputDialog("Enter Repository Version Number");
        if (repAnalysis == null) {
            JOptionPane.showMessageDialog(null, "You must select a Versata 5.5 repository (File/Open Repository...)", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            RepositoryDO repos = RepositoryAnalysis.repos;
            repos.setRepositoryVersion(version != null ? version : "1");
            if (repos != null) {
                PersistenceManager eh = new PersistenceManager(repos);
                eh.StartInbound();
            }
            JOptionPane.showMessageDialog(null, "The automation analyzer has persisted your Meta Data. \n" +
                    "Run MetaRepos Java or HTML application to view meta data.", "Meta Data Persisted", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unable to persist Meta Data - please check AutomationAnalyzer.Properties and make sure your VLS is running....", "Error", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    ///////////////////////////////////////////////////////////////////////

    /**
     * Help | About action performed
     */
    public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
        MainWindow_AboutBox dlg = new MainWindow_AboutBox(this);
        Dimension dlgSize = dlg.getPreferredSize();
        Dimension frmSize = getSize();
        Point loc = getLocation();
        dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
        dlg.setModal(true);
        dlg.show();
    }

    ///////////////////////////////////////////////////////////////////////

    void ResultsTree_keyPressed(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_F1)
            return;

        Object o = ResultsTree.getLastSelectedPathComponent();
        if (o == null)
            return;

        try {
            e.consume();
            AnalysisNode an = (AnalysisNode) o;
            helpBroker.setCurrentID(an.helpTopic);
            helpBroker.setDisplayed(true);
        } catch (Exception ee) {
            System.err.println("Problem bringing up help topic " + ee.toString());
        }
    }

    ///////////////////////////////////////////////////////////////////////

    /**
     * Overridden so we can exit when window is closed
     */
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            jMenuFileExit_actionPerformed(null);
        }
    }

    /////////////////////////////////////////////////////////////////////////

    private void MenuOpenRepository_actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(null);
        fileChooser.setDialogTitle("Open Repository");
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileFilter(new ExtensionFileFilter("Versata repository files", true));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION)
            return;

        File selRepFile = fileChooser.getSelectedFile();
        displayRepository(selRepFile);
    }

    ////////////////////////////////////////////////////////////////////////

    /**
     * This is where we create the entire tree.
     */
    private void displayRepository(File selRepFile) {
        repAnalysis = new RepositoryAnalysis(selRepFile);
        repAnalysis.analyzeRepository();

        ErrorsList.setListData(new Vector()); // Empty errors list
        if (repAnalysis.errors.size() > 0) // If errors, display them and return
        {
            ErrorsList.setListData(repAnalysis.errors);
            ResultsTabbedPane.setSelectedIndex(1);
            //return;
        }

        NotesPane.setText("");
        if (repAnalysis.notes.size() > 0) {
            StringBuffer nsb = new StringBuffer();
            nsb.append("<html><body>");
            for (int i = 0; i < repAnalysis.notes.size(); i++) {
                String note = (String) repAnalysis.notes.elementAt(i);
                nsb.append(note);
            }
            nsb.append("</body></html>");
            NotesPane.setText(nsb.toString());
        }

        topResultNode.removeAllChildren();
        topResultNode.setUserObject("Analysis of repository : " + repAnalysis.name + " in directory " + repAnalysis.rootDirectory);
        //helpBroker.enableHelpKey(topResultNode, "top", helpSet);

        // Display repository level results
        doResultNode = new AnalysisNode("Total amount of code : " + repAnalysis.totalLines + " lines", "repository_analysis_totalCode");
        topResultNode.add(doResultNode);
        doResultNode = new AnalysisNode("Total amount of generated code : " + repAnalysis.totalGeneratedLines +
                " lines (" + formatPercent(repAnalysis.totalGeneratedLines, repAnalysis.totalLines) + "%)", "repository_analysis_totalGenCode");
        topResultNode.add(doResultNode);
        doResultNode = new AnalysisNode("Total amount of user code : " + repAnalysis.totalUserLines +
                " lines (" + formatPercent(repAnalysis.totalUserLines, repAnalysis.totalLines) + "%)", "repository_analysis_totalUserCode");
        topResultNode.add(doResultNode);
        // tmb added method totals
        doResultNode = new AnalysisNode("Total Method Count : " + repAnalysis.totalMethods +
                " Methods (" + formatPercent(repAnalysis.totalMethods, repAnalysis.totalMethods) + "%)", "repository_analysis_totalUserCode");
        topResultNode.add(doResultNode);

        doResultNode = new AnalysisNode("Total User Method Count : " + repAnalysis.totalUserMethods +
                " Methods (" + formatPercent(repAnalysis.totalUserMethods, repAnalysis.totalMethods) + "%)", "repository_analysis_totalUserCode");
        topResultNode.add(doResultNode);
        doResultNode = new AnalysisNode("Total Generated Method Count : " + repAnalysis.totalGenMethods +
                " Methods (" + formatPercent(repAnalysis.totalGenMethods, repAnalysis.totalMethods) + "%)", "repository_analysis_totalUserCode");
        topResultNode.add(doResultNode);


        // Display relevant numbers of errors
        if (repAnalysis.totalDOerrors > 0) {
            DefaultMutableTreeNode en = new AnalysisNode("Number of errors in data objects : " + repAnalysis.totalDOerrors);
            topResultNode.add(en);
        }
        if (repAnalysis.totalQOerrors > 0) {
            DefaultMutableTreeNode en = new AnalysisNode("Number of errors in query objects : " + repAnalysis.totalQOerrors);
            topResultNode.add(en);
        }
        if (repAnalysis.totalHTMLAppsErrors > 0) {
            DefaultMutableTreeNode en = new AnalysisNode("Number of errors in HTML apps : " + repAnalysis.totalHTMLAppsErrors);
            topResultNode.add(en);
        }
        if (repAnalysis.totalOtherErrors > 0) {
            DefaultMutableTreeNode en = new AnalysisNode("Number of errors in other files : " + repAnalysis.totalOtherErrors);
            topResultNode.add(en);
        }


        // Display data object results
        doResultNode = new AnalysisNode("Data objects", "dataobject");
        topResultNode.add(doResultNode);

        DefaultMutableTreeNode n = new AnalysisNode("Number of data objects : " + repAnalysis.dataObjects.size(), "dataobject_numDataObjects");
        doResultNode.add(n);
        DefaultMutableTreeNode relsNode = new AnalysisNode("Total number of relationships : " + repAnalysis.numRelationships, "dataobject_numRelationships");
        doResultNode.add(relsNode);
        n = new AnalysisNode("Number of enforced relationships : " + repAnalysis.numEnforcedRelationships +
                " (" + formatPercent(repAnalysis.numEnforcedRelationships, repAnalysis.numRelationships) + "%)", "dataobject_numEnforcedRelationships");
        relsNode.add(n);
        n = new AnalysisNode("Number of unenforced relationships : " + (repAnalysis.numRelationships - repAnalysis.numEnforcedRelationships) +
                " (" + formatPercent((repAnalysis.numRelationships - repAnalysis.numEnforcedRelationships), repAnalysis.numRelationships) + "%)", "dataobject_numUnenforcedRelationships");
        relsNode.add(n);
        DefaultMutableTreeNode attribsNode = new AnalysisNode("Total number of attributes : " + repAnalysis.totalNumAttribs, "dataobject_totalNumAttribs");
        doResultNode.add(attribsNode);
        n = new AnalysisNode("Largest number of attributes : " + repAnalysis.largestNumAttribs +
                " (" + repAnalysis.largestNumAttribsName + ")", "dataobject_attribs_largestNumAttribs");
        attribsNode.add(n);
        n = new AnalysisNode("Total number of persistent attributes : " + repAnalysis.totalNumPersistentAttribs, "dataobject_attribs_totalNumPersAttribs");
        attribsNode.add(n);
        n = new AnalysisNode("Total number of non-persistent attributes : " + repAnalysis.totalNumNonPersistentAttribs, "dataobject_attribs_totalNumNonPersAttribs");
        attribsNode.add(n);
        n = new AnalysisNode("Total number of prevent user update attributes : " + repAnalysis.totalNumPreventUserUpdateAttribs,
                "dataobject_attribs_totalNumPreventUserUpdateAttribs");
        attribsNode.add(n);
        n = new AnalysisNode("Total number of coded value list attributes : " + repAnalysis.totalNumCodedValueAttribs,
                "dataobject_attribs_totalNumCVLAttribs");
        attribsNode.add(n);
        n = new AnalysisNode("Total number of validation attributes : " + repAnalysis.totalNumValidationAttribs,
                "dataobject_attribs_totalNumValidationAttribs");
        attribsNode.add(n);
        n = new AnalysisNode("Total number of derived attributes : " + repAnalysis.totalNumDerivedAttribs,
                "dataobject_attribs_totalNumDerivedAttribs");
        attribsNode.add(n);

        DefaultMutableTreeNode rulesNode = new AnalysisNode("Total number of rules : " +
                (repAnalysis.numRelationships + repAnalysis.totalNumConstraints + repAnalysis.totalNumActions +
                        repAnalysis.totalNumSumAttribs + repAnalysis.totalNumCountAttribs + repAnalysis.totalNumParentReplicateAttribs +
                        repAnalysis.totalNumFormulaAttribs + repAnalysis.totalNumDefaultAttribs + repAnalysis.totalNumValidationAttribs +
                        repAnalysis.totalNumCodedValueAttribs), "dataobject_totalNumRules");
        doResultNode.add(rulesNode);
        n = new AnalysisNode("Total number of constraints : " + repAnalysis.totalNumConstraints, "dataobject_rules_totalNumConstraints");
        rulesNode.add(n);
        n = new AnalysisNode("Total number of actions : " + repAnalysis.totalNumActions, "dataobject_rules_totalNumActions");
        rulesNode.add(n);
        n = new AnalysisNode("Total number of sum attributes : " + repAnalysis.totalNumSumAttribs, "dataobject_rules_totalNumSums");
        rulesNode.add(n);
        n = new AnalysisNode("Total number of count attributes : " + repAnalysis.totalNumCountAttribs, "dataobject_rules_totalNumCount");
        rulesNode.add(n);
        n = new AnalysisNode("Total number of parent replicate attributes : " + repAnalysis.totalNumParentReplicateAttribs, "dataobject_rules_totalNumparentReplicates");
        rulesNode.add(n);
        n = new AnalysisNode("Total number of formula attributes : " + repAnalysis.totalNumFormulaAttribs, "dataobject_rules_totalNumFormulas");
        rulesNode.add(n);
        n = new AnalysisNode("Total number of default attributes : " + repAnalysis.totalNumDefaultAttribs, "dataobject_rules_totalNumDefaults");
        rulesNode.add(n);
        n = new AnalysisNode("Total number of coded value list attributes : " + repAnalysis.totalNumCodedValueAttribs, "dataobject_attribs_totalNumCVLAttribs");
        rulesNode.add(n);
        n = new AnalysisNode("Total number of validation attributes : " + repAnalysis.totalNumValidationAttribs, "dataobject_attribs_totalNumValidationAttribs");
        rulesNode.add(n);

        n = new AnalysisNode("Total amount of code : " + repAnalysis.totalDOlines + " lines", "dataobject_totalAmtCode");
        doResultNode.add(n);
        n = new AnalysisNode("Total amount of generated code : " + repAnalysis.totalDOgeneratedLines +
                " lines (" + formatPercent(repAnalysis.totalDOgeneratedLines, repAnalysis.totalDOlines) + "%)",
                "dataobject_totalAmtGenCode");
        doResultNode.add(n);
        n = new AnalysisNode("Total amount of user code : " + repAnalysis.totalDOuserLines +
                " lines (" + formatPercent(repAnalysis.totalDOuserLines, repAnalysis.totalDOlines) + "%)", "dataobject_totalAmtUserCode");
        doResultNode.add(n);

        //tmb
        n = new AnalysisNode("Total Methods : " + repAnalysis.totalMethods, "dataobject_totalAmtGenCode");
        doResultNode.add(n);
        n = new AnalysisNode("Total amount of generated methods : " + repAnalysis.totalGenMethods +
                " methods (" + formatPercent(repAnalysis.totalGenMethods, repAnalysis.totalMethods) + "%)",
                "dataobject_totalAmtGenCode");
        doResultNode.add(n);
        n = new AnalysisNode("Total amount of user methods : " + repAnalysis.totalUserMethods +
                " methods (" + formatPercent(repAnalysis.totalUserMethods, repAnalysis.totalMethods) + "%)", "dataobject_totalAmtUserCode");
        doResultNode.add(n);

        DefaultMutableTreeNode perDataObjectNode = new AnalysisNode("Data objects", "dataobject_dataObjects");
        doResultNode.add(perDataObjectNode);

        Iterator doIter = repAnalysis.dataObjects.values().iterator();
        while (doIter.hasNext()) {
            DataObjectAnalysis doa = (DataObjectAnalysis) doIter.next();

            DefaultMutableTreeNode doNode;
            int numErrors = doa.errors.size();
            if (numErrors == 0)
                doNode = new AnalysisNode(doa.simpleName, "dataobject_do_dataObject");
            else
                doNode = new AnalysisNode(doa.simpleName + " - " + numErrors + " errors", "dataobject_do_dataObject");
            perDataObjectNode.add(doNode);

            n = new AnalysisNode("Number of attributes : " + doa.numAttribs, "dataobject_do_numAttribs");
            doNode.add(n);

            int numDORelations = doa.parents.size() + doa.children.size();
            AnalysisNode doRelsNode = new AnalysisNode("Number of relationships : " + numDORelations, "dataobject_do_numRels");
            doNode.add(doRelsNode);
            if (numDORelations > 0) {
                n = new AnalysisNode("Number of enforced relationships : " + doa.numEnforcedRelationships, "dataobject_do_numEnforcedRels");
                doRelsNode.add(n);
                n = new AnalysisNode("Number of unenforced relationships : " +
                        (numDORelations - doa.numEnforcedRelationships), "dataobject_do_numUnenforcedRels");
                doRelsNode.add(n);

                if (doa.children.size() > 0) {
                    AnalysisNode doChildNode = new AnalysisNode("Children : " +
                            doa.children.size(), "dataobject_do_relsChildren");
                    doRelsNode.add(doChildNode);
                    for (int i = 0; i < doa.children.size(); i++) {
                        String childEntry[] = (String[]) doa.children.elementAt(i);
                        String nname = childEntry[0] + " (" + childEntry[1] + ")";
                        if (childEntry[2].equalsIgnoreCase("false"))
                            nname = nname + " - not enforced";
                        n = new AnalysisNode(nname, "dataobject_do_child");
                        doChildNode.add(n);
                    }
                }

                if (doa.parents.size() > 0) {
                    AnalysisNode doParentNode = new AnalysisNode("Parents : " +
                            doa.parents.size(), "dataobject_do_relsParents");
                    doRelsNode.add(doParentNode);
                    for (int i = 0; i < doa.parents.size(); i++) {
                        String parentEntry[] = (String[]) doa.parents.elementAt(i);
                        String nname = parentEntry[0] + " (" + parentEntry[1] + ")";
                        if (parentEntry[2].equalsIgnoreCase("false"))
                            nname = nname + " - not enforced";
                        n = new AnalysisNode(nname, "dataobject_do_parent");
                        doParentNode.add(n);
                    }
                }
            }

            DefaultMutableTreeNode statsNode = new AnalysisNode("Total lines of code : " + doa.totalLines, "dataobject_do_totalLines");
            doNode.add(statsNode);
            n = new AnalysisNode("Total generated lines : " + doa.totalGeneratedLines +
                    " (" + formatPercent(doa.totalGeneratedLines, doa.totalLines) + "%)", "dataobject_do_code_genLines");
            statsNode.add(n);
            n = new AnalysisNode("Total user lines : " + doa.totalUserLines, "dataobject_do_code_userLines");
            statsNode.add(n);

            n = new AnalysisNode("Total generated methods : " + doa.totalGeneratedMethods, "dataobject_do_code_userLines");
            statsNode.add(n);   //tmb

            n = new AnalysisNode("Total user methods : " + doa.totalUserMethods, "dataobject_do_code_userLines");
            statsNode.add(n);  //tmb


            DefaultMutableTreeNode rulesStatsNode = new AnalysisNode("Rules statistics", "dataobject_do_rules");
            doNode.add(rulesStatsNode);

            n = new AnalysisNode("Number of persistent attributes : " + doa.numPersistentAttribs +
                    " (" + formatPercent(doa.numPersistentAttribs, doa.numAttribs) + "%)",
                    "dataobject_do_rules_numPersAttribs");
            rulesStatsNode.add(n);
            n = new AnalysisNode("Number of non-persistent attributes : " + doa.numNonPersistentAttribs +
                    " (" + formatPercent(doa.numNonPersistentAttribs, doa.numAttribs) + "%)",
                    "dataobject_do_rules_numNonPersAttribs");
            rulesStatsNode.add(n);
            n = new AnalysisNode("Number of required attributes : " + doa.numRequiredAttribs +
                    " (" + formatPercent(doa.numRequiredAttribs, doa.numAttribs) + "%)",
                    "dataobject_do_rules_numReqAttribs");
            rulesStatsNode.add(n);
            n = new AnalysisNode("Number of rules : " +
                    (doa.numActions + doa.numCodedValueAttribs + doa.numConstraints + doa.numDerivedAttribs + doa.numValidationAttribs),
                    "dataobject_do_rules_numRules");
            rulesStatsNode.add(n);
            n = new AnalysisNode("Number of actions : " + doa.numActions, "dataobject_do_rules_numActions");
            rulesStatsNode.add(n);
            n = new AnalysisNode("Number of constraints : " + doa.numConstraints, "dataobject_do_rules_numConstraints");
            rulesStatsNode.add(n);
            n = new AnalysisNode("Number of coded value attributes : " + doa.numCodedValueAttribs, "dataobject_do_rules_numCVLs");
            rulesStatsNode.add(n);
            n = new AnalysisNode("Number of validated attributes : " + doa.numValidationAttribs, "dataobject_do_rules_numValids");
            rulesStatsNode.add(n);
            DefaultMutableTreeNode derivedNode = new AnalysisNode("Number of derived attributes : " + doa.numDerivedAttribs +
                    " (" + formatPercent(doa.numDerivedAttribs, doa.numAttribs) + "%)", "dataobject_do_rules_numDerivedAttribs");
            rulesStatsNode.add(derivedNode);
            n = new AnalysisNode("Number of count attributes : " + doa.numCountAttribs, "dataobject_do_rules_derive_count");
            derivedNode.add(n);
            n = new AnalysisNode("Number of sum attributes : " + doa.numSumAttribs, "dataobject_do_rules_derive_sum");
            derivedNode.add(n);
            n = new AnalysisNode("Number of defaults : " + doa.numDefaultAttribs, "dataobject_do_rules_derive_default");
            derivedNode.add(n);
            n = new AnalysisNode("Number of formula attributes : " + doa.numFormulaAttribs, "dataobject_do_rules_derive_formula");
            derivedNode.add(n);
            n = new AnalysisNode("Number of parent replicate attributes : " + doa.numParentReplicateAttribs, "dataobject_do_rules_derive_replicate");
            derivedNode.add(n);

            DefaultMutableTreeNode filesNode = new AnalysisNode("Files", "dataobject_do_files");
            doNode.add(filesNode);

            DefaultMutableTreeNode baseFileNode = new AnalysisNode("Base file : " + doa.baseFileName, "dataobject_do_files_base");
            filesNode.add(baseFileNode);
            AnalysisNode linesNode = new AnalysisNode("Total code lines : " + doa.baseFileNumLines, "dataobject_do_files_base_lines");
            baseFileNode.add(linesNode);

            AnalysisNode userFileNode = new AnalysisNode("User file : " + doa.userFileName, "dataobject_do_files_user");
            filesNode.add(userFileNode);
            linesNode = new AnalysisNode("Total code lines : " + doa.userFileNumLines, "dataobject_do_files_user_totalLines");
            userFileNode.add(linesNode);
            linesNode = new AnalysisNode("Total generated lines : " + doa.userFileNumGenLines, "dataobject_do_files_user_totalGenLines");
            userFileNode.add(linesNode);
            linesNode = new AnalysisNode("Total user lines : " + doa.userFileNumUserLines, "dataobject_do_files_user_totalUserLines");
            userFileNode.add(linesNode);
            linesNode = new AnalysisNode("Total raw lines : " + doa.userFileNumTextLines, "dataobject_do_files_user_totalRawLines");
            userFileNode.add(linesNode);
            linesNode = new AnalysisNode("Total non-blank lines : " + doa.userFileNumNonBlankLines, "dataobject_do_files_user_totalNonBlankLines");
            userFileNode.add(linesNode);
            linesNode = new AnalysisNode("Total comment lines : " + doa.userFileNumCommentLines, "dataobject_do_files_user_totalCommentLines");
            userFileNode.add(linesNode);
            linesNode = new AnalysisNode("Total misc. lines : " + doa.userFileNumMiscLines, "dataobject_do_files_user_totalMiscLines");
            userFileNode.add(linesNode);

            if (doa.errors.size() > 0) {
                DefaultMutableTreeNode errorsNode = new AnalysisNode("Errors");
                doNode.add(errorsNode);
                for (int i = 0; i < doa.errors.size(); i++) {
                    String err = (String) doa.errors.elementAt(i);
                    n = new AnalysisNode(err);
                    errorsNode.add(n);
                }
            }
        }

        // Display query object results
        queryResultNode = new AnalysisNode("Query objects");
        topResultNode.add(queryResultNode);
        n = new AnalysisNode("Number of query objects : " + repAnalysis.queryObjects.size());
        queryResultNode.add(n);
        DefaultMutableTreeNode qAttribsNode = new AnalysisNode("Total number of attributes : " + repAnalysis.totalNumQueryAttribs);
        queryResultNode.add(qAttribsNode);
        n = new AnalysisNode("Total number of persistent attributes : " + repAnalysis.totalNumQueryPersistentAttribs);
        qAttribsNode.add(n);
        n = new AnalysisNode("Total number of non-persistent attributes : " + repAnalysis.totalNumQueryNonPersistentAttribs);
        qAttribsNode.add(n);
        n = new AnalysisNode("Total number of aggregated attributes : " + repAnalysis.totalNumQueryAggregatedAttribs);
        qAttribsNode.add(n);
        n = new AnalysisNode("Total number of derived attributes : " + repAnalysis.totalNumQueryDerivedAttribs);
        qAttribsNode.add(n);

        n = new AnalysisNode("Total amount of code : " + repAnalysis.totalQOlines + " lines");
        queryResultNode.add(n);
        n = new AnalysisNode("Total amount of generated code : " + repAnalysis.totalQOgeneratedLines +
                " lines (" + formatPercent(repAnalysis.totalQOgeneratedLines, repAnalysis.totalQOlines) + "%)");
        queryResultNode.add(n);
        n = new AnalysisNode("Total amount of user code : " + repAnalysis.totalQOuserLines +
                " lines (" + formatPercent(repAnalysis.totalQOuserLines, repAnalysis.totalQOlines) + "%)");
        queryResultNode.add(n);

        if (repAnalysis.queryObjects.size() > 0) {
            DefaultMutableTreeNode perQueryObjectNode = new AnalysisNode("Query objects");
            queryResultNode.add(perQueryObjectNode);
            Iterator qoIter = repAnalysis.queryObjects.values().iterator();
            while (qoIter.hasNext()) {
                QueryObjectAnalysis qoa = (QueryObjectAnalysis) qoIter.next();

                DefaultMutableTreeNode qoNode;
                int numErrors = qoa.errors.size();
                if (numErrors == 0)
                    qoNode = new AnalysisNode(qoa.simpleName);
                else
                    qoNode = new AnalysisNode(qoa.simpleName + " - " + numErrors + " errors");
                perQueryObjectNode.add(qoNode);

                n = new AnalysisNode("Number of objects involved : " + qoa.numObjects);
                qoNode.add(n);
                n = new AnalysisNode("Number of attributes : " + qoa.numAttribs);
                qoNode.add(n);

                DefaultMutableTreeNode statsNode = new AnalysisNode("Code statistics");
                qoNode.add(statsNode);

                DefaultMutableTreeNode linesNode = new AnalysisNode("Total lines : " + qoa.totalLines);
                statsNode.add(linesNode);
                linesNode = new AnalysisNode("Total generated lines : " + qoa.totalGeneratedLines +
                        " (" + formatPercent(qoa.totalGeneratedLines, qoa.totalLines) + "%)");
                statsNode.add(linesNode);
                linesNode = new AnalysisNode("Total user lines : " + qoa.totalUserLines);
                statsNode.add(linesNode);

                DefaultMutableTreeNode rulesStatsNode = new AnalysisNode("Rules statistics");
                qoNode.add(rulesStatsNode);

                n = new AnalysisNode("Number of persistent attributes : " + qoa.numPersistentAttribs +
                        " (" + formatPercent(qoa.numPersistentAttribs, qoa.numAttribs) + "%)");
                rulesStatsNode.add(n);
                n = new AnalysisNode("Number of non-persistent attributes : " + qoa.numNonPersistentAttribs +
                        " (" + formatPercent(qoa.numNonPersistentAttribs, qoa.numAttribs) + "%)");
                rulesStatsNode.add(n);
                n = new AnalysisNode("Number of aggregated attributes : " + qoa.numAggregatedAttribs +
                        " (" + formatPercent(qoa.numAggregatedAttribs, qoa.numAttribs) + "%)");
                rulesStatsNode.add(n);
                n = new AnalysisNode("Number of derived attributes : " + qoa.numDerivedAttribs +
                        " (" + formatPercent(qoa.numDerivedAttribs, qoa.numAttribs) + "%)");
                rulesStatsNode.add(n);

                DefaultMutableTreeNode filesNode = new AnalysisNode("Files");
                qoNode.add(filesNode);

                DefaultMutableTreeNode baseFileNode = new AnalysisNode("Base file : " + qoa.baseFileName);
                filesNode.add(baseFileNode);
                linesNode = new AnalysisNode("Total code lines : " + qoa.baseFileNumLines);
                baseFileNode.add(linesNode);

                DefaultMutableTreeNode userFileNode = new AnalysisNode("User file : " + qoa.userFileName);
                filesNode.add(userFileNode);
                linesNode = new AnalysisNode("Total code lines : " + qoa.userFileNumLines);
                userFileNode.add(linesNode);
                linesNode = new AnalysisNode("Total generated lines : " + qoa.userFileNumGenLines);
                userFileNode.add(linesNode);
                linesNode = new AnalysisNode("Total user lines : " + qoa.userFileNumUserLines);
                userFileNode.add(linesNode);
                linesNode = new AnalysisNode("Total raw lines : " + qoa.userFileNumTextLines);
                userFileNode.add(linesNode);
                linesNode = new AnalysisNode("Total non-blank lines : " + qoa.userFileNumNonBlankLines);
                userFileNode.add(linesNode);
                linesNode = new AnalysisNode("Total comment lines : " + qoa.userFileNumCommentLines);
                userFileNode.add(linesNode);
                linesNode = new AnalysisNode("Total misc. lines : " + qoa.userFileNumMiscLines);
                userFileNode.add(linesNode);

                if (qoa.errors.size() > 0) {
                    DefaultMutableTreeNode errorsNode = new AnalysisNode("Errors");
                    qoNode.add(errorsNode);
                    for (int i = 0; i < qoa.errors.size(); i++) {
                        String err = (String) qoa.errors.elementAt(i);
                        n = new AnalysisNode(err);
                        errorsNode.add(n);
                    }
                }
            }
        }

        // Display HTML apps results
        htmlResultNode = new AnalysisNode("HTML applications", "html_top");
        topResultNode.add(htmlResultNode);
        n = new AnalysisNode("Number of HTML applications : " + repAnalysis.htmlApps.size(), "html_numApps");
        htmlResultNode.add(n);
        n = new AnalysisNode("Number of HTML pages : " + repAnalysis.totalNumHTMLpages +
                " (avg. " + formatDecimal(repAnalysis.totalNumHTMLpages, repAnalysis.htmlApps.size()) + "/app)", "html_numPages");
        htmlResultNode.add(n);
        n = new AnalysisNode("Number of regenerable HTML pages : " + repAnalysis.totalNumRegenHTMLpages +
                " (" + formatPercent(repAnalysis.totalNumRegenHTMLpages, repAnalysis.totalNumHTMLpages) + "%)", "html_numRegenPages");
        htmlResultNode.add(n);
        n = new AnalysisNode("Total amount of code : " + repAnalysis.totalHTMLlines + " lines", "html_totalCode");
        htmlResultNode.add(n);
        n = new AnalysisNode("Total amount of generated code : " + repAnalysis.totalHTMLgeneratedLines +
                " lines (" + formatPercent(repAnalysis.totalHTMLgeneratedLines, repAnalysis.totalHTMLlines) + "%)", "html_totalGenCode");
        htmlResultNode.add(n);
        n = new AnalysisNode("Total amount of user code : " + repAnalysis.totalHTMLuserLines +
                " lines (" + formatPercent(repAnalysis.totalHTMLuserLines, repAnalysis.totalHTMLlines) + "%)", "html_totalUserCode");
        htmlResultNode.add(n);

        if (repAnalysis.htmlApps.size() > 0) {
            DefaultMutableTreeNode htmlAppsNode = new AnalysisNode("Apps", "html_apps");
            htmlResultNode.add(htmlAppsNode);

            Iterator htaIter = repAnalysis.htmlApps.values().iterator();
            while (htaIter.hasNext()) {
                HTMLApplicationAnalysis hta = (HTMLApplicationAnalysis) htaIter.next();

                DefaultMutableTreeNode htaNode;
                int numErrors = hta.errors.size();
                if (numErrors == 0)
                    htaNode = new AnalysisNode(hta.name, "html_app");
                else
                    htaNode = new AnalysisNode(hta.name + " - " + numErrors + " errors", "html_app");
                htmlAppsNode.add(htaNode);

                AnalysisNode pagesNode = new AnalysisNode("Number of pages : " + hta.numForms, "html_app_numPages");
                htaNode.add(pagesNode);
                Iterator formsIter = hta.forms.values().iterator();
                while (formsIter.hasNext()) {
                    FormAnalysis fa = (FormAnalysis) formsIter.next();
                    AnalysisNode pageNode = new AnalysisNode(fa.formName + " : " + fa.numLines + " lines", "html_app_page_numLines");
                    pagesNode.add(pageNode);
                    n = new AnalysisNode("Lines of generated code : " + fa.numGenLines, "html_app_numLines");
                    pageNode.add(n);
                    n = new AnalysisNode("Lines of user code : " + fa.numUserLines, "html_app_numLines");
                    pageNode.add(n);
                    n = new AnalysisNode("Regenerable : " + fa.isRegen, "html_app_numLines");
                    pageNode.add(n);
                }
                n = new AnalysisNode("Number of regenerable pages : " + (hta.numForms - hta.numNonRegenForms) +
                        " (" + formatPercent((hta.numForms - hta.numNonRegenForms), hta.numForms) + "%)", "html_app_numRegenPages");
                htaNode.add(n);

                n = new AnalysisNode("Lines of code : " + hta.totalLines, "html_app_numLines");
                htaNode.add(n);
                n = new AnalysisNode("Lines of generated code : " + hta.totalGeneratedLines +
                        " (" + formatPercent(hta.totalGeneratedLines, hta.totalLines) + "%)", "html_app_numGenLines");
                htaNode.add(n);
                n = new AnalysisNode("Lines of user code : " + hta.totalUserLines +
                        " (" + formatPercent(hta.totalUserLines, hta.totalLines) + "%)", "html_app_numUserLines");
                htaNode.add(n);

                n = new AnalysisNode("Total Methods : " + hta.totalMethods, "html_app_numLines");
                htaNode.add(n);
                n = new AnalysisNode("Generated Methods : " + hta.totalGeneratedMethods +
                        " (" + formatPercent(hta.totalGeneratedMethods, hta.totalMethods) + "%)", "html_app_numGenLines");
                htaNode.add(n);
                n = new AnalysisNode("User Methods : " + hta.totalUserMethods +
                        " (" + formatPercent(hta.totalUserMethods, hta.totalMethods) + "%)", "html_app_numUserLines");
                htaNode.add(n);

                // Display errors if any
                if (hta.errors.size() > 0) {
                    DefaultMutableTreeNode errorsNode = new AnalysisNode("Errors");
                    htaNode.add(errorsNode);
                    for (int i = 0; i < hta.errors.size(); i++) {
                        String err = (String) hta.errors.elementAt(i);
                        n = new AnalysisNode(err);
                        errorsNode.add(n);
                    }
                }
            }
        }

        // Display Java apps results
        javaResultNode = new AnalysisNode("Java applications");
        topResultNode.add(javaResultNode);
        n = new AnalysisNode("Number of Java applications : " + repAnalysis.javaApps.size());
        javaResultNode.add(n);
        n = new AnalysisNode("Number of Java forms : " + repAnalysis.totalNumJavaForms +
                " (avg. " + formatDecimal(repAnalysis.totalNumJavaForms, repAnalysis.javaApps.size()) + "/app)");
        javaResultNode.add(n);
        n = new AnalysisNode("Number of regenerable Java forms : " + repAnalysis.totalNumRegenJavaForms +
                " (" + formatPercent(repAnalysis.totalNumRegenJavaForms, repAnalysis.totalNumJavaForms) + "%)");
        javaResultNode.add(n);
        n = new AnalysisNode("Total amount of code : " + repAnalysis.totalJavaLines + " lines");
        javaResultNode.add(n);
        n = new AnalysisNode("Total amount of generated code : " + repAnalysis.totalJavaGeneratedLines +
                " lines (" + formatPercent(repAnalysis.totalJavaGeneratedLines, repAnalysis.totalJavaLines) + "%)");
        javaResultNode.add(n);
        n = new AnalysisNode("Total amount of user code : " + repAnalysis.totalJavaUserLines +
                " lines (" + formatPercent(repAnalysis.totalJavaUserLines, repAnalysis.totalJavaLines) + "%)");
        javaResultNode.add(n);

        if (repAnalysis.javaApps.size() > 0) {
            DefaultMutableTreeNode javaAppsNode = new AnalysisNode("Apps");
            javaResultNode.add(javaAppsNode);

            Iterator jaaIter = repAnalysis.javaApps.values().iterator();
            while (jaaIter.hasNext()) {
                JavaApplicationAnalysis jaa = (JavaApplicationAnalysis) jaaIter.next();

                DefaultMutableTreeNode jaaNode;
                int numErrors = jaa.errors.size();
                if (numErrors == 0)
                    jaaNode = new AnalysisNode(jaa.name);
                else
                    jaaNode = new AnalysisNode(jaa.name + " - " + numErrors + " errors");
                javaAppsNode.add(jaaNode);

                n = new AnalysisNode("Number of forms : " + jaa.numForms);
                jaaNode.add(n);
                n = new AnalysisNode("Number of regenerable forms : " + (jaa.numForms - jaa.numNonRegenForms) +
                        " (" + formatPercent((jaa.numForms - jaa.numNonRegenForms), jaa.numForms) + "%)");
                jaaNode.add(n);

                n = new AnalysisNode("Lines of code : " + jaa.totalLines);
                jaaNode.add(n);
                n = new AnalysisNode("Lines of generated code : " + jaa.totalGeneratedLines +
                        " (" + formatPercent(jaa.totalGeneratedLines, jaa.totalLines) + "%)");
                jaaNode.add(n);
                n = new AnalysisNode("Lines of user code : " + jaa.totalUserLines +
                        " (" + formatPercent(jaa.totalUserLines, jaa.totalLines) + "%)");
                jaaNode.add(n);

                n = new AnalysisNode("Total Methods : " + jaa.totalMethods);
                jaaNode.add(n);
                n = new AnalysisNode("Generated Methods : " + jaa.totalGeneratedMethods +
                        " (" + formatPercent(jaa.totalGeneratedMethods, jaa.totalMethods) + "%)");
                jaaNode.add(n);
                n = new AnalysisNode("User Methods : " + jaa.totalUserMethods +
                        " (" + formatPercent(jaa.totalUserMethods, jaa.totalMethods) + "%)");
                jaaNode.add(n);

                // Display errors if any
                if (jaa.errors.size() > 0) {
                    DefaultMutableTreeNode errorsNode = new AnalysisNode("Errors");
                    jaaNode.add(errorsNode);
                    for (int i = 0; i < jaa.errors.size(); i++) {
                        String err = (String) jaa.errors.elementAt(i);
                        n = new AnalysisNode(err);
                        errorsNode.add(n);
                    }
                }
            }
        }

        // Display other files results
        otherResultNode = new AnalysisNode("Other files");
        topResultNode.add(otherResultNode);
        n = new AnalysisNode("Number of other files : " + repAnalysis.totalNumOtherFiles);
        otherResultNode.add(n);
        n = new AnalysisNode("Total lines of code : " + repAnalysis.totalOtherFilesLines);
        otherResultNode.add(n);

        if (repAnalysis.otherFiles.size() > 0) {
            DefaultMutableTreeNode otherFiles = new AnalysisNode("Files");
            otherResultNode.add(otherFiles);

            Iterator otfIter = repAnalysis.otherFiles.values().iterator();
            while (otfIter.hasNext()) {
                OtherFileAnalysis ofa = (OtherFileAnalysis) otfIter.next();

                DefaultMutableTreeNode ofaNode;
                int numErrors = ofa.errors.size();
                if (numErrors == 0)
                    ofaNode = new AnalysisNode(ofa.simpleName + " : " + ofa.numCodeLines + " lines - " + ofa.numMethods + " Methods ");
                else
                    ofaNode = new AnalysisNode(ofa.simpleName + " : " + ofa.numCodeLines + " lines - " + ofa.numMethods + " Methods - " + numErrors + " errors");
                otherFiles.add(ofaNode);


                // Display errors if any
                if (ofa.errors.size() > 0) {
                    DefaultMutableTreeNode errorsNode = new AnalysisNode("Errors");
                    ofaNode.add(errorsNode);
                    for (int i = 0; i < ofa.errors.size(); i++) {
                        String err = (String) ofa.errors.elementAt(i);
                        n = new AnalysisNode(err);
                        errorsNode.add(n);
                    }
                }
            }
        }

        // Refresh tree
        DefaultTreeModel model = new DefaultTreeModel(topResultNode);
        ResultsTree.setModel(model);
    }

    ////////////////////////////////////////////////////////////////////////

    private static DecimalFormat decForm = new DecimalFormat("###.##");
    JScrollPane NotesScrollPane = new JScrollPane();
    JEditorPane NotesPane = new JEditorPane();

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

    ////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    public class ExtensionFileFilter extends javax.swing.filechooser.FileFilter {
        private String extension;
        private int extLength;
        private String name;
        private boolean repositoryOnly = false;

        /**
         * Create a new file filter.
         *
         * @arg extension The acceptable extension, e.g. "html"
         * @arg name The name for this filter, which is typically displayed in the file selection dialog
         */
        public ExtensionFileFilter(String extension, String name) {
            super();
            this.extension = extension;
            this.name = name;
            this.extLength = extension.length();
        }

        /**
         * Create a new file filter for XML files.
         *
         * @arg name The name for this filter, which is typically displayed in the file selection dialog
         * @arg repositoryOnly If true, only allow repository files (i.e. must be in Source directory)
         */
        public ExtensionFileFilter(String name, boolean repositoryOnly) {
            this("xml", name);
            this.repositoryOnly = repositoryOnly;
        }

        public boolean accept(File f) {
            if (f.isDirectory())
                return true;

            // If file name is too short, no point in continuing
            String fileName = f.getName();
            if (fileName.length() < (extLength + 2))
                return false;

            // If the file does not end with extension, it's not for us
            String lastChars = fileName.substring(fileName.length() - (extLength + 1));
            if (!lastChars.equalsIgnoreCase("." + extension))
                return false;

            if (!repositoryOnly)
                return true;

            // Check whether the file is in a directory named Source
            String parentDirName = f.getParentFile().getName();
            if (parentDirName.equalsIgnoreCase("Source"))
                return true;
            else
                return false;
        }

        public String getDescription() {
            return name;
        }
    }

    //////////////////////////////////////////////////////////////////////////

    public class AnalysisNode extends javax.swing.tree.DefaultMutableTreeNode {
        public String helpTopic;

        public AnalysisNode(String text) {
            super(text);
            this.helpTopic = "top";
        }

        public AnalysisNode(String text, String helpTopic) {
            super(text);
            this.helpTopic = helpTopic;
        }
    }
}