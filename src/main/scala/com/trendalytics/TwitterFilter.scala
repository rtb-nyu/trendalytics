package com.trendalytics

import java.io._
import org.apache.http.client._
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json4s._
import org.json4s.native.JsonMethods._
import twitter4j._
// import java.time.LocalDateTime
import java.util._
import java.text._

/**
 * @author ${user.name}
 */
object FilterUtil {
  val config = new twitter4j.conf.ConfigurationBuilder()
  .setOAuthConsumerKey("OycBERb5BcBhDj9b0HkUrkCM2")
  .setOAuthConsumerSecret("Cg7tyvJF90zLq9MmOk2gvzLF0SSbIA8yF26ITNF9cgulSejBFy")
  .setOAuthAccessToken("278043834-NTTee6inRqUg2cBvEl1NpazpsskDHQZ8N8cUww8j")
  .setOAuthAccessTokenSecret("z4LpUMLYvuEKDe5H23LBlPQbdF7aaz3H6kerl56CjR8UP")
  .setUseSSL(true)
  .build
}

class TwitterFilter {
  def fetch() {

    val twitter = new TwitterFactory(FilterUtil.config).getInstance
    
    val long = -74.1687;
    val lat = 40.5722;
    val res = 50;
    val resUnit = "mi";

    val query = new Query("apple")

    // query.setGeoCode(new GeoLocation(lat, lon), res, resUnit);
    val result = twitter.search(query)

    println("Tweetes Found :" + result.getTweets.size)

    val statuses = result.getTweets()

    val it = statuses.iterator()

    val dNow = new Date()
    val ft = new SimpleDateFormat ("yyyy.MM.dd_hh:mm:ss")

    val currentTime = ft.format(dNow).toString().replaceAll("[-+.^:,]","");

    while (it.hasNext()) {
      val status = it.next()

      val id = status.getId
      val userName = status.getUser.getName
      val numFriends = status.getUser.getFriendsCount.toString()
      val datetime = status.getCreatedAt.toString()
      val location  = status.getGeoLocation()
      val tweets = status.getText
      val numReTweet = status.getRetweetCount.toString()

      val pw = new FileWriter("trendalytics_data/tweets/" + currentTime + ".txt", true)
      val delimiter = "\t"
      val toPrint = id + delimiter + userName + delimiter + numFriends + delimiter + datetime + delimiter + tweets + delimiter + numReTweet

      pw.write(toPrint + "\n")

      pw.close
    }

  }
}
