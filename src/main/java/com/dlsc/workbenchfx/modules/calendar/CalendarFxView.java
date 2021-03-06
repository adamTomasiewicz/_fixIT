/*
 *  Copyright (C) 2017 Dirk Lemmermann Software & Consulting (dlsc.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.dlsc.workbenchfx.modules.calendar;


import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.LocalTime;

public class CalendarFxView extends StackPane {

  private Thread updateTimeThread;
  boolean running = false;
 private static Calendar entries_Urgent_Important = new Calendar("Urgent-Important");
  private static Calendar entries_Urgent_nImportant = new Calendar("Urgent-nImportant");
  private static Calendar entries_nUrgent_Important = new Calendar("nUrgent-Important");
  private static Calendar entries_nUrgent_nImportant = new Calendar("nUrgent-nImportant");
  private static Calendar entries_OfficialMatters = new Calendar("Official Matters");
  private static Calendar entries_ShoppingList = new Calendar("Shopping List");

  public static Calendar getEntries_Urgent_Important() {
    return entries_Urgent_Important;
  }
  public static Calendar getEntries_Urgent_nImportant() {
    return entries_Urgent_nImportant;
  }
  public static Calendar getEntries_nUrgent_Important() {
    return entries_nUrgent_Important;
  }
  public static Calendar getEntries_nUrgent_nImportant() {
    return entries_nUrgent_nImportant;
  }
  public static Calendar getEntries_OfficialMatters() {
    return entries_OfficialMatters;
  }
  public static Calendar getEntries_ShoppingList() {
    return entries_ShoppingList;
  }




  public CalendarFxView() {
    CalendarView calendarView = new CalendarView();


    entries_Urgent_Important.setShortName("U-I");
    entries_Urgent_nImportant.setShortName("U-nI");
    entries_nUrgent_Important.setShortName("nU-I");
    entries_nUrgent_nImportant.setShortName("nU-nI");
    entries_OfficialMatters.setShortName("OM");
    entries_ShoppingList.setShortName("SL");

    entries_Urgent_Important.setStyle(Calendar.Style.STYLE1);
    entries_Urgent_nImportant.setStyle(Calendar.Style.STYLE2);
    entries_nUrgent_Important.setStyle(Calendar.Style.STYLE3);
    entries_nUrgent_nImportant.setStyle(Calendar.Style.STYLE4);
    entries_OfficialMatters.setStyle(Calendar.Style.STYLE5);
    entries_ShoppingList.setStyle(Calendar.Style.STYLE6);

    CalendarSource localCalendarSource = new CalendarSource("LocalCalendar");
    localCalendarSource.getCalendars().addAll(
            entries_Urgent_Important,entries_Urgent_nImportant,
            entries_nUrgent_Important,entries_nUrgent_nImportant,
            entries_OfficialMatters,entries_ShoppingList
            );

    calendarView.getCalendarSources().setAll(localCalendarSource);
    calendarView.setRequestedTime(LocalTime.now());
    calendarView.setShowSearchField(true);
    calendarView.setShowDeveloperConsole(true);
    getChildren().addAll(calendarView);

    updateTimeThread = new Thread("Calendar: Update Time Thread") {
      @Override
      public void run() {
        while (running) {
          Platform.runLater(() -> {
            calendarView.setToday(LocalDate.now());
            calendarView.setTime(LocalTime.now());
          });

          try {
            sleep(10000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

        }
      }
    };

    updateTimeThread.setPriority(Thread.MIN_PRIORITY);
    updateTimeThread.setDaemon(true);
    updateTimeThread.start();
  }

  public void start() {
    running = true;
  }

  public void stop() {
    running = false;
  }

}
