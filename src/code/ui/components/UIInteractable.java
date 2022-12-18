package code.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Path2D;

import code.ui.UIAction;
import code.ui.UIActionGetter;

/**
* Class for making clickable items in a User Interface
*/
public abstract class UIInteractable extends UIComponent {

  protected UIAction primeAction;

  protected UIActionGetter<Boolean> lockCheck = this::isLocked;

  protected boolean in = false;
  protected boolean locked = false;

  protected FontMetrics metrics;
  protected int fontStyle = Font.BOLD;

  public void setIn() {in = true;}

  public void setOut() {in = false;}

  public void lock() {locked = true;}

  public void unlock() {locked = false;}

  public void setLocked(boolean locked) {this.locked = locked;}

  public void setLockCheck(UIActionGetter<Boolean> lockCheck) {this.lockCheck = lockCheck;}

  public boolean isIn() {return in;}

  public boolean isLocked() {return locked;}

  public void setPrimeAct(UIAction action) {primeAction = action;}

  public void primeAct() {if (primeAction != null) primeAction.perform();}

  public void draw(Graphics2D g, Color... colours) {
    if (colours.length != 5) throw new IllegalArgumentException("Must contain five colours (text, body, bodyIn, textIn, bodyLocked)");
    Font font = new Font("Copperplate", fontStyle, (int) Math.round((height/2)));
    metrics = g.getFontMetrics(font);
    g.setFont(font);

    Color high = colours[1].brighter();
    Color low = colours[1].darker();

    setLocked(lockCheck.get());

    if (locked) {
      drawBody(g, 0, colours[4], high, low, colours[0]);
    }
    else if (in) {
      drawBody(g, 2, colours[2], low, high, colours[3]);
    }
    else {
      drawBody(g, 0, colours[1], high, low, colours[0]);
    }
  }

  protected void drawBody(Graphics2D g, int off, Color bodyCol, Color tl, Color br, Color textCol) {
    double incrx = width/16;
    double incry = height/16;

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


// g.setColor(bodyCol);
// g.fill(new Rectangle2D.Double(x+off, y+off, width-off, height-off));
// g.setColor(textCol);
// g.draw(new Rectangle2D.Double(x, y, width, height));
// g.drawString(name, x+off+(width-metrics.stringWidth(name))/2, y+off+((height - metrics.getHeight())/2) + metrics.getAscent());
