@echo off

:: Run firefox with a profile template - Requires the Sauce RC version of selenium-server.jar
:: Include my user-extensions.js file

SET SEL_OPTS=-singleWindow -timeout 60
SET SEL_OPTS=%SEL_OPTS% -userExtensions "C:\Vitro_stuff\Selenium\user extensions\user-extensions.js"  
SET SEL_OPTS=%SEL_OPTS% -firefoxProfileTemplate "C:\Vitro_stuff\Selenium\experiments\profiles\selenium"
SET SEL_OPTS=%SEL_OPTS% -htmlSuite "*firefox"  "http://localhost:8080/vivo/" "..\suites\user-management\Suite.html" "vivo_output.html"
SET Path=%Path%;C:\Program Files (x86)\Mozilla Firefox

@echo on
java -jar ..\selenium\selenium-server.jar %SEL_OPTS%

