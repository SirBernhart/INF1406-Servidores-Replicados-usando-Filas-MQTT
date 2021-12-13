@echo off
title test2
:: arguments: server amount / log cleanup interval / heartbeat interval
start mvn compile exec:java -Dexec.mainClass="com.trab4.ServerManager" -Dexec.args="3 50 5"
:: arguments: server amount / timeout
start mvn compile exec:java -Dexec.mainClass="com.trab4.Monitor" -Dexec.args="3 20"
timeout 8
start mvn compile exec:java -Dexec.mainClass="com.trab4.Client" -Dexec.args="insert key3 resp1 1 val1"
timeout 4
start mvn compile exec:java -Dexec.mainClass="com.trab4.Client" -Dexec.args="consult key3 resp2 2"