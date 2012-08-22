@echo off
cd %~dp0
call mvn install -Dmaven.test.skip=true

pause