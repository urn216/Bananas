package code.math;

/**
* class helping do file stuff
*/
public class MathHelp {
  /**
  * Clamps a value to one between an upper and lower bound.
  *
  * @param val The value to clamp.
  * @param l The lower bound.
  * @param u The upper bound.
  *
  * @return The clamped value.
  */
  public static double clamp(double val, double l, double u) {
    return Math.min(Math.max(val, l), u);
  }

  /**
  * Gives a value a certain percentage of the way from one point to another.
  *
  * @param start The starting value.
  * @param end The ending value.
  * @param p The percentage of the way through the transition.
  *
  * @return The lerped value.
  */
  public static double lerp(double start, double end, double p) {
    return start + (end-start)*p;
  }
}
