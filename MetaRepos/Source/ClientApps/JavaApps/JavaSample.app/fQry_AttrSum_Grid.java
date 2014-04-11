//{{IMPORT_STMTS
package JavaSample;
import java.awt.*;
import java.util.*;
import java.applet.*;
import versata.vfc.*;
import versata.common.*;
import versata.common.vstrace.*;
import javax.swing.*;
import java.awt.event.*;

//END_IMPORT_STMTS}}

/*
**  fQry_AttrSum_Grid*/

//{{FORM_CLASS_DECL
public class fQry_AttrSum_Grid extends fQry_AttrSum_GridBase
//END_FORM_CLASS_DECL}}
{
	
	//{{FORM_CLASS_CTOR
	public fQry_AttrSum_Grid () throws VSException, java.beans.PropertyVetoException {
		super();
		afterFormInitialize();
		addListeners();
	//END_FORM_CLASS_CTOR}}

	}

	public void paintComponent(Graphics g) {
        	super.paintComponent(g); //paint background
    	}
	

	public void afterFormInitialize() {
		super.afterFormInitialize();
		//Write code here for initializing your own control
		//or creating new control.

	}

	//{{EVENT_CODE
	
	//END_EVENT_CODE}}

	public void addListeners() {
	//{{EVENT_ADD_LISTENERS
	
	//END_EVENT_ADD_LISTENERS}}
	}

	//{{EVENT_ADAPTER_CODE
	
	//END_EVENT_ADAPTER_CODE}}
}

