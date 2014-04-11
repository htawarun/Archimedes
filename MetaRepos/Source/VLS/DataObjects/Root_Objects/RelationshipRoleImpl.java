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
**  RelationshipRole*/

//{{COMPONENT_RULES_CLASS_DECL
public class RelationshipRoleImpl extends  RelationshipRoleBaseImpl


//END_COMPONENT_RULES_CLASS_DECL}}
{
	//{{COMP_CLASS_CTOR
	public RelationshipRoleImpl (){
		super();
	}
	
	public RelationshipRoleImpl(Session session, boolean makeDefaults)
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
		public static RelationshipRoleImpl getNewObject(Session session, boolean makeDefaults)
		{
			return new RelationshipRoleImpl(session, makeDefaults);
		}	
	
	//END_COMPONENT_RULES}}
	
}

