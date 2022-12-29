package code.ui;

import code.math.Vector2;
import code.math.Vector2I;

import code.ui.components.UIComponent;
import code.ui.components.UIInteractable;
import code.ui.components.interactables.UISlider;
import code.ui.components.interactables.UITextfield;
import code.ui.elements.ElemInfo;

import java.awt.event.KeyEvent;

import java.util.*;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
* UI class
*/
public abstract class UIController {
  
  private static final HashMap<String, UIPane> panes = new HashMap<String, UIPane>();;
  private static UIPane current = new UIPane();
  
  private static UIInteractable highlighted = null;
  private static UITextfield activeTextfield = null;
  private static UISlider activeSlider = null;
  
  public static void displayWarning(double bufferHeight, double componentHeight, String... message) {
    double height = UIHelp.calculateListHeight(bufferHeight, UIHelp.calculateComponentHeights(componentHeight, message));
    double width = UIHelp.calculateElementWidth(bufferHeight, UIHelp.calculateComponentWidths(componentHeight/2, message));

    ElemInfo info = new ElemInfo(
    new Vector2(0.5-width/2, 0.5-height/2),
    new Vector2(0.5+width/2, 0.5+height/2),
    bufferHeight, 
    new boolean[] {false, false, false, false}, 
    message
    );
    
    current.setTempElement(info);
    info.transIn();
  }
  
  public static void putPane(String s, UIPane p) {
    panes.put(s, p);
  }
  
  public static UIPane getPane(String name) {
    return panes.get(name);
  }
  
  public static UIPane setCurrent(String name) {
    current.clear();
    current = panes.get(name);
    current.reset();
    return current;
  }
  
  public static void setMode(UIState name) {
    current.setMode(name);
  }
  
  public static UIState getMode() {
    return current.getMode();
  }
  
  public static void retMode() {
    current.retMode();
  }
  
  public static void back() {
    current.back();
  }
  
  public static void transOut() {
    current.transOut();
  }
  
  public static void transIn() {
    current.transIn();
  }
  
  public static boolean isTransitioning() {
    return current.isTransitioning();
  }
  
  public static UIComponent getComponent(double x, double y) {
    return current == null ? null : current.getComponent(x, y);
  }
  
  public static void resetClickables() {
    current.resetClickables();
  }
  
  /**
  * Sets the highlighted UIInteractable
  *
  * @param highlighted The UIInteractable to set as highlighted
  */
  public static void setHighlighted(UIInteractable highlighted) {UIController.highlighted = highlighted;}
  
  /**
  * Gets the highlighted UIInteractable
  *
  * @return The UIInteractable currently highlighted
  */
  public static UIInteractable getHighlighted() {return UIController.highlighted;}
  
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
    setHighlighted(comp instanceof UIInteractable ? (UIInteractable) comp : null);

    useSlider(x);
  }
  
  public static boolean press() {
    if (highlighted == null) return false;
    if (highlighted.isLocked()) return true;
    highlighted.setIn();
    if (highlighted instanceof UISlider) activeSlider = (UISlider)highlighted;
    return true;
  }
  
  public static void release() {
    selectInteractable(highlighted);
    activeSlider = null;
  }
  
  public static void draw(Graphics2D g, int screenSizeX, int screenSizeY) {
    current.draw(g, screenSizeX, screenSizeY, highlighted);
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
      if (highlighted == null || highlighted == activeTextfield) {
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
