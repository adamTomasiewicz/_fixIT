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

package com.dlsc.workbenchfx.modules.notepadtxt;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.LocalTime;

public class NotepadView extends StackPane {

  private Thread updateTimeThread;

  boolean running = false;

  public NotepadView() {
    CalendarView calendarView = new CalendarView();

    Calendar user = new Calendar("User");
    Calendar admin = new Calendar("Admin");


    user.setShortName("U");
    admin.setShortName("A");


    user.setStyle(Style.STYLE1);
    admin.setStyle(Style.STYLE2);


    CalendarSource familyCalendarSource = new CalendarSource("Family");
    familyCalendarSource.getCalendars().addAll(user, admin);

    calendarView.getCalendarSources().setAll(familyCalendarSource);
    calendarView.setRequestedTime(LocalTime.now());
    calendarView.setShowSearchField(true);

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
