# Categorical Sentiment Analysis & Recommendation System for Social Networks


## Introduction

The project implements a model to cluster Tweets/Facebook posts into various categories such as Movies, Restaurants etc. Then a real time analysis of what the most talked about items in each category by location is performed, for example: top trending movies in the Greenwich Village area, etc. We will understand the semantic/sentiment behind the post to give a score weighting to each item in the category possibly by using some kind of sentiment analysis, number of likes, retweets etc. This will also be a word cloud associated for the items in the list to give the user more granular information.

The project is written in Scala on [Spark](http://spark.apache.org), with big data techniques such as [Spark SQL](http://spark.apache.org/sql/), [Apache Pig](https://pig.apache.org), and the [MLlib](http://spark.apache.org/mllib/) distributed machine learning library.

## Running the Program

use the run.sh script to execute the program.

## Data Processing Scripts 

run individual pig scripts within the folder ('/data-processing')for processing each datasource:

example : pig /data-processing/yelp-process.pig

## Dependencies

Apache Spark
Apache Maven
Scala

## Acknowledgement

We would like to thank Facebook, Twitter, Yelp and TMDb for allowing access to their APIs.
We would like to thank Professor Suzanne McIntosh for providing us constant support and guidance.

## References
http://docs.scala-lang.org/tutorials/scala-with-maven.html

https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html

http://spark.apache.org/mllib/

http://json4s.org/
