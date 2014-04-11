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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

/**
 * Title:
 * Description:
 * Copyright:    Copyright Versata, Inc. 2001
 * Company: Versata
 *
 * @author Max Tardiveau, Versata + Tyler Band - Band Software Design LLC
 * @version $Id: MainWindow_AboutBox.java,v 1.3 2003/04/19 02:03:43 Tyler Exp $
 */

public class MainWindow_AboutBox extends JDialog implements ActionListener {

    JPanel panel1 = new JPanel();
    JButton button1 = new JButton();
    JLabel label2 = new JLabel();
    JLabel label3 = new JLabel();
    JLabel label5 = new JLabel();
    String product = "Archimedes Metadata Anaylsis for Versata 5.5.x";
    String version = "1.7";
    String copyright = "Copyright (c) 2003";
    String comments = "";
    JButton jButton1 = new JButton();
    JLabel label4 = new JLabel();
    JTextArea jTextArea1 = new JTextArea();

    public MainWindow_AboutBox(Frame parent) {
        super(parent);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        pack();


    }

    /**
     * ******************************************************************
     * Component initialization
     */
    private void jbInit() throws Exception {
        //imageLabel.setIcon(new ImageIcon(MainWindow_AboutBox.class.getResource("images/SmallVersata.gif")));
        this.setResizable(false);
        this.getContentPane().setBackground(Color.white);
        this.setModal(true);
        this.setTitle("About Archimeds Metadata Rules Analyzer");
        this.getContentPane().setLayout(null);
        panel1.setLayout(null);
        label2.setHorizontalAlignment(SwingConstants.CENTER);
        label2.setHorizontalTextPosition(SwingConstants.CENTER);
        label2.setText("Archimedes Metadata Rules Analyzer, v1.7");
        label2.setBounds(new Rectangle(192, 20, 268, 17));
        label3.setHorizontalAlignment(SwingConstants.CENTER);
        label3.setText("Copyright 2002. 2003 Versata Inc.");
        label5.setText("& Band Software Design, LLC");
        label5.setHorizontalAlignment(SwingConstants.CENTER);
        label3.setBounds(new Rectangle(199, 71, 256, 17));
        label5.setBounds(new Rectangle(199, 86, 256, 17));
        button1.setText("Ok");
        button1.setBounds(new Rectangle(201, 292, 49, 27));
        button1.addActionListener(this);
        panel1.setBackground(Color.white);
        panel1.setBounds(new Rectangle(-2, 0, 480, 336));
        jButton1.setBackground(Color.white);
        jButton1.setBorderPainted(false);
        jButton1.setFocusPainted(false);
        jButton1.setIcon(new ImageIcon(MainWindow_AboutBox.class.getResource("/images/SmallVersata.gif")));
        jButton1.setPressedIcon(new ImageIcon(MainWindow_AboutBox.class.getResource("/images/SmallVersataSweat.gif")));
        jButton1.setBounds(new Rectangle(9, 9, 167, 64));
        label4.setBounds(new Rectangle(186, 44, 274, 17));
        label4.setText("Last update : 2003/03/19");
        label4.setHorizontalAlignment(SwingConstants.CENTER);
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setLineWrap(true);
        jTextArea1.setText("The software is provided as is, without any express or implied warranty " +
                "by Versata, Inc and Band Software Design, LLC. or its suppliers, including, but not limited to, " +
                "the implied warranties of merchantability and fitness for a particular " +
                "purpose.  Versata, Inc and Band Software Design, LLC. nor its suppliers promise that the " +
                "software will be error free or will operate without interruption. " +
                "In no event shall Versata, Inc and Band Software Design, LLC. be liable for direct, indirect, special, " +
                "incidental or consequential damages of any kind including, without " +
                "limitation, lost profits.");
        jTextArea1.setEditable(false);
        jTextArea1.setBounds(new Rectangle(17, 116, 452, 157));
        this.getContentPane().add(panel1, null);
        panel1.add(jButton1, null);
        panel1.add(button1, null);
        panel1.add(label2, null);
        panel1.add(label4, null);
        panel1.add(label3, null);
        panel1.add(label5, null);
        panel1.add(jTextArea1, null);
    }

    public Dimension getPreferredSize() {
        return new Dimension(480, 370);
    }

    /**
     * Overridden so we can exit when window is closed
     */
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            cancel();
        }
        super.processWindowEvent(e);
    }

    /**
     * Close the dialog
     */
    void cancel() {
        dispose();
    }

    /**
     * Close the dialog on a button event
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1) {
            cancel();
        }
    }
}