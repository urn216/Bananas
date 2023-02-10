package code.ui;

import code.math.Vector2;
import code.math.Vector2I;

import code.ui.components.UIComponent;
import code.ui.components.UIInteractable;
import code.ui.components.interactables.UISlider;
import code.ui.components.interactables.UITextfield;
import code.ui.elements.ElemInfo;
import code.ui.elements.UIElement;

import java.awt.event.KeyEvent;

import java.util.*;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
* UI class
*/
public abstract class UIController {

  public static final long DEFAULT_ANIMATION_TIME_MILLIS = 175;
  public static final long TRANSITION_ANIMATION_TIME_MILLIS = 1000;
  
  private static final HashMap<String, UIPane> panes = new HashMap<String, UIPane>();;
  private static UIPane currentPane = new UIPane();
  
  private static UIInteractable highlightedInteractable = null;
  private static UITextfield activeTextfield = null;
  private static UISlider activeSlider = null;
  
  public static synchronized void displayWarning(double bufferHeight, double componentHeight, String... message) {
    double height = UIHelp.calculateListHeight(bufferHeight, UIHelp.calculateComponentHeights(componentHeight, message));
    double width = UIHelp.calculateElementWidth(bufferHeight, UIHelp.calculateComponentWidths(componentHeight/2, message));

    ElemInfo info = new ElemInfo(
    new Vector2(0.5-width/2, 0.5-height/2),
    new Vector2(0.5+width/2, 0.5+height/2),
    bufferHeight, 
    new boolean[] {false, false, false, false}, 
    message
    );
    
    currentPane.setTempElement(info);
    info.transIn(DEFAULT_ANIMATION_TIME_MILLIS);
  }

  public static synchronized void displayTempElement(UIElement temp) {
    currentPane.setTempElement(temp);
    temp.transIn(DEFAULT_ANIMATION_TIME_MILLIS);
  }

  public static synchronized void clearTempElement() {
    currentPane.clearTempElement();
  }
  
  public static void putPane(String s, UIPane p) {
    panes.put(s, p);
  }
  
  public static UIPane getPane(String name) {
    return panes.get(name);
  }
  
  public static synchronized UIPane setCurrentPane(String name) {
    currentPane.clear();
    currentPane = panes.get(name);
    currentPane.reset();
    return currentPane;
  }
  
  public static synchronized void setMode(UIState name) {
    currentPane.setMode(name, DEFAULT_ANIMATION_TIME_MILLIS);
  }
  
  public static UIState getMode() {
    return currentPane.getMode();
  }

  public static boolean isMode(UIState name) {
    return currentPane.getMode() == name;
  }
  
  public static synchronized void retMode() {
    currentPane.retMode();
  }
  
  public static synchronized void back() {
    currentPane.back();
  }
  
  public static synchronized void transOut() {
    currentPane.transOut();
  }
  
  public static synchronized void transIn() {
    currentPane.transIn();
  }
  
  public static boolean isTransitioning() {
    return currentPane.isTransitioning();
  }
  
  public static UIComponent getComponent(double x, double y) {
    return currentPane == null ? null : currentPane.getComponent(x, y);
  }
  
  public static synchronized void resetClickables() {
    currentPane.resetClickables();
  }
  
  /**
  * Sets the highlighted UIInteractable
  *
  * @param highlighted The UIInteractable to set as highlighted
  */
  public static void setHighlightedInteractable(UIInteractable highlighted) {UIController.highlightedInteractable = highlighted;}
  
  /**
  * Gets the highlighted UIInteractable
  *
  * @return The UIInteractable currently highlighted
  */
  public static UIInteractable getHighlightedInteractable() {return UIController.highlightedInteractable;}
  
  /**
  * Sets the active UITextfield
  *
  * @param textfield The UITextfield to set as active
  */
  public static void setActiveTextfield(UITextfield textfield) {UIController.activeTextfield = textfield;}
  
  /**
  * Gets the active UITextfield
  *
  * @return The UITextfield currently active
  */
  public static UITextfield getActiveTextfield() {return UIController.activeTextfield;}

  public static void cursorMove(Vector2I pos) {
    cursorMove(pos.x, pos.y);
  }
  
  public static void cursorMove(int x, int y) {
    UIComponent comp = getComponent(x, y);
    setHighlightedInteractable(comp instanceof UIInteractable ? (UIInteractable) comp : null);

    useSlider(x);
  }
  
  /**
   * Presses the highlighted {@code UIInteractible} in, if one is present.
   * 
   * @return true if a highlighted {@code UIInteractible} was pressed
   */
  public static boolean press() {
    if (highlightedInteractable == null) return false;
    if (highlightedInteractable.isLocked()) return true;
    highlightedInteractable.setIn();
    if (highlightedInteractable instanceof UISlider) activeSlider = (UISlider)highlightedInteractable;
    return true;
  }
  
  public static void release() {
    selectInteractable(highlightedInteractable);
    activeSlider = null;
  }
  
  public static void draw(Graphics2D g, int screenSizeX, int screenSizeY) {
    currentPane.draw(g, screenSizeX, screenSizeY, highlightedInteractable);
  }
  
  public static void drawBoundingBox(Graphics2D g, Vector2 a, Vector2 b) {
    double u = a.y;
    double d = b.y;
    if (a.y > b.y) {
      u = b.y;
      d = a.y;
    }
    
    double l = a.x;
    double r = b.x;
    if (a.x > b.x) {
      l = b.x;
      r = a.x;
    }
    
    g.setColor(UIColours.DEFAULT[UIColours.BUTTON_OUT_ACC]);
    g.draw(new Rectangle2D.Double(l, u, r-l, d-u));
  }
  
  /**
  * Types a key into the activeTextField. Assumes activeTextField is not null
  *
  * @param e The KeyEvent to type into activeTextField
  */
  public static void typeKey(KeyEvent e) {
    int keyCode = e.getKeyCode();
    if (keyCode == KeyEvent.VK_ENTER) {
      if (highlightedInteractable == null || highlightedInteractable == activeTextfield) {
        activeTextfield.enterAct();
        return;
      }
    }
    if (keyCode >= 32 && keyCode <= 122) {activeTextfield.print(e.getKeyChar()); return;}
    if (keyCode == KeyEvent.VK_BACK_SPACE) {activeTextfield.backspace(); return;}
  }
  
  /**
  * Changes the value held in the activeSlider.
  *
  * @param x The x coordinate of the cursor
  */
  private static void useSlider(int x) {
    if (activeSlider != null) activeSlider.moveNode(x);
  }
  
  /**
  * Activates a given UIInteractable
  *
  * @param interact The UIInteractable to activate
  */
  private static void selectInteractable(UIInteractable interact) {
    if (activeTextfield != null) activeTextfield.clearAct();
    if (interact != null && interact.isIn()) {
      interact.primeAct();
      resetClickables();
      if (interact instanceof UITextfield) interact.setIn();
      return;
    }
    resetClickables();
  }
}
