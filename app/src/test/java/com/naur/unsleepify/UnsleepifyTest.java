package com.naur.unsleepify;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import static org.junit.Assert.*;

public class UnsleepifyTest {
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

    public void adjustAndAssertIsTomorrow(LocalDateTime dateTime, boolean isTomorrow) {
        LocalDateTime originalDateTime = LocalDateTime.of(dateTime.toLocalDate(), dateTime.toLocalTime());

        dateTime = DateUtils.adjustToTomorrowIfBeforeOrEqualCurrentTime(dateTime);
        assertEquals(dateTime.getHour(), originalDateTime.getHour());
        assertEquals(dateTime.getMinute(), originalDateTime.getMinute());
        assertEquals(dateTime.getSecond(), LocalDateTime.now().getSecond());
        assertEquals(dateTime.getYear(), LocalDateTime.now().getYear());
        assertEquals(dateTime.getMonth(), LocalDateTime.now().getMonth());

        LocalDateTime dayDateTime = LocalDateTime.now();
        dayDateTime = dayDateTime.plusDays(isTomorrow ? 1 : 0);
        assertEquals(dayDateTime.getDayOfMonth(), dateTime.getDayOfMonth());
    }

    @Test
    public void getLocalTimeHourAndMinute() {
        assertLocalTimeIsTime(DateUtils.getTime(0, 0), 0, 0);
        assertLocalTimeIsTime(DateUtils.getTime(5, 10), 5, 10);
        assertLocalTimeIsTime(DateUtils.getTime(23, 59), 23, 59);
    }

    @Test
    public void getLocalTimeMillis() {
        assertEquals(12345, DateUtils.getTime(12345).toSecondOfDay());
    }

    @Test
    public void leftPad() {
        assertEquals("000123", Utils.leftPad(123,0,6));
        assertEquals("00000123", Utils.leftPad(123,0,8));
        assertEquals("123", Utils.leftPad(123,0,3));
        assertEquals("123", Utils.leftPad(123,0,2));
        assertEquals("00" ,Utils.leftPad(0,0,2));
    }
}
