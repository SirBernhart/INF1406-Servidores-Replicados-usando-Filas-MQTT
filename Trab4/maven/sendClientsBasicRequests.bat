@echo off
title Run client basic requests
start mvn compile exec:java -Dexec.mainClass="com.trab4.Client" -Dexec.args="insert testkey resp1 1 testvalue"