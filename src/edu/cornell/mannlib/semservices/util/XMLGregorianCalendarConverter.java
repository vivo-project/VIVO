/*

 * $Id: XMLGregorianCalendarConverter.java 28642 2006-10-25 13:41:54Z jdamick $
 *
 * Copyright 2006- Revolution Health Group.  All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Revolution Health Group.  (Confidential Information).
 * You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license
 * agreement you entered into with Revolution Health Group.
 *
 */

package edu.cornell.mannlib.semservices.util;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

public final class XMLGregorianCalendarConverter implements Converter {

   // Constructors

   /**
    * Create a {@link Converter} that will throw a {@link ConversionException}
    * if a conversion error occurs.
    */
   public XMLGregorianCalendarConverter() {
      this.defaultValue = null;
      this.useDefault = false;
   }

   /**
    * Create a {@link Converter} that will return the specified default value if
    * a conversion error occurs.
    *
    * @param defaultValue
    *           The default value to be returned
    */
   public XMLGregorianCalendarConverter(Object defaultValue) {
      this.defaultValue = defaultValue;
      this.useDefault = true;
   }

   // Instance Variables

   /**
    * The default value specified to our Constructor, if any.
    */
   private Object defaultValue = null;

   /**
    * Should we return the default value on conversion errors?
    */
   private boolean useDefault = true;

   // Public Methods

   /**
    * Convert the specified input object into an output object of the specified
    * type.
    *
    * @param type
    *  XMLGregorianCalendar type to which this value should be
    *  converted
    * @param value
    *  The input value to be converted
    *
    * @exception ConversionException
    *  if conversion cannot be performed successfully
    */
   @SuppressWarnings("unchecked")
   public Object convert(Class type, Object value) {

      if (value == null) {
         if (useDefault) {
            return (defaultValue);
         } else {
            throw new ConversionException("No value specified");
         }
      }

      if (value instanceof XMLGregorianCalendar) {
         return (value);
      }

      try {
         return DatatypeFactory.newInstance().newXMLGregorianCalendar(
               value.toString());
      } catch (Exception e) {
         if (useDefault) {
            return (defaultValue);
         } else {
            throw new ConversionException(e);
         }
      }
   }
}
