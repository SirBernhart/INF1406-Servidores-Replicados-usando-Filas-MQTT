@echo off
title Run client basic requests
start mvn compile exec:java -Dexec.mainClass="com.trab4.Client" -Dexec.args="insert key1 resp1 1 val1"
pause
start mvn compile exec:java -Dexec.mainClass="com.trab4.Client" -Dexec.args="consult key1 resp2 2"