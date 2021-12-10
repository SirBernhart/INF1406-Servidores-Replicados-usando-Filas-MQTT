@echo off
title kill java processes
wmic Path win32_process Where "name like '%%java.exe%%' AND CommandLine like '%%Server%%'" Call Terminate
pause