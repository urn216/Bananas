package code.ui.elements;

import code.math.MathHelp;
import code.math.Vector2;
import code.ui.UIColours;
// import code.math.MathHelp;
import code.ui.components.UIComponent;
import code.ui.components.UIInteractable;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

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
  private final Vector2 topLeft;
  private final Vector2 botRight;
  
  private final double fadeDist = 0.08;
  private double fadeCount = 0;
  
  private long animTimeMillis = 175;
  private long startTimeMillis = System.currentTimeMillis();
  
  private final int fadeUp   ;
  private final int fadeDown ;
  private final int fadeLeft ;
  private final int fadeRight;
  
  private static Color[] colourPack = UIColours.DEFAULT;
  
  protected int backgroundColour = UIColours.BACKGROUND;
  
  protected boolean solidBacking = false;
  
  private boolean active = false;
  private boolean transIn = false;
  private boolean transOut = false;
  
  protected UIComponent[] components = {};
  
  public UIElement(Vector2 topLeft, Vector2 botRight, boolean[] ties) {
    if (ties == null || ties.length != 4) throw new IllegalArgumentException("Must have four ties: (up, down, left, right)");
    this.topLeft = topLeft;
    this.botRight = botRight;
    
    if (ties[0] || ties[1]) {
      fadeUp    = ties[0] ? 1 : -1;
      fadeDown  = ties[1] ? -1 : 1;
    } else fadeUp=fadeDown=0;
    if (ties[2] || ties[3]) {
      fadeLeft  = ties[2] ? 1 : -1;
      fadeRight = ties[3] ? -1 : 1;
    } else fadeLeft=fadeRight=0;
    
    init();
  }
  
  protected void init() {}
  
  /**
  * Gets the active state of this element
  *
  * @return true if the element is active
  */
  public final boolean isActive() {
    return !transOut && active && !transIn;
  }
  
  /**
  * Sets the element to an inactive state immediately without transitioning
  */
  public final void deactivate() {
    transIn = transOut = active = false;
  }
  
  /**
  * Gets the transition state of this element
  *
  * @return true if the element is transitioning
  */
  public final boolean isTransitioning() {
    return transOut || transIn;
  }
  
  /**
  * Tells the element to transition out if it is not already doing so
  *
  * @return {@code true} if the element is now transitioning out
  */
  public final boolean transOut(long animTimeMillis) {
    resetClickables();
    if (transOut) return true;

    this.startTimeMillis = System.currentTimeMillis();
    this.animTimeMillis = animTimeMillis;
    this.transOut = active;
    return transOut;
  }
  
  /**
  * Tells the element to transition in if it is not already doing so
  *
  * @return {@code true} if the element is now transitioning in
  */
  public final boolean transIn(long animTimeMillis) {
    for (UIComponent c : components) c.onTransIn();
    if (transIn) return true;

    this.startTimeMillis = System.currentTimeMillis();
    this.animTimeMillis = animTimeMillis;
    this.transIn = !active;
    return transIn;
  }
  
  /**
  * toggles the state of this element
  *
  * @return true if the element is now transitioning
  */
  public final boolean toggle(long animTimeMillis) {
    return active ? transOut(animTimeMillis) : transIn(animTimeMillis);
  }
  
  /**
  * retrieves the component at a given position
  *
  * @param x The x coord of the given position
  * @param y The y coord of the given position
  *
  * @return the UIComponent present at this location, provided it exists
  */
  public UIComponent getComponent(double x, double y) {
    for (UIComponent c : components) {
      if (c.touching(x, y)) return c;
    }
    return null;
  }
  
  /**
  * resets all the clickables in this element
  */
  public void resetClickables() {
    for (UIComponent c : components) {
      if (c instanceof UIInteractable) ((UIInteractable)c).setOut();
    }
  }
  
  /**
  * @return the components tied to this element
  */
  public UIComponent[] getComponents() {return components;}
  
  /**
  * draws the current element
  *
  * @param g The Graphics2D object to draw to
  * @param UIscale The scale to magnify the UI to
  * @param screenSizeX The length of the screen
  * @param screenSizeY The height of the screen
  * @param highlighted The currently highlighted component
  */
  public final void draw(Graphics2D g, int screenSizeX, int screenSizeY, UIInteractable highlighted) {
    if (!active && !transIn) {return;}
    Color[] c = colourPack;
    Vector2[] lurd = {topLeft, botRight};
    
    //Transition if necessary
    if (transIn) {
      if (fadeCount >= fadeDist) {transIn = false; active = true; fadeCount = 0;}
      else {
        fadeCount = Math.min(fadeDist, MathHelp.lerp(0, fadeDist, (1.0*System.currentTimeMillis()-startTimeMillis)/animTimeMillis));
        c = fadeCols(fadeCount/fadeDist);
        lurd = fadeVecs(topLeft, botRight, fadeDist-fadeCount);
      }
    }
    else if (transOut) {
      if (fadeCount >= fadeDist) {transOut = false; active = false; fadeCount = 0; return;}
      else {
        fadeCount = Math.min(fadeDist, MathHelp.lerp(0, fadeDist, (1.0*System.currentTimeMillis()-startTimeMillis)/animTimeMillis));
        c = fadeCols(1-fadeCount/fadeDist);
        lurd = fadeVecs(topLeft, botRight, fadeCount);
      }
    }
    
    Vector2 tL = lurd[0].scale(screenSizeX, screenSizeY);
    Vector2 bR = lurd[1].scale(screenSizeX, screenSizeY);
    
    g.setColor(c[backgroundColour]);
    g.fill(new Rectangle2D.Double(tL.x, tL.y, bR.x-tL.x, bR.y-tL.y));
    
    if (solidBacking) {
      double buffer = 0.007;
      
      Rectangle2D s = new Rectangle2D.Double(
      tL.x+buffer*screenSizeY, tL.y+buffer*screenSizeY, 
      bR.x-tL.x-buffer*2*screenSizeY, 
      bR.y-tL.y-buffer*2*screenSizeY
      );
      
      g.setColor(c[UIColours.BUTTON_BODY]);
      g.fill(s);
      g.setColor(c[UIColours.BUTTON_OUT_ACC]);
      g.draw(s);
    }
    
    draw(g, screenSizeY, tL, bR, c, highlighted);
  }
  
  private final Color[] fadeCols(double percent) {
    Color[] cs = new Color[colourPack.length];
    for (int i = 0; i < cs.length; i++) {
      cs[i] = new Color(colourPack[i].getRed(), colourPack[i].getGreen(), colourPack[i].getBlue(), (int)(colourPack[i].getAlpha()*percent));
    }
    return cs;
  }
  
  private final Vector2[] fadeVecs(Vector2 tL, Vector2 bR, double dist) {
    return new Vector2[] {
      tL.subtract(fadeLeft * dist, fadeUp * dist),
      bR.subtract(fadeRight * dist, fadeDown * dist)
    };
  }
  
  /**
  * draws the current element
  * 
  * @param g The Graphics2D object to draw to
  * @param screenSizeY The height of the screen
  * @param tL The Vector2 representing the top left corner of the element in pixels
  * @param bR The Vector2 representing the bottom right corner of the element in pixels
  * @param c The colours to use in drawing the element
  * @param highlighted The currently highlighted component
  */
  protected abstract void draw(Graphics2D g, int screenSizeY, Vector2 tL, Vector2 bR, Color[] c, UIInteractable highlighted);
}
