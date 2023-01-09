package code.math;

/**
* Immutable 2D Vector using {@code double} coordinates
*/
public class Vector2  // implements Comparable<Vector2>
{
  /**
   * The first coordinate of this vector.
   */
  public final double x;
  /**
   * The second coordinate of this vector.
   */
  public final double y;

  /**
   * Constructs a new immutable {@code Vector2} from two double values.
   * 
   * @param x the x coordinate of this vector
   * @param y the y coordinate of this vector
   */
  public Vector2(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Constructs a new immutable {@code Vector2} from an existing Vector2.
   * 
   * @param old the pre-existing Vector2 to copy
   */
  public Vector2(Vector2 old) {
    this.x = old.x;
    this.y = old.y;
  }

  /**
   * Constructs a new immutable {@code Vector2} with values set to {@code 0}.
   */
  public Vector2() {
    this.x = 0;
    this.y = 0;
  }

  /**
   * Constructs a new immutable {@code Vector2} from an angle with a specified magnitude
   * 
   * @param ang the angle from north from which to generate the {@code Vector2}.
   * @param length the magnitude to give this {@code Vector2}.
   * 
   * @return a new {@code Vector2}.
   */
  public static Vector2 fromAngle(double ang, double length) {
    return new Vector2(Math.cos(ang)*length, Math.sin(ang)*length);
  }

  /**
   * Constructs a new immutable {@code Vector2} with coordinates representing the absolute values of an existing {@code Vector2}.
   * 
   * @param input the {@code Vector2} to calculate the absolute values of
   * 
   * @return a new, completely non-negative {@code Vector2}.
   */
  public static Vector2 abs(Vector2 input) {
    return new Vector2(Math.abs(input.x), Math.abs(input.y));
  }

  /**
   * @return the length of the {@code Vector2}
   */
  public double magnitude() {
    return Math.sqrt((x*x)+(y*y));
  }

  /**
   * @return the length of the {@code Vector2} squared
   */
  public double magsquare() {
    return (x*x)+(y*y);
  }

  /**
   * Creates a normal {@code Vector2} from this {@code Vector2}
   * 
   * @return a unit {@code Vector2} facing in the same direction as this {@code Vector2}
   */
  public Vector2 unitize() {
    double mag = magnitude();
    if (mag == 0) {mag = 1;}
    return new Vector2(x/mag, y/mag);
  }

  /**
   * performs subtraction on this {@code Vector2} with another {@code Vector2}.
   * <p>
   * Subtraction in the form of (this.x-other.x, this.y-other.y)
   * 
   * @param other the {@code Vector2} to subtract from this
   * 
   * @return a new immutable {@code Vector2} with updated values
   */
  public Vector2 subtract(Vector2 other) {
    return new Vector2(this.x-other.x, this.y-other.y);
  }

  /**
   * performs subtraction on this {@code Vector2} with another set of two doubles.
   * <p>
   * Subtraction in the form of (this.x-x, this.y-y)
   * 
   * @param x the x coordinate to subtract from this
   * @param y the y coordinate to subtract from this
   * 
   * @return a new immutable {@code Vector2} with updated values
   */
  public Vector2 subtract(double x, double y) {
    return new Vector2(this.x-x, this.y-y);
  }

  /**
   * performs subtraction on this {@code Vector2} with a single value on both coordinates of this {@code Vector2}.
   * <p>
   * Subtraction in the form of (this.x-other, this.y-other)
   * 
   * @param other the value to subtract from both coordinates of this {@code Vector2}
   * 
   * @return a new immutable {@code Vector2} with updated values
   */
  public Vector2 subtract(double other) {
    return new Vector2(this.x-other, this.y-other);
  }

  /**
   * performs addition on this {@code Vector2} with another {@code Vector2}.
   * <p>
   * Addition in the form of (this.x+other.x, this.y+other.y)
   * 
   * @param other the {@code Vector2} to add to this
   * 
   * @return a new immutable {@code Vector2} with updated values
   */
  public Vector2 add(Vector2 other) {
    return new Vector2(this.x+other.x, this.y+other.y);
  }

  /**
   * performs addition on this {@code Vector2} with another set of two doubles.
   * <p>
   * Addition in the form of (this.x+x, this.y+y)
   * 
   * @param x the x coordinate to add to this
   * @param y the y coordinate to add to this
   * 
   * @return a new immutable {@code Vector2} with updated values
   */
  public Vector2 add(double x, double y) {
    return new Vector2(this.x+x, this.y+y);
  }

  /**
   * performs addition on this {@code Vector2} with a single value on both coordinates of this {@code Vector2}.
   * <p>
   * Addition in the form of (this.x+other, this.y+other)
   * 
   * @param other the value to add to both coordinates of this {@code Vector2}
   * 
   * @return a new immutable {@code Vector2} with updated values
   */
  public Vector2 add(double other) {
    return new Vector2(this.x+other, this.y+other);
  }

  /**
   * performs multiplication on this {@code Vector2} with another {@code Vector2}.
   * <p>
   * Multiplication in the form of (this.x*other.x, this.y*other.y)
   * 
   * @param other the {@code Vector2} to scale this by
   * 
   * @return a new immutable {@code Vector2} with updated values
   */
  public Vector2 scale(Vector2 other) {
    return new Vector2(this.x*other.x, this.y*other.y);
  }

  /**
   * performs multiplication on this {@code Vector2} with another set of two doubles.
   * <p>
   * Multiplication in the form of (this.x*x, this.y*y)
   * 
   * @param x the x coordinate to scale this by
   * @param y the y coordinate to scale this by
   * 
   * @return a new immutable {@code Vector2} with updated values
   */
  public Vector2 scale(double x, double y) {
    return new Vector2(this.x*x, this.y*y);
  }

  /**
   * performs multiplication on this {@code Vector2} with a single value on both coordinates of this {@code Vector2}.
   * <p>
   * Multiplication in the form of (this.x*other, this.y*other)
   * 
   * @param other the value to scale both coordinates of this {@code Vector2} by
   * 
   * @return a new immutable {@code Vector2} with updated values
   */
  public Vector2 scale(double other) {
    return new Vector2(this.x*other, this.y*other);
  }

  /**
   * Performs the dot product on this {@code Vector2} with another {@code Vector2}
   * 
   * @param other the other {@code Vector2} to dot against this
   * 
   * @return the dot product
   */
  public double dot(Vector2 other) {
    return this.x*other.x + this.y*other.y;
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }

  @Override
  public Vector2 clone() {
    return new Vector2(this);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof Vector2 ? equals((Vector2)other) : false;
  }

  /**
   * Checks if a {@code Vector2} is equivalent to this one; i.e. the {@code x} and {@code y} coordinates are the same.
   * 
   * @param other the {@code Vector2} to compare against this one.
   * @return true if the {@code x} and {@code y} coordinates of each {@code Vector2} are equal
   */
  public boolean equals(Vector2 other) {
    return other.x==this.x && other.y==this.y;
  }

  // public double toAngle() {
  //   return Math.atan2(y, x);
  // }
  // 
  // public int hashCode() {
  //   return (Double.hashCode(Math.round(x))^Double.hashCode(Math.round(y)));
  // }
  //
  // public int compareTo(Vector2 other) {
  //   int comp = Double.compare(this.magsquare(), other.magsquare());
  //   if (comp==0) {
  //     comp = Double.compare(this.toAngle(), other.toAngle());
  //   }
  //   return comp;
  // }
}
