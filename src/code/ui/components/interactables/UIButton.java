package code.ui.components.interactables;

import java.awt.Color;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Path2D;

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

  @Override
  protected void drawBody(Graphics2D g, int off, Color bodyCol, Color textCol, Color defaultBodyCol) {
    double incrx = width/16;
    double incry = height/16;

    Color tl;
    Color br;
    if (in) {
      tl = defaultBodyCol.darker();
      br = defaultBodyCol.brighter();
    }
    else {
      tl = defaultBodyCol.brighter();
      br = defaultBodyCol.darker();
    }

    g.setColor(tl);
    g.fill(new Rectangle2D.Double(x, y, width, height));
    
    g.setColor(br);
    Path2D p = new Path2D.Double();
    p.moveTo(x-incry, y+height);
    p.lineTo(x+width, y+height);
    p.lineTo(x+width, y);
    p.closePath();
    g.fill(p);
    
    g.setColor(bodyCol);
    g.fill(new Rectangle2D.Double(x+incrx, y+incry, width-incrx*2, height-incry*2));
    g.setColor(textCol);
    g.drawString(text, x+off+(width-metrics.stringWidth(text))/2, y+off+((height - metrics.getHeight())/2) + metrics.getAscent());
  }
}
