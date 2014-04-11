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
**  Repository*/

//{{COMPONENT_RULES_CLASS_DECL
public class RepositoryImpl extends  RepositoryBaseImpl


//END_COMPONENT_RULES_CLASS_DECL}}
{
	//{{COMP_CLASS_CTOR
	public RepositoryImpl (){
		super();
	}
	
	public RepositoryImpl(Session session, boolean makeDefaults)
	{
		super(session, makeDefaults);
	
	
	
	
	//END_COMP_CLASS_CTOR}}

	}


	//{{COMPONENT_RULES
		public static RepositoryImpl getNewObject(Session session, boolean makeDefaults)
		{
			return new RepositoryImpl(session, makeDefaults);
		}	
	
	//END_COMPONENT_RULES}}

	//{{EVENT_CODE
	
	//END_EVENT_CODE}}

	public void addListeners() {
		//{{EVENT_ADD_LISTENERS
		
		//END_EVENT_ADD_LISTENERS}}
	}
	//we have to write to this string buffer and pass it around
	//we have the other repository to compare to 
	public void writeDiff(StringBuffer sb,RepositoryImpl repos2){
		Enumeration e = getDataObjects();
		DataObjectImpl dataObj;
		DataObjectImpl dataObj2;
		while(e.hasMoreElements()){
			dataObj = (DataObjectImpl) e.nextElement();
			//does this dataObject exist in the other repos
			sb.append("Checking DataObject " + dataObj.getDataObjectName());
			sb.append("\n");
			dataObj2 = findDataObject(dataObj);
			if(dataObj2 != null)	
				dataObj.compareAttrs(sb, dataObj2);
			else {
				sb.append("DataObject Added " + dataObj.getDataObjectName());		
				sb.append("\n");
			}
						if(repos2.dataObjectExists(dataObj))	
				//dataObj2.compareAttrs(sb, dataObj);
				;
			else {
				sb.append("DataObject Removed " + dataObj.getDataObjectName());		
				sb.append("\n");
			}

		}
		sb.append("\n");
	}
	public boolean dataObjectExists(DataObjectImpl aDataObj){
		Enumeration e = getDataObjects();
		DataObjectImpl dataObj;
		while(e.hasMoreElements()){
			dataObj = (DataObjectImpl) e.nextElement();
			if(aDataObj.getDataObjectName().equals(dataObj.getDataObjectName()))
				return true;
		}
		return false;
	
	}
	public DataObjectImpl findDataObject(DataObjectImpl aDataObj){
		Enumeration e = getDataObjects();
		DataObjectImpl dataObj;
		while(e.hasMoreElements()){
			dataObj = (DataObjectImpl) e.nextElement();
			if(aDataObj.getDataObjectName().equals(dataObj.getDataObjectName()))
				return dataObj;
		}
		return null;
	
	}

}

