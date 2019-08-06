/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.controller.harvester;

import java.util.ArrayList;
import java.util.Collections;

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
    public static void runDiff(Object ... args) {
        Diff.main(stringsToArray(args));
    }

    // fetch
    public static void runCSVtoRDF(Object ... args) {
        CSVtoRDF.main(stringsToArray(args));
    }
    public static void runD2RMapFetch(Object ... args) {
        D2RMapFetch.main(stringsToArray(args));
    }
    public static void runJDBCFetch(Object ... args) {
        JDBCFetch.main(stringsToArray(args));
    }
    public static void runNLMJournalFetch(Object ... args) {
        NLMJournalFetch.main(stringsToArray(args));
    }
    public static void runOAIFetch(Object ... args) {
        OAIFetch.main(stringsToArray(args));
    }
    public static void runPubmedFetch(Object ... args) {
        PubmedFetch.main(stringsToArray(args));
    }
    public static void runPubmedHTTPFetch(Object ... args) {
        PubmedHTTPFetch.main(stringsToArray(args));
    }

    // qualify
    public static void runChangeNamespace(Object ... args) {
        ChangeNamespace.main(stringsToArray(args));
    }
    public static void runQualify(Object ... args) {
        Qualify.main(stringsToArray(args));
    }
    public static void runRenameBlankNodes(Object ... args) {
        RenameBlankNodes.main(stringsToArray(args));
    }
    public static void runRenameResources(Object ... args) {
        RenameResources.main(stringsToArray(args));
    }
    public static void runSmush(Object ... args) {
        Smush.main(stringsToArray(args));
    }
    public static void runSplitProperty(Object ... args) {
        SplitProperty.main(stringsToArray(args));
    }

    // score
    public static void runMatch(Object ... args) {
        Match.main(stringsToArray(args));
    }
    public static void runPubmedScore(Object ... args) {
        PubmedScore.main(stringsToArray(args));
    }
    public static void runScore(Object ... args) {
        Score.main(stringsToArray(args));
    }

    // transfer
    public static void runTransfer(Object ... args) {
        Transfer.main(stringsToArray(args));
    }

    // translate
    public static void runGlozeTranslator(Object ... args) {
        GlozeTranslator.main(stringsToArray(args));
    }
    public static void runRunBibutils(Object ... args) {
        RunBibutils.main(stringsToArray(args));
    }
    public static void runSanitizeMODSXML(Object ... args) {
        SanitizeMODSXML.main(stringsToArray(args));
    }
    public static void runSPARQLTranslator(Object ... args) {
        SPARQLTranslator.main(stringsToArray(args));
    }
    public static void runXSLTranslator(Object ... args) {
        XSLTranslator.main(stringsToArray(args));
    }

    // util
    public static void runCSVtoJDBC(Object ... args) {
        CSVtoJDBC.main(stringsToArray(args));
    }
    public static void runDatabaseClone(Object ... args) {
        DatabaseClone.main(stringsToArray(args));
    }
    public static void runMerge(Object ... args) {
        Merge.main(stringsToArray(args));
    }
    public static void runXPathTool(Object ... args) {
        XPathTool.main(stringsToArray(args));
    }
    */

    /**
     * Convenience method to expand the ability to use Java's "..." arg list.  Harvester scripts frequently declare sub-macros,
     * so for example you might have:
     *
     * SCOREINPUT="-i $H2MODEL -ImodelName=$MODELNAME -IdbUrl=$MODELDBURL -IcheckEmpty=$CHECKEMPTY"
     * SCOREDATA="-s $H2MODEL -SmodelName=$SCOREDATANAME -SdbUrl=$SCOREDATADBURL -ScheckEmpty=$CHECKEMPTY"
     * SCOREMODELS="$SCOREINPUT -v $VIVOCONFIG -VcheckEmpty=$CHECKEMPTY $SCOREDATA -t $TEMPCOPYDIR -b $SCOREBATCHSIZE"
     * $Score $SCOREMODELS -AGrantNumber=$EQTEST -WGrantNumber=1.0 -FGrantNumber=$GRANTIDNUM -PGrantNumber=$GRANTIDNUM -n ${BASEURI}grant/
     *
     * In order to mimic this functionality for easy use in Java, this method has been created.  It takes a "..." arg list of Object
     * objects, and returns an array of Strings.  For each object, if it's an array of Strings, each String is added to the output
     * array.  Otherwise, its toString() method is called and that value is added to the output array.
     *
     * It is intended to be used with a combination of String and String[] values, in any arbitrary order.
     *
     * All static Harvester methods in this class take an Object arg list rather than a String arg list, and automatically call
     * this method.
     *
     * @param args an array of objects, which ought to be a combination of String and String[] values, in any arbitrary order
     * @return all the strings put together as one array
     */
    public static String[] stringsToArray(Object ... args) {
        ArrayList<String> allData = new ArrayList<String>();
        for (Object arg : args) {
            if (arg instanceof String[]) {
                String[] array = (String[]) (arg);
                Collections.addAll(allData, array);
            } else {
                allData.add(arg.toString());
            }
        }
        return allData.toArray(new String[allData.size()]);
    }
}

