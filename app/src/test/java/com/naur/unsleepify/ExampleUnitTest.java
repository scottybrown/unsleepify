package com.naur.unsleepify;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test
    public void canSkipAheadEightHours() {
        assertCalendarIsTime(MainActivity.getEightHoursFromNow(0,0), 8,true);
        assertCalendarIsTime(MainActivity.getEightHoursFromNow(15,0), 23,true);
        assertCalendarIsTime(MainActivity.getEightHoursFromNow(16,0), 0,false);
        assertCalendarIsTime(MainActivity.getEightHoursFromNow(17,0), 1,false);
        assertCalendarIsTime(MainActivity.getEightHoursFromNow(20,0), 4,false);

    }

    private void assertCalendarIsTime(Calendar calendar, int hour, boolean isToday) {
        assertEquals(calendar.get(Calendar.HOUR_OF_DAY),hour);
        assertEquals(calendar.get(Calendar.MINUTE),Calendar.getInstance().get(Calendar.MINUTE));
        assertEquals(calendar.get(Calendar.SECOND),Calendar.getInstance().get(Calendar.SECOND));

        Calendar day = Calendar.getInstance();
        if(!isToday){day.add(Calendar.DAY_OF_MONTH,1);}
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH),day.get(Calendar.DAY_OF_MONTH));
        assertEquals(calendar.get(Calendar.YEAR),day.get(Calendar.YEAR));
        assertEquals(calendar.get(Calendar.MONTH),day.get(Calendar.MONTH));
    }
}