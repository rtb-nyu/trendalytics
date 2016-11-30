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
object Util {
  val config = new twitter4j.conf.ConfigurationBuilder()
  .setOAuthConsumerKey("OycBERb5BcBhDj9b0HkUrkCM2")
  .setOAuthConsumerSecret("Cg7tyvJF90zLq9MmOk2gvzLF0SSbIA8yF26ITNF9cgulSejBFy")
  .setOAuthAccessToken("278043834-NTTee6inRqUg2cBvEl1NpazpsskDHQZ8N8cUww8j")
  .setOAuthAccessTokenSecret("z4LpUMLYvuEKDe5H23LBlPQbdF7aaz3H6kerl56CjR8UP")
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

class TwitterStreamer {
  def fetch() {
    val query = new Query("Doctor Strange" OR "Arrival" OR "Fantastic Beasts");
    val result = twitter.search(query)
    println(result.getTweets.size)
    
    val twitterStream = new TwitterStreamFactory(Util.config).getInstance
    twitterStream.addListener(Util.simpleStatusListener)

    val nycBox = Array(Array(-74.1687,40.5722),Array(-73.8062,40.9467))

    twitterStream.filter(new FilterQuery().locations(nycBox))

    // twitterStream.sample
    Thread.sleep(100000)
    twitterStream.cleanUp
    twitterStream.shutdown
  }
}
