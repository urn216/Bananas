package code.ui.elements;

import java.awt.Font;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;

import code.math.Vector2;
import code.ui.UIAction;
import code.ui.UIColours;
import code.ui.components.UIComponent;
import code.ui.components.UIInteractable;
import code.ui.components.UIText;
import code.ui.components.interactables.UIButton;

public class ElemConfirmation extends UIElement {

  protected double buffer;
  protected double componentHeight;

  public ElemConfirmation(Vector2 tL, Vector2 bR, double buffer, boolean[] ties, UIAction yes, UIAction no, String... info) {
    super(tL, bR, ties);
    this.buffer = buffer;
    this.componentHeight = (bR.y-tL.y-(buffer*(info.length+2)))/(info.length*0.5+1);
    components = new UIComponent[info.length+3];

    for (int i = 0; i < info.length; i++) {
      components[i] = new UIText(info[i], 1, Font.PLAIN);
    }
    components[info.length  ] = new UIButton("Yes"   , yes           );
    components[info.length+1] = new UIButton("No"    , no            );
    components[info.length+2] = new UIButton("Cancel", this::transOut);
  }

  @Override
  protected void draw(Graphics2D g, int screenSizeY, Vector2 tL, Vector2 bR, Color[] c, UIInteractable highlighted) {
    
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

    float buff = (float) (this.buffer * screenSizeY);

    float x = (float) tL.x + buff;
    float y = (float) tL.y + buff;
    float width = (float) bR.x - buff - x;

    for (int i = 0; i < components.length-3; i++) {
      UIComponent comp = components[i];
      float height = (float) (componentHeight * screenSizeY) / 2;
      comp.draw(g, x, y, width, height, c[UIColours.TEXT]);
      y += buff + height;
    }

    float height = (float) (componentHeight * screenSizeY);
    width = (float) (bR.x-tL.x-buff*4)/3;

    for (int i = components.length-3; i < components.length; i++) {
      UIComponent comp = components[i];
      comp.draw(g, x, y, width, height, comp == highlighted ? c[UIColours.BUTTON_HIGHLIGHTED_ACC] : c[UIColours.BUTTON_OUT_ACC], c[UIColours.BUTTON_BODY], c[UIColours.BUTTON_OUT_ACC], c[UIColours.BUTTON_IN_ACC], c[UIColours.BUTTON_LOCKED_BODY]);
      x += buff + width;
    }
  }
}
