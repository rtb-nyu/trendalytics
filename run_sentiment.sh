/opt/maven/bin/mvn clean
/opt/maven/bin/mvn package

spark-submit --class com.trendalytics.SentimentAnalysis --master yarn target/trendalytics-0.0.1.jar