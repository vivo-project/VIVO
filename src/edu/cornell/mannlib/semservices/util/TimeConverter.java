/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.semservices.util;
import edu.cornell.mannlib.semservices.bo.Time;
import java.util.Date;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class TimeConverter implements Converter {
   @SuppressWarnings("unused")
   private static final Log logger = LogFactory.getLog(TimeConverter.class);

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
     *
     */
   public TimeConverter() {
      this.defaultValue = null;
      this.useDefault = false;
   }

   /**
    * @param defaultValue
    */
   public TimeConverter(Object defaultValue) {
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
    * @param time
    * @return
    */
   public static String toFormattedString(Object time) {
      return time.toString();
   }

   /**
    * @param time
    * @return
    */
   public static String toUnixTime(Object time) {
      Time timeObject = (Time) time;
      Date date = timeObject.getDate();
      Long seconds = date.getTime() / 1000;
      //logger.info("unixtime: " + seconds.toString());
      return seconds.toString();
   }

}
