package com.dlsc.workbenchfx.view.controls;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a toolbar, which displays all toolbar items stored in the representative Lists.
 * It consists of two areas to display the items: The left and the right toolbarControlBox.
 *
 * @author François Martin
 * @author Marco Sanfratello
 */
public class ToolbarControl extends HBox {
  private static final Logger LOGGER = LoggerFactory.getLogger(ToolbarControl.class.getName());

  private HBox toolbarControlLeftBox;
  private HBox toolbarControlRightBox;

  // The content of the two HBoxes listens to the two Lists and will be set on change.
  private final ListProperty<ToolbarItem> toolbarControlsLeft = new SimpleListProperty<>(this,
      "toolbarControlsLeft", FXCollections.observableArrayList());
  private final ListProperty<ToolbarItem> toolbarControlsRight = new SimpleListProperty<>(this,
      "toolbarControlsRight", FXCollections.observableArrayList());

  private final BooleanProperty empty = new SimpleBooleanProperty(this, "empty", true);

  /**
   * Creates an empty {@link ToolbarControl} object and fully initializes it.
   */
  public ToolbarControl() {
    initializeParts();
    layoutParts();
    setupListeners();
    setupBindings();
  }

  private void initializeParts() {
    getStyleClass().add("toolbar-control");

    toolbarControlLeftBox = new HBox();
    toolbarControlLeftBox.getStyleClass().add("toolbar-control-left-box");

    toolbarControlRightBox = new HBox();
    toolbarControlRightBox.getStyleClass().add("toolbar-control-right-box");
  }

  private void layoutParts() {
    getChildren().addAll(toolbarControlLeftBox, toolbarControlRightBox);
    HBox.setHgrow(toolbarControlLeftBox, Priority.ALWAYS);
  }

  private void setupListeners() {
    toolbarControlsLeft.addListener((InvalidationListener) change ->
        toolbarControlLeftBox.getChildren().setAll(toolbarControlsLeft));
    toolbarControlsRight.addListener((InvalidationListener) change ->
        toolbarControlRightBox.getChildren().setAll(toolbarControlsRight));
  }

  private void setupBindings() {
    empty.bind(Bindings.isEmpty(toolbarControlsLeft).and(Bindings.isEmpty(toolbarControlsRight)));
  }

  /**
   * Returns whether the toolbar is empty or not.
   * As long as the toolbarControlsLeft and -right doesn't contain any values,
   * the toolbar is considered empty.
   *
   * @return whether the toolbar is empty or not
   */
  public final boolean isEmpty() {
    return empty.get();
  }

  /**
   * Returns whether the toolbar is empty or not.
   * As long as the toolbarControlsLeft and -right doesn't contain any values,
   * the toolbar is considered empty.
   *
   * @return whether the toolbar is empty or not
   */
  public final ReadOnlyBooleanProperty emptyProperty() {
    return empty;
  }

  public final ObservableList<ToolbarItem> getToolbarControlsLeft() {
    return toolbarControlsLeft.get();
  }

  public final ListProperty<ToolbarItem> toolbarControlsLeftProperty() {
    return toolbarControlsLeft;
  }

  public final ObservableList<ToolbarItem> getToolbarControlsRight() {
    return toolbarControlsRight.get();
  }

  public final ListProperty<ToolbarItem> toolbarControlsRightProperty() {
    return toolbarControlsRight;
  }
}
