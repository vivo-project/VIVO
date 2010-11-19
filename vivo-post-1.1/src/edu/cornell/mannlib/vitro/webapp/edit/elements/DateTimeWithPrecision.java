/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.elements;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditConfiguration;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditSubmission;
import freemarker.template.Configuration;

/**
 * This is intended to work in conjunction with a tempalte to create the HTML for a 
 * datetime with precision and to convert he submitted parameters into 
 * varname -> Literal and varname -> URI maps.
 */
public class DateTimeWithPrecision extends BaseEditElement {
    private static final Log log = LogFactory.getLog(DateTimeWithPrecision.class);
    private String TEMPATE_NAME = "DateTimeWithPrecision.ftl";
    protected  String[] PRECISIONS = {
            "http://bogus.com/precision/none",
            "http://bogus.com/precision/year",
            "http://bogus.com/precision/month",
            "http://bogus.com/precision/day",
            "http://bogus.com/precision/hour",
            "http://bogus.com/precision/minute",
            "http://bogus.com/precision/second" };
    
    @Override
    public String draw(String fieldName, EditConfiguration editConfig,
            EditSubmission editSub, Configuration fmConfig) {                        
        Map map = getMapForTemplate(fieldName, editConfig, editSub);
        map.putAll( FreemarkerHttpServlet.getDirectives());        
        return merge( fmConfig, TEMPATE_NAME, map);
    }    
    
    /**
     * This produces a map for use in the template.
     */
    private Map getMapForTemplate(String fieldName, EditConfiguration editConfig, EditSubmission editSub) {              
        Map<String,Object>map = new HashMap<String,Object>();       
        
        map.put("fieldName", fieldName);
        
        DateTime value = getTimeValue(fieldName,editConfig,editSub);
        map.put("year", Integer.toString(value.getYear()));
        map.put("month", Integer.toString(value.getMonthOfYear()));
        map.put("day", Integer.toString(value.getDayOfMonth()) );
        map.put("hour", Integer.toString(value.getHourOfDay()) );
        map.put("minute", Integer.toString(value.getMinuteOfHour()) );
        map.put("second", Integer.toString(value.getSecondOfMinute() )) ;
               
        map.put("precision", getPrecision(fieldName,editConfig,editSub));
        
        //maybe we should put in empty validation errors to show what they would be?
        //ex: map.put("year.error","");
        
        return map;
    }
   
    
    private String getPrecision(String fieldName,
            EditConfiguration editConfig, EditSubmission editSub) {
        // TODO Auto-generated method stub
        return "http://bogus.precision.uri.com/bogus";
    }

    private DateTime getTimeValue(String fieldName, EditConfiguration editConfig,
            EditSubmission editSub) {
        return new DateTime();
    }

    /**
     * This gets the literals for a submitted form from the queryParmeters. 
     * It will only be called if getValidationErrors() doesn't return any errors.
     */
    @Override
    public Map<String, Literal> getLiterals(String fieldName,
            EditConfiguration editConfig, Map<String, String[]> queryParameters) {         
        Map<String,Literal> literalMap = new HashMap<String,Literal>();
       
        Literal datetime =getDateTime( fieldName, queryParameters);
        literalMap.put(fieldName+".value", datetime);
        
        return literalMap;
    }
    
    protected Literal getDateTime(String fieldName,
            Map<String, String[]> queryParameters) {
        Integer year = parseToInt(fieldName+".year", queryParameters);
        Integer month = parseToInt(fieldName+".month", queryParameters);
        if( month == null || month == 0 ) 
            month = 1;        
        Integer day = parseToInt(fieldName+".day", queryParameters);
        if( day == null || day == 0 )
            day = 1;
        Integer hour = parseToInt(fieldName+".hour", queryParameters);
        if( hour == null )
            hour = 0;
        Integer minute = parseToInt(fieldName+".minute", queryParameters);
        if( minute == null )
            minute = 0;
        Integer second = parseToInt(fieldName+".second", queryParameters);
        if( second == null )
            second = 0;
        int mills = 0;
        
        
        DateTime value = new DateTime(
                year.intValue(),month.intValue(),day.intValue(),
                hour.intValue(),minute.intValue(),second.intValue(),mills);
        
        Date dValue = value.toDate();
        
        /*This isn't doing what I want it to do.  It is recording the correct instance of timeb
         * but it is recording it with the timezone UTC/zulu */          
        //return ResourceFactory.createTypedLiteral(ISODateTimeFormat.dateTimeNoMillis().print(value),XSDDatatype.XSDdateTime);
         
        Calendar c = Calendar.getInstance();
        c.setTime(value.toDate());        
        
        Model m = ModelFactory.createDefaultModel();
        Literal lit = m.createTypedLiteral( c );  
        return lit;
    }

