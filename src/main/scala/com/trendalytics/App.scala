package com.trendalytics

import java.io._
import org.apache.http.client._
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json4s._
import org.json4s.native.JsonMethods._
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import java.net.URI
import org.apache.hadoop.util.Progressable

/**
 * @author ${user.name}
 */
object App {
  
  def main(args : Array[String]) {

    val sc = new SparkContext(new SparkConf().setAppName("Trendalytics"))

    //val twitter = new TwitterFilter()
    //twitter.fetch()

     val facebook = new FacebookStreamer()
     facebook.fetch()

    // val tmdb = new TMDBStreamer()
    // tmdb.fetch()

    // val yelp = new YelpStreamer()
    // yelp.fetch()

    // val hdfsObj = new HDFSManager()

    // hdfsObj.createFolder("trendalytics_data")
    // hdfsObj.createFolder("trendalytics_data/tweets")

    // val stopWordsFile = "trendalytics_data/stop_words.txt"

    // if(!hdfsObj.isFilePresent(stopWordsFile))
    //     hdfsObj.saveFile(stopWordsFile)

    // val yelpOutputFile = "trendalytics_data/output.csv"

    // if(!hdfsObj.isFilePresent(yelpOutputFile))
    //     hdfsObj.saveFile(yelpOutputFile)

    // val tweetFile = "trendalytics_data/tweets/20161130_060833.txt"

    // if(!hdfsObj.isFilePresent(tweetFile))
    //     hdfsObj.saveFile(tweetFile)

    // val text = sc.textFile(tweetFile)
    // val stopWords = sc.textFile(stopWordsFile)
    // val words = text.flatMap(line => line.split("\\W"))
    // val clean = words.subtract(stopWords)

    // val cleanTweetPath = "trendalytics_data/tweets_processed"

    // hdfsObj.deleteFolder(cleanTweetPath)

    // clean.saveAsTextFile(cleanTweetPath)
    val hdfsObj = new HDFSManager()

    hdfsObj.createFolder("trendalytics_data")
    hdfsObj.createFolder("trendalytics_data/tweets")
    hdfsObj.createFolder("trendalytics_data/tweets_processed")

    val yelpOutputFile = "trendalytics_data/output.csv"

    if(!hdfsObj.isFilePresent(yelpOutputFile))
        hdfsObj.saveFile(yelpOutputFile)

    val tweetFile = "trendalytics_data/tweets/20161130_060833.txt"

    if(!hdfsObj.isFilePresent(tweetFile))
        hdfsObj.saveFile(tweetFile)

    val text = sc.textFile(tweetFile)
    val stopWords = sc.textFile("trendalytics_data/stop_words.txt")
    val words = text.flatMap(line => line.split("\\W"))
    val clean = words.subtract(stopWords)

    val cleanTweetPath = "trendalytics_data/tweets_processed/20161130_060833.txt"

    if(!hdfsObj.isFilePresent(cleanTweetPath))
        clean.saveAsTextFile(cleanTweetPath)

    return

  }
}
