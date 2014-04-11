/**
 *
 */

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
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * A very simple, self-contained demo REST client that exercises an Espresso Logic API.<br><br>
 * <p/>
 * You will need the following jars:
 * <ol>
 * <li>httpclient-4.x.x.jar</li>
 * <li>httpcore-4.x.x.jar</li>
 * <li>commons-logging-1.1.1.jar</li>
 * <li>jackson-core-2.x.x.jar</li>
 * <li>jackson-databind-2.x.x.jar</li>
 * <li>jackson-annotations-2.x.x.jar</li>
 * </ol>
 * ObjectMapper objectMapper = new ObjectMapper();
 * //convert json string to object
 * Employee emp = objectMapper.readValue(jsonData, Employee.class);
 *
 * @see <a href="http://docs.espressologic.com/docs/rest-apis/using-ssl">Using https</a>
 */
public class RESTClientServices {

    private final static HttpClient client = new DefaultHttpClient();
    private final static JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

    protected static String LOCAL_BASE_URL = "http://bandrepo.my.espressologic.com/";   // for internal testing (ignore)
    protected static String SERVER = "http://bandrepo.my.espressologic.com/";
    protected static String ACCOUNT = "rest/bandrepo/";  // TODO edit these for your account
    protected static String PROJECT = "demo/";   // the Url Name of the pre-installed Espresso Logic demo project
    protected static String API_VERSION = "v1/";
    static String BASE_URL = SERVER + ACCOUNT + PROJECT + API_VERSION; // LOCAL_BASE_URL;
    // Result: https://eval.espressologic.com/rest/valJune18/demo/v1/
    protected static String MY_API_KEY = "demo_full";  // TODO edit for your project
    protected static String API_KEY = "Espresso " + MY_API_KEY + ":123";
    private final static Header API_KEY_HEADER = new BasicHeader("Authorization", API_KEY);

    private static String reposName = "MetaRepos";//change this your repository name
    private static String reposVersion = "4"; //only work on a specific version
    private static String filter = "?filter=RepositoryVersion=" + reposVersion;

    public static void main(String[] args) throws Exception {

        // {@link <a href="http://docs.espressologic.com/docs/rest-apis/using-ssl">more info</>
        System.setProperty("javax.net.ssl.trustStore", "src/main/resources/keystore");

        // Retrieve all customer objects
        System.out.println("\n\n------ Retrieve all customers using BASE_URL: " + BASE_URL);
        JsonNode tables = get("@tables");
        Iterator<JsonNode> listOfTables = tables.iterator();
        //System.out.println("   balance:" + eachCustomer.get("balance").asText());
        while ( listOfTables.hasNext()) {
            JsonNode eachTable = listOfTables.next();
            String name = eachTable.get("name").asText();
            System.out.println("name:" + name);
            if ("DataObject".equals(name)) {
                JsonNode aTable = get("@tables/" + name);
                Iterator<JsonNode> tableContent = aTable.iterator();
                while( tableContent.hasNext()) {
                    JsonNode meta = tableContent.next();
                    System.out.println(meta.get("columns").iterator().next());
                    //{"name":"RepositoryName","type":"VARCHAR","size":50,"computed":false,"is_editable":true
                    for (JsonNode a : meta) {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode node1 = a.path("columns");
                        JsonParser jp = new JsonFactory().createJsonParser(node1 != null ? node1.toString() : a.toString()); // construct using JsonFactory (can get one using ObjectMapper.getJsonFactory)
                        JsonNode node = mapper.readTree(jp);
                        System.out.println(node);//c.get("name") + " " + c.get("type"));
                    }
                }
                System.exit(1);
            }
        }

    }
    public static String transformFormula(String input){
        //If ( Total_Line_Cnt  = 0) Then	$value = 0Else	$value =  (Gen_Lines_Cnt  /  Total_Line_Cnt ) * 100End If
        //Becomes
        // if (row.Total_Line_Cnt == 0) then return 0 else return (row.Gen_Line_Cnt / row.Total_Line_cnt) * 100 END IF
        return "";//output
    }

    public static String transformCount(String childObject,String where){

        return "";
    }

    public static String transformSum(String formula){
        return "";
    }

    public static void methodTest() throws Exception {
        // Insert a brand new customer object
        JsonNode customerList = get("Cutomer");
        System.out.println("\n\n------ Insert a new customer");
        ObjectNode newCustomerinfo = new ObjectNode(nodeFactory);
        newCustomerinfo.put("name", "NewCustomer");  // simple name (spaces in url need replacement)
        newCustomerinfo.put("credit_limit", 0);
        JsonNode postResult = post("customer", newCustomerinfo);
        if (postResult.get("statusCode").asInt() != 201)
            throw new RuntimeException("Unable to insert: " + postResult);
        JsonNode postTxSummary = postResult.get("txsummary");
        for (JsonNode node : postTxSummary) {
            System.out.println("Transaction summary for post: " + node);
        }
        ObjectNode insertedNode = (ObjectNode) postTxSummary.get(0);

        // Now update the new customer object
        System.out.println("\n\n------ Update the new customer");
        insertedNode.put("credit_limit", 1);
        JsonNode putResult = put("customer", insertedNode);
        if (putResult.get("statusCode").asInt() != 200)
            throw new RuntimeException("Unable to update: " + putResult);
        JsonNode putTxSummary = putResult.get("txsummary");
        for (JsonNode node : putTxSummary) {
            System.out.println("Transaction summary for put: " + node);
        }
        ObjectNode updatedNode = (ObjectNode) putTxSummary.get(0);

        // And delete the object we just inserted
        System.out.println("\n\n------ Delete the new customer");
        JsonNode deleteResult = delete(updatedNode);
        if (deleteResult.get("statusCode").asInt() != 200)
            throw new RuntimeException("Unable to delete: " + deleteResult);
        JsonNode deleteTxSummary = deleteResult.get("txsummary");
        for (JsonNode node : deleteTxSummary) {
            System.out.println("Transaction summary for delete: " + node);
        }

        System.out.println("\n------ Test is complete");
    }

    private static JsonNode get(String resource) throws Exception {
        HttpGet get = new HttpGet(BASE_URL + resource);
        get.addHeader(API_KEY_HEADER);
        HttpResponse response = client.execute(get);
        return parseResponse(response);
    }

    private static JsonNode put(String resource, JsonNode object) throws Exception {
        HttpPut put = new HttpPut(BASE_URL + resource + filter);
        put.addHeader(API_KEY_HEADER);
        StringEntity entity = new StringEntity(object.toString());
        entity.setContentType("application/json");
        put.setEntity(entity);
        HttpResponse response = client.execute(put);
        return parseResponse(response);
    }

    private static JsonNode post(String resource, JsonNode object) throws Exception {
        HttpPost post = new HttpPost(BASE_URL + resource);
        post.addHeader(API_KEY_HEADER);
        StringEntity entity = new StringEntity(object.toString());
        entity.setContentType("application/json");
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        return parseResponse(response);
    }

    private static JsonNode delete(JsonNode object) throws Exception {
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
    private static JsonNode parseResponse(HttpResponse response) throws Exception {
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
}
