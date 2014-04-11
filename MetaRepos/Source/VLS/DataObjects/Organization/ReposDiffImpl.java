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
**  ReposDiff*/
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.FileInputStream;

//{{COMPONENT_RULES_CLASS_DECL
public class ReposDiffImpl extends  ReposDiffBaseImpl


//END_COMPONENT_RULES_CLASS_DECL}}
{
	//{{COMP_CLASS_CTOR
	public ReposDiffImpl (){
		super();
	}
	
	public ReposDiffImpl(Session session, boolean makeDefaults)
	{
		super(session, makeDefaults);
	
	
	
	
	//END_COMP_CLASS_CTOR}}

	}


	//{{COMPONENT_RULES
		public static ReposDiffImpl getNewObject(Session session, boolean makeDefaults)
		{
			return new ReposDiffImpl(session, makeDefaults);
		}	
	
	//END_COMPONENT_RULES}}

	//{{EVENT_CODE
	
	//END_EVENT_CODE}}

	public void addListeners() {
		//{{EVENT_ADD_LISTENERS
		
		//END_EVENT_ADD_LISTENERS}}
	}
	public void startDiff(){
		addTrace("StartDiff called");
		RepositoryImpl repos = getReposSource();
		RepositoryImpl repos2 = getReposSource2();
		createObjectDiff(repos,repos2);
		createQueryDiff(repos,repos2);
		createApplicationDiff(repos,repos2);
		//createMethodDiff(repos,repos2);
	}
	private void createObjectDiff(RepositoryImpl repos1, RepositoryImpl  repos2){
		//DataObjects
		ObjectDiffImpl diff = null;
		Enumeration e1 = repos1.getDataObjects();
		Enumeration e2 = repos2.getDataObjects();
		Hashtable ht1 = createHashtable(repos1.getDataObjects(),"DataObjectName");
		Hashtable ht2 = createHashtable(repos2.getDataObjects(),"DataObjectName");
		Vector v1 = createVector(e1, "DataObjectName");
		Vector v2 = createVector(e2, "DataObjectName");
		//first check by name, then compare stats
		//what is new in version2, removed in version 2 and different
		int i = 0;
		//Missing in Repos1
		for(i = 0; i< v1.size() ; i++){
			if (!v2.contains((String)v1.elementAt(i))) {
				diff = createObjectDiff("DATAOBJECT", (String) v1.elementAt(i), "none", "Object Missing");
				diff.setDiffState("DELETE");
				diff.save();
				
			}
		}
		//Added in Repos2
		for(i = 0; i < v2.size() ; i++){
			if (!v1.contains((String)v2.elementAt(i))) {
				diff = createObjectDiff("DATAOBJECT", "none", (String) v2.elementAt(i),  "Object Added");				
				diff.setDiffState("ADD");
				diff.save();
			}
		}
		//Different (method or line count)
		DataObjectImpl obj1;
		DataObjectImpl obj2;
		String objectName;
		for(i = 0; i < v2.size() ; i++){
			objectName = (String)v2.elementAt(i);
			obj1 = (DataObjectImpl) findDataObject(ht1, objectName);
			obj2 = (DataObjectImpl)findDataObject(ht2, objectName);	
			if (v1.contains(objectName)) {
				if(compareDataObject(obj1, obj2)){
					diff = createObjectDiff("DATAOBJECT", (String) v2.elementAt(i),(String) v2.elementAt(i) , "Data Object has internal line and/or method count differences");				
					diff.setTotal_1_Line_Cnt(obj1.getTotal_Line_Cnt());
					diff.setTotal_2_Line_Cnt(obj2.getTotal_Line_Cnt());
					diff.setTotal_1_Method_Cnt(obj1.getTotal_Method_Cnt());
					diff.setTotal_2_Method_Cnt(obj2.getTotal_Method_Cnt());
					diff.setDiffState("CHANGE");					
					diff.save();
				} else {
					diff = createObjectDiff("DATAOBJECT", (String) v2.elementAt(i),(String) v2.elementAt(i) , "Data Objects appears to be same");				
					diff.setDiffState("SAME");					
					diff.save();

				}
			}
		}
	}
	private Vector createVector(Enumeration e, String name){
		Vector v = new Vector();
		DataObject obj;
		while(e.hasMoreElements()){
			obj= (DataObject) e.nextElement();
			v.addElement(obj.getData(name).getString());
			trace("Add to Vector DataObject " + obj.getData(name).getString());
		}
		return v;
	}
	private ObjectDiffImpl createObjectDiff(String objType, String obj1, String obj2, String msg){
	//createObjectDiff("DataObject", "none", v2.elementAt(i),  "Object Added");
		ObjectDiffImpl diff = ObjectDiffImpl.getNewObject(getSession(), true);
		diff.setReposName1(getReposName1());
		diff.setReposName2(getReposName2());
		diff.setObjectName1(obj1);
		diff.setObjectName2(obj2);
		diff.setObjectType(objType);
		diff.setDiffComment(msg);
		return diff;
		// diff.save(); calling method must save
	}
	private boolean compareDataObject(DataObjectImpl do1, DataObjectImpl do2){
		//lookup the DataObject by name for both repository versions
		//then compare their stats (#attrs, #methods, #lines of code, etc)
		if(do1 != null && do2 != null){
		   if (do1.getTotal_Line_Cnt() != do2.getTotal_Line_Cnt())
			return true;
		   if( do1.getTotal_Method_Cnt() !=  do2.getTotal_Method_Cnt())
			return true;
		   if( do1.getAttr_Cnt() !=  do2.getAttr_Cnt())
			return true;
		}
		trace("Compare Data Objects not equal");
		if(do1 != null)
			trace("Object 1 " + do1.toString());
		if(do2 != null)
			trace(" and Object 2 " + do2.toString());
		return false;	
	}
	private DataObject findDataObject(Hashtable ht, String name){
		DataObject obj = null;
		String objName;
		trace("Search for Object named: " + name);
		if(ht.get(name) != null){
			obj = (DataObject) ht.get(name);
			return obj;
		}
		trace("Did not find object: " + name);
		return obj;
	}
	private Hashtable createHashtable(Enumeration e, String attrName){
		Hashtable ht = new Hashtable();
		DataObject obj;
		while(e.hasMoreElements()){
			obj = (DataObject) e.nextElement();
			ht.put(obj.getData(attrName).getString(), obj);		
		}	
		return ht;
			}
	//sample code to show how to create a difference report
	public void startDiffReport(){
		//Added/removed DataObjects (Constraint/Action)
		//Added Removed Attributes (Derivation/Validation)
		//Added Removed Relns 
		//Added Removed Apps/Forms
		StringBuffer sb = new StringBuffer();
		sb.append("Repository Name 1: " + getReposName1());
		sb.append("\n");
		sb.append("Repository Name 2: " + getReposName2());
		sb.append("\n");
		RepositoryImpl repos = getReposSource();
		RepositoryImpl repos2 = getReposSource2();
		repos.writeDiff(sb, repos2);	
		createFile(sb, "ReposDiff.txt");	
	}
   public void createFile(StringBuffer sb, String fileName) {
        String dir = getDirectory();
        File file = new File(dir + fileName);
        writeReport(file, sb);
   }

    private String getDirectory() {

        return "c:/temp/";

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
   // used to debug code - turn off after completed
    private void trace(String msg){
	addTrace(msg);
	System.out.println(msg);
    }	
    private void createQueryDiff(RepositoryImpl repos1, RepositoryImpl  repos2){
	//QueryObjects	
		ObjectDiffImpl diff = null;
		Enumeration e1 = repos1.getQueryObjects();
		Enumeration e2 = repos2.getQueryObjects();
		Hashtable ht1 = createHashtable(repos1.getQueryObjects(),"QueryObjectName");
		Hashtable ht2 = createHashtable(repos2.getQueryObjects(),"QueryObjectName");
		Vector v1 = createVector(e1, "QueryObjectName");
		Vector v2 = createVector(e2, "QueryObjectName");
		//first check by name, then compare stats
		//what is new in version2, removed in version 2 and different
		int i = 0;
		//Missing in Repos1
		for(i = 0; i< v1.size() ; i++){
			if (!v2.contains((String)v1.elementAt(i))) {
				diff = createObjectDiff("QRYOBJECT", (String) v1.elementAt(i), "none", "Object Missing");
				diff.setDiffState("DELETE");
				diff.save();
				
			}
		}
		//Added in Repos2
		for(i = 0; i < v2.size() ; i++){
			if (!v1.contains((String)v2.elementAt(i))) {
				diff = createObjectDiff("QRYOBJECT", "none", (String) v2.elementAt(i),  "Object Added");				
				diff.setDiffState("ADD");
				diff.save();
			}
		}
		//Different (method or line count)
		QueryObjectImpl obj1;
		QueryObjectImpl obj2;
		String objectName;
		for(i = 0; i < v2.size() ; i++){
			objectName = (String)v2.elementAt(i);
			obj1 = (QueryObjectImpl) findDataObject(ht1, objectName);
			obj2 = (QueryObjectImpl) findDataObject(ht2, objectName);	
			if (v1.contains(objectName)) {
				if(compareQueryObject(obj1, obj2)){
					diff = createObjectDiff("QRYOBJECT", (String) v2.elementAt(i),(String) v2.elementAt(i) , "Query Object has internal differences");				
					diff.setTotal_1_Line_Cnt(obj1.getTotal_Line_Cnt());
					diff.setTotal_2_Line_Cnt(obj2.getTotal_Line_Cnt());
					diff.setTotal_1_Method_Cnt(obj1.getTotal_Method_Cnt());
					diff.setTotal_2_Method_Cnt(obj2.getTotal_Method_Cnt());
					diff.setDiffState("CHANGE");					
					diff.save();
				} else {
					diff = createObjectDiff("QRYOBJECT", (String) v2.elementAt(i),(String) v2.elementAt(i) , "Query Object appears to be same");				
					diff.setDiffState("SAME");					
					diff.save();

				}
			}
		}
	}
	private void createApplicationDiff(RepositoryImpl repos1, RepositoryImpl  repos2){
	//Applications
	
		ObjectDiffImpl diff = null;
		Enumeration e1 = repos1.getApplications();
		Enumeration e2 = repos2.getApplications();		
		Hashtable ht1 = createHashtable(repos1.getApplications(),"ApplicationName");
		Hashtable ht2 = createHashtable(repos2.getApplications(),"ApplicationName");
		Vector v1 = createVector(e1, "ApplicationName");
		Vector v2 = createVector(e2, "ApplicationName");
		//first check by name, then compare stats
		//what is new in version2, removed in version 2 and different
		int i = 0;
		//Missing in Repos1
		for(i = 0; i< v1.size() ; i++){
			if (!v2.contains((String)v1.elementAt(i))) {
				diff = createObjectDiff("APPLICATION", (String) v1.elementAt(i), "none", "Object Missing");
				diff.setDiffState("DELETE");
				diff.save();
				
			}
		}
		//Added in Repos2
		for(i = 0; i < v2.size() ; i++){
			if (!v1.contains((String)v2.elementAt(i))) {
				diff = createObjectDiff("APPLICATION", "none", (String) v2.elementAt(i),  "Object Added");				
				diff.setDiffState("ADD");
				diff.save();
			}
		}
		//Different (method or line count)
		ApplicationImpl obj1;
		ApplicationImpl obj2;
		String objectName;
		for(i = 0; i < v2.size() ; i++){
			objectName = (String)v2.elementAt(i);
			obj1 = (ApplicationImpl )findDataObject(ht1, objectName);
			obj2 = (ApplicationImpl )findDataObject(ht2, objectName);	
			if (v1.contains(objectName)) {
				if(compareApplication(obj1, obj2)){
					diff = createObjectDiff("APPLICATION", (String) v2.elementAt(i),(String) v2.elementAt(i) , "Application has internal line and/or method count differences");				
					diff.setTotal_1_Line_Cnt(obj1.getTotal_Line_Cnt());
					diff.setTotal_2_Line_Cnt(obj2.getTotal_Line_Cnt());
					diff.setTotal_1_Method_Cnt(obj1.getTotal_Method_Cnt());
					diff.setTotal_2_Method_Cnt(obj2.getTotal_Method_Cnt());
					diff.setDiffState("CHANGE");					
					diff.save();					
				} else {
					diff = createObjectDiff("APPLICATION", (String) v2.elementAt(i),(String) v2.elementAt(i) , "Application appears to be same");				
					diff.setDiffState("SAME");					
					diff.save();					
				}
				createFormDiff(obj1,obj2);
			}
		}
	}
	private void createFormDiff(ApplicationImpl app1, ApplicationImpl app2){
	//Forms
		FormObjectDiffImpl diff = null;
		Enumeration e1 = app1.getForms();
		Enumeration e2 = app2.getForms();		
		Hashtable ht1 = createHashtable(app1.getForms(),"FormName");
		Hashtable ht2 = createHashtable(app2.getForms(),"FormName");
		Vector v1 = createVector(e1, "FormName");
		Vector v2 = createVector(e2, "FormName");
		//first check by name, then compare stats
		String FORMTYPE = app1.getisJava()? "JAVA_APP": "HTML_APP";
		String fname;
		String appName1 = app1.getApplicationName();
		String appName2 = app2.getApplicationName();
		//what is new in version2, removed in version 2 and different
		int i = 0;
		//Missing in App1
		for(i = 0; i< v1.size() ; i++){
			if (!v2.contains((String)v1.elementAt(i))) {
				fname = (String) v1.elementAt(i);
				diff = createFormObjectDiff(FORMTYPE, appName1, appName2 ,fname , "", "Form Missing");
				diff.setDiffState("DELETE");
				diff.save();
				
			}
		}
		//Added in Repos2
		for(i = 0; i < v2.size() ; i++){
			if (!v1.contains((String)v2.elementAt(i))) {
				fname = (String) v2.elementAt(i);
				diff = createFormObjectDiff(FORMTYPE,   appName1, appName2, "", fname , "Form Added");				
				diff.setDiffState("ADD");
				diff.save();
			}
		}
		//Different (method or line count)
		FormImpl obj1;
		FormImpl obj2;
		String objectName;
		for(i = 0; i < v2.size() ; i++){
			objectName = (String)v2.elementAt(i);
			fname = (String) v1.elementAt(i);
			obj1 = (FormImpl)findDataObject(ht1, objectName);
			obj2 = (FormImpl)findDataObject(ht2, objectName);	
			if (v1.contains(objectName)) {
				if(compareForm(obj1, obj2)){
					fname = (String) v2.elementAt(i);
					diff = createFormObjectDiff(FORMTYPE,  appName1, appName2, fname , fname, "Form has transition, record source, internal line and/or method count differences...");				
					diff.setTotal_1_Line_Cnt(obj1.getTotal_Line_Cnt());
					diff.setTotal_2_Line_Cnt(obj2.getTotal_Line_Cnt());
					diff.setTotal_1_Method_Cnt(obj1.getTotal_Method_Cnt());
					diff.setTotal_2_Method_Cnt(obj2.getTotal_Method_Cnt());
					diff.setDiffState("CHANGE");					
					diff.save();
				} else {
					fname = (String) v2.elementAt(i);
					diff = createFormObjectDiff(FORMTYPE,  appName1, appName2, fname, fname , "Forms appear to be same");				
					diff.setDiffState("SAME");					
					diff.save();					
				}
			}
		}	
	}
	private boolean compareForm(FormImpl do1, FormImpl do2){
		if(do1 != null && do2 != null){
		   if (do1.getTotal_Line_Cnt() != do2.getTotal_Line_Cnt())
			return true;
		   if( do1.getTotal_Method_Cnt() !=  do2.getTotal_Method_Cnt())
			return true;
		   if(do1.getRecordSourceCnt() != do2.getRecordSourceCnt() ||
			do1.getTransitionCount() != do2.getTransitionCount())
			return true;
		}			
		trace("Compare Form Objects not equal");
		if(do1 != null)
			trace("Form 1 " + do1.toString());
		if(do2 != null)
			trace(" and Form 2 " + do2.toString());
		return false;	

	}
	private boolean compareQueryObject(QueryObjectImpl do1, QueryObjectImpl do2){
		//lookup the DataObject by name for both repository versions
		//then compare their stats (#attrs, #methods, #lines of code, etc)
		if(do1 != null && do2 != null){
		   if (do1.getTotal_Line_Cnt() != do2.getTotal_Line_Cnt())
			return true;
		   if( do1.getTotal_Method_Cnt() !=  do2.getTotal_Method_Cnt())
			return true;
		   if( do1.getReln_Cnt() !=  do2.getReln_Cnt() ||
			 do1.getDataObject_Cnt() != do2.getDataObject_Cnt())
			return true;
		}
		trace("Compare Query Objects not equal");
		if(do1 != null)
			trace("Object 1 " + do1.toString());
		if(do2 != null)
			trace(" and Object 2 " + do2.toString());
		return false;	
	}
	private boolean compareApplication(ApplicationImpl do1, ApplicationImpl  do2){
		//lookup the Application by name for both repository versions
		//then compare their stats (#attrs, #methods, #lines of code, etc)
		if(do1 != null && do2 != null){
		   if (do1.getTotal_Line_Cnt() != do2.getTotal_Line_Cnt())
			return true;
		   if( do1.getTotal_Method_Cnt() !=  do2.getTotal_Method_Cnt())
			return true;
		   if( do1.getForm_Cnt() !=  do2.getForm_Cnt() ||
			 do1.getTransitionCnt() != do2.getTransitionCnt() )
			return true;
		}
		trace("Compare Application not equal");
		if(do1 != null)
			trace("Object 1 " + do1.toString());
		if(do2 != null)
			trace(" and Object 2 " + do2.toString());
		return false;	
	}
	private FormObjectDiffImpl createFormObjectDiff(String formType, String appName1,String appName2, String obj1, String obj2, String msg){
	//createObjectDiff("DataObject", "none", v2.elementAt(i),  "Object Added");
		FormObjectDiffImpl diff = FormObjectDiffImpl.getNewObject(getSession(), true);
		diff.setReposName1(getReposName1());
		diff.setReposName2(getReposName2());
		diff.setAppName1(appName1);
		diff.setAppName2(appName2);
		diff.setFormName1(obj1);
		diff.setFormName2(obj2);
		diff.setObjectType("APPLICATION");
		diff.setFormType(formType);
		diff.setDiffComment(msg);
		return diff;
		// diff.save(); calling method must save
	}

}

