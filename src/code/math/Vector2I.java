package code.math;

/**
* 2D Vector
*/
public class Vector2I extends Vector2  // implements Comparable<Vector2>
{
  public final int x;
  public final int y;

  public Vector2I(int x, int y) {
    super(x, y);
    
    this.x = x;
    this.y = y;
  }

  public Vector2I(Vector2I old) {
    super(old);
    
    this.x = old.x;
    this.y = old.y;
  }

  public Vector2I() {
    super();
    
    this.x = 0;
    this.y = 0;
  }

  public static Vector2I abs(Vector2I input) {
    return new Vector2I(Math.abs(input.x), Math.abs(input.y));
  }

  public Vector2I subtract(Vector2I other) {
    return new Vector2I(this.x-other.x, this.y-other.y);
  }

  public Vector2I subtract(int x, int y) {
    return new Vector2I(this.x-x, this.y-y);
  }

  public Vector2I subtract(int other) {
    return new Vector2I(this.x-other, this.y-other);
  }

  public Vector2I add(Vector2I other) {
    return new Vector2I(this.x+other.x, this.y+other.y);
  }

  public Vector2I add(int x, int y) {
    return new Vector2I(this.x+x, this.y+y);
  }

  public Vector2I add(int other) {
    return new Vector2I(this.x+other, this.y+other);
  }

  public Vector2I scale(Vector2I other) {
    return new Vector2I(this.x*other.x, this.y*other.y);
  }

  public Vector2I scale(int x, int y) {
    return new Vector2I(this.x*x, this.y*y);
  }

  public Vector2I scale(int other) {
    return new Vector2I(this.x*other, this.y*other);
  }

  public Vector2I copy() {
    return new Vector2I(this);
  }

  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}
