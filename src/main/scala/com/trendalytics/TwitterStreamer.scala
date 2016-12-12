package com.trendalytics

import java.io._
import scala.io.Source
import util.control.Breaks._
import org.apache.http.client._
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json4s._
import org.json4s.native.JsonMethods._
import twitter4j._

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.clustering.KMeans

/**
 * @author ${user.name}
 */
object StreamerUtil {
  val config = new twitter4j.conf.ConfigurationBuilder()
  .setOAuthConsumerKey("ADEo7Xxrq7Gz0uF0rdkc4pdEY")
  .setOAuthConsumerSecret("yDvWWeEP4WLAgkexEOD79Xp9dOT16pjX2phmQ0t2BR2ihbff8d")
  .setOAuthAccessToken("804203820532252672-2xp6JVWFepbN6lmYnxPLXUwLUUTEgQO")
  .setOAuthAccessTokenSecret("tcXQpznt4za1RqJa4mYNB57lFDIcCwUM8lCzhq7fCOtAf")
  .setUseSSL(true)
  .build

  val modelFile = "trendalytics_data/tweets_processed/KMeansModel"
  val sc = new SparkContext(new SparkConf().setAppName("Trendalytics"))
  val model = KMeansModel.load(sc, modelFile)

  val numFeatures = 1000
  val tf = new HashingTF(numFeatures)

  def featurize(s: String): Vector = {
    tf.transform(s.sliding(2).toSeq)
  }

  def storeTweets (key_name: String, rate: String) = {
    val twitter = new TwitterFactory(FilterUtil.config).getInstance

    val query = new Query(key_name)

    query.setCount(20);  // can fetch more tweets each time
                // maximum to 100
    query.setLang("en");    // restrict to English tweets
    val result = twitter.search(query)

    val statuses = result.getTweets()

    val it = statuses.iterator()

    while (it.hasNext()) {
      breakable {
        val status = it.next()

        val tweets = status.getText.replaceAll("[\n\t]",". ").replaceAll("http[s]*://[a-zA-Z0-9.?/&=:]*", "").replaceAll("#", "").replaceAll("@", "")
        if (tweets matches "^RT .*") break
        if (tweets.length() < 100) break
        val datetime = status.getCreatedAt.toString()

        println(key_name.toUpperCase() + " rated as: " + rate)
        println(tweets + "\n" + datetime)
        println("### Sentiment is: ", SentimentAnalysis.findSentiment(tweets))
        println("It's in cluster: " + model.predict(featurize(tweets)).toString + "\n")

      }
    } 
  }

  def filterKeyTweets (filename: String, idx: Int, rate: Int) = {
    try {
        val bufferedSource = Source.fromFile(filename)
        for (line <- bufferedSource.getLines) {
            val lines = line.toLowerCase.split('\t')
            storeTweets(lines(idx), lines(rate));
        }
        bufferedSource.close
    } catch {
        case e: FileNotFoundException => println("Can't read from file " + filename)
    }
  }
}

class TwitterStreamer {
  def fetch() {

    println("------- Begin to search for MOVIES -------");
    
    StreamerUtil.filterKeyTweets("trendalytics_data/movies.txt", 0, 5);
    println("Finished searching for movies.\n");

    println("------- Begin to search for RESTAURANTS -------");
    StreamerUtil.filterKeyTweets("trendalytics_data/yelp20LinesClean.txt", 1 ,0);
    println("Finished searching for restaurants.");    

  }
}