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
**  DataObject*/

//{{COMPONENT_RULES_CLASS_DECL
public class DataObjectImpl extends  DataObjectBaseImpl


//END_COMPONENT_RULES_CLASS_DECL}}
{
	//{{COMP_CLASS_CTOR
	public DataObjectImpl (){
		super();
	}
	
	public DataObjectImpl(Session session, boolean makeDefaults)
	{
		super(session, makeDefaults);
	
	
	
	
	//END_COMP_CLASS_CTOR}}

	}


	//{{COMPONENT_RULES
		public static DataObjectImpl getNewObject(Session session, boolean makeDefaults)
		{
			return new DataObjectImpl(session, makeDefaults);
		}	
	
	//END_COMPONENT_RULES}}

	//{{EVENT_CODE
	
	//END_EVENT_CODE}}

	public void addListeners() {
		//{{EVENT_ADD_LISTENERS
		
		//END_EVENT_ADD_LISTENERS}}
	}
	public void compareAttrs(StringBuffer sb, DataObjectImpl dataObj2){
		Enumeration e = getAttributes();
		AttributeImpl attr;
		AttributeImpl attr2;
		while(e.hasMoreElements()){
			attr = (AttributeImpl) e.nextElement();	
			attr2 = findAttribute(dataObj2, attr);
			if(attr2 == null){
				sb.append("Attribute Added "+ attr.getAttrName());
				sb.append("\n");	
			}
		}								
	}
	public AttributeImpl findAttribute(DataObjectImpl dataObj, AttributeImpl attr){
		Enumeration e = dataObj.getAttributes();
		AttributeImpl attr2;
		while(e.hasMoreElements()){
			attr2 = (AttributeImpl) e.nextElement();	
			if(attr.getAttrName().equals(attr2.getAttrName())){
				return attr2;	
			}
		}	
		return null;
	}
}