    /**
     * This gets the URIs for a submitted form from the queryParmeters. 
     * It will only be called if getValidationErrors() doesn't return any errors.
     */
    @Override
    public Map<String, String> getURIs(String fieldName,
            EditConfiguration editConfig, Map<String, String[]> queryParameters) {                                
        String precisionUri;
        try {
            precisionUri = getSubmittedPrecision( fieldName, queryParameters);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("getURIS() should only be called on input that passed getValidationErrors()");
            return Collections.emptyMap();        
        }
        Map<String,String> uriMap = new HashMap<String,String>();
        uriMap.put(fieldName+".precision", precisionUri);        
        return uriMap;
    }
    
    /**
     * Precision is based on the values returned by the form. Throws an exception with
     * the error message if the queryParameters cannot make a valid date/precision because
     * there are values missing.
     */
    protected String getSubmittedPrecision(String fieldName, 
            Map<String, String[]> queryParameters) throws Exception {
        
        Integer year = parseToInt(fieldName+".year",queryParameters);
        Integer month = parseToInt(fieldName+".month",queryParameters);
        Integer day = parseToInt(fieldName+".day",queryParameters);
        Integer hour  = parseToInt(fieldName+".hour",queryParameters);
        Integer minute = parseToInt(fieldName+".minute",queryParameters);
        Integer second = parseToInt(fieldName+".second",queryParameters);
        Integer[] values = { year, month, day, hour, minute, second };
        
        /*  find the most significant date field that is null. */
        int indexOfFirstNull= -1;        
        for(int i=0; i < values.length ; i++){
            if( values[i] == null ){
                indexOfFirstNull = i;
                break;
            }            
        }
        
        /* if they all had values then we have seconds precision */
        if( indexOfFirstNull == -1 )
            return PRECISIONS[6];
       
        
        /* check that there are no values after the most significant null field 
         * that are non-null. */         
        boolean nonNullAfterFirstNull=false;
        for(int i=0; i < values.length ; i++){
            if( i > indexOfFirstNull && values[i] != null ){
                nonNullAfterFirstNull = true;
                break;
            }
        }
        if( nonNullAfterFirstNull )
            throw new Exception("cannot determine precision, there were filledout values after the first un-filledout value, ");
        else{
           
            return PRECISIONS[ indexOfFirstNull ];
        }
    }
    
    @Override
    public Map<String, String> getValidationMessages(String fieldName,
            EditConfiguration editConfig, Map<String, String[]> queryParameters) {
        Map<String,String> errorMsgMap = new HashMap<String,String>();                               
                
        //check that any parameters we got are single values
        String[] names = {"year","month","day","hour","minute","second", "precision"};
        for( String name:names){            
            if ( !hasNoneOrSingle(fieldName+"."+name, queryParameters))
                errorMsgMap.put(fieldName+"."+name, "must have only one value for " + name);            
        }
                    
        errorMsgMap.putAll(checkDate( fieldName, queryParameters) );
        
        return errorMsgMap; 
    }

    /**
     * This checks for invalid date times like "2010-02-31" or "2010-02-01T99:99:99".
     */
    private Map<String,String> checkDate(String fieldName, Map<String, String[]> queryParameters){
        //TODO
        //see EditSubmission.getDateTime() for an example of checking for valid dates.
        
//        Integer year,month,day,hour,minute,second;
//        
//        year = parseToInt(fieldName+".year", queryParameters);
//        month= parseToInt(fieldName+".month", queryParameters);
//        day = parseToInt(fieldName+".day", queryParameters);
//        hour = parseToInt(fieldName+".hour", queryParameters);
//        minute = parseToInt(fieldName+".minute",queryParameters);
//        second = parseToInt(fieldName+".second", queryParameters);
//        
//        DateTime dateTime = new DateTime();
//        DateTimeFormatter fmt = ISODateTimeFormat.dateParser();
//        

        return Collections.emptyMap();
    }       
    
