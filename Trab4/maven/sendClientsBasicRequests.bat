@echo off
title Run client basic requests
start mvn compile exec:java -Dexec.mainClass="com.trab4.Client" -Dexec.args="insert key1 resp1 1 val1"
start mvn compile exec:java -Dexec.mainClass="com.trab4.Client" -Dexec.args="insert key2 resp2 2 val2"
start mvn compile exec:java -Dexec.mainClass="com.trab4.Client" -Dexec.args="insert key3 resp3 3 val3"