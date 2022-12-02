package code.ui.components;

import java.awt.Color;

import java.awt.Graphics2D;

public abstract class UIComponent {
  
  /**
   * Text representing this component, ideally to be drawn onto the component in some way, so a user may identify the purpose of the component
   */
  protected String text;

  protected float x = 0;
  protected float y = 0;
  protected float width = 0;
  protected float height = 0;

  /**
   * Sets the text within this component to something new
   * 
   * @param text Replacement text
   * @return The successfully changed text
   */
  public String setText(String text) {
    return this.text = text;
  }

  /**
   * Gets the text representing this component
   * 
   * @return The text
   */
  public String getText() {
    return text;
  }

  /**
   * Determines whether a set of coordinates intersects the bounds of this component's last drawn position
   * 
   * @param oX The x coordinate of the location to check
   * @param oY The y coordinate of the location to check
   * @return true if the location intersects with this component
   */
  public boolean touching(double oX, double oY) {
    if (oX > x && oX < x+width && oY > y && oY < y+height) {
      return true;
    }
    return false;
  }

  /**
   * Draws this component to a Graphics2D object at a given position and size
   * 
   * @param g The Graphics2D object to draw to
   * @param x The x coordinate to draw the component from
   * @param y The y coordinate to draw the component from
   * @param width The width in pixels to scale the horizontal size of the component to
   * @param height The height in pixels to scale the vertical size of the component to
   * @param colours The colours to use in drawing the component
   */
  public final void draw(Graphics2D g, float x, float y, float width, float height, Color... colours) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;

    draw(g, colours);
  }

  /**
   * Draws this component to a Graphics2D object at a given position and size
   * 
   * @param g The Graphics2D object to draw to
   * @param colours The colours to use in drawing the component
   */
  protected abstract void draw(Graphics2D g, Color... colours);
}
