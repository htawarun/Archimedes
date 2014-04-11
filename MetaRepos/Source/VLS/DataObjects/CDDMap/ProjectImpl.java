//{{COMPONENT_IMPORT_STMTS
package MetaRepos;
import java.util.Enumeration;
import java.util.Vector;
import versata.common.*;
import versata.common.vstrace.*;
import versata.vls.*;
import java.util.*;
import java.math.*;
import versata.vls.cache.*;

//END_COMPONENT_IMPORT_STMTS}}

/*
**  Project
*/

//{{COMPONENT_RULES_CLASS_DECL
public class ProjectImpl extends  ProjectBaseImpl


//END_COMPONENT_RULES_CLASS_DECL}}
{
	//{{COMP_CLASS_CTOR
	public ProjectImpl (){
		super();
	}
	
	public ProjectImpl(Session session, boolean makeDefaults)
	{
		super(session, makeDefaults);
	
	
	
	
	//END_COMP_CLASS_CTOR}}

	}

	//{{EVENT_CODE
	
	//END_EVENT_CODE}}



	public void addListeners() {
		//{{EVENT_ADD_LISTENERS
		
		//END_EVENT_ADD_LISTENERS}}
	}

	//{{COMPONENT_RULES
		public static ProjectImpl getNewObject(Session session, boolean makeDefaults)
		{
			return new ProjectImpl(session, makeDefaults);
		}	
	
	//END_COMPONENT_RULES}}
	
}

