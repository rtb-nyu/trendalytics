#!/bin/bash
# Loop to fetch realtime tweets

for i in {1..15}
do
	echo "-----------------------------------"
	echo "###### Begin "$i" interation ######"
	echo "-----------------------------------"
    # scala -cp target/trendalytics-0.0.1.jar com.trendalytics.App
    spark-submit --class com.trendalytics.App --master yarn target/trendalytics-0.0.1.jar
	printf "Pause to wait for new tweets...\n"
	sleep 15m
done
