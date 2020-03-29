package com.dlsc.workbenchfx.testing;

import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.view.controls.module.Page;
import javafx.scene.control.Skin;

public class MockPage extends Page {
  /**
   * Constructs a new {@link Page}.
   *
   * @param workbench which created this {@link Page}
   */
  public MockPage(Workbench workbench) {
    super(workbench);
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new MockPageSkin(this);
  }
}
