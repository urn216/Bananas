package code.ui.components.interactables;

import code.ui.UIAction;
import code.ui.components.UIInteractable;

/**
* Class for making functional Buttons
*/
public class UIButton extends UIInteractable {

  /**
  * Constructor for Buttons
  */
  public UIButton(String text, UIAction action) {
    this.text = text;
    this.primeAction = action;
  }
}
