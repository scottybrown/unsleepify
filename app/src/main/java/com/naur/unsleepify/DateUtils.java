package com.naur.unsleepify;

import java.util.Calendar;

public class DateUtils {
    public static Calendar getTimeEightHoursFromNow() {
        return getTimeEightHoursFromTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE));
    }

    public static Calendar getTimeEightHoursFromTime(int hour, int minute) {
        long eightHoursFromNow = hour + 8;

        if (eightHoursFromNow > 23) {
            eightHoursFromNow = eightHoursFromNow - 24;
        }

        return getCalendar((int) eightHoursFromNow, minute);
    }

    public static String getTimeDifferenceString(Calendar alarmTime) {
        Calendar nowWithNoSeconds = Calendar.getInstance();
        nowWithNoSeconds.set(Calendar.SECOND, 0);
        long timeDifferenceInMillis = alarmTime.getTimeInMillis() - nowWithNoSeconds.getTime().getTime();
        long timeDifferenceInMins = timeDifferenceInMillis / (60000);
        long timeDifferenceInHours = timeDifferenceInMins / 60;
        long minutesRemainder = timeDifferenceInMins % 60;
        if (minutesRemainder == 60) {
            minutesRemainder = 0;
            timeDifferenceInHours = timeDifferenceInHours + 1;
        }

        String pluralString = minutesRemainder == 1 ? "" : "s";
        return "Alarm set for " + timeDifferenceInHours + " hours, " + minutesRemainder + " minute" + pluralString + " from now";
    }

    public static void adjustToTomorrowIfBeforeOrEqualCurrentTime(Calendar time) {
        boolean isCurrentHour =
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == time.get(Calendar.HOUR_OF_DAY);
        boolean isCurrentMinute = Calendar.getInstance().get(Calendar.MINUTE) == time.get(Calendar.MINUTE);
        boolean isCurrentTime = isCurrentHour && isCurrentMinute;

        if (Calendar.getInstance().after(time) || isCurrentTime) {
            time.add(Calendar.DATE, 1);
        }
    }

    public static Calendar calculateAlarmTimeOnly() {
        Calendar time = getTimeEightHoursFromNow();
        time.set(Calendar.SECOND, 0);
        return time;
    }

    public static Calendar getCalendar(int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        return cal;
    }

    public static Calendar getCalendar(long existingAlarmInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(existingAlarmInMillis);
        return calendar;
    }
}