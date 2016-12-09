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
import org.apache.spark.sql.{Row, DataFrame}

// Import Spark SQL data types
import org.apache.spark.sql.types.{StructType,StructField,StringType};


import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.sql.functions.udf

   import org.apache.spark.ml.classification.MultilayerPerceptronClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator

/* @author ${user.name}
 */
object App {
  
  case class TweetRecord(key_name: String, text: String, id: String, username: String, retweets: Int, num_friends: Int, datetime: String)
  case class CrimeType(label: Double, features:Vector)

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
    hdfsObj.createFolder("trendalytics_data/facebook_posts")
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
        if(!hdfsObj.isFilePresent(tweet_file.toString))
            hdfsObj.saveFile(tweet_file.toString)
    }

     println("####### Writing FB files to HDFS ########")
    
    val fb_files = getListOfFiles("trendalytics_data/facebook_posts")

    for (fb_file <- fb_files) {
        if(!hdfsObj.isFilePresent(fb_file.toString))
            hdfsObj.saveFile(fb_file.toString)
    }


    /*val tweetFile = "trendalytics_data/tweets/test.txt"
    val tweets = sc.textFile(tweetFile)

    // val tweets = sc.textFile(tweet_files(0).toString())

    for (tweet <- tweets.take(5)) {
      println(tweet.split("\t").foreach(println))
    }

    val sqlContext = new SQLContext(sc)

    import sqlContext.implicits._

    val customSchemaTweet = StructType(Array(
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
        .schema(customSchemaTweet)
        .load(tweetFile)

    println("Starting CSV processing...")
    df.printSchema()

    df.registerTempTable("tweets")

    val selectedData = df.select("key", "text")

    df.select(df("key"), df("text")).show()
*/
   val sqlContext = new SQLContext(sc)

    import sqlContext.implicits._


    //for (fb_file <- fb_files) {

    //val fbFile = "trendalytics_data/facebook_posts/12042016_01.txt"
    //val post_file = sc.textFile(fb_File)

    // val tweets = sc.textFile(tweet_files(0).toString)

   {

   val fb_file = fb_files(0).toString


    val customSchemafb = StructType(Array(
        StructField("key", StringType, true),
        StructField("features", StringType, true),
        StructField("id", StringType, true),
        StructField("time", StringType, true)))

        val dfb = sqlContext.read
        .format("com.databricks.spark.csv")
        .option("header", "false") // Use first line of all files as header 
        .option("delimiter", "\t")
        .schema(customSchemafb)
        .load(fb_file.toString)


    println("Starting CSV processing...")
    dfb.printSchema()

    dfb.registerTempTable("posts")
  /*  val toFea    = udf[Vector,String]{a => var myArray : Array[String] = new Array[String](1)
                                              myArray(0)=a
                                              Vectors.dense(myArray)
                                    }*/
    
println("posts table registered")
    def toLab(c : String):Double = c match {
        case "Sid Gold's Request Room" => 0.0
        case "Gersi" => 1.0
        case "Holy Guacamole" => 2.0
        case "Gristmill" => 3.0
        case "O'Donoghues Pub" => 4.0
        case "Tipsy Shanghai" => 5.0
        case "Brooklyn Bazaar" => 6.0
        case _ => 7.0
    

  }

  

 /*   val selectedData = dfb.withColumn("label",toLab(dfb("key"))).withColumn("features",toFea(dfb("text")))
   
    selectedData.map(row => {val col2 = "1:"+row.getAs[String](1)
                Row(row(0),col2)})*/
  val numFeatures = 50000
  val tf = new HashingTF(numFeatures)
   def featurize(s: String): Vector = {
    tf.transform(s.sliding(2).toSeq)
  }

val texts = sqlContext.sql("SELECT features from posts").map(t=>t.toString)
  val selectedData = texts.map(featurize).cache()
  selectedData.count()

println("selectedData featurized")
 // val newvar = selectedData.withColumn("label",toLab(dfb("key")))
    //dfb.select(dfb("key"), dfb("text")).show()
    selectedData.map(row => Row(row))
 selectedData.registerTempTable("selected")
 val nevar = sqlContext.sql("SELECT key as label,selectedData.* as features from selected,posts").toDF()



 nevar.map(row => {//val col1 = row.getAs[String](0)
        Row(toLab(row(0).toString),row(1))})
 nevar.registerTempTable("postsfb")
  val newvar = sqlContext.sql("SELECT * from postsfb").toDF()
//val newvar = sqlContext.createDataFrame(nevar.map { case Row(label: Double, features : Vector) => CrimeType(label,features) })

println(" training data ready")
// Load the data stored in LIBSVM format as a DataFrame.
//val data = spark.read.format("libsvm")
 // .load("data/mllib/sample_multiclass_classification_data.txt")
// Split the data into train and test
val splits = newvar.randomSplit(Array(0.6, 0.4), seed = 1234L)
val train = splits(0)
val test = splits(1)
// specify layers for the neural network:
// input layer of size 4 (features), two intermediate of size 5 and 4
// and output of size 3 (classes)
val layers = Array[Int](1, 5, 4, 8)
// create the trainer and set its parameters
val trainer = new MultilayerPerceptronClassifier()
  .setLayers(layers)
  .setBlockSize(128)
  .setSeed(1234L)
  .setMaxIter(100)
// train the model
val model = trainer.fit(train)
// compute accuracy on the test set
val result = model.transform(test)
val predictionAndLabels = result.select("prediction", "label")
val evaluator = new MulticlassClassificationEvaluator()
  .setMetricName("accuracy")
println("Accuracy: " + evaluator.evaluate(predictionAndLabels))

  }
    return

  }
}
