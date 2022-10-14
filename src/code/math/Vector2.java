package code.math;

/**
* 2D Vector
*/
public class Vector2  // implements Comparable<Vector2>
{
  public final double x;
  public final double y;

  public Vector2(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public Vector2(Vector2 old) {
    this.x = old.x;
    this.y = old.y;
  }

  public Vector2() {
    this.x = 0;
    this.y = 0;
  }

  public static Vector2 fromAngle(double ang, double length) {
    return new Vector2(Math.cos(ang)*length, Math.sin(ang)*length);
  }

  public static Vector2 abs(Vector2 input) {
    return new Vector2(Math.abs(input.x), Math.abs(input.y));
  }

  public String toString() {
    return "(" + x + ", " + y + ")";
  }

  public double magnitude() {
    return Math.sqrt((x*x)+(y*y));
  }

  public double magsquare() {
    return (x*x)+(y*y);
  }

  public Vector2 unitize() {
    double mag = magnitude();
    if (mag == 0) {mag = 1;}
    return new Vector2(x/mag, y/mag);
  }

  public Vector2 subtract(Vector2 other) {
    return new Vector2(this.x-other.x, this.y-other.y);
  }

  public Vector2 subtract(double x, double y) {
    return new Vector2(this.x-x, this.y-y);
  }

  public Vector2 subtract(double other) {
    return new Vector2(this.x-other, this.y-other);
  }

  public Vector2 add(Vector2 other) {
    return new Vector2(this.x+other.x, this.y+other.y);
  }

  public Vector2 add(double x, double y) {
    return new Vector2(this.x+x, this.y+y);
  }

  public Vector2 add(double other) {
    return new Vector2(this.x+other, this.y+other);
  }

  public Vector2 scale(Vector2 other) {
    return new Vector2(this.x*other.x, this.y*other.y);
  }

  public Vector2 scale(double x, double y) {
    return new Vector2(this.x*x, this.y*y);
  }

  public Vector2 scale(double other) {
    return new Vector2(this.x*other, this.y*other);
  }

  public double dot(Vector2 other) {
    return this.x*other.x + this.y*other.y;
  }

  public Vector2 copy() {
    return new Vector2(this);
  }

  public double toAngle() {
    return Math.atan2(y, x);
  }

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



  //public void setX(double x) {this.x = x; }

  //public void setY(double y) {this.y = y; }

  //public double getX() {return x; }

  //public double getY() {return y; }
}
