package code.ui;

// import code.math.Vector2;

import java.util.*;

import code.math.Vector2;
import code.ui.components.UIComponent;
import code.ui.components.UIInteractable;
import code.ui.elements.UIElement;

import java.awt.Color;
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
  
  private UIElement tempElement = new UIElement(new Vector2(), new Vector2(), new boolean[]{false, false, false, false}) {
    protected void draw(Graphics2D g, int screenSizeY, Vector2 tL, Vector2 bR, Color[] c, UIInteractable highlighted) {}
  };
  
  private List<UIElement> elements;
  private Map<UIState, Mode> modes;
  private UIState current;
  
  /**
  * Constructor for objects of class Pane
  */
  public UIPane() {
    elements = new ArrayList<UIElement>();
    modes = new HashMap<UIState, Mode>();
    modes.put(UIState.DEFAULT, new Mode(this::retMode));
  }
  
  public void reset() {
    clear();
    setMode(UIState.DEFAULT);
  }
  
  public void clear() {
    for (UIElement e : elements) {
      e.deactivate();
    }
  }
  
  public UIElement getPane(int i) {
    return elements.get(i);
  }
  
  public void addElement(UIElement e) {
    elements.add(e);
  }
  
  public void removeElement(UIElement e) {
    elements.remove(e);
  }
  
  public UIElement getTempElement() {
    return tempElement;
  }
  
  public void setTempElement(UIElement tempElement) {
    this.tempElement = tempElement;
  }
  
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
  
  public void back() {
    modes.get(current).back().perform();
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
    addMode(name, e, null);
  }
  
  public void addMode(UIState name, UIElement e, UIState parent) {
    addMode(name, e, parent, this::retMode);
  }
  
  public void addMode(UIState name, UIElement e, UIState parent, UIAction back) {
    if (!modes.containsKey(name)) {modes.put(name, new Mode(parent, back));}
    if (!elements.contains(e)) elements.add(e);
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
  public UIComponent getComponent(double x, double y) {
    UIComponent res = null;
    for (UIElement e : elements) {
      if (!e.isActive()) continue;
      UIComponent c = e.getComponent(x, y);
      if (c!=null) res = c;
    }

    if (!tempElement.isActive()) return res;
    
    UIComponent c = tempElement.getComponent(x, y);

    return c!=null ? c : res;
  }
  
  /**
  * Resets all UIClickables belonging to this pane
  */
  public void resetClickables() {
    for (UIElement e : elements) {
      e.resetClickables();
    }
  }
  
  public void draw(Graphics2D g, int screenSizeX, int screenSizeY, UIInteractable highlighted) {
    for (UIElement e : elements) {
      e.draw(g, screenSizeX, screenSizeY, highlighted);
    }
    
    tempElement.draw(g, screenSizeX, screenSizeY, highlighted);
  }
}

class Mode {
  private final List<UIElement> elems = new ArrayList<UIElement>();
  private UIState parent;
  private final UIAction back;
  
  public Mode(UIAction back) {
    this(null, back);
  }
  
  public Mode(UIState parent, UIAction back) {
    if (back == null) throw new RuntimeException();
    this.parent = parent;
    this.back = back;
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
  
  public UIAction back() {
    return back;
  }
}
