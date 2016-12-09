1.  `App.scala`: fetch new data, upload data to HDFS, remove "stop words" in
    posts, and train the clustering model

2.  `Predict.scala`: load trained model from HDFS, and predict with it to do
    sentiment analysis and clustering for realtime tweets

3.  `SentimentAnalysis.scala`: sentiment analysis algorithms provided by
    Stanford CoreNLP

4.  `HdfsManager.scala`: automatically store files in HDFS, so that Spark
    Context can load to RDD

5.  `TwitterFilter.scala`: get and filter out tweets only containing certain
    keywords, or posted in some geolocations

6.  `StopWordFilter.scala`: filter out "stop words", e.g., "the", "that", etc,
    in posts

7.  `*Streamer.scala`: streaming for each data sources
