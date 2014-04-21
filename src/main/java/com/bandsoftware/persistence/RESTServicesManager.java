package com.bandsoftware.persistence;


import com.bandsoftware.beans.EspressoRuleBean;
import com.bandsoftware.beans.LogonBean;
import com.bandsoftware.beans.RuleTypeBean;
import com.bandsoftware.data.BSDDataObject;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * REST Service Manage to get,put, and post to Espresso Logic REST service
 * change the archimedes.properties file
 * Created by Tyler on 3/31/14.
 */


public class RESTServicesManager {

    private final static HttpClient client = new DefaultHttpClient();
    private final static JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

    protected static String LOCAL_BASE_URL = "http://bandrepo.my.espressologic.com/";   // for internal testing (ignore)
    protected static String SERVER = "http://bandrepo.my.espressologic.com/";
    protected static String ACCOUNT = "rest/bandrepo/";
    protected static String PROJECT = "demo/";   // the Url Name of the pre-installed Espresso Logic demo project
    protected static String API_VERSION = "v1/";
    static String BASE_URL = SERVER + ACCOUNT + PROJECT + API_VERSION; // LOCAL_BASE_URL;
    // Result: https://eval.espressologic.com/rest/valJune18/demo/v1/
    protected static String MY_API_KEY = "demo_full";
    protected static String API_KEY = "Espresso " + MY_API_KEY + ":123";

    private final static Header API_KEY_HEADER = new BasicHeader("Authorization", API_KEY);
    private static Logger log = Logger.getLogger(RESTPersistenceManager.class.getName());
    private static String ADMIN_API_KEY = null;
    //http://bandrepo.my.espressologic.com/rest/abl/admin/v2/AllRules/1008
    private static String ADMIN_VERSION = "v2/";
    private static String ADMINPW = null;
    private static String ADMINUSER = null;
    private static String ADMIN_RULE_BASE_URL = SERVER + "rest/abl/admin/";

    public RESTServicesManager() {
        initProperties();
    }

