package com.trendalytics

import java.io._
import scala.io.Source
import org.apache.http.client._
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import twitter4j._
import java.util._
import java.text._

import net.liftweb.json._
/**
 * @author ${user.name}
 */


object FilterUtil {
  val config = new twitter4j.conf.ConfigurationBuilder()
  .setOAuthConsumerKey("ADEo7Xxrq7Gz0uF0rdkc4pdEY")
  .setOAuthConsumerSecret("yDvWWeEP4WLAgkexEOD79Xp9dOT16pjX2phmQ0t2BR2ihbff8d")
  .setOAuthAccessToken("804203820532252672-2xp6JVWFepbN6lmYnxPLXUwLUUTEgQO")
  .setOAuthAccessTokenSecret("tcXQpznt4za1RqJa4mYNB57lFDIcCwUM8lCzhq7fCOtAf")
  .setUseSSL(true)
  .build

  def storeTweets (key_name: String) = {
    val twitter = new TwitterFactory(FilterUtil.config).getInstance

    val query = new Query(key_name)

    val long = -74.1687;
    val lat = 40.5722;
    val res = 50;
    val resUnit = "mi";
    query.setCount(100);  // can fetch more tweets each time
                // maximum to 100
    query.setLang("en");    // restrict to English tweets
    val result = twitter.search(query)

    println('\t' + key_name + ":\t" + result.getTweets.size)

    val statuses = result.getTweets()

    val it = statuses.iterator()

    val dNow = new Date()
    val ft = new SimpleDateFormat ("MMddyyyy_hh")

    val currentTime = ft.format(dNow).toString();

    while (it.hasNext()) {
      val status = it.next()

      val id = status.getId
      val userName = status.getUser.getName
      val numFriends = status.getUser.getFriendsCount.toString()
      val datetime = status.getCreatedAt.toString()
      val location  = status.getGeoLocation()
      val tweets = status.getText.replaceAll("[\n\t]",". ").replaceAll("http[s]*://[a-zA-Z0-9.?/&=:]*", "")
      val numReTweet = status.getRetweetCount.toString()

      // write to txt file
      val pw = new FileWriter("trendalytics_data/tweets/" + currentTime + ".txt", true)
      val delimiter = "\t"
      val toPrint = key_name + delimiter + tweets + delimiter + id + delimiter + userName + delimiter + numReTweet + delimiter + numFriends + delimiter + datetime

      pw.write(toPrint + "\n")

      pw.close

    }
  }

  def filterKeyTweets (filename: String, idx: Int) = {
    try {
        val bufferedSource = Source.fromFile(filename)
        for (line <- bufferedSource.getLines) {
            val lines = line.toLowerCase.split('\t')
            storeTweets(lines(idx));
        }
        bufferedSource.close
    } catch {
        case e: FileNotFoundException => println("Can't read from file " + filename)
    }
  }
}

class TwitterFilter {
  def fetch() {    

    println("------- Begin to search for MOVIES -------");
    println("Number of Tweets found for:");
    FilterUtil.filterKeyTweets("trendalytics_data/movies.txt", 0);
    println("Finished searching for movies.\n");

    println("------- Begin to search for RESTAURANTS -------");
    println("Number of Tweets found for:");
    FilterUtil.filterKeyTweets("trendalytics_data/yelpData/20restaurant.txt", 1);
    println("Finished searching for restaurants.");

    println("------- Begin to search for BARS -------");
    println("Number of Tweets found for:");
    FilterUtil.filterKeyTweets("trendalytics_data/yelpData/20bar.txt", 1);
    println("Finished searching for bars.");

    println("------- Begin to search for SHOPPING -------");
    println("Number of Tweets found for:");
    FilterUtil.filterKeyTweets("trendalytics_data/yelpData/20shopping.txt", 1);
    println("Finished searching for shopping.");

    println("------- Begin to search for MUSEUMS -------");
    println("Number of Tweets found for:");
    FilterUtil.filterKeyTweets("trendalytics_data/yelpData/20museum.txt", 1);
    println("Finished searching for museums.");
  }
}
