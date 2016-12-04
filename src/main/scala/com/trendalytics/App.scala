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
import org.apache.spark.sql.SQLContext._
// import org.apache.spark.sql.SQLContext.implicits._

import org.apache.spark.mllib.clustering.KMeans

// Import Row.
import org.apache.spark.sql.Row;

// Import Spark SQL data types
import org.apache.spark.sql.types.{StructType,StructField,StringType};

/**
 * @author ${user.name}
 */
object App {
  
  // case class TweetRecord(key_name: String, text: String, id: String, username: String, retweets: Int, num_friends: Int, datetime: String)

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
    val tweetFile = "trendalytics_data/tweets/12012016_01.txt"
    val tweets = sc.textFile(tweetFile)

    // val tweets = sc.textFile(tweet_files(0).toString())

    for (tweet <- tweets.take(5)) {
      println(tweet)
    }

    val sqlContext = new SQLContext(sc)

    import sqlContext.implicits._

    // val tweetInfo = tweets.map(_.split("\t")).map(p => TweetRecord(p(0), p(1), p(2), p(3), p(4).trim.toInt, p(5).trim.toInt, p(6))).toDF()

    // tweetInfo.registerTempTable("tweets")

    // val movie = sqlContext.sql("SELECT key_name, text FROM tweets")

    // movie.map(t => "Movie Name: " + t.getAs[String]("key_name")).collect().foreach(println)

    // The schema is encoded in a string
    // key_name: String, text: String, id: String, username: String, retweets: Int, num_friends: Int, datetime: String

    val schemaString = "key_name\ttext\tid\tusername\tretweets\tnum_friends\tdatetime"

    // Generate the schema based on the string of schema
    val schema =
      StructType(
        schemaString.split("\t").map(fieldName => StructField(fieldName, StringType, true)))

    // Convert records of the RDD (tweets) to Rows.
    val rowRDD = tweets.map(_.split("\t")).map(p => Row(p(0), p(1), p(2), p(3), p(4).trim, p(5).trim, p(6)))

    // Apply the schema to the RDD.
    val peopleDataFrame = sqlContext.createDataFrame(rowRDD, schema)

    // Register the DataFrames as a table.
    peopleDataFrame.registerTempTable("tweets")

    peopleDataFrame.printSchema()

    // SQL statements can be run by using the sql methods provided by sqlContext.
    sqlContext.sql("SELECT key_name FROM tweets").collect().foreach(println)

    // The results of SQL queries are DataFrames and support all the normal RDD operations.
    // The columns of a row in the result can be accessed by field index or by field name.
    // results.map(t => "Movie Name: " + t(0)).collect().foreach(println)
    return

  }
}
