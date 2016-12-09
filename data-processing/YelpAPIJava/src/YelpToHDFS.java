package com.trendalytics.data;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
    JsonObject obj = new JsonParser().parse(result).getAsJsonObject();
    String mergedResult = obj.get("businesses").getAsJsonArray().toString();
    JSONParser parser = new JSONParser();
    try {
      org.json.simple.JSONObject response = (org.json.simple.JSONObject) parser.parse(result);
      String total = response.get("total").toString();
      numResult = Integer.parseInt(total);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    //test
    numResult = 20;
    while (numResult > 0) {
      result = search(category, location, offset);
      mergedResult = mergeJSONString(mergedResult, result);
      offset += 20;
      numResult -=20;
      System.out.println(numResult);
    }
    return mergedResult;
  }
  
  private String mergeJSONString(String json1, String json2) {
    JsonParser gparser = new JsonParser();
    JsonArray bus1 = gparser.parse(json1).getAsJsonArray();
    JsonObject o2 = gparser.parse(json2).getAsJsonObject();
    JsonArray bus2 = o2.get("businesses").getAsJsonArray();
    String s1 = bus1.toString();
    String s2 = bus2.toString();
    String merged = s1.substring(0, s1.length()-1) + "," + s2.substring(1);
    String replaced = merged.replace("location\":{\"", "").replace("}},{\"is_claimed", "},{\"is_claimed")
            .replace("\"coordinate\":{", "").replace("},\"state_code", ",\"state_code");
    return replaced.substring(0, replaced.length() - 2) + "]";
  }
  
  private static void JSONToCSV(String jsonString) {
    try {
      org.json.JSONArray docs = new org.json.JSONArray(jsonString);
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
    String response = yelp.search("restaurant", "New York City");
    JSONToCSV(response);
    
  }
}