@echo off
title kill specified server
:: mata o servidor com id igual ao número no -Dexec.args=\"
wmic Path win32_process Where "CommandLine like '%%-Dexec.args=\"1%%'" Call Terminate