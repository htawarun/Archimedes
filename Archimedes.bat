@echo on

REM $Id: Archimedes.bat,v 1.1 2003/07/31 11:55:34 tband Exp tband $

REM Batch file to run the Versata automation analyzer

setlocal

REM Set these properties to your local of the JDK, Versata 5.5 and/or WebSphere Appserver
REM set JAVA_HOME=c:\jdk1.3.1
set VERSATA_HOME=c:\projects\Versata
set WS_HOME=c:\WebSphere\AppServer


REM Use this classpath to connect to Versata WebSphere 5.5.x (remove the following REM and REM the CORBA line)
REM set CLASSPATH=.\libs\AutomationAnalyzer.jar;.\libs\BandSoftware.jar;.\classes;.\libs\AutomationAnalyzerHelp.jar;.\libs\dcxjp.zip;.\libs\jh.jar;.\lib\xalan.jar;.\lib\xerces.jar;%VERSATA_HOME%\client\lib\vfcEJB51.jar;%VERSATA_HOME%\vls\lib\vlsEJB51.jar;%VERSATA_HOME%\VlsComponents\Classes;;%VERSATA_HOME%\vls\lib\vlsBeans51.jar;%VERSATA_HOME%\client\lib

REM Use this classpath to connect to Versata CORBA 5.5.3 or higher
set CP=.\;.\libs\AutomationAnalyzer.jar;.\libs\BandSoftware.jar;.\classes;.\libs\AutomationAnalyzerHelp.jar;.\libs\dcxjp.zip;.\libs\jh.jar;.\lib\xalan.jar;.\lib\xerces.jar;
REM set CP=%CP%;VERSATA_HOME%\client\lib\vfcORB55.jar;%VERSATA_HOME%\client\lib
set CP=%CP%;VERSATA_HOME%\vls\lib\vlsORB55.jar;%VERSATA_HOME%\vls\lib
REM set CP=%CP%;%VERSATA_HOME%\Orb\lib\vbdev.jar;%VERSATA_HOME%\Orb\lib\vbjorb.jar;%VERSATA_HOME%\Orb\lib\vbjdev.jar;%VERSATA_HOME%\VlsComponents\Classes;%JAVA_HOME%\jre\lib\rt.jar;%JAVA_HOME%\lib\tools.jar;%JAVA_HOME%\lib\i8n.jar
set CLASSPATH=%CP%
"%JAVA_HOME%"\bin\java -Xms256m -Xmx512m com.versata.automationanalyzer.AutomationAnalyzer %1 %2
pause


