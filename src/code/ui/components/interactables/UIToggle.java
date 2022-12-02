package code.ui.components.interactables;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import code.ui.UIActionGetter;
import code.ui.UIActionSetter;
import code.ui.components.UIInteractable;

/**
* Class for making functional Toggle Buttons
*/
public class UIToggle extends UIInteractable {

  private static final float BUFFER_SCALE = 1f/4f;

  private final UIActionGetter<Boolean> get;

  /**
  * Constructor for Toggles
  */
  public UIToggle(String text, UIActionGetter<Boolean> get, UIActionSetter<Boolean> set) {
    this.text = text;
    this.get = get;
    this.primeAction = () -> {
      set.set(!get.get());
    };
  }

  @Override
  protected void drawBody(Graphics2D g, int off, Color bodyCol, Color tl, Color br, Color textCol) {
    float incrx = width/16;
    float incry = height/16;

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
    float centreWidth  = width  - incrx*2;
    float centreHeight = height - incry*2;
    g.fill(new Rectangle2D.Double(x+incrx, y+incry, width-incrx*2, height-incry*2));

    float bodyW = centreWidth-centreHeight;
    float buffer = centreHeight*BUFFER_SCALE;

    g.setColor(tl);

    g.draw(new Line2D.Double(x+incrx+bodyW, y+incry, x+incrx+bodyW, y+incry+centreHeight));

    g.setColor(textCol);

    g.drawString(text, x+incrx+off+(bodyW-metrics.stringWidth(text))/2, y+incry+off+((centreHeight - metrics.getHeight())/2) + metrics.getAscent());

    g.draw(new Rectangle2D.Double(x+incrx+bodyW+buffer, y+incry+buffer, centreHeight-buffer*2, centreHeight-buffer*2));
    if (get.get()) g.fill(new Rectangle2D.Double(x+incrx+bodyW+buffer*1.125+off/2, y+incry+buffer*1.125+off/2, centreHeight-buffer*2.25, centreHeight-buffer*2.25));
  }
}
