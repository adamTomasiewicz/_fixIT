package com.dlsc.workbenchfx.modules.dashboard;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class DashboardView extends BorderPane {

  public DashboardView() {
    getStyleClass().add("module-background");
    setCenter(new Label("DashboardModule"));
  }

}
