package com.bandsoftware.persistence;


import com.bandsoftware.data.BSDDataObject;
import com.bandsoftware.data.EspressoRuleBean;
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
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;

/**
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
    protected static String RULE_HEADER_API_KEY = "Espresso AkYraatF51TR:123";
    private final static Header API_KEY_HEADER = new BasicHeader("Authorization", API_KEY);
    private final static Header RULE_API_KEY_HEADER = new BasicHeader("Authorization", RULE_HEADER_API_KEY);
    private static Logger log = Logger.getLogger(RESTPersistenceManager.class.getName());
    private static String ADMIN_API_KEY;
    //http://bandrepo.my.espressologic.com/rest/abl/admin/v2/AllRules/1008
    private static String  ADMIN_VERSION  = "v2/";
    private static String ADMINPW = "AkYraatF51TR";
    private static String ADMINUSER = "admin";
    private static String ADMIN_RULE_BASE_URL = SERVER + "rest/abl/admin/" +  ADMIN_VERSION ;
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
            if(fis != null){
                properties.load(fis);
                LOCAL_BASE_URL = (String) properties.get("LOCAL_BASE_URL");
                SERVER = (String) properties.get("REST_SERVER");
                ACCOUNT =(String) properties.get("ACCOUNT");
                PROJECT = (String) properties.get("PROJECT");
                MY_API_KEY =(String) properties.get("MY_API_KEY");
                API_KEY = (String) properties.get("API_KEY");
                API_VERSION = (String) properties.get("API_VERSION");
                ADMIN_VERSION = (String) properties.get("ADMIN_VERSION");
                ADMINUSER = (String) properties.get("ADMINUSER");
                ADMINPW = (String)properties.get("ADMINPW");
            }

        } catch (Exception ex) {
            log.error(ex.getMessage(),ex);
        } finally {
            if(fis != null) {
                try{
                    fis.close();
                } catch (Exception e){
                    ;//tcb
                }
            }
        }
    }

     public static String insertData(String name, HashMap<String,BSDField> map) throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         ObjectNode newNode = new ObjectNode(nodeFactory);
         String json =  mapper.writeValueAsString(map);
         JsonNode jnode = mapper.readTree(json);
         log.debug("InsertData >> "+ name+ ": json "+json);
         JsonNode result = post(name, jnode);
         return null;
     }

    public static String insertUpdateData(String name, HashMap<String,BSDField> map, HashMap<String,BSDField> fieldMap) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String json =  mapper.writeValueAsString(map);
        //need to do a get first to see if it exists and then post as update or insert
        log.debug(name +" insertUpdateData :" + json);
        return json;
    }

    public static BSDDataObject getBusinessObjectRow(String queryName, String where) throws Exception{
        //do a JSON GET
        String resource = queryName + "/" + where;
        log.debug("getBusinessObjectRow :" + resource);
        return null;//get(resource);

    }

    public static HashMap findAllBusinessObjects(String queryName, String where) throws Exception{
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
        return parseResponse(response);
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
        return parseResponse(response);
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
        return parseResponse(response);
    }

    protected static JsonNode delete(JsonNode object) throws Exception {
        String objectUrl = object.get("@metadata").get("href").asText();
        String checksum = object.get("@metadata").get("checksum").asText();
        HttpDelete delete = new HttpDelete(objectUrl + "?checksum=" + checksum);
        delete.addHeader(API_KEY_HEADER);
        HttpResponse response = client.execute(delete);
        return parseResponse(response);
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
        if (inData.get("statusCode").asInt() != 201){
            throw new RuntimeException("REST Error Code: " + inData.get("statusCode") + " message " + inData.get("errorMessage")) ;
        }
        return inData;
    }

    private static Header getAuthentication() throws Exception {
        String uri = ADMIN_RULE_BASE_URL+ "@authentication";
        String json = "{\"username\" : \""+
                ADMINUSER +
                "\", \"password\" : \""+
                ADMINPW +
                "\"}"; // this is the admin logon
        //get()
        /* RESPONSE
        {
        "apikey": "NyX3kDQJc3CDJVA28cSVwaJc5SwMFg",
        "expiration": "9999-12-31-23:59:59.000+0000",
        "lastLoginTs": "2014-04-10T13:09:13.000+0000",
        "lastLoginIP": "71.47.148.175"

         */




        if(ADMIN_API_KEY == null){
             ADMIN_API_KEY = "Espresso NyX3kDQJc3CDJVA28cSVwaJc5SwMFg:1";
        }
        //{"username":"admin","password":"AkYraatF51TR"}
        //{"username" : "admin", "password" : "AkYraatF51TR"}
        String key = "{\"username\":\"admin\",\"password\":\"AkYraatF51TR\"}";
        Header header = new BasicHeader("Authorization", ADMIN_API_KEY);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.convertValue(json, JsonNode.class);
        StringEntity entity = new StringEntity(node.toString());
        entity.setContentType("application/json");
        HttpPost post = new HttpPost(uri);
        post.setEntity(entity);
        log.debug("postAdmin >> "+  ": json "+node);
        HttpResponse response = client.execute(post);
        JsonNode resp = parseResponse(response);
        return header;
    }
    public static JsonNode insertRule(EspressoRuleBean bean) throws Exception {
        String resource =  ADMIN_RULE_BASE_URL + "/AllRules";
        return postAdmin(resource ,"" );
    }

    private static JsonNode postAdmin(String resource,String json) throws Exception {

        HttpPost post = new HttpPost(resource);
        post.addHeader(getAuthentication());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.convertValue(json, JsonNode.class);
        StringEntity entity = new StringEntity(node.toString());
        entity.setContentType("application/json");
        post.setEntity(entity);
        log.debug("postAdmin >> "+  ": json "+node);
        HttpResponse response = client.execute(post);
        return parseResponse(response);
    }
}
