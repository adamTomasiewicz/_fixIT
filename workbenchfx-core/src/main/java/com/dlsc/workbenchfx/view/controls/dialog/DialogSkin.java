package com.dlsc.workbenchfx.view.controls.dialog;

import com.dlsc.workbenchfx.model.WorkbenchDialog;
import com.dlsc.workbenchfx.view.controls.MultilineLabel;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the Skin of the {@link DialogControl}.
 *
 * @author Dirk Lemmermann
 * @author François Martin
 * @author Marco Sanfratello
 */
public class DialogSkin extends SkinBase<DialogControl> {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(DialogSkin.class.getName());
  private static final double MARGIN_PERCENT = .1;

  private final ReadOnlyObjectProperty<WorkbenchDialog> dialog;

  private Label dialogTitle;
  private VBox dialogPane;
  private HBox dialogHeader;
  private StackPane dialogContentPane;
  private ButtonBar dialogButtonBar;
  private final ObservableList<Button> buttons;

  /**
   * Creates a new {@link DialogSkin} object for a corresponding {@link DialogControl}.
   *
   * @param dialogControl the {@link DialogControl} for which this Skin is created
   */
  public DialogSkin(DialogControl dialogControl) {
    super(dialogControl);

    dialog = dialogControl.dialogProperty();
    buttons = dialogControl.getButtons();
    dialogControl.setButtonTextUppercase(true);

    initializeParts();
    layoutParts();
    setupBindings();
    setupEventHandlers();
    setupValueChangedListeners();

    updateDialog(null, getSkinnable().getDialog());
  }

  private void initializeParts() {
    dialogPane = new VBox();
    dialogPane.getStyleClass().add("dialog-pane");

    dialogHeader = new HBox();
    dialogHeader.getStyleClass().add("dialog-header");

    dialogTitle = new Label("Dialog");
    dialogTitle.getStyleClass().add("dialog-title");

    dialogContentPane = new StackPane();
    dialogContentPane.getStyleClass().add("dialog-content-pane");

    dialogButtonBar = new ButtonBar();
    dialogButtonBar.getStyleClass().add("dialog-button-bar");
  }

  private void layoutParts() {
    dialogPane.setFillWidth(true);
    dialogPane.getChildren().setAll(dialogHeader, dialogContentPane, dialogButtonBar);

    VBox.setVgrow(dialogContentPane, Priority.ALWAYS);

    dialogHeader.getChildren().setAll(dialogTitle);

    dialogTitle.setMaxWidth(Double.MAX_VALUE);
    dialogTitle.setWrapText(true);
    HBox.setHgrow(dialogTitle, Priority.ALWAYS);
    VBox.setVgrow(dialogTitle, Priority.NEVER);

    VBox.setVgrow(dialogButtonBar, Priority.NEVER);

    getChildren().add(dialogPane);
  }

  private void setupBindings() {
    dialogButtonBar.managedProperty().bind(dialogButtonBar.visibleProperty());
    // FIXME: these two select bindings result in these warnings:
    /* com.sun.javafx.binding.SelectBinding$SelectBindingHelper getObservableValue
       WARNING: Exception while evaluating select-binding */
    dialogButtonBar.visibleProperty().bind(Bindings.select(dialog, "buttonsBarShown"));
    dialogTitle.textProperty().bind(Bindings.select(dialog, "title"));
    Bindings.bindContent(dialogButtonBar.getButtons(), buttons);
  }

  private void setupEventHandlers() {

  }

  private void setupValueChangedListeners() {
    dialog.addListener((observable, oldDialog, newDialog) -> updateDialog(oldDialog, newDialog));
  }

  private void updateDialog(WorkbenchDialog oldDialog, WorkbenchDialog newDialog) {
    if (!Objects.isNull(newDialog)) {
      // undo old dialog
      if (!Objects.isNull(oldDialog)) {
        dialogPane.getStyleClass().removeAll(oldDialog.getStyleClass());
      }

      // if the new dialog is an error dialog which uses the standard MultilineLabel control
      // wrap it in a DialogErrorContent control
      if (newDialog.getType() == WorkbenchDialog.Type.ERROR
          && newDialog.getContent() instanceof MultilineLabel) {
        newDialog.setContent(
            new DialogErrorContent(newDialog.getContent(), newDialog.getDetails())
        );
      }

      // update to new dialog
      dialogContentPane.getChildren().setAll(newDialog.getContent());
      dialogPane.getStyleClass().setAll("dialog-pane");
      dialogPane.getStyleClass().addAll(newDialog.getStyleClass());
    }
  }

  @Override
  protected void layoutChildren(
      double contentX, double contentY, double contentWidth, double contentHeight) {
    super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
    if (dialogPane.isVisible() && getChildren().contains(dialogPane)) {

      double dialogPrefWidth = dialogPane.prefWidth(-1);
      double dialogPrefHeight = dialogPane.prefHeight(-1);

      final WorkbenchDialog dialog = getSkinnable().getDialog();

      final double maxWidth = contentWidth * (1 - MARGIN_PERCENT);
      final double maxHeight = contentHeight * (1 - MARGIN_PERCENT);

      // on a maximized dialog, set the size to the maximum, else use the dialog size
      if (dialog == null) {
        dialogPrefWidth = dialogPane.getWidth();
        dialogPrefHeight = dialogPane.getHeight();
      } else if (dialog.isMaximized()) {
        dialogPrefWidth = maxWidth;
        dialogPrefHeight = maxHeight;
      }

      // make sure dialog doesn't get bigger than the maximum size
      if (dialogPrefWidth > maxWidth) {
        dialogPrefWidth = maxWidth;
      }
      if (dialogPrefHeight > maxHeight) {
        dialogPrefHeight = maxHeight;
      }

      final double dialogTargetX = contentX + (contentWidth - dialogPrefWidth) / 2;
      final double dialogTargetY = contentY + (contentHeight - dialogPrefHeight) / 2;
      dialogPane.resizeRelocate(
          dialogTargetX, dialogTargetY, dialogPrefWidth, dialogPrefHeight);

      // make sure the content and title are never longer than the dialog (causing it not to wrap)
      dialogContentPane.setMaxWidth(dialogPrefWidth);
      dialogTitle.setMaxWidth(dialogPrefWidth);
    }
  }
}
