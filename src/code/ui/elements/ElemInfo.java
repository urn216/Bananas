package code.ui.elements;

import code.ui.components.UIComponent;
import code.ui.components.UIText;
import code.ui.components.interactables.UIButton;

import java.awt.Font;

import code.math.Vector2;

/**
* An info/warning screen to display information to a user that they can clear themselves
*
* @author William Kilty
* @version 0.1
*/
public class ElemInfo extends ElemList {
  
  /**
  * An info/warning window
  *
  * @param tL The percent inwards from the top left corner of the screen for the top left corner of this element
  * @param bR The percent inwards from the top left corner of the screen for the bottom right corner of this element
  * @param buffer The amount of buffer space between buttons
  * @param ties Determines which directions should be faded from/towards in transitions
  * @param info The text that should appear on this element, separated by lines
  */
  public ElemInfo(Vector2 tL, Vector2 bR, double buffer, boolean[] ties, String... info) {
    super(
    tL, 
    bR, 
    (bR.y-tL.y-(buffer*(info.length+2)))/(info.length*0.5+1), 
    buffer, 
    new UIComponent[info.length+1],
    ties
    );
    
    for (int i = 0; i < info.length; i++) {
      components[i] = new UIText(info[i], 1, Font.PLAIN);
    }
    components[info.length] = new UIButton("OK", this::transOut);

    this.solidBacking = true;
  }
}