    private Integer parseToInt(String key,Map<String, String[]> queryParameters){        
        Integer out = null;
        try{
            String[] vt = queryParameters.get(key);
            if( vt == null || vt.length ==0 || vt[0] == null)
                out = null;
            else
                out = Integer.parseInt(vt[0]);
        }catch(IndexOutOfBoundsException iex){
            out =  null;
        }catch(NumberFormatException nfe){
            out =  null;
        }catch(NullPointerException npe){
            out = null;
        }        
        return out;
    }
    
    
//    
//    /**
//     * Create the var->value map for the datetimeprec.ftl template. 
//     */
//    private Map<String, Object> getDateTimePrecMap(String fieldName,
//            EditConfiguration editConfig, EditSubmission editSub) {        
//        Date dateFromLit = null;
//        String dateStrFromLit = null;        
//        
//        if( editSub != null && editSub.getLiteralsFromForm() != null && editSub.getLiteralsFromForm().get(fieldName) != null ){
//            log.debug("found the field " + fieldName + " in the EditSubmission");
//            Literal date = editSub.getLiteralsFromForm().get(fieldName);
//            Object valueFromLiteral = date.getValue();            
//            if( valueFromLiteral != null && valueFromLiteral instanceof Date){
//                dateFromLit = (Date)valueFromLiteral;
//                log.debug("found literal in submission of type Date for field " + fieldName);
//            }else if( valueFromLiteral != null && valueFromLiteral instanceof String){
//                dateStrFromLit = (String) valueFromLiteral;            
//                log.debug("found literal in submission of type String for field " + fieldName);
//            } else if ( valueFromLiteral != null && valueFromLiteral instanceof XSDDateTime) {
//                dateStrFromLit = date.getLexicalForm();
//                log.debug("found existing literal of type XSDDateTime for field " + fieldName);
//            } else {
//               log.error("found a value from the submsission but it was not a String or Date.");
//            }
//        }else if( editConfig != null && editConfig.getLiteralsInScope() != null && editConfig.getLiteralsInScope().containsKey(fieldName)){
//            log.debug( "No EditSubmission found for the field " + fieldName + ", trying to get an existing value");                 
//            Literal date = editConfig.getLiteralsInScope().get(fieldName);
//            Object valueFromLiteral = date.getValue();                               
//            if( valueFromLiteral != null && valueFromLiteral instanceof Date){
//                dateFromLit = (Date)valueFromLiteral;
//                log.debug("found existing literal of type Date for field " + fieldName);
//            }else if( valueFromLiteral != null && valueFromLiteral instanceof String){
//                dateStrFromLit = (String) valueFromLiteral;            
//                log.debug("found exisitng literal of type String for field " + fieldName);
//            } else if ( valueFromLiteral != null && valueFromLiteral instanceof XSDDateTime) {
//                dateStrFromLit = date.getLexicalForm();
//                log.debug("found existing literal of type XSDDateTime for field " + fieldName);
//            } else {
//                log.error("found an existing value from the editConfig but it was not a String or Date:");
//                log.error(valueFromLiteral.getClass().getName());
//            }                
//        }else{
//            log.debug("no value found for field " + fieldName + " in the submission or config, try to get default value");            
//            Field field = editConfig.getField(fieldName);
//            List<List<String>> options = field.getLiteralOptions();
//            if( options.size() >=1 && options.get(0) != null && 
//                    options.get(0).size() >= 1 && options.get(0).get(0) != null){
//                dateStrFromLit = options.get(0).get(0);                
//            }else{                
//                log.debug("no default found for field " + fieldName);
//            }
//        }
//                 
//        DateTime dt = null;
//        if( dateStrFromLit != null){
//            try {
//                /* See:
//                 * http://joda-time.sourceforge.net/api-release/org/joda/time/format/ISODateTimeFormat.html#dateParser() 
//                 * for date format information*/
//                DateTimeFormatter dtFmt = ISODateTimeFormat.dateParser();
//                dt = new DateTime( dtFmt.parseDateTime( dateStrFromLit ));
//            } catch (Exception e) {
//                log.warn("Could not convert '" + dateStrFromLit +"' to DateTime.",e);
//                dt = null;
//            }
//        }else if (dateFromLit != null){
//
//    }
//    
    
}


