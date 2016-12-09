package com.trendalytics

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
import org.apache.spark.mllib.clustering.KMeans
import com.google.gson.{GsonBuilder, JsonParser}

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.ml.feature.CountVectorizer
import org.apache.spark.ml.feature.CountVectorizerModel
import org.apache.spark.ml.classification.LogisticRegression
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

    //remove stop words from text
    val tokenizer: Tokenizer = new Tokenizer()
  		.setInputCol("text")
  		.setOutputCol("text_tokenized")

    val remover = new StopWordsRemover()
  		.setInputCol("text_tokenized")
  		.setOutputCol("filtered_text")

    val tokenized = tokenizer.transform(itemTable)
    val filtered = remover.transform(tokenized)

    //######### Linear Regression ############
    // val countVectorizer = new CountVectorizer()
    //   .setInputCol("filtered_text")
    //   .setOutputCol("features")

    // val countVectorizer: CountVectorizerModel = new CountVectorizer()
    //   .setInputCol("filtered_text")
    //   .setOutputCol("features")
    //   .setVocabSize(3)
    //   .setMinDF(2)
    //   .fit(filtered)
    
    // val lr = new LogisticRegression()
    //   .setMaxIter(10)
    //   .setRegParam(0.2)
    //   .setElasticNetParam(0.0)

    // val featurized = countVectorizer.transform(filtered)
    // println("######################### After CountVectorizer")
    // featurized.show()

    // val pipeline = new Pipeline().setStages(Array(tokenizer, filterer, countVectorizer, lr))
    // val lrModel = pipeline.fit(trainingData)

    // println("######################### After LogisticRegression")
    // classified.show()
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

    flattenedDF;
  }
}

