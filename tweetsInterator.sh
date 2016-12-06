#!/bin/bash
# Loop to fetch realtime tweets

for i in {1..20}
do
	echo "-----------------------------------"
	echo "###### Begin "$i" interation ######"
	echo "-----------------------------------"
    # scala -cp target/trendalytics-0.0.1.jar com.trendalytics.App
    spark-submit --class com.trendalytics.App --master yarn trendalytics_data/trendalytics-fetchMoviesOnly.jar
	printf "Pause to wait for new tweets...\n"
	sleep 15m
done

# spark-submit --class com.trendalytics.App --master yarn trendalytics_data/trendalytics-fetchMoviesRestaurants.jar
