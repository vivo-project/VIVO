/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.harvester; 

/* //PLEASE SEE JAVADOC COMMENT FOR CLASS BELOW
import org.vivoweb.harvester.diff.Diff;
import org.vivoweb.harvester.fetch.CSVtoRDF;
import org.vivoweb.harvester.fetch.D2RMapFetch;
import org.vivoweb.harvester.fetch.JDBCFetch;
import org.vivoweb.harvester.fetch.NLMJournalFetch;
import org.vivoweb.harvester.fetch.OAIFetch;
import org.vivoweb.harvester.fetch.PubmedFetch;
import org.vivoweb.harvester.fetch.PubmedHTTPFetch;
import org.vivoweb.harvester.qualify.ChangeNamespace;
import org.vivoweb.harvester.qualify.Qualify;
import org.vivoweb.harvester.qualify.RenameBlankNodes;
import org.vivoweb.harvester.qualify.RenameResources;
import org.vivoweb.harvester.qualify.Smush;
import org.vivoweb.harvester.qualify.SplitProperty;
import org.vivoweb.harvester.score.Match;
import org.vivoweb.harvester.score.PubmedScore;
import org.vivoweb.harvester.score.Score;
import org.vivoweb.harvester.transfer.Transfer;
import org.vivoweb.harvester.translate.GlozeTranslator;
import org.vivoweb.harvester.translate.RunBibutils;
import org.vivoweb.harvester.translate.SPARQLTranslator;
import org.vivoweb.harvester.translate.SanitizeMODSXML;
import org.vivoweb.harvester.translate.XSLTranslator;
import org.vivoweb.harvester.util.CSVtoJDBC;
import org.vivoweb.harvester.util.DatabaseClone;
import org.vivoweb.harvester.util.Merge;
import org.vivoweb.harvester.util.XPathTool;
*/

/**
 * *** NOTE: This is currently completely commented-out until we (all VIVO developers) decide on a policy for
 *           integrating the Harvester with the rest of VIVO.  If Harvester is added to the classpath of VIVO,
 *           this can be uncommented and will work fine. ***

 * Interface to the Harvester.
 *
 * The best approach for doing this is probably a bunch of static methods that call the Harvester main classes.
 * At first I tried to call the execute() methods, using the object parameters rather than the raw string args,
 * but that was troublesome for a few reasons, the most important being related to the simple fact that the
 * Harvester was designed to be used as a collection of command-line tools, and thus we have, for example, the
 * versatility of Score which would be very difficult to replicate without essentially allowing the user to
 * pass in a string to be parsed, which would defeat the purpose.   
 *
 * @author mbarbieri
 *
 */
class Harvester {
/*
    // diff
    public static void runDiff(String ... args) {
        Diff.main(args);
    }

    // fetch
    public static void runCSVtoRDF(String ... args) {
        CSVtoRDF.main(args);
    }
    public static void runD2RMapFetch(String ... args) {
        D2RMapFetch.main(args);
    }
    public static void runJDBCFetch(String ... args) {
        JDBCFetch.main(args);
    }
    public static void runNLMJournalFetch(String ... args) {
        NLMJournalFetch.main(args);
    }
    public static void runOAIFetch(String ... args) {
        OAIFetch.main(args);
    }
    public static void runPubmedFetch(String ... args) {
        PubmedFetch.main(args);
    }
    public static void runPubmedHTTPFetch(String ... args) {
        PubmedHTTPFetch.main(args);
    }
    
    // qualify
    public static void runChangeNamespace(String ... args) {
        ChangeNamespace.main(args);
    }
    public static void runQualify(String ... args) {
        Qualify.main(args);
    }
    public static void runRenameBlankNodes(String ... args) {
        RenameBlankNodes.main(args);
    }
    public static void runRenameResources(String ... args) {
        RenameResources.main(args);
    }
    public static void runSmush(String ... args) {
        Smush.main(args);
    }
    public static void runSplitProperty(String ... args) {
        SplitProperty.main(args);
    }
    
    // score
    public static void runMatch(String ... args) {
        Match.main(args);
    }
    public static void runPubmedScore(String ... args) {
        PubmedScore.main(args);
    }
    public static void runScore(String ... args) {
        Score.main(args);
    }
    
    // transfer
    public static void transfer(String ... args) {
        Transfer.main(args);
    }
    
    // translate
    public static void runGlozeTranslator(String ... args) {
        GlozeTranslator.main(args);
    }
    public static void runRunBibutils(String ... args) {
        RunBibutils.main(args);
    }
    public static void runSanitizeMODSXML(String ... args) {
        SanitizeMODSXML.main(args);
    }
    public static void runSPARQLTranslator(String ... args) {
        SPARQLTranslator.main(args);
    }
    public static void runXSLTranslator(String ... args) {
        XSLTranslator.main(args);
    }

    // util
    public static void runCSVtoJDBC(String ... args) {
        CSVtoJDBC.main(args);
    }
    public static void runDatabaseClone(String ... args) {
        DatabaseClone.main(args);
    }
    public static void runMerge(String ... args) {
        Merge.main(args);
    }
    public static void runXPathTool(String ... args) {
        XPathTool.main(args);
    }
*/
}

