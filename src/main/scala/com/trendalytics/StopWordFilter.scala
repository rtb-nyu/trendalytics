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

    flattenedDF;
  }
}

