mvn package
scala -cp target/trendalytics-0.0.1.jar com.trendalytics.App

hdfs dfs -test -d /user/pd1241/trendalytics/movies.txt

if [ $? == 0 ]
  then
    echo "Found existing datasource file ... Removing"
    hdfs dfs -rm -r /user/pd1241/trendalytics/movies.txt
fi


hdfs dfs -test -d /user/pd1241/trendalytics

if [ $? == 0 ]
  then
    echo "Creating Directory Structure"
    hdfs dfs -mkdir /user/pd1241/trendalytics
fi

hdfs dfs -put movies.txt /user/pd1241/trendalytics