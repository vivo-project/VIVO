/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.semservices.bo;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A class representing a moment in time. Extends Day which represents the day
 * of the moment, and defines the time within the day to millisecond accuracy.
 *
 * @author Jacob Dreyer (<a
 *         href="mailto:jacob.dreyer@geosoft.no">jacob.dreyer@geosoft.no</a>)
 */
public class Time extends Day {
   protected final Log logger = LogFactory.getLog(getClass());

   protected static final Log staticlogger = LogFactory.getLog(Time.class);

   /**
    * Instantiate a Time object. The time is lenient meaning that illegal day
    * parameters can be specified and results in a recomputed day with legal
    * month/day values.
    *
    * @param year  Year of this time
    * @param month Month of this time
    * @param dayOfMonth Day of month of this time.
    * @param hourOfDay Hours of this time [0-23]
    * @param minutes Minutes of this time [0-23]
    * @param seconds Seconds of this time [0-23]
    */
   public Time(int year, int month, int dayOfMonth, int hourOfDay, int minutes,
         int seconds) {
      super(year, month, dayOfMonth);
      setHourOfDay(hourOfDay);
      setMinutes(minutes);
      setSeconds(seconds);
   }

   /**
    * Constructor
    * @param day
    * @param hourOfDay
    * @param minutes
    * @param seconds
    */
   public Time(Day day, int hourOfDay, int minutes, int seconds) {
      this(day.getYear(), day.getMonth(), day.getDayOfMonth(), hourOfDay,
            minutes, seconds);
   }


   /**
    * Constructor
    * @param hourOfDay
    * @param minutes
    * @param seconds
    */
   public Time(int hourOfDay, int minutes, int seconds) {
      this(new Day(), hourOfDay, minutes, seconds);
   }

   /**
    * Constructor
    */
   public Time() {
      calendar_ = new GregorianCalendar(); // Now
   }

   // end of constructors

   // Get Methods

   public Date getDay() {
      return calendar_.getTime();
   }

   public int getHour() {
      return calendar_.get(Calendar.HOUR);
   }

   public int getHourOfDay() {
      return calendar_.get(Calendar.HOUR_OF_DAY);
   }

   public int getMinutes() {
      return calendar_.get(Calendar.MINUTE);
   }

   public int getSeconds() {
      return calendar_.get(Calendar.SECOND);
   }

   public int getMilliSeconds() {
      return calendar_.get(Calendar.MILLISECOND);
   }

   public int getAmPm() {
      return calendar_.get(Calendar.AM_PM);
   }

   // set Methods

   public void setDay(Day day) {
      setYear(day.getYear());
      setMonth(day.getMonth());
      setDayOfMonth(day.getDayOfMonth());
   }

   public void setYear(int year) {
      calendar_.set(Calendar.YEAR, year);
   }

   public void setMonth(int month) {
      calendar_.set(Calendar.MONTH, month);
   }

   public void setDayOfMonth(int dayOfMonth) {
      calendar_.set(Calendar.DAY_OF_MONTH, dayOfMonth);
   }

   public void setHourOfDay(int hourOfDay) {
      calendar_.set(Calendar.HOUR_OF_DAY, hourOfDay);
   }

   public void setHour(int hour) {
      calendar_.set(Calendar.HOUR, hour);
   }

   public void setAmPm(int amPm) {
      calendar_.set(Calendar.AM_PM, amPm);
   }

   public void setMinutes(int minutes) {
      calendar_.set(Calendar.MINUTE, minutes);
   }

   public void setSeconds(int seconds) {
      calendar_.set(Calendar.SECOND, seconds);
   }

   public void setMilliSeconds(int milliSeconds) {
      calendar_.set(Calendar.MILLISECOND, milliSeconds);
   }

   // Time modification methods

   public void addHours(int nHours) {
      calendar_.add(Calendar.HOUR_OF_DAY, nHours);
   }

   public void addMinutes(int nMinutes) {
      calendar_.add(Calendar.MINUTE, nMinutes);
   }

   public void addSeconds(int nSeconds) {
      calendar_.add(Calendar.SECOND, nSeconds);
   }

   public void addMilliSeconds(int nMilliSeconds) {
      calendar_.add(Calendar.MILLISECOND, nMilliSeconds);
   }

   public void subtractHours(int nHours) {
      addHours(-nHours);
   }

   public void subtractMinutes(int nMinutes) {
      addMinutes(-nMinutes);
   }

   public void subtractSeconds(int nSeconds) {
      addSeconds(-nSeconds);
   }

   // Time test methods

   public boolean isAfter(Time time) {
      return calendar_.after(time.calendar_);
   }

   public boolean isBefore(Time time) {
      return calendar_.before(time.calendar_);
   }

   public boolean equals(Time time) {
      return calendar_.equals(time.calendar_);
   }

   // Time difference methods

   public long milliSecondsBetween(Time time) {
      long millisBetween = calendar_.getTime().getTime()
            - time.calendar_.getTime().getTime();
      return millisBetween;
   }

   public double secondsBetween(Time time) {
      long millisBetween = calendar_.getTime().getTime()
            - time.calendar_.getTime().getTime();
      return millisBetween / 1000;
   }

   public double minutesBetween(Time time) {
      long millisBetween = calendar_.getTime().getTime()
            - time.calendar_.getTime().getTime();
      return millisBetween / (1000 * 60);
   }

   public double hoursBetween(Time time) {
      long millisBetween = calendar_.getTime().getTime()
            - time.calendar_.getTime().getTime();
      return millisBetween / (1000 * 60 * 60);
   }

   // Display methods

   public String toString() {
      StringBuffer string = new StringBuffer();

      if (getHour() == 0) {
         string.append("12"); // display "12" for midnight
      } else {
         string.append(getHour());
      }
      string.append(':');
      if (getMinutes() < 10)
         string.append('0');
      string.append(getMinutes());

      if (getAmPm() == Calendar.AM) {
         string.append(" AM");
      } else {
         string.append(" PM");
      }
      return string.toString();
   }

   // Misc. methods

   public static Time getTimeFromSqlTime(java.sql.Time sqlTime) {
      // staticlogger.info("sqlTime "+ sqlTime.toString());
      long ms = sqlTime.getTime();
      java.util.Calendar gcal = GregorianCalendar.getInstance();
      gcal.setTime(new Date(ms));
      Time time = new Time(gcal.get(Calendar.HOUR_OF_DAY), gcal
            .get(Calendar.MINUTE), gcal.get(Calendar.SECOND));
      return time;
   }

   public static void main(String args[]) {
      Time time = new Time(12, 00, 00);
      System.out.println(time);
   }
}
