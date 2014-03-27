package logicdemotest.get;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A very simple, self-contained demo REST client that exercises an Espresso Logic API.<br><br>
 * 
 * You will need the following jars:
 * <ol>
 * <li>httpclient-4.x.x.jar</li>
 * <li>httpcore-4.x.x.jar</li>
 * <li>commons-logging-1.1.1.jar</li>
 * <li>jackson-core-2.x.x.jar</li>
 * <li>jackson-databind-2.x.x.jar</li>
 * <li>jackson-annotations-2.x.x.jar</li>
 * </ol>
 * 
 * @see <a href="http://docs.espressologic.com/docs/rest-apis/using-ssl">Using https</a> 
 */
public class SimpleEspressoClient {

	private final static HttpClient client = new DefaultHttpClient();
	private final static JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

	protected static String LOCAL_BASE_URL = "http://localhost:8080/KahunaService/rest/abl/demo/demo1/";   // for internal testing (ignore)
	protected static String SERVER = "https://RPTyler.my.espressologic.com";
	protected static String ACCOUNT = "RPTyler_dem/";  // TODO edit these for your account
	protected static String PROJECT = "demo/";   // the Url Name of the pre-installed Espresso Logic demo project
	protected static String API_VERSION ="v1/"; 
	static String BASE_URL = SERVER + ACCOUNT + PROJECT + API_VERSION; // LOCAL_BASE_URL;
	// Result: https://eval.espressologic.com/rest/valJune18/demo/v1/
	protected static String MY_API_KEY = "69RUfvhej9KW";  // TODO edit for your project
	protected static String API_KEY = "Espresso " + MY_API_KEY + ":123";
	private final static Header API_KEY_HEADER = new BasicHeader("Authorization", API_KEY );

	public static void main(String[] args) throws Exception {
		
		// {@link <a href="http://docs.espressologic.com/docs/rest-apis/using-ssl">more info</>
		System.setProperty("javax.net.ssl.trustStore", "src/main/resources/keystore");  

		// Retrieve all customer objects 
		System.out.println("\n\n------ Retrieve all customers using BASE_URL: " + BASE_URL);  
		JsonNode customerList = get("customer");
		for (JsonNode eachCustomer : customerList) {
			System.out.println("name:" + eachCustomer.get("name").asText());
			System.out.println("   balance:" + eachCustomer.get("balance").asText());
		}

		// Insert a brand new customer object
		System.out.println("\n\n------ Insert a new customer");
		ObjectNode newCustomerinfo = new ObjectNode(nodeFactory);
		newCustomerinfo.put("name", "NewCustomer");  // simple name (spaces in url need replacement)
		newCustomerinfo.put("credit_limit", 0);
		JsonNode postResult = post("customer", newCustomerinfo);
		if (postResult.get("statusCode").intValue() != 201)
			throw new RuntimeException("Unable to insert: " + postResult);
		JsonNode postTxSummary = postResult.get("txsummary");
		for (JsonNode node : postTxSummary) {
			System.out.println("Transaction summary for post: " + node);
		}
		ObjectNode insertedNode = (ObjectNode)postTxSummary.get(0);

		// Now update the new customer object
		System.out.println("\n\n------ Update the new customer");
		insertedNode.put("credit_limit", 1);
		JsonNode putResult = put("customer", insertedNode);
		if (putResult.get("statusCode").intValue() != 200)
			throw new RuntimeException("Unable to update: " + putResult);
		JsonNode putTxSummary = putResult.get("txsummary");
		for (JsonNode node : putTxSummary) {
			System.out.println("Transaction summary for put: " + node);
		}
		ObjectNode updatedNode = (ObjectNode)putTxSummary.get(0);

		// And delete the object we just inserted
		System.out.println("\n\n------ Delete the new customer");
		JsonNode deleteResult = delete(updatedNode);
		if (deleteResult.get("statusCode").intValue() != 200)
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
		HttpPut put = new HttpPut(BASE_URL + resource);
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
