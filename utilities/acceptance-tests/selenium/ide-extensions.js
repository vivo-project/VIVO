/* $This file is distributed under the terms of the license in /doc/license.txt$ */

/*
 ******************************************************************************
 *
 * ide-extensions.js
 *
 * Vivo-specific modifications to ßSelenium IDE (1.0.4). 
 *
 * As delivered, the HTML formatter writes comments as actual HTML comments, 
 * so they are invisible when the test file is viewed in a browser.
 *
 * These modifications will change the options in the HTML formatter, so:
 * 1) When writing an HTML test file, comments are written as "comment" 
 *    commands, where the text of the comment is the target of the command.
 * 2) When reading an HTML test file, "comment" commands are parsed as 
 *    comments, so they can be recognized by the IDE editor. Old-style 
 *    comments will also be recognized, for compatibility.
 * 3) When writing an HTML test file, a CSS stylesheet is embedded in the 
 *    heading with styles for the test name and the comments. Class attributes 
 *    are inserted in the test name cell and the comment text cell to apply
 *    these styles.
 *
 * Tests written using these mods must have access to "user-extensions.js" 
 * when running, either in the IDE or in Selenium RC. That file will define
 * "comment" as a command that does nothing.
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
 ******************************************************************************
 */

/*
 * Locate the HTML format object.
 */
var formatCollection = window.editor.app.formats;
var htmlFormat = formatCollection.formats["0"];

/*
 * Ask the HTML format object to load its formatter. The formatter is
 * cached on the first load, so changes to the options will remain 
 * in effect.
 */
htmlFormat.getFormatter();
var formatterOptions = htmlFormat.formatterCache.options;

/*
 * Modify the regular expression that loads commands:
 * 1) The second column of the table must have a simple "<td>" tag with no 
 *    spaces or attributes (to distinguish from comments).
 * 2) Correct some silly syntax in the expression.
 */
formatterOptions["commandLoadPattern"] =
	"<tr.*?>" +
	"\\s*(<!--.*?-->)?" +
	"\\s*<td.*?>\\s*([\\w]*?)\\s*</td>" +
	"\\s*<td>(.*?)</td>" +
	"\\s*(<td.*?/>|<td.*?>(.*?)</td>)" +
	"\\s*</tr>\\s*";
	
/*
 * Modify the regular expression that loads comments:
 * 1) It will load a "comment" command as a comment, not as a command. 
 *    The target of the command is taken as the text of the comment.
 * 2) It will still load old-style comments, for compatibility with 
 *    older tests.
 * This requires a change to the script that loads comments, to accept 
 *   both kinds of comments.
 */
formatterOptions["commentLoadPattern"] =
	"<tr>\\s*" +                        // <tr> tag
	"<td>comment</td>\\s*" +            // "comment" in column 1.
	"<td[^>]*>(.*?)</td>\\s*" +         // text of comment in column 2.
	"<td>\\s*.*?\\s*</td>\\s*" +        // ignore column 3.
	"</tr>\\s*" +                       // </tr> tag
	"|" +                               // -- OR -- 
	"<!--\\s*(.*?)\\s*-->\\s*";         // An HTML comment between table rows.
formatterOptions["commentLoadScript"] =
  "comment.comment = result[1] || result[2];\n",

/*
 * Modify the template for writing an HTML test file:
 * 1) A stylesheet is embedded, for "testName" and "comment" classes.
 * 2) The name of the test (first row) uses the "testName" class.
 */
formatterOptions["testTemplate"] =
  '<?xml version="1.0" encoding="${encoding}"?>\n' +
  '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">\n' +
	'<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">\n' +
	'<head profile="http://selenium-ide.openqa.org/profiles/test-case">\n' +
	'<meta http-equiv="Content-Type" content="text/html; charset=${encoding}" />\n' +
  '<link rel="selenium.base" href="${baseURL}" />\n' +
	"<title>${name}</title>\n" +
	'<style type="text/css">\n' +
	".testName {\n" +
	"  color: blue;\n" +
	"  background: rgb(80%, 80%, 80%);\n" +
	"  font-family: sans-serif;\n" +
	"  font-weight: bold;\n" +
	"  font-size: larger;\n" +
	"  text-align: center;\n" +
	"}\n" +
	".comment {\n" +
	"  color: blue;\n" +
	"  background: rgb(80%, 80%, 80%);\n" +
	"  font-family: sans-serif;\n" +
	"  font-weight: bold;\n" +
	"  font-size: larger;\n" +
	"  text-align: center;\n" +
	"}\n" +
	"</style>\n" +
	"</head>\n" +
	"<body>\n" +
	'<table cellpadding="1" cellspacing="1" border="1">\n'+
	'<thead>\n' +
	'<tr><td rowspan="1" colspan="3" class="testName">${name}</td></tr>\n' +
	"</thead><tbody>\n" +
	"${commands}\n" +
	"</tbody></table>\n" +
	"</body>\n" +
	"</html>\n";

/*
 * Modify the template for writing a comment to the HTML test file:
 * 1) The comment is rendered as a "comment" command.
 */
formatterOptions["commentTemplate"] =
	"<tr>\n" +
	"\t<td>comment</td>\n" +
	'\t<td class="comment">${comment.comment}</td>\n' +
	"\t<td></td>\n" +
	"</tr>\n";
