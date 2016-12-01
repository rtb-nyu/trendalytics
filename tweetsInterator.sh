#!/bin/bash
# Loop to scrach realtime tweets

for i in {1..10}
do
	echo "-----------------------------------"
	echo "###### Begin "$i" interation ######"
	echo "-----------------------------------"
	scala -cp target/trendalytics-0.0.1.jar com.trendalytics.App
	sleep 1h
	printf "\n"
done