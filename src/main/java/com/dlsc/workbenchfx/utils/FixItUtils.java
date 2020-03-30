package com.dlsc.workbenchfx.utils;

import com.calendarfx.model.Entry;
import com.dlsc.workbenchfx.modules.calendar.CalendarFxView;

public class FixItUtils {

    CalendarFxView calendarFxView = new CalendarFxView();

    public void testMethod () {
        calendarFxView.getEntries_Urgent_Important()
    .addEntries(new Entry<>("Dentist"),new Entry<>("test"));
        System.out.println(calendarFxView.getEntries_Urgent_Important().findEntries(""));
    }

}
