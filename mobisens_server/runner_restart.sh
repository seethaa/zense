#!/bin/bash

pid=$(ps ux | awk '/rake/ && !/awk/ {print $2}')
if [ "$pid" != "" ] 
then
    echo "Cron runner detected, pid $pid."
    echo "killing running cron runner.."
    kill -9 $pid
    pid=$(ps ux | awk '/rake/ && !/awk/ {print $2}')
    if [ "$pid" == "" ] 
    then
	echo "runner killed."
    else
	echo "kill runner failed."
    fi
fi

echo "Starting new cron runner..."
rake cron_runner > /mobisens/server/log/cron.log &
pid=$(ps ux | awk '/rake/ && !/awk/ {print $2}')
if [ "$pid" != "" ] 
then
    echo "Runner started with pid $pid."
else
    echo "Start runner failed."
fi