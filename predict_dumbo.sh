/opt/maven/bin/mvn clean
/opt/maven/bin/mvn package

spark-submit --class com.trendalytics.Predict --master yarn target/trendalytics-0.0.1.jar

# /user/<net id>/book.txt /user/<net id>/output
# mvn package exec:java -Dexec.mainClass=com.trendalytics.App
