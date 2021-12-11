@echo off
title kill java processes
start mvn compile exec:java -Dexec.mainClass="com.trab4.ServerManager" -Dexec.args="5 15"
pause
wmic Path win32_process Where "CommandLine like '%%com.trab4.Server%%'" Call Terminate
::wmic Path win32_process Where "name like '%%java.exe%%' AND CommandLine like '%%com.trab4.Server%%'" Call Terminate 
pause