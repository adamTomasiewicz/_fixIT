package com.dlsc.workbenchfx.modules.dashboard;

import com.dlsc.workbenchfx.model.WorkbenchModule;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.scene.Node;

public class DashboardModule extends WorkbenchModule {

  public DashboardModule() {
    super("Dashboard", MaterialDesignIcon.HUMAN_HANDSUP);
  }

  @Override
  public Node activate() {
    return new DashboardView();
  }

}
