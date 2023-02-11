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
* A collection of {@code UIElement}s stored within one window pane.
* This {@code UIPane} should contain a number of {@code Mode}s which
* can be transitioned between easily within one section of a User Interface
*/
public class UIPane {
  
  private static UIElement TEMP_ELEMENT_TEMPLATE = new UIElement(new Vector2(), new Vector2(), new boolean[]{false, false, false, false}) {
    protected void draw(Graphics2D g, int screenSizeY, Vector2 tL, Vector2 bR, Color[] c, UIInteractable highlighted) {}
  };

  private UIElement tempElement = TEMP_ELEMENT_TEMPLATE;
  
  private List<UIElement> elements;
  private Map<UIState, Mode> states;
  private UIState currentState;
  
  /**
  * Constructor for {@code UIPane}s
  */
  public UIPane() {
    elements = new ArrayList<UIElement>();
    states = new HashMap<UIState, Mode>();
    currentState = null;
    
    states.put(UIState.DEFAULT, new Mode(this::retState));
  }
  
  /**
   * Clears the {@code UIPane}, setting all {@code UIElement}s within 
   * to their inactive state, and then activates the {@code DEFAULT} mode. 
   */
  public void reset() {
    clear();
    setState(UIState.DEFAULT, UIController.TRANSITION_ANIMATION_TIME_MILLIS);
  }
  
  /**
   * Clears the {@code UIPane}, setting all {@code UIElement}s within 
   * to their inactive state.
   */
  public void clear() {
    currentState = null;

    for (UIElement e : elements) {
      e.deactivate();
    }

    tempElement.deactivate();
  }
  
  /**
   * Retrieves a {@code UIElement} at a given index of this {@code UIPane}'s list of {@code UIElement}s
   * 
   * @param i the index of the {@code UIElement} to retrieve
   * 
   * @return the requested {@code UIElement}
   */
  public UIElement getElement(int i) {
    return elements.get(i);
  }
  
  /**
   * Adds a {@code UIElement} to this {@code UIPane}'s list of {@code UIElement}s.
   * 
   * @param e the {@code UIElement} to add
   */
  public void addElement(UIElement e) {
    elements.add(e);
  }
  
  /**
   * Removes a {@code UIElement} from this {@code UIPane}'s list of {@code UIElement}s.
   * 
   * @param e the {@code UIElement} to remove
   */
  public void removeElement(UIElement e) {
    elements.remove(e);
  }
  
  /**
   * Retrieves this {@code UIPane}'s temporary overlay {@code UIElement}
   * 
   * @return the requested {@code UIElement}
   */
  public UIElement getTempElement() {
    return tempElement;
  }
  
  /**
   * Sets this {@code UIPane}'s temporary overlay {@code UIElement} to the given element
   * 
   * @param tempElement the {@code UIElement} to use as the temporary element
   */
  public void setTempElement(UIElement tempElement) {
    this.tempElement = tempElement;
  }

  /**
   * Clears this {@code UIPane}'s temporary overlay {@code UIElement}
   */
  public void clearTempElement() {
    this.tempElement = TEMP_ELEMENT_TEMPLATE;
  }
  
  /**
   * Transitions all the {@code UIElement}s within this {@code UIPane} to their inactive state
   */
  public void transOut() {
    for (UIElement e : elements) {
      e.transOut(UIController.DEFAULT_ANIMATION_TIME_MILLIS);
    }
  }
  
  /**
   * Transitions all the {@code UIElement}s within this {@code UIPane} to their active state
   */
  public void transIn() {
    for (UIElement e : elements) {
      e.transIn(UIController.DEFAULT_ANIMATION_TIME_MILLIS);
    }
  }
  
  /**
   * Checks to see if any {@code UIElement}s within this {@code UIPane} are transitioning
   * 
   * @return {@code true} if there is at least one {@code UIElement} transitioning
   */
  public boolean isTransitioning() {
    for (UIElement e : elements) {
      if (e.isTransitioning()) return true;
    }
    return false;
  }
  
