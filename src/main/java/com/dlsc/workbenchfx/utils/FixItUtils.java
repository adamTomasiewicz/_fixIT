package com.dlsc.workbenchfx.utils;

import com.calendarfx.model.Entry;
import com.calendarfx.model.Interval;
import com.dlsc.workbenchfx.modules.calendar.CalendarFxView;

import java.time.LocalDateTime;

import static com.dlsc.workbenchfx.modules.calendar.CalendarFxView.*;

public class FixItUtils {
    static int counter = 1;


    public static void loadTestData () {
        System.out.println(++counter);
        getEntries_nUrgent_Important().addEntry(new Entry<>("Dentist",
       new Interval(LocalDateTime.of(2020, 4,counter,15,00),LocalDateTime.of(2020, 4,counter,16,00))));
                getEntries_Urgent_nImportant().addEntry (new Entry<>("Call",
         new Interval(LocalDateTime.of(2020, 4,counter,11,00),LocalDateTime.of(2020, 4,counter,13,00))));
        getEntries_Urgent_Important().addEntry (new Entry<>("Important meeting",
                new Interval(LocalDateTime.of(2020, 4,counter,9,00),LocalDateTime.of(2020, 4,counter,12,00))));
        getEntries_Urgent_Important().addEntry (new Entry<>("Interview",
                new Interval(LocalDateTime.of(2020, 4,counter,14,00),LocalDateTime.of(2020, 4,counter,16,00))));
        System.out.println(getEntries_Urgent_Important().findEntries(""));
    }

    public static void pushData() {

    }
    public static void pullData() {

    }

}
