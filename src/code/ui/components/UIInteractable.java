package code.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import java.awt.Graphics2D;

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

    setLocked(lockCheck.get());

    if (locked) {
      drawBody(g, 0, colours[4], colours[0], colours[1]);
    }
    else if (in) {
      drawBody(g, 2, colours[2], colours[3], colours[1]);
    }
    else {
      drawBody(g, 0, colours[1], colours[0], colours[1]);
    }
  }

  protected void drawBody(Graphics2D g, int off, Color bodyCol, Color textCol, Color defaultBodyCol) {}
}


// g.setColor(bodyCol);
// g.fill(new Rectangle2D.Double(x+off, y+off, width-off, height-off));
// g.setColor(textCol);
// g.draw(new Rectangle2D.Double(x, y, width, height));
// g.drawString(name, x+off+(width-metrics.stringWidth(name))/2, y+off+((height - metrics.getHeight())/2) + metrics.getAscent());
