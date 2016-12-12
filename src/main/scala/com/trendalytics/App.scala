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

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.SQLContext._

import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.clustering.KMeans

// Import Row.
import org.apache.spark.sql.Row;

// Import Spark SQL data types
import org.apache.spark.sql.types.{StructType,StructField,StringType};

/**
 * @author ${user.name}
 */
object App {
  
  val numFeatures = 1000
  val tf = new HashingTF(numFeatures)

  def getListOfFiles(dir: String):List[File] = {
      val d = new File(dir)
      if (d.exists && d.isDirectory) {
        d.listFiles.filter(_.isFile).toList
      } else {
        List[File]()
      }
  }

  def removeStopwords(sc : SparkContext, sqlContext : SQLContext, selectedData : DataFrame) : DataFrame = {
    val sw = new StopWordFilter()
    sw.remove(sc, sqlContext, selectedData)
  }

  def featurize(s: String): Vector = {
    tf.transform(s.sliding(2).toSeq)
  }

  def main(args : Array[String]) {

    val sc = new SparkContext(new SparkConf().setAppName("Trendalytics"))

    val hdfsObj = new HDFSManager()

    hdfsObj.createFolder("trendalytics_data")
    hdfsObj.createFolder("trendalytics_data/tweets")

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

         println("####### Writing FB files to HDFS ########")
    
    val fb_files = getListOfFiles("trendalytics_data/facebook_posts")

    for (fb_file <- fb_files) {
        if(!hdfsObj.isFilePresent(fb_file.toString))
            hdfsObj.saveFile(fb_file.toString)
    }

    val tweetFile = "trendalytics_data/tweets/"
    val tweets = sc.textFile(tweetFile)

    val sqlContext = new SQLContext(sc)

    import sqlContext.implicits._

    val customSchema = StructType(Array(
        StructField("key", StringType, true),
        StructField("text", StringType, true),
        StructField("id", StringType, true),
        StructField("username", StringType, true),
        StructField("retweets", StringType, true),
        StructField("num_friends", StringType, true),
        StructField("datetime", StringType, true)))

    val df = sqlContext.read
        .format("com.databricks.spark.csv")
        .option("header", "false") // Use first line of all files as header
        .option("delimiter", "\t")
        .schema(customSchema)
        .load(tweetFile)

    println("Starting CSV processing...")
    df.printSchema()

    df.registerTempTable("tweets")

    val selectedData = df.select("key", "text")

    df.select(df("key"), df("text")).show()

    val filteredData = removeStopwords(sc, sqlContext, selectedData)

    filteredData.registerTempTable("tweets_filtered")
     println("Starting FB processing...")


    val postsFile = "trendalytics_data/facebook_posts/12122016_12.txt"
    val posts = sc.textFile(postsFile)
       val customSchemafb = StructType(Array(
        StructField("key", StringType, true),
        StructField("text", StringType, true),
        StructField("id", StringType, true),
        StructField("time", StringType, true)))

    val dfb = sqlContext.read
        .format("com.databricks.spark.csv")
        .option("header", "false") // Use first line of all files as header 
        .option("delimiter", "\t")
        .schema(customSchemafb)
        .load(postsFile)

    dfb.printSchema()
    dfb.na.fill("no review")
    dfb.select(dfb("key"), dfb("text")).show()

    dfb.registerTempTable("posts")
    println("End FB processing...")

    val FBselectedData = dfb.select("key", "text")
    val FBfilteredData = removeStopwords(sc, sqlContext, FBselectedData)
    FBfilteredData.registerTempTable("posts_filtered")


    val texts = sqlContext.sql("SELECT filtered_text from tweets_filtered UNION ALL SELECT filtered_text from posts_filtered").map(t => t.toString)
     
    // Caches the vectors since it will be used many times by KMeans.
    val vectors = texts.map(featurize).cache()

    vectors.count()  // Calls an action to create the cache.
    println("vectorized texts")

    val numIterations = 100
    val numClusters = 2

    val model = KMeans.train(vectors, numClusters, numIterations)

    hdfsObj.deleteFolder("trendalytics_data/tweets_processed")
    
    model.save(sc, "trendalytics_data/tweets_processed/KMeansModel")

    val some_tweets = texts.sample(true, 0.1).take(100)
    println("----Example tweets from the clusters")
    for (i <- 0 until numClusters) {
      println(s"\nCLUSTER $i:")
      some_tweets.foreach { t =>
        if (model.predict(featurize(t)) == i) {
          println(t)
        }
      }
    }
    return

  }
}