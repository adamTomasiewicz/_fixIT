package com.dlsc.workbenchfx.view.controls.dialog;

import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.model.WorkbenchDialog;
import com.dlsc.workbenchfx.view.controls.GlassPane;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.WeakHashMap;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the standard control used to display dialogs in the {@link Workbench}.
 *
 * @author François Martin
 * @author Marco Sanfratello
 */
public class DialogControl extends Control {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(DialogControl.class.getName());

  private final BooleanProperty showingProperty = new SimpleBooleanProperty(this, "showing", false);
  private final BooleanProperty buttonTextUppercase =
      new SimpleBooleanProperty(this, "buttonTextUppercase", false);

  private final ObjectProperty<Workbench> workbench =
      new SimpleObjectProperty<>(this, "workbench");
  private final ObjectProperty<WorkbenchDialog> dialog =
      new SimpleObjectProperty<>(this, "dialog");
  private final ObjectProperty<EventHandler<Event>> onHidden =
      new SimpleObjectProperty<>(this, "onHidden");
  private final ObjectProperty<EventHandler<Event>> onShown =
      new SimpleObjectProperty<>(this, "onShown");

  private final ObservableList<Button> buttons = FXCollections.observableArrayList();
  private final Map<ButtonType, Button> buttonNodes = new WeakHashMap<>();
  private Button defaultButton;
  private ButtonType defaultButtonType;
  private Button cancelButton;
  private ButtonType cancelButtonType;

  private InvalidationListener dialogChangedListener;
  private InvalidationListener blockingChangedListener;
  private EventHandler<KeyEvent> escapeConsumeHandler;

  /**
   * Creates a dialog control.
   */
  public DialogControl() {
    // makes sure that when clicking on transparent pixels outside of dialog, GlassPane will still
    // receive click events!
    setPickOnBounds(false);

    setupChangeListeners();

    getStyleClass().add("dialog-control");
  }

  private void setupChangeListeners() {
    // update buttons whenever dialog, buttonTypes, workbench, or buttonTextUppercase changes
    dialogChangedListener = observable -> updateButtons(getDialog());
    escapeConsumeHandler = event -> {
      if (KeyCode.ESCAPE.equals(event.getCode())) {
        LOGGER.trace("ESC was pressed on a blocking dialog, consuming event");
        event.consume();
      }
    };
    blockingChangedListener = observable -> {
      if (getDialog().isBlocking()) {
        LOGGER.trace("Added escapeConsumeHandler");
        addEventFilter(KeyEvent.ANY, escapeConsumeHandler);
      } else {
        LOGGER.trace("Removed escapeConsumeHandler");
        removeEventFilter(KeyEvent.ANY, escapeConsumeHandler);
      }
    };
    dialog.addListener((observable, oldDialog, newDialog) -> {
      if (!Objects.isNull(oldDialog)) {
        oldDialog.getButtonTypes().removeListener(dialogChangedListener);
        oldDialog.blockingProperty().removeListener(blockingChangedListener);
      }
      if (!Objects.isNull(newDialog)) {
        newDialog.getButtonTypes().addListener(dialogChangedListener);
        newDialog.blockingProperty().addListener(blockingChangedListener);
        blockingChangedListener.invalidated(null); // initially trigger
      }
    });
    dialog.addListener(dialogChangedListener);
    buttonTextUppercase.addListener(dialogChangedListener);

    // fire events depending on the dialog being shown or not
    showingProperty.addListener((observable, oldShowing, newShowing) -> {
      LOGGER.trace("Dialog Showing Listener - old: " + oldShowing + " new: " + newShowing);
      if (!oldShowing && newShowing) {
        fireOnShown();
      } else if (oldShowing && !newShowing) {
        fireOnHidden();
      }
    });

    skinProperty().addListener(observable -> {
      // make sure onShown doesn't get fired before the skin was fully initialized
      if (!Objects.isNull(getSkin())) {
        setShowing(true);

        // now that the skin is initialized, whenever workbench is set, it is about to get shown
        showingProperty.bind(Bindings.isNotNull(workbenchProperty()));
      }
    });
  }

  private void updateButtons(WorkbenchDialog dialog) {
    LOGGER.trace("Updating buttons");

    buttons.clear();
    cancelButton = null;
    cancelButtonType = null;
    defaultButton = null;
    defaultButtonType = null;
    buttonNodes.clear();

    if (Objects.isNull(dialog)) {
      return;
    }

    boolean hasDefault = false;
    for (ButtonType cmd : dialog.getButtonTypes()) {
      Button button = buttonNodes.computeIfAbsent(cmd, dialogButton -> createButton(cmd));

      // keep only first default button
      ButtonBar.ButtonData buttonType = cmd.getButtonData();

      boolean isFirstDefaultButton =
          !hasDefault && buttonType != null && buttonType.isDefaultButton();
      button.setDefaultButton(isFirstDefaultButton);
      if (isFirstDefaultButton) {
        defaultButton = button;
        defaultButtonType = cmd;
      }
      // take last cancel button
      boolean isCancelButton = buttonType != null && buttonType.isCancelButton();
      button.setCancelButton(isCancelButton);
      if (isCancelButton) {
        cancelButton = button;
        cancelButtonType = cmd;
      }
      button.setOnAction(evt -> completeDialog(cmd));

      hasDefault |= buttonType != null && buttonType.isDefaultButton();

      buttons.add(button);

      LOGGER.trace("updateButtons finished");
    }

    updateKeyboardBehavior();
  }

  private void completeDialog(ButtonType cmd) {
    getDialog().getOnResult().accept(cmd);
    hide();
  }

