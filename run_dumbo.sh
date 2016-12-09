/opt/maven/bin/mvn clean
/opt/maven/bin/mvn package 2>&1 | tee outputResults/logFiles/train_maven_build_jar.log

spark-submit --packages com.databricks:spark-csv_2.11:1.5.0 \
--class com.trendalytics.App \
--master yarn target/trendalytics-0.0.1.jar \
2>&1 | tee outputResults/logFiles/train_cluster_model.log

# /user/<net id>/book.txt /user/<net id>/output
# mvn package exec:java -Dexec.mainClass=com.trendalytics.App
