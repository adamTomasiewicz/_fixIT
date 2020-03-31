package com.dlsc.workbenchfx;

import com.dlsc.workbenchfx.modules.calendar.CalendarFxView;
import com.dlsc.workbenchfx.modules.calendar.CalendarModule;
import com.dlsc.workbenchfx.modules.dashboard.DashboardModule;
import com.dlsc.workbenchfx.modules.preferences.Preferences;
import com.dlsc.workbenchfx.modules.preferences.PreferencesModule;
import com.dlsc.workbenchfx.modules.webview.WebModule;
import com.dlsc.workbenchfx.utils.FixItUtils;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FixIT_ver2 extends Application {

  private static final String DOCUMENTATION_PATH =
      WebModule.class.getResource("index.html").toExternalForm();

  private Workbench workbench;
  private Preferences preferences;

  FixItUtils fixItUtils = new FixItUtils();

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    preferences = new Preferences();
    Scene myScene = new Scene(initWorkbench());
    primaryStage.setTitle("FixIT-organizer-app");
    primaryStage.setScene(myScene);
    primaryStage.setWidth(1080);
    primaryStage.setHeight(860);
    primaryStage.show();
    primaryStage.centerOnScreen();

    initNightMode();
  }

  private Workbench initWorkbench() {
    //toolbar items
   // ToolbarItem toolbarLabelFixITOrganizer = new ToolbarItem("FixIT-organizer-app");
    ToolbarItem toolbarButtonAccount = new ToolbarItem("Account",
            new MaterialDesignIconView(MaterialDesignIcon.ACCOUNT)
            , new MenuItem("",
            new VBox(
                    new Label("Login: "),
                    new TextField(),
                    new Label("Password: "),
                    new TextField(),
                    new Button("", new MaterialDesignIconView(
                            MaterialDesignIcon.LOGIN))
            ))
    );
    ToolbarItem toolbarButtonPushData = new ToolbarItem("Push",
            new MaterialDesignIconView(MaterialDesignIcon.ARROW_UP));
    ToolbarItem toolbarButtonPullData = new ToolbarItem("Pull",
            new MaterialDesignIconView(MaterialDesignIcon.ARROW_DOWN));
    ToolbarItem toolbarButtonLoadTestData = new ToolbarItem("LoadTestData",
            new MaterialDesignIconView(MaterialDesignIcon.SETTINGS));


    toolbarButtonLoadTestData.setOnClick(event -> workbench.showConfirmationDialog("Load Test Data",
            "Are you sure you want to load test data?", buttonType -> {
              if(buttonType.getText().equals("Yes")) {
               fixItUtils.loadTestData();
              }}));

    toolbarButtonPullData.setOnClick(event -> workbench.showConfirmationDialog("Pull Data",
            "Are you sure you want to pull data?", null));
    toolbarButtonPushData.setOnClick(event -> workbench.showConfirmationDialog("Push Data",
            "Are you sure you want to push data?", buttonType -> {
              if(buttonType.getText().equals("Yes")) {Platform.exit();System.exit(0);
              }}));


    //menu items
    MenuItem menuItem1 = new MenuItem("Demolition", new MaterialDesignIconView(MaterialDesignIcon.BOMB));
    MenuItem menuItem2 = new MenuItem("Print Tasks", new MaterialDesignIconView(MaterialDesignIcon.PRINTER));
    MenuItem menuItem3 = new MenuItem("Settings", new MaterialDesignIconView(MaterialDesignIcon.SETTINGS));
    MenuItem menuItem4 = new MenuItem("Close App", new MaterialDesignIconView(MaterialDesignIcon.CLOSE));

    menuItem1.setOnAction(event -> workbench.hideNavigationDrawer());
    menuItem2.setOnAction(event -> workbench.hideNavigationDrawer());
    menuItem3.setOnAction(event -> workbench.hideNavigationDrawer());
    menuItem4.setOnAction(event -> workbench.showConfirmationDialog(
                    "Close without saving?",
                    "Are you sure you want to continue without saving"
                            + "your document?", buttonType -> {
                                      if(buttonType.getText().equals("Yes")) {Platform.exit();System.exit(0);
                                      }}));


    //workbench
    workbench =
        Workbench.builder(

            new DashboardModule(),
            new CalendarModule(),
            new WebModule("WebNotepad", MaterialDesignIcon.NOTE, "https://docs.google.com"),
            new WebModule("DLSC",  MaterialDesignIcon.WEB,"http://dlsc.com"),
            new WebModule("Documentation", MaterialDesignIcon.BOOK, DOCUMENTATION_PATH),
            new PreferencesModule(preferences)
        )
            .toolbarLeft(new ToolbarItem("FixIT-organizer-app"))
            .toolbarRight( toolbarButtonPushData, toolbarButtonPullData, toolbarButtonLoadTestData, toolbarButtonAccount)
            .navigationDrawerItems(menuItem1, menuItem2, menuItem3, menuItem4)
            .build();

    //styles
    workbench.getStylesheets().add(FixIT_ver2.class.getResource("customTheme.css").toExternalForm());
    workbench.getStylesheets().add(FixIT_ver2.class.getResource("darkTheme.css").toExternalForm());

    return workbench;
  }

  private void initNightMode() {
    setNightMode(preferences.isNightMode());
    preferences.nightModeProperty().addListener(
        (observable, oldValue, newValue) -> setNightMode(newValue)
    );
  }

  private void setNightMode(boolean on) {
    String customTheme = FixIT_ver2.class.getResource("customTheme.css").toExternalForm();
    String darkTheme = FixIT_ver2.class.getResource("darkTheme.css").toExternalForm();
    ObservableList<String> stylesheets = workbench.getStylesheets();
    if (on) {
      stylesheets.remove(customTheme);
      stylesheets.add(darkTheme);
    } else {
      stylesheets.remove(darkTheme);
      stylesheets.add(customTheme);
    }
  }
}
