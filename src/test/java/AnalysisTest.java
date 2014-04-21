/**
 * Created by Tyler on 4/2/14.
 */

import com.bandsoftware.beans.EspressoRuleBean;
import com.bandsoftware.data.*;
import com.bandsoftware.persistence.RESTPersistenceManager;
import com.versata.automationanalyzer.DataObjectAnalysis;
import com.versata.automationanalyzer.RepositoryAnalysis;
import org.codehaus.jackson.JsonNode;
import org.junit.*;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class AnalysisTest extends RESTClientServices {

    private File repFile;
    private RepositoryAnalysis repAnalysis = null;
    private static String fullPath = "../MetaRepos/Source/metarepos.xml";
    private static String VersataPath = "C:\\Versata\\VLS-5.6-WebSphere\\Samples\\SampDB1\\Source\\SampDB1.xml";

    @BeforeClass
    public static void setUpClass() throws Exception {
        RESTPersistenceManager.testModeBypassInsert = true;
        System.out.println("\nSETUP CLASS RUNNING -");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        RESTPersistenceManager.testModeBypassInsert = false;
        System.out.println("\nTEARDOWN CLASS RUNNING ");

    }

    @Before
    public void setUp() {
        System.out.println("\nSETUP IS RUNNING -- Read Repository");

        repFile = new File(fullPath);
        try {
            repAnalysis = new RepositoryAnalysis(repFile);
            repAnalysis.analyzeRepository();
        } catch (Exception ex) {
            ex.printStackTrace();
            assertTrue("unable to read repository analysis", true);
        }
    }

    @After
    public void tearDown() {
        System.out.println("TEARDOWN IS RUNNING -- Nothing to do though");
    }

    @Test
    public void testRepository() {

        //check counts after analysis
        assertTrue("Data Objects Empty", !repAnalysis.dataObjects.isEmpty());
        System.out.println(repAnalysis.totalNumForms);
        assertTrue("HTML App Count ", repAnalysis.htmlApps.size() == 2);
        assertTrue("Java App Count ", repAnalysis.javaApps.size() == 3);
        assertTrue("DataObject Count ", repAnalysis.dataObjects.size() == 42);
        assertTrue("Query Count ", repAnalysis.queryObjects.size() == 4);
        assertTrue("Known Error Count ", repAnalysis.errors.size() == 137);
        assertTrue("Other Filer Count ", repAnalysis.otherFiles.size() == 1);
        assertTrue("Attribute Count ", repAnalysis.totalNumAttribs == 388);
        assertTrue("Non Persistent Attribute Count ", repAnalysis.totalNumNonPersistentAttribs == 33);
    }

    @Test
    public void testDataObjectContent() {

        Map<String, DataObjectAnalysis> map = repAnalysis.dataObjects;
        DataObjectAnalysis dataobja = map.get("DataObject");
        DataObjectDO dataObject = dataobja.dataObj;

        List<AttributeDO> attrList = dataObject.findAttributeList();
        assertTrue("DataObject Attribute Count ", attrList.size() == 24);
        assertTrue(attrList.get(0).getAttrName().equals("RepositoryName"));
        assertTrue(attrList.get(0).getDerivations().get(0).getDerivationType().equals("None"));
    }

    @Test
    public void createTransformTest(){
        StringBuffer sb = new StringBuffer();
        Map<String, DataObjectAnalysis> map = repAnalysis.dataObjects;

        RESTPersistenceManager rpm = new RESTPersistenceManager(RepositoryAnalysis.repos);
        try {
            rpm.StartInbound();
            rpm.getAllRules();
            System.out.println("Delete All Rules Start");
            rpm.deleteAllRules();

            RepositoryDO root = rpm.getRootRepositoryDataObject();
            List<EspressoRuleBean> beans = rpm.allEsprssoRules;
            System.out.println("Start Insert of Rules...");
            for(EspressoRuleBean bean: beans){
                if(bean.getName() != null && bean.getRuletype_ident() > 0){
                    sb.append("DO: [ ");
                    sb.append(bean.getEntity_name());
                    sb.append( "] AttrName: [");
                    sb.append(bean.getAttribute_name());
                    sb.append("] : ");
                    sb.append(bean.getName());
                    //System.out.println(sb.toString());
                    //sb = new StringBuffer();
                    rpm.insertRule(bean);
                    sb.append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(sb.toString());
    }
}
