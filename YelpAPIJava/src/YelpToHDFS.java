import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class YelpToHDFS {

  OAuthService service;
  Token accessToken;

  public YelpToHDFS(String consumerKey, String consumerSecret, String token, String tokenSecret) {
    this.service = new ServiceBuilder().provider(YelpAPI.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
    this.accessToken = new Token(token, tokenSecret);
  }
  
  private String search(String category, String location, int offset) {
    OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
    request.addQuerystringParameter("term", category);
    request.addQuerystringParameter("location", location);
    request.addQuerystringParameter("offset", Integer.toString(offset));
    this.service.signRequest(this.accessToken, request);
    Response response = request.send();
    return response.getBody();
  }
  
  public String search(String category, String location) {
    int numResult = 0;
    int offset = 0;
    String result = search(category, location, 0);
    
    String mergedResult = result;
    JSONParser parser = new JSONParser();
    try {
      org.json.simple.JSONObject response = (org.json.simple.JSONObject) parser.parse(result);
      String total = response.get("total").toString();
      numResult = Integer.parseInt(total);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    //test
    //numResult = 200;
    while (numResult > 0) {
      result = search(category, location, offset);
      mergedResult = mergeJSONString(mergedResult, result);
      offset += 20;
      numResult -=20;
      //System.out.println(numResult);
    }
    return mergedResult;
  }
  
  private String mergeJSONString(String json1, String json2) {
    ObjectMapper mapper = new ObjectMapper();
    try {
    Map<String, Object> map1 = mapper.readValue(json1, Map.class);
    Map<String, Object> map2 = mapper.readValue(json2, Map.class);
    Map<String, Object> merged = new HashMap<String, Object>(map2);
    merged.putAll(map1);
    return mapper.writeValueAsString(merged);
    } catch (Exception e) {
      //ignored
    }
    return null;
  }
  
  private static void JSONToCSV(String jsonString) {
    JSONObject output;
    try {
        output = new JSONObject(jsonString);
        org.json.JSONArray docs = output.getJSONArray("businesses");
        File file=new File("output.csv");
        String csv = CDL.toString(docs);
        FileUtils.writeStringToFile(file, csv);
    } catch (JSONException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }        
  }

  public static void main(String[] args) {
    String consumerKey = "NucDGu-AyqiKD-J-G5hGWg";
    String consumerSecret = "YrVwj4Zhbi1Ii85RTihPmPryVx0";
    String token = "deKNsTgsherYjI7Ze1zC5XT5UxzfmOhG";
    String tokenSecret = "xJCWmNwLQRbkuTsjvKWMHgZHTp4";

    YelpToHDFS yelp = new YelpToHDFS(consumerKey, consumerSecret, token, tokenSecret);
    String response = yelp.search("restaurant", "New York");
    JSONToCSV(response);
    JSONObject result = new JSONObject(response);
//    try (Writer writer = new FileWriter("Output.json")) {
//      Gson gson = new GsonBuilder().create();
//      gson.toJson(result, writer);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
    
  }
}