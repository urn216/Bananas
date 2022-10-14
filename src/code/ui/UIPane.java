package code.ui;

// import code.math.Vector2;

import java.util.*;
import java.awt.Graphics2D;

// import java.awt.Color;
//import java.awt.Font;

/**
* Write a description of class Pane here.
*
* @author (your name)
* @version (a version number or a date)
*/
public class UIPane {
  private List<UIElement> elements;
  private Map<UIState, Mode> modes;
  private UIState current;

  /**
  * Constructor for objects of class Pane
  */
  public UIPane() {
    elements = new ArrayList<UIElement>();
    modes = new HashMap<UIState, Mode>();
    modes.put(UIState.DEFAULT, new Mode(new ArrayList<UIElement>()));
  }

  public void reset() {
    current = null;
    setMode(UIState.DEFAULT);
  }

  public UIElement getPane(int i) {
    return elements.get(i);
  }

  public void addElement(UIElement e) {
    elements.add(e);
  }

  // public void addElement(Vector2 tL, Vector2 bR, Color bg, boolean[] ties) {
  //   elements.add(new UIElement(tL, bR, bg, ties));
  // }

  // public void addElement(Vector2 tL, Vector2 bR, String title, int style, int size, Color bg, Color acc, boolean[] ties) {
  //   elements.add(new UIElement(tL, bR, title, style, size, bg, acc, ties));
  // }

  // public void addElement(Vector2 tL, Vector2 bR, double buff, Color bg, Color acc, Color hov, Color out, Color in, Color lock, String[] bNames, boolean[] ties) {
  //   elements.add(new UIElement(tL, bR, buff, bg, acc, hov, out, in, lock, bNames, ties));
  // }

  public void transOut() {
    for (UIElement e : elements) {
      e.transOut();
    }
  }

  public void transIn() {
    for (UIElement e : elements) {
      e.transIn();
    }
  }

  public boolean isTransitioning() {
    for (UIElement e : elements) {
      if (e.isTransitioning()) return true;
    }
    return false;
  }

  public void retMode() {
    setMode(modes.get(current).getParent());
  }

  public void setMode(UIState name) {
    if (isTransitioning()
    ||  name == null
    ||  modes.get(name) == null
    ||  name.equals(current)) return;

    Mode mode = modes.get(name);
    current = name;
    for (UIElement e : elements) {
      if (mode.contains(e)) {e.transIn();}
      else {e.transOut();}
    }
  }

  public UIState getMode() {
    return current;
  }

  public void addMode(UIState name, UIElement e) {
    if (!modes.containsKey(name)) {modes.put(name, new Mode(new ArrayList<UIElement>()));}
    modes.get(name).add(e);
  }

  public void addMode(UIState name, UIElement e, UIState parent) {
    if (!modes.containsKey(name)) {modes.put(name, new Mode(new ArrayList<UIElement>(), parent));}
    modes.get(name).add(e);
  }

  public void setModeParent(UIState name, UIState parent) {
    Mode mode = modes.get(name);
    if (mode == null) return;
    mode.setParent(parent);
  }

  /**
  * Looks through all the elements in this pane to retrieve the top-most clickable at a given location
  *
  * @param x the x coord to check
  * @param y the y coord to check
  *
  * @return the UIClickable present at this location, or null if none applicable
  */
  public UIInteractable getClickable(double x, double y) {
    UIInteractable res = null;
    for (UIElement e : elements) {
      UIInteractable c = e.getClickable(x, y);
      if (e.isActive() && c!=null) res = c;
    }
    return res;
  }

  /**
  * Resets all UIClickables belonging to this pane
  */
  public void resetClickables() {
    for (UIElement e : elements) {
      e.resetClickables();
    }
  }

  public void draw(Graphics2D g, double UIscale, int screenSizeX, int screenSizeY, UIInteractable highlighted) {
    double[] empty = {};
    for (UIElement e : elements) {
      e.draw(g, UIscale, screenSizeX, screenSizeY, highlighted, empty);
    }
  }

  public void draw(Graphics2D g, double UIscale, int screenSizeX, int screenSizeY, UIInteractable highlighted, double[] playerStats) {
    for (UIElement e : elements) {
      e.draw(g, UIscale, screenSizeX, screenSizeY, highlighted, playerStats);
    }
  }
}

class Mode {
  private UIState parent = null;
  private List<UIElement> elems;

  public Mode(List<UIElement> elems) {
    this.elems = elems;
  }

  public Mode(List<UIElement> elems, UIState parent) {
    this.elems = elems;
    this.parent = parent;
  }

  public void add(UIElement e) {
    elems.add(e);
  }

  public boolean contains(Object e) {
    return elems.contains(e);
  }

  public UIState getParent() {
    return parent;
  }

  public void setParent(UIState parent) {
    this.parent = parent;
  }
}
