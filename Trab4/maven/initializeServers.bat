@echo off
title initialize servers
start mvn compile exec:java -Dexec.mainClass="com.trab4.ServerManager" -Dexec.args="5 15"