  /**
   * Sets the current state to its previous one, most commonly changing to its parent {@code UIState}
   */
  public void back() {
    states.get(currentState).back();
  }
  
  /**
   * Sets the current state to the current state's parent
   */
  public void retState() {
    setState(states.get(currentState).getParent(), UIController.DEFAULT_ANIMATION_TIME_MILLIS);
  }
  
  /**
   * Changes the current {@code UIState} to the given one, assuming the given {@code UIState} is valid 
   * and the current state is not actively transitioning
   * 
   * @param state the {@code UIState} to switch to
   * @param animTimeMillis the length of the transition in milliseconds
   */
  public void setState(UIState state, long animTimeMillis) {
    if (isTransitioning()
    ||  state            == currentState
    ||  state            == null
    ||  states.get(state) == null   ) return;
    
    Mode mode = states.get(state);
    currentState = state;
    for (UIElement e : elements) {
      if (mode.contains(e)) {e.transIn(animTimeMillis);}
      else {e.transOut(animTimeMillis);}
    }

    tempElement.transOut(UIController.DEFAULT_ANIMATION_TIME_MILLIS);
  }
  
  /**
   * Gets the current {@code UIState} that is currently being displayed through this {@code UIPane}
   * 
   * @return the {@code UIState} currently active
   */
  public UIState getState() {
    return currentState;
  }

  /**
   * Adds a new empty state to this {@code UIPane} - if there is no other state with the same name already present.
   * 
   * @param state the {@code UIState} to represent the new state
   */
  public void addState(UIState state) {
    if (!states.containsKey(state)) {states.put(state, new Mode(null, this::retState));}
  }
  
  /**
   * Adds a new state to this {@code UIPane} if there is no other state with the same name already present.
   * <p>
   * The state is then populated with the given {@code UIElement}.
   * 
   * @param stateName the {@code UIState} to represent the new state
   * @param e The {@code UIElement} to add
   */
  public void addState(UIState stateName, UIElement e) {
    addState(stateName, e, null);
  }
  
  /**
   * Adds a new state with a given parent state to this {@code UIPane} if there is no other state with the same name already present.
   * <p>
   * The state is then populated with the given {@code UIElement}.
   * 
   * @param stateName the {@code UIState} to represent the new state
   * @param e The {@code UIElement} to add
   * @param parent the {@code UIState} to act as the new state's parent
   */
  public void addState(UIState stateName, UIElement e, UIState parent) {
    addState(stateName, e, parent, this::retState);
  }
  
  /**
   * Adds a new state with a given parent state and 'back' {@code UIAction} to this {@code UIPane} if there is no other state with the same name already present.
   * <p>
   * The state is then populated with the given {@code UIElement}.
   * 
   * @param stateName the {@code UIState} to represent the new state
   * @param e The {@code UIElement} to add
   * @param parent the {@code UIState} to act as the new state's parent
   * @param back the {@code UIAction} to perform when the user tries to back out of this state
   */
  public void addState(UIState stateName, UIElement e, UIState parent, UIAction back) {
    if (!states.containsKey(stateName)) {states.put(stateName, new Mode(parent, back));}
    if (!elements.contains(e)) elements.add(e);
    states.get(stateName).add(e);
  }
  
  /**
   * Sets an existing state's parent state.
   * 
   * @param state the existing state to add a parent to
   * @param parent the desired parent state to link to
   */
  public void setModeParent(UIState state, UIState parent) {
    Mode mode = states.get(state);
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
  * Resets all {@code UIInteractable}s belonging to this pane to their unpressed state
  */
  public void resetClickables() {
    for (UIElement e : elements) {
      e.resetClickables();
    }

    tempElement.resetClickables();
  }
  
  /**
   * Draws the contents of this {@code UIPane} to the screen.
   * 
   * @param g the {@code Graphics2D} object to draw to
   * @param screenSizeX the width of the screen in pixels
   * @param screenSizeY the height of the screen in pixels
   * @param highlighted the {@code UIInteractable} to draw as highlighted 
   */
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
    if (back == null) throw new RuntimeException("Must have a valid return action");
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
  
  public void back() {
    back.perform();
  }
}
