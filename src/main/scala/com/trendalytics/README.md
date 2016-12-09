`App.scala`: fetch new data, upload data to HDFS, remove "stop words" in posts, and train the clustering model
`Predict.scala`: load trained model from HDFS, and predict with it to do sentiment analysis and clustering for realtime tweets
`SentimentAnalysis.scala`: sentiment analysis algorithms provided by Stanford CoreNLP
`HdfsManager.scala`: automatically store files in HDFS, so that Spark Context can load to RDD
`TwitterFilter.scala`: get and filter out tweets only containing certain keywords, or posted in some geolocations
`StopWordFilter.scala`: filter out "stop words", e.g., "the", "that", etc, in posts
`*Streamer.scala`: streaming for each data sources
