package code.ui.interactables;

import code.math.MathHelp;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;

import code.ui.UIInteractable;
import code.ui.UIActionGetter;
import code.ui.UIActionSetter;

/**
* Class for making functional Sliders
*/
public class UISlider extends UIInteractable {
  private final UIActionGetter<Integer> get;
  private final UIActionSetter<Integer> set;
  private final int min;
  private final int max;

  private float zeroLine = 0;
  private float barLength = 0;

  /**
  * Constructor for Sliders
  */
  public UISlider(String name, UIActionGetter<Integer> get, UIActionSetter<Integer> set, int min, int max) {
    this.name = name;
    this.get = get;
    this.set = set;
    this.min = min;
    this.max = max;
    this.primeAction = () -> set(get());
  }

  public int get() {return get.get();}

  public void set(int v) {
    set.set((int)MathHelp.clamp(v, min, max));
  }

  public void moveNode(int x) {
    set((int)(((x-zeroLine)/barLength)*(max-min)+min));
  }

  @Override
  protected void drawBody(Graphics2D g, int off, Color bodyCol, Color textCol) {
    height*=2;
    float inset = ((height/2 - metrics.getHeight())/2);
    float nodeWidth = (height/2-inset*2)/3;

    barLength = width-inset*2;
    zeroLine = x+inset-nodeWidth/2;

    float percent = (1f*get()-min)/(1f*max-min);

    g.setColor(bodyCol);
    g.fill(new Rectangle2D.Double(x+inset, y+height*3/4-inset/4, barLength, inset/2));
    g.setColor(textCol);
    g.fill(new Rectangle2D.Double(x+inset, y+height*3/4-inset/2, barLength*percent, inset));
    g.setColor(bodyCol);
    g.fill(new Rectangle2D.Double(zeroLine+percent*barLength-off/2, y-off/2+height/2+inset, nodeWidth+off, height/2-inset*2+off));
    g.setColor(textCol);
    g.draw(new Rectangle2D.Double(zeroLine+percent*barLength-off/2, y-off/2+height/2+inset, nodeWidth+off, height/2-inset*2+off));
    g.drawString(String.format(name, get()), x+inset, y+inset + metrics.getAscent());
  }
}
