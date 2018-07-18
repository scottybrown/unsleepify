package com.naur.unsleepify;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class UnsleepifyTest {
    @Test
    public void canSkipAheadEightHours() {
        assertCalendarIsTime(DateUtils.getTimeEightHoursFromTime(0, 0), 8, 0);
        assertCalendarIsTime(DateUtils.getTimeEightHoursFromTime(15, 10), 23, 10);
        assertCalendarIsTime(DateUtils.getTimeEightHoursFromTime(16, 35), 0, 35);
        assertCalendarIsTime(DateUtils.getTimeEightHoursFromTime(17, 0), 1, 0);
        assertCalendarIsTime(DateUtils.getTimeEightHoursFromTime(20, 0), 4, 0);

    }

    private void assertCalendarIsTime(Calendar calendar, int hour, int minute) {
        assertEquals(calendar.get(Calendar.HOUR_OF_DAY), hour);
        assertEquals(calendar.get(Calendar.MINUTE), minute);
        assertEquals(calendar.get(Calendar.SECOND), Calendar.getInstance().get(Calendar.SECOND));
    }

    @Test
    public void getTimeDifferenceString() {
        Calendar now = Calendar.getInstance();
        Calendar timeToTest = Calendar.getInstance();
        assertEquals(buildExpectedTimeDifferenceString(0, 0), DateUtils.getTimeDifferenceString(timeToTest));
        timeToTest.add(Calendar.MINUTE, 1);
        assertEquals(buildExpectedTimeDifferenceString(0, 1), DateUtils.getTimeDifferenceString(timeToTest));
        timeToTest.add(Calendar.MINUTE, 9);
        assertEquals(buildExpectedTimeDifferenceString(0, 10), DateUtils.getTimeDifferenceString(timeToTest));
        timeToTest.add(Calendar.MINUTE, 48);
        assertEquals(buildExpectedTimeDifferenceString(0, 58), DateUtils.getTimeDifferenceString(timeToTest));
        timeToTest.add(Calendar.MINUTE, 1);
        assertEquals(buildExpectedTimeDifferenceString(0, 59), DateUtils.getTimeDifferenceString(timeToTest));
        timeToTest.add(Calendar.MINUTE, 1);
        assertEquals(buildExpectedTimeDifferenceString(1, 0), DateUtils.getTimeDifferenceString(timeToTest));
        timeToTest.add(Calendar.MINUTE, 59);
        assertEquals(buildExpectedTimeDifferenceString(1, 59), DateUtils.getTimeDifferenceString(timeToTest));
        timeToTest.add(Calendar.MINUTE, 1);
        assertEquals(buildExpectedTimeDifferenceString(2, 0), DateUtils.getTimeDifferenceString(timeToTest));
        timeToTest.add(Calendar.HOUR, 21);
        assertEquals(buildExpectedTimeDifferenceString(23, 0), DateUtils.getTimeDifferenceString(timeToTest));
        timeToTest.add(Calendar.MINUTE, 59);
        assertEquals(buildExpectedTimeDifferenceString(23, 59), DateUtils.getTimeDifferenceString(timeToTest));
    }

    private String buildExpectedTimeDifferenceString(int hour, int minute) {
        String pluralS = minute == 1 ? "s" : "";
        return "Alarm set for " + hour + " hours, " + minute + " minute" + pluralS + " from now";
    }

    @Test
    public void adjustToTomorrowIfBeforeOrEqualCurrentTime() {
        Calendar timeToTest = Calendar.getInstance();
        adjustAndAssertIsTomorrow(timeToTest, true);
        timeToTest.add(Calendar.MINUTE, -1);
        adjustAndAssertIsTomorrow(timeToTest, true);
        timeToTest.add(Calendar.HOUR, -1);
        adjustAndAssertIsTomorrow(timeToTest, true);

        timeToTest = Calendar.getInstance();
        timeToTest.add(Calendar.MINUTE, 1);
        adjustAndAssertIsTomorrow(timeToTest, false);
        timeToTest.add(Calendar.HOUR, 1);
        adjustAndAssertIsTomorrow(timeToTest, false);
    }

    public void adjustAndAssertIsTomorrow(Calendar calendar, boolean isTomorrow) {
        Calendar originalCalendar = Calendar.getInstance();
        originalCalendar.setTimeInMillis(calendar.getTimeInMillis());
        DateUtils.adjustToTomorrowIfBeforeOrEqualCurrentTime(calendar);
        assertEquals(calendar.get(Calendar.HOUR_OF_DAY), originalCalendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(calendar.get(Calendar.MINUTE), originalCalendar.get(Calendar.MINUTE));
        assertEquals(calendar.get(Calendar.SECOND), Calendar.getInstance().get(Calendar.SECOND));
        assertEquals(calendar.get(Calendar.YEAR), Calendar.getInstance().get(Calendar.YEAR));
        assertEquals(calendar.get(Calendar.MONTH), Calendar.getInstance().get(Calendar.MONTH));
        Calendar dayCalendar = Calendar.getInstance();
        dayCalendar.add(Calendar.DAY_OF_MONTH, isTomorrow ? 1 : 0);
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH), dayCalendar.get(Calendar.DAY_OF_MONTH));
    }
}