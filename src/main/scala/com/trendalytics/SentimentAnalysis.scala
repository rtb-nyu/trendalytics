package com.trendalytics

import java.io._
import java.util.Properties
import scala.collection.JavaConversions._
// import org.apache.http.client._
// import org.apache.http.client.methods.HttpGet
// import org.apache.http.client.utils.URIBuilder
// import org.apache.http.impl.client.HttpClients
// import org.apache.http.util.EntityUtils
// import org.json4s._
// import org.json4s.native.JsonMethods._
// import twitter4j._

// import org.apache.spark.SparkContext
// import org.apache.spark.SparkContext._
// import org.apache.spark.SparkConf

// import org.apache.spark.mllib.feature.HashingTF
// import org.apache.spark.mllib.linalg.Vector
// import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
// import org.apache.spark.mllib.clustering.KMeans

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations

import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.AnnotatedTree;
/**
 * @author ${user.name}
 */

object SentimentAnalysis {

  def findSentiment(line : String) : Int = {
 
    val props = new Properties();
    props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
    val pipeline = new StanfordCoreNLP(props);
    var mainSentiment : Int = 0;
    var longest : Int = 0;

    if (line != null && line.length() > 0) {
        
        val annotation = pipeline.process(line);

        for (sentence <- annotation.get(classOf[CoreAnnotations.SentencesAnnotation])) {
            val tree = sentence.get(classOf[SentimentCoreAnnotations.AnnotatedTree]);
            var sentiment = RNNCoreAnnotations.getPredictedClass(tree);
            val partText = sentence.toString();
            
            if (partText.length() > longest) {
                mainSentiment = sentiment;
                longest = partText.length();
            }

        }
    }

    mainSentiment
  }

  def main(args : Array[String]) {
    println("hello World")
    println("Sentiment is :", findSentiment("this is an awesome movie!!"))
  }
}
