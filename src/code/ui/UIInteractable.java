package code.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
* Class for making clickable items in a User Interface
*/
public abstract class UIInteractable {

  protected String name;

  protected UIAction primeAction;

  protected boolean in = false;
  protected boolean locked = false;

  protected FontMetrics metrics;

  protected float x = 0;
  protected float y = 0;
  protected float width = 0;
  protected float height = 0;

  public String setName(String name) {
    this.name = name;
    return this.name;
  }

  public String getName() {
    return name;
  }

  public void setIn() {in = true;}

  public void setOut() {in = false;}

  public void lock() {locked = true;}

  public void unlock() {locked = false;}

  public boolean isIn() {return in;}

  public boolean isLocked() {return locked;}

  public boolean touching(double oX, double oY) {
    if (oX > x && oX < x+width && oY > y && oY < y+height) {
      return true;
    }
    return false;
  }

  public void setPrimeAct(UIAction action) {primeAction = action;}

  public void primeAct() {if (primeAction != null) primeAction.perform();}

  public void draw(Graphics2D g, float x, float y, float width, float height, Color bodyCol, Color textCol, Color inBodyCol, Color inTextCol, Color lockedBodyCol) {
    Font font = new Font("Copperplate", Font.BOLD, (int) Math.round((height/2)));
    metrics = g.getFontMetrics(font);
    g.setFont(font);
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;

    if (locked) {
      drawBody(g, 0, lockedBodyCol, textCol);
    }
    else if (in) {
      drawBody(g, 2, inBodyCol, inTextCol);
    }
    else {
      drawBody(g, 0, bodyCol, textCol);
    }
  }

  protected void drawBody(Graphics2D g, int off, Color bodyCol, Color textCol) {
    g.setColor(bodyCol);
    g.fill(new Rectangle2D.Double(x+off, y+off, width-off, height-off));
    g.setColor(textCol);
    g.draw(new Rectangle2D.Double(x, y, width, height));
    g.drawString(name, x+off+(width-metrics.stringWidth(name))/2, y+off+((height - metrics.getHeight())/2) + metrics.getAscent());
  }
}
