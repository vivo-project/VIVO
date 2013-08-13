/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.semservices.util;



import java.util.Date;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.semservices.bo.Day;

public class DayConverter implements Converter {
   @SuppressWarnings("unused")
   private static final Log logger = LogFactory.getLog(DayConverter.class);
    /**
     * The default value specified to our Constructor, if any.
     */
    @SuppressWarnings("unused")
   private Object defaultValue = null;

    /**
     * Should we return the default value on conversion errors?
     */
    @SuppressWarnings("unused")
   private boolean useDefault = true;


   /**
    * Constructor
    */
   public DayConverter() {
      this.defaultValue = null;
      this.useDefault = false;
   }

   /**
    * Constructor with object
    * @param defaultValue
    */
   public DayConverter(Object defaultValue) {
      this.defaultValue = defaultValue;
      this.useDefault = true;
   }

    /* (non-Javadoc)
    * @see org.apache.commons.beanutils.Converter#convert(java.lang.Class, java.lang.Object)
    */
   @SuppressWarnings("unchecked")
   public Object convert(Class type, Object value) {
        String s = value.toString();
        return s;
    }

    /**
     * Format the output to something reasonable
    * @param day
    * @return
    */
   public static String toFormattedString(Object day) {
       Day dayObject = (Day) day;
       String s = dayObject.getDayName()+
           ", "+dayObject.getMonthName()+
           " "+dayObject.getDayOfMonth()+
           ", "+dayObject.getYear();
       return s;
   }

   public static String toUnixTime(Object day) {
      // get date in milliseconds and divide by zero to return unixtime
      Day dayObject = (Day) day;
      Date date = dayObject.getDate();
      Long seconds = date.getTime() / 1000;
      //logger.info("unixtime: " + seconds.toString());
      return seconds.toString();
   }


}
