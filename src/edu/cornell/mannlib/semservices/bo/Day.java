/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.semservices.bo;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import edu.cornell.mannlib.semservices.util.DateUtils;

/**
 * A time-less and immutable Date class for basic date arithmetics.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
public class Day implements Comparable<Day> {
   /** The back end calendar instance of this day. */
   protected  Calendar calendar_ = Calendar.getInstance();

   /**
    * Create a new day. The day is lenient meaning that illegal day parameters
    * can be specified and results in a recomputed day with legal month/day
    * values.
    *
    * @param year
    *           Year of new day.
    * @param month
    *           Month of new day (0-11)
    * @param dayOfMonth
    *           Day of month of new day (1-31)
    */
   public Day(int year, int month, int dayOfMonth) {
      initialize(year, month, dayOfMonth);
   }

   /**
    * Create a new day, specifying the year and the day of year. The day is
    * lenient meaning that illegal day parameters can be specified and results
    * in a recomputed day with legal month/day values.
    *
    * @param year
    *           Year of new day.
    * @param dayOfYear
    *           1=January 1, etc.
    */
   public Day(int year, int dayOfYear) {
      initialize(year, Calendar.JANUARY, 1);
      calendar_.set(Calendar.DAY_OF_YEAR, dayOfYear);
   }

   /**
    * Create a new day representing the day of creation (according to the
    * setting of the current machine).
    */
   public Day() {
      // Now (in the current locale of the client machine)
      Calendar calendar = Calendar.getInstance();

      // Prune time part
      initialize(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH));
   }

   /**
    * Create a new day based on a java.util.Calendar instance. NOTE: The time
    * component from calendar will be pruned.
    *
    * @param calendar
    *           Calendar instance to copy.
    * @throws IllegalArgumentException
    *            If calendar is null.
    */
   public Day(Calendar calendar) {
      if (calendar == null)
         throw new IllegalArgumentException("calendar cannot be null");

      initialize(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH));
   }

   /**
    * Create a new day based on a java.util.Date instance. NOTE: The time
    * component from date will be pruned.
    *
    * @param date
    *           Date instance to copy.
    * @throws IllegalArgumentException
    *            If date is null.
    */
   public Day(Date date) {
      if (date == null)
         throw new IllegalArgumentException("date cannot be null");

      // Create a calendar based on given date
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);

      // Extract date values and use these only
      initialize(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH));
   }

   /**
    * Create a new day based on a time value. Time is milliseconds since
    * "the Epoch" (1.1.1970). NOTE: The time component from time will be pruned.
    *
    * @param time
    *           Milliseconds since "the Epoch".
    */
   public Day(long time) {
      this(new Date(time));
   }

   /**
    * Create a new day as a copy of the specified day.
    *
    * @param day
    *           Day to clone.
    * @throws IllegalArgumentException
    *            If day is null.
    */
   public Day(Day day) {
      if (day == null)
         throw new IllegalArgumentException("day cannot be null");

      initialize(day.getYear(), day.getMonth(), day.getDayOfMonth());
   }

   /**
    * Initialize the internal calendar instance.
    *
    * @param year
    *           Year of new day.
    * @param month
    *           Month of new day.
    * @param dayOfMonth
    *           Day of month of new day.
    */
   private void initialize(int year, int month, int dayOfMonth) {
      calendar_.setLenient(true);
      calendar_.setFirstDayOfWeek(Calendar.MONDAY);
      calendar_.setTimeZone(TimeZone.getDefault());
      calendar_.set(Calendar.YEAR, year);
      calendar_.set(Calendar.MONTH, month);
      calendar_.set(Calendar.DAY_OF_MONTH, dayOfMonth);

      // Prune the time component
      calendar_.set(Calendar.HOUR_OF_DAY, 0);
      calendar_.set(Calendar.MINUTE, 0);
      calendar_.set(Calendar.SECOND, 0);
      calendar_.set(Calendar.MILLISECOND, 0);
   }

   /**
    * A more explicit front-end to the Day() constructor which return a day
    * object representing the day of creation.
    *
    * @return A day instance representing today.
    */
   public static Day today() {
      return new Day();
   }

   /**
    * Return a Calendar instance representing the same day as this instance. For
    * use by secondary methods requiring java.util.Calendar as input.
    *
    * @return Calendar equivalent representing this day.
    */
   public Calendar getCalendar() {
      return (Calendar) calendar_.clone();
   }

   /**
    * Return a Date instance representing the same date as this instance. For
    * use by secondary methods requiring java.util.Date as input.
    *
    * @return Date equivalent representing this day.
    */
   public Date getDate() {
      return getCalendar().getTime();
   }

   /**
    * Compare this day to the specified day. If object is not of type Day a
    * ClassCastException is thrown.
    *
    * @param day
    *           Day object to compare to.
    * @return @see Comparable#compareTo(Object)
    * @throws IllegalArgumentException
    *            If day is null.
    */
   public int compareTo(Day day) {
      if (day == null)
         throw new IllegalArgumentException("day cannot be null");

      return calendar_.getTime().compareTo(day.calendar_.getTime());
   }

   /**
    * Return true if this day is after the specified day.
    *
    * @param day
    *           Day to compare to.
    * @return True if this is after day, false otherwise.
    * @throws IllegalArgumentException
    *            If day is null.
    */
   public boolean isAfter(Day day) {
      if (day == null)
         throw new IllegalArgumentException("day cannot be null");

      return calendar_.after(day.calendar_);
   }

   /**
    * Return true if this day is before the specified day.
    *
    * @param day
    *           Day to compare to.
    * @return True if this is before day, false otherwise.
    * @throws IllegalArgumentException
    *            If day is null.
    */
   public boolean isBefore(Day day) {
      if (day == null)
         throw new IllegalArgumentException("day cannot be null");

      return calendar_.before(day.calendar_);
   }

   /**
    * Return true if this day equals (represent the same date) as the specified
    * day.
    *
    * @param object
    *           Object to compare to.
    * @return True if this equals day, false otherwise.
    * @throws IllegalArgumentException
    *            If day is null.
    */
   @Override
   public boolean equals(Object object) {
      Day day = (Day) object;

      if (day == null)
         throw new IllegalArgumentException("day cannot be null");

      return calendar_.equals(day.calendar_);
   }

   /**
    * Overload required as default definition of equals() has changed.
    *
    * @return A hash code value for this object.
    */
   @Override
   public int hashCode() {
      return calendar_.hashCode();
   }

   /**
    * Return year of this day.
    *
    * @return Year of this day.
    */
   public int getYear() {
      return calendar_.get(Calendar.YEAR);
   }



   /**
    * Return month of this day. The result must be compared to Calendar.JANUARY,
    * Calendar.FEBRUARY, etc.
    *
    * @return Month of this day.
    */
   public int getMonth() {
      return calendar_.get(Calendar.MONTH);
   }

   public String getMonthName() {
      switch (getMonth()) {
      case Calendar.JANUARY:
         return "January";
      case Calendar.FEBRUARY:
         return "February";
      case Calendar.MARCH:
         return "March";
      case Calendar.APRIL:
         return "April";
      case Calendar.MAY:
         return "May";
      case Calendar.JUNE:
         return "June";
      case Calendar.JULY:
         return "July";
      case Calendar.AUGUST:
         return "August";
      case Calendar.SEPTEMBER:
         return "September";
      case Calendar.OCTOBER:
         return "October";
      case Calendar.NOVEMBER:
         return "November";
      case Calendar.DECEMBER:
         return "December";
      default:
         assert false : "Invalid mongth: " + getMonth();
      }

      // This will never happen
      return "";
   }

   /**
    * Return the 1-based month number of the month of this day. 1 = January, 2 =
    * February and so on.
    *
    * @return Month number of this month
    */
   public int getMonthNo() {
      // It is tempting to return getMonth() + 1 but this is conceptually
      // wrong, as Calendar month is an enumeration and the values are tags
      // only and can be anything.
      switch (getMonth()) {
      case Calendar.JANUARY:
         return 1;
      case Calendar.FEBRUARY:
         return 2;
      case Calendar.MARCH:
         return 3;
      case Calendar.APRIL:
         return 4;
      case Calendar.MAY:
         return 5;
      case Calendar.JUNE:
         return 6;
      case Calendar.JULY:
         return 7;
      case Calendar.AUGUST:
         return 8;
      case Calendar.SEPTEMBER:
         return 9;
      case Calendar.OCTOBER:
         return 10;
      case Calendar.NOVEMBER:
         return 11;
      case Calendar.DECEMBER:
         return 12;
      default:
         assert false : "Invalid mongth: " + getMonth();
      }

      // This will never happen
      return 0;
   }

   /**
    * Return day of month of this day. NOTE: First day of month is 1 (not 0).
    *
    * @return Day of month of this day.
    */
   public int getDayOfMonth() {
      return calendar_.get(Calendar.DAY_OF_MONTH);
   }

   /**
    * Return the day number of year this day represents. January 1 = 1 and so
    * on.
    *
    * @return day number of year.
    */
   public int getDayOfYear() {
      return calendar_.get(Calendar.DAY_OF_YEAR);
   }

   /**
    * Return the day of week of this day. NOTE: Must be compared to
    * Calendar.MONDAY, TUSEDAY etc.
    *
    * @return Day of week of this day.
    */
   public int getDayOfWeek() {
      return calendar_.get(Calendar.DAY_OF_WEEK);
   }

   /**
    * Return the day number of week of this day, where Monday=1, Tuesday=2, ...
    * Sunday=7.
    *
    * @return Day number of week of this day.
    */
   public int getDayNumberOfWeek() {
      return getDayOfWeek() == Calendar.SUNDAY ? 7 : getDayOfWeek()
            - Calendar.SUNDAY;
   }

   /**
    * Return the week number of year, this day belongs to. 1st=1 and so on.
    *
    * @return Week number of year of this day.
    */
   public int getWeekOfYear() {
      return calendar_.get(Calendar.WEEK_OF_YEAR);
   }

   /**
    * Return a day which is the given number of days after this day.
    *
    * @param nDays
    *           Number of days to add. May be negative.
    * @return Day as requested.
    */
   public Day addDays(int nDays) {
      // Create a clone
      Calendar calendar = (Calendar) calendar_.clone();

      // Add/remove the specified number of days
      calendar.add(Calendar.DAY_OF_MONTH, nDays);

      // Return new instance
      return new Day(calendar);
   }

   /**
    * Subtract a number of days from this day.
    *
    * @param nDays
    *           Number of days to subtract.
    * @return Day as requested.
    */
   public Day subtractDays(int nDays) {
      return addDays(-nDays);
   }

   /**
    * Return a day wich is a given number of month after this day.
    *
    * The actual number of days added depends on the staring day. Subtracting a
    * number of months can be done by a negative argument to addMonths() or
    * calling subtactMonths() explicitly. NOTE: addMonth(n) m times will in
    * general give a different result than addMonth(m*n). Add 1 month to January
    * 31, 2005 will give February 28, 2005.
    *
    * @param nMonths
    *           Number of months to add.
    * @return Day as requested.
    */
   public Day addMonths(int nMonths) {
      // Create a clone
      Calendar calendar = (Calendar) calendar_.clone();

      // Add/remove the specified number of days
      calendar.add(Calendar.MONTH, nMonths);

      // Return new instance
      return new Day(calendar);
   }

   /**
    * Subtract a number of months from this day.
    *
    * @see #addMonths(int).
    *
    * @param nMonths
    *           Number of months to subtract.
    * @return Day as requested.
    */
   public Day subtractMonths(int nMonths) {
      return addMonths(-nMonths);
   }

   /**
    * Return a day wich is a given number of years after this day.
    *
    * Add a number of years to this day. The actual number of days added depends
    * on the starting day. Subtracting a number of years can be done by a
    * negative argument to addYears() or calling subtractYears explicitly.
    *
    * @param nYears
    *           Number of years to add.
    * @return Day as requested.
    */
   public Day addYears(int nYears) {
      // Create a clone
      Calendar calendar = (Calendar) calendar_.clone();

      // Add/remove the specified number of days
      calendar.add(Calendar.YEAR, nYears);

      // Return new instance
      return new Day(calendar);
   }

   /**
    * Subtract a number of years from this day.
    *
    * @see #addYears(int).
    *
    * @param nYears
    *           Number of years to subtract.
    * @return Day as requested.
    */
   public Day subtractYears(int nYears) {
      return addYears(-nYears);
   }

   /**
    * Return the number of days in the year of this day.
    *
    * @return Number of days in this year.
    */
   public int getDaysInYear() {
      return calendar_.getActualMaximum(Calendar.DAY_OF_YEAR);
   }

   /**
    * Return true if the year of this day is a leap year.
    *
    * @return True if this year is a leap year, false otherwise.
    */
   public boolean isLeapYear() {
      return getDaysInYear() == calendar_.getMaximum(Calendar.DAY_OF_YEAR);
   }

   /**
    * Return true if the specified year is a leap year.
    *
    * @param year
    *           Year to check.
    * @return True if specified year is leap year, false otherwise.
    */
   public static boolean isLeapYear(int year) {
      return (new Day(year, Calendar.JANUARY, 1)).isLeapYear();
   }

   /**
    * Return the number of days in the month of this day.
    *
    * @return Number of days in this month.
    */
   public int getDaysInMonth() {
      return calendar_.getActualMaximum(Calendar.DAY_OF_MONTH);
   }



   /**
    * Get default locale name of this day ("Monday", "Tuesday", etc.
    *
    * @return Name of day.
    */
   public String getDayName() {
      switch (getDayOfWeek()) {
      case Calendar.MONDAY:
         return "Monday";
      case Calendar.TUESDAY:
         return "Tuesday";
      case Calendar.WEDNESDAY:
         return "Wednesday";
      case Calendar.THURSDAY:
         return "Thursday";
      case Calendar.FRIDAY:
         return "Friday";
      case Calendar.SATURDAY:
         return "Saturday";
      case Calendar.SUNDAY:
         return "Sunday";
      default:
         assert false : "Invalid day of week: " + getDayOfWeek();
      }

      // This will never happen
      return null;
   }

   /**
    * Get default locale name of this day ("Monday", "Tuesday", etc.
    *
    * @return Name of day.
    */
   public String getShortDayName() {
      switch (getDayOfWeek()) {
      case Calendar.MONDAY:
         return "Mon";
      case Calendar.TUESDAY:
         return "Tue";
      case Calendar.WEDNESDAY:
         return "Wed";
      case Calendar.THURSDAY:
         return "Thu";
      case Calendar.FRIDAY:
         return "Fri";
      case Calendar.SATURDAY:
         return "Sat";
      case Calendar.SUNDAY:
         return "Sun";
      default:
         assert false : "Invalid day of week: " + getDayOfWeek();
      }

      // This will never happen
      return null;
   }

   /**
    * Return number of days between two days. The method always returns a
    * positive number of days.
    *
    * @param day
    *           The day to compare to.
    * @return Number of days between this and day.
    * @throws IllegalArgumentException
    *            If day is null.
    */
   public int daysBetween(Day day) {
      if (day == null)
         throw new IllegalArgumentException("day cannot be null");

      long millisBetween = Math.abs(calendar_.getTime().getTime()
            - day.calendar_.getTime().getTime());
      return (int) Math.round(millisBetween / (1000 * 60 * 60 * 24));
   }

   /**
    * Find the n'th xxxxday of s specified month (for instance find 1st sunday
    * of May 2006; findNthOfMonth (1, Calendar.SUNDAY, Calendar.MAY, 2006);
    * Return null if the specified day doesn't exists.
    *
    * @param n
    *           Nth day to look for.
    * @param dayOfWeek
    *           Day to look for (Calendar.XXXDAY).
    * @param month
    *           Month to check (Calendar.XXX).
    * @param year
    *           Year to check.
    * @return Required Day (or null if non-existent)
    * @throws IllegalArgumentException
    *            if dyaOfWeek parameter doesn't represent a valid day.
    */
   public static Day getNthOfMonth(int n, int dayOfWeek, int month, int year) {
      // Validate the dayOfWeek argument

      if (dayOfWeek < 1 || dayOfWeek > 7)
         throw new IllegalArgumentException("Invalid day of week: " + dayOfWeek);

      Day first = new Day(year, month, 1);

      int offset = dayOfWeek - first.getDayOfWeek();
      if (offset < 0)
         offset = 7 + offset;

      int dayNo = (n - 1) * 7 + offset + 1;

      return dayNo > first.getDaysInMonth() ? null
            : new Day(year, month, dayNo);
   }

   /**
    * Find the first of a specific day in a given month. For instance first
    * Tuesday of May: getFirstOfMonth(Calendar.TUESDAY, Calendar.MAY, 2005);
    *
    * @param dayOfWeek
    *           Weekday to get.
    * @param month
    *           Month of day to get.
    * @param year
    *           Year of day to get.
    * @return The requested day.
    */
   public static Day getFirstOfMonth(int dayOfWeek, int month, int year) {
      return Day.getNthOfMonth(1, dayOfWeek, month, year);
   }

   /**
    * Find the last of a specific day in a given month. For instance last
    * Tuesday of May: getLastOfMonth (Calendar.TUESDAY, Calendar.MAY, 2005);
    *
    * @param dayOfWeek
    *           Weekday to get.
    * @param month
    *           Month of day to get.
    * @param year
    *           Year of day to get.
    * @return The requested day.
    */
   public static Day getLastOfMonth(int dayOfWeek, int month, int year) {
      Day day = Day.getNthOfMonth(5, dayOfWeek, month, year);
      return day != null ? day : Day.getNthOfMonth(4, dayOfWeek, month, year);
   }

   public String getFormattedString(String fmt) {
      return DateUtils.getFormattedDate(calendar_.getTime(), fmt);
   }

   /**
    * Return a scratch string representation of this day. Used for debugging
    * only. The format of the day is yyyy-mm-dd
    *
    * @return A string representation of this day.
    */
   @Override
   public String toString() {
      StringBuffer s = new StringBuffer();
      s.append(getYear());
      s.append('-');
      if (getMonth() < 9)
         s.append('0');
      s.append(getMonth() + 1);
      s.append('-');
      if (getDayOfMonth() < 10)
         s.append('0');
      s.append(getDayOfMonth());
      return s.toString();
   }

   /**
    * Testing this class.
    *
    * @param arguments
    *           Not used.
    */
   public static void main(String[] arguments) {
      // This proves that there are 912 days between the two major
      // terrorist attacks, not 911 as is common knowledge.
      Day september11 = new Day(2001, Calendar.SEPTEMBER, 11);
      Day march11 = new Day(2004, Calendar.MARCH, 11);
      System.out.println(september11.daysBetween(march11));

      // This proves that Kennedy was president for 1037 days,
      // not 1000 as is the popular belief nor 1036 which is the
      // bluffers reply. Nerds knows when to add one...
      Day precidency = new Day(1961, Calendar.JANUARY, 20);
      Day assasination = new Day(1963, Calendar.NOVEMBER, 22);
      System.out.println(precidency.daysBetween(assasination) + 1);

      // Niel Armstrong walked the moon on a Sunday
      Day nielOnMoon = new Day(1969, Calendar.JULY, 20);
      System.out.println("Niel Armstron walked on the moon on "+ nielOnMoon.getDayName());

      // Find last tuesdays for 2005
      for (int i = 0; i < 12; i++) {
         Day tuesday = Day.getLastOfMonth(Calendar.TUESDAY, i, 2005);
         System.out.println(tuesday);
      }
   }
}
