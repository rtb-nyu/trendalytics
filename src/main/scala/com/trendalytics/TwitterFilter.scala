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

  def simpleStatusListener = new StatusListener() {
    def onStatus(status: Status) { 
      
      
      val id = status.getId
      val userName = status.getUser.getName
      val numFriends = status.getUser.getFriendsCount.toString()
      val datetime = status.getCreatedAt.toString()
      val location  = status.getGeoLocation()
      val tweets = status.getText
      val numReTweet = status.getRetweetCount.toString()


      if(location != null){

        val lat = location.getLatitude().toString
        val long = location.getLongitude().toString

        val pw = new FileWriter("nyTweets.txt", true)
        val delimiter = "\t"
        val toPrint = id + delimiter + userName + delimiter + numFriends + delimiter + datetime + delimiter + lat + 
                      delimiter + long + delimiter + tweets + delimiter + numReTweet
        // println(toPrint)
        pw.write(toPrint + "\n")

        pw.close

      }
        
    }

    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
    def onException(ex: Exception) { ex.printStackTrace }
    def onScrubGeo(arg0: Long, arg1: Long) {}
    def onStallWarning(warning: StallWarning) {}
  }
}

class TwitterFilter {
  def fetch() {

    val twitter = new TwitterFactory(FilterUtil.config).getInstance
    
    val lon = -74.1687;
    val lat = 40.5722;
    val res = 50;
    val resUnit = "mi";

    val query = new Query("apple")

    // query.setGeoCode(new GeoLocation(lat, lon), res, resUnit);
    val result = twitter.search(query)

    println("Tweetes Found :" + result.getTweets.size)

    val statuses = result.getTweets()

    val it = statuses.iterator()

    while (it.hasNext()) {
      val status = it.next()
      println(status.getUser().getName() + ":" +
              status.getText());
    }

  }
}
