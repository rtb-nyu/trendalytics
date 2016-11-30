package com.trendalytics

import java.io._
import org.apache.http.client._
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json4s._
import org.json4s.native.JsonMethods._

import org.scribe.builder.api.DefaultApi10a;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * @author Wenhao Lu
 */

class YelpStreamer{  

    val consumerKey = "NucDGu-AyqiKD-J-G5hGWg";
    val consumerSecret = "YrVwj4Zhbi1Ii85RTihPmPryVx0";
    val token = "deKNsTgsherYjI7Ze1zC5XT5UxzfmOhG";
    val tokenSecret = "xJCWmNwLQRbkuTsjvKWMHgZHTp4";  
    val service = new ServiceBuilder()
                                                .provider(classOf[YelpAPI])
                                                .apiKey(consumerKey)
                                                .apiSecret(consumerSecret)
                                                .build();
    val accessToken = new Token(token, tokenSecret);

    class YelpAPI extends DefaultApi10a {
        override def getAccessTokenEndpoint() : String = null
        override def getAuthorizationUrl(arg0 : Token) : String = null
        override def getRequestTokenEndpoint() : String = null
    }

    def search(category : String, location : String, offset : int) : String = {
        val request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", category);
        request.addQuerystringParameter("location", location);
        request.addQuerystringParameter("offset", offset.toString);
        service.signRequest(accessToken, request);
        val response = request.send();
        response.getBody();
    }

    def search(category : String, location : String) : String = {
        val numResult = 0;
        val offset = 0;
        val result = search(category, location, 0);
        val obj = new JsonParser().parse(result).getAsJsonObject();
        val mergedResult = obj.get("businesses").getAsJsonArray().toString;
        val parser = new JSONParser();
        try {
          val response = parser.parse(result).asInstanceOf[org.json.simple.JSONObject];
          val total = response.get("total").toString();
          numResult = total.toInt;
        } catch {
         case pe: ParseException => pe.printStackTrace();
        }
        //test
        numResult = 20;
        while (numResult > 0) {
          result = search(category, location, offset);
          mergedResult = mergeJSONString(mergedResult, result);
          offset += 20;
          numResult -=20;
          //debug
          println(numResult);
        }
        mergedResult;
    }

    def mergeJSONString(json1 : String, json2 : String) : String {
        val gparser = new JsonParser();
        val bus1 = gparser.parse(json1).getAsJsonArray();
        val o2 = gparser.parse(json2).getAsJsonObject();
        val bus2 = o2.get("businesses").getAsJsonArray();
        val s1 = bus1.toString;
        val s2 = bus2.toString;
        val merged = s1.substring(0, s1.length()-1) + "," + s2.substring(1);
        val replaced = merged.replace("location\":{\"", "").replace("}},{\"is_claimed", "},{\"is_claimed")
                .replace("\"coordinate\":{", "").replace("},\"state_code", ",\"state_code");
        val result = replaced.substring(0, replaced.length() - 2) + "]";
    }

    def JSONToCSV(jsonString : String) {
        try {
            val docs = new org.json.JSONArray(jsonString);
            val file=new File("output.csv");
            val csv = CDL.toString(docs);
            FileUtils.writeStringToFile(file, csv);
        } catch {
         case ioe: IOException => ioe.printStackTrace();
         case jsone: JSONException => jsone.printStackTrace();
        }
    }

    def fetch() {
      	println("hello yelp")
        val response = search("restaurant", "New York City");
        JSONToCSV(response);

        println(response);
    }
}