/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.elements;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditConfiguration;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditSubmission;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.Field;
import freemarker.template.Configuration;

/**
 * This is intended to work in conjunction with a template to create the HTML for a 
 * datetime with precision and to convert he submitted parameters into 
 * varname -> Literal and varname -> URI maps.
 */
public class DateTimeWithPrecision extends BaseEditElement {
    
    String fieldName;
    
    public DateTimeWithPrecision(Field field) {
        super(field);
        fieldName = field.getName();
    }


    private static final Log log = LogFactory.getLog(DateTimeWithPrecision.class);
    protected String TEMPATE_NAME = "dateTimeWithPrecision.ftl";
    protected static final String vivoCore = "http://vivoweb.org/ontology/core#";
    protected  static final String[] PRECISIONS = {
            vivoCore+"NoPrecision",
            vivoCore+"YearPrecision",
            vivoCore+"YearMonthPrecision",
            vivoCore+"YearMonthDayPrecision",
            vivoCore+"YearMonthDayHourPrecision",
            vivoCore+"YearMonthDayHourMinutePrecision",
            vivoCore+"YearMonthDayTimePrecision"};
    
    protected enum Precision {
        NONE(PRECISIONS[0]),
        YEAR(PRECISIONS[1]),
        MONTH(PRECISIONS[2]),
        DAY(PRECISIONS[3]),
        HOUR(PRECISIONS[4]),
        MINUTE(PRECISIONS[5]),
        SECOND(PRECISIONS[6]);        
        
        private final String URI;
        Precision(String uri){
            URI=uri;
        }
        public String uri(){return URI;}
    }
    
    @Override
    public String draw(String fieldName, EditConfiguration editConfig,
            EditSubmission editSub, Configuration fmConfig) {                        
        Map map = getMapForTemplate( editConfig, editSub);
        map.putAll( FreemarkerHttpServlet.getDirectives());        
        return merge( fmConfig, TEMPATE_NAME, map);
    }    
    
    /**
     * This produces a map for use in the template.
     */
    private Map getMapForTemplate(EditConfiguration editConfig, EditSubmission editSub) {              
        Map<String,Object>map = new HashMap<String,Object>();       
        
        map.put("fieldName", fieldName);
        
        DateTime value = getTimeValue(editConfig,editSub);
        map.put("year", Integer.toString(value.getYear()));
        map.put("month", Integer.toString(value.getMonthOfYear()));
        map.put("day", Integer.toString(value.getDayOfMonth()) );
        map.put("hour", Integer.toString(value.getHourOfDay()) );
        map.put("minute", Integer.toString(value.getMinuteOfHour()) );
        map.put("second", Integer.toString(value.getSecondOfMinute() )) ;
               
        map.put("precision", getPrecision(editConfig,editSub));
        
        //maybe we should put in empty validation errors to show what they would be?
        //ex: map.put("year.error","");
        
        return map;
    }
   
    
    private String getPrecision(EditConfiguration editConfig, EditSubmission editSub) {
        // TODO Auto-generated method stub
        return "http://bogus.precision.uri.com/bogus";
    }

    private DateTime getTimeValue(EditConfiguration editConfig, EditSubmission editSub) {
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
       
        Literal datetime =getDateTime( queryParameters);
        literalMap.put(fieldName+".value", datetime);
        
        return literalMap;
    }
    
