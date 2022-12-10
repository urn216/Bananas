package code.math;

/**
* class helping do file stuff
*/
public abstract class MathHelp {
  /**
  * Clamps a value to one between an upper and lower bound.
  *
  * @param val The value to clamp.
  * @param l The lower bound.
  * @param u The upper bound.
  *
  * @return The clamped value.
  */
  public static final double clamp(double val, double l, double u) {
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
  public static final double lerp(double start, double end, double p) {
    return start + (end-start)*p;
  }

  /**
   * A formula giving the probability of any given point being the outcome of normally distributed data
   * 
   * @param x the value of which to give the probability
   * @param mu the mean value of the normal distribution
   * @param sigma the standard deviation of the normal distribution
   * 
   * @return the probability that any datapoint will be x on a normal distribution curve
   */
  public static final double normDistrib(double x, double mu, double sigma) {
    return (Math.exp(-0.5*Math.pow((x-mu)/sigma, 2)))/(sigma*Math.sqrt(2*Math.PI));
  }

  /**
   * A formula giving the possible outcomes of a given probability in a normal distribution curve
   * 
   * @param p the probability to give the result of
   * @param mu the mean value of the normal distribution
   * @param sigma the standard deviation of the normal distribution
   * 
   * @return the x values of a given probability showing up on a normal distribution curve
   */
  public static final double[] inverseNormDistrib(double p, double mu, double sigma) {
    double offset = sigma*Math.sqrt(-2*Math.log(p*sigma*Math.sqrt(2*Math.PI)));
    return new double[]{mu+offset, mu-offset};
  }
}
