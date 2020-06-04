cd /d %~dp0
cd ..
start javaw -cp  gftp-1.2.jar com.study.gftp.server.ConsoleMain conf/gftp-server.properties conf/server-log.properties
pause