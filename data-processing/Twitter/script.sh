### First, remove URLs and emojis in tweets
### Also remove duplicated tweets
chmod +x preproc_mapper.py
chmod +x preproc_reducer.py
hdfs dfs -rm /user/cloudera/class9/input.txt
hdfs dfs -put input.txt /user/cloudera/class9
hdfs dfs -rm -r /user/cloudera/class9/output
hadoop jar /usr/lib/hadoop-mapreduce/hadoop-streaming.jar \
-input /user/cloudera/class9/input.txt \
-output /user/cloudera/class9/output \
-mapper /home/cloudera/Documents/big_data/hwk9/preproc_mapper.py \
-reducer /home/cloudera/Documents/big_data/hwk9/preproc_reducer.py \
2>&1 | tee output.log
hdfs dfs -ls /user/cloudera/class9/output
#hdfs dfs -cat /user/cloudera/class9/output/part-00000
hdfs dfs -cat /user/cloudera/class9/output/part-00000 > preproc.txt


### Then count tweets by geo-location
chmod +x geo_mapper.py
chmod +x geo_reducer.py
#hdfs dfs -cat /user/cloudera/class9/input.txt
hdfs dfs -rm -r /user/cloudera/class9/output
hdfs dfs -rm -r /user/cloudera/class9/input.txt
hdfs dfs -put preproc.txt /user/cloudera/class9/input.txt
hadoop jar /usr/lib/hadoop-mapreduce/hadoop-streaming.jar \
-input /user/cloudera/class9/input.txt \
-output /user/cloudera/class9/output \
-mapper /home/cloudera/Documents/big_data/hwk9/geo_mapper.py \
-reducer /home/cloudera/Documents/big_data/hwk9/geo_reducer.py \
2>&1 | tee output.log
hdfs dfs -ls /user/cloudera/class9/output
hdfs dfs -cat /user/cloudera/class9/output/part-00000
