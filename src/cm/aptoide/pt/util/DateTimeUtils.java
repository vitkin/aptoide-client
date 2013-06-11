/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt.util;

import android.content.Context;
import android.text.format.DateUtils;
import cm.aptoide.pt.R;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class DateTimeUtils extends DateUtils {

    private static String mTimestampLabelYesterday;
    private static String mTimestampLabelToday;
    private static String mTimestampLabelJustNow;
    private static String mTimestampLabelMinutesAgo;
    private static String mTimestampLabelHoursAgo;
    private static String mTimestampLabelHourAgo;
	private static Context mCtx;
	private static DateTimeUtils instance;

   /**
    * Singleton contructor, needed to get access to the application context & strings for i18n
    * @param context Context
    * @return DateTimeUtils singleton instanec
    * @throws Exception
    */
    public static DateTimeUtils getInstance(Context context) {
        mCtx = context;
        if (instance == null) {
            instance = new DateTimeUtils();
            mTimestampLabelYesterday = context.getResources().getString(R.string.WidgetProvider_timestamp_yesterday);
            mTimestampLabelToday = context.getResources().getString(R.string.WidgetProvider_timestamp_today);
            mTimestampLabelJustNow = context.getResources().getString(R.string.WidgetProvider_timestamp_just_now);
            mTimestampLabelMinutesAgo = context.getResources().getString(R.string.WidgetProvider_timestamp_minutes_ago);
            mTimestampLabelHoursAgo = context.getResources().getString(R.string.WidgetProvider_timestamp_hours_ago);
            mTimestampLabelHourAgo = context.getResources().getString(R.string.WidgetProvider_timestamp_hour_ago);
        }
        return instance;
    }

   /**
    * Checks if the given date is yesterday.
    *
    * @param date - Date to check.
    * @return TRUE if the date is yesterday, FALSE otherwise.
    */
   public static boolean isYesterday(long date) {

       final Calendar currentDate = Calendar.getInstance();
       currentDate.setTimeInMillis(date);

       final Calendar yesterdayDate = Calendar.getInstance();
       yesterdayDate.add(Calendar.DATE, -1);

       return yesterdayDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) && yesterdayDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR);
   }

   public static String[] weekdays = new DateFormatSymbols().getWeekdays(); // get day names
   public static final long millisInADay = 1000 * 60 * 60 * 24;


   /**
    * Displays a user-friendly date difference string
    * @param timedate Timestamp to format as date difference from now
    * @return Friendly-formatted date diff string
    */
   public String getTimeDiffString(long timedate) {
       Calendar startDateTime = Calendar.getInstance();
       Calendar endDateTime = Calendar.getInstance();
       endDateTime.setTimeInMillis(timedate);
       long milliseconds1 = startDateTime.getTimeInMillis();
       long milliseconds2 = endDateTime.getTimeInMillis();
       long diff = milliseconds1 - milliseconds2;

       long hours = diff / (60 * 60 * 1000);
       long minutes = diff / (60 * 1000);
       minutes = minutes - 60 * hours;
       long seconds = diff / (1000);

       boolean isToday = DateTimeUtils.isToday(timedate);
       boolean isYesterday = DateTimeUtils.isYesterday(timedate);

       if (hours > 0 && hours < 12) {
           return hours==1? String.format(mTimestampLabelHourAgo,hours) : String.format(mTimestampLabelHoursAgo,hours);
       } else if (hours <= 0) {
           if (minutes > 0)
               return String.format(mTimestampLabelMinutesAgo,minutes);
           else {
               return mTimestampLabelJustNow;
           }
       } else if (isToday) {
           return mTimestampLabelToday;
       } else if (isYesterday) {
           return mTimestampLabelYesterday;
       } else if (startDateTime.getTimeInMillis() - timedate < millisInADay * 6) {
           return weekdays[endDateTime.get(Calendar.DAY_OF_WEEK)];
       } else {
           return formatDateTime(mCtx, timedate, DateUtils.FORMAT_NUMERIC_DATE);
       }
   }

}
