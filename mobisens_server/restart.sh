#!/bin/bash
cd /mobisens/server
#rm tmp/pids/mongrel.*.pid
sudo /etc/init.d/httpd restart
sudo /etc/init.d/mysqld restart
mongrel_rails cluster::restart