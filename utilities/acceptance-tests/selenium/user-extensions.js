/* $This file is distributed under the terms of the license in /doc/license.txt$ */

/*
 ******************************************************************************
 *
 * user-extensions.js
 *
 * Vivo-specific modifications to Selenium IDE (1.0.4) and Selenium RC (1.0.2)
 *
 * The file "ide-extensions.js" will modify Selenium IDE so comments are 
 * written as "comment" commands. This makes them visible when the HTML test
 * file is viewed in a browser.
 *
 * This file creates a "comment" command (that does nothing) so the Selenium 
 * engine will not throw an error when running those HTML test files.
 *
 * To install these mods in Selenium IDE:
 * 1) Start the IDE.
 * 2) Go the "Options" menu and select "Options..."
 * 3) Put the path to "user-extensions.js into the 
 *    "Selenium core extensions" field.
 * 4) Put the path to this file "ide-extensions.js" into the 
 *    "Selenium IDE extensions" field.
 * 5) Close the IDE and re-start it.
 *
 * To run Selenium RC with these mods:
 * 1) Add the "-userExtensions [file]" option to the command line, with [file]
 *    containing the path to "user-extension.js".
 *
 * For example: (this should be a single line)
 *    java -jar "C:\Vitro_stuff\Selenium\sauceRC-selenium-server.jar" 
 *         -singleWindow 
 *         -htmlSuite 
 *              "*firefox" 
 *              [URL of Vivo site] 
 *              [path to test suite] 
 *              [path to output file]
 *         -firefoxProfileTemplate [path to filefox profile]
 *         -timeout 600 
 *         -userExtensions [path to user-extensions.js]
 *
 ******************************************************************************
 */

/*
 * Create "comment" as a command that does nothing.
 */
Selenium.prototype.doComment = function(){};
