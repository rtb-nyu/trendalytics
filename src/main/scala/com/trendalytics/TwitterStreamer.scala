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
object Util {
  val config = new twitter4j.conf.ConfigurationBuilder()
  .setOAuthConsumerKey("ADEo7Xxrq7Gz0uF0rdkc4pdEY")
  .setOAuthConsumerSecret("yDvWWeEP4WLAgkexEOD79Xp9dOT16pjX2phmQ0t2BR2ihbff8d")
  .setOAuthAccessToken("804203820532252672-2xp6JVWFepbN6lmYnxPLXUwLUUTEgQO")
  .setOAuthAccessTokenSecret("tcXQpznt4za1RqJa4mYNB57lFDIcCwUM8lCzhq7fCOtAf")
  .build

  val modelFile = "trendalytics_data/tweets_processed/KMeansModel"
  val sc = new SparkContext(new SparkConf().setAppName("Trendalytics"))
  val model = KMeansModel.load(sc, modelFile)

  val numFeatures = 1000
  val tf = new HashingTF(numFeatures)
  def featurize(s: String): Vector = {
    tf.transform(s.sliding(2).toSeq)
  }

  def simpleStatusListener = new StatusListener() {
    def onStatus(status: Status) { 
      
      
      // val id = status.getId
      // val userName = status.getUser.getName
      // val numFriends = status.getUser.getFriendsCount.toString()
      // val datetime = status.getCreatedAt.toString()
      // val location  = status.getGeoLocation()
      val tweets = status.getText
      // val numReTweet = status.getRetweetCount.toString()

      println("Tweets is in cluster number: " + model.predict(featurize(tweets)).toString)

      // if(location != null){

      //   val lat = location.getLatitude().toString
      //   val long = location.getLongitude().toString

      //   val pw = new FileWriter("nyTweets.txt", true)
      //   val delimiter = "\t"
      //   val toPrint = id + delimiter + userName + delimiter + numFriends + delimiter + datetime + delimiter + lat + 
      //                 delimiter + long + delimiter + tweets + delimiter + numReTweet
      //   // println(toPrint)
      //   pw.write(toPrint + "\n")

      //   pw.close

      // }
        
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
    val twitterStream = new TwitterStreamFactory(Util.config).getInstance
    twitterStream.addListener(Util.simpleStatusListener)

    // val nycBox = Array(Array(-74.1687,40.5722),Array(-73.8062,40.9467))

    // twitterStream.filter(new FilterQuery().locations(nycBox))

    println("################### INSIDE TWITTER STREAMING ###################")
    twitterStream.sample
    Thread.sleep(10000)
    twitterStream.cleanUp
    twitterStream.shutdown
  }
}
