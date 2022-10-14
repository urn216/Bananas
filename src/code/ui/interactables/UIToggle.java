package code.ui.interactables;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;

import code.ui.UIInteractable;
import code.ui.UIActionGetter;
import code.ui.UIActionSetter;

/**
* Class for making functional Toggle Buttons
*/
public class UIToggle extends UIInteractable {

  private static final float BUFFER_SCALE = 1f/4f;

  private final UIActionGetter<Boolean> get;

  /**
  * Constructor for Toggles
  */
  public UIToggle(String name, UIActionGetter<Boolean> get, UIActionSetter<Boolean> set) {
    this.name = name;
    this.get = get;
    this.primeAction = () -> {
      set.set(!get.get());
    };
  }

  @Override
  protected void drawBody(Graphics2D g, int off, Color bodyCol, Color textCol) {
    g.setColor(bodyCol);
    g.fill(new Rectangle2D.Double(x+off, y+off, width-off, height-off));
    g.setColor(textCol);
    g.draw(new Rectangle2D.Double(x, y, width, height));
    float bodyW = width-height;
    float buffer = height*BUFFER_SCALE;

    g.draw(new Rectangle2D.Double(x+bodyW, y, height, height));
    g.drawString(name, x+off+(bodyW-metrics.stringWidth(name))/2, y+off+((height - metrics.getHeight())/2) + metrics.getAscent());

    g.draw(new Rectangle2D.Double(x+bodyW+buffer, y+buffer, height-buffer*2, height-buffer*2));
    if (get.get()) g.fill(new Rectangle2D.Double(x+bodyW+buffer*1.125+off/2, y+buffer*1.125+off/2, height-buffer*2.25, height-buffer*2.25));
  }
}
