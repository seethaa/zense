#!/bin/bash
sudo /etc/init.d/httpd start
sudo /etc/init.d/mysqld start
cd /mobisens/server
#rm tmp/pids/mongrel.*.pid
mongrel_rails cluster::start