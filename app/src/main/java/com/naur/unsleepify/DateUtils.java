package com.naur.unsleepify;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class DateUtils {
    public static LocalTime getTimeEightHoursFromNow() {
        return getTimeEightHoursFromTime(LocalTime.now().getHour(), LocalTime.now().getMinute());
    }

    public static LocalTime getTimeEightHoursFromTime(int hour, int minute) {
        long eightHoursFromNow = hour + 8;

        if (eightHoursFromNow > 23) {
            eightHoursFromNow = eightHoursFromNow - 24;
        }

        return getTime((int) eightHoursFromNow, minute);
    }

    public static String getTimeDifferenceString(LocalDateTime dateTime) {
        LocalDateTime nowWithNoSeconds = LocalDateTime.now().withSecond(0);
        long timeDifferenceInMillis = DateUtils.getMillis(dateTime) - DateUtils.getMillis(nowWithNoSeconds);
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

    public static long getMillis(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDateTime adjustToTomorrowIfBeforeOrEqualCurrentTime(LocalDateTime dateTime) {
        boolean isCurrentHour = LocalTime.now().getHour() == dateTime.getHour();
        boolean isCurrentMinute = LocalTime.now().getMinute() == dateTime.getMinute();
        boolean isCurrentTime = isCurrentHour && isCurrentMinute;

        if (LocalDateTime.now().isAfter(dateTime) || isCurrentTime) {
            dateTime = dateTime.plusDays(1);
        }
        return dateTime;
    }

    public static LocalTime getTime(int hour, int minute) {
        return LocalTime.of(hour, minute);
    }

    public static LocalTime getTime(long secondOfDay) {
        return LocalTime.ofSecondOfDay(secondOfDay);
    }

    public static LocalDateTime getLocalDateTime(LocalTime alarmTime) {
        LocalDateTime dateTime = LocalDateTime.now();
        dateTime = dateTime.withHour(alarmTime.getHour());
        dateTime = dateTime.withMinute(alarmTime.getMinute());
        return dateTime;
    }
}