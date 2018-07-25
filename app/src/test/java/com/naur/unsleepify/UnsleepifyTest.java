package com.naur.unsleepify;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class UnsleepifyTest {
    // todo don't use calendar. use jodatime. or...android equiv? given it's somewhere past java 6
    @Test
    public void canSkipAheadEightHours() {
        assertLocalTimeIsTime(DateUtils.getTimeEightHoursFromTime(0, 0), 8, 0);
        assertLocalTimeIsTime(DateUtils.getTimeEightHoursFromTime(15, 10), 23, 10);
        assertLocalTimeIsTime(DateUtils.getTimeEightHoursFromTime(16, 35), 0, 35);
        assertLocalTimeIsTime(DateUtils.getTimeEightHoursFromTime(17, 0), 1, 0);
        assertLocalTimeIsTime(DateUtils.getTimeEightHoursFromTime(20, 0), 4, 0);
    }

    private void assertLocalTimeIsTime(LocalTime time, int hour, int minute) {
        assertEquals(time.getHour(), hour);
        assertEquals(time.getMinute(), minute);
        assertEquals(time.getSecond(), 0);
    }

    @Test
    public void getTimeDifferenceString() {
        LocalDateTime timeToTest = LocalDateTime.now();
        assertEquals(buildExpectedTimeDifferenceString(0, 0), DateUtils.getTimeDifferenceString(timeToTest));
        timeToTest = timeToTest.plusMinutes(1);
        assertEquals(buildExpectedTimeDifferenceString(0, 1), DateUtils.getTimeDifferenceString(timeToTest));
        timeToTest = timeToTest.plusMinutes(9);
        assertEquals(buildExpectedTimeDifferenceString(0, 10), DateUtils.getTimeDifferenceString(timeToTest));
        timeToTest = timeToTest.plusMinutes(48);
        assertEquals(buildExpectedTimeDifferenceString(0, 58), DateUtils.getTimeDifferenceString(timeToTest));
        timeToTest = timeToTest.plusMinutes(1);
        assertEquals(buildExpectedTimeDifferenceString(0, 59), DateUtils.getTimeDifferenceString(timeToTest));
        timeToTest = timeToTest.plusMinutes(1);
        assertEquals(buildExpectedTimeDifferenceString(1, 0), DateUtils.getTimeDifferenceString(timeToTest));
        timeToTest = timeToTest.plusMinutes(59);
        assertEquals(buildExpectedTimeDifferenceString(1, 59), DateUtils.getTimeDifferenceString(timeToTest));
        timeToTest = timeToTest.plusMinutes(1);
        assertEquals(buildExpectedTimeDifferenceString(2, 0), DateUtils.getTimeDifferenceString(timeToTest));
        timeToTest = timeToTest.plusHours(21);
        assertEquals(buildExpectedTimeDifferenceString(23, 0), DateUtils.getTimeDifferenceString(timeToTest));
        timeToTest = timeToTest.plusMinutes(59);
        assertEquals(buildExpectedTimeDifferenceString(23, 59), DateUtils.getTimeDifferenceString(timeToTest));
    }

    private String buildExpectedTimeDifferenceString(int hour, int minute) {
        String pluralS = minute == 1 ? "" : "s";
        return "Alarm set for " + hour + " hours, " + minute + " minute" + pluralS + " from now";
    }

    @Test
    public void adjustToTomorrowIfBeforeOrEqualCurrentTime() {
        LocalDateTime timeToTest = LocalDateTime.now();
        adjustAndAssertIsTomorrow(timeToTest, true);
        timeToTest = timeToTest.plusMinutes(-1);
        adjustAndAssertIsTomorrow(timeToTest, true);
        timeToTest = timeToTest.plusHours(-1);
        adjustAndAssertIsTomorrow(timeToTest, true);

        timeToTest = LocalDateTime.now();
        timeToTest = timeToTest.plusMinutes(1);
        adjustAndAssertIsTomorrow(timeToTest, false);
        timeToTest = timeToTest.plusHours(1);
        adjustAndAssertIsTomorrow(timeToTest, false);
    }

    public void adjustAndAssertIsTomorrow(LocalDateTime calendar, boolean isTomorrow) {
        LocalDateTime originalCalendar = LocalDateTime.of(calendar.toLocalDate(), calendar.toLocalTime());

        calendar=DateUtils.adjustToTomorrowIfBeforeOrEqualCurrentTime(calendar);
        assertEquals(calendar.getHour(), originalCalendar.getHour());
        assertEquals(calendar.getMinute(), originalCalendar.getMinute());
        assertEquals(calendar.getSecond(), LocalDateTime.now().getSecond());
        assertEquals(calendar.getYear(), LocalDateTime.now().getYear());
        assertEquals(calendar.getMonth(), LocalDateTime.now().getMonth());

        LocalDateTime dayCalendar = LocalDateTime.now();
        dayCalendar = dayCalendar.plusDays(isTomorrow ? 1 : 0);
        assertEquals(dayCalendar.getDayOfMonth(),calendar.getDayOfMonth());
    }

    @Test
    public void getCalendarHourAndMinute() {
        assertLocalTimeIsTime(DateUtils.getTime(0, 0), 0, 0);
        assertLocalTimeIsTime(DateUtils.getTime(5, 10), 5, 10);
        assertLocalTimeIsTime(DateUtils.getTime(23, 59), 23, 59);
    }

    @Test
    public void getCalendarMillis() {
        assertEquals(12345, DateUtils.getTime(12345).toSecondOfDay());
    }
}
// todo eradicate the word calendar