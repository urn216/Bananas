package code.ui.elements;

import code.ui.UIColours;
import code.ui.components.UIComponent;
import code.ui.components.UIInteractable;
import code.ui.components.UIText;
import code.ui.components.interactables.UIButton;

import java.awt.Font;

import code.math.Vector2;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import java.awt.Color;

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
  * @param buff The amount of buffer space between buttons
  * @param info The text that should appear on this element
  * @param ties Determines which directions should be faded from/towards in transitions
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
  }
  
  @Override
  public void draw(Graphics2D g, int screenSizeY, Vector2 tL, Vector2 bR, Color[] c, UIInteractable highlighted) {

    double buffer = this.buffer/2;

    Rectangle2D s = new Rectangle2D.Double(
    tL.x+buffer*screenSizeY, tL.y+buffer*screenSizeY, 
    bR.x-tL.x-buffer*2*screenSizeY, 
    bR.y-tL.y-buffer*2*screenSizeY
    );

    g.setColor(c[UIColours.BUTTON_BODY]);
    g.fill(s);
    g.setColor(c[UIColours.BUTTON_OUT_ACC]);
    g.draw(s);

    super.draw(g, screenSizeY, tL, bR, c, highlighted);
  }
}
