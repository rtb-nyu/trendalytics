/opt/maven/bin/mvn clean
/opt/maven/bin/mvn package

spark-submit --packages com.databricks:spark-csv_2.11:1.5.0 --class com.trendalytics.StopWordFilter --master yarn target/trendalytics-0.0.1.jar
