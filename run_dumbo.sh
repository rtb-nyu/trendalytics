mvn clean
mvn package

spark-submit --class com.trendalytics.App --master yarn target/scala-0.0.1-SNAPSHOT.jar 
# /user/<net id>/book.txt /user/<net id>/output
# mvn package exec:java -Dexec.mainClass=com.trendalytics.App

