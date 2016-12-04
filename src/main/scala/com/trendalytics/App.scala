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
// import org.apache.hadoop.util.Progressable
import org.apache.spark.sql.SQLContext
// import org.apache.spark.sql.SQLContext.implicits._

// import org.apache.spark.mllib.clustering.KMeans

/**
 * @author ${user.name}
 */
object App {
  
  case class Tweet(key_name: String, text: String, id: String, username: String, retweets: Int, num_friends: Int, datetime: String)

  def getListOfFiles(dir: String):List[File] = {
      val d = new File(dir)
      if (d.exists && d.isDirectory) {
        d.listFiles.filter(_.isFile).toList
      } else {
        List[File]()
      }
  }

  def main(args : Array[String]) {

    val sc = new SparkContext(new SparkConf().setAppName("Trendalytics"))

    // val twitter = new TwitterFilter()
    // twitter.fetch()

    // val facebook = new FacebookStreamer()
    // facebook.fetch()

    // val tmdb = new TMDBStreamer()
    // tmdb.fetch()

    // val yelp = new YelpStreamer()
    // yelp.fetch()

    val hdfsObj = new HDFSManager()

    hdfsObj.createFolder("trendalytics_data")
    hdfsObj.createFolder("trendalytics_data/tweets")
    // hdfsObj.createFolder("trendalytics_data/tweets_processed")

    val stopWordsFile = "trendalytics_data/stop_words.txt"

    if(!hdfsObj.isFilePresent(stopWordsFile))
        hdfsObj.saveFile(stopWordsFile)

    val yelpOutputFile = "trendalytics_data/output.csv"

    if(!hdfsObj.isFilePresent(yelpOutputFile))
        hdfsObj.saveFile(yelpOutputFile)


    println("####### Writing Tweet files to HDFS ########")
    val tweet_files = getListOfFiles("trendalytics_data/tweets")

    for (tweet_file <- tweet_files) {
        if(!hdfsObj.isFilePresent(tweet_file.toString()))
            hdfsObj.saveFile(tweet_file.toString())
    }
    // val tweetFile = "trendalytics_data/tweets/20161130_060833.txt"

    val tweets = sc.textFile(tweet_files(0).toString())

    // val tweets = sc.textFile(tweetInput)
    
    for (tweet <- tweets.take(5)) {
      println(tweet)
    }

    val sqlContext = new SQLContext(sc)

    import sqlContext.implicits._

    val tweetInfo = tweets.map(_.split("\t")).map(p => Tweet(p(0), p(1), p(2), p(3), p(4).trim.toInt, p(5).trim.toInt, p(6))).toDF()

    tweetInfo.registerTempTable("tweets")

    val movie = sqlContext.sql("SELECT key_name, text FROM tweets WHERE key_name = 'doctor strange' ")

    movie.map(t => "Movie Name: " + t.getAs[String]("key_name")).collect().foreach(println)

    return

  }
}