    private void initProperties() {
        // {@link <a href="http://docs.espressologic.com/docs/rest-apis/using-ssl">more info</>
        System.setProperty("javax.net.ssl.trustStore", "src/main/resources/keystore");

        Properties properties = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("Archimedes.properties");
            if (fis != null) {
                properties.load(fis);
                LOCAL_BASE_URL = (String) properties.get("LOCAL_BASE_URL");
                SERVER = (String) properties.get("REST_SERVER");
                ACCOUNT = (String) properties.get("ACCOUNT");
                PROJECT = (String) properties.get("PROJECT");
                MY_API_KEY = (String) properties.get("MY_API_KEY");
                API_KEY = (String) properties.get("API_KEY");
                API_VERSION = (String) properties.get("API_VERSION");
                ADMIN_VERSION = (String) properties.get("ADMIN_VERSION");
                ADMINUSER = (String) properties.get("ADMINUSER");
                ADMINPW = (String) properties.get("ADMINPW");
            }

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    ;//tcb
                }
            }
        }
    }

    public static String insertData(String name, HashMap<String, BSDField> map) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        // ObjectNode newNode = new ObjectNode(nodeFactory);
        String json = mapper.writeValueAsString(map);
        JsonNode jnode = mapper.readTree(json);
        log.debug("InsertData >> " + name + ": json " + json);
        JsonNode result = post(name, jnode);
        return null;
    }

    public static String insertUpdateData(String name, HashMap<String, BSDField> map, HashMap<String, BSDField> fieldMap) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(map);
        //need to do a get first to see if it exists and then post as update or insert
        log.debug(name + " insertUpdateData :" + json);
        return json;
    }

    public static BSDDataObject getBusinessObjectRow(String queryName, String where) throws Exception {
        //do a JSON GET
        String resource = queryName + "/" + where;
        log.debug("getBusinessObjectRow :" + resource);
        return null;//get(resource);

    }

    public static HashMap findAllBusinessObjects(String queryName, String where) throws Exception {
        //do a JSON GET
        String resource = queryName + "/" + where;
        log.debug("findAllBusinessObjects :" + resource);
        return null;//get(resource);

    }

    /**
     * Send a GET request to Espresso Logic REST service
     *
     * @param resource
     * @return JsonNode response
     * @throws Exception
     */
    protected static JsonNode get(String resource) throws Exception {
        HttpGet get = new HttpGet(BASE_URL + resource);
        get.addHeader(API_KEY_HEADER);
        HttpResponse response = client.execute(get);
        return validateReturnStatusCode(parseResponse(response));
    }

    /**
     * @param resource
     * @param object
     * @return
     * @throws Exception
     */
    protected static JsonNode put(String resource, JsonNode object) throws Exception {
        HttpPut put = new HttpPut(BASE_URL + resource);
        put.addHeader(API_KEY_HEADER);
        StringEntity entity = new StringEntity(object.toString());
        entity.setContentType("application/json");
        put.setEntity(entity);
        HttpResponse response = client.execute(put);
        return validateReturnStatusCode(parseResponse(response));
    }

    /**
     * @param resource
     * @param object
     * @return
     * @throws Exception
     */
    protected static JsonNode post(String resource, JsonNode object) throws Exception {
        HttpPost post = new HttpPost(BASE_URL + resource);
        post.addHeader(API_KEY_HEADER);
        StringEntity entity = new StringEntity(object.toString());
        entity.setContentType("application/json");
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        return validateReturnStatusCode(parseResponse(response));
    }

    protected static JsonNode delete(JsonNode object) throws Exception {
        String objectUrl = object.get("@metadata").get("href").asText();
        String checksum = object.get("@metadata").get("checksum").asText();
        HttpDelete delete = new HttpDelete(objectUrl + "?checksum=" + checksum);
        delete.addHeader(API_KEY_HEADER);
        HttpResponse response = client.execute(delete);
        return validateReturnStatusCode(parseResponse(response));
    }

    /**
     * Read a Response object and parse it into a JsonNode
     */
    protected static JsonNode parseResponse(HttpResponse response) throws Exception {
        InputStreamReader inStr = new InputStreamReader(response.getEntity().getContent());
        BufferedReader rd = new BufferedReader(inStr);
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode inData = mapper.readTree(sb.toString());

        return inData;
    }

    private static JsonNode validateReturnStatusCode(JsonNode inData) {
        assert inData != null;
        //accepted and success
        if (inData.get("statusCode").asInt() != 201 && inData.get("statusCode").asInt() != 200) {
            throw new RuntimeException("REST Error Code: " + inData.get("statusCode") + " message " + inData.get("errorMessage"));
        }
        return inData;
    }

    /**
     * get a list of existing rules
     * @return
     * @throws Exception
     */
    public static JsonNode getAllRules() throws Exception {

        HttpGet get = new HttpGet(ADMIN_RULE_BASE_URL + ADMIN_VERSION + "/AllRules");
        get.addHeader(getAuthentication());
        HttpResponse response = client.execute(get);
        JsonNode jsonNode = parseResponse(response);
        // validateReturnStatusCode(jsonNode);
        return jsonNode;
    }

     public static void deleteAllRules() throws Exception{
       JsonNode jsonNodes = getAllRules();
         for(JsonNode jsonNode : jsonNodes){
             deleteRule(jsonNode);
         }
     }

    public static boolean deleteRule(JsonNode jsonNode) throws Exception{
        //bandrepo.my.espressologic.com/rest/abl/admin/v2/AllRules/1144?checksum=A:e82ef435ab767a8c
        String ident = jsonNode.get("ident").asText();
        String checksum = jsonNode.get("@metadata").get("checksum").asText();
        HttpDelete delete = new HttpDelete(ADMIN_RULE_BASE_URL + ADMIN_VERSION + "/AllRules/"+ident+"?checksum="+checksum);

        delete.addHeader(getAuthentication());
        HttpResponse response = client.execute(delete);
        validateReturnStatusCode(parseResponse(response));
        return true;
    }
    private static int getProject() throws Exception {
        //http://bandrepo.my.espressologic.com/rest/abl/admin/projects
        HttpGet get = new HttpGet(ADMIN_RULE_BASE_URL + ADMIN_VERSION + "/AllProjects");
        get.addHeader(getAuthentication());
        HttpResponse response = client.execute(get);
        JsonNode jsonNode = parseResponse(response);
        // validateReturnStatusCode(jsonNode);
        return 200;
    }

    /**
     * this method uses Espresso Logic authentication to get the internal Admin API KEY
     *
     * @return
     * @throws Exception
     */
    private static Header getAuthentication() throws Exception {
        String uri = ADMIN_RULE_BASE_URL + ADMIN_VERSION + "/@authentication";
        if(ADMIN_API_KEY == null){
            JsonNode apikey = getEspressoLogicAdminAPIKey(uri);
             ADMIN_API_KEY = "Espresso " + apikey.asText() + ":1";
        }

        Header header = new BasicHeader("Authorization", ADMIN_API_KEY);
        return header;
    }

    private static JsonNode getEspressoLogicAdminAPIKey(String uri) throws Exception {
        LogonBean logon = new LogonBean(ADMINUSER, ADMINPW);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.convertValue(logon, JsonNode.class);
        StringEntity entity = new StringEntity(jsonNode.toString());
        entity.setContentType("application/json");
        HttpPost post = new HttpPost(uri);
        post.setEntity(entity);
        log.debug("postAdmin >> " + ": json " + jsonNode);
        HttpResponse response = client.execute(post);
        JsonNode resp = parseResponse(response);
        return resp.get("apikey");
    }

    /**
     * insert a new Espresso Logic Bean using the admin account
     *
     * @param bean
     * @return
     * @throws Exception
     */
    public static boolean insertRule(EspressoRuleBean bean) throws Exception {
        String resource = ADMIN_RULE_BASE_URL + ADMIN_VERSION + "/AllRules";
        bean.setProject_ident(getProject());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.convertValue(bean, JsonNode.class);
        ((ObjectNode) node).remove("auth_Name");//not sure where this came from?
        ((ObjectNode) node).remove("ident");//not needed
        postAdmin(resource, node);
        return true;
    }

    /**
     * special post to use the Admin authentication account
     *
     * @param resource
     * @param node
     * @return
     * @throws Exception
     */
    private static void postAdmin(String resource, JsonNode node) throws Exception {
        Header header = getAuthentication();
        postBasicRule(resource, node, header);
    }

    private static boolean postBasicRule(String resource, JsonNode node, Header header) throws Exception {

        StringEntity entity = new StringEntity(createEspressoNode(node).toString());
        entity.setContentType("application/json");
        HttpPost post = new HttpPost(resource);
        post.addHeader(header);
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        validateReturnStatusCode(parseResponse(response));
        return true;
    }

    private static JsonNode createEspressoNode(JsonNode node) throws IOException {
        String entityName = node.get("entity_name").asText();
        String attrName = node.get("attribute_name").asText();
        int ruletype_ident = node.get("ruletype_ident").asInt();
        int project = node.get("project_ident").asInt();
        JsonNode ruleTypeNode = createRuleTypeBean(ruletype_ident);
        //((ObjectNode) node).put("RuleType", ruleTypeNode);
       // new EspressoBean(entityName, attrName, ruletype_ident, project != 0 ? project : 200);
        return node;
    }

    private static JsonNode createRuleTypeBean(int ruletype_ident) throws IOException {
        RuleTypeBean ruleTypeBean = new RuleTypeBean(ruletype_ident);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(ruleTypeBean);
        return mapper.readTree(json);
    }
}
