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
 * @author ${user.name}
 */

class YelpAPI extends DefaultApi10a {

  override def getAccessTokenEndpoint() : String = null

  override def getAuthorizationUrl(arg0 : Token) : String = null

  override def getRequestTokenEndpoint() : String = null

}

class YelpStreamer{

	def search() : String = {
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

    val request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
    
    val numResult = 0;
    val offset = 0;
    val location = "New York";
    val term = "restaurant"

    request.addQuerystringParameter("term", term);
    request.addQuerystringParameter("location", location);
    request.addQuerystringParameter("offset", offset.toString);
    
    service.signRequest(accessToken, request);
    
    val response = request.send();

    response.getBody()
	}

  def fetch() {
  	println("hello yelp")

  	
    val mergedResult = search();

    println(mergedResult)

    // val parser = new JSONParser();
    
    // try {
    //   val response = (org.json.simple.JSONObject) parser.parse(response);
    //   val total = response.get("total").toString();
    //   numResult = Integer.parseInt(total);
    // } 
    // catch (ParseException e) {
    //   e.printStackTrace();
    // }
    
    // while (numResult > 0) {
    //   result = search(category, location, offset);
    //   mergedResult = mergeJSONString(mergedResult, result);
    //   offset += 20;
    //   numResult -=20;
    //   //System.out.println(numResult);
    // }

    

  }

}