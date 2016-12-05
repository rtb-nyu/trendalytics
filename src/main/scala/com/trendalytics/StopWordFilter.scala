package com.trendalytics

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types._
import org.apache.spark.mllib.clustering.KMeans
import com.google.gson.{GsonBuilder, JsonParser}

import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.ml.feature.StopWordsRemover
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.linalg.Vector;

/**
 * @author ${user.name}
 */
class StopWordFilter {
  
  def remove(sc : SparkContext, sqlContext : SQLContext, itemTable : DataFrame) : DataFrame = {
    //read json to dataframe
    // val inputFilePath = "hdfs:///user/wl1485/project/test/"

    // val jsonParser = new JsonParser()
    // val gson = new GsonBuilder().setPrettyPrinting().create() 
    // val sqlContext = new SQLContext(sc)

    // val itemTable = sqlContext.jsonFile(inputFilePath).cache()
    itemTable.registerTempTable("itemTable")

//   println("------Item table Schema---")
//    itemTable.printSchema()
//
//    println("----Sample Tweet Text-----")
//    sqlContext.sql("SELECT snippet_text FROM itemTable LIMIT 10").collect().foreach(println)
//
//    println("------Sample Name, text---")
//    sqlContext.sql("SELECT name, snippet_text FROM itemTable LIMIT 100").collect().foreach(println)
//
//    println("----Stop Words Removed----")

    //remove stop words from text
    val tokenizer: Tokenizer = new Tokenizer()
  		.setInputCol("text")
  		.setOutputCol("text_tokenized")

    val remover = new StopWordsRemover()
  		.setInputCol("text_tokenized")
  		.setOutputCol("filtered_text")

    val tokenized = tokenizer.transform(itemTable)
    val filtered = remover.transform(tokenized)
    
    // println("----list of words in the text----")
    // tokenized.select("text_tokenized").show()
    // println("----list of filtered words in the text----")
    // filtered.select("filtered_text").show()
    // println("----show table containing all tokenized info----")
    // filtered.show()
    import sqlContext.implicits._;
    val flattenedRDD =  filtered.map(row => {
    val row1 = row.getAs[Seq[String]](3)
    val flattenedText = row1.mkString(" ")
    Row(row(0),flattenedText)})

    val schemaString = "key filtered_text"
    val fields = schemaString.split(" ")
      .map(fieldName => StructField(fieldName, StringType, nullable = true))
    val schema = StructType(fields)
    val flattenedDF = sqlContext.createDataFrame(flattenedRDD, schema)

    // println("-----the flattened dataframe------")
    // flattenedDF.show()

    // println("--- Training the model and persist it")
    // val numClusters = 3
    // val numIterations = 3
    // val outputModelDir = "hdfs:///user/wl1485/project/test_output"
    // val texts = sqlContext.sql("SELECT text from itemTable").map(_.getString(0))
    // val vectors = texts.map(Utils.featurize).cache()
    // vectors.count()
    // val model = KMeans.train(vectors, numClusters, numIterations)

    // val hdfsObj = new HDFSManager()
    // hdfsObj.deleteFolder(outputModelDir)

    // sc.makeRDD(model.clusterCenters, numClusters).saveAsObjectFile(outputModelDir)
    // val some_tweets = texts.take(100)
    // println("----Example tweets from the clusters")
    // for (i <- 0 until numClusters) {
    //   println(s"\nCLUSTER $i:")
    //   some_tweets.foreach { t =>
    //     if (model.predict(Utils.featurize(t)) == i) {
    //       println(t)
    //     }
    //   }
    // }

    flattenedDF;
  }
}

object Utils{
  val numFeatures = 10
  val tf = new HashingTF(numFeatures)

  /**
   * Create feature vectors by turning each tweet into bigrams of
   * characters (an n-gram model) and then hashing those to a
   * length-1000 feature vector that we can pass to MLlib.
   * This is a common way to decrease the number of features in a
   * model while still getting excellent accuracy (otherwise every
   * pair of Unicode characters would potentially be a feature).
   */
  def featurize(s: String): Vector = {
    tf.transform(s.sliding(2).toSeq)
  }
}

