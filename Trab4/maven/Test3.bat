@echo off
title test3
:: arguments: server amount / log cleanup interval / heartbeat interval
start mvn compile exec:java -Dexec.mainClass="com.trab4.ServerManager" -Dexec.args="3 50 5"
:: arguments: server amount / timeout
start mvn compile exec:java -Dexec.mainClass="com.trab4.Monitor" -Dexec.args="3 20"
timeout 12
wmic Path win32_process Where "CommandLine like '%%-Dexec.args=\"2%%'" Call Terminate