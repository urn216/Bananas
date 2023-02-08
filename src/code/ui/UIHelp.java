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
  public static final double calculateListHeight(double buffer, double... componentHeights) {
    return buffer * (componentHeights.length + 1) + sum(componentHeights);
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
  public static final double calculateListHeightDefault(int numComponents, double buffer, double componentHeights) {
    return (buffer+componentHeights) * numComponents + buffer;
  }

  /**
   * Calculates the heights of all the components in a given list, with a given height for 'normally sized' components
   * 
   * @param defaultHeight the height of standard components
   * @param components all the components to find the heights of
   * 
   * @return a list of component heights
   */
  public static final double[] calculateComponentHeights(double defaultHeight, UIComponent... components) {
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
   * Calculates the heights of all the components in an ElemInfo, with a given height for 'normally sized' components
   * 
   * @param defaultHeight the height of standard components
   * @param components the strings present in the ElemInfo
   * 
   * @return a list of component heights {all the strings, and then the 'ok' button}
   */
  public static final double[] calculateComponentHeights(double defaultHeight, String... components) {
    double[] res = new double[components.length+1];
    for (int i = 0; i < components.length; i++) {
      res[i] = defaultHeight/2;
    }
    res[res.length-1] = defaultHeight;
    return res;
  }

  /**
   * Calculates the width of an arbitrary element with a given buffer size and component widths
   * 
   * @param buffer the buffer between components
   * @param componentWidths the widths of all the components in the element
   * 
   * @return the calclated width
   */
  public static final double calculateElementWidth(double buffer, double... componentWidths) {
    double maxW = 0;
    for (double d : componentWidths) maxW = Math.max(maxW, d);
    return 2*buffer + maxW;
  }

  /**
   * Calculates the widths of all the components in a given list of text, with a given height for the text to appear at
   * 
   * @param textHeight the height of standard text
   * @param componentTexts all the components to find the widths of
   * 
   * @return a list of component widths
   */
  public static final double[] calculateComponentWidths(double textHeight, String... componentTexts) {
    double[] widths = new double[componentTexts.length];
    for (int i = 0; i < componentTexts.length; i++) {
      widths[i] = componentTexts[i].length()*textHeight/3.5;
    }
    return widths;
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
