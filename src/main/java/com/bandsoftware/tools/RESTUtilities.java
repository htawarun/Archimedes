package com.bandsoftware.tools;

import com.bandsoftware.beans.EspressoRuleBean;
import com.bandsoftware.data.EspressoRuleObjectImpl;
import com.bandsoftware.data.RepositoryDO;
import com.bandsoftware.persistence.RESTPersistenceManager;
import com.versata.automationanalyzer.DataObjectAnalysis;
import com.versata.automationanalyzer.RepositoryAnalysis;
import org.codehaus.jackson.JsonNode;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by Tyler on 4/22/14.
 */
public class RESTUtilities extends RESTServicesManager {

    //o/v1/Application?filter=RepositoryName%20%3D%20'SampDB1'%20and%20RepositoryVersion%20%3D%20'1'"

    public static void main(String[] args) {
        JsonNode allTables;
        try {
            createRuleTransform();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createDDL(JsonNode t) {
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE ");
        sb.append(" @tablename@ (");
        //insert attributes
        // datatype and null/not null
        sb.append(");");
    }

    public static JsonNode getAllTables() throws Exception {
        String resource = "http://bandrepo.my.espressologic.com/rest/bandrepo/demo/v1/DataObject?filter=RepositoryName%20%3D%20'SampDB1'%20and%20RepositoryVersion%20%3D%20'1'";
        return get(resource);
    }

    public static RepositoryAnalysis setupFiles(String fullPath) {
        RepositoryAnalysis repAnalysis = null;
        File repFile = new File(fullPath);
        try {
            repAnalysis = new RepositoryAnalysis(repFile);
            repAnalysis.analyzeRepository();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return repAnalysis;
    }

    /**
     * this method demonstrates the full life cycle
     * 1) Read Repository file
     * 2) Start Persistence Manager (optional - write rules to REST server)
     * 3) transform and write rules (optional - delete all rules first)
     * 4) Print out the results
     */
    public static void createRuleTransform() {
        String fullPath = "../MetaRepos/Source/metarepos.xml";
        String VersataPath = "C:\\Versata\\VLS-5.6-WebSphere\\Samples\\SampDB1\\Source\\SampDB1.xml";

        StringBuffer sb = new StringBuffer();
        RESTPersistenceManager.testModeBypassInsert = true;
        RepositoryAnalysis repAnalysis = setupFiles(fullPath);
        assert repAnalysis != null;
        Map<String, DataObjectAnalysis> map = repAnalysis.dataObjects;

        RESTPersistenceManager rpm = new RESTPersistenceManager(RepositoryAnalysis.repos);
        try {
            //RESTPersistenceManager.testModeBypassInsert = false; // change to true to live test inserts
            rpm.StartInbound();
            removeSlelectedRules(rpm);
            RepositoryDO root = rpm.getRootRepositoryDataObject();
            List<EspressoRuleBean> beans = rpm.allEsprssoRules;
            System.out.println("Start Insert of Rules...");
            for (EspressoRuleBean bean : beans) {
                if (bean.getName() != null && bean.getRuletype_ident() > 0) {
                    writeRuleOutput(sb, bean);
                    if (!RESTPersistenceManager.testModeBypassInsert) {
                        rpm.insertRule(bean);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error " + e.getMessage());
            System.exit(0);
        } finally {
            RESTPersistenceManager.testModeBypassInsert = false;
        }
        System.out.println(sb.toString());
    }

    private static void writeRuleOutput(StringBuffer sb, EspressoRuleBean bean) {
        sb.append("Entity: [");
        sb.append(bean.getEntity_name());
        sb.append("] Attribute: [");
        sb.append(bean.getAttribute_name());
        sb.append("] Rule Type: [");
        sb.append(EspressoRuleObjectImpl.RULE_TYPES.ruleName(bean.getRuletype_ident()));
        sb.append("]");
        sb.append(" Rule Ident [");
        sb.append(bean.getRuletype_ident());
        sb.append("] \n\t ");
        sb.append(bean.getName());
        sb.append("\n");
    }

    private static void removeSlelectedRules(RESTPersistenceManager rpm) throws Exception {
        if (!RESTPersistenceManager.testModeBypassInsert) {
            rpm.getAllRules();
            System.out.println("Delete All Rules Start");
            rpm.deleteAllRules();
        }
    }
}
