package code.ui;

import code.ui.components.UIComponent;
import code.ui.components.UIText;
import code.ui.components.interactables.UISlider;

/**
 * A helper class for UI related calculations
 */
public abstract class UIHelp {

  /**
   * Calculates the height of a list element with a given buffer size and component heights
   * 
   * @param buff the buffer between components
   * @param componentHeights the heights of all the components in the list
   * 
   * @return the calclated height
   */
  public static final double listHeight(double buff, double... componentHeights) {
    return buff * (componentHeights.length + 1) + sum(componentHeights);
  }

  /**
   * Calculates the height of a list element with a given number of components, buffer size, and component height.
   * This assumes all components are the same size
   * 
   * @param numComponents the bumber of components
   * @param buff the buffer between components
   * @param componentHeights the height of all the components in the list
   * 
   * @return the calclated height
   */
  public static final double listHeightDefault(int numComponents, double buff, double componentHeights) {
    return (buff+componentHeights) * numComponents + buff;
  }

  /**
   * Calculates the heights of all the components in a given list, with a given height for 'normally sized' components
   * 
   * @param defaultHeight the height of standard components
   * @param components all the components to find the heights of
   * 
   * @return a list of component heights
   */
  public static final double[] componentHeights(double defaultHeight, UIComponent... components) {
    double[] res = new double[components.length];
    for (int i = 0; i < components.length; i++) {
      UIComponent component = components[i];
      double height = defaultHeight;
      if (component instanceof UIText  ) height /= 2;
      if (component instanceof UISlider) height *= 2;
      res[i] = height;
    }
    return res;
  }

  /**
   * Sums elements in a double array
   * 
   * @param array array of doubles to sum
   * 
   * @return the sum
   */
  private static final double sum(double[] array) {
    double res = 0;
    for (double elem : array) {res+=elem;}
    return res;
  }
}
