/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.semservices.util;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.beanutils.Converter;

public class TimestampConverter implements Converter {
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
    public TimestampConverter() {
        this.defaultValue = null;
        this.useDefault = false;
    }

    /**
    * @param defaultValue
    */
   public TimestampConverter(Object defaultValue) {
        this.defaultValue = defaultValue;
        this.useDefault = true;
    }

    /* (non-Javadoc)
    * @see org.apache.commons.beanutils.Converter#convert(java.lang.Class, java.lang.Object)
    */

    @SuppressWarnings("unchecked")
   public Object convert(Class type, Object value) {
        Timestamp ts = (Timestamp) value;
        String s = new String();
        s = new SimpleDateFormat("MMM d, h:mm a").format(ts.getTime());
        return s;
    }

    /**
    * @param time
    * @return
    */
   public String toUnixTime(Object time) {
       Timestamp ts = (Timestamp) time;
       Date date = new Date(ts.getTime());
       Long seconds = date.getTime() / 1000;
       //logger.info("unixtime: " + seconds.toString());
       return seconds.toString();
    }


}
