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

import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.clustering.KMeans

// Import Row.
import org.apache.spark.sql.Row;

// Import Spark SQL data types
import org.apache.spark.sql.types.{StructType,StructField,StringType};

/**
 * @author ${user.name}
 */
object Predict {
  
  // case class TweetRecord(key_name: String, text: String, id: String, username: String, retweets: Int, num_friends: Int, datetime: String)
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

  def featurize(s: String): Vector = {
    tf.transform(s.sliding(2).toSeq)
  }

  def main(args : Array[String]) {

    println("Initializing Streaming Spark Context...")
    val conf = new SparkConf().setAppName(this.getClass.getSimpleName)
    // val ssc = new StreamingContext(conf, Seconds(5))


    val sc = new SparkContext(new SparkConf().setAppName("Trendalytics"))

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

    val texts = sqlContext.sql("SELECT text from tweets").map(t => t(0).toString)

    val numIterations = 10
    val numClusters = 5
    val modelFile = "trendalytics_data/tweets_processed/KMeansModel"

    // write predicted clustering tweets into files
    // val dNow = new Date()
    // val ft = new SimpleDateFormat ("MMddyyyy_hh")
    // val currentTime = ft.format(dNow).toString();
    val pwKmeans = new FileWriter("trendalytics_data/clusters/" + numClusters.toString + "-" + numIterations.toString + "_KMeans_predict.txt", true)

    // Caches the vectors since it will be used many times by KMeans.
    val vectors = texts.map(featurize).cache()
    pwKmeans.write("Vectors Count: " + vectors.count().toString() + "\n")  // Calls an action to create the cache.

    // val model = KMeans.train(vectors, numClusters, numIterations)

    // hdfsObj.deleteFolder("trendalytics_data/tweets_processed")
    
    // sc.makeRDD(model.clusterCenters, numClusters).saveAsObjectFile("trendalytics_data/tweets_processed")
    println("Initalizaing the the KMeans model...")
    // val model = new KMeansModel(ssc.sparkContext.objectFile[Vector](modelFile.toString).collect())
    val model = KMeansModel.load(sc, modelFile)

    val some_tweets = texts.take(100)
    println("----Example tweets from the clusters")
    for (i <- 0 until numClusters) {
      // println(s"\nCLUSTER $i:")
      pwKmeans.write(s"\nCLUSTER $i:\n")
      some_tweets.foreach { t =>
        if (model.predict(featurize(t)) == i) {
          // println(t)
          pwKmeans.write(t.toString + "\n")
        }
      }
    }
    pwKmeans.close

    return

  }
}