  private void updateKeyboardBehavior() {
    // if there is no default button, but there is only one button present
    if (Objects.isNull(defaultButton) && buttons.size() == 1) {
      // set the button as the default button
      LOGGER.trace("updateKeyboardBehavior - No default button, setting button as default");
      buttons.get(0).setDefaultButton(true);
    }
    if (Objects.isNull(cancelButton)) {
      LOGGER.trace("No cancel button, setting ButtonType.CANCEL as cancel");
      // focus the dialog if none of the buttons are focused by the ButtonBar or the ButtonBar has
      // been made invisible, since onKeyReleased event only triggers, if the node or any of its
      // children are focused
      Platform.runLater(() -> {
        if (!buttons.stream().anyMatch(Node::isFocused)) {
          requestFocus();
        }
      });
      setOnKeyReleased(event -> {
        if (KeyCode.ESCAPE.equals(event.getCode())) {
          LOGGER.trace("ESC was pressed, closing dialog");
          completeDialog(ButtonType.CANCEL);
        }
      });
    } else {
      LOGGER.trace("Cancel button present");
      setOnKeyReleased(event -> {
        // do nothing, reset previously set event handler since it's being handled by the buttons
      });
    }
  }

  private void fireOnShown() {
    if (!Objects.isNull(getOnShown())) {
      LOGGER.trace("Firing onShown event - Dialog is initialized and being shown");
      getOnShown().handle(new Event(this, this, Event.ANY));
    }
  }

  private void fireOnHidden() {
    if (!Objects.isNull(getOnHidden())) {
      LOGGER.trace("Firing onHidden event - Dialog has been hidden");
      getOnHidden().handle(new Event(this, this, Event.ANY));
    }
  }

  private Button createButton(ButtonType buttonType) {
    LOGGER.trace("Create Button: " + buttonType.getText());
    String buttonText;
    if (isButtonTextUppercase()) {
      buttonText = buttonType.getText().toUpperCase();
    } else {
      buttonText = buttonType.getText();
    }
    final Button button = new Button(buttonText);
    final ButtonBar.ButtonData buttonData = buttonType.getButtonData();
    ButtonBar.setButtonData(button, buttonData);
    button.setDefaultButton(buttonData.isDefaultButton());
    button.setCancelButton(buttonData.isCancelButton());
    return button;
  }

  public final void hide() {
    getWorkbench().hideDialog(getDialog());
  }

  /**
   * Retrieves the {@link Button} instance of the {@link DialogControl} which is of the specified
   * {@link ButtonType}.
   *
   * @param buttonType to retrieve from the {@link DialogControl}
   * @return the button or an empty {@link Optional}, if the {@link DialogControl} hasn't been
   *     initialized before
   */
  public final Optional<Button> getButton(ButtonType buttonType) {
    return Optional.ofNullable(buttonNodes.get(buttonType));
  }

  // EventHandler

  /**
   * The dialog's action, which is invoked whenever the dialog has been fully initialized and is
   * being shown. Whenever the {@link #dialogProperty()}, {@link WorkbenchDialog#buttonTypes},
   * {@link #buttonTextUppercaseProperty()} or {@link #workbenchProperty()} changes, the dialog will
   * be rebuilt and upon completion, an event will be fired.
   *
   * @return the property to represent the event, which is invoked whenever the dialog has been
   *     fully initialized and is being shown.
   */
  public final ObjectProperty<EventHandler<Event>> onShownProperty() {
    return onShown;
  }

  public final void setOnShown(EventHandler<Event> value) {
    onShown.set(value);
  }

  public final EventHandler<Event> getOnShown() {
    return onShown.get();
  }

  /**
   * The dialog's action, which is invoked whenever the dialog has been hidden in the scene graph.
   * An event will be fired whenever {@link #hide()} or
   * {@link Workbench#hideDialog(WorkbenchDialog)} has been called or the dialog has been closed by
   * clicking on its corresponding {@link GlassPane}.
   *
   * @return the property to represent the event, which is invoked whenever the dialog has been
   *     hidden in the scene graph.
   */
  public final ObjectProperty<EventHandler<Event>> onHiddenProperty() {
    return onHidden;
  }

  public final void setOnHidden(EventHandler<Event> value) {
    onHidden.set(value);
  }

  public final EventHandler<Event> getOnHidden() {
    return onHidden.get();
  }

  // Accessors and mutators

  public final WorkbenchDialog getDialog() {
    return dialog.get();
  }

  public final ObjectProperty<WorkbenchDialog> dialogProperty() {
    return dialog;
  }

  public final void setDialog(WorkbenchDialog dialog) {
    this.dialog.set(dialog);
  }

  public final ObservableList<Button> getButtons() {
    return FXCollections.unmodifiableObservableList(buttons);
  }

  public final boolean isButtonTextUppercase() {
    return buttonTextUppercase.get();
  }

  public final BooleanProperty buttonTextUppercaseProperty() {
    return buttonTextUppercase;
  }

  public final void setButtonTextUppercase(boolean buttonTextUppercase) {
    this.buttonTextUppercase.set(buttonTextUppercase);
  }

  public final Workbench getWorkbench() {
    return workbench.get();
  }

  public final void setWorkbench(Workbench workbench) {
    this.workbench.set(workbench);
  }

  public final ObjectProperty<Workbench> workbenchProperty() {
    return workbench;
  }

  /**
   * Represents whether the dialog is currently showing.
   *
   * @return the property representing whether the dialog is currently showing
   */
  public final ReadOnlyBooleanProperty showingProperty() {
    return showingProperty;
  }

  /**
   * Returns whether or not the dialog is showing.
   *
   * @return true if dialog is showing.
   */
  public final boolean isShowing() {
    return showingProperty().get();
  }

  private void setShowing(boolean showing) {
    this.showingProperty.set(showing);
  }

  public ButtonType getCancelButtonType() {
    return cancelButtonType;
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new DialogSkin(this);
  }
}
