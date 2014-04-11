//{{PROJECT_NAME
//******************************************************************************
// CommonDataDict.java:	Applet
//
//******************************************************************************
//END_PROJECT_NAME}}

//{{PROJECT_IMPORT_STMTS
package CommonDataDict;
import java.applet.*;
import java.awt.*;
import versata.vfc.*;
import versata.common.*;
import versata.common.vstrace.*;
import javax.swing.*;
import java.awt.event.*;

//END_PROJECT_IMPORT_STMTS}}


//==============================================================================
// Main Class for applet CommonDataDict
//
//==============================================================================
//{{PROJECT_CLASS_DECL
public class CommonDataDict extends CommonDataDictBase
//END_PROJECT_CLASS_DECL}}
{
	// THREAD SUPPORT:
	//		m_CommonDataDict	is the Thread object for the applet

	// As we have to do setup work in start(), a flag is need to indicate whether
	// applet is in initial process
	boolean isInitProcess = true;
	

	// CommonDataDict Class Constructor
	//--------------------------------------------------------------------------
	//{{PROJECT_CLASS_CTOR
	public CommonDataDict()
	{
		super(725,575);
	//END_PROJECT_CLASS_CTOR}}
	}

	// APPLET INFO SUPPORT:
	//		The getAppletInfo() method returns a string describing the applet's
	// author, copyright date, or miscellaneous information.
    //--------------------------------------------------------------------------
	//{{PROJECT_APPLET_INFO
	public String getAppletInfo()
	//END_PROJECT_APPLET_INFO}}
	{
		//{{PROJECT_APPLET_INFO_CODE
		String s;
		s = getAppletInfoCode();
		//END_PROJECT_APPLET_INFO_CODE}}
	
		return  s;
	}

	public static void main(String args[])
	{
		//{{PROJECT_MAIN_FUNCTION
		JFrame frame = new JFrame("CommonDataDict");
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				VSApplicationContext.getApplet().stop();
				try {	
					if(e.getSource() instanceof JFrame)  {
						VSApplicationContext.getApplet().destroy();
						((JFrame)e.getSource()).dispose();
				    }
				} catch (Exception ec) {
				    JOptionPane.showMessageDialog(null, 
						ec + "\n Exception is ignored and unsaved changes are rolled back. Continue exit ?", "Confirm Exit", JOptionPane.ERROR_MESSAGE);
				} finally {
					System.exit(0);
				}
			}
		});
		
		// Must show Frame before we size it so insets() will return valid values
		//----------------------------------------------------------------------
		frame.setVisible(true);
		frame.toFront();
		frame.setVisible(false);
		
		frame.setSize(frame.getInsets().left + frame.getInsets().right  + 725,
					frame.getInsets().top  + frame.getInsets().bottom + 575);
		
		CommonDataDict applet_CommonDataDict = new CommonDataDict();
		
		frame.getContentPane().add("Center", applet_CommonDataDict);
		
		applet_CommonDataDict.init();
		applet_CommonDataDict.start();
		frame.setVisible(true);
		
		//END_PROJECT_MAIN_FUNCTION}}
	}

	// The init() method is called by the AWT when an applet is first loaded or
	// reloaded.  Override this method to perform whatever initialization your
	// applet needs, such as initializing data structures, loading images or
	// fonts, creating frame windows, setting the layout manager, or adding UI
	// components.
   

	//If you override start method make sure call super.start()
    //--------------------------------------------------------------------------
	//{{PROJECT_INIT
	public void start()	
	//END_PROJECT_INIT}}
	{
		super.start();
		// Due to a Netscape 4.x bug, we have to move setup routine from init to here	
		if ( isInitProcess )
		{
			//{{PROJECT_SESSION_CTOR
			VSSession s = setSessionCtor();
			
			//END_PROJECT_SESSION_CTOR}}
			isInitProcess = false;
		}
	}	
	 	
	//This thread is called when applet is started. First form is shown as part of
	//this thread. 
	//{{PROJECT_RUN
	public void customRun()
	//END_PROJECT_RUN}}
	{	
		//Write your custom code here.
	}
}
