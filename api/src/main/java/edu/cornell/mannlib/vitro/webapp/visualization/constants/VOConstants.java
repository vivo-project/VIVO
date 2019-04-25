/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * This contains the constants related to all the value objects.
 * @author cdtank
 */
public class VOConstants {

	public static final String DEFAULT_ACTIVITY_YEAR = "Unknown";
	public static final String DEFAULT_PUBLICATION_YEAR = "Unknown";
	public static final String DEFAULT_GRANT_YEAR = "Unknown";

	/*
	 * Employee related constants
	 * */
	public static enum EntityClassType {
		ORGANIZATION, PERSON, UNKNOWN
	}

	public static final int NUM_CHARS_IN_YEAR_FORMAT = 4;
	public static final int MINIMUM_PUBLICATION_YEAR = 1800;
	public static final int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);

	@SuppressWarnings("serial")
	public static final List<DateTimeFormatter> POSSIBLE_DATE_TIME_FORMATTERS = new ArrayList<DateTimeFormatter>() {{

		add(ISODateTimeFormat.dateTimeNoMillis());
		add(ISODateTimeFormat.dateHourMinuteSecond());
		add(ISODateTimeFormat.dateTimeParser());

	}};



}
