#!/bin/bash

MYPROG="ruby"
RESTART="/mobisens/server/script/server -d"
PGREP="/usr/bin/pgrep"
# find myprog pid
$PGREP ${MYPROG}
# if not running
if [ $? -ne 0 ]
then
   killall ruby
   $RESTART
   date >> /mobisens/server/log/cron_restart.log
   echo "MobiSens restarted" >> /mobisens/server/log/cron_restart.log
fi