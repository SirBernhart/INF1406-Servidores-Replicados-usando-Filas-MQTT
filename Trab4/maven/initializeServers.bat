@echo off
title initialize servers
:: arguments: server amount / server killing interval / log cleanup interval / heartbeat interval
start mvn compile exec:java -Dexec.mainClass="com.trab4.ServerManager" -Dexec.args="3 100 50 5"
start mvn compile exec:java -Dexec.mainClass="com.trab4.Monitor" -Dexec.args="3 20"