    protected Literal getDateTime( Map<String, String[]> queryParameters ) {
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
            precisionUri = getSubmittedPrecision( queryParameters);
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
    protected String getSubmittedPrecision(Map<String, String[]> queryParameters) throws Exception {
        
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
        
        String precisionURI = null;
        try{
            precisionURI = getSubmittedPrecision( queryParameters);
        }catch(Exception ex){            
            errorMsgMap.put(fieldName,ex.getMessage());
            return errorMsgMap;
        }
        
        errorMsgMap.putAll(checkDate( precisionURI,  queryParameters) );
        
        return errorMsgMap; 
    }

    /**
     * This checks for invalid date times.
     */
    final static String NON_INTEGER_YEAR = "must enter a valid year";
    final static String NON_INTEGER_MONTH = "must enter a valid month";
    final static String NON_INTEGER_DAY = "must enter a valid day";
    final static String NON_INTEGER_HOUR = "must enter a valid hour";
    final static String NON_INTEGER_MINUTE = "must enter a valid minute";
    final static String NON_INTEGER_SECOND = "must enter a valid second";
    
    private Map<String,String> checkDate( String precisionURI, Map<String, String[]> qp){
        if( precisionURI == null )
            return Collections.emptyMap();
        
        Map<String,String> errors = new HashMap<String,String>();
        
        Integer year,month,day,hour,minute,second;
                
        //just check if the values for the precision parse to integers
        if( precisionURI.equals( Precision.YEAR.uri() ) ){
            if( ! canParseToNumber(fieldName+".year" ,qp))
                errors.put(fieldName+".year", NON_INTEGER_YEAR);            
        }else if( precisionURI.equals( Precision.MONTH.uri() )){
            if( ! canParseToNumber(fieldName+".year" ,qp))
                errors.put(fieldName+".year", NON_INTEGER_YEAR);
            if( ! canParseToNumber(fieldName+".month" ,qp))
                errors.put(fieldName+".month", NON_INTEGER_MONTH);
        }else if( precisionURI.equals( Precision.DAY.uri() )){
            if( ! canParseToNumber(fieldName+".year" ,qp))
                errors.put(fieldName+".year", NON_INTEGER_YEAR);
            if( ! canParseToNumber(fieldName+".month" ,qp))
                errors.put(fieldName+".month", NON_INTEGER_MONTH);
            if( ! canParseToNumber(fieldName+".day" ,qp))
                errors.put(fieldName+".day", NON_INTEGER_DAY);
        }else if( precisionURI.equals( Precision.HOUR.uri() )){
            if( ! canParseToNumber(fieldName+".year" ,qp))
                errors.put(fieldName+".year", NON_INTEGER_YEAR);
            if( ! canParseToNumber(fieldName+".month" ,qp))
                errors.put(fieldName+".month", NON_INTEGER_MONTH);
            if( ! canParseToNumber(fieldName+".day" ,qp))
                errors.put(fieldName+".day", NON_INTEGER_DAY);
            if( ! canParseToNumber(fieldName+".hour" ,qp))
                errors.put(fieldName+".hour", NON_INTEGER_HOUR);
        }else if( precisionURI.equals( Precision.MINUTE.uri() )){
            if( ! canParseToNumber(fieldName+".year" ,qp))
                errors.put(fieldName+".year", NON_INTEGER_YEAR);
            if( ! canParseToNumber(fieldName+".month" ,qp))
                errors.put(fieldName+".month", NON_INTEGER_MONTH);
            if( ! canParseToNumber(fieldName+".day" ,qp))
                errors.put(fieldName+".day", NON_INTEGER_DAY);
            if( ! canParseToNumber(fieldName+".hour" ,qp))
                errors.put(fieldName+".hour", NON_INTEGER_HOUR);
            if( ! canParseToNumber(fieldName+".minute" ,qp))
                errors.put(fieldName+".minute", NON_INTEGER_HOUR);
        }else if( precisionURI.equals( Precision.SECOND.uri() )){
            if( ! canParseToNumber(fieldName+".year" ,qp))
                errors.put(fieldName+".year", NON_INTEGER_YEAR);
            if( ! canParseToNumber(fieldName+".month" ,qp))
                errors.put(fieldName+".month", NON_INTEGER_MONTH);
            if( ! canParseToNumber(fieldName+".day" ,qp))
                errors.put(fieldName+".day", NON_INTEGER_DAY);
            if( ! canParseToNumber(fieldName+".hour" ,qp))
                errors.put(fieldName+".hour", NON_INTEGER_HOUR);
            if( ! canParseToNumber(fieldName+".minute" ,qp))
                errors.put(fieldName+".minute", NON_INTEGER_HOUR);
            if( ! canParseToNumber(fieldName+".second" ,qp))
                errors.put(fieldName+".second", NON_INTEGER_SECOND);
        }
                       
        //check if we can make a valid date with these integers
        year = parseToInt(fieldName+".year", qp);
        if( year == null ) 
            year = 1999;
        month= parseToInt(fieldName+".month", qp);
        if(month == null )
            month = 1;
        day = parseToInt(fieldName+".day", qp);
        if( day == null )
             day = 1;
        hour = parseToInt(fieldName+".hour", qp);
        if( hour == null )
            hour = 0;
        minute = parseToInt(fieldName+".minute",qp);
        if( minute == null )
            minute = 0;
        second = parseToInt(fieldName+".second", qp);
        if( second == null )
            second = 0;                
                
        DateTime dateTime = new DateTime();
        try{
            dateTime.withYear(year);
        }catch(IllegalArgumentException iae){
           errors.put(fieldName+".year", iae.getLocalizedMessage());   
        }
        try{
            dateTime.withMonthOfYear(month);
        }catch(IllegalArgumentException iae){
            errors.put(fieldName+".month", iae.getLocalizedMessage());
        }
        try{
            dateTime.withDayOfMonth(day);
        }catch(IllegalArgumentException iae){
            errors.put(fieldName+".day", iae.getLocalizedMessage());
        }
        try{
            dateTime.withHourOfDay(hour);
        }catch(IllegalArgumentException iae){
            errors.put(fieldName+".hour", iae.getLocalizedMessage());
        }
        try{
            dateTime.withSecondOfMinute(second);
        }catch(IllegalArgumentException iae){
            errors.put(fieldName+".second", iae.getLocalizedMessage());    
        }       

        return errors;
    }       
    
    
    private boolean fieldMatchesPattern( String fieldName, Map<String,String[]>queryParameters, Pattern pattern){
        String[] varg = queryParameters.get(fieldName);
        if( varg == null || varg.length != 1 || varg[0] == null)
            return false;
        String value = varg[0];
        Matcher match = pattern.matcher(value);
        return match.matches();
    }
    
    private boolean emptyOrBlank(String key,Map<String, String[]> queryParameters){
        String[] vt = queryParameters.get(key);
        return ( vt == null || vt.length ==0 || vt[0] == null || vt[0].length() == 0 );
    }
    
    private boolean canParseToNumber(String key,Map<String, String[]> queryParameters){
        Integer out = null;
        try{
            String[] vt = queryParameters.get(key);
            if( vt == null || vt.length ==0 || vt[0] == null)
                return false;
            else{
                out = Integer.parseInt(vt[0]);
                return true;
            }            
        }catch(IndexOutOfBoundsException iex){
            out =  null;
        }catch(NumberFormatException nfe){
            out =  null;
        }catch(NullPointerException npe){
            out = null;
        }        
        return false;
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
    
}


