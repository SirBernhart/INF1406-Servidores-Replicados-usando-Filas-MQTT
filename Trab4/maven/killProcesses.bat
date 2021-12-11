@echo off
title kill java processes
wmic Path win32_process Where "CommandLine like '%%com.trab4.Server%%'" Call Terminate
::wmic Path win32_process Where "name like '%%java.exe%%' AND CommandLine like '%%com.trab4.Server%%'" Call Terminate 
pause