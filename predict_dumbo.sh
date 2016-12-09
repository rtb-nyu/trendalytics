/opt/maven/bin/mvn clean
/opt/maven/bin/mvn package 2>&1 | tee outputResults/logFiles/predict_maven_build_jar.log

spark-submit --class com.trendalytics.Predict \
--master yarn target/trendalytics-0.0.1.jar \
2>&1 | tee outputResults/logFiles/predict_realtimeTweets.log

# /user/<net id>/book.txt /user/<net id>/output
# mvn package exec:java -Dexec.mainClass=com.trendalytics.App
