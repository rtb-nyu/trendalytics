Categorical Sentiment Analysis & Recommendation System for Social Networks
==========================================================================

Introduction
------------

The project implements a model to cluster Tweets/Facebook posts into various
categories such as Movies, Restaurants etc. Then a real time analysis of what
the most talked about items in each category by location is performed, for
example: top trending movies in the Greenwich Village area, etc. We will
understand the semantic/sentiment behind the post to give a score weighting to
each item in the category possibly by using some kind of sentiment analysis,
number of likes, retweets etc. This will also be a word cloud associated for the
items in the list to give the user more granular information.

We stored queried categorized tweets as the training data, with labels of
keywords on the first column, texts and other metadata. With this training set,
we can cluster these tweets into categories using Spark with N-gram and K-Means
algorithms by MLlib. After training, we uploaded the model to HDFS, so that it
can be loaded to cluster further tweets in real time. Sentiment analysis will
also be adopted meanwhile to clustering new tweets. We used the methods, e.g.
Recursive Neural Networks (RNN), provided by the Stanford CoreNLP to understand
whether a user/tweet expresses positive or negative attitude. Scores from 0 to 4
indicate as “very negative”, “negative”, “neutral”, “positive”, and “very
positive” respectively.

The project is written in Scala on [Spark](http://spark.apache.org), with big
data techniques such as [Spark SQL](http://spark.apache.org/sql/), [Apache
Pig](https://pig.apache.org), the [MLlib](http://spark.apache.org/mllib/)
distributed machine learning library, and [Stanford
CoreNLP](http://stanfordnlp.github.io/CoreNLP/).

Running the Program
-------------------

-   To train the clustering model, and upload the model to HDFS:

On Dumbo: use the `run_dumbo.sh` script to execute the program.

On local machine: use the `run.sh` script to execute the program.

-   To do sentiment analysis and cluster realtime tweets with the model stored
    in HDFS:

On Dumbo: use the `predict_dumbo.sh` script to execute the program.

-   To automatically fetch more tweets for training the model:

Use the `tweetsInterator.sh` script to execute the program.

-   To process data:

Run individual pig scripts within the folder (`/data-processing`)for processing
each datasource:

example : `pig /data-processing/yelp-process.pig`

Dependencies
------------

Apache Spark

Apache Maven

Scala

MLlib

Stanford CoreNLP

Repository
------------

Being consistent with the [GitHub rtb-nyu/trendalytics](https://github.com/rtb-nyu/trendalytics) repository, the codes inside the following directories are:

-  `src` Main Scala source codes. Please refer to
   `src/main/scala/com/trendalytics/README.md` for more details.

-  `outputResults` Output log files illustrating the successfulness of
    analytics.

-  `trendalytics_data` Four data sources.

-  `data-processing` PigLatin or Python scripts for processing Yelp and
    Facebook data.

Acknowledgement
---------------

We would like to thank Facebook, Twitter, Yelp and TMDb for allowing access to
their APIs.

We would like to thank Professor Suzanne McIntosh for providing us constant
support and guidance.

References
----------

-   [Databricks Spark Reference
    Applications](https://www.gitbook.com/book/databricks/databricks-spark-reference-applications/details)

-   [Scala with
    Maven](http://docs.scala-lang.org/tutorials/scala-with-maven.html), [Maven
    in 5
    Minutes](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

-   [MLlib](http://spark.apache.org/mllib/), [Stanford
    CoreNLP](http://stanfordnlp.github.io/CoreNLP/)

-   [Spark SQL, DataFrames and Datasets
    Guide](https://spark.apache.org/docs/1.6.0/sql-programming-guide.html#spark-sql-dataframes-and-datasets-guide)

-   [Json4s](http://json4s.org/)

-   [Communicating with Twitter via Twitter4J using
    Scala](https://blog.knoldus.com/2012/12/10/communicating-with-twitter-via-twitter4j-using-scala/),
    [The Facebook Graph API](https://developers.facebook.com/docs/graph-api)

1.  Mikolov, T., Chen, K., Corrado, G., and Dean, J.: Efficient estimation of
    word representations in vector space, arXiv

2.  T. White. Hadoop: The Definitive Guide. O’Reilly Media Inc., Sebastopol, CA,
    May 2012.

3.  A. Gates. Programming Pig. O’Reilly Media Inc.,Sebastopol, CA,  October
    2011.

4.  J. Dean and S. Ghemawat. MapReduce: Simplified data processing on large
    clusters. In proceedings of 6th Symposium on Operating Systems Design and
    Implementation, 2004.

5.  C. Olston, B. Reed, U. Srivastava, R. Kumar, A. Tomkins. Pig Latin: A
    not-so-foreign language for data processing. In proceedings of SIGMOD, June
    2008.

6.  Thibault Debatty, Pietro Michiardi, Wim Mees, Olivier Thonnard. Determining
    the k in k-means with MapReduce

7.  Hutto, C.J. & Gilbert, E.E. (2014). VADER: A Parsimonious Rule-based Model
    for Sentiment Analysis of Social Media Text. Eighth International Conference
    on Weblogs and Social Media (ICWSM-14). Ann Arbor, MI, June 2014.

8.  Xiangrui Meng, Joseph Bradley, Burak Yavuz, Evan Sparks, Shivaram
    Venkataraman, Davies Liu, Jeremy Freeman, DB Tsai, Manish Amde, Sean Owen,
    Doris Xin, Reynold Xin, Michael J. Franklin, Reza Zadeh, Matei Zaharia, and
    Ameet Talwalkar. MLlib: Machine Learning in Apache Spark, Journal of Machine
    Learning Research (JMLR). 2016.

9.  Tapas Kanungo, David M. Mount, Nathan S. Netanyahu, Christine D. Piatko,
    Ruth Silverman, and Angela Y. Wu. An Efficient k-Means Clustering Algorithm:
    Analysis and Implementation. IEEE Transactions on pattern analysis and
    machine intelligence, vol. 24, no. 7, July 2002

10. Stefani Chan, Raymond K. Pon, Alfonso F. Cardenas. Visualization and
    Clustering of Author Social Networks.

11. Chen Jin, Ruoqian Liu, Zhengzhang Chen, William Hendrix, Ankit Agrawal,
    Wei-keng Liao, Alok Choudhary. A Scalable Hierarchical Clustering Algorithm
    Using Spark
