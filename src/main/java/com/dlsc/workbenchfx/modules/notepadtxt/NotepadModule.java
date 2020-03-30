package com.dlsc.workbenchfx.modules.notepadtxt;

import com.dlsc.workbenchfx.model.WorkbenchModule;
import com.dlsc.workbenchfx.modules.calendar.CalendarFxView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.scene.Node;

import java.util.Objects;

public class NotepadModule extends WorkbenchModule {

  private CalendarFxView calendarView;

  public NotepadModule() {
    super("Calendar", MaterialDesignIcon.CALENDAR);
  }

  @Override
  public Node activate() {
    if (Objects.isNull(calendarView)) {
      calendarView = new CalendarFxView();
    }
    calendarView.start();
    return calendarView;
  }

  @Override
  public void deactivate() {
    calendarView.stop();
  }

  @Override
  public boolean destroy() {
    calendarView = null;
    return true;
  }
}
