package code.ui.elements;

import code.ui.components.UIComponent;
import code.ui.components.UIInteractable;
import code.ui.components.UIText;
import code.ui.components.interactables.UISlider;
import code.ui.UIColours;
import code.math.Vector2;

import java.awt.Graphics2D;
// import java.awt.geom.Rectangle2D;

import java.awt.Color;

/**
* An element consisting of a vertical list of components
*
* @author William Kilty
* @version 0.1
*/
public class ElemList extends UIElement {

  protected double buffer;
  protected double componentHeight;

  /**
  * Vertical Button box element
  *
  * @param tL The percent inwards from the top left corner of the screen for the top left corner of this element
  * @param bR The percent inwards from the top left corner of the screen for the bottom right corner of this element
  * @param componentHeight The height in pixels of each component in this element
  * @param buffer The amount of buffer space between buttons
  * @param components an array containing all the components to have in the column
  * @param ties Determines which directions should be faded from/towards in transitions (up, down, left, right)
  */
  public ElemList(Vector2 tL, Vector2 bR, double componentHeight, double buffer, UIComponent[] components, boolean[] ties) {
    super(tL, bR, ties);
    this.buffer = buffer;
    this.componentHeight = componentHeight;
    this.components = components;
  }

  @Override
  public void draw(Graphics2D g, int screenSizeY, Vector2 tL, Vector2 bR, Color[] c, UIInteractable highlighted) {
    
    float buff = (float) (buffer * screenSizeY);

    float x = (float) tL.x + buff;
    float y = (float) tL.y + buff;
    float width = (float) bR.x - buff - x;

    for (UIComponent i : components) {
      float height = (float) (componentHeight * screenSizeY);
      if (i instanceof UIText) height/=2;
      i.draw(g, x, y, width, height, i == highlighted ? c[UIColours.BUTTON_HIGHLIGHTED_ACC] : c[UIColours.BUTTON_OUT_ACC], c[UIColours.BUTTON_BODY], c[UIColours.BUTTON_OUT_ACC], c[UIColours.BUTTON_IN_ACC], c[UIColours.BUTTON_LOCKED_BODY]);
      y += buff + height;
      if (i instanceof UISlider) y += height;
    }
  }
}
