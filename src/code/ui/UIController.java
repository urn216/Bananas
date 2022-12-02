package code.ui;

import code.core.Settings;
import code.math.Vector2;
import code.ui.components.UIComponent;
import code.ui.components.UIInteractable;
import code.ui.components.interactables.UISlider;
import code.ui.components.interactables.UITextfield;

import java.awt.event.KeyEvent;

import java.util.*;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
* UI class
*/
public class UIController {

  private final Settings globalSettings;

  private final HashMap<String, UIPane> panes;
  private UIPane current = new UIPane();

  private UIInteractable highlighted = null;
  private UITextfield activeTextfield = null;
  private UISlider activeSlider = null;

  public UIController(Settings globalSettings) {
    panes = new HashMap<String, UIPane>();
    this.globalSettings = globalSettings;
  }

  public void putPane(String s, UIPane p) {
    panes.put(s, p);
  }

  public UIPane getPane(String name) {
    return panes.get(name);
  }

  public UIPane setCurrent(String name) {
    current.clear();
    current = panes.get(name);
    current.reset();
    return current;
  }

  public void setMode(UIState name) {
    current.setMode(name);
  }

  public UIState getMode() {
    return current.getMode();
  }

  public void back() {
    if (globalSettings.hasChanged()) {
      globalSettings.saveChanges();
      System.out.println("You changed something, you bastard");
    }
    current.retMode();
  }

  public void transOut() {
    current.transOut();
  }

  public void transIn() {
    current.transIn();
  }

  public boolean isTransitioning() {
    return current.isTransitioning();
  }

  public UIComponent getComponent(double x, double y) {
    return current == null ? null : current.getComponent(x, y);
  }

  public void resetClickables() {
    current.resetClickables();
  }

  /**
  * Sets the highlighted UIInteractable
  *
  * @param highlighted The UIInteractable to set as highlighted
  */
  public void setHighlighted(UIInteractable highlighted) {this.highlighted = highlighted;}

  /**
  * Gets the highlighted UIInteractable
  *
  * @return The UIInteractable currently highlighted
  */
  public UIInteractable getHighlighted() {return this.highlighted;}

  /**
  * Sets the active UITextfield
  *
  * @param textfield The UITextfield to set as active
  */
  public void setActiveTextfield(UITextfield textfield) {this.activeTextfield = textfield;}

  /**
  * Gets the active UITextfield
  *
  * @return The UITextfield currently active
  */
  public UITextfield getActiveTextfield() {return this.activeTextfield;}

  public void cursorMove(int x, int y) {
    UIComponent comp = getComponent(x, y);
    setHighlighted(comp instanceof UIInteractable ? (UIInteractable) comp : null);
  }

  public boolean press() {
    if (highlighted == null) return false;
    if (highlighted.isLocked()) return true;
    highlighted.setIn();
    if (highlighted instanceof UISlider) activeSlider = (UISlider)highlighted;
    return true;
  }

  public void release() {
    selectInteractable(highlighted);
    activeSlider = null;
  }

  public void draw(Graphics2D g, int screenSizeX, int screenSizeY) {
    current.draw(g, screenSizeX, screenSizeY, highlighted);
  }

  public void drawBoundingBox(Graphics2D g, Vector2 a, Vector2 b) {
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
  public void typeKey(KeyEvent e) {
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
  public void useSlider(int x) {
    if (activeSlider != null) activeSlider.moveNode(x);
  }

  /**
  * Activates a given UIInteractable
  *
  * @param interact The UIInteractable to activate
  */
  private void selectInteractable(UIInteractable interact) {
    activeTextfield = null;
    if (interact != null && interact.isIn()) {
      interact.primeAct();
      resetClickables();
      if (interact instanceof UITextfield) interact.setIn();
      return;
    }
    resetClickables();
  }
}
