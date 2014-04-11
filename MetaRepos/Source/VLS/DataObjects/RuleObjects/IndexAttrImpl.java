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
**  IndexAttr*/

//{{COMPONENT_RULES_CLASS_DECL
public class IndexAttrImpl extends  IndexAttrBaseImpl


//END_COMPONENT_RULES_CLASS_DECL}}
{
	//{{COMP_CLASS_CTOR
	public IndexAttrImpl (){
		super();
	}
	
	public IndexAttrImpl(Session session, boolean makeDefaults)
	{
		super(session, makeDefaults);
	
	
	
	
	//END_COMP_CLASS_CTOR}}

	}


	//{{COMPONENT_RULES
		public static IndexAttrImpl getNewObject(Session session, boolean makeDefaults)
		{
			return new IndexAttrImpl(session, makeDefaults);
		}	
	
	//END_COMPONENT_RULES}}

	//{{EVENT_CODE
	
	//END_EVENT_CODE}}

	public void addListeners() {
		//{{EVENT_ADD_LISTENERS
		
		//END_EVENT_ADD_LISTENERS}}
	}
	
}

