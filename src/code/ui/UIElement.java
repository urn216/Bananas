package code.ui;

import code.math.Vector2;
// import code.math.MathHelp;

import java.awt.Graphics2D;
// import java.awt.geom.Rectangle2D;

import java.awt.Color;
// import java.awt.Font;
// import java.awt.FontMetrics;

/**
* Write a description of class Element here.
*
* @author (your name)
* @version (a version number or a date)
*/
public abstract class UIElement {
  protected Vector2 topLeft;
  protected Vector2 botRight;

  protected double fadeDist = 0.08;
  protected double fadeCount = 0;
  protected double animTimeMillis = 175;
  protected long startTimeMillis = System.currentTimeMillis();

  protected int fadeUp    = 0;
  protected int fadeDown  = 0;
  protected int fadeLeft  = 0;
  protected int fadeRight = 0;

  protected Color bgCol;

  protected boolean active = false;
  protected boolean transIn = false;
  protected boolean transOut = false;

  /**
  * Gets this element type
  *
  * @return the type of element this is
  */
  public String getType() {
    return "Ah";
  }

  /**
  * Gets the active state of this element
  *
  * @return true if the element is active
  */
  public boolean isActive() {
    return !transOut && active && !transIn;
  }

  /**
  * Gets the transition state of this element
  *
  * @return true if the element is transitioning
  */
  public boolean isTransitioning() {
    return transOut || transIn;
  }

  /**
  * Tells the element to transition out if it is not already doing so
  */
  public void transOut() {
    if (!transOut) startTimeMillis = System.currentTimeMillis();
    transOut = active;
  }

  /**
  * Tells the element to transition in if it is not already doing so
  */
  public void transIn() {
    if (!transIn) startTimeMillis = System.currentTimeMillis();
    transIn = !active;
  }

  /**
  * toggles the state of this element
  *
  * @return true if the element is now transitioning in
  */
  public boolean toggle() {
    transIn = !active;
    transOut = active;
    startTimeMillis = System.currentTimeMillis();
    return !active;
  }

  /**
  * retrieves the clickable item at a given position
  *
  * @param x The x coord of the given position
  * @param y The y coord of the given position
  *
  * @return the UIClickable present at this location, provided it exists
  */
  public UIInteractable getClickable(double x, double y) {
    return null;
  }

  /**
  * resets all the clickables in this element
  */
  public void resetClickables() {}

  /**
  * draws the current element
  *
  * @param g The Graphics2D object to draw to
  * @param UIscale The scale to magnify the UI to
  * @param screenSizeX The length of the screen
  * @param screenSizeY The height of the screen
  * @param playerStats The stats of the player to draw onto the screen
  */
  public void draw(Graphics2D g, double UIscale, int screenSizeX, int screenSizeY, UIInteractable highlighted, double[] playerStats) {}

  public Color fadeCol(Color toFade, double percent) {
    return new Color(toFade.getRed(), toFade.getGreen(), toFade.getBlue(), (int)(toFade.getAlpha()*percent));
  }

  public Vector2[] fadeLoc(Vector2[] lurd, double dist) {
    lurd[0] = lurd[0].subtract(fadeLeft * dist, fadeUp * dist);
    lurd[1] = lurd[1].subtract(fadeRight * dist, fadeDown * dist);
    return lurd;
  }
}
