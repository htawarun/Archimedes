//{{APP_IMPORT_STMTS
package MetaRepos.Project1;
import versata.vfc.html.*;
import versata.vfc.html.common.*;
import versata.common.*;
import versata.common.vstrace.*;
import versata.vls.*;
import java.util.*;
import java.math.BigDecimal;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

//END_APP_IMPORT_STMTS}}

//{{APP_CLASS_DECL
public class Project1 extends Project1Base
//END_APP_CLASS_DECL}}
{
    // Project1 Class Constructor
	//{{APP_CLASS_CTOR
	public Project1 ()
	//END_APP_CLASS_CTOR}}
    {
	    super();
    }
	
    public synchronized PLSResponse doAction(PLSRequest preq)
    {
		//{{APP_DOACTION
		   IVSTrace tr = null;  long begTime = 0;
		   if ( VSTrace.IS_ON )
		     begTime = (tr = VSTrace.get()).set(VST_SESSION_ID, getSessionIDForTracing()).set(VST_CATEGORY, VST_PLS_ACTION).set(VST_ACTION_NAME,"Project1.doAction").beg();
		
		    PLSResponse response = new PLSResponse();
			VSPage p = null;
			try
			{
				p = getPageFromList(preq.getParameter("page_id"));
				m_currentReq = preq;
				p.doAction(preq, response);
			}
			catch (Throwable ex)
			{
				m_currentReq = null;
				ex.printStackTrace();
				response.responseValue = ex.toString(); //FIXME need to improve error handling
				if (p != null)
				   response.metaContentType = p.getContentType();
				else
				   response.metaContentType = "text/html";
			}
			
		    if (  VSTrace.IS_ON ) tr.end( begTime );
		
		    return response;
		
		//END_APP_DOACTION}}
    }

	public PLSResponse start(String sessionID, VSSession session)
	{
		//{{APP_START
		PLSResponse response = super.start( sessionID, session );
		return response;
		
		//END_APP_START}}
    }


    public String getStartPageName()
    {
	    return getDefaultStartPageName();
    }

    public String getPackageName()
    {
	    return getDefaultPackageName();
    }
}