package code.ui.interactables;

import code.ui.UIInteractable;
import code.ui.UIAction;

/**
* Class for making functional Buttons
*/
public class UIButton extends UIInteractable {

  /**
  * Constructor for Buttons
  */
  public UIButton(String name, UIAction action) {
    this.name = name;
    this.primeAction = action;
  }
}
