package com.trendalytics

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

import org.apache.spark.sql.SQLContext
import org.apache.spark.mllib.clustering.KMeans
import com.google.gson.{GsonBuilder, JsonParser}

import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.ml.feature.StopWordsRemover

/**
 * @author ${user.name}
 */
object Filter {
  
  def remove(sc : SparkContext) = {
    //read json to dataframe
    val inputFilePath = "hdfs:///user/wl1485/project/test/"

    val jsonParser = new JsonParser()
    val gson = new GsonBuilder().setPrettyPrinting().create() 
    val sqlContext = new SQLContext(sc)

    val itemTable = sqlContext.jsonFile(inputFilePath).cache()
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
  		.setInputCol("snippet_text")
  		.setOutputCol("snippet_tokenized")

    val remover = new StopWordsRemover()
  		.setInputCol("snippet_tokenized")
  		.setOutputCol("filtered_text")

    val tokenized = tokenizer.transform(itemTable)
    val filtered = remover.transform(tokenized)
    
    println("----list of words in the text----")
    tokenized.select("snippet_tokenized").show()
    println("----list of filtered words in the text----")
    filtered.select("filtered_text").show()

  }
}

class StopWordFilter {
  def remove(sc : SparkContext) = {    
    Filter.remove(sc);
  }
}